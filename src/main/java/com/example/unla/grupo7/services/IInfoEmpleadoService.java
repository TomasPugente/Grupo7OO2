package com.example.unla.grupo7.services;

import com.example.unla.grupo7.dtos.InfoEmpleadoDTO;

public interface IInfoEmpleadoService {

    // Información del empleado identificado por su username: datos propios y
    // festivales / unidades de venta en las que trabajó.
    InfoEmpleadoDTO obtenerPorUsername(String username);
}
