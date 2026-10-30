package com.example.unla.grupo7.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
public class LiquidacionDTO {
    private String empleado;
    private String rol;
    private int diasFestival;
    private int antiguedadAnios;
    private BigDecimal sueldoBase;
    private BigDecimal adicional;     
    private int porcentaje;          
    private BigDecimal total;
}