package com.example.unla.grupo7.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.unla.grupo7.dtos.PlatoDTO;
import com.example.unla.grupo7.services.IPlatoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/responsable/platos")
public class PlatoController {

    private static final String VISTA_LISTA = "responsable/platos";
    private static final String VISTA_FORM = "responsable/plato-form";
    private static final String REDIRECT_LISTA = "redirect:/responsable/platos";

    private final IPlatoService platoService;

    public PlatoController(IPlatoService platoService) {
        this.platoService = platoService;
    }

    /** Rol que se pasa a la barra de navegación (fragments/layout). */
    @ModelAttribute("rolNav")
    public String rolNav(Authentication authentication) {
        boolean esResponsable = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RESPONSABLE"));
        return esResponsable ? "responsable" : "empleado";
    }

    @GetMapping("")
    public String listar(Authentication authentication, Model model) {
        model.addAttribute("platos", platoService.listar(authentication.getName()));
        return VISTA_LISTA;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("plato", new PlatoDTO());
        model.addAttribute("accion", "/responsable/platos");
        model.addAttribute("titulo", "Nuevo plato");
        return VISTA_FORM;
    }

    @PostMapping("")
    public String crear(@Valid @ModelAttribute("plato") PlatoDTO plato,
                        BindingResult result,
                        Authentication authentication,
                        Model model,
                        RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                platoService.crear(plato, authentication.getName());
                flash.addFlashAttribute("exito", "Plato agregado correctamente.");
                return REDIRECT_LISTA;
            } catch (IllegalArgumentException e) {
                agregarError(result, e);
            }
        }
        model.addAttribute("accion", "/responsable/platos");
        model.addAttribute("titulo", "Nuevo plato");
        return VISTA_FORM;
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Authentication authentication, Model model) {
        model.addAttribute("plato", platoService.obtener(id, authentication.getName()));
        model.addAttribute("accion", "/responsable/platos/" + id);
        model.addAttribute("titulo", "Modificar plato");
        return VISTA_FORM;
    }

    @PostMapping("/{id}")
    public String modificar(@PathVariable Long id,
                            @Valid @ModelAttribute("plato") PlatoDTO plato,
                            BindingResult result,
                            Authentication authentication,
                            Model model,
                            RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                platoService.modificar(id, plato, authentication.getName());
                flash.addFlashAttribute("exito", "Plato modificado correctamente.");
                return REDIRECT_LISTA;
            } catch (IllegalArgumentException e) {
                agregarError(result, e);
            }
        }
        model.addAttribute("accion", "/responsable/platos/" + id);
        model.addAttribute("titulo", "Modificar plato");
        return VISTA_FORM;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, Authentication authentication, RedirectAttributes flash) {
        platoService.eliminar(id, authentication.getName());
        flash.addFlashAttribute("exito", "Plato eliminado.");
        return REDIRECT_LISTA;
    }

    private void agregarError(BindingResult result, IllegalArgumentException e) {
        result.reject("plato.invalido", e.getMessage());
    }
}