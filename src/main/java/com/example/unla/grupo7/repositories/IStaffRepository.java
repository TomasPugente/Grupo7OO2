package com.example.unla.grupo7.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.unla.grupo7.entities.Staff;

@Repository
public interface IStaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByEmailIgnoreCase(String email);

    Optional<Staff> findByDni(int dni);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByDni(int dni);

    List<Staff> findByActivoTrue();

    List<Staff> findByActivoFalse();
}