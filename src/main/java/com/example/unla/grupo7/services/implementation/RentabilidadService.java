package com.example.unla.grupo7.services.implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
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

        UnidadDeVenta unidad = cargarUnidad(idUnidadDeVenta);

        List<Pedido> pedidos =
                pedidoRepository.findByUnidadDeVenta_IdUnidadDeVenta(idUnidadDeVenta);

        List<DetallePedido> detalles = detallePedidoRepository
                .findByPedido_UnidadDeVenta_IdUnidadDeVenta(idUnidadDeVenta);

        return armarDTO(unidad, pedidos, detalles, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public RentabilidadDTO calcularPorUnidadEntreFechas(long idUnidadDeVenta,
                                                        LocalDate desde,
                                                        LocalDate hasta) {

        if (desde == null || hasta == null) {
            throw new IllegalArgumentException(
                    "Las fechas 'desde' y 'hasta' son obligatorias.");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "La fecha 'desde' no puede ser posterior a 'hasta'.");
        }

        UnidadDeVenta unidad = cargarUnidad(idUnidadDeVenta);

        List<Pedido> pedidos = pedidoRepository
                .findByUnidadDeVenta_IdUnidadDeVentaAndFechaTransaccionBetween(
                        idUnidadDeVenta, desde, hasta);

        List<DetallePedido> detalles = detallePedidoRepository
                .findByPedido_UnidadDeVenta_IdUnidadDeVentaAndPedido_FechaTransaccionBetween(
                        idUnidadDeVenta, desde, hasta);

        return armarDTO(unidad, pedidos, detalles, desde, hasta);
    }

    private UnidadDeVenta cargarUnidad(long id) {
        return unidadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la unidad de venta con id " + id));
    }

    private RentabilidadDTO armarDTO(UnidadDeVenta unidad,
                                     List<Pedido> pedidos,
                                     List<DetallePedido> detalles,
                                     LocalDate desde,
                                     LocalDate hasta) {

        BigDecimal pedidosTotales = pedidos.stream()
                .map(this::precioTotalSeguro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal costoPlatos = detalles.stream()
                .map(this::costoDetalle)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sueldos = staffRepository
                .findByUnidadDeVenta_IdUnidadDeVentaAndActivoTrue(unidad.getIdUnidadDeVenta())
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
                rentabilidadNeta,
                desde,
                hasta);
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
