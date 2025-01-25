package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteMayorDeEdadException;
import ar.edu.utn.frbb.tup.model.exception.cliente.TipoPersonaNoSoportada;
import ar.edu.utn.frbb.tup.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteValidator clienteValidator;


    //crea cliente
    @PostMapping
    public Cliente crearCliente(@RequestBody ClienteDto clienteDto) throws ClienteAlreadyExistsException, CampoIncorrecto, ClienteMayorDeEdadException, TipoPersonaNoSoportada {
        clienteValidator.validateCliente(clienteDto);
        return clienteService.darDeAltaCliente(clienteDto);
    }

    //obtiene todos los clientes
    @GetMapping()
    public List<Cliente> buscarTodosLosClientes() {
        return clienteService.buscarClientes();
    }

    //busca cliente
    @GetMapping("/{dni}")
    public Cliente obtenerClientePorId(@PathVariable long dni) throws ClientNoExisteException {
        return clienteService.buscarClientePorDni(dni);
    }

    //desactiva cliente
    @DeleteMapping("/{dni}")
    public Cliente desactivarClient(@PathVariable long dni) throws ClientNoExisteException {
        return clienteService.desactivarCliente(dni);
    }

}
