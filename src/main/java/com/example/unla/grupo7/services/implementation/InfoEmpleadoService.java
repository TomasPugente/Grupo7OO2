package com.example.unla.grupo7.services.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.unla.grupo7.dtos.InfoEmpleadoDTO;
import com.example.unla.grupo7.dtos.ParticipacionFestivalDTO;
import com.example.unla.grupo7.entities.Cajero;
import com.example.unla.grupo7.entities.Cocinero;
import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.entities.FoodTruck;
import com.example.unla.grupo7.entities.PuestoDesarmable;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.repositories.IStaffRepository;
import com.example.unla.grupo7.services.IInfoEmpleadoService;

@Service
public class InfoEmpleadoService implements IInfoEmpleadoService {

    private static final String PUESTO_CAJERO = "Cajero";
    private static final String PUESTO_COCINERO = "Cocinero";
    private static final String PUESTO_GENERICO = "Empleado";
    private static final String TIPO_FOOD_TRUCK = "Food Truck";
    private static final String TIPO_PUESTO = "Puesto Desarmable";

    private final IStaffRepository staffRepository;

    public InfoEmpleadoService(IStaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public InfoEmpleadoDTO obtenerPorUsername(String username) {
        Staff staff = staffRepository.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró un empleado para el usuario " + username));

        return new InfoEmpleadoDTO(
                staff.getNombre(),
                staff.getApellido(),
                staff.getDni(),
                staff.getEmail(),
                staff.getFechaIngreso(),
                puesto(staff),
                staff.isActivo(),
                participaciones(staff));
    }

    // El modelo guarda una única unidad por empleado (la actual), por lo que la
    // lista tiene a lo sumo un elemento. Si más adelante se agrega historial de
    // asignaciones, solo hay que completar esta lista.
    private List<ParticipacionFestivalDTO> participaciones(Staff staff) {
        List<ParticipacionFestivalDTO> lista = new ArrayList<>();
        UnidadDeVenta unidad = staff.getUnidadDeVenta();
        if (unidad == null) {
            return lista;
        }
        Festival festival = unidad.getFestival();
        lista.add(new ParticipacionFestivalDTO(
                festival != null ? festival.getNombre() : null,
                festival != null ? festival.getTemporada() : null,
                festival != null ? festival.getFechaInicio() : null,
                festival != null ? festival.getFechaFin() : null,
                festival != null && festival.isActivo(),
                unidad.getNombreComercial(),
                unidad.getCodigo(),
                tipo(unidad)));
        return lista;
    }

    private String puesto(Staff staff) {
        if (staff instanceof Cajero) {
            return PUESTO_CAJERO;
        }
        if (staff instanceof Cocinero) {
            return PUESTO_COCINERO;
        }
        return PUESTO_GENERICO;
    }

    private String tipo(UnidadDeVenta unidad) {
        if (unidad instanceof FoodTruck) {
            return TIPO_FOOD_TRUCK;
        }
        if (unidad instanceof PuestoDesarmable) {
            return TIPO_PUESTO;
        }
        return "";
    }
}
