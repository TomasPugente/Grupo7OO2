package com.example.unla.grupo7.services;

import java.math.BigDecimal;

import com.example.unla.grupo7.dtos.CanonDTO;
import com.example.unla.grupo7.dtos.LiquidacionDTO;
import com.example.unla.grupo7.entities.Costos;
import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;

public interface ICostosService {
    LiquidacionDTO liquidarSueldo(Staff staff, Festival festival) ; 
    BigDecimal calcularCanon(UnidadDeVenta unidad);

    BigDecimal costoPorSuperficie(UnidadDeVenta unidad, Costos costos); 
    CanonDTO detalleCanon(UnidadDeVenta unidad);
}
