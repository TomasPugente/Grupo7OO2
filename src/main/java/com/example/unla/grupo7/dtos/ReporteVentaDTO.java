package com.example.unla.grupo7.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO no persistente: reporte de recaudación de un festival.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentaDTO {
    private long idFestival;
    private String nombreFestival;
    private String temporada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private long cantidadPedidos;
    private BigDecimal recaudacionTotal;
}
