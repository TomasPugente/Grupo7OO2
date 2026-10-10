package com.example.unla.grupo7.entities;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode (callSuper = false)
@Entity 
public class Cajero extends Staff{
    private String turno;

    private LocalTime horarioEntrada;

    private LocalTime horarioSalida;
}
