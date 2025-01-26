package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoPersona;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.*;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import ar.edu.utn.frbb.tup.service.imp.ClienteServiceImp;
import ar.edu.utn.frbb.tup.service.imp.CuentaServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaServiceTest {

    @Mock private CuentaDao cuentaDao;
    @Mock private ClienteServiceImp clienteService;
    @InjectMocks private CuentaServiceImp cuentaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //metodo para crear cuenta con dto
    private CuentaDto crearCuentaDto(long dni, double balance, String tipoMoneda, String tipoCuenta) {
        CuentaDto cuenta = new CuentaDto();
        cuenta.setDniTitular(dni);
        cuenta.setTipoMoneda(tipoMoneda);
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setBalance(balance);
        return cuenta;
    }

    //metodo para crear cuenta
    private Cuenta crearCuenta(long dniCliente, double balance, TipoMoneda tipoMoneda, TipoCuenta tipoCuenta) {
        Cuenta cuenta = new Cuenta();
        cuenta.setDniTitular(dniCliente);
        cuenta.setBalance(balance);
        cuenta.setTipoMoneda(tipoMoneda);
        cuenta.setTipoCuenta(tipoCuenta);
        return cuenta;
    }

    //metodo para crear clientes con model
    private Cliente crearCliente(String nombre, String apellido, Long dni, LocalDate fechaNacimiento, String telefono, String email, TipoPersona tipoPersona, String banco) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setDni(dni);
        cliente.setFechaNacimiento(fechaNacimiento);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setTipoPersona(tipoPersona);
        cliente.setBanco(banco);
        cliente.setCuentas(new HashSet<>());
        return cliente;
    }

    //crea cuenta
    @Test
    public void testCrearCuentaNuevo_Success() throws CuentaYaExisteException, ClientNoExisteException, TipoMonedaNoSoportada, TipoCuentaYaExisteException, CuentaNoSoportadaException {
        CuentaDto cuentaNueva = crearCuentaDto(40860006, 200000.0, "P", "C");
        Cuenta nuevaCuenta = new Cuenta(cuentaNueva);

        when(cuentaDao.find(anyLong())).thenReturn(null);
        doNothing().when(clienteService).agregarCuenta(any(), anyLong());

        Cuenta resultado = cuentaService.darDeAltaCuenta(cuentaNueva);

        assertNotNull(resultado, "La cuenta creada no debe ser nula");
        assertEquals(nuevaCuenta.getTipoCuenta(), resultado.getTipoCuenta());
        assertEquals(nuevaCuenta.getTipoMoneda(), resultado.getTipoMoneda());
        verify(cuentaDao, times(1)).save(any(Cuenta.class));
    }

    //cuenta ya existe
    @Test
    public void testCrearCuentaNuevo_Failure() {
        CuentaDto cuentaNueva = crearCuentaDto(40860006, 200000.0, "P", "C");
        Cuenta nueva = new Cuenta();

        when(cuentaDao.find(anyLong())).thenReturn(nueva);
        assertThrows(CuentaYaExisteException.class, () -> cuentaService.darDeAltaCuenta(cuentaNueva));

        verify(cuentaDao, times(0)).save(any(Cuenta.class));
    }

    //busca por id
    @Test
    void testBuscarCuentaPorId_Success() throws CuentaNoExisteException {
        Cuenta cuentaEsperada = new Cuenta();
        cuentaEsperada.setDniTitular(40860006);
        cuentaEsperada.setBalance(200000.0);
        cuentaEsperada.setTipoMoneda(TipoMoneda.PESOS);
        cuentaEsperada.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);

        when(cuentaDao.find(cuentaEsperada.getNumeroCuenta())).thenReturn(cuentaEsperada);

        Cuenta resultado = cuentaService.buscarCuentaPorId(cuentaEsperada.getNumeroCuenta());

        assertNotNull(resultado, "La cuenta no debe ser nula.");
        assertEquals(cuentaEsperada.getNumeroCuenta(), resultado.getNumeroCuenta(), "El ID de la cuenta no es el esperado.");
        assertEquals(cuentaEsperada.getTipoCuenta(), resultado.getTipoCuenta());
        assertEquals(cuentaEsperada.getTipoMoneda(), resultado.getTipoMoneda());
        assertEquals(cuentaEsperada.getBalance(), resultado.getBalance());

        verify(cuentaDao, times(1)).find(cuentaEsperada.getNumeroCuenta());
    }

    //busca cuenta por id que no existe
    @Test
    void testBuscarCuentaPorId_Failure() {
        long id = 12345678;
        when(cuentaDao.find(id)).thenReturn(null);

        CuentaNoExisteException e = assertThrows(CuentaNoExisteException.class, () -> cuentaService.buscarCuentaPorId(id));

        assertEquals("La cuenta con ID: " + id + " no existe.", e.getMessage());
        verify(cuentaDao, times(1)).find(id);
        System.out.println("Excepción capturada: " + e.getMessage());

    }

    //busca cuenta por dni cliente
    @Test
    void testBuscarCuentasPorDni_Success() throws ClientNoExisteException, CuentaNoExisteException {
        Long dni = 40860006L;
        Cliente cliente = crearCliente("Brenda", "Yañez", dni, LocalDate.parse("1997-03-18"),
                "2914789635", "brendayañez@gmail.com", TipoPersona.PERSONA_FISICA, "Nacion");
        Cuenta cuenta1 = crearCuenta(dni, 1000.0, TipoMoneda.PESOS, TipoCuenta.CUENTA_CORRIENTE);
        Cuenta cuenta2 = crearCuenta(dni, 2000.0, TipoMoneda.DOLARES, TipoCuenta.CAJA_AHORRO);
        cliente.getCuentas().add(cuenta1);
        cliente.getCuentas().add(cuenta2);

        when(clienteService.buscarClientePorDni(dni)).thenReturn(cliente);
        List<Cuenta> resultado = cuentaService.buscarCuentaPorCliente(dni);
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(cuenta1));
        assertTrue(resultado.contains(cuenta2));
        verify(clienteService, times(1)).buscarClientePorDni(dni);
    }

    //actualiza balance
    @Test
    void testActualizarBalance_Success() throws CuentaNoExisteException {
        Long dniTitular = 40860006L;
        TipoMoneda moneda = TipoMoneda.PESOS;
        double montoSolicitado = 1500.0;

        Cuenta cuenta = crearCuenta(dniTitular, 2000.0, moneda, TipoCuenta.CUENTA_CORRIENTE);

        Prestamo prestamo = new Prestamo();
        prestamo.setDniTitular(dniTitular);
        prestamo.setMontoSolicitado(montoSolicitado);
        prestamo.setMoneda(moneda);

        when(cuentaDao.findByClienteYTipoMoneda(dniTitular, moneda.toString())).thenReturn(cuenta);
        cuentaService.actualizarBalance(prestamo);

        assertEquals(3500.0, cuenta.getBalance(), 0.01); // 2000 + 1500 = 3500

        verify(cuentaDao, times(1)).save(cuenta);
    }

    @Test
    void testActualizarBalance_Failure() {
        Long dniTitular = 40860006L;
        TipoMoneda moneda = TipoMoneda.PESOS;
        double montoSolicitado = 1500.0;

        Prestamo prestamo = new Prestamo();
        prestamo.setDniTitular(dniTitular);
        prestamo.setMontoSolicitado(montoSolicitado);
        prestamo.setMoneda(moneda);

        when(cuentaDao.findByClienteYTipoMoneda(dniTitular, moneda.toString())).thenReturn(null);
        assertThrows(CuentaNoExisteException.class, () -> cuentaService.actualizarBalance(prestamo));

        verify(cuentaDao, times(0)).save(any(Cuenta.class));
    }

    //desactiva la cuenta
    @Test
    void testDesactivarCuenta_Success() throws CuentaNoExisteException {
        long id = 40860006L;
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(id);
        cuenta.setEstado(true);

        when(cuentaDao.find(id)).thenReturn(cuenta);

        Cuenta cuentaDesactivada = cuentaService.desactivarCuenta(id);

        assertFalse(cuentaDesactivada.isEstado(), "El estado de la cuenta debería ser false");
        verify(cuentaDao, times(1)).update(cuenta);
    }

    @Test
    void testDesactivarCuenta_Failure() {
        long cuentaId = 14533778L;

        when(cuentaDao.find(cuentaId)).thenReturn(null);
        assertThrows(CuentaNoExisteException.class, () -> cuentaService.desactivarCuenta(cuentaId));
        verify(cuentaDao, times(0)).update(any(Cuenta.class));
    }



}
