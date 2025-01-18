package ar.edu.utn.frbb.tup.persistence.imp;

import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.persistence.AbstractBaseDao;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import ar.edu.utn.frbb.tup.persistence.entity.PrestamoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrestamoDaoImp extends AbstractBaseDao implements PrestamoDao {
    @Override
    protected String getEntityName() {
        return "PRESTAMO";
    }

    public void savePrestamo(Prestamo prestamo) {
        PrestamoEntity entity = new PrestamoEntity(prestamo);
        getInMemoryDatabase().put(entity.getId(), entity);
    }

    //busca prestamo por id del prestamo
    public Prestamo findPrestamo(long id) {
        if (getInMemoryDatabase().get(id) == null) {
            return null;
        }
        Prestamo prestamo = ((PrestamoEntity) getInMemoryDatabase().get(id)).toPrestamo();
        return prestamo;
    }

    public List<Prestamo> findAll() {
        List<Prestamo> prestamos = new ArrayList<>();
        for (Object object : getInMemoryDatabase().values()) {
            prestamos.add(((PrestamoEntity) object).toPrestamo());
        }
        return prestamos;
    }

    //obtiene prestamos por numero de cliente
    public List<Prestamo> buscarPrestamoPorCliente(long dni) {
        List<Prestamo> prestamosDelCliente = new ArrayList<>();
        for (Object object: getInMemoryDatabase().values()) {
            PrestamoEntity prestamo = ((PrestamoEntity) object);
            if (prestamo.toPrestamo().getDniTitular() == dni) {
                prestamosDelCliente.add(prestamo.toPrestamo());
            }
        }
        return prestamosDelCliente;
    }

    //verificar
    //agregar exception
    public Prestamo update(Prestamo prestamo) {
        Prestamo actualizado = findPrestamo(prestamo.getId());
        if (actualizado != null) {
            System.out.println("Datos actualizados.");
        }
        return actualizado;
    }
}
