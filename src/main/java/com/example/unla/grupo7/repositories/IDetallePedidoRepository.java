package com.example.unla.grupo7.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.unla.grupo7.entities.DetallePedido;

public interface IDetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedido_UnidadDeVenta_IdUnidadDeVenta(long idUnidadDeVenta);

    List<DetallePedido> findByPedido_UnidadDeVenta_IdUnidadDeVentaAndPedido_FechaTransaccionBetween(
            long idUnidadDeVenta, LocalDate desde, LocalDate hasta);

    // Platos de la unidad agrupados por cantidad vendida total, desc.
    // Devuelve filas [Plato, Long totalCantidad]. Usar Pageable con size=1 para el ganador.
    @Query("""
            select dp.plato, sum(dp.cantidad) as total
            from DetallePedido dp
            where dp.pedido.unidadDeVenta.idUnidadDeVenta = :idUnidad
            group by dp.plato
            order by total desc
            """)
    List<Object[]> findPlatosMasVendidos(@Param("idUnidad") long idUnidad, Pageable pageable);
}