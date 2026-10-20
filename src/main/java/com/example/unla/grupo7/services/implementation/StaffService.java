package com.example.unla.grupo7.services.implementation;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.unla.grupo7.dtos.StaffFormDTO;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.entities.User;
import com.example.unla.grupo7.entities.UserRole;
import com.example.unla.grupo7.repositories.IStaffRepository;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.repositories.IUserRepository;
import com.example.unla.grupo7.services.IStaffService;

@Service
public class StaffService implements IStaffService {

    private static final String ROL_EMPLEADO = "EMPLEADO";

    private static final String PASSWORD_ALPHABET =
        "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%&*";

    private static final int PASSWORD_LENGTH = 12;

    private final IStaffRepository staffRepository;
    private final IUserRepository userRepository;
    private final IUnidadDeVentaRepository unidadRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final SecureRandom random = new SecureRandom();

    public StaffService(
            IStaffRepository staffRepository,
            IUserRepository userRepository,
            IUnidadDeVentaRepository unidadRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.unidadRepository = unidadRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> listarActivos() {
        return staffRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> listarInactivos() {
        return staffRepository.findByActivoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public Staff buscarPorId(long id) {
        return staffRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("El empleado no existe."));
    }

    @Override
    @Transactional
    public void alta(StaffFormDTO dto) {

        validarDni(dto.getDni(), null);
        validarEmail(dto.getEmail(), null);

        String email = normalizarEmail(dto.getEmail());
        String passwordEnClaro = generarPasswordAleatorio();

        User user = new User();
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(passwordEnClaro));
        user.setEnabled(true);

        UserRole rol = new UserRole();
        rol.setUser(user);
        rol.setRole(ROL_EMPLEADO);

        user.getUserRoles().add(rol);
        user = userRepository.save(user);

        Staff staff = new Staff();
        copiarDatos(dto, staff);
        staff.setEmail(email);
        staff.setActivo(true);
        staff.setUser(user);

        staffRepository.save(staff);

        // Si el envío falla, la excepción impide confirmar
        // esta transacción.
        emailService.enviarCredenciales(
            email,
            user.getUsername(),
            passwordEnClaro
        );
    }

    @Override
    @Transactional
    public void modificar(long id, StaffFormDTO dto) {

        Staff staff = buscarPorId(id);

        if (!staff.isActivo()) {
            throw new IllegalArgumentException(
                "No se puede modificar un empleado dado de baja.");
        }

        validarDni(dto.getDni(), id);
        validarEmail(dto.getEmail(), id);

        String email = normalizarEmail(dto.getEmail());

        User user = staff.getUser();
        if (user == null) {
            throw new IllegalStateException(
                "El empleado no tiene una cuenta asociada.");
        }

        user.setUsername(email);
        staff.setEmail(email);

        copiarDatos(dto, staff);

        // Se conservan la contraseña y los roles existentes.
        userRepository.save(user);
        staffRepository.save(staff);
    }

    @Override
    @Transactional
    public void bajaLogica(long id) {

        Staff staff = buscarPorId(id);

        if (!staff.isActivo()) {
            throw new IllegalArgumentException(
                "El empleado ya está dado de baja.");
        }

        staff.setActivo(false);

        User user = staff.getUser();
        if (user == null) {
            throw new IllegalStateException(
                "El empleado no tiene una cuenta asociada.");
        }

        user.setEnabled(false);

        staffRepository.save(staff);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void reactivar(long id) {

        Staff staff = buscarPorId(id);

        if (staff.isActivo()) {
            throw new IllegalArgumentException(
                "El empleado ya está activo.");
        }

        User user = staff.getUser();
        if (user == null) {
            throw new IllegalStateException(
                "El empleado no tiene una cuenta asociada.");
        }

        validarEmail(staff.getEmail(), id);

        staff.setActivo(true);
        user.setEnabled(true);

        staffRepository.save(staff);
        userRepository.save(user);
    }

    private void copiarDatos(StaffFormDTO dto, Staff staff) {

        staff.setNombre(dto.getNombre().trim());
        staff.setApellido(dto.getApellido().trim());
        staff.setDni(dto.getDni());
        staff.setFechaNacimiento(dto.getFechaNacimiento());
        staff.setFechaIngreso(dto.getFechaIngreso());
        staff.setSueldo(dto.getSueldo());

        if (dto.getUnidadDeVentaId() == null) {
            staff.setUnidadDeVenta(null);
        } else {
            UnidadDeVenta unidad = unidadRepository
                .findById(dto.getUnidadDeVentaId())
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "La unidad de venta seleccionada no existe."));

            staff.setUnidadDeVenta(unidad);
        }
    }

    private void validarDni(int dni, Long idExcluir) {

        staffRepository.findByDni(dni)
            .filter(s -> idExcluir == null || s.getId() != idExcluir)
            .ifPresent(s -> {
                throw new IllegalArgumentException(
                    "Ya existe un empleado con ese DNI.");
            });
    }

    private void validarEmail(String email, Long idExcluir) {

        String normalizado = normalizarEmail(email);

        staffRepository.findByEmailIgnoreCase(normalizado)
            .filter(s -> idExcluir == null || s.getId() != idExcluir)
            .ifPresent(s -> {
                throw new IllegalArgumentException(
                    "Ya existe un empleado con ese email.");
            });

        userRepository.findByUsername(normalizado)
            .filter(u -> {
                if (idExcluir == null) {
                    return true;
                }

                Staff staff = staffRepository.findById(idExcluir)
                    .orElseThrow();

                return staff.getUser() == null
                    || staff.getUser().getId() != u.getId();
            })
            .ifPresent(u -> {
                throw new IllegalArgumentException(
                    "El email ya está utilizado por otra cuenta.");
            });
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private String generarPasswordAleatorio() {

        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(
                PASSWORD_ALPHABET.charAt(
                    random.nextInt(PASSWORD_ALPHABET.length())
                )
            );
        }

        return sb.toString();
    }
}