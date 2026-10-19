package com.example.unla.grupo7.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMayoresCanonDTO {

    private String nombreComercial;
    private int codigo;
    private String tipoUnidad;
    private double canon;
}

