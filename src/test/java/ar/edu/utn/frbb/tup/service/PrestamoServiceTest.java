//package ar.edu.utn.frbb.tup.service;
//
//import ar.edu.utn.frbb.tup.model.cliente.Client;
//import ar.edu.utn.frbb.tup.model.cuenta.Account;
//import ar.edu.utn.frbb.tup.model.prestamo.Loan;
//import ar.edu.utn.frbb.tup.model.prestamo.dto.LoanRequestDto;
//import ar.edu.utn.frbb.tup.model.prestamo.LoanStatus;
//import ar.edu.utn.frbb.tup.model.cuenta.TipoCuenta;
//import ar.edu.utn.frbb.tup.model.cuenta.TipoMoneda;
//import ar.edu.utn.frbb.tup.model.persona.TipoPersona;
//import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClientNoExisteException;
//import ar.edu.utn.frbb.tup.model.cuenta.exceptions.CuentaNoExisteException;
//import ar.edu.utn.frbb.tup.model.prestamo.exceptions.CreditScoreException;
//import ar.edu.utn.frbb.tup.model.prestamo.exceptions.PrestamoNoExisteException;
//import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
//import ar.edu.utn.frbb.tup.service.imp.PrestamoServiceImp;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class PrestamoServiceTest {
//
//    @Mock private PrestamoDao prestamoDao;
//    @Mock private ClienteService clienteService;
//    @Mock private CuentaService cuentaService;
//    @Mock private CreditScoreService creditScoreService;
//    @InjectMocks private PrestamoServiceImp prestamoService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    private LoanRequestDto crearPrestamoDto(long numeroCliente, double montoPrestamo, String tipoMoneda, int plazo) {
//        LoanRequestDto prestamoDto = new LoanRequestDto();
//        prestamoDto.setNumeroCliente(numeroCliente);
//        prestamoDto.setMontoPrestamo(montoPrestamo);
//        prestamoDto.setTipoMoneda(tipoMoneda);
//        prestamoDto.setPlazoMeses(plazo);
//        return prestamoDto;
//    }
//
//    //metodo para crear clientes con model
//    private Client crearCliente(String nombre, String apellido, Long dni, LocalDate fechaNacimiento, String telefono, String email, TipoPersona tipoPersona, String banco) {
//        Client cliente = new Client();
//        cliente.setNombre(nombre);
//        cliente.setApellido(apellido);
//        cliente.setDni(dni);
//        cliente.setFechaNacimiento(fechaNacimiento);
//        cliente.setTelefono(telefono);
//        cliente.setEmail(email);
//        cliente.setTipoPersona(tipoPersona);
//        cliente.setBanco(banco);
//        cliente.setCuentas(new HashSet<>());
//        return cliente;
//    }
//
//    //metodo para crear cuenta
//    private Account crearCuenta(long dniCliente, double balance, TipoMoneda tipoMoneda, TipoCuenta tipoCuenta) {
//        Account cuenta = new Account();
//        cuenta.setDniTitular(dniCliente);
//        cuenta.setBalance(balance);
//        cuenta.setTipoMoneda(tipoMoneda);
//        cuenta.setTipoCuenta(tipoCuenta);
//        return cuenta;
//    }
//
//    //crea prestamo con exito
//    @Test
//    void testCrearPrestamo_Success() throws ClientNoExisteException, CuentaNoExisteException, CreditScoreException {
//        Client clienteNuevo = crearCliente("Brenda", "Yañez", 40860006L, LocalDate.of(1997,4,9),
//                "2914785135", "brendayañez@gmail.com", TipoPersona.PERSONA_FISICA, "Nacion");
//        Account cuentaNueva = crearCuenta(clienteNuevo.getDni(), 100000.0, TipoMoneda.PESOS, TipoCuenta.CUENTA_CORRIENTE);
//
//        Set<Account> cuentas = new HashSet<>();
//        cuentas.add(cuentaNueva);
//        clienteNuevo.setCuentas(cuentas);
//
//        LoanRequestDto prestamoDto = crearPrestamoDto(clienteNuevo.getDni(), 150000.0, "P", 12);
//
//        when(clienteService.buscarClientePorDni(prestamoDto.getNumeroCliente())).thenReturn(clienteNuevo);
//        doNothing().when(creditScoreService).validarScore(clienteNuevo);
//        when(creditScoreService.calcularScore(any())).thenReturn(750);
//
//        PrestamoDetalle prestamoDetalle = prestamoService.darAltaPrestamo(prestamoDto);
//
//        assertNotNull(prestamoDetalle);
//        assertEquals(LoanStatus.APROBADO, prestamoDetalle.getEstado());
//        assertEquals("El préstamo fue aprobado.", prestamoDetalle.getMensaje());
//    }
//
//    //crea préstamo rechazado por score insuficiente
//    @Test
//    void testCrearPrestamo_Failure() throws ClientNoExisteException, CuentaNoExisteException, CreditScoreException {
//        Client clienteNuevo = crearCliente("Brenda", "Yañez", 40860006L, LocalDate.of(1997, 4, 9),
//                "2914785135", "brendayañez@gmail.com", TipoPersona.PERSONA_FISICA, "Nacion");
//        Account cuentaNueva = crearCuenta(clienteNuevo.getDni(), 100000.0, TipoMoneda.PESOS, TipoCuenta.CUENTA_CORRIENTE);
//
//        Set<Account> cuentas = new HashSet<>();
//        cuentas.add(cuentaNueva);
//        clienteNuevo.setCuentas(cuentas);
//
//        LoanRequestDto prestamoDto = crearPrestamoDto(40860006L, 150000.0, "P", 12);
//
//        when(clienteService.buscarClientePorDni(prestamoDto.getNumeroCliente())).thenReturn(clienteNuevo);
//        doNothing().when(creditScoreService).validarScore(clienteNuevo);
//        when(creditScoreService.calcularScore(any())).thenReturn(500);
//
//        PrestamoDetalle prestamoDetalle = prestamoService.darAltaPrestamo(prestamoDto);
//
//        assertNotNull(prestamoDetalle);
//        assertEquals(LoanStatus.RECHAZADO, prestamoDetalle.getEstado());
//        assertEquals("El préstamo ha sido rechazado debido a una calificación crediticia insuficiente", prestamoDetalle.getMensaje());
//    }
//
//    //busca los prestamos por id del prestamo
//    @Test
//    void testBuscarCuentaPorId_Success() throws PrestamoNoExisteException {
//        long id = 123456789;
//        Loan prestamo = new Loan();
//        prestamo.setId(id);
//
//        when(prestamoDao.findPrestamo(id)).thenReturn(prestamo);
//
//        Loan resultado = prestamoService.buscarPrestamoPorId(id);
//
//        assertNotNull(resultado);
//        assertEquals(id, resultado.getId());
//    }
//
//    @Test
//    void testBuscarCuentaPorId_Failure() {
//        long id = 123456789;
//        when(prestamoDao.findPrestamo(id)).thenReturn(null);
//
//        PrestamoNoExisteException e = assertThrows(PrestamoNoExisteException.class, () -> prestamoService.buscarPrestamoPorId(id));
//
//        assertEquals("El préstamo con ID: " + id + " no existe.", e.getMessage());
//    }
//
//    //obtiene todos los prestamo solicitados por un cliente
//    @Test
//    void testPrestamosPorCliente() throws ClientNoExisteException, PrestamoNoExisteException {
//        long dniCliente = 12345678L;
//        Client cliente = new Client();
//        cliente.setDni(dniCliente);
//
//        Loan prestamo1 = new Loan();
//        prestamo1.setId(123456789L);
//        prestamo1.setDniTitular(dniCliente);
//        prestamo1.setLoanStatus(LoanStatus.APROBADO);
//        prestamo1.setMonto(100000.0);
//        prestamo1.setPlazoMeses(12);
//        prestamo1.setPagosRealizados(6);
//        prestamo1.setSaldoRestante(50000.0);
//
//        Loan prestamo2 = new Loan();
//        prestamo2.setId(23456789L);
//        prestamo2.setDniTitular(dniCliente);
//        prestamo2.setLoanStatus(LoanStatus.APROBADO);
//        prestamo2.setMonto(50000.0);
//        prestamo2.setPlazoMeses(10);
//        prestamo2.setPagosRealizados(4);
//        prestamo2.setSaldoRestante(20000.0);
//
//        List<Loan> prestamos = Arrays.asList(prestamo1, prestamo2);
//
//        when(clienteService.buscarClientePorDni(dniCliente)).thenReturn(cliente);
//        when(prestamoDao.buscarPrestamoPorCliente(dniCliente)).thenReturn(prestamos);
//
//        PrestamoRespuesta respuesta = prestamoService.prestamosPorCliente(dniCliente);
//
//        assertNotNull(respuesta);
//        assertEquals(dniCliente, respuesta.getNumeroCliente());
//        assertEquals(2, respuesta.getPrestamoResume().size());
//
//        assertEquals(100000.0, respuesta.getPrestamoResume().get(0).getMonto(), "El monto del primer préstamo no coincide");
//        assertEquals(50000.0, respuesta.getPrestamoResume().get(0).getSaldoRestante(), "El saldo restante del primer préstamo no coincide");
//        assertEquals(50000.0, respuesta.getPrestamoResume().get(1).getMonto(), "El monto del segundo préstamo no coincide");
//        assertEquals(20000.0, respuesta.getPrestamoResume().get(1).getSaldoRestante(), "El saldo restante del segundo préstamo no coincide");
//
//        verify(clienteService, times(1)).buscarClientePorDni(dniCliente);
//        verify(prestamoDao, times(1)).buscarPrestamoPorCliente(dniCliente);
//    }
//
//    @Test
//    void testPrestamosPorCliente_Failure() throws ClientNoExisteException {
//        long dniCliente = 12345678L;
//        Client cliente = new Client();
//        cliente.setDni(dniCliente);
//
//        when(clienteService.buscarClientePorDni(dniCliente)).thenReturn(cliente);
//        when(prestamoDao.buscarPrestamoPorCliente(dniCliente)).thenReturn(Collections.emptyList());
//
//        PrestamoNoExisteException e = assertThrows(PrestamoNoExisteException.class, () -> prestamoService.prestamosPorCliente(dniCliente),
//                "Se esperaba que se lanzara PrestamoNoExisteException cuando no hay préstamos solicitados");
//
//        verify(clienteService, times(1)).buscarClientePorDni(dniCliente);
//        verify(prestamoDao, times(1)).buscarPrestamoPorCliente(dniCliente);
//    }
//
//    // cerrar prestamo
//    @Test
//    void testCerrarPrestamo_Success() throws PrestamoNoExisteException {
//        long id = 123456789;
//        Loan prestamo = new Loan();
//        prestamo.setId(id);
//        prestamo.setLoanStatus(LoanStatus.APROBADO);
//
//        when(prestamoDao.findPrestamo(id)).thenReturn(prestamo);
//
//        Loan resultado = prestamoService.cerrarPrestamo(id);
//
//        assertEquals(LoanStatus.CERRADO, resultado.getLoanStatus());
//        verify(prestamoDao, times(1)).savePrestamo(prestamo);
//    }
//}
