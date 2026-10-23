package com.example.unla.grupo7.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.unla.grupo7.dtos.CredencialesGeneradasDTO;
import com.example.unla.grupo7.dtos.RegistroUnidadDTO;
import com.example.unla.grupo7.dtos.ReporteMayoresCanonDTO;
import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.helper.ViewRouteHelper;
import com.example.unla.grupo7.repositories.IFestivalRepository;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.services.IFestivalService;
import com.example.unla.grupo7.services.IRegistroService;
import com.example.unla.grupo7.services.implementation.UnidadDeVentaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {
    private final IRegistroService registroService;
    private final IFestivalService festivalService;
    private final IFestivalRepository festivalRepository;
    private final IUnidadDeVentaRepository unidadDeVentaRepository;
    private final UnidadDeVentaService unidadDeVentaService;

    public AdminController(IRegistroService registroService,IFestivalService festivalService, IFestivalRepository festivalRepository,
        IUnidadDeVentaRepository unidadDeVentaRepository,
        UnidadDeVentaService unidadDeVentaService) {

    this.festivalRepository = festivalRepository;
    this.unidadDeVentaRepository = unidadDeVentaRepository;
    this.unidadDeVentaService = unidadDeVentaService;
        this.registroService = registroService;
        this.festivalService = festivalService;
    }

    // REGISTRAR UNIDADES

    @GetMapping("/registrarunidad")
    public ModelAndView mostrarFormulario() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.REGISTRO);
        mAV.addObject("registro", new RegistroUnidadDTO());
        mAV.addObject("festivales", festivalService.getAll());
        return mAV;
    }

    @PostMapping("/registrarunidad")
    public ModelAndView registrar(@Valid RegistroUnidadDTO registro,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.REGISTRO);
            mAV.addObject("registro", registro);
            mAV.addObject("festivales", festivalService.getAll());
            return mAV;
        }
        try {
            CredencialesGeneradasDTO credenciales = registroService.registrar(registro);
            redirectAttrs.addFlashAttribute("credenciales", credenciales);
            return new ModelAndView("redirect:/registro/exito");
        } catch (IllegalArgumentException ex) {
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.REGISTRO);
            mAV.addObject("registro", registro);
            mAV.addObject("festivales", festivalService.getAll());
            mAV.addObject("error", ex.getMessage());
            return mAV;
        }
    }

    @GetMapping("/exito")
    public ModelAndView exito() {
        return new ModelAndView(ViewRouteHelper.REGISTRO_EXITO);
    }

    // UNIDADES DE VENTA

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
