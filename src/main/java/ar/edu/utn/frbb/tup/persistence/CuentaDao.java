package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.persistence.entity.CuentaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CuentaDao  extends AbstractBaseDao{

    @Override
    protected String getEntityName() {
        return "CUENTA";
    }

    public Cuenta find(long id) {
        if(getInMemoryDatabase().get(id) == null){
            return null;
        }
        Cuenta cuenta = ((CuentaEntity) getInMemoryDatabase().get(id)).toCuenta();
        return cuenta;
    }

    public List<Cuenta> findAll() {
        List<Cuenta> cuentas = new ArrayList<>();
        for (Object object : getInMemoryDatabase().values()) {
            cuentas.add(((CuentaEntity) object).toCuenta());
        }
        return cuentas;
    }

    //busca si el cliente tiene la cuenta en esa moneda
    public Cuenta findByClienteYTipoMoneda(long dni, String tipoMoneda) {
        List<Cuenta> cuentas = findAll();
        for (Cuenta cuenta : cuentas) {
            if (cuenta.getDniTitular() == dni && cuenta.getTipoMoneda().toString().equals(tipoMoneda)) {
                return cuenta;
            }
        }
        return null;
    }

    public void save(Cuenta cuenta) {
        CuentaEntity entity = new CuentaEntity(cuenta);
        getInMemoryDatabase().put(entity.getId(), entity);
    }

    public List<Cuenta> buscarCuentasByCliente(long dni) {
        List<Cuenta> cuentasDelCliente = new ArrayList<>();
        for (Object object: getInMemoryDatabase().values()) {
            CuentaEntity cuenta = ((CuentaEntity) object);
            if (cuenta.toCuenta().getDniTitular() == dni) {
                cuentasDelCliente.add(cuenta.toCuenta());
            }
        }
        return cuentasDelCliente;
    }

    public Cuenta update(Cuenta cuenta) {
        Cuenta actualizado = find(cuenta.getNumeroCuenta());
        if (actualizado != null) {
            if (cuenta.getBalance() != 0.0) {
                actualizado.setBalance(cuenta.getBalance());
            }
            System.out.println("Datos actualizados con exito.");
        }
        return actualizado;
    }
}