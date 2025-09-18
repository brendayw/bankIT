//package ar.edu.utn.frbb.tup.controller;
//
//import ar.edu.utn.frbb.tup.model.cliente.dto.ClientDto;
//import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
//import ar.edu.utn.frbb.tup.model.cliente.Client;
//import ar.edu.utn.frbb.tup.infra.exception.CampoIncorrecto;
//import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClientNoExisteException;
//import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClienteAlreadyExistsException;
//import ar.edu.utn.frbb.tup.model.cliente.exceptions.ClienteMayorDeEdadException;
//import ar.edu.utn.frbb.tup.model.cliente.exceptions.TipoPersonaNoSoportada;
//import ar.edu.utn.frbb.tup.service.ClienteService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ClienteControllerTest {
//    @Mock
//    private ClienteService clienteService;
//
//    @Mock
//    private ClienteValidator clienteValidator;
//
//    @InjectMocks
//    private ClientController clienteController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    //metodo para crear clientes con Dto
//    private ClientDto crearClienteDto(String nombre, String apellido, Long dni, String fechaNacimiento) {
//        ClientDto cliente = new ClientDto();
//        cliente.setNombre(nombre);
//        cliente.setApellido(apellido);
//        cliente.setDni(dni);
//        cliente.setFechaNacimiento(fechaNacimiento);
//        return cliente;
//    }
//
//    //crea un cliente
//    @Test
//    void testCrearCliente_Success() throws ClienteAlreadyExistsException, CampoIncorrecto, ClienteMayorDeEdadException, TipoPersonaNoSoportada {
//        ClientDto clienteNuevo = crearClienteDto("Brenda", "Yañez", 40860006L, "1997-03-18");
//
//        Client cliente = new Client();
//        cliente.setDni(40860006L);
//
//        doNothing().when(clienteValidator).validateCliente(clienteNuevo);
//        when(clienteService.darDeAltaCliente(clienteNuevo)).thenReturn(cliente);
//
//        Client result = clienteController.crearCliente(clienteNuevo);
//
//        assertNotNull(result);
//        assertEquals(40860006L, result.getDni());
//        verify(clienteService, times(1)).darDeAltaCliente(clienteNuevo);
//    }
//
//    //crea cliente que ya existe
//    @Test
//    void testCrearClienteExistente() throws ClientNoExisteException, ClienteMayorDeEdadException, ClienteAlreadyExistsException {
//        ClientDto clienteNuevo = crearClienteDto("Lucas", "Dominguez", 46582354L, "2003-03-18");
//
//        when(clienteService.darDeAltaCliente(clienteNuevo))
//                .thenThrow(new ClienteAlreadyExistsException("Ya existe un cliente con ese DNI."));
//
//        ClienteAlreadyExistsException e = assertThrows(ClienteAlreadyExistsException.class, () -> clienteController.crearCliente(clienteNuevo));
//        assertEquals("Ya existe un cliente con ese DNI.", e.getMessage());
//        verify(clienteService, times(1)).darDeAltaCliente(clienteNuevo);
//        System.out.println("Excepción encontrada: " + e.getMessage());
//
//    }
//
//    //busca el cliente por dni / numeroCliente (tomado como id)
//    @Test
//    void testObtenerClientePorId_Success() throws ClientNoExisteException {
//        long dni = 41858762L;
//        Client cliente = new Client();
//        cliente.setDni(41858762L);
//
//        when(clienteService.buscarClientePorDni(dni)).thenReturn(cliente);
//
//        Client resultado = clienteController.obtenerClientePorId(dni);
//
//        assertNotNull(resultado);
//        assertEquals(dni, resultado.getDni());
//        verify(clienteService, times(1)).buscarClientePorDni(dni);
//    }
//
//    //no encuentra el cliente por id
//    @Test
//    void testObtenerClientePorId_Failure() throws ClientNoExisteException {
//        long dni = 27537844L;
//        when(clienteService.buscarClientePorDni(dni))
//                .thenThrow(new ClientNoExisteException("El cliente no existe."));
//
//        ClientNoExisteException e = assertThrows(ClientNoExisteException.class, () -> clienteController.obtenerClientePorId(dni));
//        assertEquals("El cliente no existe.", e.getMessage());
//        verify(clienteService, times(1)).buscarClientePorDni(dni);
//        System.out.println("Excepcion encontrada: " + e.getMessage());
//    }
//
//    //busca todos los clientes
//    @Test
//    void testBuscarClientes_Succes() throws ClientNoExisteException {
//        Client cliente1 = new Client();
//        Client cliente2 = new Client();
//        cliente1.setDni(40860006L);
//        cliente2.setDni(14533778L);
//
//        List<Client> clientes = List.of(cliente1, cliente2);
//
//        when(clienteService.buscarClientes()).thenReturn(clientes);
//        List<Client> resultado = clienteController.buscarTodosLosClientes();
//
//        assertEquals(2, resultado.size());
//        assertTrue(resultado.contains(cliente1));
//        assertTrue(resultado.contains(cliente2));
//        verify(clienteService, times(1)).buscarClientes();
//    }
//
//    //desactiva al cliente
//    @Test
//    void testDesactivarCliente() throws ClientNoExisteException {
//        long dni = 24858762L;
//        Client cliente = new Client();
//        cliente.setDni(24858762L);
//
//        when(clienteService.desactivarCliente(dni)).thenReturn(cliente);
//
//        Client resultado = clienteController.desactivarClient(dni);
//
//        assertNotNull(resultado);
//        assertEquals(dni, resultado.getDni());
//        verify(clienteService, times(1)).desactivarCliente(dni);
//    }
//}
