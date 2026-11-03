package com.example.unla.grupo7.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO no persistente: información personal del empleado + festivales/unidades en los que trabajó.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoEmpleadoDTO {
    private String nombre;
    private String apellido;
    private int dni;
    private String email;
    private LocalDate fechaIngreso;
    private String puesto;
    private boolean activo;
    private List<ParticipacionFestivalDTO> participaciones;
}
