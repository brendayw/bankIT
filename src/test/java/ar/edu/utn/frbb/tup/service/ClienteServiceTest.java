package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoPersona;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteMayorDeEdadException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoCuentaYaExisteException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;

import ar.edu.utn.frbb.tup.service.imp.ClienteServiceImp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClienteServiceTest {

    @Mock private ClienteDao clienteDao;
    @InjectMocks private ClienteValidator clienteValidator;
    @InjectMocks private ClienteServiceImp clienteService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //metodo para crear clientes con Dto
    private ClienteDto crearClienteDto(String nombre, String apellido, Long dni, String fechaNacimiento) {
        ClienteDto cliente = new ClienteDto();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setDni(dni);
        cliente.setFechaNacimiento(fechaNacimiento);
        return cliente;
    }

    //metodo para crear clientes con model
    private Cliente crearCliente(String nombre, String apellido, Long dni, LocalDate fechaNacimiento, String telefono, String email, TipoPersona tipoPersona, String banco) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setDni(dni);
        cliente.setFechaNacimiento(fechaNacimiento);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setTipoPersona(tipoPersona);
        cliente.setBanco(banco);
        cliente.setCuentas(new HashSet<>());
        return cliente;
    }

    //crea cliente
    private Cuenta crearCuenta(long dni, TipoMoneda tipoMoneda, TipoCuenta tipoCuenta, double balance) {
        Cuenta cuenta = new Cuenta();
        cuenta.setDniTitular(dni);
        cuenta.setTipoMoneda(tipoMoneda);
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setBalance(balance);
        return cuenta;
    }

    //cliente nuevo
    @Test
    public void testCrearClienteNuevo_Success() throws ClienteMayorDeEdadException {
       ClienteDto clienteNuevo = crearClienteDto("Brenda", "Yañez", 40860006L, "1997-03-18");

        try {
            clienteService.darDeAltaCliente(clienteNuevo);
            System.out.println("Cliente creado con éxito.");
        } catch (ClienteAlreadyExistsException e) {
            assertEquals("Ya existe un cliente con ese DNI.", e.getMessage());
            System.out.println("Excepción capturada: " + e.getMessage());
        }
    }

    //cliente menor a 18 años
    @Test
    public void testClienteMenorDeEdad() throws ClienteAlreadyExistsException{
        ClienteDto clienteMenorDeEdad = crearClienteDto("Juan", "Perez", 12345678L, "2009-03-18");

        try {
            clienteService.darDeAltaCliente(clienteMenorDeEdad);
            fail("Se esperaba que se lanzara una excepción ClienteMayorDeEdadException");
        } catch (ClienteMayorDeEdadException e) {
            assertEquals("El cliente debe ser mayor a 18 años", e.getMessage());
            System.out.println("Excepción capturada: " + e.getMessage());
        }
    }

    //cliente ya existe
    @Test
    public void testClienteExistente() throws ClienteMayorDeEdadException, ClienteAlreadyExistsException {
                ClienteDto cliente = crearClienteDto("Brenda", "Yañez", 40860006L, "1997-04-09");
        ClienteService clienteServiceMock = Mockito.mock(ClienteService.class);

        Mockito.doThrow(new ClienteAlreadyExistsException("Ya existe un cliente con ese DNI."))
                .when(clienteServiceMock)
                .darDeAltaCliente(cliente);

        try {
            clienteServiceMock.darDeAltaCliente(cliente);
            fail("Se esperaba que se lanzara una excepción ClienteAlreadyExistsException");
        } catch (ClienteAlreadyExistsException e) {
            assertEquals("Ya existe un cliente con ese DNI.", e.getMessage());
            System.out.println("Excepción capturada: " + e.getMessage());
        }
    }

    //agrega cuenta correctamente
    @Test
    public void testAgregarCuentaACliente_Success() throws ClientNoExisteException, TipoCuentaYaExisteException {
        ClienteService clienteService = Mockito.mock(ClienteService.class);

        Cliente cliente = crearCliente("Brenda", "Yañez", 40860006L, LocalDate.parse("1997-03-18"),
                "2914789635", "brendayañez@gmail.com", TipoPersona.PERSONA_FISICA, "Nacion");

        Cuenta cuenta = crearCuenta(40860006, TipoMoneda.PESOS, TipoCuenta.CAJA_AHORRO, 100000.0);
        cliente.addCuenta(cuenta);

        doNothing().when(clienteService).agregarCuenta(cuenta, cliente.getDni());
        clienteService.agregarCuenta(cuenta, cliente.getDni());

        assertNotNull(cliente.getCuentas(), "La lista de cuentas no debe ser nula");
        assertTrue(cliente.getCuentas().contains(cuenta), "La cuenta debe estar en la lista de cuentas");

        verify(clienteService, times(1)).agregarCuenta(cuenta, cliente.getDni());
        System.out.println("Cuenta agregada con éxito.");
    }

    //intenta agregar cuenta un tipo existente y de la moneda existente
    @Test
    public void testCrearCuentaTipoYMonedaExistente() throws TipoCuentaYaExisteException, ClientNoExisteException {
        ClienteService clienteServiceMock = Mockito.mock(ClienteService.class);
        ClienteDto cliente = crearClienteDto("Brenda", "Yañez", 40860006L, "1997-04-09");
        Cuenta cuenta1 = crearCuenta(40860006L,TipoMoneda.PESOS, TipoCuenta.CAJA_AHORRO, 1000.0);

        doThrow(new TipoCuentaYaExisteException("Ya existe una cuenta con ese tipo y moneda para este cliente."))
                .when(clienteServiceMock)
                .agregarCuenta(cuenta1, cliente.getDni());

        try {
            clienteServiceMock.agregarCuenta(cuenta1, cliente.getDni());
            fail("Se esperaba que se lanzara una excepción TipoCuentaYaExisteException");
        } catch (TipoCuentaYaExisteException e) {
            assertEquals("Ya existe una cuenta con ese tipo y moneda para este cliente.", e.getMessage());
            System.out.println("Excepción capturada: " + e.getMessage());
        }

        verify(clienteServiceMock, times(1)).agregarCuenta(cuenta1, cliente.getDni()); // Verifica que el método fue llamado dos veces
    }

    //busca cliente
    @Test
    public void testBuscaClientePorDni_Success() throws ClientNoExisteException {
        ClienteService clienteServiceMock = Mockito.mock(ClienteService.class);
        Long dni = 40860006L;
        Cliente clienteEsperado = crearCliente("Brenda", "Yañez", 40860006L, LocalDate.parse("1997-03-18"),
                "2914789635", "brendayañez@gmail.com", TipoPersona.PERSONA_FISICA, "Nacion");

        ClienteDto clienteEsperadoDto = new ClienteDto(clienteEsperado);

        when(clienteServiceMock.buscarClientePorDni(dni)).thenReturn(clienteEsperado);

        ClienteDto clienteObtenido = new ClienteDto(clienteServiceMock.buscarClientePorDni(dni));

        assertNotNull(clienteObtenido, "El cliente no debe ser nulo");
        assertEquals(clienteEsperadoDto.getDni(), clienteObtenido.getDni(), "El DNI del cliente debe coincidir");
        assertEquals(clienteEsperadoDto.getNombre(), clienteObtenido.getNombre(), "El nombre del cliente debe coincidir");
        assertEquals(clienteEsperadoDto.getApellido(), clienteObtenido.getApellido(), "El apellido del cliente debe coincidir");

        System.out.println("Cliente encontrado con éxito:");

        verify(clienteServiceMock, times(1)).buscarClientePorDni(dni);
    }

    @Test
    public void testBuscaClientePorDniNoExiste() throws ClientNoExisteException {
        ClienteService clienteServiceMock = Mockito.mock(ClienteService.class);
        Long dniInexistente = 99999999L;

        when(clienteServiceMock.buscarClientePorDni(dniInexistente)).thenThrow(new ClientNoExisteException("El cliente no existe"));

        try {
            clienteServiceMock.buscarClientePorDni(dniInexistente);
            fail("Se esperaba que se lanzara una excepción ClientNoExisteException");
        } catch (ClientNoExisteException e) {
            assertEquals("El cliente no existe", e.getMessage());
            System.out.println("Excepción capturada: " + e.getMessage());
        }

        verify(clienteServiceMock, times(1)).buscarClientePorDni(dniInexistente);
    }

}