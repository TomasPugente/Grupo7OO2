
package com.example.unla.grupo7.repositories;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.unla.grupo7.entities.Pedido;

public interface IPedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUnidadDeVenta_IdUnidadDeVenta(long idUnidadDeVenta);

    // Rentabilidad: pedidos entre fechas
    List<Pedido> findByUnidadDeVenta_IdUnidadDeVentaAndFechaTransaccionBetween(
            long idUnidadDeVenta,
            LocalDate desde,
            LocalDate hasta);

    // Dashboard: últimos cinco pedidos de la unidad
    List<Pedido> findFirst5ByUnidadDeVenta_IdUnidadDeVentaOrderByFechaTransaccionDesc(
            long idUnidadDeVenta);

            // Reporte de recaudación: total y cantidad de pedidos de un festival
    @Query("SELECT SUM(p.precioTotal) FROM Pedido p "
            + "WHERE p.unidadDeVenta.festival.idFestival = :idFestival")
    BigDecimal sumarPrecioTotalPorFestival(@Param("idFestival") long idFestival);

    @Query("SELECT COUNT(p) FROM Pedido p "
            + "WHERE p.unidadDeVenta.festival.idFestival = :idFestival")
    long contarPedidosPorFestival(@Param("idFestival") long idFestival);

    @Query ("""
       select coalesce(sum(p.precioTotal), 0) from Pedido p
       where p.unidadDeVenta.idUnidadDeVenta = :unidadId and p.fechaTransaccion = :fecha
       """)
        BigDecimal totalDelDia(@Param("unidadId") Long unidadId, @Param("fecha") LocalDate fecha);

        long countByUnidadDeVenta_IdUnidadDeVentaAndFechaTransaccion(Long unidadId, LocalDate fecha);

        @Query("""
       select distinct p from Pedido p
       left join fetch p.detallePedidos d
       left join fetch d.plato
       where p.unidadDeVenta.idUnidadDeVenta = :unidadId and p.fechaTransaccion = :fecha
       order by p.idPedido
       """)
        List<Pedido> findDelDia(@Param("unidadId") Long unidadId, @Param("fecha") LocalDate fecha);
}

