package com.example.unla.grupo7.dtos;

import java.math.BigDecimal;

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
}
