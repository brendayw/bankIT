package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.exception.CampoIncorrecto;
import ar.edu.utn.frbb.tup.model.exception.cliente.ClienteMayorDeEdadException;
import ar.edu.utn.frbb.tup.model.exception.cliente.TipoPersonaNoSoportada;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

@Component
public class ClienteValidator {

    public void validateCliente(ClienteDto clienteDto) throws ClienteMayorDeEdadException, TipoPersonaNoSoportada, CampoIncorrecto {
        validatePersona(clienteDto);
        validateFechaNacimiento(clienteDto);
        validateDatosCompletos(clienteDto);
        validateDni(clienteDto);
    }

    public void validatePersona(ClienteDto clienteDto) throws TipoPersonaNoSoportada{
        if (!"F".equals(clienteDto.getTipoPersona()) && !"J".equals(clienteDto.getTipoPersona())) {
            throw new TipoPersonaNoSoportada("El tipo de persona no es correcto. Ingrese F: fisica o J: juridica");
        }
    }

    public void validateFechaNacimiento(ClienteDto clienteDto) throws ClienteMayorDeEdadException, CampoIncorrecto {
        if (clienteDto.getFechaNacimiento() == null || clienteDto.getFechaNacimiento().isEmpty()) {
            throw new CampoIncorrecto("La fecha de nacimiento no puede ser nula o vacía.");
        }
        try {
            LocalDate fechaNacimiento = LocalDate.parse(clienteDto.getFechaNacimiento());
            LocalDate fechaActual = LocalDate.now();
            int edad = Period.between(fechaNacimiento, fechaActual).getYears();

            if (edad < 18) {
                throw new ClienteMayorDeEdadException("El cliente debe ser mayor de 18 años.");
            }
        } catch (DateTimeParseException e) {
            throw new CampoIncorrecto("El formato de fecha de nacimiento es incorrecto. Use 'yyyy-mm-dd'.");
        }
    }

    public void validateDatosCompletos(ClienteDto clienteDto) throws CampoIncorrecto {
        if (clienteDto.getNombre() == null || clienteDto.getNombre().isEmpty()) {
            throw new CampoIncorrecto("El nombre no puede ser nulo.");
        }
        if (clienteDto.getApellido() == null || clienteDto.getApellido().isEmpty()) {
            throw new CampoIncorrecto("El apellido no puede ser nulo.");
        }
        if (clienteDto.getFechaNacimiento() == null || clienteDto.getFechaNacimiento().isEmpty()) {
            throw new CampoIncorrecto("La fecha de nacimiento no puede ser nulo.");
        }
        if (clienteDto.getTelefono() == null || clienteDto.getTelefono().isEmpty()) {
            throw new CampoIncorrecto("El telefono no puede ser nulo.");
        }
        if (clienteDto.getEmail() == null || clienteDto.getEmail().isEmpty()) {
            throw new CampoIncorrecto("El email no puede ser nulo.");
        }
        if (clienteDto.getBanco() == null || clienteDto.getBanco().isEmpty()) {
            throw new CampoIncorrecto("El banco no puede ser nulo.");
        }
        if (clienteDto.getTipoPersona() == null || clienteDto.getTipoPersona().isEmpty()) {
            throw new CampoIncorrecto("El tipoPersona no puede ser nulo.");
        }
    }

    private void validateDni(ClienteDto clienteDto) {
        long dni = clienteDto.getDni();
        if (dni <= 0) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o cero.");
        }
        if (dni < 00_000_000L || dni > 99_999_999L) {
            throw new IllegalArgumentException("Error: El DNI debe tener 8 dígitos y estar entre 00.000.000 y 99.999.999.");
        }
    }
}
