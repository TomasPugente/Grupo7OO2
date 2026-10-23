package com.example.unla.grupo7.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.unla.grupo7.dtos.PlatoDTO;
import com.example.unla.grupo7.dtos.PlatoFiltroDTO;
import com.example.unla.grupo7.dtos.StaffDTO;
import com.example.unla.grupo7.dtos.StaffFormDTO;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.helper.ViewRouteHelper;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.services.IFestivalService;
import com.example.unla.grupo7.services.IPlatoService;
import com.example.unla.grupo7.services.IStaffService;
import com.example.unla.grupo7.services.IUnidadDeVentaService;
import com.example.unla.grupo7.entities.Cajero;
import com.example.unla.grupo7.entities.Cocinero;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/responsable")
@PreAuthorize ("hasRole('RESPONSABLE')")

public class ResponsableController {
    
    private final IStaffService staffService;
    private final IUnidadDeVentaRepository unidadRepository;
    private final IPlatoService platoService;
    private final IUnidadDeVentaService unidadService;

    public ResponsableController(
            IStaffService staffService,
            IUnidadDeVentaRepository unidadRepository, IPlatoService platoService,  IUnidadDeVentaService unidadService) {
        this.staffService = staffService;
        this.unidadRepository = unidadRepository;
        this.platoService= platoService;
        this.unidadService= unidadService;
    }

    // DASHBOARD

    @GetMapping("/dashboard")
    public ModelAndView dashboardResponsable(Authentication authentication) {
        String username = authentication.getName();   // el usuario con el que inició sesión
        UnidadDeVenta unidad = unidadService.buscarUnidadPorResponsableUsername(username);
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESPONSABLE_DASHBOARD);
        mAV.addObject("unidad", unidad);
        mAV.addObject("festival", unidad.getFestival());
        return mAV;
    }

    


    // VER EMPLEADOS

    @GetMapping("/empleados")
    public ModelAndView empleados() {
        ModelAndView mAV= new ModelAndView("empleado/lista");
        mAV.addObject("empleados", staffService.listarActivos());
        mAV.addObject("empleado", new StaffDTO());
        return mAV;
    }

    @GetMapping("/inactivos")
    public String listarInactivos(Model model) {
        model.addAttribute("empleados", staffService.listarInactivos());
        return "empleado/inactivos";
    }

    @GetMapping("/empleados/nuevo")
    public String nuevoStaff(Model model) {
        model.addAttribute("staffForm", new StaffFormDTO());
        cargarUnidades(model);
        return "empleado/formulario";
    }

    @PostMapping("/empleados/nuevo")
    public String alta(
            @Valid @ModelAttribute("staffForm") StaffFormDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            cargarUnidades(model);
            return "empleado/formulario";
        }

        try {
            staffService.alta(dto);
            redirectAttrs.addFlashAttribute(
                "mensaje",
                "Empleado creado. Se enviaron las credenciales por correo."
            );
            return "redirect:/empleado";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarUnidades(model);
            return "empleado/formulario";
        }
    }

    @GetMapping("/empleados/{id}/editar")
    public String editar(@PathVariable long id, Model model) {

        Staff staff = staffService.buscarPorId(id);

        StaffFormDTO dto = new StaffFormDTO();
        if (staff instanceof Cajero) {
            dto.setTipoEmpleado("Cajero");
        } else if (staff instanceof Cocinero cocinero) {
            dto.setTipoEmpleado("Cocinero");
            dto.setEspecialidad(cocinero.getEspecialidad());
        }
        dto.setNombre(staff.getNombre());
        dto.setApellido(staff.getApellido());
        dto.setDni(staff.getDni());
        dto.setFechaNacimiento(staff.getFechaNacimiento());
        dto.setFechaIngreso(staff.getFechaIngreso());
        dto.setEmail(staff.getEmail());

        if (staff.getUnidadDeVenta() != null) {
            dto.setUnidadDeVentaId(
                staff.getUnidadDeVenta().getIdUnidadDeVenta()
            );
        }

        model.addAttribute("staffForm", dto);
        model.addAttribute("idEmpleado", id);
        cargarUnidades(model);

        return "empleado/formulario";
    }

    @PostMapping("/empleados/{id}/editar")
    public String modificar(
            @PathVariable long id,
            @Valid @ModelAttribute("staffForm") StaffFormDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            model.addAttribute("idEmpleado", id);
            cargarUnidades(model);
            return "empleado/formulario";
        }

        try {
            staffService.modificar(id, dto);
            redirectAttrs.addFlashAttribute(
                "mensaje", "Empleado modificado correctamente."
            );
            return "redirect:/empleado";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("idEmpleado", id);
            model.addAttribute("error", ex.getMessage());
            cargarUnidades(model);
            return "empleado/formulario";
        }
    }

    @PostMapping("/empleados/{id}/eliminar")
    public String baja(
            @PathVariable long id,
            RedirectAttributes redirectAttrs) {

        staffService.bajaLogica(id);

        redirectAttrs.addFlashAttribute(
            "mensaje", "Empleado dado de baja correctamente."
        );

        return "redirect:/empleado";
    }

    @PostMapping("/empleados/{id}/reactivar")
    public String reactivar(
            @PathVariable long id,
            RedirectAttributes redirectAttrs) {

        staffService.reactivar(id);

        redirectAttrs.addFlashAttribute(
            "mensaje", "Empleado reactivado correctamente."
        );

        return "redirect:/empleado/inactivos";
    }

    private void cargarUnidades(Model model) {
        model.addAttribute("unidades", unidadRepository.findAll());
    }

    // VER PLATOS
 /** Rol que se pasa a la barra de navegación (fragments/layout). */
    @ModelAttribute("rolNav")
    public String rolNav(Authentication authentication) {
        boolean esResponsable = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RESPONSABLE"));
        return esResponsable ? "responsable" : "empleado";
    }

    @GetMapping("/platos")
    public String platos(@Valid @ModelAttribute("filtro") PlatoFiltroDTO filtro,
                         BindingResult result,
                         Authentication authentication,
                         Model model) {
        String username = authentication.getName();
        List<PlatoDTO> platos;

        if (result.hasErrors()) {
            platos = platoService.listar(username);
        } else {
            try {
                platos = platoService.filtrar(filtro, username);
            } catch (IllegalArgumentException e) {
                result.reject("filtro.invalido", e.getMessage());
                platos = platoService.listar(username);
            }
        }

        model.addAttribute("platos", platos);
        model.addAttribute("hayFiltros", filtro.hayFiltros() && !result.hasErrors());
        
        return ViewRouteHelper.VISTA_LISTA;
    }

    @GetMapping("/platos/nuevo")
    public String nuevoPlato(Model model) {
        model.addAttribute("plato", new PlatoDTO());
        model.addAttribute("accion", "/responsable/platos");
        model.addAttribute("titulo", "Nuevo plato");
        return ViewRouteHelper.VISTA_FORM;
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
                return ViewRouteHelper.REDIRECT_LISTA;
            } catch (IllegalArgumentException e) {
                agregarError(result, e);
            }
        }
        model.addAttribute("accion", "/responsable/platos");
        model.addAttribute("titulo", "Nuevo plato");
        return ViewRouteHelper.VISTA_FORM;
    }

    @GetMapping("/platos/{id}/editar")
    public String editar(@PathVariable Long id, Authentication authentication, Model model) {
        model.addAttribute("plato", platoService.obtener(id, authentication.getName()));
        model.addAttribute("accion", "/responsable/platos/" + id);
        model.addAttribute("titulo", "Modificar plato");
        return ViewRouteHelper.VISTA_FORM;
    }

    @PostMapping("/platos/{id}")
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
                return ViewRouteHelper.REDIRECT_LISTA;
            } catch (IllegalArgumentException e) {
                agregarError(result, e);
            }
        }
        model.addAttribute("accion", "/responsable/platos/" + id);
        model.addAttribute("titulo", "Modificar plato");
        return ViewRouteHelper.VISTA_FORM;
    }

    @PostMapping("/platos/{id}/eliminar")
    public String eliminar(@PathVariable Long id, Authentication authentication, RedirectAttributes flash) {
        platoService.eliminar(id, authentication.getName());
        flash.addFlashAttribute("exito", "Plato eliminado.");
        return ViewRouteHelper.REDIRECT_LISTA;
    }

    private void agregarError(BindingResult result, IllegalArgumentException e) {
        result.reject("plato.invalido", e.getMessage());
    }
    
}
