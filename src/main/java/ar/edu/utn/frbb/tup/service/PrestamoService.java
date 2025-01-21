package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.CreditScoreException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.PrestamoNoExisteException;

import java.util.List;

public interface PrestamoService {
    PrestamoDetalle darAltaPrestamo(PrestamoDto prestamoDto) throws ClientNoExisteException, CuentaNoExisteException, CreditScoreException, PrestamoNoExisteException, CampoIncorrecto;
    List<Prestamo> buscarPrestamos();
    Prestamo buscarPrestamoPorId(long id) throws PrestamoNoExisteException;
    PrestamoRespuesta prestamosPorCliente(long numeroCliente) throws ClientNoExisteException;
    List<PrestamoResume> prestamoResumes(List<Prestamo> prestamos);
    void pagarCuota(PrestamoDto prestamoDto) throws PrestamoNoExisteException;
    //PrestamoRespuesta buscarPrestamosPorCliente(long dni) throws ClientNoExisteException, PrestamoNoExisteException;
    Prestamo actualizarDatosPrestamo(long id, PrestamoDto prestamoDto/*, double monto, LoanStatus estado*/) throws PrestamoNoExisteException, CampoIncorrecto;
    Prestamo cerrarPrestamo(long id) throws PrestamoNoExisteException, CampoIncorrecto;
    double calcularInteres(Prestamo prestamo) throws CampoIncorrecto;
    //int calcularPagosRealizados(Prestamo prestamo);
    double calcularCuotaMensual(double montoPrestamo, double tasaInteresAnual, int plazoMeses);
    void planPagos(Prestamo prestamo);
    double calcularSaldoRestante(double montoTotal, double montoCuota, int pagosRealizados);
    //double calcularSaldoRestante(double montoConInteres, double cuotaMensual, int pagosRealizados);
    //PrestamoRespuesta realizarPago(long id) throws PrestamoNoExisteException;
    //double calcularSaldoRestante(Prestamo prestamo);
}
