package com.example.unla.grupo7.services;

import com.example.unla.grupo7.dtos.CredencialesGeneradasDTO;
import com.example.unla.grupo7.dtos.RegistroUnidadDTO;

public interface IRegistroService {

    CredencialesGeneradasDTO registrar(RegistroUnidadDTO dto);
}
