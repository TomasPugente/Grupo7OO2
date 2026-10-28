package com.example.unla.grupo7.services.implementation;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.unla.grupo7.dtos.ReporteVentaDTO;
import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.repositories.IFestivalRepository;
import com.example.unla.grupo7.repositories.IPedidoRepository;
import com.example.unla.grupo7.services.IReporteVentaService;

@Service
public class ReporteVentaService implements IReporteVentaService {

    private final IPedidoRepository pedidoRepository;
    private final IFestivalRepository festivalRepository;

    public ReporteVentaService(IPedidoRepository pedidoRepository,
                               IFestivalRepository festivalRepository) {
        this.pedidoRepository = pedidoRepository;
        this.festivalRepository = festivalRepository;
    }

    @Override
    public ReporteVentaDTO recaudacionPorFestival(long idFestival) {
        Festival festival = festivalRepository.findById(idFestival)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el festival con id " + idFestival));

        BigDecimal total = pedidoRepository.sumarPrecioTotalPorFestival(idFestival);
        long cantidad = pedidoRepository.contarPedidosPorFestival(idFestival);

        return new ReporteVentaDTO(
                festival.getIdFestival(), festival.getNombre(), festival.getTemporada(),
                festival.getFechaInicio(), festival.getFechaFin(),
                cantidad, total == null ? BigDecimal.ZERO : total);
    }
}
