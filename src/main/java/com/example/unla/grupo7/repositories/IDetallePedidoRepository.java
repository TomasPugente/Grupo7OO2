package com.example.unla.grupo7.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.unla.grupo7.entities.DetallePedido;

public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    
}
