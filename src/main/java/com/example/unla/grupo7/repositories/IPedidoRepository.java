package com.example.unla.grupo7.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.unla.grupo7.entities.Pedido;

public interface IPedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUnidadDeVenta_IdUnidadDeVenta(long idUnidadDeVenta);

    List<Pedido> findByUnidadDeVenta_IdUnidadDeVentaAndFechaTransaccionBetween(
            long idUnidadDeVenta, LocalDate desde, LocalDate hasta);
}
