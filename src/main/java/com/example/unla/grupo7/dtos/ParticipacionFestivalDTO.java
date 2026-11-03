package com.example.unla.grupo7.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO no persistente: un festival y la unidad de venta en la que trabajó/trabaja el empleado.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipacionFestivalDTO {
    private String nombreFestival;
    private String temporada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean festivalActivo;

    private String nombreUnidad;
    private String codigoUnidad;
    private String tipoUnidad;
}
