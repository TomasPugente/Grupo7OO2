package com.example.unla.grupo7.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.helper.ViewRouteHelper;
import com.example.unla.grupo7.repositories.IStaffRepository;
import com.example.unla.grupo7.services.IStaffService;
import com.example.unla.grupo7.services.IUnidadDeVentaService;

@Controller 
@RequestMapping ("/empleado")
/* @PreAuthorize("hasRole('EMPLEADO')") */
public class StaffController {

    private final IStaffService staffService;

    public StaffController( IStaffService staffService){
        this.staffService= staffService;
    }


    @GetMapping ("/dashboard")
    public ModelAndView dashboardEmpleado(Authentication authentication){
        String username= authentication.getName();
        ModelAndView mAV= new ModelAndView(ViewRouteHelper.EMPLEADO_DASHBOARD);
        Staff staff= staffService.buscarPorNombre(username);
        mAV.addObject("staff", staff);
        mAV.addObject("unidad", staff.getUnidadDeVenta());
        mAV.addObject("festival", staff.getUnidadDeVenta().getFestival());
        return mAV;
    }

}
