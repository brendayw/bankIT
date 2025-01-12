package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PrestamoService {
    PrestamoDao prestamoDao;
    @Autowired ClienteService clienteService;
    @Autowired CreditScoreService creditScoreService;

    public PrestamoService(PrestamoDao prestamoDao) {
        this.prestamoDao = prestamoDao;
    }

    //para calcular el interes

    //solicitar prestamo
    public Prestamo darAltaPrestamo(PrestamoDto prestamoDto) throws ClientNoExisteException {
        Prestamo prestamo = new Prestamo(prestamoDto);
        long id = prestamo.getId();
        while (prestamoDao.findPrestamo(id) != null) {
            id = prestamo.getId();
        }
        prestamo.setId(id);
        if (prestamoDao.findPrestamo(prestamo.getId()) != null) {
           throw new IllegalArgumentException("Prestamo no encontrado");
        }
        Cliente cliente = clienteService.buscarClientePorDni(prestamo.getDniTitular());
        creditScoreService.validarScore(cliente);
        double montoConInteres = calcularInteres(prestamoDto);
        double cuotaMensual = calcularCuotaMensual(montoConInteres, prestamo.getPlazoMeses());
        prestamo.setmontoConInteres(montoConInteres);
        prestamo.setCuotaMensual(cuotaMensual);
        clienteService.agregarPrestamo(prestamo, prestamo.getDniTitular());
        prestamoDao.savePrestamo(prestamo);
        return prestamo;
    }

    public double calcularInteres(PrestamoDto prestamoDto) {
        Prestamo prestamo = new Prestamo(prestamoDto);
        if (prestamo == null || prestamo.getMonto() <= 0 || prestamo.getPlazoMeses() <= 0) {
            new IllegalArgumentException("El monto y plazo del prestamo deben ser mayores a cero.");
        }
        double monto = prestamo.getMonto();
        int plazoMeses = prestamo.getPlazoMeses();
        double tiempo = plazoMeses / 12.0;
        double interes = monto * 0.40 * tiempo; //0.40 es la tasa de interes anual
        double montoConInteres = monto + interes;
        return montoConInteres;
    }

    private double calcularCuotaMensual(double montoTotal, int plazoMeses) {
        return montoTotal / plazoMeses;
    }

    //busca prestamo por id de prestamo
    public Prestamo buscarPrestamoPorId(long id) {
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new IllegalArgumentException("Prestamo no encontrado");
        }
        return prestamo;
    }

    //buscar prestamo por cliente
    public List<Prestamo> buscarPrestamosPorCliente(long dni) throws ClientNoExisteException {
        Cliente cliente = clienteService.buscarClientePorDni(dni);
        if (cliente == null) {
            throw new ClientNoExisteException("El cliente con DNI: " + dni + " no existe.");
        }
        Set<Prestamo> prestamos = cliente.getPrestamos();
        if (prestamos == null || prestamos.isEmpty()) {
            throw new IllegalArgumentException("El cliente con DNI: " + dni + " no tiene prestamos solicitados.");
        }
        return new ArrayList<>(prestamos);
    }

    //actualizar datos
    public Prestamo actualizarDatosPrestamo(long id, double monto, LoanStatus estado, int nuevaCuotasPagada, int nuevaCuotasRestantes) {
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new IllegalArgumentException("El prestamo con ID: " + id + " no existe.");
        }
        if (monto != 0) {
            throw new IllegalArgumentException("Esa accion no esta permtida");
        }
        if (nuevaCuotasPagada != 0) {
            prestamo.setCuotasPagadas(nuevaCuotasPagada);
        }
        if (nuevaCuotasRestantes !=0) {
            prestamo.setCuotasRestantes(nuevaCuotasRestantes);
        }
        if (estado != null) {
            switch (estado) {
                case APROBADO:
                    prestamo.setLoanStatus(LoanStatus.APROBADO);
                    prestamo.setAprovacionFecha(LocalDate.now());
                    break;
                case RECHAZADO:
                    prestamo.setLoanStatus(LoanStatus.RECHAZADO);
                    break;
                case DESEMBOLSADO:
                    prestamo.setLoanStatus(LoanStatus.DESEMBOLSADO);
                    break;
                default:
                    prestamo.setLoanStatus(LoanStatus.PENDIENTE);
                    break;
            }
        }
        prestamoDao.update(prestamo);
        return prestamo;
    }

    //delete
    public Prestamo cerrarPrestamo(long id) {
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new IllegalArgumentException("El prestamo con ID: " + id + " no existe.");
        }
        prestamo.setLoanStatus(LoanStatus.CERRADO);
        actualizarDatosPrestamo(id, 0, LoanStatus.CERRADO, 0, 0);
        return prestamo;
    }

}
