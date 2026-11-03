package com.example.unla.grupo7.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
public class ResumenCajaDTO {
    private LocalDate fecha;
    private long cantidadPedidos;
    private BigDecimal total;
    private boolean cerrada;
}