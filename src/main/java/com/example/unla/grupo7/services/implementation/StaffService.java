package com.example.unla.grupo7.services.implementation;

import java.security.SecureRandom;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.time.LocalDate;

import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.unla.grupo7.dtos.LiquidacionDTO;
import com.example.unla.grupo7.dtos.StaffFormDTO;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.entities.User;
import com.example.unla.grupo7.entities.UserRole;
import com.example.unla.grupo7.repositories.IStaffRepository;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.repositories.IUserRepository;
import com.example.unla.grupo7.services.ICostosService;
import com.example.unla.grupo7.services.IStaffService;
import com.example.unla.grupo7.entities.Cajero;
import com.example.unla.grupo7.entities.Cocinero;
import com.example.unla.grupo7.entities.Festival;


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
    private final ICostosService costosService;

    private final SecureRandom random = new SecureRandom();

    public StaffService(
            IStaffRepository staffRepository,
            IUserRepository userRepository,
            IUnidadDeVentaRepository unidadRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            ICostosService costosService) {

        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.unidadRepository = unidadRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.costosService= costosService;
    }

    public Staff buscarPorNombre(String username) {
        return staffRepository.findByUser_Username(username).orElse(null);
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

        Staff staff;
        if ("Cajero".equals(dto.getTipoEmpleado())) {

            if (dto.getTurno() == null || dto.getTurno().isBlank()) {
                throw new IllegalArgumentException(
                "Debe seleccionar el turno del cajero.");
            }

            Cajero cajero = new Cajero();
            cajero.setTurno(dto.getTurno().trim());

            staff = cajero;

        } else if ("Cocinero".equals(dto.getTipoEmpleado())) {

            if (dto.getEspecialidad() == null
                || dto.getEspecialidad().isBlank()) {
                throw new IllegalArgumentException(
                "Debe indicar la especialidad del cocinero.");
            }

            Cocinero cocinero = new Cocinero();
            cocinero.setEspecialidad(dto.getEspecialidad().trim());

            staff = cocinero;

        } else {
            throw new IllegalArgumentException(
            "Debe seleccionar Cajero o Cocinero.");
        }

    

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

        copiarDatos(dto, staff);
        
        staff.setFechaIngreso(LocalDate.now());

        UnidadDeVenta unidad = unidadRepository
            .findById(dto.getUnidadDeVentaId())
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "La unidad de venta seleccionada no existe."));

        staff.setUnidadDeVenta(unidad);
        staff.setEmail(email);
        staff.setActivo(true);
        staff.setUser(user);

        // El sueldo no se asigna: queda null inicialmente.
        staffRepository.save(staff);

        try {
            emailService.enviarCredenciales(
                email,
                user.getUsername(),
                passwordEnClaro
            );
        } catch (Exception ex) {
            // Si falla el envío (p.ej. SMTP TLS roto en dev), logeamos las
            // credenciales en vez de hacer rollback de todo el alta.
            System.out.println("[StaffService] No se pudo enviar el mail de credenciales. "
                + "Causa: " + ex.getMessage()
                + " | Usuario: " + user.getUsername()
                + " | Contraseña temporal: " + passwordEnClaro);
        }
    }


    @Override
    @Transactional
    public void modificar(long id, StaffFormDTO dto) {

        Staff staff = buscarPorId(id);

        String tipoActual;

        if (staff instanceof Cajero) {
            tipoActual = "Cajero";
        } else if (staff instanceof Cocinero) {
            tipoActual = "Cocinero";
        } else {
            throw new IllegalStateException(
                "El empleado tiene un tipo no reconocido.");
        }

        if (!tipoActual.equals(dto.getTipoEmpleado())) {
            throw new IllegalArgumentException(
                "No se puede cambiar el tipo de un empleado existente.");
        }

        if (staff instanceof Cocinero cocinero) {

            if (dto.getEspecialidad() == null
                    || dto.getEspecialidad().isBlank()) {
                throw new IllegalArgumentException(
                    "Debe indicar la especialidad del cocinero.");
            }

            cocinero.setEspecialidad(dto.getEspecialidad().trim());
        }


        if (!staff.isActivo()) {
            throw new IllegalArgumentException(
                "No se puede modificar un empleado dado de baja.");
        }
        
        if (staff instanceof Cajero cajero) {

            if (dto.getTurno() == null || dto.getTurno().isBlank()) {
                throw new IllegalArgumentException(
                "Debe seleccionar el turno del cajero.");
            }

            cajero.setTurno(dto.getTurno().trim());
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

    
    @Override
    @Transactional(readOnly = true)
    public List<Staff> listarActivosPorUnidad(Long idUnidadDeVenta) {
        return staffRepository.findByUnidadDeVenta_IdUnidadDeVentaAndActivoTrue(idUnidadDeVenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> filtrarActivosPorUnidad(
        Long idUnidadDeVenta,
        String nombre,
        String apellido,
        String dni,
        BigDecimal sueldoMinimo,
        BigDecimal sueldoMaximo,
        String turno,
        String especialidad) {

    // Primero obtenemos solamente los empleados activos
    // de la unidad del Responsable.
    List<Staff> empleados =
        staffRepository
            .findByUnidadDeVenta_IdUnidadDeVentaAndActivoTrue(
                idUnidadDeVenta
            );

    return empleados.stream()
        .filter(e -> {

           // Filtro opcional por nombre.
                if (nombre != null && !nombre.isBlank()) {
                    if (e.getNombre() == null
                        || !e.getNombre()
                        .toLowerCase(Locale.ROOT)
                        .contains(nombre.trim().toLowerCase(Locale.ROOT))) {
                        return false;
                    }
                }

            // Filtro opcional por apellido.
                if (apellido != null && !apellido.isBlank()) {
                    if (e.getApellido() == null
                        || !e.getApellido()
                        .toLowerCase(Locale.ROOT)
                        .contains(apellido.trim().toLowerCase(Locale.ROOT))) {
                        return false;
                    }
                }

            // Filtro opcional por DNI parcial.
                if (dni != null && !dni.isBlank()) {
                    String dniEmpleado = String.valueOf(e.getDni());

                    if (!dniEmpleado.contains(dni.trim())) {
                        return false;
                    }
                }

            // Filtro opcional por sueldo mínimo.
            if (sueldoMinimo != null) {
                if (e.getSueldo() == null
                        || e.getSueldo()
                            .compareTo(sueldoMinimo) < 0) {
                    return false;
                }
            }

            // Filtro opcional por sueldo máximo.
            if (sueldoMaximo != null) {
                if (e.getSueldo() == null
                        || e.getSueldo()
                            .compareTo(sueldoMaximo) > 0) {
                    return false;
                }
            }

            // Filtro opcional por turno:
            // solamente puede coincidir un Cajero.
            if (turno != null && !turno.isBlank()) {
                if (!(e instanceof Cajero cajero)) {
                    return false;
                }

                if (cajero.getTurno() == null
                        || !cajero.getTurno().equalsIgnoreCase(
                            turno.trim()
                        )) {
                    return false;
                }
            }

            // Filtro opcional por especialidad:
            // solamente puede coincidir un Cocinero.
            if (especialidad != null
                    && !especialidad.isBlank()) {

                if (!(e instanceof Cocinero cocinero)) {
                    return false;
                }

                if (cocinero.getEspecialidad() == null
                        || !cocinero.getEspecialidad()
                            .toLowerCase(Locale.ROOT)
                            .contains(
                                especialidad.trim()
                                    .toLowerCase(Locale.ROOT)
                            )) {
                    return false;
                }
            }

            return true;
        })
        .collect(Collectors.toList());
}



    public List<Staff> listarInactivosPorUnidad(Long idUnidadDeVenta) {
        return staffRepository.findByUnidadDeVenta_IdUnidadDeVentaAndActivoFalse(idUnidadDeVenta);
    }

    public LiquidacionDTO liquidarYActualizarSueldo(Staff staff, Festival festival) {
    Staff real = (Staff) Hibernate.unproxy(staff);

    LiquidacionDTO liquidacion = costosService.liquidarSueldo(real, festival);

    real.setSueldo(liquidacion.getTotal()); 
    staffRepository.save(real);

    return liquidacion;
}

}