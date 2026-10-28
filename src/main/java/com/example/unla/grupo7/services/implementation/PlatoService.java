package com.example.unla.grupo7.services.implementation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.unla.grupo7.dtos.PlatoFiltroDTO;
import com.example.unla.grupo7.dtos.PlatoDTO;
import com.example.unla.grupo7.dtos.PlatoEstrellaDTO;
import com.example.unla.grupo7.entities.Plato;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.repositories.IDetallePedidoRepository;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.repositories.IplatoRepository;
import com.example.unla.grupo7.services.IPlatoService;

@Service("platoService")
public class PlatoService implements IPlatoService {
        // Límites que se usan cuando un rango no se completó (equivale a "sin límite")
    private static final BigDecimal MINIMO = BigDecimal.ZERO;
    private static final BigDecimal MAXIMO = new BigDecimal("9999999999.99");
    private final IplatoRepository platoRepository;
    private final IUnidadDeVentaRepository unidadDeVentaRepository;
    private final IDetallePedidoRepository detallePedidoRepository;
    public PlatoService(IplatoRepository platoRepository,
                        IUnidadDeVentaRepository unidadDeVentaRepository, IDetallePedidoRepository detallePedidoRepository) {
        this.platoRepository = platoRepository;
        this.unidadDeVentaRepository = unidadDeVentaRepository;
        this.detallePedidoRepository=detallePedidoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlatoDTO> listar(String username) {
        UnidadDeVenta unidad = unidadDelUsuario(username);
        return platoRepository
                .findByUnidadDeVenta_IdUnidadDeVentaAndActivoTrueOrderByNombreAsc(unidad.getIdUnidadDeVenta())
                .stream()
                .map(this::toDTO)
                .toList();
    }

        @Override
    @Transactional(readOnly = true)
    public List<PlatoDTO> filtrar(PlatoFiltroDTO filtro, String username) {
        validarRango("precio", filtro.getPrecioMin(), filtro.getPrecioMax());
        validarRango("costo", filtro.getCostoMin(), filtro.getCostoMax());

        UnidadDeVenta unidad = unidadDelUsuario(username);
        String nombre = filtro.getNombre() == null ? "" : filtro.getNombre().trim();

        return platoRepository.filtrar(
                        unidad.getIdUnidadDeVenta(),
                        nombre,
                        filtro.getPrecioMin() != null ? filtro.getPrecioMin() : MINIMO,
                        filtro.getPrecioMax() != null ? filtro.getPrecioMax() : MAXIMO,
                        filtro.getCostoMin() != null ? filtro.getCostoMin() : MINIMO,
                        filtro.getCostoMax() != null ? filtro.getCostoMax() : MAXIMO)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlatoDTO obtener(Long id, String username) {
        return toDTO(platoVigenteDeLaUnidad(id, unidadDelUsuario(username)));
    }

    @Override
    @Transactional
    public PlatoDTO crear(PlatoDTO dto, String username) {
        validarPrecios(dto);
        UnidadDeVenta unidad = unidadDelUsuario(username);

        List<Plato> mismoNombre = platoRepository
                .findByUnidadDeVenta_IdUnidadDeVentaAndNombreIgnoreCase(
                        unidad.getIdUnidadDeVenta(), dto.getNombre().trim());

        if (mismoNombre.stream().anyMatch(Plato::isActivo)) {
            throw new IllegalArgumentException("Ya existe un plato con ese nombre en la unidad.");
        }

        // Método de actualización específico: si el plato fue eliminado, se reactiva
        Optional<Plato> eliminado = mismoNombre.stream().findFirst();
        Plato plato = eliminado.orElseGet(Plato::new);
        plato.setNombre(dto.getNombre().trim());
        plato.setPrecioVenta(dto.getPrecioVenta());
        plato.setCostoProduccion(dto.getCostoProduccion());
        plato.setUnidadDeVenta(unidad);
        plato.setActivo(true);
        return toDTO(platoRepository.save(plato));
    }

    @Override
    @Transactional
    public PlatoDTO modificar(Long id, PlatoDTO dto, String username) {
        validarPrecios(dto);
        UnidadDeVenta unidad = unidadDelUsuario(username);
        Plato plato = platoVigenteDeLaUnidad(id, unidad);

        boolean nombreEnUso = platoRepository
                .findByUnidadDeVenta_IdUnidadDeVentaAndNombreIgnoreCase(
                        unidad.getIdUnidadDeVenta(), dto.getNombre().trim())
                .stream()
                .anyMatch(p -> p.isActivo() && !p.getId().equals(plato.getId()));
        if (nombreEnUso) {
            throw new IllegalArgumentException("Ya existe un plato con ese nombre en la unidad.");
        }

        plato.setNombre(dto.getNombre().trim());
        plato.setPrecioVenta(dto.getPrecioVenta());
        plato.setCostoProduccion(dto.getCostoProduccion());
        return toDTO(platoRepository.save(plato));
    }

    @Override
    @Transactional
    public void eliminar(Long id, String username) {
        Plato plato = platoVigenteDeLaUnidad(id, unidadDelUsuario(username));
        plato.setActivo(false);
        platoRepository.save(plato);
    }

        @Override
    @Transactional(readOnly = true)
    public List<PlatoDTO> listarInactivos(String username) {
        UnidadDeVenta unidad = unidadDelUsuario(username);

    return platoRepository
                .findByUnidadDeVenta_IdUnidadDeVentaAndActivoFalseOrderByNombreAsc(
                        unidad.getIdUnidadDeVenta())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ---------- helpers ----------

    /** Regla de negocio: no se puede vender un plato por debajo de su costo de producción. */
    private void validarPrecios(PlatoDTO dto) {
        BigDecimal precio = dto.getPrecioVenta();
        BigDecimal costo = dto.getCostoProduccion();
        if (precio != null && costo != null && precio.compareTo(costo) < 0) {
            throw new IllegalArgumentException(
                    "El precio de venta no puede ser menor al costo de producción.");
        }
    }

        private void validarRango(String atributo, BigDecimal min, BigDecimal max) {
        if (min != null && max != null && min.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                    "El " + atributo + " mínimo no puede ser mayor al " + atributo + " máximo.");
        }
    }

    /** Unidad del usuario autenticado: la que tiene a cargo (responsable) o donde trabaja (empleado). */
    private UnidadDeVenta unidadDelUsuario(String username) {
        return unidadDeVentaRepository.findByResponsableUsername(username)
                .or(() -> unidadDeVentaRepository.findByEmpleadoActivoEmail(username))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "El usuario no tiene una unidad de venta asignada."));
    }

    private Plato platoVigenteDeLaUnidad(Long id, UnidadDeVenta unidad) {
        Plato plato = platoRepository.findById(id)
                .filter(Plato::isActivo)
                .filter(p -> p.getUnidadDeVenta() != null
                        && p.getUnidadDeVenta().getIdUnidadDeVenta() == unidad.getIdUnidadDeVenta())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontró el plato."));
        return plato;
    }

    private PlatoDTO toDTO(Plato p) {
        return new PlatoDTO(p.getId(), p.getNombre(), p.getPrecioVenta(), p.getCostoProduccion());
    }

    
    public void reactivar(Long id, String username) {
        UnidadDeVenta unidad = unidadDelUsuario(username);

        Plato plato = platoRepository
                .findByIdAndUnidadDeVenta_IdUnidadDeVenta(
                        id, unidad.getIdUnidadDeVenta())
                .filter(p -> !p.isActivo())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el plato eliminado."));

        plato.setActivo(true);
        platoRepository.save(plato);
    }

    @Override
    @Transactional(readOnly = true)
    public PlatoEstrellaDTO obtenerPlatoEstrella(String username) {
        UnidadDeVenta unidad = unidadDelUsuario(username);

        List<Object[]> resultado = detallePedidoRepository.findPlatosMasVendidos(
                unidad.getIdUnidadDeVenta(), PageRequest.of(0, 1));

        Object[] fila = resultado.get(0);
        Plato plato = (Plato) fila[0];
        long cantidad = ((Number) fila[1]).longValue();

        return new PlatoEstrellaDTO(toDTO(plato), cantidad);
    }
}