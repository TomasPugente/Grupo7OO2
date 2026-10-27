package com.example.unla.grupo7.services.implementation;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.unla.grupo7.entities.Costos;
import com.example.unla.grupo7.entities.FoodTruck;
import com.example.unla.grupo7.entities.PuestoDesarmable;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.repositories.ICostosRepository;
import com.example.unla.grupo7.services.ICostosService;

@Service 
public class CostosService implements ICostosService{
    
    private final ICostosRepository costosRepository;

    public CostosService(ICostosRepository costosRepository) {
        this.costosRepository = costosRepository;
    }

    public BigDecimal calcularCanon(UnidadDeVenta unidad) {
        Costos costos = costosRepository
                .findByFestival_IdFestival(unidad.getFestival().getIdFestival())
                .orElseThrow(() -> new IllegalStateException("El festival no tiene costos cargados"));

        BigDecimal canon = costoPorSuperficie(unidad, costos);

        if (unidad instanceof FoodTruck foodTruck) {
            if (foodTruck.isRequiereElectricidad()) {
                canon = canon.add(costos.getPlusPorElectricidad());
            }
        } else if (unidad instanceof PuestoDesarmable puesto) {
            BigDecimal descuento = BigDecimal.valueOf(puesto.getTiempoMontaje())
                    .multiply(costos.getCostoPorMontaje());
            canon = canon.subtract(descuento).max(BigDecimal.ZERO);
        }

        return canon;
    }

    public BigDecimal costoPorSuperficie(UnidadDeVenta unidad, Costos costos) {
        return BigDecimal.valueOf(unidad.getSuperficie())
                .multiply(costos.getCostoPorSuperficie());
    }
}
