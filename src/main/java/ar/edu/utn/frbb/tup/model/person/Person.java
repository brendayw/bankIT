package ar.edu.utn.frbb.tup.model.person;

import ar.edu.utn.frbb.tup.model.person.dto.PersonDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "dni")
public class Person {
    private Long dni;
    private String apellido;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String email;

    public Person(PersonDto dto) {
        this.dni = dto.dni();
        this.apellido = dto.apellido();
        this.nombre = dto.nombre();
        this.fechaNacimiento = dto.fechaNacimiento();
        this.telefono = dto.telefono();
        this.email = dto.email();
    }

    @Override
    public String toString() {
        return "Person: " +
                "\ndni=" + dni +
                "\napellido=" + apellido +
                "\nnombre=" + nombre +
                "\nfechaNacimiento=" + fechaNacimiento +
                "\ntelefono='" + telefono +
                "\nemail='" + email;
    }
}