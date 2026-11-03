package com.example.unla.grupo7.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.unla.grupo7.dtos.InfoEmpleadoDTO;
import com.example.unla.grupo7.services.IInfoEmpleadoService;
import com.example.unla.grupo7.dtos.PedidoFormDTO;
import com.example.unla.grupo7.dtos.PedidosDTO;
import com.example.unla.grupo7.dtos.PlatoDTO;
import com.example.unla.grupo7.entities.Cajero;
import com.example.unla.grupo7.entities.CierreCaja;
import com.example.unla.grupo7.entities.Cocinero;
import com.example.unla.grupo7.entities.Festival;
import com.example.unla.grupo7.entities.Pedido;
import com.example.unla.grupo7.entities.Staff;
import com.example.unla.grupo7.entities.UnidadDeVenta;
import com.example.unla.grupo7.helper.ViewRouteHelper;
import com.example.unla.grupo7.services.ICierreCajaService;
import com.example.unla.grupo7.services.IPedidoService;
import com.example.unla.grupo7.services.IPlatoService;
import com.example.unla.grupo7.services.IStaffService;
import com.example.unla.grupo7.dtos.LiquidacionDTO;
import com.example.unla.grupo7.services.implementation.CierreCajaPdfService;
import com.example.unla.grupo7.services.implementation.ReciboSueldoPdfService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller 
@RequestMapping ("/empleado")
/* @PreAuthorize("hasRole('EMPLEADO')") */
public class StaffController {

    private final IStaffService staffService;
    private final IPlatoService platoService;
    private final IPedidoService pedidoService;
    private final ICierreCajaService cierreCajaService;
    private final ReciboSueldoPdfService reciboPdfService;
    private final IInfoEmpleadoService infoEmpleadoService;
    private final CierreCajaPdfService cierreCajaPdfService;
    public StaffController( IStaffService staffService, IPlatoService platoService, IPedidoService pedidoService,  ICierreCajaService cierreCajaService, ReciboSueldoPdfService reciboPdfService, CierreCajaPdfService cierreCajaPdfService, IInfoEmpleadoService infoEmpleadoService){
        this.staffService= staffService;
        this.platoService= platoService;
        this.pedidoService=pedidoService;
        this.cierreCajaService= cierreCajaService;
        this.reciboPdfService = reciboPdfService;
        this.cierreCajaPdfService= cierreCajaPdfService;
        this.infoEmpleadoService = infoEmpleadoService;
    }


    @GetMapping ("/dashboard")
    public ModelAndView dashboardEmpleado(Authentication authentication){
        String username= authentication.getName();
        ModelAndView mAV= new ModelAndView(ViewRouteHelper.EMPLEADO_DASHBOARD);
        Staff staff= staffService.buscarPorNombre(username);
        UnidadDeVenta unidad = staff.getUnidadDeVenta();
        Festival festival = unidad != null ? unidad.getFestival() : null;

        // CU-20: Cronograma de trabajo.
        // Hay que trabajar hoy si el empleado está activo, el festival está activo
        // y la fecha de hoy cae dentro del rango del festival.
        LocalDate hoy = LocalDate.now();
        boolean trabajaHoy = staff.isActivo()
                && festival != null
                && festival.isActivo()
                && festival.getFechaInicio() != null
                && festival.getFechaFin() != null
                && !hoy.isBefore(festival.getFechaInicio())
                && !hoy.isAfter(festival.getFechaFin());

                int anios = Period.between(staff.getFechaIngreso(), LocalDate.now()).getYears();
        mAV.addObject("staff", staff);
        mAV.addObject("esCajero", staff instanceof Cajero);
        mAV.addObject("esCocinero", staff instanceof Cocinero);
        mAV.addObject("antiguedad", anios);
        mAV.addObject("unidad", unidad);
        mAV.addObject("festival", festival);
        mAV.addObject("trabajaHoy", trabajaHoy);
        mAV.addObject("hoy", hoy);
        return mAV;
    }

    // VER MI INFORMACION: datos del empleado, festivales y unidades en las que trabajó
    @GetMapping("/mi-informacion")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ModelAndView miInformacion(Authentication authentication) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.EMPLEADO_INFO);
        try {
            InfoEmpleadoDTO info = infoEmpleadoService.obtenerPorUsername(authentication.getName());
            mAV.addObject("info", info);
        } catch (IllegalArgumentException e) {
            mAV.addObject("error", e.getMessage());
        }
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

    @PostMapping("/pedidos/{id}/entregar")
public ModelAndView entregarPedido(
        @org.springframework.web.bind.annotation.PathVariable Long id,
        Authentication authentication,
        RedirectAttributes flash) {

    try {
        pedidoService.entregarPedido(
                id,
                authentication.getName()
        );

        flash.addFlashAttribute(
                "exito",
                "El pedido fue entregado correctamente."
        );

    } catch (IllegalArgumentException e) {

        flash.addFlashAttribute("error", e.getMessage());
    }

    return new ModelAndView("redirect:/empleado/pedidos");
}

    @GetMapping("/cerrar-caja")
    public ModelAndView cerrarCaja(Authentication authentication) {
        Staff staff = staffService.buscarPorNombre(authentication.getName());
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.EMPLEADO_CERRAR_CAJA);
        mAV.addObject("unidad", staff.getUnidadDeVenta());
        mAV.addObject("resumen", cierreCajaService.resumenDelDia(staff.getUnidadDeVenta()));
        return mAV;
    }

@PostMapping("/cerrar-caja")
public ModelAndView confirmarCierre(Authentication authentication,
                                    HttpServletResponse response,
                                    RedirectAttributes flash) throws IOException {
    Staff staff = staffService.buscarPorNombre(authentication.getName());
    try {
        CierreCaja cierre = cierreCajaService.cerrar(staff);
    enviarPdf(response, cierre);        
    return null; 
    } catch (IllegalArgumentException e) {
        flash.addFlashAttribute("error", e.getMessage());
        return new ModelAndView("redirect:/empleado/cerrar-caja");
    }
}

@GetMapping("/cerrar-caja/pdf")
public ModelAndView descargarPdf(Authentication authentication,
                                 HttpServletResponse response,
                                 RedirectAttributes flash) throws IOException {
    Staff staff = staffService.buscarPorNombre(authentication.getName());
    try {
        enviarPdf(response, cierreCajaService.cierreDeHoy(staff.getUnidadDeVenta()));
        return null;
    } catch (IllegalArgumentException e) {
        flash.addFlashAttribute("error", e.getMessage());
        return new ModelAndView("redirect:/empleado/cerrar-caja");
    }
        }

        private void enviarPdf(HttpServletResponse response, CierreCaja cierre) throws IOException {
    byte[] pdf = cierreCajaPdfService.generar(cierre,
            cierreCajaService.pedidosDelDia(cierre.getUnidadDeVenta()));

    response.setContentType(MediaType.APPLICATION_PDF_VALUE);
    response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"cierre-caja-" + cierre.getFecha() + ".pdf\"");
    response.setContentLength(pdf.length);
    response.getOutputStream().write(pdf);
}
    // CONSULTAR RECIBO DE SUELDO
    @GetMapping("/sueldos")
    public ModelAndView sueldos(Authentication authentication) {

        Staff staff = staffService.buscarPorNombre(authentication.getName());

        if (staff == null || !staff.isActivo()) {
            throw new IllegalArgumentException(
                    "No se encontró un empleado activo.");
        }

        UnidadDeVenta unidad = staff.getUnidadDeVenta();

        if (unidad == null || unidad.getFestival() == null) {
            throw new IllegalArgumentException(
                    "No tenés un festival asociado a tu unidad de venta.");
        }

        Festival festival = unidad.getFestival();

        LiquidacionDTO liquidacion = staffService.liquidarYActualizarSueldo(staff, festival);

        ModelAndView mAV = new ModelAndView("empleado/sueldos");

        mAV.addObject("staff", staff);
        mAV.addObject("festival", festival);
        mAV.addObject("liquidacion", liquidacion);

        return mAV;
    }


    // DESCARGAR RECIBO EN PDF
        @GetMapping("/sueldos/pdf")
    public ResponseEntity<byte[]> descargarRecibo(Authentication authentication) {

        Staff staff = staffService.buscarPorNombre(authentication.getName());

        if (staff == null || !staff.isActivo()) {
            throw new IllegalArgumentException(
                "No se encontró un empleado activo.");
        }

        UnidadDeVenta unidad = staff.getUnidadDeVenta();

        if (unidad == null || unidad.getFestival() == null) {
            throw new IllegalArgumentException(
                "No tenés un festival asociado a tu unidad de venta.");
        }

        Festival festival = unidad.getFestival();

    LiquidacionDTO liquidacion = staffService.liquidarYActualizarSueldo(staff, festival);

    byte[] pdf = reciboPdfService.generar(staff, festival, liquidacion);

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recibo-sueldo.pdf").contentType(MediaType.APPLICATION_PDF).body(pdf);
    }

}
