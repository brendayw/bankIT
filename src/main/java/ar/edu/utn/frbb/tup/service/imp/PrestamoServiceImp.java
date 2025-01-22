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
    @Autowired PrestamoDao prestamoDao;
    @Autowired ClienteService clienteService;
    @Autowired
    CuentaService cuentaService;
    @Autowired CreditScoreService creditScoreService;

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
        System.out.print("saldo restante al dar de alta: " + prestamo.getSaldoRestante());

        //prestamoDto.setMontoPrestamo(montoConInteres); //guarda monto del prestamo + interes en el dto
        planPagos(prestamo);
        prestamoDao.savePrestamo(prestamo);

        System.out.println("ID del prestamo: " + prestamo.getId());

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

    //GET - busca todos los prestamos
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

    //GET -> no devuelve el monto bien
    public PrestamoRespuesta prestamosPorCliente(long numeroCliente) throws ClientNoExisteException {
        PrestamoRespuesta prestamoRespuesta = new PrestamoRespuesta();

        if (clienteService.buscarClientePorDni(numeroCliente) != null) {
            List<Prestamo> prestamosDelCliente = prestamoDao.buscarPrestamoPorCliente(numeroCliente);
            if (prestamosDelCliente.isEmpty()) {
                throw new PrestamoNoExisteException("El cliente no tiene prestamos.");
            }
            prestamoRespuesta.setNumeroCliente(numeroCliente);
            prestamoRespuesta.setPrestamoResume(prestamoResumes(prestamosDelCliente));
        }
        return prestamoRespuesta;
    }

    //PUT - paga -> creo que funciona bien
    public void pagarCuota(PrestamoDto prestamoDto, long id) throws PrestamoNoExisteException {
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

        //calcularSaldoRestante(pagar); //calcula el saldo restante
        System.out.println("saldo restante: " + pagar.getSaldoRestante());
        pagar.setSaldoRestante(calcularSaldoRestante(pagar));
        System.out.println("saldo restante: " + pagar.getSaldoRestante());

        double montoCuota = pagar.getMonto() / pagar.getPlazoMeses();  // Asegúrate de que la cuota sea el monto dividido entre los meses
        System.out.println("Monto de la cuota a pagar: " + montoCuota);

        Cliente cliente = clienteService.buscarClientePorDni(pagar.getDniTitular());
        if (!cliente.tieneCuentaEnMoneda(TipoMoneda.fromString(prestamoDto.getTipoMoneda()))) {
            throw new CuentaNoExisteException("El cliente no tiene cuentas en esa moneda.");
        }

        if (pagar.getSaldoRestante() <= 0) {
            throw new IllegalStateException("El préstamo ya está completamente pagado.");
        }

        pagar.setSaldoRestante(pagar.getSaldoRestante() - montoCuota);

        System.out.println("saldo restante: " + pagar.getSaldoRestante());

        pagar.incrementarPagosRealizados();
        System.out.print(pagar.getPagosRealizados());
        planPagos.remove(0);
        pagar.setPlanDePagos(planPagos);

        prestamoDao.update(pagar);
        System.out.println("Pago realizado. Restante: " + pagar.getSaldoRestante());
        prestamoDao.buscarPrestamoPorCliente(pagar.getDniTitular());
        System.out.println(pagar);


    }

    //no devuelve monto con interes bien
    public List<PrestamoResume> prestamoResumes(List<Prestamo> prestamos) {
        List<PrestamoResume> listado = new ArrayList<>();

        for (Prestamo prestamo : prestamos) {
            PrestamoResume datos = new PrestamoResume();
            int pagosRealizados = prestamo.getPlazoMeses() - prestamo.getPlanDePagos().size();
            datos.setMonto(prestamo.getMonto());
            datos.setPlazoMeses(prestamo.getPlazoMeses());
            datos.setPagosRealizados(pagosRealizados);

            double saldoRestante = prestamo.getSaldoRestante();
            System.out.print("Saldo restante de list resumes: " + saldoRestante);
            datos.setSaldoRestante(saldoRestante);

            listado.add(datos);
            System.out.print("Impresion de resumes: " + listado);
        }
        return listado;
    }

    //PUT - actualizar datos -> no se
    public Prestamo actualizarDatosPrestamo(long id, PrestamoDto prestamoDto /*double monto, LoanStatus estado*/) throws PrestamoNoExisteException, CampoIncorrecto {
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


        prestamoDao.update(prestamo);
        return prestamo;
    }

    //put
//    public PrestamoRespuesta realizarPago(long id) throws PrestamoNoExisteException {
//        Prestamo prestamo = prestamoDao.findPrestamo(id);
//        if (prestamo == null) {
//            throw new PrestamoNoExisteException("El prestamo no existe");
//        }
//        double montoOriginal = prestamo.getMonto();
//        double tasaInteresAnual = 0.40;
//        int plazoMeses = prestamo.getPlazoMeses();
//        double cuotaMensual = calcularCuotaMensual(montoOriginal, tasaInteresAnual, plazoMeses);
//
//        if (prestamo.getSaldoRestante() <= 0) {
//            throw new IllegalStateException("El préstamo ya está saldado.");
//        }
//
//        prestamo.incrementarPagosRealizados();
//        int pagosRealizados = prestamo.getPagosRealizados();
//        double montoConInteres = montoOriginal + (montoOriginal * tasaInteresAnual);
//        double saldoRestante = calcularSaldoRestante(prestamo);
//
//        prestamo.setSaldoRestante(saldoRestante);
//        prestamo.setMontoConInteres(montoConInteres);
//        prestamo.setPagosRealizados(pagosRealizados);
//        prestamoDao.update(prestamo);
//
//        PrestamoResume prestamoResume = new PrestamoResume(
//                montoConInteres,
//                plazoMeses,
//                pagosRealizados,
//                saldoRestante
//        );
//        List<PrestamoResume> prestamosResumes = new ArrayList<>();
//        prestamosResumes.add(prestamoResume);
//
//        System.out.println("Después de pagar: " + prestamo);
//        return new PrestamoRespuesta(prestamo.getDniTitular(), prestamosResumes);
//    }

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
    public double calcularInteres(Prestamo prestamo) throws CampoIncorrecto{
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

    private List<PlanPago> mostrarPlanPago(Prestamo prestamo) {
        List<PlanPago> planPagos = prestamo.getPlanDePagos();
        List<PlanPago> cuota = new ArrayList<>();
        if (!planPagos.isEmpty()) {
            cuota.add(planPagos.get(0));
        }
        return cuota;
    }

    public double calcularSaldoRestante(Prestamo prestamo) {
        double montoTotal = prestamo.getMonto();
        double montoCuota = montoTotal / prestamo.getPlazoMeses();
        int pagosRealizados = prestamo.getPagosRealizados();

        double saldoRestante = montoTotal - montoCuota * pagosRealizados;
        if (saldoRestante <= 0) {
            saldoRestante = 0;
        }
        return saldoRestante;
    }
}