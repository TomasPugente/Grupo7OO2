package com.example.unla.grupo7.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.unla.grupo7.entities.Costos;

public interface ICostosRepository extends JpaRepository<Costos, Long>{
    Optional<Costos> findByFestival_IdFestival(Long idFestival);
}
