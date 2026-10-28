package com.example.unla.grupo7.controllers;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.unla.grupo7.dtos.PedidosDTO;
import com.example.unla.grupo7.dtos.PlatoDTO;
import com.example.unla.grupo7.dtos.PlatoFiltroDTO;
import com.example.unla.grupo7.dtos.RentabilidadDTO;
import com.example.unla.grupo7.dtos.StaffDTO;
import com.example.unla.grupo7.dtos.StaffFormDTO;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.helper.ViewRouteHelper;
import com.example.unla.grupo7.repositories.IUnidadDeVentaRepository;
import com.example.unla.grupo7.services.ICostosService;
import com.example.unla.grupo7.services.IPedidoService;
import com.example.unla.grupo7.services.IPlatoService;
import com.example.unla.grupo7.services.IRentabilidadService;
import com.example.unla.grupo7.services.IStaffService;
import com.example.unla.grupo7.services.IUnidadDeVentaService;
import com.example.unla.grupo7.entities.Cajero;
import com.example.unla.grupo7.entities.Cocinero;
import com.example.unla.grupo7.entities.Pedido;
import com.example.unla.grupo7.dtos.CanonDTO;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/responsable")
@PreAuthorize ("hasRole('RESPONSABLE')")

public class ResponsableController {

    private final IStaffService staffService;
    private final IUnidadDeVentaRepository unidadRepository;
    private final IPlatoService platoService;
    private final IUnidadDeVentaService unidadService;
    private final IRentabilidadService rentabilidadService;
    private final ICostosService costosService;
    private final IPedidoService pedidoService;

    public ResponsableController(
            IStaffService staffService,
            IUnidadDeVentaRepository unidadRepository,
            IPlatoService platoService,
            IUnidadDeVentaService unidadService,
            IRentabilidadService rentabilidadService,
            ICostosService costosService,
            IPedidoService pedidoService) {

        this.staffService = staffService;
        this.unidadRepository = unidadRepository;
        this.platoService = platoService;
        this.unidadService = unidadService;
        this.rentabilidadService = rentabilidadService;
        this.costosService = costosService;
        this.pedidoService = pedidoService;
    }

    // RENTABILIDAD (CU-15 total / CU-16 entre fechas)

    @GetMapping("/reportes")
    public ModelAndView rentabilidad(
            Authentication authentication,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        UnidadDeVenta unidad =
                unidadService.buscarUnidadPorResponsableUsername(authentication.getName());

        ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESPONSABLE_RENTABILIDAD);
        mAV.addObject("desde", desde);
        mAV.addObject("hasta", hasta);

        boolean hayAlgunaFecha = desde != null || hasta != null;
        boolean hayAmbasFechas = desde != null && hasta != null;

        if (hayAlgunaFecha && !hayAmbasFechas) {
            mAV.addObject("error", "Completá ambas fechas para filtrar.");
            mAV.addObject("rentabilidad",
                    rentabilidadService.calcularPorUnidad(unidad.getIdUnidadDeVenta()));
            return mAV;
        }

        try {
            RentabilidadDTO rentabilidad = hayAmbasFechas
                    ? rentabilidadService.calcularPorUnidadEntreFechas(
                            unidad.getIdUnidadDeVenta(), desde, hasta)
                    : rentabilidadService.calcularPorUnidad(unidad.getIdUnidadDeVenta());
            mAV.addObject("rentabilidad", rentabilidad);
        } catch (IllegalArgumentException ex) {
            mAV.addObject("error", ex.getMessage());
            mAV.addObject("rentabilidad",
                    rentabilidadService.calcularPorUnidad(unidad.getIdUnidadDeVenta()));
        }
        return mAV;
    }

    // CANON DE LA UNIDAD

    @GetMapping("/canon")
    public ModelAndView canon(Authentication authentication) {
        UnidadDeVenta unidad =
                unidadService.buscarUnidadPorResponsableUsername(authentication.getName());

        ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESPONSABLE_CANON);
        try {
            CanonDTO canon = costosService.detalleCanon(unidad);
            mAV.addObject("canon", canon);
        } catch (IllegalStateException ex) {
            mAV.addObject("error", ex.getMessage());
        }
        return mAV;
    }

    // DASHBOARD

    @GetMapping("/dashboard")
    public ModelAndView dashboardResponsable(Authentication authentication) {
        String username = authentication.getName();   // el usuario con el que inició sesión
        UnidadDeVenta unidad = unidadService.buscarUnidadPorResponsableUsername(username);
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.RESPONSABLE_DASHBOARD);
        BigDecimal canon= costosService.calcularCanon(unidad);
        List<Pedido> pedidos= pedidoService.traerUltimos5Pedidos(username);
        mAV.addObject("unidad", unidad);
        mAV.addObject("festival", unidad.getFestival());
        mAV.addObject("canon", canon);
        mAV.addObject("pedidos", pedidos);
        mAV.addObject("pedido", new PedidosDTO());
        return mAV;
    }

    


    // VER EMPLEADOS

    @GetMapping("/empleados")
    public ModelAndView empleados(
        Authentication authentication,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String apellido,
        @RequestParam(required = false) String dni,
        @RequestParam(required = false) BigDecimal sueldoMinimo,
        @RequestParam(required = false) BigDecimal sueldoMaximo,
        @RequestParam(required = false) String turno,
        @RequestParam(required = false) String especialidad) {

        String username = authentication.getName();

        UnidadDeVenta unidad =
            unidadService.buscarUnidadPorResponsableUsername(username);

        ModelAndView mAV = new ModelAndView("empleado/lista");

        List<Staff> empleados =
            staffService.filtrarActivosPorUnidad(
                unidad.getIdUnidadDeVenta(),
                nombre,
                apellido,
                dni,
                sueldoMinimo,
                sueldoMaximo,
                turno,
                especialidad
            );

        mAV.addObject("empleados", empleados);
        mAV.addObject("empleado", new StaffDTO());

        // Conservamos los valores ingresados en los filtros.
        mAV.addObject("nombre", nombre);
        mAV.addObject("apellido", apellido);
        mAV.addObject("dni", dni);
        mAV.addObject("sueldoMinimo", sueldoMinimo);
        mAV.addObject("sueldoMaximo", sueldoMaximo);
        mAV.addObject("turno", turno);
        mAV.addObject("especialidad", especialidad);

        return mAV;
    }
    
    @GetMapping("/inactivos")
    public String listarInactivos(
        Authentication authentication,
        Model model) {

        String username = authentication.getName();

        UnidadDeVenta unidad =
            unidadService.buscarUnidadPorResponsableUsername(username);

        model.addAttribute(
            "empleados",
            staffService.listarInactivosPorUnidad(
                    unidad.getIdUnidadDeVenta())
        );

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
        Authentication authentication,
        Model model,
        RedirectAttributes redirectAttrs) {

        // Obtener la unidad del Responsable autenticado
        String username = authentication.getName();

        UnidadDeVenta unidad =
            unidadService.buscarUnidadPorResponsableUsername(username);

        // Asignar automáticamente la unidad correspondiente
        dto.setUnidadDeVentaId(unidad.getIdUnidadDeVenta());

        if (result.hasErrors()) {
            model.addAttribute("idUnidadResponsable",
                unidad.getIdUnidadDeVenta());
            return "empleado/formulario";
        }

        try {
            staffService.alta(dto);
            
            redirectAttrs.addFlashAttribute(
                "mensaje",
                "Empleado creado correctamente en tu unidad de venta."
            );

            return "redirect:/responsable/empleados";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute(
                "idUnidadResponsable",
                unidad.getIdUnidadDeVenta()
            );
            return "empleado/formulario";
        }
    }


    @GetMapping("/empleados/{id}/editar")
    public String editar(@PathVariable long id, Model model) {

        Staff staff = staffService.buscarPorId(id);

        StaffFormDTO dto = new StaffFormDTO();
        if (staff instanceof Cajero cajero) {
            dto.setTipoEmpleado("Cajero");
            dto.setTurno(cajero.getTurno());
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
            return "redirect:/responsable/empleados";

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

        return "redirect:/responsable/empleados";
    }

    @PostMapping("/empleados/{id}/reactivar")
    public String reactivar(
            @PathVariable long id,
            RedirectAttributes redirectAttrs) {

        staffService.reactivar(id);

        redirectAttrs.addFlashAttribute(
            "mensaje", "Empleado reactivado correctamente."
        );

        return "redirect:/responsable/inactivos";
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
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'EMPLEADO')")
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

    @PostMapping("/platos")
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
    
    @GetMapping("/platos/inactivos")
public String listarPlatosInactivos(
        Authentication authentication,
        Model model) {

    model.addAttribute(
        "platos",
        platoService.listarInactivos(authentication.getName())
    );

    return "responsable/platos-inactivos";
}

    @PostMapping("/platos/{id}/reactivar")
public String reactivarPlato(
        @PathVariable Long id,
        Authentication authentication,
        RedirectAttributes flash) {

    platoService.reactivar(id, authentication.getName());

    flash.addFlashAttribute(
        "exito", "Plato reactivado correctamente."
    );

    return "redirect:/responsable/platos/inactivos";
}

}
