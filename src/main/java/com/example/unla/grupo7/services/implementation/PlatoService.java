package com.example.unla.grupo7.services.implementation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.unla.grupo7.dtos.PlatoDTO;
import com.example.unla.grupo7.entities.Plato;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.repositories.IplatoRepository;
import com.example.unla.grupo7.services.IPlatoService;

@Service("platoService")
public class PlatoService implements IPlatoService {

    private final IplatoRepository platoRepository;
    private final IUnidadDeVentaRepository unidadDeVentaRepository;

    public PlatoService(IplatoRepository platoRepository,
                        IUnidadDeVentaRepository unidadDeVentaRepository) {
        this.platoRepository = platoRepository;
        this.unidadDeVentaRepository = unidadDeVentaRepository;
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
}