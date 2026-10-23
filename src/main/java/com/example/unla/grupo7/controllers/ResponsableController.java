package com.example.unla.grupo7.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.unla.grupo7.helper.ViewRouteHelper;

@Controller 
@RequestMapping ("/responsable")
public class ResponsableController {
    
    @GetMapping ("")
    public ModelAndView dashboardResponsable(){
        ModelAndView mAV= new ModelAndView(ViewRouteHelper.RESPONSABLE_DASHBOARD);
        return mAV;
    }
}
