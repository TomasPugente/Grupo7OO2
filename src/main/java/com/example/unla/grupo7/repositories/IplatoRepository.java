package com.example.unla.grupo7.repositories;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.unla.grupo7.entities.Plato;

@Repository("platoRepository")
public interface IplatoRepository extends JpaRepository<Plato, Long> {

    // Platos vigentes (no eliminados lógicamente) de una unidad
    List<Plato> findByUnidadDeVenta_IdUnidadDeVentaAndActivoTrueOrderByNombreAsc(Long idUnidadDeVenta);

    // Todos los platos (activos o no) de una unidad con ese nombre
    List<Plato> findByUnidadDeVenta_IdUnidadDeVentaAndNombreIgnoreCase(Long idUnidadDeVenta, String nombre);

        // Filtro por uno o varios atributos. Siempre excluye los eliminados lógicamente (activo = false).
    @Query("""
            select p from Plato p
            where p.unidadDeVenta.idUnidadDeVenta = :idUnidad
              and p.activo = true
              and lower(p.nombre) like lower(concat('%', :nombre, '%'))
              and p.precioVenta between :precioMin and :precioMax
              and p.costoProduccion between :costoMin and :costoMax
            order by p.nombre asc
            """)
    List<Plato> filtrar(@Param("idUnidad") Long idUnidad,
                        @Param("nombre") String nombre,
                        @Param("precioMin") BigDecimal precioMin,
                        @Param("precioMax") BigDecimal precioMax,
                        @Param("costoMin") BigDecimal costoMin,
                        @Param("costoMax") BigDecimal costoMax);
}