package com.example.unla.grupo7.entities;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data 
@Entity 
public class UnidadDeVenta {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    protected long idUnidadDeVenta;

    protected String nombreComercial;

    protected double superficie;

    protected int codigo;

    protected boolean activo;

    @ManyToOne (fetch= FetchType.LAZY)
    @JoinColumn (name="festivalId")
    protected Festival festival;

    @OneToMany (fetch= FetchType.LAZY, mappedBy = "unidadDeVenta")
    protected Set<Staff> staff;

    @OneToMany (fetch = FetchType.LAZY, mappedBy = "unidadDeVenta")
    protected Set<Plato> plato;

    @OneToMany (fetch =FetchType.LAZY, mappedBy = "unidadDeVenta")
    protected Set<Pedido> pedido;

    @OneToOne (fetch = FetchType.LAZY, mappedBy = "unidadDeVenta")
    protected Responsable responsable;
    
    @CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
