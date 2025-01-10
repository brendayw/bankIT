package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.controller.validator.PrestamoValidator;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.LoanStatus;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import ar.edu.utn.frbb.tup.persistence.entity.PrestamoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrestamoService {
    PrestamoDao prestamoDao;
    PrestamoValidator prestamoValidator;
    @Autowired ClienteService clienteService;
    @Autowired CreditScoreService creditScoreService;

    //para calcular el interes

    //solicitar prestamo
    public Prestamo solicitarPrestamo(PrestamoDto prestamoDto) throws ClientNoExisteException {
        Cliente cliente = clienteService.buscarClientePorDni(prestamoDto.getNumeroCliente());
        Prestamo prestamo = new Prestamo(prestamoDto, cliente);
        if (prestamo.getNumeroCliente() == null) {
            throw new IllegalArgumentException("El cliente no pueden ser nulos.");
        }
        creditScoreService.validarScore(prestamo.getNumeroCliente());
        prestamoValidator.validate(prestamoDto);
        PrestamoEntity prestamoEntity = new PrestamoEntity(prestamo);
        prestamoDao.savePrestamo(prestamoEntity);
        return prestamo;
    }

    //aprueba el prestamo
    public void apruebaPrestamo(Prestamo prestamo, Long numeroCliente) throws ClientNoExisteException {
        if(prestamoDao.findPrestamo(prestamo.getId_loan()) != null) {
            throw new IllegalArgumentException("El prestamo " + prestamo + " ya existe.");
        }
        Cliente cliente = clienteService.buscarClientePorDni(numeroCliente);
        if (cliente == null) {
            throw new ClientNoExisteException("El cliente " + numeroCliente + " no existe.");
        }
        clienteService.agregarPrestamo(prestamo, numeroCliente);
        PrestamoEntity prestamoEntity = new PrestamoEntity(prestamo);
        prestamoDao.savePrestamo(prestamoEntity);
    }

    //actualiza estado del prestamo
    public Prestamo actualizarEstado(Long numeroCliente, LoanStatus nuevoEstado) {
        Prestamo prestamo = prestamoDao.findPrestamo(numeroCliente);
        if (prestamo == null) {
            throw new IllegalArgumentException("El cliente no tiene prestamos solicitados.");
        }
        prestamo.setLoanStatus(nuevoEstado);
        return prestamoDao.update(prestamo.getId_loan(), nuevoEstado);
    }

    //calcular interes
    public double calcularInteres(Prestamo prestamo) {
        if (prestamo == null || prestamo.getAmount() <= 0 || prestamo.getTermMonths() <= 0) {
            new IllegalArgumentException("El monto y plazo del prestamo deben ser mayores a cero.");
        }
        double monto = prestamo.getAmount();
        int plazoMeses = prestamo.getTermMonths();
        double tiempo = plazoMeses / 12.0;
        double interes = monto * 0.40 * tiempo; //0.40 es la tasa de interes anual
        double montoConInteres = monto + interes;

        return montoConInteres;
    }

    //calcula la cuota mensual
    private double calcularCuotaMensual(double montoTotal, int plazoMeses) {
        return montoTotal / plazoMeses;
    }

    //calcula cantidad de cuotas restantes
    public void realizarPago(Prestamo prestamo, double pago) {
        if (prestamo == null || pago <= 0) {
            throw new IllegalArgumentException("El pago debe ser mayor que cero.");
        }
        if (pago >= prestamo.getCuotaMensual()) {
            int cuotasPagadas = (int) (pago / prestamo.getCuotaMensual());
            prestamo.setCuotasPagadas(prestamo.getCuotasPagadas() + cuotasPagadas);
            int cuotasRestantes = prestamo.getCuotasRestantes() - cuotasPagadas;
            prestamo.setCuotasRestantes(Math.max(cuotasRestantes, 0));

            if (prestamo.getCuotasRestantes() == 0) {
                prestamo.setLoanStatus(LoanStatus.CERRADO); // prestamo pagado
            }

            System.out.println("Pago realizado. Cuotas pagadas: " + prestamo.getCuotasPagadas() +
                    ", Cuotas restantes: " + prestamo.getCuotasRestantes());
        } else {
            System.out.println("El pago no cubre una cuota mensual completa.");
        }

        // Guardar en base de datos o realizar otras acciones.
    }


    //busca prestamos solicitados por el dni o numero de cliente
    public List<Prestamo> buscarPrestamosPorCliente(Long dni) {
        List<Prestamo> prestamos = prestamoDao.getPrestamoByCliente(dni);
        if (prestamos.isEmpty()) {
            throw new IllegalArgumentException("El cliente no tiene prestamos solicitados.");
        }
        return prestamos;
    }

    //busca prestamo por id de prestamo
    public Prestamo buscarPrestamoPorId(long id) {
        Prestamo prestamo = prestamoDao.findPrestamo(id);
        if (prestamo == null) {
            throw new IllegalArgumentException("Prestamo no encontrado");
        }
        return prestamo;
    }

}
