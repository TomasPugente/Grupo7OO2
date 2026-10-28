package com.example.unla.grupo7.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatoEstrellaDTO {

    private PlatoDTO plato;
    private long cantidadVendida;
}