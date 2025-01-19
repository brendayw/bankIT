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
    @Autowired PrestamoDao prestamoDao;
    @Autowired ClienteService clienteService;
    @Autowired CuentaService cuentaService;
    @Autowired CreditScoreService creditScoreService;

    public PrestamoServiceImp(PrestamoDao prestamoDao) {
        this.prestamoDao = prestamoDao;
    }

    //solicitar prestamo
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

        double montoConInteres = calcularInteres(prestamoDto);
        prestamo.setMontoConInteres(montoConInteres);
        prestamoDao.savePrestamo(prestamo);

        LoanStatus estado = prestamo.getLoanStatus();
        String mensaje = prestamo.devolverMensaje(estado);
        List<PlanPago> planPagos = prestamo.getPlanDePagos();
        if (estado == LoanStatus.APROBADO) {
            planPagos = prestamo.getPlanDePagos();
        } else if (estado == LoanStatus.RECHAZADO) {
            planPagos = null;
        }

        clienteService.agregarPrestamo(prestamo, prestamo.getDniTitular());
        PrestamoDetalle respuesta = new PrestamoDetalle(estado, mensaje, planPagos);
        return respuesta;
    }

    //busca todos los prestamos
    public List<Prestamo> buscarPrestamos() {
        return prestamoDao.findAll();
    }
    //busca prestamo por id de prestamo
    public Prestamo buscarPrestamoPorId(long id) throws PrestamoNoExisteException{
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new PrestamoNoExisteException("Prestamo no encontrado");
        }
        return prestamo;
    }

    //buscar prestamo por cliente
    public List<Prestamo> buscarPrestamosPorCliente(long dni) throws ClientNoExisteException, PrestamoNoExisteException {
        Cliente cliente = clienteService.buscarClientePorDni(dni);
        if (cliente == null) {
            throw new ClientNoExisteException("El cliente con DNI: " + dni + " no existe.");
        }
        Set<Prestamo> prestamos = cliente.getPrestamos();
        if (prestamos == null || prestamos.isEmpty()) {
            throw new PrestamoNoExisteException("El cliente con DNI: " + dni + " no tiene prestamos solicitados.");
        }
        return new ArrayList<>(prestamos);
    }

    //actualizar datos
    public Prestamo actualizarDatosPrestamo(long id, double monto, LoanStatus estado) throws PrestamoNoExisteException, CampoIncorrecto {
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new PrestamoNoExisteException("El prestamo con ID: " + id + " no existe.");
        }
        if (monto != 0) {
            throw new CampoIncorrecto("Esa accion no esta permtida");
        }
        if (estado != null) {
            prestamo.setLoanStatus(estado);
        }
        prestamoDao.update(prestamo);
        return prestamo;
    }

    //delete
    public Prestamo cerrarPrestamo(long id) throws PrestamoNoExisteException, CampoIncorrecto {
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new PrestamoNoExisteException("El prestamo con ID: " + id + " no existe.");
        }
        prestamo.setLoanStatus(LoanStatus.CERRADO);
        actualizarDatosPrestamo(id, 0, LoanStatus.CERRADO /*, 0, 0*/);
        return prestamo;
    }

    //otros metodos
    public double calcularInteres(PrestamoDto prestamoDto) throws CampoIncorrecto{
        int score = 600;
        Prestamo prestamo = new Prestamo(prestamoDto, score);
        if (prestamo == null || prestamo.getMonto() <= 0 || prestamo.getPlazoMeses() <= 0) {
            throw new CampoIncorrecto("El monto y plazo del prestamo deben ser mayores a cero.");
        }
        double monto = prestamo.getMonto();
        int plazoMeses = prestamo.getPlazoMeses();
        double tiempo = plazoMeses / 12.0;
        double interes = monto * 0.40 * tiempo; //0.40 es la tasa de interes anual
        double montoConInteres = monto + interes;
        return montoConInteres;
    }

    //actuliza balance si el prestamo es rechazado

}
