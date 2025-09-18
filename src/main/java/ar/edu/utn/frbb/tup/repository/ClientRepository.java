package ar.edu.utn.frbb.tup.repository;

import ar.edu.utn.frbb.tup.model.cliente.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Page<Client> findByActiveTrue(Pageable pagination);

    @Query("""
            SELECT c FROM Cliente c
            WHERE c.persona.dni = :dni
    """)
    Optional<Client> findByPersonaDni(@Param("dni") Long dni);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c WHERE c.persona.dni = :dni")
    boolean existsByPersonaDni(@Param("dni") Long dni);
}