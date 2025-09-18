//package ar.edu.utn.frbb.tup.service;
//
//import ar.edu.utn.frbb.tup.model.cliente.dto.ClientDto;
//import ar.edu.utn.frbb.tup.model.prestamo.dto.LoanRequestDto;
//import ar.edu.utn.frbb.tup.model.cliente.Client;
//import ar.edu.utn.frbb.tup.model.cuenta.Account;
//import ar.edu.utn.frbb.tup.model.prestamo.Loan;
//import ar.edu.utn.frbb.tup.model.cuenta.TipoCuenta;
//import ar.edu.utn.frbb.tup.model.cuenta.TipoMoneda;
//import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClientNoExisteException;
//import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClienteAlreadyExistsException;
//import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClienteMayorDeEdadException;
//import ar.edu.utn.frbb.tup.model.cuenta.exceptions.TipoCuentaYaExisteException;
//import ar.edu.utn.frbb.tup.persistence.ClienteDao;
//import ar.edu.utn.frbb.tup.service.imp.ClienteServiceImp;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.time.LocalDate;
//import java.util.HashSet;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class ClienteServiceTest {
//
//    @Mock private ClienteDao clienteDao;
//    @InjectMocks private ClienteServiceImp clienteService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    //metodo para crear clientes con Dto
//    private ClientDto crearClienteDto(String nombre, String apellido, Long dni, String fechaNacimiento, String tipoPersona, String banco) {
//        ClientDto cliente = new ClientDto();
//        cliente.setNombre(nombre);
//        cliente.setApellido(apellido);
//        cliente.setDni(dni);
//        cliente.setFechaNacimiento(fechaNacimiento);
//        cliente.setTipoPersona(tipoPersona);
//        cliente.setBanco(banco);
//        return cliente;
//    }
//
//    //metodo para crear clientes con model
//    private Client crearCliente(String nombre, String apellido, Long dni, LocalDate fechaNacimiento) {
//        Client cliente = new Client();
//        cliente.setNombre(nombre);
//        cliente.setApellido(apellido);
//        cliente.setDni(dni);
//        cliente.setFechaNacimiento(fechaNacimiento);
//        cliente.setCuentas(new HashSet<>());
//        return cliente;
//    }
//
//    //crea cliente
//    private Account crearCuenta(long dni, TipoMoneda tipoMoneda, TipoCuenta tipoCuenta, double balance) {
//        Account cuenta = new Account();
//        cuenta.setDniTitular(dni);
//        cuenta.setTipoMoneda(tipoMoneda);
//        cuenta.setTipoCuenta(tipoCuenta);
//        cuenta.setBalance(balance);
//        return cuenta;
//    }
//
//    //crea prestamo
//    private LoanRequestDto crearPrestamoDto(long numeroCliente, double montoPrestamo, String tipoMoneda, int plazo) {
//        LoanRequestDto prestamoDto = new LoanRequestDto();
//        prestamoDto.setNumeroCliente(numeroCliente);
//        prestamoDto.setMontoPrestamo(montoPrestamo);
//        prestamoDto.setTipoMoneda(tipoMoneda);
//        prestamoDto.setPlazoMeses(plazo);
//        return prestamoDto;
//    }
//
//    //crear cliente nuevo
//    @Test
//    void testCrearCliente_Success() throws ClienteAlreadyExistsException, ClienteMayorDeEdadException {
//        ClientDto clienteDto = crearClienteDto( "Brenda", "Yañez",
//                40860006L, "1997-04-09", "F", "Nacion");
//        Client cliente = new Client(clienteDto);
//
//        when(clienteDao.find(cliente.getDni(), false)).thenReturn(null);
//        doNothing().when(clienteDao).save(any(Client.class));
//
//        Client result = clienteService.darDeAltaCliente(clienteDto);
//
//        assertNotNull(result);
//        assertEquals(40860006L, result.getDni());
//        verify(clienteDao, times(1)).save(any(Client.class));
//    }
//
//    //cliente menor a 18 años
//    @Test
//    public void testClienteMenorDeEdad() throws ClienteAlreadyExistsException{
//        ClientDto menorDeEdad = crearClienteDto("Juan", "Perez", 12345678L,
//                "2009-03-18", "F", "Provincia");
//        Client cliente = new Client(menorDeEdad);
//
//        when(clienteDao.find(cliente.getDni(), false)).thenReturn(null);
//
//        ClienteMayorDeEdadException e = assertThrows(ClienteMayorDeEdadException.class, () -> clienteService.darDeAltaCliente(menorDeEdad));
//        assertEquals("El cliente debe ser mayor a 18 años", e.getMessage());
//        verify(clienteDao, never()).save(any(Client.class));
//    }
//
//    //cliente ya existe
//    @Test
//    public void testClienteExistente() throws ClienteMayorDeEdadException, ClienteAlreadyExistsException {
//        ClientDto cliente = crearClienteDto("Brenda", "Yañez", 40860006L,
//                "1997-04-09", "F", "Nacion");
//        Client clienteExistente = new Client(cliente);
//
//        when(clienteDao.find(cliente.getDni(), false)).thenReturn(clienteExistente);
//
//        ClienteAlreadyExistsException e = assertThrows(ClienteAlreadyExistsException.class, () -> clienteService.darDeAltaCliente(cliente));
//        assertEquals("Ya existe un cliente con ese DNI.", e.getMessage());
//        verify(clienteDao, never()).save(any(Client.class));
//    }
//
//    //agrega cuenta correctamente
//    @Test
//    public void testAgregarCuentaACliente_Success() throws ClientNoExisteException, TipoCuentaYaExisteException {
//        Client cliente = crearCliente("Brenda", "Yañez", 40860006L, LocalDate.parse("1997-03-18"));
//        Account cuenta = crearCuenta(40860006, TipoMoneda.PESOS, TipoCuenta.CAJA_AHORRO, 100000.0);
//
//        when(clienteDao.find(40860006L, true)).thenReturn(cliente);
//        doNothing().when(clienteDao).save(cliente);
//
//        clienteService.agregarCuenta(cuenta, 40860006L);
//
//        assertNotNull(cliente.getCuentas());
//        assertTrue(cliente.getCuentas().contains(cuenta));
//
//        verify(clienteDao, times(1)).find(40860006L, true);
//        verify(clienteDao, times(1)).save(cliente);
//    }
//
//    //agrega prestamo corectamente
//    @Test
//    void testAgregarPrestamoACliente_Sucess() throws ClientNoExisteException {
//        Client cliente = crearCliente("Brenda", "Yañez", 40860006L, LocalDate.parse("1997-03-18"));
//        LoanRequestDto dto = crearPrestamoDto(40860006, 210000.0, "P", 12);
//        Loan prestamo = new Loan(dto, 750);
//        when(clienteDao.find(40860006, true)).thenReturn(cliente);
//        doNothing().when(clienteDao).save(cliente);
//
//        clienteService.agregarPrestamo(prestamo, cliente.getDni());
//
//        assertNotNull(cliente.getPrestamos());
//        assertTrue(cliente.getPrestamos().contains(prestamo));
//
//        verify(clienteDao, times(1)).find(40860006L, true);
//        verify(clienteDao, times(1)).save(cliente);
//    }
//
//    @Test
//    public void testBuscaClientePorDni_Success() throws ClientNoExisteException {
//        long dni = 40860006L;
//        Client cliente = new Client();
//        cliente.setDni(dni);
//
//        when(clienteDao.find(dni, true)).thenReturn(cliente);
//
//        Client resultado = clienteService.buscarClientePorDni(dni);
//
//        assertNotNull(resultado);
//        assertEquals(dni, resultado.getDni());
//    }
//
//
//    @Test
//    public void testBuscaClientePorDniNoExiste() throws ClientNoExisteException {
//        long dni = 12345678L;
//        when(clienteDao.find(dni, true)).thenReturn(null);
//        ClientNoExisteException exception = assertThrows(ClientNoExisteException.class, () -> clienteService.buscarClientePorDni(dni));
//        assertEquals("El cliente no existe", exception.getMessage());
//    }
//
//}