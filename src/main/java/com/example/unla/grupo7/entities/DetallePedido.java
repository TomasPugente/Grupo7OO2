package com.example.unla.grupo7.entities;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data 
public class DetallePedido {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long idDetallePedido;

    private int cantidad;

    @ManyToMany (fetch = FetchType.LAZY, mappedBy = "detallePedido")
    private Set<Pedido> pedidos;

    @ManyToMany (fetch = FetchType.LAZY, mappedBy = "detallePedido")
    private Set<Plato> platos;

    @CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
