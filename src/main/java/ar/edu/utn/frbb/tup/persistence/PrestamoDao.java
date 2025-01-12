package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.persistence.entity.PrestamoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrestamoDao extends AbstractBaseDao {

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

    //agregar exception
    public Prestamo update(Prestamo prestamo) {
        Prestamo actualizado = findPrestamo(prestamo.getId());
        if (actualizado != null) {
//            if (prestamo.getCuotasPagadas() != 0) {
//                actualizado.setCuotasPagadas(prestamo.getCuotasPagadas());
//            }
//            if (prestamo.getCuotasRestantes() != 0) {
//                actualizado.setCuotasRestantes(prestamo.getCuotasRestantes());
//            }
            System.out.println("Datos actualizados.");
        } else {
            throw new IllegalArgumentException("Prestamo no encontrado.");
        }
        return actualizado;
    }
}
