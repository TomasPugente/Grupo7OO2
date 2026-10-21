package com.example.unla.grupo7.services;

import java.util.List;

import com.example.unla.grupo7.dtos.StaffFormDTO;
import com.example.unla.grupo7.entities.Staff;

public interface IStaffService {

    List<Staff> listarActivos();

    List<Staff> listarInactivos();

    Staff buscarPorId(long id);

    void alta(StaffFormDTO dto);

    void modificar(long id, StaffFormDTO dto);

    void bajaLogica(long id);

    void reactivar(long id);
}