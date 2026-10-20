package com.example.unla.grupo7.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter 
public class StaffDTO {
      private long id;

    private String nombre;
    private String apellido;
    private int dni;
    private LocalDate fechaNacimiento;
    private LocalDate fechaIngreso;
    private BigDecimal sueldo;
    private String email;
    private boolean activo;
}
