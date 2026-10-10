package com.example.unla.grupo7.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode (callSuper = false)
@Entity 
public class Cocinero extends Staff{
    private String especialidad;

    private BigDecimal plusPorCategoria;
}
