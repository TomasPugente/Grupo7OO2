package com.example.unla.grupo7.services.implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.unla.grupo7.dtos.ItemPedidoDTO;
import com.example.unla.grupo7.dtos.PedidoFormDTO;
import com.example.unla.grupo7.entities.DetallePedido;
import com.example.unla.grupo7.entities.Pedido;
import com.example.unla.grupo7.entities.Plato;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.repositories.IDetallePedidoRepository;
import com.example.unla.grupo7.repositories.IPedidoRepository;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.repositories.IplatoRepository;
import com.example.unla.grupo7.services.IPedidoService;

@Service 
public class PedidoService implements IPedidoService {

    private final IPedidoRepository pedidoRepository;
    private final IDetallePedidoRepository detalleRepository;
    private final IplatoRepository platoRepository;
    private final IUnidadDeVentaRepository unidadDeVentaRepository;

    public PedidoService(IPedidoRepository pedidoRepository, IDetallePedidoRepository detallesRepository, IplatoRepository platoRepository, IUnidadDeVentaRepository unidadDeVentaRepository){
        this.pedidoRepository= pedidoRepository;
        this.detalleRepository= detallesRepository;
        this.platoRepository= platoRepository;
        this.unidadDeVentaRepository= unidadDeVentaRepository;
    }

    
    public List<Pedido> traerPorUsuario(String username){
        UnidadDeVenta unidad = unidadDelUsuario(username);
        return pedidoRepository.findByUnidadDeVenta_IdUnidadDeVenta(unidad.getIdUnidadDeVenta());
    }

    public List<Pedido> traerUltimos5Pedidos(String username){
        UnidadDeVenta unidad = unidadDelUsuario(username);
            return pedidoRepository.findFirst5ByUnidadDeVenta_IdUnidadDeVentaOrderByFechaTransaccion(unidad.getIdUnidadDeVenta());
    }

    public Pedido crear(PedidoFormDTO form, UnidadDeVenta unidad) {

        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (ItemPedidoDTO item : form.getItems()) {
            if (item.getPlatoId() != null && item.getCantidad() > 0) {
                cantidades.merge(item.getPlatoId(), item.getCantidad(), Integer::sum);
            }
        }
        if (cantidades.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un plato con cantidad mayor a 0.");
        }


        Map<Long, Plato> validos = platoRepository
                .findByUnidadDeVenta_IdUnidadDeVentaAndActivoTrue(unidad.getIdUnidadDeVenta())
                .stream()
                .collect(Collectors.toMap(Plato::getId, p -> p));

        Pedido pedido = new Pedido();
        pedido.setFechaTransaccion(LocalDate.now()); 
        pedido.setUnidadDeVenta(unidad);

        BigDecimal total = BigDecimal.ZERO;
        List<DetallePedido> detalles = new ArrayList<>();

        for (Map.Entry<Long, Integer> e : cantidades.entrySet()) {
            Plato plato = validos.get(e.getKey());
            if (plato == null) {
                throw new IllegalArgumentException("Uno de los platos no existe o no pertenece a tu unidad.");
            }
            DetallePedido d = new DetallePedido();
            d.setPedido(pedido);
            d.setPlato(plato);
            d.setCantidad(e.getValue());
            detalles.add(d);

            total = total.add(plato.getPrecioVenta().multiply(BigDecimal.valueOf(e.getValue())));
        }
        pedido.setEstado("En preparación");
        pedido.setPrecioTotal(total);

        pedidoRepository.save(pedido);
        detalleRepository.saveAll(detalles);
        return pedido;
    }

        private UnidadDeVenta unidadDelUsuario(String username) {
        return unidadDeVentaRepository.findByResponsableUsername(username)
                .or(() -> unidadDeVentaRepository.findByEmpleadoActivoEmail(username))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "El usuario no tiene una unidad de venta asignada."));
    }
}