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

    public PrestamoServiceImp(PrestamoDao prestamoDao, ClienteService clienteService, CuentaService cuentaService, CreditScoreService creditScoreService) {
        this.prestamoDao = prestamoDao;
        this.clienteService = clienteService;
        this.cuentaService = cuentaService;
        this.creditScoreService = creditScoreService;
    }

    //POST - solicitar prestamo -> OK (refactorizado)
    @Override
    public PrestamoDetalle darAltaPrestamo(PrestamoDto prestamoDto) throws ClientNoExisteException, CuentaNoExisteException, CreditScoreException, PrestamoNoExisteException, CampoIncorrecto {
        Cliente cliente = obtenerClientePorDni(prestamoDto.getNumeroCliente());
        validarCuentaCliente(cliente, prestamoDto.getTipoMoneda());
        creditScoreService.validarScore(cliente);

        int score = creditScoreService.calcularScore(cliente.getPrestamos());
        Prestamo prestamo = crearPrestamo(prestamoDto, score);

        if (prestamo.getLoanStatus() == LoanStatus.APROBADO) {
            planPagos(prestamo);
            System.out.println("\nID del prestamo: " + prestamo.getId());
            cuentaService.actualizarBalance(prestamo);
        }
        prestamoDao.savePrestamo(prestamo);
        clienteService.agregarPrestamo(prestamo, prestamo.getDniTitular());

        return new PrestamoDetalle(prestamo.getLoanStatus(), prestamo.getMensaje(), prestamo.getPlanDePagos());
    }

    //GET - busca todos los prestamos - ok
    @Override
    public List<Prestamo> buscarPrestamos() {
        return prestamoDao.findAll();
    }

    //GET - busca prestamo por id del prestamo -> OK (refactorizado)
    @Override
    public Prestamo buscarPrestamoPorId(long id) throws PrestamoNoExisteException {
        return obtenerPrestamoPorId(id);
    }

    //PUT - paga cuota del prestamo -> OK (refactorizado)
    @Override
    public PrestamoRespuesta pagarCuota(PrestamoDto prestamoDto, long id) throws PrestamoNoExisteException, CuentaNoExisteException {
        Prestamo prestamo = obtenerPrestamoAprobado(prestamoDto.getNumeroCliente(), id);
        validarCuentaCliente(prestamoDto.getNumeroCliente(), prestamoDto.getTipoMoneda());
        pagarCuotaPrestamo(prestamo);
        return generarRespuestaPrestamos(prestamoDto.getNumeroCliente());
    }

    //GET - obtiene el prestamo por dni o numero de cliente -> OK (refactorizado)
    @Override
    public PrestamoRespuesta prestamosPorCliente(long numeroCliente) throws ClientNoExisteException {
        Cliente cliente = obtenerClientePorDni(numeroCliente);
        List<Prestamo> prestamos = prestamoDao.buscarPrestamoPorCliente(numeroCliente);

        if (prestamos.isEmpty()) {
            throw new PrestamoNoExisteException("El cliente no tiene préstamos registrados.");
        }

        return generarRespuestaPrestamos(numeroCliente);
    }

    //DELETE - cierra el prestamo (refactorizado)
    @Override
    public Prestamo cerrarPrestamo(long id) throws PrestamoNoExisteException, CampoIncorrecto {
        Prestamo prestamo = obtenerPrestamoPorId(id);
        prestamo.setLoanStatus(LoanStatus.CERRADO);
        prestamoDao.savePrestamo(prestamo);
        return prestamo;
    }


    //otros metodos
    private Cliente obtenerClientePorDni(long dni) throws ClientNoExisteException {
        Cliente cliente = clienteService.buscarClientePorDni(dni);
        if (cliente == null) {
            throw new ClientNoExisteException("El cliente con DNI: " + dni + " no existe.");
        }
        return cliente;
    }

    private void validarCuentaCliente(Cliente cliente, String tipoMoneda) throws CuentaNoExisteException {
        if (!cliente.tieneCuentaEnMoneda(TipoMoneda.fromString(tipoMoneda))) {
            throw new CuentaNoExisteException("El cliente no tiene cuenta en la moneda especificada.");
        }
    }

    private void validarCuentaCliente(long dni, String tipoMoneda) throws CuentaNoExisteException {
        Cliente cliente = clienteService.buscarClientePorDni(dni);
        validarCuentaCliente(cliente, tipoMoneda);
    }

    private Prestamo obtenerPrestamoPorId(long id) throws PrestamoNoExisteException {
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new PrestamoNoExisteException("El préstamo con ID: " + id + " no existe.");
        }
        return prestamo;
    }

    private Prestamo obtenerPrestamoAprobado(long dni, long id) throws PrestamoNoExisteException {
        return prestamoDao.buscarPrestamoPorCliente(dni).stream()
                .filter(p -> p.getId() == id && p.getLoanStatus() == LoanStatus.APROBADO)
                .findFirst()
                .orElseThrow(() -> new PrestamoNoExisteException("No se encontró un préstamo aprobado con el ID: " + id));
    }

    private Prestamo crearPrestamo(PrestamoDto prestamoDto, int score) {
        Prestamo prestamo = new Prestamo(prestamoDto, score);
        double montoSolicitado = prestamoDto.getMontoPrestamo();
        System.out.println("monto prestamo dto" + prestamoDto.getMontoPrestamo());
        double montoConInteres = calcularInteres(prestamo);
        prestamo.setMontoSolicitado(montoSolicitado);
        prestamo.setMonto(montoConInteres);
        prestamo.setSaldoRestante(calcularSaldoRestante(prestamo));
        return prestamo;
    }

    //ok - refactorizado
    private double calcularInteres(Prestamo prestamo) throws CampoIncorrecto {
        double monto = prestamo.getMonto();
        double tiempo = prestamo.getPlazoMeses() / 12.0;
        double interes = monto * prestamo.getTasaInteres() * tiempo;
        return monto + interes;
    }

    //ok
    private void planPagos(Prestamo prestamo) {
        double cuotaMensual = prestamo.getMonto() / prestamo.getPlazoMeses();
        List<PlanPago> plan = new ArrayList<>();
        for (int i = 1; i <= prestamo.getPlazoMeses(); i++) {
            plan.add(new PlanPago(i, cuotaMensual));
        }
        prestamo.setPlanDePagos(plan);
    }

    //ok
    private void pagarCuotaPrestamo(Prestamo prestamo) {
        if (prestamo.getPlanDePagos().isEmpty()) {
            throw new IllegalArgumentException("No hay cuotas para pagar.");
        }
        PlanPago primerCuota = prestamo.getPlanDePagos().get(0);
        double montoCuota = primerCuota.getMontoCuota();
        prestamo.setSaldoRestante(prestamo.getSaldoRestante() - montoCuota);
        prestamo.getPlanDePagos().remove(0);
        prestamo.setPagosRealizados(prestamo.getPagosRealizados() + 1);
        prestamoDao.savePrestamo(prestamo);
    }

    //ok
    private PrestamoRespuesta generarRespuestaPrestamos(long numeroCliente) {
        List<Prestamo> prestamos = prestamoDao.buscarPrestamoPorCliente(numeroCliente);
        List<PrestamoResume> resumenPrestamos = new ArrayList<>();
        for (Prestamo prestamo : prestamos) {
            resumenPrestamos.add(new PrestamoResume(
                    prestamo.getMonto(),
                    prestamo.getPlazoMeses(),
                    prestamo.getPagosRealizados(),
                    prestamo.getSaldoRestante()
            ));
        }
        return new PrestamoRespuesta(numeroCliente, resumenPrestamos);
    }

    //OK
    private double calcularSaldoRestante(Prestamo prestamo) {
        double montoCuota = prestamo.getMonto() / prestamo.getPlazoMeses();
        double saldoRestante = prestamo.getMonto() - (montoCuota * prestamo.getPagosRealizados());
        return Math.max(saldoRestante, 0);
    }

}