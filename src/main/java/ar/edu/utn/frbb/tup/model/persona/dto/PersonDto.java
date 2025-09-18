package ar.edu.utn.frbb.tup.model.persona.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PersonDto(
        @NotNull @Min(1000000) @Max(999999999) Long dni,
        @NotBlank String apellido,
        @NotBlank String nombre,
        @NotNull LocalDate fechaNacimiento,
        @NotBlank String telefono,
        @NotBlank @Email String email) {
}
