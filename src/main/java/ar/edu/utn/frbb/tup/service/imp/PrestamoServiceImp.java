package ar.edu.utn.frbb.tup.service.imp;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
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
    @Autowired CuentaService cuentaService;
    @Autowired CreditScoreService creditScoreService;

    public PrestamoServiceImp(PrestamoDao prestamoDao, ClienteService clienteService, CuentaService cuentaService, CreditScoreService creditScoreService) {
        this.prestamoDao = prestamoDao;
        this.clienteService = clienteService;
        this.cuentaService = cuentaService;
        this.creditScoreService = creditScoreService;
    }

    //POST - solicitar prestamo -> OK (refactorizado)
    @Override
    public PrestamoDetalle darAltaPrestamo(PrestamoDto prestamoDto) throws ClientNoExisteException, CuentaNoExisteException, CreditScoreException {
        Cliente cliente = obtenerClientePorDni(prestamoDto.getNumeroCliente());
        validarCuentaCliente(cliente.getDni(), prestamoDto.getTipoMoneda());
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
    public List<Prestamo> buscarPrestamos() throws PrestamoNoExisteException {
        List<Prestamo> prestamos = prestamoDao.findAll();
        if (prestamos.isEmpty()) {
            throw new PrestamoNoExisteException("No se encontraron préstamos.");
        }
        return prestamos;
    }

    //GET - busca prestamo por id del prestamo -> OK (refactorizado)
    @Override
    public Prestamo buscarPrestamoPorId(long id) throws PrestamoNoExisteException {
        return obtenerPrestamoPorId(id);
    }

    //PUT - paga cuota del prestamo -> OK (refactorizado)
    @Override
    public PrestamoRespuesta pagarCuota(PrestamoDto prestamoDto, long id) throws PrestamoNoExisteException, CuentaNoExisteException, ClientNoExisteException {
        Prestamo prestamo = obtenerPrestamoAprobado(prestamoDto.getNumeroCliente(), id);
        validarCuentaCliente(prestamoDto.getNumeroCliente(), prestamoDto.getTipoMoneda());
        pagarCuotaPrestamo(prestamo);

        List<Prestamo> prestamosAprobados = prestamoDao.buscarPrestamoPorCliente(prestamoDto.getNumeroCliente()).stream()
                .filter(p -> p.getLoanStatus() == LoanStatus.APROBADO)
                .collect(Collectors.toList());

        return generarRespuestaPrestamos(prestamoDto.getNumeroCliente(), prestamosAprobados);
    }

    //GET - obtiene el prestamo por dni o numero de cliente -> OK (refactorizado)
    @Override
    public PrestamoRespuesta prestamosPorCliente(long numeroCliente) throws ClientNoExisteException, PrestamoNoExisteException {
        Cliente cliente = obtenerClientePorDni(numeroCliente);
        if (cliente == null) {
            throw new ClientNoExisteException("El cliente no existe.");
        }
        List<Prestamo> prestamos = prestamoDao.buscarPrestamoPorCliente(numeroCliente);
        List<Prestamo> prestamosAprobados = prestamos.stream()
                .filter(p -> p.getLoanStatus() == LoanStatus.APROBADO)
                .collect(Collectors.toList());
        if (prestamosAprobados.isEmpty()) {
            throw new PrestamoNoExisteException("El cliente no tiene préstamos aprobados.");
        }

        return generarRespuestaPrestamos(numeroCliente, prestamosAprobados);
    }

    //DELETE - cierra el prestamo (refactorizado)
    @Override
    public Prestamo cerrarPrestamo(long id) throws PrestamoNoExisteException {
        Prestamo prestamo = obtenerPrestamoPorId(id);
        prestamo.setLoanStatus(LoanStatus.CERRADO);
        prestamo.setPagosRealizados(prestamo.getPlazoMeses());
        prestamo.setSaldoRestante(0.0);
        prestamo.setPlanDePagos(null);
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

    public void validarCuentaCliente(long dni, String tipoMoneda) throws CuentaNoExisteException, ClientNoExisteException {
        Cliente cliente = clienteService.buscarClientePorDni(dni);

        if (cliente.getCuentas().isEmpty()) {
            throw new CuentaNoExisteException("El cliente no tiene cuentas registradas.");
        }

        boolean cuentaValida = cliente.getCuentas().stream()
                .anyMatch(cuenta -> cuenta.getTipoCuenta() == TipoCuenta.CUENTA_CORRIENTE
                && cuenta.getTipoMoneda() == TipoMoneda.fromString(tipoMoneda));

        if (!cuentaValida) {
            throw new CuentaNoExisteException("El cliente no tiene una cuenta corriente en la moneda especificada.");
        }
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
        double montoConInteres = calcularInteres(prestamo);
        prestamo.setMontoSolicitado(montoSolicitado);
        prestamo.setMonto(montoConInteres);
        prestamo.setSaldoRestante(calcularSaldoRestante(prestamo));
        return prestamo;
    }

    //ok - refactorizado
    private double calcularInteres(Prestamo prestamo) {
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
    public PrestamoRespuesta generarRespuestaPrestamos(long numeroCliente, List<Prestamo> prestamos) {
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