package com.example.unla.grupo7.repositories;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.unla.grupo7.entities.UnidadDeVenta;

@Repository("unidadDeVentaRepository")
public interface IUnidadDeVentaRepository
        extends JpaRepository<UnidadDeVenta, Long> {

    boolean existsByCodigo(String codigo);

    // Orden original: por ID de menor a mayor
    List<UnidadDeVenta> findByFestival_IdFestivalOrderByIdUnidadDeVentaAsc(
            Long idFestival);

    // Orden por recaudación: de menor a mayor
    List<UnidadDeVenta> findByFestival_IdFestivalOrderByRecaudacionAsc(
            Long idFestival);

    // Orden por recaudación: de mayor a menor
    List<UnidadDeVenta> findByFestival_IdFestivalOrderByRecaudacionDesc(
            Long idFestival);

    List<UnidadDeVenta> findByFestival_IdFestival(Long idFestival);
    
        // Unidad a cargo de un responsable (según el usuario con el que inició sesión)
    @Query("select r.unidadDeVenta from Responsable r where r.user.username = :username")
    Optional<UnidadDeVenta> findByResponsableUsername(@Param("username") String username);

    // Unidad en la que trabaja un empleado activo (su usuario es su email)
    @Query("select s.unidadDeVenta from Staff s where lower(s.email) = lower(:email) and s.activo = true")
    Optional<UnidadDeVenta> findByEmpleadoActivoEmail(@Param("email") String email);
}
