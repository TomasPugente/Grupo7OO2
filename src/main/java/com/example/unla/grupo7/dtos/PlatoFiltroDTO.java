package com.example.unla.grupo7.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Criterios opcionales para filtrar platos. Los que vengan vacíos no se aplican. */
@Data
@NoArgsConstructor
public class PlatoFiltroDTO {

    private String nombre;

    @DecimalMin(value = "0.00", message = "El precio mínimo no puede ser negativo.")
    private BigDecimal precioMin;

    @DecimalMin(value = "0.00", message = "El precio máximo no puede ser negativo.")
    private BigDecimal precioMax;

    @DecimalMin(value = "0.00", message = "El costo mínimo no puede ser negativo.")
    private BigDecimal costoMin;

    @DecimalMin(value = "0.00", message = "El costo máximo no puede ser negativo.")
    private BigDecimal costoMax;

    /** true si se cargó al menos un criterio. */
    public boolean hayFiltros() {
        return (nombre != null && !nombre.isBlank())
                || precioMin != null || precioMax != null
                || costoMin != null || costoMax != null;
    }
}