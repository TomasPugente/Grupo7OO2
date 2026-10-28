package com.example.unla.grupo7.services;

import com.example.unla.grupo7.dtos.ReporteVentaDTO;

public interface IReporteVentaService {
    // Recaudación total (suma de precioTotal de los pedidos) de un festival.
    ReporteVentaDTO recaudacionPorFestival(long idFestival);
}
