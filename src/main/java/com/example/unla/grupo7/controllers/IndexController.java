package com.example.unla.grupo7.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.core.Authentication;
import com.example.unla.grupo7.dtos.FestivalDTO;
import com.example.unla.grupo7.helper.ViewRouteHelper;
import com.example.unla.grupo7.services.IFestivalService;

@Controller 
@RequestMapping ("/index")
public class IndexController {
    
    private final IFestivalService festivalService;

    public IndexController(IFestivalService festivalService){
        this.festivalService= festivalService;
    }

   
    @GetMapping("")
    public ModelAndView index(Authentication authentication) {

        ModelAndView mAV = new ModelAndView(ViewRouteHelper.INDEX);

        mAV.addObject("festivales", festivalService.getAll());
        mAV.addObject("festival", new FestivalDTO());

        boolean esAdmin = authentication != null &&
            authentication.getAuthorities().stream()
                    .anyMatch(authority ->
                            authority.getAuthority().equals("ROLE_ADMINISTRADOR"));
        mAV.addObject("esAdmin", esAdmin);

        boolean esResponsable = authentication != null &&
            authentication.getAuthorities().stream()
                    .anyMatch(authority ->
                            authority.getAuthority().equals("ROLE_RESPONSABLE"));
        mAV.addObject("esResponsable", esResponsable);

            boolean esEmpleado = authentication != null &&
            authentication.getAuthorities().stream()
                    .anyMatch(authority ->
                            authority.getAuthority().equals("ROLE_EMPLEADO"));
        mAV.addObject("esEmpleado", esEmpleado);

    return mAV;
}

}
