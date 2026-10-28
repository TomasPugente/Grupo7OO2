package com.example.unla.grupo7.services.implementation;

import java.math.BigDecimal;
import com.example.unla.grupo7.dtos.CanonDTO;
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

        @Override
    public BigDecimal calcularCanon(UnidadDeVenta unidad) {
        return detalleCanon(unidad).getTotal();
    }

    /**
     * Food Truck: superficie * costoPorSuperficie + plusPorElectricidad (si requiere electricidad).
     * Puesto:     superficie * costoPorSuperficie - tiempoMontaje * costoPorMontaje (nunca menor a 0).
     * Los valores ($500, $2000, $10) salen de la tabla de costos del festival, no del código.
     */
    @Override
    public CanonDTO detalleCanon(UnidadDeVenta unidad) {
        Costos costos = costosRepository
                .findByFestival_IdFestival(unidad.getFestival().getIdFestival())
                .orElseThrow(() -> new IllegalStateException("El festival no tiene costos cargados"));

        CanonDTO detalle = new CanonDTO();
        detalle.setNombreUnidad(unidad.getNombreComercial());
        detalle.setCodigo(unidad.getCodigo());
        detalle.setNombreFestival(unidad.getFestival().getNombre());
        detalle.setSuperficie(BigDecimal.valueOf(unidad.getSuperficie()));
        detalle.setCostoPorSuperficie(costos.getCostoPorSuperficie());

        BigDecimal subtotal = costoPorSuperficie(unidad, costos);
        detalle.setSubtotalSuperficie(subtotal);

        BigDecimal total = subtotal;

        if (unidad instanceof FoodTruck foodTruck) {
            detalle.setTipoUnidad("Food Truck");
            detalle.setRequiereElectricidad(foodTruck.isRequiereElectricidad());
            if (foodTruck.isRequiereElectricidad()) {
                detalle.setPlusElectricidad(costos.getPlusPorElectricidad());
                total = total.add(costos.getPlusPorElectricidad());
            }
        } else if (unidad instanceof PuestoDesarmable puesto) {
            detalle.setTipoUnidad("Puesto Desarmable");
            detalle.setTiempoMontaje(puesto.getTiempoMontaje());
            detalle.setCostoPorMontaje(costos.getCostoPorMontaje());
            BigDecimal descuento = BigDecimal.valueOf(puesto.getTiempoMontaje())
                    .multiply(costos.getCostoPorMontaje());
            detalle.setDescuentoMontaje(descuento);
            total = total.subtract(descuento).max(BigDecimal.ZERO);
        }

        detalle.setTotal(total);
        return detalle;
    }

    public BigDecimal costoPorSuperficie(UnidadDeVenta unidad, Costos costos) {
        return BigDecimal.valueOf(unidad.getSuperficie())
                .multiply(costos.getCostoPorSuperficie());
    }
}