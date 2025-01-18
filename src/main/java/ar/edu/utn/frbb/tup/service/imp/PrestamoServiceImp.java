package ar.edu.utn.frbb.tup.service.imp;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.CreditScoreException;
import ar.edu.utn.frbb.tup.model.exception.prestamo.PrestamoNoExisteException;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import ar.edu.utn.frbb.tup.service.ClienteService;
import ar.edu.utn.frbb.tup.service.CreditScoreService;
import ar.edu.utn.frbb.tup.service.CuentaService;
import ar.edu.utn.frbb.tup.service.PrestamoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PrestamoServiceImp implements PrestamoService {
    PrestamoDao prestamoDao;
    @Autowired
    ClienteService clienteService;
    @Autowired
    CuentaService cuentaService;
    @Autowired
    CreditScoreService creditScoreService;

    public PrestamoServiceImp(PrestamoDao prestamoDao) {
        this.prestamoDao = prestamoDao;
    }

    //solicitar prestamo
    public Prestamo darAltaPrestamo(PrestamoDto prestamoDto) throws ClientNoExisteException, CuentaNoExisteException, CreditScoreException, PrestamoNoExisteException, CampoIncorrecto {
        System.out.println("Solicitando préstamo para el cliente con DNI: " + prestamoDto.getDniTitular());
        Cliente cliente = clienteService.buscarClientePorDni(prestamoDto.getDniTitular());
        if (cliente == null || !cliente.tieneCuentaEnMoneda(TipoMoneda.fromString(prestamoDto.getTipoMoneda()))) {
            throw new CuentaNoExisteException("El cliente no tiene una cuenta en la moneda solicitada.");
        }
        creditScoreService.validarScore(cliente);
        int score = creditScoreService.calcularScore(cliente.getPrestamos());
        System.out.println("Puntaje calculado para el préstamo: " + score);
        Prestamo prestamo = new Prestamo(prestamoDto, score);
        System.out.println("Préstamo creado con ID: " + prestamo.getId());
        long id = prestamo.getId();
        while (prestamoDao.findPrestamo(id) != null) {
            id = prestamo.getId();
        }
        prestamo.setId(id);

        if (prestamoDao.findPrestamo(prestamo.getId()) != null) {
            throw new PrestamoNoExisteException("Prestamo no encontrado");
        }
        double montoConInteres = calcularInteres(prestamoDto);
        prestamo.setMontoConInteres(montoConInteres);
        prestamoDao.savePrestamo(prestamo);

        if (prestamo.getLoanStatus() == LoanStatus.APROBADO) {
            cuentaService.actualizarBalance(prestamo);
        }
        clienteService.agregarPrestamo(prestamo, prestamo.getDniTitular());
        return prestamo;
    }

    //busca todos los prestamos
    public List<Prestamo> buscarPrestamos() {
        return prestamoDao.findAll();
    }
    //busca prestamo por id de prestamo
    public Prestamo buscarPrestamoPorId(long id) throws PrestamoNoExisteException{
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new PrestamoNoExisteException("Prestamo no encontrado");
        }
        return prestamo;
    }

    //buscar prestamo por cliente
    public List<Prestamo> buscarPrestamosPorCliente(long dni) throws ClientNoExisteException, PrestamoNoExisteException {
        Cliente cliente = clienteService.buscarClientePorDni(dni);
        if (cliente == null) {
            throw new ClientNoExisteException("El cliente con DNI: " + dni + " no existe.");
        }
        Set<Prestamo> prestamos = cliente.getPrestamos();
        if (prestamos == null || prestamos.isEmpty()) {
            throw new PrestamoNoExisteException("El cliente con DNI: " + dni + " no tiene prestamos solicitados.");
        }
        return new ArrayList<>(prestamos);
    }

    //actualizar datos
    public Prestamo actualizarDatosPrestamo(long id, double monto, LoanStatus estado) throws PrestamoNoExisteException, CampoIncorrecto {
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new PrestamoNoExisteException("El prestamo con ID: " + id + " no existe.");
        }
        if (monto != 0) {
            throw new CampoIncorrecto("Esa accion no esta permtida");
        }
        if (estado != null) {
            prestamo.setLoanStatus(estado);
        }
        prestamoDao.update(prestamo);
        return prestamo;
    }

    //delete
    public Prestamo cerrarPrestamo(long id) throws PrestamoNoExisteException, CampoIncorrecto {
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new PrestamoNoExisteException("El prestamo con ID: " + id + " no existe.");
        }
        prestamo.setLoanStatus(LoanStatus.CERRADO);
        actualizarDatosPrestamo(id, 0, LoanStatus.CERRADO /*, 0, 0*/);
        return prestamo;
    }

    //otros metodos
    public double calcularInteres(PrestamoDto prestamoDto) throws CampoIncorrecto{
        int score = 600;
        Prestamo prestamo = new Prestamo(prestamoDto, score);
        if (prestamo == null || prestamo.getMonto() <= 0 || prestamo.getPlazoMeses() <= 0) {
            throw new CampoIncorrecto("El monto y plazo del prestamo deben ser mayores a cero.");
        }
        double monto = prestamo.getMonto();
        int plazoMeses = prestamo.getPlazoMeses();
        double tiempo = plazoMeses / 12.0;
        double interes = monto * 0.40 * tiempo; //0.40 es la tasa de interes anual
        double montoConInteres = monto + interes;
        return montoConInteres;
    }

    //actuliza balance si el prestamo es rechazado

}
