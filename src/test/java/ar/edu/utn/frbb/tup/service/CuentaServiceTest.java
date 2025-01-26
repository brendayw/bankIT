package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteMayorDeEdadException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.CuentaYaExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoCuentaYaExisteException;
import ar.edu.utn.frbb.tup.model.exception.cuenta.TipoMonedaNoSoportada;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import ar.edu.utn.frbb.tup.service.imp.ClienteServiceImp;
import ar.edu.utn.frbb.tup.service.imp.CuentaServiceImp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaServiceTest {

    @Mock private CuentaDao cuentaDao;
    @InjectMocks private CuentaValidator cuentaValidator;
    @InjectMocks private CuentaServiceImp cuentaService;
    @InjectMocks private ClienteServiceImp clienteService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CuentaDto crearCuentaDto(long dni, double balance, String tipoMoneda, String tipoCuenta) {
        CuentaDto cuenta = new CuentaDto();
        cuenta.setDniTitular(dni);
        cuenta.setTipoMoneda(tipoMoneda);
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setBalance(balance);
        return cuenta;
    }

    //crea cuenta
//    @Test
//    public void testCrearCuentaNuevo_Success() throws CuentaYaExisteException, ClientNoExisteException, TipoMonedaNoSoportada, TipoCuentaYaExisteException, CuentaNoSoportadaException {
//        CuentaDto cuentaNueva = crearCuentaDto(40860006, 200000.0, "P", "C");
//
//        Cuenta nuevaCuenta = new Cuenta(cuentaNueva);
//
//        when(cuentaDao.find(anyLong())).thenReturn(null);
//        when(clienteService.agregarCuenta(any(), anyLong())).thenReturn(false);
//
//        Cuenta result = cuentaService.darDeAltaCuenta(cuentaNueva);
//
//        assertNotNull(result, "La cuenta creada no debe ser nula");
//        assertEquals(nuevaCuenta.getTipoCuenta(), result.getTipoCuenta());
//        assertEquals(nuevaCuenta.getTipoMoneda(), result.getTipoMoneda());
//        verify(cuentaDao, times(1)).save(any(Cuenta.class));
//
//    }

    //cuenta ya existe

    //busca por id

    //busca por dni cliente

    //actualiza balance

    //desactiva la cuenta

}
