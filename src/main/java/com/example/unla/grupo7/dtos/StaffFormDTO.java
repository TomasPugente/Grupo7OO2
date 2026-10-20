package com.example.unla.grupo7.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

@Data
public class StaffFormDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @Positive
    private int dni;

    @NotNull
    private LocalDate fechaNacimiento;

    @NotNull
    private LocalDate fechaIngreso;

    @NotNull
    @Positive
    private BigDecimal sueldo;

    @NotBlank
    @Email
    private String email;

    private Long unidadDeVentaId;
}
