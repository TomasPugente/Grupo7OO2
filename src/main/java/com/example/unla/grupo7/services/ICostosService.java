package com.example.unla.grupo7.services;

import java.math.BigDecimal;

import com.example.unla.grupo7.entities.Costos;
import com.example.unla.grupo7.entities.UnidadDeVenta;

public interface ICostosService {
    BigDecimal calcularCanon(UnidadDeVenta unidad);

    BigDecimal costoPorSuperficie(UnidadDeVenta unidad, Costos costos); 
}
