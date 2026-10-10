package com.example.unla.grupo7.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode (callSuper = false)
@Entity 
public class PuestoDesarmable extends UnidadDeVenta{
    private int cantidadCarpas;
    
    private int tiempoMontaje;
}
