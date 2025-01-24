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
    PrestamoRespuesta pagarCuota(PrestamoDto prestamoDto, long id) throws PrestamoNoExisteException, ClientNoExisteException;
    PrestamoRespuesta prestamosPorCliente(long numeroCliente) throws ClientNoExisteException;
    Prestamo cerrarPrestamo(long id) throws PrestamoNoExisteException, CampoIncorrecto;
}
