package com.example.unla.grupo7.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data 
@Entity 
@Table (uniqueConstraints = @UniqueConstraint (columnNames = {"unidad_de_venta_id", "fecha"}))
public class CierreCaja {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idCierreCaja;

    private LocalDate fecha;
    private BigDecimal totalVentas;
    private int cantidadPedidos;

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "unidad_de_venta_id", nullable = false)
    private UnidadDeVenta unidadDeVenta;

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cajero_id", nullable = false)
    private Staff cajero;

    @CreationTimestamp 
    private LocalDateTime createdAt;
}