package com.example.unla.grupo7.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data 
public class Responsable {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long idResponsable;

    private String nombre;

    private String apellido;

    private int dni;
    
    private LocalDate fechaNacimiento;

    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn (name="unidadDeVentaId", nullable = true)
    private UnidadDeVenta unidadDeVenta;

    @CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;


}
