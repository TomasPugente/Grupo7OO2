package com.example.unla.grupo7.services;

import java.time.LocalDate;

import com.example.unla.grupo7.dtos.RentabilidadDTO;

public interface IRentabilidadService {

    RentabilidadDTO calcularPorUnidad(long idUnidadDeVenta);

    RentabilidadDTO calcularPorUnidadEntreFechas(long idUnidadDeVenta,
                                                 LocalDate desde,
                                                 LocalDate hasta);
}
