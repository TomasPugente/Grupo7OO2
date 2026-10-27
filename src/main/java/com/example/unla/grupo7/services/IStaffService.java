package com.example.unla.grupo7.services;

import java.util.List;

import com.example.unla.grupo7.dtos.StaffFormDTO;
import com.example.unla.grupo7.entities.Staff;

public interface IStaffService {

    List<Staff> listarActivos();

    List<Staff> listarInactivos();

    Staff buscarPorId(long id);

    Staff buscarPorNombre(String username);

    void alta(StaffFormDTO dto);

    void modificar(long id, StaffFormDTO dto);

    void bajaLogica(long id);

    void reactivar(long id);

    List<Staff> listarActivosPorUnidad(Long idUnidadDeVenta);

    List<Staff> listarInactivosPorUnidad(Long idUnidadDeVenta);

    List<Staff> filtrarActivosPorUnidad(
        Long idUnidadDeVenta,
        String nombre,
        String apellido,
        String dni,
        java.math.BigDecimal sueldoMinimo,
        java.math.BigDecimal sueldoMaximo,
        String turno,
        String especialidad
    );

}