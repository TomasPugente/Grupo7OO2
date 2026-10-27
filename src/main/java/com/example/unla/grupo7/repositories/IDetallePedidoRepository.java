package com.example.unla.grupo7.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.unla.grupo7.entities.DetallePedido;

public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedido_UnidadDeVenta_IdUnidadDeVenta(long idUnidadDeVenta);
}
