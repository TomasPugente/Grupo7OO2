package com.example.unla.grupo7.services;


import com.example.unla.grupo7.entities.UnidadDeVenta;

public interface IUnidadDeVentaService {
    public UnidadDeVenta buscarUnidadPorResponsableUsername(String username);

    public UnidadDeVenta buscarUnidadPorStaffUsername(String username);

    double calcularCanon(UnidadDeVenta unidad);
}
