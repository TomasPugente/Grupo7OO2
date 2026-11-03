package com.example.unla.grupo7.services;

import java.util.List;

import com.example.unla.grupo7.dtos.ResumenCajaDTO;
import com.example.unla.grupo7.entities.CierreCaja;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.entities.Pedido;

public interface ICierreCajaService {
 
    public ResumenCajaDTO resumenDelDia(UnidadDeVenta unidad);

    public CierreCaja cerrar(Staff staff);

    public List<Pedido> pedidosDelDia(UnidadDeVenta unidad);
    public CierreCaja cierreDeHoy(UnidadDeVenta unidad);  
}
