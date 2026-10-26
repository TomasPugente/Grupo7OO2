package com.example.unla.grupo7.controllers;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.unla.grupo7.dtos.PedidoFormDTO;
import com.example.unla.grupo7.dtos.PedidosDTO;
import com.example.unla.grupo7.dtos.PlatoDTO;
import com.example.unla.grupo7.entities.Pedido;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.helper.ViewRouteHelper;
import com.example.unla.grupo7.services.IPedidoService;
import com.example.unla.grupo7.services.IPlatoService;
import com.example.unla.grupo7.services.IStaffService;

import jakarta.validation.Valid;

@Controller 
@RequestMapping ("/empleado")
/* @PreAuthorize("hasRole('EMPLEADO')") */
public class StaffController {

    private final IStaffService staffService;
    private final IPlatoService platoService;
    private final IPedidoService pedidoService;

    public StaffController( IStaffService staffService, IPlatoService platoService, IPedidoService pedidoService){
        this.staffService= staffService;
        this.platoService= platoService;
        this.pedidoService=pedidoService;
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

    @GetMapping ("/pedidos")
    public ModelAndView pedidos(Authentication authentication){
        String username= authentication.getName();
        ModelAndView mAV= new ModelAndView(ViewRouteHelper.EMPLEADO_PEDIDOS);
        List<Pedido> pedidos= pedidoService.traerPorUsuario(username);
        mAV.addObject("pedidos", pedidos);
        mAV.addObject("pedido", new PedidosDTO());
        return mAV;
    }

    @GetMapping ("/pedidos/nuevo")
    public ModelAndView nuevoPedido(Authentication authentication){
        String username= authentication.getName();
        ModelAndView mAV= new ModelAndView(ViewRouteHelper.EMPLEADO_NUEVOPEDIDO);
        Staff staff= staffService.buscarPorNombre(username);
        List<PlatoDTO> platos= platoService.listar(username);
        mAV.addObject("unidad", staff.getUnidadDeVenta());
        mAV.addObject("platos", platos);
        mAV.addObject("pedidoForm", new PedidoFormDTO());
        return mAV;
    }

    @PostMapping("/pedidos")
    public ModelAndView crearPedido(@Valid @ModelAttribute("pedidoForm") PedidoFormDTO form,
                                    BindingResult result,
                                    Authentication authentication,
                                    RedirectAttributes flash) {

        String username = authentication.getName();
        Staff staff = staffService.buscarPorNombre(username);

        if (!result.hasErrors()) {
            try {
                pedidoService.crear(form, staff.getUnidadDeVenta());
                flash.addFlashAttribute("exito", "Pedido cargado correctamente.");
                return new ModelAndView("redirect:/empleado/pedidos");
            } catch (IllegalArgumentException e) {
                result.reject("pedido.invalido", e.getMessage());
            }
        }

        ModelAndView mAV = new ModelAndView(ViewRouteHelper.EMPLEADO_NUEVOPEDIDO);
        mAV.addObject("unidad", staff.getUnidadDeVenta());
        mAV.addObject("platos", platoService.listar(username));
        return mAV;
}
}
