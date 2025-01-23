package ar.edu.utn.frbb.tup.persistence.imp;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClientNoExisteException;
import ar.edu.utn.frbb.tup.persistence.AbstractBaseDao;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import ar.edu.utn.frbb.tup.persistence.entity.ClienteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ClienteDaoImp extends AbstractBaseDao implements ClienteDao {
    @Autowired
    CuentaDao cuentaDao;
    @Autowired
    PrestamoDao prestamoDao;

    @Override
    protected String getEntityName() {
        return "CLIENTE";
    }

    public Cliente find(long dni, boolean loadComplete) {
        if (getInMemoryDatabase().get(dni) == null)
            return null;
        Cliente cliente = ((ClienteEntity) getInMemoryDatabase().get(dni)).toCliente();
        if (loadComplete) {
            for (Cuenta cuenta :
                    cuentaDao.buscarCuentasByCliente(dni)) {
                cliente.addCuenta(cuenta);
            }
            for (Prestamo prestamo :
                    prestamoDao.buscarPrestamoPorCliente(dni)) {
                cliente.addPrestamo(prestamo);
            }
        }
        return cliente;
    }

    public List<Cliente> findAll() {
        List<Cliente> clientes = new ArrayList<>();

        for (Object object : getInMemoryDatabase().values()) {
            ClienteEntity clienteEntity = (ClienteEntity) object;
            Cliente cliente = clienteEntity.toCliente();

            Set<Cuenta> cuentasSet = new HashSet<>();
            for (long cuentaId : clienteEntity.getCuentas()) {
                Cuenta cuenta = cuentaDao.find(cuentaId);
                if (cuenta != null) {
                    cuentasSet.add(cuenta);
                }
            }
            cliente.setCuentas(cuentasSet);

            Set<Prestamo> prestamosSet = new HashSet<>();
            for (long prestamoId : clienteEntity.getPrestamos()) {
                Prestamo prestamo = prestamoDao.findPrestamo(prestamoId);
                if (prestamo != null) {
                    prestamosSet.add(prestamo);
                }
            }
            cliente.setPrestamos(prestamosSet);

            clientes.add(cliente);
        }
        return clientes;
    }

    //aca arroja empty fields
    public void save(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity(cliente);
        getInMemoryDatabase().put(entity.getId(), entity);
    }

    public Cliente update(Cliente cliente) throws ClientNoExisteException {
        Cliente actualizado = find(cliente.getDni(), true);
        if(actualizado  != null) {
            if (cliente.getEmail() != null) {
                actualizado.setEmail(cliente.getEmail());
            }
            if (cliente.getTelefono() != null) {
                actualizado.setTelefono(cliente.getTelefono());
            }
            System.out.println("Datos actualizados con exito.");
        } else {
            throw new ClientNoExisteException("Cliente no encontrado.");
        }
        return actualizado;
    }
}
