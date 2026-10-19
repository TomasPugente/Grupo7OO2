package com.example.unla.grupo7.services.implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.unla.grupo7.dtos.ReporteMayoresCanonDTO;
import com.example.unla.grupo7.entities.FoodTruck;
import com.example.unla.grupo7.entities.PuestoDesarmable;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;

@Service
public class UnidadDeVentaService {

    private final IUnidadDeVentaRepository unidadDeVentaRepository;

    public UnidadDeVentaService(
            IUnidadDeVentaRepository unidadDeVentaRepository) {
        this.unidadDeVentaRepository = unidadDeVentaRepository;
    }

    // Calcula el canon que debe pagar una unidad de venta
    public double calcularCanon(UnidadDeVenta unidad) {

        double canon = unidad.getSuperficie() * 500;

        if (unidad instanceof FoodTruck) {

            FoodTruck foodTruck = (FoodTruck) unidad;

            if (foodTruck.isUsaElectricidad()) {
                canon += 2000;
            }

        } else if (unidad instanceof PuestoDesarmable) {

            PuestoDesarmable puesto = (PuestoDesarmable) unidad;

            canon -= puesto.getTiempoMontaje() * 10;
        }

        return canon;
    }

    // Devuelve las X unidades con mayor canon de un festival
    public List<ReporteMayoresCanonDTO> obtenerMayoresCanon(
            Long idFestival, int x) {

        if (x <= 0) {
            return List.of();
        }

        List<UnidadDeVenta> unidades =
                unidadDeVentaRepository
                        .findByFestival_IdFestival(idFestival);

        return unidades.stream()
                .map(unidad -> {

                    String tipoUnidad;

                    if (unidad instanceof FoodTruck) {
                        tipoUnidad = "Food Truck";

                    } else if (unidad instanceof PuestoDesarmable) {
                        tipoUnidad = "Puesto Desarmable";

                    } else {
                        tipoUnidad = "Otro";
                    }

                    double canon = calcularCanon(unidad);

                    return new ReporteMayoresCanonDTO(
                            unidad.getNombreComercial(),
                            unidad.getCodigo(),
                            tipoUnidad,
                            canon
                    );
                })
                .sorted((a, b) ->
                        Double.compare(b.getCanon(), a.getCanon()))
                .limit(x)
                .collect(Collectors.toList());
    }
}
