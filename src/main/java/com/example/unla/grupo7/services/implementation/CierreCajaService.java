package com.example.unla.grupo7.services.implementation;

import java.time.LocalDate;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import com.example.unla.grupo7.dtos.ResumenCajaDTO;
import com.example.unla.grupo7.entities.Cajero;
import com.example.unla.grupo7.entities.CierreCaja;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.repositories.ICierreCajaRepository;
import com.example.unla.grupo7.repositories.IPedidoRepository;
import com.example.unla.grupo7.services.ICierreCajaService;

@Service 
public class CierreCajaService implements ICierreCajaService {

    private final ICierreCajaRepository cierreRepository;
    private final IPedidoRepository pedidoRepository;

    public CierreCajaService(ICierreCajaRepository cierreRepository, IPedidoRepository pedidoRepository){
        this.cierreRepository= cierreRepository;
        this.pedidoRepository= pedidoRepository;
    }


    public ResumenCajaDTO resumenDelDia(UnidadDeVenta unidad) {
        LocalDate hoy = LocalDate.now();
        Long id = unidad.getIdUnidadDeVenta();
        return new ResumenCajaDTO(
                hoy,
                pedidoRepository.countByUnidadDeVenta_IdUnidadDeVentaAndFechaTransaccion(id, hoy),
                pedidoRepository.totalDelDia(id, hoy),
                cierreRepository.existsByUnidadDeVenta_IdUnidadDeVentaAndFecha(id, hoy));
    }

    public CierreCaja cerrar(Staff staff) {
        Staff real = (Staff) Hibernate.unproxy(staff);
        if (!(real instanceof Cajero)) {
            throw new IllegalArgumentException("Solo un cajero puede cerrar la caja.");
        }

        UnidadDeVenta unidad = real.getUnidadDeVenta();
        LocalDate hoy = LocalDate.now();

        if (cierreRepository.existsByUnidadDeVenta_IdUnidadDeVentaAndFecha(unidad.getIdUnidadDeVenta(), hoy)) {
            throw new IllegalArgumentException("La caja de hoy ya fue cerrada.");
        }

        CierreCaja cierre = new CierreCaja();
        cierre.setFecha(hoy);
        cierre.setUnidadDeVenta(unidad);
        cierre.setCajero(real);
        cierre.setCantidadPedidos((int) pedidoRepository
                .countByUnidadDeVenta_IdUnidadDeVentaAndFechaTransaccion(unidad.getIdUnidadDeVenta(), hoy));
        cierre.setTotalVentas(pedidoRepository.totalDelDia(unidad.getIdUnidadDeVenta(), hoy));

        return cierreRepository.save(cierre);
    }
}