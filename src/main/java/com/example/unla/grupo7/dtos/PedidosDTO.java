package com.example.unla.grupo7.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.example.unla.grupo7.entities.DetallePedido;
import com.example.unla.grupo7.entities.UnidadDeVenta;

import lombok.Data;

@Data  
public class PedidosDTO {
    private Long idPedido;

    private LocalDate fechaTransaccion;

    private Set<DetallePedido> detallePedidos = new HashSet<>();

    private BigDecimal precioTotal;

    private UnidadDeVenta unidadDeVenta;
}
