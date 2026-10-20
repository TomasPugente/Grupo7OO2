package com.example.unla.grupo7.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatoDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    private String nombre;

    @NotNull(message = "El precio de venta es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a 0.")
    @Digits(integer = 10, fraction = 2, message = "El precio de venta admite hasta 2 decimales.")
    private BigDecimal precioVenta;

    @NotNull(message = "El costo de producción es obligatorio.")
    @DecimalMin(value = "0.00", message = "El costo de producción no puede ser negativo.")
    @Digits(integer = 10, fraction = 2, message = "El costo de producción admite hasta 2 decimales.")
    private BigDecimal costoProduccion;
}