package com.example.unla.grupo7.configuration;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.entities.User;
import com.example.unla.grupo7.entities.UserRole;
import com.example.unla.grupo7.repositories.IFestivalRepository;
import com.example.unla.grupo7.repositories.IUserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final IUserRepository userRepository;
    private final IFestivalRepository festivalRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(IUserRepository userRepository,
                      IFestivalRepository festivalRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.festivalRepository = festivalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdmin();
        seedFestivales();
    }

    private void seedAdmin() {
        if (userRepository.findByUsername("admin").isPresent()) {
            return;
        }
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setEnabled(true);
        admin = userRepository.save(admin);

        UserRole rol = new UserRole();
        rol.setUser(admin);
        rol.setRole("ADMINISTRADOR");
        admin.getUserRoles().add(rol);
        userRepository.save(admin);

        System.out.println("[DataSeeder] Usuario admin creado (usuario: admin / password: admin)");
    }

    private void seedFestivales() {
        if (festivalRepository.count() > 0) {
            return;
        }
        festivalRepository.save(nuevoFestival(
                "Festival del Chocolate",
                "Verano 2026",
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 15)));
        festivalRepository.save(nuevoFestival(
                "Feria de Sabores del Mar",
                "Otoño 2026",
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 4, 25)));

        System.out.println("[DataSeeder] Festivales de ejemplo creados.");
    }

    private Festival nuevoFestival(String nombre, String temporada, LocalDate inicio, LocalDate fin) {
        Festival f = new Festival();
        f.setNombre(nombre);
        f.setTemporada(temporada);
        f.setFechaInicio(inicio);
        f.setFechaFin(fin);
        f.setActivo(true);
        return f;
    }
}
