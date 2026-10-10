package com.example.unla.grupo7.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data 
public class Pedido {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long idPedido;

    private LocalDate fechaTransaccion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn (name="detallePedidoId", nullable = true)
    private Set<DetallePedido> detallePedidos;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "unidadDeVentaId", nullable = true)
    private UnidadDeVenta unidadDeVenta;
    
    @CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
