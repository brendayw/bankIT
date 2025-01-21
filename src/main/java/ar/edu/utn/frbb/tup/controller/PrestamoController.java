package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.controller.validator.PrestamoValidator;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.PrestamoDetalle;
import ar.edu.utn.frbb.tup.model.PrestamoRespuesta;
import ar.edu.utn.frbb.tup.model.PrestamoResume;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoMonedaNoSoportada;
import ar.edu.utn.frbb.tup.model.exception.prestamo.CreditScoreException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.PrestamoNoExisteException;
import ar.edu.utn.frbb.tup.service.PrestamoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
//    public PrestamoRespuesta obtenerPrestamosPorCliente(@PathVariable long dni) throws ClientNoExisteException, PrestamoNoExisteException {
//        return prestamoService.buscarPrestamosPorCliente(dni);
//    }
    @GetMapping("/cliente/{dni}")
    public PrestamoRespuesta obtenerPrestamosPorCliente(@PathVariable long dni) throws ClientNoExisteException, PrestamoNoExisteException {
        PrestamoRespuesta prestamoRespuesta = prestamoService.prestamosPorCliente(dni);
        if (prestamoRespuesta == null || prestamoRespuesta.getPrestamoResume() == null || prestamoRespuesta.getPrestamoResume().isEmpty()) {
            throw new PrestamoNoExisteException("El cliente no tiene préstamos.");
        }
        return prestamoRespuesta;
    }

    //actualizar
    @PutMapping("/{id}")
    public Prestamo actualizarPrestamo(@PathVariable long id, @RequestBody PrestamoDto prestamoActualizado) throws PrestamoNoExisteException, CampoIncorrecto {
        Prestamo prestamo = prestamoService.actualizarDatosPrestamo(id, prestamoActualizado);
        return prestamo;
    }

    @PutMapping("/pagar/{id}")
    public PrestamoRespuesta pagarCuota(@PathVariable long id, @RequestBody PrestamoDto prestamoDto) throws PrestamoNoExisteException {
        Prestamo prestamo = prestamoService.buscarPrestamoPorId(id);
        if (prestamo == null) {
            throw new PrestamoNoExisteException("Prestamo no encontrado.");
        }

        prestamoDto.setNumeroCliente(prestamo.getDniTitular());
        prestamoService.pagarCuota(prestamoDto);
        Prestamo prestamoActualizado = prestamoService.buscarPrestamoPorId(id);
        List<PrestamoResume> resumen = prestamoService.prestamoResumes(Collections.singletonList(prestamoActualizado));
        return new PrestamoRespuesta(prestamoActualizado.getDniTitular(), resumen);
    }

    @DeleteMapping("/{id}")
    public Prestamo cerrarPrestamo(@PathVariable long id) throws PrestamoNoExisteException, CampoIncorrecto {
        return prestamoService.cerrarPrestamo(id);
    }
}
