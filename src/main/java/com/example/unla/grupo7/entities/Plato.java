package com.example.unla.grupo7.entities;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data 
public class Plato {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;

    private BigDecimal precioVenta;

    private BigDecimal costoProduccion;

    private boolean activo;

    @ManyToMany (fetch = FetchType.LAZY)
    @JoinColumn (name="detallePedidoId", nullable = true)
    private Set<DetallePedido> detallePedido;
}
