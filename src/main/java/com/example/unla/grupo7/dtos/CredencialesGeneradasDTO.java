package com.example.unla.grupo7.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CredencialesGeneradasDTO {

    private String usuario;

    private String passwordEnClaro;

    private String nombreComercial;
}
