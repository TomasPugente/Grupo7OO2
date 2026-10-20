package com.example.unla.grupo7.services;

import java.util.List;

import com.example.unla.grupo7.dtos.PlatoDTO;

public interface IPlatoService {

    /** Platos vigentes (no eliminados) de la unidad del usuario autenticado. */
    List<PlatoDTO> listar(String username);

    PlatoDTO obtener(Long id, String username);

    /** Alta. Si existía un plato eliminado con el mismo nombre, se reactiva con los nuevos datos. */
    PlatoDTO crear(PlatoDTO dto, String username);

    PlatoDTO modificar(Long id, PlatoDTO dto, String username);

    /** Baja lógica. */
    void eliminar(Long id, String username);
}