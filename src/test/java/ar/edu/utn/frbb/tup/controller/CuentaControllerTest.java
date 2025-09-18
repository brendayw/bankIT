//package ar.edu.utn.frbb.tup.controller;
//
//import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
//import ar.edu.utn.frbb.tup.service.CuentaService;
//import org.junit.jupiter.api.BeforeEach;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//public class CuentaControllerTest {
//    @Mock private CuentaService cuentaService;
//    @Mock private CuentaValidator cuentaValidator;
//    @InjectMocks private AccountController cuentaController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
////    private CuentaDto crearCuentaDto(long dni, double balance, String tipoCuenta, String tipoMoneda) {
////        CuentaDto cuenta = new CuentaDto();
////        cuenta.setDniTitular(dni);
////        cuenta.setBalance(balance);
////        cuenta.setTipoCuenta(tipoCuenta);
////        cuenta.setTipoMoneda(tipoMoneda);
////        return cuenta;
////    }
//
////    @Test
////    void testCrearCuenta_Success() throws CuentaNoExisteException, ClientNoExisteException, CampoIncorrecto, TipoMonedaNoSoportada, CuentaNoSoportadaException, TipoCuentaYaExisteException, CuentaYaExisteException {
////        CuentaDto cuentaNueva = crearCuentaDto(40860006L, 100000.0, "A", "P");
////
////        Cuenta cuenta = new Cuenta();
////        cuenta.setDniTitular(40860006L);
////
////        doNothing().when(cuentaValidator).validateCuenta(cuentaNueva);
////        when(cuentaService.darDeAltaCuenta(cuentaNueva)).thenReturn(cuenta);
////
////        Cuenta resultado = cuentaController.crearCuenta(cuentaNueva);
////
////        assertNotNull(resultado);
////        assertEquals(40860006L, resultado.getDniTitular());
////        verify(cuentaService, times(1)).darDeAltaCuenta(cuentaNueva);
////    }
//
//    //crea cuenta que ya existe
////    @Test
////    void testCrearCuenta_Failure() throws CuentaYaExisteException, TipoCuentaYaExisteException, TipoMonedaNoSoportada, CuentaNoSoportadaException, ClientNoExisteException {
////        CuentaDto cuentaNueva = crearCuentaDto(40860006L, 100000.0, "C", "P");
////
////        when(cuentaService.darDeAltaCuenta(cuentaNueva))
////                .thenThrow(new CuentaYaExisteException("El cliente ya tiene una cuenta de ese tipo y de esa moneda."));
////
////        CuentaYaExisteException e = assertThrows(CuentaYaExisteException.class, () -> cuentaController.crearCuenta(cuentaNueva));
////        assertEquals("El cliente ya tiene una cuenta de ese tipo y de esa moneda.", e.getMessage());
////        verify(cuentaService, times(1)).darDeAltaCuenta(cuentaNueva);
////        System.out.println("Excepción encontrada: " + e.getMessage());
////    }
//
//    //busca todas las cuentas
////    @Test
////    void testBuscarCuentas_Success() throws CuentaNoExisteException {
////        Cuenta cuenta1 = new Cuenta();
////        Cuenta cuenta2 = new Cuenta();
////
////        cuenta1.setNumeroCuenta(123456789);
////        cuenta2.setNumeroCuenta(234567891);
////        List<Cuenta> cuentas = List.of(cuenta1, cuenta2);
////
////        when(cuentaService.buscarCuentas()).thenReturn(cuentas);
////        List<Cuenta> resultado = cuentaController.buscarCuentas();
////
////        assertEquals(2, resultado.size());
////        assertTrue(resultado.contains(cuenta1));
////        assertTrue(resultado.contains(cuenta2));
////        verify(cuentaService, times(1)).buscarCuentas();
////    }
//
//    //busca cuenta por id de la cuenta
////    @Test
////    void testObtenerCuentaPorId_Success() throws CuentaNoExisteException {
////        long id = 41858762;
////        Cuenta cuenta = new Cuenta();
////        cuenta.setNumeroCuenta(41858762);
////
////        when(cuentaService.buscarCuentaPorId(id)).thenReturn(cuenta);
////
////        Cuenta resultado = cuentaController.obtenerCuentaPorId(id);
////
////        assertNotNull(resultado);
////        assertEquals(id, resultado.getNumeroCuenta());
////        verify(cuentaService, times(1)).buscarCuentaPorId(id);
////    }
//
// //   @Test
////    void testObtenerCuentaPorId_Failure() throws CuentaNoExisteException {
////        long id = 12568734;
////
////        when(cuentaService.buscarCuentaPorId(id))
////                .thenThrow(new CuentaNoExisteException("La cuenta no existe."));
////
////        CuentaNoExisteException e = assertThrows(CuentaNoExisteException.class, () -> cuentaController.obtenerCuentaPorId(id));
////        assertEquals("La cuenta no existe.", e.getMessage());
////        verify(cuentaService, times(1)).buscarCuentaPorId(id);
////        System.out.println("Excepcion encontrada: " + e.getMessage());
////    }
//
//    //busca cuentas del cliente por su dni
////    @Test
////    void testBuscarCuentasPorDniCliente_Success() throws CuentaNoExisteException, ClientNoExisteException {
////        long dni = 40860006;
////        Cliente cliente = new Cliente();
////        cliente.setDni(dni);
////
////        Cuenta cuenta1 = new Cuenta();
////        Cuenta cuenta2 = new Cuenta();
////        cuenta1.setDniTitular(dni);
////        cuenta2.setDniTitular(dni);
////
////        List<Cuenta> cuentas = List.of(cuenta1, cuenta2);
////
////        when(cuentaService.buscarCuentaPorCliente(dni)).thenReturn(cuentas);
////        List<Cuenta> resultado = cuentaController.obtenerCuentasPorCliente(dni);
////
////        assertEquals(2, resultado.size());
////        assertTrue(resultado.contains(cuenta1));
////        assertTrue(resultado.contains(cuenta2));
////        verify(cuentaService, times(1)).buscarCuentaPorCliente(dni);
////    }
//
//    //desactiva la cuenta
////    @Test
////    void testDesactivarCuenta() throws CuentaNoExisteException{
////        long id = 123456789;
////        Cuenta cuenta = new Cuenta();
////        cuenta.setNumeroCuenta(id);
////
////        when(cuentaService.desactivarCuenta(id)).thenReturn(cuenta);
////        Cuenta resultado = cuentaController.desactivarCuenta(id);
////        assertNotNull(resultado);
////        assertEquals(id, resultado.getNumeroCuenta());
////        verify(cuentaService, times(1)).desactivarCuenta(id);
////
////    }
//
//}
