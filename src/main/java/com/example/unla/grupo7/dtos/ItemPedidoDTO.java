package com.example.unla.grupo7.dtos;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data 
public class ItemPedidoDTO {

    private Long platoId;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad = 0;

}