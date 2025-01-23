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
@RequestMapping("/api/prestamo")
public class PrestamoController {
    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private PrestamoValidator prestamoValidator;

    //crea el prestamo
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
    @GetMapping("/id/{id}")
    public Prestamo obtenerPrestamoPorId(@PathVariable long id) throws PrestamoNoExisteException {
        return prestamoService.buscarPrestamoPorId(id);
    }

    //busca los prestamos del cliente
    @GetMapping("/{dni}")
    public PrestamoRespuesta obtenerPrestamosPorCliente(@PathVariable long dni) throws ClientNoExisteException, PrestamoNoExisteException {
        PrestamoRespuesta prestamoRespuesta = prestamoService.prestamosPorCliente(dni);
        if (prestamoRespuesta == null || prestamoRespuesta.getPrestamoResume() == null || prestamoRespuesta.getPrestamoResume().isEmpty()) {
            throw new PrestamoNoExisteException("El cliente no tiene préstamos.");
        }
        return prestamoRespuesta;
    }

    //paga cuotas del prestamo
    @PutMapping("/pagar/{id}")
    public PrestamoRespuesta pagarCuota(@PathVariable long id, @RequestBody PrestamoDto prestamoDto) {
        PrestamoRespuesta prestamoActualizado = prestamoService.pagarCuota(prestamoDto, id);
        if (prestamoActualizado == null) {
            throw new RuntimeException("Error al procesar el pago del préstamo.");
        }
        return prestamoActualizado;
    }

    //marca prestamo como cerrado
    @DeleteMapping("/{id}")
    public Prestamo cerrarPrestamo(@PathVariable long id) throws PrestamoNoExisteException, CampoIncorrecto {
        return prestamoService.cerrarPrestamo(id);
    }
}
