package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.controller.validator.PrestamoValidator;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.PrestamoDetalle;
import ar.edu.utn.frbb.tup.model.PrestamoRespuesta;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaNoExisteException;
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
    public PrestamoDetalle crearPrestamo(@RequestBody PrestamoDto prestamoDto) throws ClientNoExisteException, CuentaNoExisteException, TipoMonedaNoSoportada, CreditScoreException, PrestamoNoExisteException, CampoIncorrecto {
        prestamoValidator.validatePrestamo(prestamoDto);
        return prestamoService.darAltaPrestamo(prestamoDto);
    }

    //obtiene todos los prestamos
    @GetMapping
    public List<Prestamo> obtenerPrestamos() {
        return prestamoService.buscarPrestamos();
    }

    //busca prestamo por id de prestamo
    @GetMapping("/{id}")
    public Prestamo obtenerPrestamoPorId(@PathVariable long id) throws PrestamoNoExisteException {
        return prestamoService.buscarPrestamoPorId(id);
    }

    //busca prestamo por dni de cliente
//    @GetMapping("/cliente/{dni}")
//    public Prestamo obtenerPrestamosPorCliente(@PathVariable long dni) throws ClientNoExisteException, PrestamoNoExisteException {
//        return prestamoService.buscarPrestamosPorCliente(dni);
//    }

    //actualizar
    @PutMapping("/{id}")
    public Prestamo actualizarPrestamo(@PathVariable long id, @RequestBody Prestamo prestamoActualizado) throws PrestamoNoExisteException, CampoIncorrecto {
        Prestamo prestamo = prestamoService.actualizarDatosPrestamo(id, prestamoActualizado.getMonto(), prestamoActualizado.getLoanStatus());
        return prestamo;
    }

    @DeleteMapping("/{id}")
    public Prestamo cerrarPrestamo(@PathVariable long id) throws PrestamoNoExisteException, CampoIncorrecto {
        return prestamoService.cerrarPrestamo(id);
    }
}
