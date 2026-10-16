package com.example.unla.grupo7.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data 
public class FestivalDTO {
    private long id;

    private String nombre;

    private String temporada;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private boolean activo;
}
