package com.example.unla.grupo7.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.unla.grupo7.entities.Festival;

@Repository("festivalRepository")
public interface IFestivalRepository
        extends JpaRepository<Festival, Long> {
}