package com.example.unla.grupo7.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class StaffFormDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @Min(value = 10000000, message = "El DNI debe tener exactamente 8 dígitos.")
    @Max(value = 99999999, message = "El DNI debe tener exactamente 8 dígitos.")
    private int dni;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Debe ingresar la fecha de nacimiento.")
    private LocalDate fechaNacimiento;

    private LocalDate fechaIngreso;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String tipoEmpleado;

    private String especialidad;

    private String turno;

    private Long unidadDeVentaId;
}
