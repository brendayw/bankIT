//package ar.edu.utn.frbb.tup.service;
//
//import ar.edu.utn.frbb.tup.model.cuenta.exceptions.CuentaNoExisteException;
//import ar.edu.utn.frbb.tup.model.cuenta.exceptions.CuentaNoSoportadaException;
//import ar.edu.utn.frbb.tup.model.cuenta.exceptions.TipoCuentaYaExisteException;
//import ar.edu.utn.frbb.tup.model.cuenta.exceptions.TipoMonedaNoSoportada;
//import ar.edu.utn.frbb.tup.model.cuenta.dto.AccountDto;
//import ar.edu.utn.frbb.tup.model.cliente.Client;
//import ar.edu.utn.frbb.tup.model.cuenta.Account;
//import ar.edu.utn.frbb.tup.model.prestamo.Loan;
//import ar.edu.utn.frbb.tup.model.cuenta.TipoCuenta;
//import ar.edu.utn.frbb.tup.model.cuenta.TipoMoneda;
//import ar.edu.utn.frbb.tup.model.persona.TipoPersona;
//import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClientNoExisteException;
//import ar.edu.utn.frbb.tup.persistence.ClienteDao;
//import ar.edu.utn.frbb.tup.persistence.CuentaDao;
//import ar.edu.utn.frbb.tup.service.imp.ClienteServiceImp;
//import ar.edu.utn.frbb.tup.service.imp.CuentaServiceImp;
//
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
//import java.util.HashSet;
//import java.util.List;
//import java.util.Arrays;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class CuentaServiceTest {
//
//    @Mock private CuentaDao cuentaDao;
//    @Mock private ClienteDao clienteDao;
//    @Mock private ClienteServiceImp clienteService;
//    @InjectMocks private CuentaServiceImp cuentaService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    //metodo para crear cuenta con dto
//    private AccountDto crearCuentaDto(long dni, double balance, String tipoMoneda, String tipoCuenta) {
//        AccountDto cuenta = new AccountDto();
//        cuenta.setDniTitular(dni);
//        cuenta.setTipoMoneda(tipoMoneda);
//        cuenta.setTipoCuenta(tipoCuenta);
//        cuenta.setBalance(balance);
//        return cuenta;
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
//    //crea cuenta
//    @Test
//    public void testCrearCuentaNuevo_Success() throws ClientNoExisteException, TipoMonedaNoSoportada, TipoCuentaYaExisteException, CuentaNoSoportadaException {
//        Client clienteNuevo = crearCliente("Brenda", "Yañez", 40860006L, LocalDate.of(1997,4,9),
//                "2914785135", "brendayañez@gmail.com", TipoPersona.PERSONA_FISICA, "Nacion");
//        AccountDto cuentaNueva = crearCuentaDto(40860006, 200000.0, "P", "C");
//        Account nuevaCuenta = new Account(cuentaNueva);
//
//        when(clienteService.buscarClientePorDni(40860006L)).thenReturn(clienteNuevo);
//        doNothing().when(clienteService).agregarCuenta(any(), anyLong());
//
//        Account resultado = cuentaService.darDeAltaCuenta(cuentaNueva);
//
//        assertNotNull(resultado, "La cuenta creada no debe ser nula");
//        assertEquals(nuevaCuenta.getTipoCuenta(), resultado.getTipoCuenta());
//        assertEquals(nuevaCuenta.getTipoMoneda(), resultado.getTipoMoneda());
//        verify(cuentaDao, times(1)).save(any(Account.class));
//    }
//
//    //tipo cuenta ya existe
//    @Test
//    public void testCrearCuentaNuevo_Failure() {
//        AccountDto cuentaNueva = crearCuentaDto(40860006L, 200000.0, "P", "C");
//        Account cuentaExistente = new Account(cuentaNueva);
//
//        when(cuentaDao.buscarCuentasByCliente(40860006L)).thenReturn(Arrays.asList(cuentaExistente));
//
//        assertThrows(TipoCuentaYaExisteException.class, () -> cuentaService.darDeAltaCuenta(cuentaNueva));
//        verify(cuentaDao, times(0)).save(any(Account.class));
//    }
//
//    //busca por id
//    @Test
//    void testBuscarCuentaPorId_Success() throws CuentaNoExisteException {
//        Account cuentaEsperada = new Account();
//        cuentaEsperada.setDniTitular(40860006);
//        cuentaEsperada.setBalance(200000.0);
//        cuentaEsperada.setTipoMoneda(TipoMoneda.PESOS);
//        cuentaEsperada.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
//
//        when(cuentaDao.find(cuentaEsperada.getNumeroCuenta())).thenReturn(cuentaEsperada);
//
//        Account resultado = cuentaService.buscarCuentaPorId(cuentaEsperada.getNumeroCuenta());
//
//        assertNotNull(resultado, "La cuenta no debe ser nula.");
//        assertEquals(cuentaEsperada.getNumeroCuenta(), resultado.getNumeroCuenta(), "El ID de la cuenta no es el esperado.");
//        assertEquals(cuentaEsperada.getTipoCuenta(), resultado.getTipoCuenta());
//        assertEquals(cuentaEsperada.getTipoMoneda(), resultado.getTipoMoneda());
//        assertEquals(cuentaEsperada.getBalance(), resultado.getBalance());
//
//        verify(cuentaDao, times(1)).find(cuentaEsperada.getNumeroCuenta());
//    }
//
//    //busca cuenta por id que no existe
//    @Test
//    void testBuscarCuentaPorId_Failure() {
//        long id = 12345678;
//        when(cuentaDao.find(id)).thenReturn(null);
//
//        CuentaNoExisteException e = assertThrows(CuentaNoExisteException.class, () -> cuentaService.buscarCuentaPorId(id));
//
//        assertEquals("La cuenta con ID: " + id + " no existe.", e.getMessage());
//        verify(cuentaDao, times(1)).find(id);
//        System.out.println("Excepción capturada: " + e.getMessage());
//
//    }
//
//    //busca cuenta por dni cliente
//    @Test
//    void testBuscarCuentasPorDni_Success() throws ClientNoExisteException, CuentaNoExisteException {
//        Long dni = 40860006L;
//        Client cliente = crearCliente("Brenda", "Yañez", dni, LocalDate.parse("1997-03-18"),
//                "2914789635", "brendayañez@gmail.com", TipoPersona.PERSONA_FISICA, "Nacion");
//        Account cuenta1 = crearCuenta(dni, 1000.0, TipoMoneda.PESOS, TipoCuenta.CUENTA_CORRIENTE);
//        Account cuenta2 = crearCuenta(dni, 2000.0, TipoMoneda.DOLARES, TipoCuenta.CAJA_AHORRO);
//        cliente.getCuentas().add(cuenta1);
//        cliente.getCuentas().add(cuenta2);
//
//        when(clienteService.buscarClientePorDni(dni)).thenReturn(cliente);
//        List<Account> resultado = cuentaService.buscarCuentaPorCliente(dni);
//        assertNotNull(resultado);
//        assertEquals(2, resultado.size());
//        assertTrue(resultado.contains(cuenta1));
//        assertTrue(resultado.contains(cuenta2));
//        verify(clienteService, times(1)).buscarClientePorDni(dni);
//    }
//
//    //actualiza balance
//    @Test
//    void testActualizarBalance_Success() throws CuentaNoExisteException {
//        Long dniTitular = 40860006L;
//        TipoMoneda moneda = TipoMoneda.PESOS;
//        double montoSolicitado = 1500.0;
//        TipoCuenta tipoCuenta = TipoCuenta.CUENTA_CORRIENTE;
//
//        Account cuenta = crearCuenta(dniTitular, 2000.0, moneda, TipoCuenta.CUENTA_CORRIENTE);
//
//        Loan prestamo = new Loan();
//        prestamo.setDniTitular(dniTitular);
//        prestamo.setMontoSolicitado(montoSolicitado);
//        prestamo.setMoneda(moneda);
//
//        when(cuentaDao.findByClienteYTipoMonedaYTipoCuenta(dniTitular, moneda.toString(), tipoCuenta.toString())).thenReturn(cuenta);
//        cuentaService.actualizarBalance(prestamo);
//
//        assertEquals(3500.0, cuenta.getBalance(), 0.01); // 2000 + 1500 = 3500
//
//        verify(cuentaDao, times(1)).save(cuenta);
//    }
//
//    @Test
//    void testActualizarBalance_Failure() {
//        Long dniTitular = 40860006L;
//        TipoMoneda moneda = TipoMoneda.PESOS;
//        double montoSolicitado = 1500.0;
//        TipoCuenta cuenta = TipoCuenta.CUENTA_CORRIENTE;
//
//        Loan prestamo = new Loan();
//        prestamo.setDniTitular(dniTitular);
//        prestamo.setMontoSolicitado(montoSolicitado);
//        prestamo.setMoneda(moneda);
//
//        when(cuentaDao.findByClienteYTipoMonedaYTipoCuenta(dniTitular, moneda.toString(), cuenta.toString())).thenReturn(null);
//        assertThrows(CuentaNoExisteException.class, () -> cuentaService.actualizarBalance(prestamo));
//
//        verify(cuentaDao, times(0)).save(any(Account.class));
//    }
//
//    //desactiva la cuenta
//    @Test
//    void testDesactivarCuenta_Success() throws CuentaNoExisteException {
//        long id = 40860006L;
//        Account cuenta = new Account();
//        cuenta.setNumeroCuenta(id);
//        cuenta.setEstado(true);
//
//        when(cuentaDao.find(id)).thenReturn(cuenta);
//
//        Account cuentaDesactivada = cuentaService.desactivarCuenta(id);
//
//        assertFalse(cuentaDesactivada.isEstado(), "El estado de la cuenta debería ser false");
//        verify(cuentaDao, times(1)).update(cuenta);
//    }
//
//    @Test
//    void testDesactivarCuenta_Failure() {
//        long cuentaId = 14533778L;
//
//        when(cuentaDao.find(cuentaId)).thenReturn(null);
//        assertThrows(CuentaNoExisteException.class, () -> cuentaService.desactivarCuenta(cuentaId));
//        verify(cuentaDao, times(0)).update(any(Account.class));
//    }
//
//
//
//}
