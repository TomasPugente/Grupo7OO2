package com.example.unla.grupo7.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.unla.grupo7.entities.UnidadDeVenta;

@Repository("unidadDeVentaRepository")
public interface IUnidadDeVentaRepository
        extends JpaRepository<UnidadDeVenta, Long> {

    boolean existsByCodigo(String codigo);

    // Orden original: por ID de menor a mayor
    List<UnidadDeVenta> findByFestival_IdFestivalOrderByIdUnidadDeVentaAsc(
            Long idFestival);

    // Orden por recaudación: de menor a mayor
    List<UnidadDeVenta> findByFestival_IdFestivalOrderByRecaudacionAsc(
            Long idFestival);

    // Orden por recaudación: de mayor a menor
    List<UnidadDeVenta> findByFestival_IdFestivalOrderByRecaudacionDesc(
            Long idFestival);

    List<UnidadDeVenta> findByFestival_IdFestival(Long idFestival);
}
