package com.example.unla.grupo7.services;

import java.util.List;

import com.example.unla.grupo7.dtos.PedidoFormDTO;
import com.example.unla.grupo7.entities.Pedido;
import com.example.unla.grupo7.entities.UnidadDeVenta;

public interface IPedidoService {
    Pedido crear(PedidoFormDTO form, UnidadDeVenta unidad);

    List<Pedido> traerPorUsuario(String username);

}
