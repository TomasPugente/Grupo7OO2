package com.example.unla.grupo7.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data @EqualsAndHashCode(callSuper = false)
@Entity 
public class FoodTruck extends UnidadDeVenta{
    private String patente;

    private boolean usaElectricidad;
}
