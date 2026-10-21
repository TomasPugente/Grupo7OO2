package com.example.unla.grupo7.services.implementation;

import java.security.SecureRandom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.unla.grupo7.dtos.CredencialesGeneradasDTO;
import com.example.unla.grupo7.dtos.RegistroUnidadDTO;
import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.entities.FoodTruck;
import com.example.unla.grupo7.entities.PuestoDesarmable;
import com.example.unla.grupo7.entities.Responsable;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.entities.User;
import com.example.unla.grupo7.entities.UserRole;
import com.example.unla.grupo7.repositories.IFestivalRepository;
import com.example.unla.grupo7.repositories.IResponsableRepository;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.repositories.IUserRepository;
import com.example.unla.grupo7.services.IRegistroService;

@Service("registroService")
public class RegistroService implements IRegistroService {

    private static final String PASSWORD_ALPHABET =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%&*";
    private static final int PASSWORD_LENGTH = 12;
    private static final String ROL_RESPONSABLE = "RESPONSABLE";

    private final IUserRepository userRepository;
    private final IResponsableRepository responsableRepository;
    private final IUnidadDeVentaRepository unidadDeVentaRepository;
    private final IFestivalRepository festivalRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public RegistroService(IUserRepository userRepository,
                           IResponsableRepository responsableRepository,
                           IUnidadDeVentaRepository unidadDeVentaRepository,
                           IFestivalRepository festivalRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.responsableRepository = responsableRepository;
        this.unidadDeVentaRepository = unidadDeVentaRepository;
        this.festivalRepository = festivalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public CredencialesGeneradasDTO registrar(RegistroUnidadDTO dto) {
        if (userRepository.findByUsername(dto.getUsuario()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya está en uso.");
        }
        if (responsableRepository.existsByDni(dto.getDni())) {
            throw new IllegalArgumentException("Ya existe un responsable con ese DNI.");
        }
        if (unidadDeVentaRepository.existsByCodigo(dto.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una unidad con ese código.");
        }

        Festival festival = festivalRepository.findById(dto.getFestivalId())
                .orElseThrow(() -> new IllegalArgumentException("Festival inexistente."));

        String passwordEnClaro = generarPasswordAleatorio();

        User user = new User();
        user.setUsername(dto.getUsuario());
        user.setPassword(passwordEncoder.encode(passwordEnClaro));
        user.setEnabled(true);
        user = userRepository.save(user);

        UserRole rol = new UserRole();
        rol.setUser(user);
        rol.setRole(ROL_RESPONSABLE);
        user.getUserRoles().add(rol);

        UnidadDeVenta unidad = crearUnidad(dto, festival);
        unidad = unidadDeVentaRepository.save(unidad);

        Responsable responsable = new Responsable();
        responsable.setNombre(dto.getNombre());
        responsable.setApellido(dto.getApellido());
        responsable.setDni(dto.getDni());
        responsable.setFechaNacimiento(dto.getFechaNacimiento());
        responsable.setFechaIngreso(dto.getFechaIngreso());
        responsable.setUnidadDeVenta(unidad);
        responsable.setUser(user);
        responsableRepository.save(responsable);

        return new CredencialesGeneradasDTO(user.getUsername(), passwordEnClaro, unidad.getNombreComercial());
    }

    private UnidadDeVenta crearUnidad(RegistroUnidadDTO dto, Festival festival) {
        UnidadDeVenta unidad;
        switch (dto.getTipo()) {
            case "FOOD_TRUCK" -> {
                if (dto.getPatente() == null || dto.getPatente().isBlank()) {
                    throw new IllegalArgumentException("La patente es obligatoria para un Food Truck.");
                }
                if (dto.getRequiereElectricidad() == null) {
                    throw new IllegalArgumentException("Indicá si el Food Truck requiere electricidad.");
                }
                FoodTruck ft = new FoodTruck();
                ft.setPatente(dto.getPatente().trim());
                ft.setRequiereElectricidad(dto.getRequiereElectricidad());
                unidad = ft;
            }
            case "PUESTO" -> {
                if (dto.getCantidadCarpas() == null || dto.getCantidadCarpas() < 1) {
                    throw new IllegalArgumentException("Ingresá una cantidad de carpas válida para el puesto.");
                }
                if (dto.getTiempoMontaje() == null || dto.getTiempoMontaje() < 0) {
                    throw new IllegalArgumentException("Ingresá el tiempo de montaje del puesto.");
                }
                PuestoDesarmable puesto = new PuestoDesarmable();
                puesto.setCantidadCarpas(dto.getCantidadCarpas());
                puesto.setTiempoMontaje(dto.getTiempoMontaje());
                unidad = puesto;
            }
            default -> throw new IllegalArgumentException("Tipo de unidad inválido: " + dto.getTipo());
        }
        unidad.setNombreComercial(dto.getNombreComercial());
        unidad.setCodigo(dto.getCodigo());
        unidad.setSuperficie(dto.getSuperficie());
        unidad.setActivo(true);
        unidad.setFestival(festival);
        return unidad;
    }

    private String generarPasswordAleatorio() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_ALPHABET.charAt(random.nextInt(PASSWORD_ALPHABET.length())));
        }
        return sb.toString();
    }
}
