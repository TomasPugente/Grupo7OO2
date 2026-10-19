package com.example.unla.grupo7.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.unla.grupo7.entities.Responsable;

@Repository("responsableRepository")
public interface IResponsableRepository extends JpaRepository<Responsable, Long> {

    boolean existsByDni(int dni);
}
