package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.controller.validator.PrestamoValidator;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoMonedaNoSoportada;
import ar.edu.utn.frbb.tup.model.exception.prestamo.CreditScoreException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.PrestamoNoExisteException;
import ar.edu.utn.frbb.tup.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prestamo")
public class PrestamoController {
    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private PrestamoValidator prestamoValidator;

    @PostMapping
    public Prestamo crearPrestamo(@RequestBody PrestamoDto prestamoDto) throws ClientNoExisteException , TipoMonedaNoSoportada, CreditScoreException, PrestamoNoExisteException {
        prestamoValidator.validate(prestamoDto);
        return prestamoService.darAltaPrestamo(prestamoDto);
    }

    //busca prestamo por id de prestamo
    @GetMapping("/{id}")
    public Prestamo obtenerPrestamoPorId(@PathVariable long id) {
        return prestamoService.buscarPrestamoPorId(id);
    }

    //busca prestamo por dni de cliente
    @GetMapping("/cliente/{dni}")
    public List<Prestamo> obtenerPrestamosPorCliente(@PathVariable long dni) throws ClientNoExisteException {
        return prestamoService.buscarPrestamosPorCliente(dni);
    }

    //actualizar
    @PutMapping("/{id}")
    public Prestamo actualizarPrestamo(@PathVariable long id, @RequestBody Prestamo prestamoActualizado) {
        Prestamo prestamo = prestamoService.actualizarDatosPrestamo(id, prestamoActualizado.getMonto(), prestamoActualizado.getLoanStatus());
        return prestamo;
    }

    @DeleteMapping("/{id}")
    public Prestamo cerrarPrestamo(@PathVariable long id) {
        return prestamoService.cerrarPrestamo(id);
    }



}
