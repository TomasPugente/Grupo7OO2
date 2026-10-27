package com.example.unla.grupo7.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentabilidadDTO {

    private String nombreUnidad;
    private String nombreFestival;

    private BigDecimal pedidosTotales;
    private BigDecimal costoPlatos;
    private BigDecimal sueldos;
    private BigDecimal canon;

    private BigDecimal rentabilidadNeta;

    // Si son null, el reporte es del total. Si están, es del rango [desde, hasta].
    private LocalDate desde;
    private LocalDate hasta;
}
