package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.CreditScoreException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.PrestamoNoExisteException;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import ar.edu.utn.frbb.tup.service.imp.PrestamoServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PrestamoServiceTest {

    @Mock private PrestamoDao prestamoDao;
    @Mock private ClienteService clienteService;
    @Mock private CuentaService cuentaService;
    @Mock private CreditScoreService creditScoreService;
    @InjectMocks private PrestamoServiceImp prestamoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private PrestamoDto crearPrestamoDto(long numeroCliente, double montoPrestamo, String tipoMoneda, int plazo) {
        PrestamoDto prestamoDto = new PrestamoDto();
        prestamoDto.setNumeroCliente(numeroCliente);
        prestamoDto.setMontoPrestamo(montoPrestamo);
        prestamoDto.setTipoMoneda(tipoMoneda);
        prestamoDto.setPlazoMeses(plazo);
        return prestamoDto;
    }

    //crea prestamo con exito
    @Test
    void testCrearPrestamo_Success() throws ClientNoExisteException, CuentaNoExisteException, CreditScoreException {
        PrestamoDto prestamoDto = crearPrestamoDto(40860006L, 150000.0, "P", 12);
        Cliente mockCliente = mock(Cliente.class);

        when(mockCliente.tieneCuentaEnMoneda(TipoMoneda.PESOS)).thenReturn(true);
        when(clienteService.buscarClientePorDni(prestamoDto.getNumeroCliente())).thenReturn(mockCliente);
        doNothing().when(creditScoreService).validarScore(mockCliente);
        when(creditScoreService.calcularScore(any())).thenReturn(750);

        Prestamo mockPrestamo = new Prestamo(prestamoDto, 750);
        mockPrestamo.setLoanStatus(LoanStatus.APROBADO);

        PrestamoDetalle prestamoDetalle = prestamoService.darAltaPrestamo(prestamoDto);

        assertNotNull(prestamoDetalle);
        assertEquals(LoanStatus.APROBADO, prestamoDetalle.getEstado());
    }

    //crea préstamo rechazado por score insuficiente
    @Test
    void testCrearPrestamo_Failure() throws ClientNoExisteException, CuentaNoExisteException, CreditScoreException {
        PrestamoDto prestamoDto = crearPrestamoDto(40860006L, 150000.0, "P", 12);
        Cliente mockCliente = mock(Cliente.class);

        when(mockCliente.tieneCuentaEnMoneda(TipoMoneda.PESOS)).thenReturn(true);
        when(clienteService.buscarClientePorDni(prestamoDto.getNumeroCliente())).thenReturn(mockCliente);
        when(clienteService.buscarClientePorDni(prestamoDto.getNumeroCliente())).thenReturn(mockCliente);
        when(creditScoreService.calcularScore(any())).thenReturn(500);

        PrestamoDetalle prestamoDetalle = prestamoService.darAltaPrestamo(prestamoDto);

        assertNotNull(prestamoDetalle);
        assertEquals(LoanStatus.RECHAZADO, prestamoDetalle.getEstado());
    }

    //busca los prestamos por id del prestamo
    @Test
    void testBuscarCuentaPorId_Success() throws PrestamoNoExisteException {
        long id = 123456789;
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);

        when(prestamoDao.findPrestamo(id)).thenReturn(prestamo);

        Prestamo resultado = prestamoService.buscarPrestamoPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
    }

    @Test
    void testBuscarCuentaPorId_Failure() {
        long id = 123456789;
        when(prestamoDao.findPrestamo(id)).thenReturn(null);

        PrestamoNoExisteException e = assertThrows(PrestamoNoExisteException.class, () -> prestamoService.buscarPrestamoPorId(id));

        assertEquals("El préstamo con ID: " + id + " no existe.", e.getMessage());
    }

    //obtiene todos los prestamo solicitados por un cliente
    @Test
    void testPrestamosPorCliente() throws ClientNoExisteException, PrestamoNoExisteException {
        long dniCliente = 12345678L;
        Cliente cliente = new Cliente();
        cliente.setDni(dniCliente);

        Prestamo prestamo1 = new Prestamo();
        prestamo1.setId(1L);
        prestamo1.setDniTitular(dniCliente);
        prestamo1.setLoanStatus(LoanStatus.APROBADO);
        prestamo1.setMonto(100000.0);
        prestamo1.setPlazoMeses(12);
        prestamo1.setPagosRealizados(6);
        prestamo1.setSaldoRestante(50000.0);

        Prestamo prestamo2 = new Prestamo();
        prestamo2.setId(2L);
        prestamo2.setDniTitular(dniCliente);
        prestamo2.setLoanStatus(LoanStatus.APROBADO);
        prestamo2.setMonto(50000.0);
        prestamo2.setPlazoMeses(10);
        prestamo2.setPagosRealizados(4);
        prestamo2.setSaldoRestante(20000.0);

        List<Prestamo> prestamos = Arrays.asList(prestamo1, prestamo2);

        when(clienteService.buscarClientePorDni(dniCliente)).thenReturn(cliente);
        when(prestamoDao.buscarPrestamoPorCliente(dniCliente)).thenReturn(prestamos);

        PrestamoRespuesta respuesta = prestamoService.prestamosPorCliente(dniCliente);

        assertNotNull(respuesta);
        assertEquals(dniCliente, respuesta.getNumeroCliente());
        assertEquals(2, respuesta.getPrestamoResume().size());

        assertEquals(100000.0, respuesta.getPrestamoResume().get(0).getMonto(), "El monto del primer préstamo no coincide");
        assertEquals(50000.0, respuesta.getPrestamoResume().get(0).getSaldoRestante(), "El saldo restante del primer préstamo no coincide");
        assertEquals(50000.0, respuesta.getPrestamoResume().get(1).getMonto(), "El monto del segundo préstamo no coincide");
        assertEquals(20000.0, respuesta.getPrestamoResume().get(1).getSaldoRestante(), "El saldo restante del segundo préstamo no coincide");

        verify(clienteService, times(1)).buscarClientePorDni(dniCliente);
        verify(prestamoDao, times(2)).buscarPrestamoPorCliente(dniCliente);
    }

    @Test
    void testPrestamosPorCliente_Failure() throws ClientNoExisteException {
        long dniCliente = 12345678L;
        Cliente cliente = new Cliente();
        cliente.setDni(dniCliente);

        when(clienteService.buscarClientePorDni(dniCliente)).thenReturn(cliente);
        when(prestamoDao.buscarPrestamoPorCliente(dniCliente)).thenReturn(Collections.emptyList());

        PrestamoNoExisteException e = assertThrows(PrestamoNoExisteException.class, () -> prestamoService.prestamosPorCliente(dniCliente),
                "Se esperaba que se lanzara PrestamoNoExisteException cuando no hay préstamos solicitados");

        verify(clienteService, times(1)).buscarClientePorDni(dniCliente);
        verify(prestamoDao, times(1)).buscarPrestamoPorCliente(dniCliente);
    }

    // cerrar prestamo
    @Test
    void testCerrarPrestamo_Success() throws PrestamoNoExisteException {
        long id = 123456789;
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);
        prestamo.setLoanStatus(LoanStatus.APROBADO);

        when(prestamoDao.findPrestamo(id)).thenReturn(prestamo);

        Prestamo resultado = prestamoService.cerrarPrestamo(id);

        assertEquals(LoanStatus.CERRADO, resultado.getLoanStatus());
        verify(prestamoDao, times(1)).savePrestamo(prestamo);
    }
}
