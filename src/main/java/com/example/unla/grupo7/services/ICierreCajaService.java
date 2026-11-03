package com.example.unla.grupo7.services;

import com.example.unla.grupo7.dtos.ResumenCajaDTO;
import com.example.unla.grupo7.entities.CierreCaja;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;

public interface ICierreCajaService {
 
     public ResumenCajaDTO resumenDelDia(UnidadDeVenta unidad);

         public CierreCaja cerrar(Staff staff);
}
