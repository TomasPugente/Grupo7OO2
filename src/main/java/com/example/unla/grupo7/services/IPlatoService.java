package com.example.unla.grupo7.services;
import com.example.unla.grupo7.dtos.PlatoFiltroDTO;
import java.util.List;
import java.util.Optional;

import com.example.unla.grupo7.dtos.PlatoDTO;
import com.example.unla.grupo7.dtos.PlatoEstrellaDTO;

public interface IPlatoService {

    /** Platos vigentes (no eliminados) de la unidad del usuario autenticado. */
    List<PlatoDTO> listar(String username);

    /** Platos vigentes de la unidad que cumplen todos los criterios cargados (los vacíos se ignoran). */
    List<PlatoDTO> filtrar(PlatoFiltroDTO filtro, String username);

    PlatoDTO obtener(Long id, String username);

    /** Alta. Si existía un plato eliminado con el mismo nombre, se reactiva con los nuevos datos. */
    PlatoDTO crear(PlatoDTO dto, String username);

    PlatoDTO modificar(Long id, PlatoDTO dto, String username);

    /** Baja lógica. */
    void eliminar(Long id, String username);

    /** Plato con mayor cantidad total vendida en la unidad del usuario, con su cantidad. */
    Optional<PlatoEstrellaDTO> obtenerPlatoEstrella(String username);
}