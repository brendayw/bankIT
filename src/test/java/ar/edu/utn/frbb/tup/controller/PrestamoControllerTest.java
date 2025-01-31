package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.controller.validator.PrestamoValidator;
import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoMonedaNoSoportada;
import ar.edu.utn.frbb.tup.model.exception.prestamo.CreditScoreException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.PrestamoNoExisteException;
import ar.edu.utn.frbb.tup.service.PrestamoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PrestamoControllerTest {
    @Mock private PrestamoService prestamoService;
    @Mock private PrestamoValidator prestamoValidator;
    @InjectMocks private PrestamoController prestamoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //metodo para crear prestamo
    private Prestamo crearPrestamo(long dni, double monto, int plazo, int pagos, double saldo) {
        Prestamo prestamo = new Prestamo();
        prestamo.setDniTitular(dni);
        prestamo.setMonto(monto);
        prestamo.setPlazoMeses(plazo);
        prestamo.setPagosRealizados(pagos);
        prestamo.setSaldoRestante(saldo);
        return prestamo;
    }
    private PrestamoDto crearPrestamoDto(long numeroCliente, double monto, String tipoMoneda, int plazo) {
        PrestamoDto prestamo = new PrestamoDto();
        prestamo.setNumeroCliente(numeroCliente);
        prestamo.setMontoPrestamo(monto);
        prestamo.setTipoMoneda(tipoMoneda);
        prestamo.setPlazoMeses(plazo);
        return prestamo;
    }

    //metodo para crear respuesta de un post
    private PrestamoDetalle respuestaPrestamoDetalle(LoanStatus estado, String mensaje, List<PlanPago> plan) {
        PrestamoDetalle detalle = new PrestamoDetalle();
        detalle.setEstado(estado);
        detalle.setMensaje(mensaje);
        detalle.setPlanPagos(plan);
        return detalle;
    }

    //aprueba el prestamo
    @Test
    void testCrearPrestamo_Success() throws ClientNoExisteException, TipoMonedaNoSoportada, CuentaNoExisteException, CreditScoreException, CampoIncorrecto, PrestamoNoExisteException {
        PrestamoDto prestamoNuevo = crearPrestamoDto(40860006, 1000, "D", 12);

        List<PlanPago> planPagos = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            planPagos.add(new PlanPago(i, 167.0));
        }

        PrestamoDetalle prestamoDetalle = respuestaPrestamoDetalle(
                LoanStatus.APROBADO,
                "EL prestamo fue aprobado.",
                planPagos);

        doNothing().when(prestamoValidator).validatePrestamo(prestamoNuevo);
        when(prestamoService.darAltaPrestamo(prestamoNuevo)).thenReturn(prestamoDetalle);

        PrestamoDetalle resultado = prestamoController.crearPrestamo(prestamoNuevo);

        assertNotNull(resultado);
        assertEquals(prestamoDetalle.getEstado(), resultado.getEstado());
        assertEquals(prestamoDetalle.getMensaje(), resultado.getMensaje());
        assertEquals(prestamoDetalle.getPlanPagos(), resultado.getPlanPagos());
        verify(prestamoValidator, times(1)).validatePrestamo(prestamoNuevo);
        verify(prestamoService, times(1)).darAltaPrestamo(prestamoNuevo);
    }

    //score credit insuficiente
    @Test
    void testCrearPrestamo_CreditScoreInsuficiente() throws CreditScoreException, ClientNoExisteException, TipoMonedaNoSoportada, CuentaNoExisteException, CampoIncorrecto, PrestamoNoExisteException {
        PrestamoDto prestamoNuevo = crearPrestamoDto(40860006, 1000, "D", 12);

        doThrow(new CreditScoreException("El préstamo ha sido rechazado debido a una calificación crediticia insuficiente"))
                .when(prestamoValidator).validatePrestamo(prestamoNuevo);

        CreditScoreException e = assertThrows(CreditScoreException.class, () -> {
            prestamoController.crearPrestamo(prestamoNuevo);
        });
        assertEquals("El préstamo ha sido rechazado debido a una calificación crediticia insuficiente", e.getMessage());

        verify(prestamoService, never()).darAltaPrestamo(any(PrestamoDto.class));
        verify(prestamoValidator, times(1)).validatePrestamo(prestamoNuevo);
    }

    //obtener prestamo por id
    @Test
    void testObtenerPrestamoPorId_Success() throws PrestamoNoExisteException {
        long id = 123456789;
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);

        when(prestamoService.buscarPrestamoPorId(id)).thenReturn(prestamo);
        Prestamo resultado = prestamoController.obtenerPrestamoPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(prestamoService, times(1)).buscarPrestamoPorId(id);
    }

    @Test
    void testObtenerPrestamoPorId_Failure() throws PrestamoNoExisteException {
        long id = 123456789;
        doThrow(new PrestamoNoExisteException("El préstamo con ID " + id + " no existe.")).when(prestamoService).buscarPrestamoPorId(id);

        PrestamoNoExisteException thrown = assertThrows(PrestamoNoExisteException.class, () -> {
            prestamoController.obtenerPrestamoPorId(id);
        });

        assertEquals("El préstamo con ID " + id + " no existe.", thrown.getMessage());
        verify(prestamoService, times(1)).buscarPrestamoPorId(id);
    }

    //obtener prestamo por dni del cliente
    @Test
    void testObtenerPrestamoPorCliente_Success() throws PrestamoNoExisteException, ClientNoExisteException {
        long dni = 40860006;
        Prestamo prestamo1 = crearPrestamo(dni, 150000.0, 12, 5, 100000.0);
        Prestamo prestamo2 = crearPrestamo(dni, 100000.0, 24, 10, 50000.0);

        PrestamoResume resume1 = new PrestamoResume(
                prestamo1.getMonto(),
                prestamo1.getPlazoMeses(),
                prestamo1.getPagosRealizados(),
                prestamo1.getSaldoRestante());

        PrestamoResume resume2 = new PrestamoResume(
                prestamo2.getMonto(),
                prestamo2.getPlazoMeses(),
                prestamo2.getPagosRealizados(),
                prestamo2.getSaldoRestante());

        List<PrestamoResume> prestamosResumen = List.of(resume1, resume2);

        PrestamoRespuesta prestamoRespuesta = new PrestamoRespuesta();
        prestamoRespuesta.setPrestamoResume(prestamosResumen);

        when(prestamoService.prestamosPorCliente(dni)).thenReturn(prestamoRespuesta);
        PrestamoRespuesta resultado = prestamoController.obtenerPrestamosPorCliente(dni);

        assertNotNull(resultado, "La respuesta no debe ser nula.");
        assertEquals(2, resultado.getPrestamoResume().size(), "Debe haber dos resúmenes de préstamos.");

        boolean encontrado1 = false;
        boolean encontrado2 = false;

        for (PrestamoResume prestamoResume : resultado.getPrestamoResume()) {
            if (prestamoResume.getMonto() == resume1.getMonto() && prestamoResume.getPlazoMeses() == resume1.getPlazoMeses()) {
                encontrado1 = true;
            }
            if (prestamoResume.getMonto() == resume2.getMonto() && prestamoResume.getPlazoMeses() == resume2.getPlazoMeses()) {
                encontrado2 = true;
            }
        }

        assertTrue(encontrado1, "El primer resumen no se encuentra en la lista.");
        assertTrue(encontrado2, "El segundo resumen no se encuentra en la lista.");
        verify(prestamoService, times(1)).prestamosPorCliente(dni);
    }

    @Test
    void testObtenerPrestamoPorCliente_Failure() throws PrestamoNoExisteException, ClientNoExisteException {
        long dni = 40860006;
        doThrow(new ClientNoExisteException("El cliente con DNI " + dni + " no existe.")).when(prestamoService).prestamosPorCliente(dni);

        ClientNoExisteException thrown = assertThrows(ClientNoExisteException.class, () -> {
            prestamoController.obtenerPrestamosPorCliente(dni);
        });

        assertEquals("El cliente con DNI " + dni + " no existe.", thrown.getMessage());
        verify(prestamoService, times(1)).prestamosPorCliente(dni);
    }

    //obtener todos los prestamos
    @Test
    void testBuscarPrestamos_Success() throws PrestamoNoExisteException {
        Prestamo prestamo1 = new Prestamo();
        Prestamo prestamo2 = new Prestamo();

        prestamo1.setDniTitular(40860006);
        prestamo2.setDniTitular(14533778);
        List<Prestamo> prestamos = List.of(prestamo1, prestamo2);

        when(prestamoService.buscarPrestamos()).thenReturn(prestamos);
        List<Prestamo> resultado = prestamoController.obtenerPrestamos();

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(prestamo1));
        assertTrue(resultado.contains(prestamo2));
        verify(prestamoService, times(1)).buscarPrestamos();
    }

    //pagar cuota del prestamo
    @Test
    void testPagarCuota_Success() throws ClientNoExisteException, CuentaNoExisteException, PrestamoNoExisteException {
        long idPrestamo = 12345L;
        PrestamoDto prestamoDto = new PrestamoDto();
        prestamoDto.setMontoPrestamo(5000.0);

        PrestamoRespuesta prestamoRespuestaEsperada = new PrestamoRespuesta();
        List<PrestamoResume> prestamosResumen = new ArrayList<>();

        PrestamoResume prestamoResume = new PrestamoResume(15000.0, 12, 6, 10000.0);
        prestamosResumen.add(prestamoResume);
        prestamoRespuestaEsperada.setPrestamoResume(prestamosResumen);

        when(prestamoService.pagarCuota(prestamoDto, idPrestamo)).thenReturn(prestamoRespuestaEsperada);

        PrestamoRespuesta resultado = prestamoController.pagarCuota(idPrestamo, prestamoDto);

        assertNotNull(resultado, "La respuesta no debe ser nula.");
        assertEquals(1, resultado.getPrestamoResume().size());
        assertEquals(prestamoResume.getMonto(), resultado.getPrestamoResume().get(0).getMonto());
        assertEquals(prestamoResume.getSaldoRestante(), resultado.getPrestamoResume().get(0).getSaldoRestante());

        verify(prestamoService, times(1)).pagarCuota(prestamoDto, idPrestamo);
    }

    //error al pagar la cuota porque no encuentra el prestamo
    @Test
    void testPagarCuota_Error() throws ClientNoExisteException, CuentaNoExisteException, PrestamoNoExisteException {
        long id = 12345L;
        PrestamoDto prestamoDto = new PrestamoDto();
        prestamoDto.setMontoPrestamo(5000.0);

        when(prestamoService.pagarCuota(prestamoDto, id))
                .thenThrow(new PrestamoNoExisteException("El prestamo no existe."));

        PrestamoNoExisteException e = assertThrows(PrestamoNoExisteException.class, () -> prestamoController.pagarCuota(id, prestamoDto));
        assertEquals("El prestamo no existe.", e.getMessage());
        verify(prestamoService, times(1)).pagarCuota(prestamoDto, id);
    }

    //cerrar prestamo
    @Test
    void testCerrarPrestamo() throws CampoIncorrecto, PrestamoNoExisteException {
        long id = 123456789;
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);
        when(prestamoService.cerrarPrestamo(id)).thenReturn(prestamo);
        Prestamo resultado = prestamoController.cerrarPrestamo(id);
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        verify(prestamoService, times(1)).cerrarPrestamo(id);
    }

}
