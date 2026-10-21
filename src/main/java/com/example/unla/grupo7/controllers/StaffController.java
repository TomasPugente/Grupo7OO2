package com.example.unla.grupo7.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.unla.grupo7.dtos.StaffDTO;
import com.example.unla.grupo7.dtos.StaffFormDTO;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.services.IStaffService;
import com.example.unla.grupo7.entities.Cajero;
import com.example.unla.grupo7.entities.Cocinero;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/empleado")
@PreAuthorize("hasRole('RESPONSABLE')")
public class StaffController {

    private final IStaffService staffService;
    private final IUnidadDeVentaRepository unidadRepository;

    public StaffController(
            IStaffService staffService,
            IUnidadDeVentaRepository unidadRepository) {
        this.staffService = staffService;
        this.unidadRepository = unidadRepository;
    }

    @GetMapping
    public ModelAndView listar() {
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

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("staffForm", new StaffFormDTO());
        cargarUnidades(model);
        return "empleado/formulario";
    }

    @PostMapping
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

    @GetMapping("/{id}/editar")
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

    @PostMapping("/{id}/editar")
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

    @PostMapping("/{id}/eliminar")
    public String baja(
            @PathVariable long id,
            RedirectAttributes redirectAttrs) {

        staffService.bajaLogica(id);

        redirectAttrs.addFlashAttribute(
            "mensaje", "Empleado dado de baja correctamente."
        );

        return "redirect:/empleado";
    }

    @PostMapping("/{id}/reactivar")
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
}
