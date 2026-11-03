package com.example.unla.grupo7.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.unla.grupo7.dtos.CredencialesGeneradasDTO;
import com.example.unla.grupo7.dtos.RegistroUnidadDTO;
import com.example.unla.grupo7.helper.ViewRouteHelper;
import com.example.unla.grupo7.services.IFestivalService;
import com.example.unla.grupo7.services.IRegistroService;

import jakarta.validation.Valid;

@Controller
public class UserController {

	private final IFestivalService festivalService;
	private final IRegistroService registroService;

	private UserController(IFestivalService festivalService, IRegistroService registroService){
		this.festivalService= festivalService;
		this.registroService= registroService;
	}

	// LOGEO DE USUARIOS
	@GetMapping("/login")
	public String login(Model model,
						@RequestParam(required=false) String error,
						@RequestParam(required=false) String logout) {
		model.addAttribute("error", error);
		model.addAttribute("logout", logout);
		return ViewRouteHelper.LOGIN;
	}

	@GetMapping("/logout")
	public String logout(Model model) {
		return ViewRouteHelper.LOGOUT;
	}

	@GetMapping("/loginsuccess")
	public String loginCheck() {
		return "redirect:/";
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
            return new ModelAndView("redirect:/exito");
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