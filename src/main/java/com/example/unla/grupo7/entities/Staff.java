package com.example.unla.grupo7.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data 
public class Staff {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    protected long id;

    private String nombre;

    private String apellido;

    private int dni;

    private LocalDate fechaNacimiento;

    private LocalDate fechaIngreso;

    private BigDecimal sueldo;

    private String email;

    private boolean activo;

    @ManyToOne (fetch= FetchType.LAZY)
    @JoinColumn (name="unidadDeVentaId", nullable = true)
    private UnidadDeVenta unidadDeVenta;
}
