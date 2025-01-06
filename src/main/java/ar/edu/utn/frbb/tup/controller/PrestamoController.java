package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.controller.validator.PrestamoValidator;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.service.ClienteService;
import ar.edu.utn.frbb.tup.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/prestamo")
public class PrestamoController {

    @Autowired private PrestamoService prestamoService;

    @Autowired private PrestamoValidator prestamoValidator;
    @Autowired private ClienteService clienteService;

    @PostMapping
    public Prestamo crearPrestamo(@RequestBody PrestamoDto prestamoDto) {
        prestamoValidator.validate(prestamoDto);
        Cliente cliente = clienteService.buscarClientePorDni(prestamoDto.getNumeroCliente());
        Prestamo prestamo = new Prestamo(prestamoDto, cliente);
        prestamo = prestamoService.solicitarPrestamo(prestamoDto);
        return prestamo;
    }

    @GetMapping("/{id}")
    public Prestamo getPrestamoById(@PathVariable long id) {
        Prestamo prestamo = prestamoService.buscarPrestamoPorId(id);
        if (prestamo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prestamo no encontrado");
        }
        return prestamo;
    }

    @GetMapping("/cliente/{numeroCliente}")
    public List<Prestamo> getPrestamosByCliente(@PathVariable long numeroCliente) {
        List<Prestamo> prestamos = prestamoService.buscarPrestamosPorCliente(numeroCliente);
        if (prestamos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron préstamos para el cliente.");
        }
        return prestamos;
    }


}
