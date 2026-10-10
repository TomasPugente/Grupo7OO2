package com.example.unla.grupo7.entities;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode (callSuper = false)
public class Cocinero {
    private String especialidad;

    private BigDecimal plusPorCategoria;
}
