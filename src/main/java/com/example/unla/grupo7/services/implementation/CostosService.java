package com.example.unla.grupo7.services.implementation;

import java.math.BigDecimal;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import com.example.unla.grupo7.dtos.CanonDTO;
import com.example.unla.grupo7.dtos.LiquidacionDTO;

import org.springframework.stereotype.Service;

import com.example.unla.grupo7.entities.Cajero;
import com.example.unla.grupo7.entities.Cocinero;
import com.example.unla.grupo7.entities.Costos;
import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.entities.FoodTruck;
import com.example.unla.grupo7.entities.PuestoDesarmable;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.repositories.ICostosRepository;
import com.example.unla.grupo7.services.ICostosService;

@Service 
public class CostosService implements ICostosService{
    
    private final ICostosRepository costosRepository;

    public CostosService(ICostosRepository costosRepository) {
        this.costosRepository = costosRepository;
    }

    private static final int DIAS_POR_TRAMO = 15;
    private static final BigDecimal MITAD = new BigDecimal("0.5");

    public LiquidacionDTO liquidarSueldo(Staff staff, Festival festival) {
        Costos costos = costosRepository
                .findByFestival_IdFestival(festival.getIdFestival())
                .orElseThrow(() -> new IllegalStateException("El festival no tiene costos cargados"));

        int dias = (int) ChronoUnit.DAYS.between(festival.getFechaInicio(), festival.getFechaFin()) + 1;

        int tramos = (dias + DIAS_POR_TRAMO - 1) / DIAS_POR_TRAMO;

        BigDecimal base = costos.getSueldoBase();
        BigDecimal adicional;
        String rol;
        int anios = 0;

        if (staff instanceof Cajero) {
            rol = "Cajero";
            anios = Math.max(0, Period.between(staff.getFechaIngreso(), festival.getFechaInicio()).getYears());
            adicional = costos.getMontoAnioAntiguedad().multiply(BigDecimal.valueOf(anios));
        } else if (staff instanceof Cocinero cocinero) {
            rol = "Cocina · " + cocinero.getEspecialidad();
            adicional = plusPorCategoria(cocinero, costos);
        } else {
            throw new IllegalArgumentException("Tipo de empleado sin liquidación definida");
        }

        BigDecimal total = base.add(adicional)
                .multiply(BigDecimal.valueOf(tramos))
                .multiply(MITAD);

        return new LiquidacionDTO(
                staff.getNombre() + " " + staff.getApellido(),
                rol, dias, anios, base, adicional, tramos * 50, total);
    }

    private BigDecimal plusPorCategoria(Cocinero cocinero, Costos costos) {
        return switch (cocinero.getEspecialidad()) {
            case "Cocinero"          -> costos.getPlusDeCocinero();
            case "Cocinero ayudante" -> costos.getPlusDeCocineroAyudante();
            case "Lavaplatos" -> costos.getPlusDeLavaPlatos();
            default -> BigDecimal.ZERO;
        };
    }

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