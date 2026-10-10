package com.example.unla.grupo7.entities;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data 
@Entity 
public class Festival {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long idFestival;

    private String nombre;

    private String temporada;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private boolean activo;

    @OneToMany(fetch= FetchType.LAZY, mappedBy = "festival")
    private Set<UnidadDeVenta> unidadesDeVenta;
    
    @OneToOne (fetch = FetchType.LAZY, mappedBy = "festival")
    private Costos costos;

    @CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
