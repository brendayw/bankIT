package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.exception.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteValidator clienteValidator;

    //crea cuenta
    @PostMapping
    public Cliente crearCliente(@RequestBody ClienteDto clienteDto) throws ClienteAlreadyExistsException {
        clienteValidator.validate(clienteDto);
        return clienteService.darDeAltaCliente(clienteDto);
    }

    //busca cliente
    @GetMapping("/{id}")
    public Cliente obtenerClientePorId(@PathVariable long id) throws ClientNoExisteException {
        return clienteService.buscarClientePorDni(id);
    }

    //actualiza cliente
    @PutMapping("/{id}")
    public Cliente actualizarCliente(@PathVariable long id, @RequestBody Cliente clienteActualizado) throws ClientNoExisteException {
        Cliente update = clienteService.actualizarDatosDelCliente(id, clienteActualizado.getTelefono(), clienteActualizado.getEmail(), clienteActualizado.isActivo());
        return update;
    }

    //desactiva cliente
    @DeleteMapping("/{id}")
    public Cliente desactivarClient(@PathVariable long id) throws ClientNoExisteException {
        return clienteService.desactivarCliente(id);
    }

}
