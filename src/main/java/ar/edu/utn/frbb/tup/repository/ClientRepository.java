package ar.edu.utn.frbb.tup.repository;

import ar.edu.utn.frbb.tup.model.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("""
            SELECT c FROM Cliente c
            WHERE c.person.dni = :dni
    """)
    Optional<Client> findByPersonDni(@Param("dni") Long dni);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c WHERE c.person.dni = :dni")
    boolean existsByPersonDni(@Param("dni") Long dni);
}