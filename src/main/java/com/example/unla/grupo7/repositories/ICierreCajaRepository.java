package com.example.unla.grupo7.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.unla.grupo7.entities.CierreCaja;

public interface ICierreCajaRepository extends JpaRepository<CierreCaja, Long> {
    boolean existsByUnidadDeVenta_IdUnidadDeVentaAndFecha(Long unidadId, LocalDate fecha);
    Optional<CierreCaja> findByUnidadDeVenta_IdUnidadDeVentaAndFecha(Long unidadId, LocalDate fecha);

}
