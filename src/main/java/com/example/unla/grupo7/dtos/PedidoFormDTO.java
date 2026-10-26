package com.example.unla.grupo7.dtos;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class PedidoFormDTO {
    @Valid 
    private List<ItemPedidoDTO> items = new ArrayList<>();
}
