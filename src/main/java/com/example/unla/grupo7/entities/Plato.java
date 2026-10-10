package com.example.unla.grupo7.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data 
@Entity 
public class Plato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private BigDecimal precioVenta;

    private BigDecimal costoProduccion;

    private boolean activo;

    @OneToMany(mappedBy = "plato", fetch = FetchType.LAZY)
    private Set<DetallePedido> detallePedidos = new HashSet<>();

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name="unidadDeVentaId", nullable = true)
    private UnidadDeVenta unidadDeVenta;

}
