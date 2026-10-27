package com.example.unla.grupo7.services.implementation;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.unla.grupo7.dtos.RentabilidadDTO;
import com.example.unla.grupo7.entities.DetallePedido;
import com.example.unla.grupo7.entities.Pedido;
import com.example.unla.grupo7.entities.Plato;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.repositories.IDetallePedidoRepository;
import com.example.unla.grupo7.repositories.IPedidoRepository;
import com.example.unla.grupo7.repositories.IStaffRepository;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.services.IRentabilidadService;
import com.example.unla.grupo7.services.IUnidadDeVentaService;

@Service
public class RentabilidadService implements IRentabilidadService {

    private final IPedidoRepository pedidoRepository;
    private final IDetallePedidoRepository detallePedidoRepository;
    private final IStaffRepository staffRepository;
    private final IUnidadDeVentaRepository unidadRepository;
    private final IUnidadDeVentaService unidadService;

    public RentabilidadService(IPedidoRepository pedidoRepository,
                               IDetallePedidoRepository detallePedidoRepository,
                               IStaffRepository staffRepository,
                               IUnidadDeVentaRepository unidadRepository,
                               IUnidadDeVentaService unidadService) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.staffRepository = staffRepository;
        this.unidadRepository = unidadRepository;
        this.unidadService = unidadService;
    }

    @Override
    @Transactional(readOnly = true)
    public RentabilidadDTO calcularPorUnidad(long idUnidadDeVenta) {

        UnidadDeVenta unidad = unidadRepository.findById(idUnidadDeVenta)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la unidad de venta con id " + idUnidadDeVenta));

        List<Pedido> pedidos =
                pedidoRepository.findByUnidadDeVenta_IdUnidadDeVenta(idUnidadDeVenta);

        BigDecimal pedidosTotales = pedidos.stream()
                .map(this::precioTotalSeguro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal costoPlatos = detallePedidoRepository
                .findByPedido_UnidadDeVenta_IdUnidadDeVenta(idUnidadDeVenta)
                .stream()
                .map(this::costoDetalle)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sueldos = staffRepository
                .findByUnidadDeVenta_IdUnidadDeVentaAndActivoTrue(idUnidadDeVenta)
                .stream()
                .map(this::sueldoSeguro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal canon = BigDecimal.valueOf(unidadService.calcularCanon(unidad));

        BigDecimal rentabilidadNeta = pedidosTotales
                .subtract(costoPlatos)
                .subtract(sueldos)
                .subtract(canon);

        String nombreFestival = unidad.getFestival() != null
                ? unidad.getFestival().getNombre()
                : "—";

        return new RentabilidadDTO(
                unidad.getNombreComercial(),
                nombreFestival,
                pedidosTotales,
                costoPlatos,
                sueldos,
                canon,
                rentabilidadNeta);
    }

    private BigDecimal precioTotalSeguro(Pedido p) {
        return p.getPrecioTotal() != null ? p.getPrecioTotal() : BigDecimal.ZERO;
    }

    private BigDecimal sueldoSeguro(Staff s) {
        return s.getSueldo() != null ? s.getSueldo() : BigDecimal.ZERO;
    }

    private BigDecimal costoDetalle(DetallePedido dp) {
        Plato plato = dp.getPlato();
        if (plato == null || plato.getCostoProduccion() == null) {
            return BigDecimal.ZERO;
        }
        return plato.getCostoProduccion()
                .multiply(BigDecimal.valueOf(dp.getCantidad()));
    }
}
