package com.example.unla.grupo7.entities;

import java.math.BigDecimal;
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
public class Costos {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long idCostos;

    private BigDecimal costoPorSuperficie;

    private BigDecimal CostoPorMontaje;

    private BigDecimal plusPorElectricidad;

    private BigDecimal sueldoBase;

    private BigDecimal montoAnioAntiguedad;

    private BigDecimal plusDeCocinero;

    private BigDecimal plusDeCocineroAyudante;

    private BigDecimal plusDeLavaPlatos;

    @OneToOne (fetch=FetchType.LAZY)
    @JoinColumn (name="FestivalId", nullable = true)
    private Festival festival;

    @CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
    
}
