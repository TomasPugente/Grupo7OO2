package com.example.unla.grupo7.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.unla.grupo7.dtos.CredencialesGeneradasDTO;
import com.example.unla.grupo7.dtos.RegistroUnidadDTO;
import com.example.unla.grupo7.helper.ViewRouteHelper;
import com.example.unla.grupo7.services.IFestivalService;
import com.example.unla.grupo7.services.IRegistroService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/registro")
@PreAuthorize("hasRole('ADMIN')")
public class RegistroController {

    private final IRegistroService registroService;
    private final IFestivalService festivalService;

    public RegistroController(IRegistroService registroService,
                              IFestivalService festivalService) {
        this.registroService = registroService;
        this.festivalService = festivalService;
    }

    @GetMapping("")
    public ModelAndView mostrarFormulario() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.REGISTRO);
        mAV.addObject("registro", new RegistroUnidadDTO());
        mAV.addObject("festivales", festivalService.getAll());
        return mAV;
    }

    @PostMapping("")
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
}
