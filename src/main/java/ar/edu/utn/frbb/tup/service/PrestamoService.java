package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.CreditScoreException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.PrestamoNoExisteException;

import java.util.List;

public interface PrestamoService {
    Prestamo darAltaPrestamo(PrestamoDto prestamoDto) throws ClientNoExisteException, CuentaNoExisteException, CreditScoreException, PrestamoNoExisteException, CampoIncorrecto;
    List<Prestamo> buscarPrestamos();
    Prestamo buscarPrestamoPorId(long id) throws PrestamoNoExisteException;
    List<Prestamo> buscarPrestamosPorCliente(long dni) throws ClientNoExisteException, PrestamoNoExisteException;
    Prestamo actualizarDatosPrestamo(long id, double monto, LoanStatus estado) throws PrestamoNoExisteException, CampoIncorrecto;
    Prestamo cerrarPrestamo(long id) throws PrestamoNoExisteException, CampoIncorrecto;
    double calcularInteres(PrestamoDto prestamoDto) throws CampoIncorrecto;

}
