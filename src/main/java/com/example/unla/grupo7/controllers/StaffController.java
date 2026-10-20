package com.example.unla.grupo7.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.unla.grupo7.dtos.StaffFormDTO;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.services.IStaffService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/empleados")
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
    public String listar(Model model) {
        model.addAttribute("empleados", staffService.listarActivos());
        return "empleados/lista";
    }

    @GetMapping("/inactivos")
    public String listarInactivos(Model model) {
        model.addAttribute("empleados", staffService.listarInactivos());
        return "empleados/inactivos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("staffForm", new StaffFormDTO());
        cargarUnidades(model);
        return "empleados/formulario";
    }

    @PostMapping
    public String alta(
            @Valid @ModelAttribute("staffForm") StaffFormDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            cargarUnidades(model);
            return "empleados/formulario";
        }

        try {
            staffService.alta(dto);
            redirectAttrs.addFlashAttribute(
                "mensaje",
                "Empleado creado. Se enviaron las credenciales por correo."
            );
            return "redirect:/empleados";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarUnidades(model);
            return "empleados/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable long id, Model model) {

        Staff staff = staffService.buscarPorId(id);

        StaffFormDTO dto = new StaffFormDTO();
        dto.setNombre(staff.getNombre());
        dto.setApellido(staff.getApellido());
        dto.setDni(staff.getDni());
        dto.setFechaNacimiento(staff.getFechaNacimiento());
        dto.setFechaIngreso(staff.getFechaIngreso());
        dto.setSueldo(staff.getSueldo());
        dto.setEmail(staff.getEmail());

        if (staff.getUnidadDeVenta() != null) {
            dto.setUnidadDeVentaId(
                staff.getUnidadDeVenta().getIdUnidadDeVenta()
            );
        }

        model.addAttribute("staffForm", dto);
        model.addAttribute("idEmpleado", id);
        cargarUnidades(model);

        return "empleados/formulario";
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
            return "empleados/formulario";
        }

        try {
            staffService.modificar(id, dto);
            redirectAttrs.addFlashAttribute(
                "mensaje", "Empleado modificado correctamente."
            );
            return "redirect:/empleados";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("idEmpleado", id);
            model.addAttribute("error", ex.getMessage());
            cargarUnidades(model);
            return "empleados/formulario";
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

        return "redirect:/empleados";
    }

    @PostMapping("/{id}/reactivar")
    public String reactivar(
            @PathVariable long id,
            RedirectAttributes redirectAttrs) {

        staffService.reactivar(id);

        redirectAttrs.addFlashAttribute(
            "mensaje", "Empleado reactivado correctamente."
        );

        return "redirect:/empleados/inactivos";
    }

    private void cargarUnidades(Model model) {
        model.addAttribute("unidades", unidadRepository.findAll());
    }
}
