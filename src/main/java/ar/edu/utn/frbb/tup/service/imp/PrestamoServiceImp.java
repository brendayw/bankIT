package ar.edu.utn.frbb.tup.service.imp;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.CreditScoreException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.PrestamoNoExisteException;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import ar.edu.utn.frbb.tup.service.ClienteService;
import ar.edu.utn.frbb.tup.service.CreditScoreService;
import ar.edu.utn.frbb.tup.service.CuentaService;
import ar.edu.utn.frbb.tup.service.PrestamoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PrestamoServiceImp implements PrestamoService {
    @Autowired
    PrestamoDao prestamoDao;
    @Autowired
    ClienteService clienteService;
    @Autowired
    CuentaService cuentaService;
    @Autowired
    CreditScoreService creditScoreService;

    public PrestamoServiceImp(PrestamoDao prestamoDao) {
        this.prestamoDao = prestamoDao;
    }

    //POST - solicitar prestamo -> OK
    public PrestamoDetalle darAltaPrestamo(PrestamoDto prestamoDto) throws ClientNoExisteException, CuentaNoExisteException, CreditScoreException, PrestamoNoExisteException, CampoIncorrecto {
        Cliente cliente = clienteService.buscarClientePorDni(prestamoDto.getNumeroCliente());
        if (cliente == null) {
            throw new ClientNoExisteException("EL cliente con DNI: " + prestamoDto.getNumeroCliente() + " no existe.");
        }
        if (!cliente.tieneCuentaEnMoneda(TipoMoneda.fromString(prestamoDto.getTipoMoneda()))) {
            throw new CuentaNoExisteException("El cliente " + prestamoDto.getNumeroCliente() + " no tiene cuneta en esa moenda.");
        }
        creditScoreService.validarScore(cliente);
        int score = creditScoreService.calcularScore(cliente.getPrestamos());

        Prestamo prestamo = new Prestamo(prestamoDto, score);
        double montoConInteres = calcularInteres(prestamo);
        prestamo.setMonto(montoConInteres);
        prestamo.setSaldoRestante(calcularSaldoRestante(prestamo));
        System.out.print("\nsaldo restante al dar de alta: " + prestamo.getSaldoRestante());

        //prestamoDto.setMontoPrestamo(montoConInteres); //guarda monto del prestamo + interes en el dto
        planPagos(prestamo);
        prestamoDao.savePrestamo(prestamo);

        System.out.println("\nID del prestamo: " + prestamo.getId());

        LoanStatus estado = prestamo.getLoanStatus();
        String mensaje = prestamo.devolverMensaje(estado);

        List<PlanPago> planPagos = prestamo.getPlanDePagos();
        if (estado == LoanStatus.APROBADO) {
            planPagos = prestamo.getPlanDePagos();
        } else if (estado == LoanStatus.RECHAZADO) {
            planPagos = null;
            System.out.println("El préstamo fue rechazado. No se generó plan de pagos.");
        }
        clienteService.agregarPrestamo(prestamo, prestamo.getDniTitular());
        return new PrestamoDetalle(estado, mensaje, planPagos);
    }

    //GET - busca todos los prestamos - ok
    public List<Prestamo> buscarPrestamos() {
        return prestamoDao.findAll();
    }

    //GET - busca prestamo por id de prestamo - OK
    public Prestamo buscarPrestamoPorId(long id) throws PrestamoNoExisteException {
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new PrestamoNoExisteException("Prestamo no encontrado");
        }
        return prestamo;
    }

    //PUT - paga cuotas -> OK
    @Override
    public PrestamoRespuesta pagarCuota(PrestamoDto prestamoDto, long id) throws PrestamoNoExisteException {
        List<Prestamo> prestamosDelCliente = prestamoDao.buscarPrestamoPorCliente(prestamoDto.getNumeroCliente());
        Prestamo pagar = null;
        for (Prestamo prestamo : prestamosDelCliente) {
            if (prestamo.getId() == id && prestamo.getLoanStatus() == LoanStatus.APROBADO) {
                pagar = prestamo;
                break;
            }
        }
        if (pagar == null) {
            throw new PrestamoNoExisteException("No se encontró ningún préstamo.");
        }
        List<PlanPago> planPagos = pagar.getPlanDePagos();
        if (planPagos == null || planPagos.isEmpty()) {
            throw new IllegalArgumentException("No hay cuotas para pagar.");
        }
        pagar.setSaldoRestante(calcularSaldoRestante(pagar));
        Cliente cliente = clienteService.buscarClientePorDni(pagar.getDniTitular());
        if (!cliente.tieneCuentaEnMoneda(TipoMoneda.fromString(prestamoDto.getTipoMoneda()))) {
            throw new CuentaNoExisteException("El cliente no tiene cuentas en esa moneda.");
        }
        if (pagar.getSaldoRestante() <= 0) {
            throw new IllegalStateException("El préstamo ya está completamente pagado.");
        }
        double montoCuota = pagar.getMonto() / pagar.getPlazoMeses();
        double nuevoSaldo = pagar.getSaldoRestante() - montoCuota;
        pagar.setSaldoRestante(nuevoSaldo);
        int nuevosPagosRealizados = pagar.getPlazoMeses() - pagar.getPlanDePagos().size() + 1;
        pagar.setPagosRealizados(nuevosPagosRealizados);
        calcularSaldoRestante(pagar);

        if (!pagar.getPlanDePagos().isEmpty()) {
            pagar.getPlanDePagos().remove(0); // Eliminar la cuota pagada
        }

        List<PrestamoResume> resumenPrestamos = new ArrayList<>();
        for (Prestamo prestamo : prestamosDelCliente) {
            PrestamoResume resumen = new PrestamoResume();
                    resumen.setMonto(prestamo.getMonto());
                    resumen.setPlazoMeses(prestamo.getPlazoMeses());
                    resumen.setPagosRealizados(prestamo.getPagosRealizados());
                    resumen.setSaldoRestante(prestamo.getSaldoRestante());
            resumenPrestamos.add(resumen);
        }
        prestamoDao.savePrestamo(pagar);
        prestamosDelCliente.add(pagar);
        return new PrestamoRespuesta(prestamoDto.getNumeroCliente(), resumenPrestamos);
    }

    //GET - obtiene todos los prestamos del cliente cuando se pasa su id
    public PrestamoRespuesta prestamosPorCliente(long numeroCliente) throws ClientNoExisteException {
        if (clienteService.buscarClientePorDni(numeroCliente) == null) {
            throw new ClientNoExisteException("El cliente con DNI: " + numeroCliente + " no existe.");
        }
        List<Prestamo> prestamosDelCliente = prestamoDao.buscarPrestamoPorCliente(numeroCliente);
        if (prestamosDelCliente == null || prestamosDelCliente.isEmpty()) {
            throw new PrestamoNoExisteException("El cliente no tiene préstamos registrados.");
        }
        List<PrestamoResume> resumenPrestamos = new ArrayList<>();
        for (Prestamo prestamo : prestamosDelCliente) {
            PrestamoResume resumen = new PrestamoResume();
            resumen.setMonto(prestamo.getMonto());
            resumen.setPlazoMeses(prestamo.getPlazoMeses());
            resumen.setPagosRealizados(prestamo.getPagosRealizados());
            resumen.setSaldoRestante(prestamo.getSaldoRestante());
            resumenPrestamos.add(resumen);
            System.out.println("Impresion de prestamosPorCliente resumen: " + resumenPrestamos);
        }
        return new PrestamoRespuesta(numeroCliente, resumenPrestamos);
    }

    //PUT - actualizar datos -> no se
    public Prestamo actualizarDatosPrestamo(long id, PrestamoDto prestamoDto) throws PrestamoNoExisteException, CampoIncorrecto {
        System.out.println("Iniciando la actualización del préstamo con ID: " + id);

        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new PrestamoNoExisteException("El prestamo con ID: " + id + " no existe.");
        }
        if (prestamo.getMonto() != 0) {
            throw new CampoIncorrecto("Esa accion no esta permtida");
        }

        prestamo.setMonto(prestamoDto.getMontoPrestamo());
        prestamo.setPlazoMeses(prestamoDto.getPlazoMeses());

        double montoConInteres = calcularInteres(prestamo);
        prestamo.setMonto(montoConInteres);

        prestamoDao.savePrestamo(prestamo);
        return prestamo;
    }

    //DELETE -
    public Prestamo cerrarPrestamo(long id) throws PrestamoNoExisteException, CampoIncorrecto {
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new PrestamoNoExisteException("El prestamo con ID: " + id + " no existe.");
        }
        prestamo.setLoanStatus(LoanStatus.CERRADO);
        actualizarDatosPrestamo(id, null /*, 0, 0*/);
        return prestamo;
    }

    //otros metodos
    //ok
    public double calcularInteres(Prestamo prestamo) throws CampoIncorrecto {
        int score = 600;
        //Prestamo prestamo = new Prestamo(prestamo, score);
        if (prestamo == null || prestamo.getMonto() <= 0 || prestamo.getPlazoMeses() <= 0) {
            throw new CampoIncorrecto("El monto y plazo del prestamo deben ser mayores a cero.");
        }
        double monto = prestamo.getMonto();
        int plazoMeses = prestamo.getPlazoMeses();
        double tasaInteres = prestamo.getTasaInteres();
        if (tasaInteres == 0.0) {
            throw new CampoIncorrecto("La tasa de interés no puede ser 0.0");
        }
        double tiempo = plazoMeses / 12.0;
        double interes = monto * tasaInteres * tiempo; //0.40 es la tasa de interes anual
        double montoConInteres = monto + interes;
        System.out.println("\nMonto: " + monto + ", " +
                "Plazo Meses: " + plazoMeses +
                ", Interés: " + interes +
                ", Monto con Interés: " + montoConInteres);
        return montoConInteres;
    }

    //ok
    public void planPagos(Prestamo prestamo) {
        double montoConInteres = prestamo.getMonto();
        int meses = prestamo.getPlazoMeses();
        double cuotaMensual = montoConInteres / meses;

        List<PlanPago> plan = new ArrayList<>();
        for (int i = 1; i <= prestamo.getPlazoMeses(); i++) {
            plan.add(new PlanPago(i, cuotaMensual));
        }
        prestamo.setPlanDePagos(plan);
    }


    //OK
    public double calcularSaldoRestante(Prestamo prestamo) {
        double montoCuota = prestamo.getMonto() / prestamo.getPlazoMeses(); // Calcula la cuota.
        int pagosRealizados = prestamo.getPagosRealizados(); // Obtén los pagos realizados actuales.
        double nuevoSaldo = prestamo.getMonto() - (montoCuota * pagosRealizados); // Calcula el saldo restante.
        if (nuevoSaldo < 0) {
            nuevoSaldo = 0; // Asegúrate de que el saldo no sea negativo.
        }
        prestamo.setSaldoRestante(nuevoSaldo);
        prestamoDao.savePrestamo(prestamo); // Guardamos el préstamo actualizado en la base de datos.
        System.out.print("Saldo restante de calcular saldo: " + nuevoSaldo);
        return nuevoSaldo;
    }

}