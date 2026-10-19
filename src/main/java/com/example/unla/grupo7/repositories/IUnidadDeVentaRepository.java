package com.example.unla.grupo7.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.unla.grupo7.entities.UnidadDeVenta;

public interface IUnidadDeVentaRepository
        extends JpaRepository<UnidadDeVenta, Long> {

    // Orden original: por ID de menor a mayor
    List<UnidadDeVenta> findByFestival_IdFestivalOrderByIdUnidadDeVentaAsc(
            Long idFestival);

    // Orden por recaudación: de menor a mayor
    List<UnidadDeVenta> findByFestival_IdFestivalOrderByRecaudacionAsc(
            Long idFestival);

    // Orden por recaudación: de mayor a menor
    List<UnidadDeVenta> findByFestival_IdFestivalOrderByRecaudacionDesc(
            Long idFestival);
}
