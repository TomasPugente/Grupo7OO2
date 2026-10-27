package com.example.unla.grupo7.configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.unla.grupo7.entities.DetallePedido;
import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.entities.Pedido;
import com.example.unla.grupo7.entities.Plato;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.entities.User;
import com.example.unla.grupo7.entities.UserRole;
import com.example.unla.grupo7.repositories.IDetallePedidoRepository;
import com.example.unla.grupo7.repositories.IFestivalRepository;
import com.example.unla.grupo7.repositories.IPedidoRepository;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.repositories.IUserRepository;
import com.example.unla.grupo7.repositories.IplatoRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final IUserRepository userRepository;
    private final IFestivalRepository festivalRepository;
    private final IUnidadDeVentaRepository unidadRepository;
    private final IplatoRepository platoRepository;
    private final IPedidoRepository pedidoRepository;
    private final IDetallePedidoRepository detallePedidoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(IUserRepository userRepository,
                      IFestivalRepository festivalRepository,
                      IUnidadDeVentaRepository unidadRepository,
                      IplatoRepository platoRepository,
                      IPedidoRepository pedidoRepository,
                      IDetallePedidoRepository detallePedidoRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.festivalRepository = festivalRepository;
        this.unidadRepository = unidadRepository;
        this.platoRepository = platoRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdmin();
        seedFestivales();
        seedPedidos();
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

    // Siembra 2 pedidos de ejemplo en la primera unidad que tenga platos activos.
    // Idempotente: no corre si ya hay pedidos en la BD.
    private void seedPedidos() {
        if (pedidoRepository.count() > 0) {
            return;
        }
        for (UnidadDeVenta unidad : unidadRepository.findAll()) {
            List<Plato> platos = platoRepository
                    .findByUnidadDeVenta_IdUnidadDeVentaAndActivoTrue(unidad.getIdUnidadDeVenta());
            if (platos.isEmpty()) {
                continue;
            }

            Plato p1 = platos.get(0);
            Plato p2 = platos.size() > 1 ? platos.get(1) : p1;

            crearPedido(unidad, LocalDate.now(),
                    p1, 2,
                    p2, 1);
            crearPedido(unidad, LocalDate.now().minusDays(1),
                    p1, 3,
                    null, 0);

            System.out.println("[DataSeeder] 2 pedidos de ejemplo creados para la unidad '"
                    + unidad.getNombreComercial() + "'.");
            return;
        }
        System.out.println("[DataSeeder] No se sembraron pedidos: ninguna unidad tiene platos activos.");
    }

    private void crearPedido(UnidadDeVenta unidad, LocalDate fecha,
                             Plato platoA, int cantidadA,
                             Plato platoB, int cantidadB) {
        BigDecimal total = subtotal(platoA, cantidadA).add(subtotal(platoB, cantidadB));

        Pedido pedido = new Pedido();
        pedido.setUnidadDeVenta(unidad);
        pedido.setFechaTransaccion(fecha);
        pedido.setPrecioTotal(total);
        pedidoRepository.save(pedido);

        guardarDetalle(pedido, platoA, cantidadA);
        guardarDetalle(pedido, platoB, cantidadB);
    }

    private BigDecimal subtotal(Plato plato, int cantidad) {
        if (plato == null || cantidad <= 0 || plato.getPrecioVenta() == null) {
            return BigDecimal.ZERO;
        }
        return plato.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad));
    }

    private void guardarDetalle(Pedido pedido, Plato plato, int cantidad) {
        if (plato == null || cantidad <= 0) {
            return;
        }
        DetallePedido dp = new DetallePedido();
        dp.setPedido(pedido);
        dp.setPlato(plato);
        dp.setCantidad(cantidad);
        detallePedidoRepository.save(dp);
    }
}
