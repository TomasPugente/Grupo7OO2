package com.example.unla.grupo7.repositories;

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
}