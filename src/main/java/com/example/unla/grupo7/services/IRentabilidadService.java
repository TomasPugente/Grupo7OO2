package com.example.unla.grupo7.services;

import com.example.unla.grupo7.dtos.RentabilidadDTO;

public interface IRentabilidadService {

    RentabilidadDTO calcularPorUnidad(long idUnidadDeVenta);
}
