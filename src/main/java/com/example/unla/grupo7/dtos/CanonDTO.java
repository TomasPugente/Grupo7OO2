package com.example.unla.grupo7.dtos;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

/** Desglose del canon de una unidad de venta. No se persiste. */
@Data
@NoArgsConstructor
public class CanonDTO {

    private String nombreUnidad;
    private String codigo;
    private String tipoUnidad;
    private String nombreFestival;

    // Superficie ocupada
    private BigDecimal superficie;
    private BigDecimal costoPorSuperficie;
    private BigDecimal subtotalSuperficie;

    // Solo Food Truck
    private boolean requiereElectricidad;
    private BigDecimal plusElectricidad = BigDecimal.ZERO;

    // Solo Puesto Desarmable
    private int tiempoMontaje;
    private BigDecimal costoPorMontaje = BigDecimal.ZERO;
    private BigDecimal descuentoMontaje = BigDecimal.ZERO;

    private BigDecimal total;
}