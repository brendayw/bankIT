package ar.edu.utn.frbb.tup.model.users.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDto(
        @NotBlank String username,
        @NotBlank String password) {
}