package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteAlreadyExistsException;
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

    //crea cliente
    @PostMapping
    public Cliente crearCliente(@RequestBody ClienteDto clienteDto) throws ClienteAlreadyExistsException {
        clienteValidator.validate(clienteDto);
        return clienteService.darDeAltaCliente(clienteDto);
    }

    //busca cliente
    @GetMapping("/{dni}")
    public Cliente obtenerClientePorId(@PathVariable long dni) throws ClientNoExisteException {
        return clienteService.buscarClientePorDni(dni);
    }

    //actualiza cliente
    @PutMapping("/{dni}")
    public Cliente actualizarCliente(@PathVariable long dni, @RequestBody Cliente clienteActualizado) throws ClientNoExisteException {
        Cliente update = clienteService.actualizarDatosDelCliente(dni, clienteActualizado.getTelefono(), clienteActualizado.getEmail(), clienteActualizado.isActivo());
        return update;
    }

    //desactiva cliente
    @DeleteMapping("/{dni}")
    public Cliente desactivarClient(@PathVariable long dni) throws ClientNoExisteException {
        return clienteService.desactivarCliente(dni);
    }

}
