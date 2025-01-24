package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteMayorDeEdadException;

import ar.edu.utn.frbb.tup.persistence.ClienteDao;

import ar.edu.utn.frbb.tup.service.imp.ClienteServiceImp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClienteServiceTest {

    @Mock private ClienteDao clienteDao;
    @InjectMocks private ClienteValidator clienteValidator;

    @InjectMocks private ClienteServiceImp clienteService;

    @Mock private ClienteDto clienteDto;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //cliente nuevo
    @Test
    public void testCrearClienteNuevo_Success() throws ClienteAlreadyExistsException, ClienteMayorDeEdadException {
        ClienteDto clienteNuevo = new ClienteDto();
        clienteNuevo.setNombre("Brenda");
        clienteNuevo.setApellido("Yañez");
        clienteNuevo.setDni(40860006L);
        clienteNuevo.setFechaNacimiento("1997-03-18");
        clienteNuevo.setTelefono("2914789635");
        clienteNuevo.setEmail("brendayañez@gmail.com");
        clienteNuevo.setTipoPersona("F");
        clienteNuevo.setBanco("Nacion");
        try {
            clienteService.darDeAltaCliente(clienteNuevo);
            System.out.println("Cliente creado con exito.");
        } catch (ClienteAlreadyExistsException e) {
            assertEquals("Ya existe un cliente con ese DNI.", e.getMessage());
            System.out.println("Excepción capturada: " + e.getMessage());
        }
    }


    //cliente menor a 18 años
    @Test
    public void testClienteMenor18Años() throws ClienteAlreadyExistsException{
        ClienteDto clienteMenorDeEdad = new ClienteDto();
        clienteMenorDeEdad.setFechaNacimiento("2009-03-18");
        clienteMenorDeEdad.setTipoPersona("F");
        try {
            clienteService.darDeAltaCliente(clienteMenorDeEdad);
        } catch (ClienteMayorDeEdadException e) {
            assertEquals("El cliente debe ser mayor a 18 años", e.getMessage());
            System.out.println("Excepción capturada: " + e.getMessage());
        }
    }

    //cliente ya existe

    //agrega cuenta


    //Agregar una CA$ y CC$ --> success 2 cuentas, titular peperino

    //Agregar una CA$ y CAU$S --> success 2 cuentas, titular peperino...

    //Testear clienteService.buscarPorDni

}