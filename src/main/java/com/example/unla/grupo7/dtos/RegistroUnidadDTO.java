package com.example.unla.grupo7.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroUnidadDTO {

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

    @NotBlank
    @Size(min = 3, max = 45)
    private String usuario;

    @Email
    private String email;

    @NotBlank
    private String nombreComercial;

    @NotBlank
    @Size(min = 10, max = 10)
    private String codigo;

    @Positive
    private double superficie;

    @NotNull
    private Long festivalId;

    @NotBlank
    private String tipo;

    private String patente;

    private Boolean requiereElectricidad;

    private Integer cantidadCarpas;

    private Integer tiempoMontaje;
}
