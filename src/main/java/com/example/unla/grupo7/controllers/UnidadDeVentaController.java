package com.example.unla.grupo7.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.repositories.IFestivalRepository;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.dtos.ReporteMayoresCanonDTO;
import com.example.unla.grupo7.services.implementation.UnidadDeVentaService;

@Controller
@RequestMapping("/festivales")
public class UnidadDeVentaController {

    private final IFestivalRepository festivalRepository;
    private final IUnidadDeVentaRepository unidadDeVentaRepository;
    private final UnidadDeVentaService unidadDeVentaService;

    public UnidadDeVentaController(
        IFestivalRepository festivalRepository,
        IUnidadDeVentaRepository unidadDeVentaRepository,
        UnidadDeVentaService unidadDeVentaService) {

    this.festivalRepository = festivalRepository;
    this.unidadDeVentaRepository = unidadDeVentaRepository;
    this.unidadDeVentaService = unidadDeVentaService;
}

    @GetMapping("/{id}/unidades")
    public String listarUnidades(
            @PathVariable("id") Long idFestival,
            @RequestParam(defaultValue = "original") String orden,
            Model model) {

        Festival festival = festivalRepository.findById(idFestival)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el festival"
                ));

        List<UnidadDeVenta> unidades;
        String siguienteOrden;
        String textoBoton;
        
        boolean mostrarRestablecer = !orden.equals("original");

        switch (orden) {
            case "asc":
                unidades = unidadDeVentaRepository
                        .findByFestival_IdFestivalOrderByRecaudacionAsc(
                                idFestival);

                siguienteOrden = "desc";
                textoBoton = "Ordenar por recaudación: mayor a menor";
                break;

            case "desc":
                unidades = unidadDeVentaRepository
                        .findByFestival_IdFestivalOrderByRecaudacionDesc(
                                idFestival);

                siguienteOrden = "asc";
                textoBoton = "Ordenar por recaudación: menor a mayor";
                break;

            default:
                unidades = unidadDeVentaRepository
                        .findByFestival_IdFestivalOrderByIdUnidadDeVentaAsc(
                                idFestival);

                siguienteOrden = "asc";
                textoBoton = "Ordenar por recaudación: menor a mayor";
                break;
        }

        model.addAttribute("festival", festival);
        model.addAttribute("unidades", unidades);
        model.addAttribute("orden", orden);
        model.addAttribute("idFestival", idFestival);
        model.addAttribute("siguienteOrden", siguienteOrden);
        model.addAttribute("textoBoton", textoBoton);
        model.addAttribute("mostrarRestablecer", mostrarRestablecer);

        return "admin/unidades-festival";
    }

        @GetMapping("/reporte-mayores-canon")
        public String mayoresCanon(
                        @RequestParam(required = false) Long idFestival,
                        @RequestParam(defaultValue = "3") int x,
                        Model model) {

                // Obtener todos los festivales para el selector
                List<Festival> festivales = festivalRepository.findAll();

                model.addAttribute("festivales", festivales);
                model.addAttribute("idFestival", idFestival);
                model.addAttribute("x", x);

                // Comprobar si se seleccionó un festival
                if (idFestival != null) {

                        Festival festival = festivalRepository.findById(idFestival)
                                .orElseThrow(() -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No se encontró el festival"
                                ));

                        // Obtener las X unidades que más canon deben pagar
                        List<ReporteMayoresCanonDTO> reporte =
                                unidadDeVentaService.obtenerMayoresCanon(idFestival, x);

                        model.addAttribute("festival", festival);
                        model.addAttribute("reporte", reporte);
                        model.addAttribute("busquedaRealizada", true);

                } else {
                        model.addAttribute("busquedaRealizada", false);
                }

                return "admin/reporte-mayores-canon";
        }
}
