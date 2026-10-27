package com.example.unla.grupo7.helper;

public class ViewRouteHelper {
	// INICIO
	public final static String INDEX = "index";

	// USER
	public final static String LOGIN = "user/login";
	public final static String LOGOUT = "user/logout";
	public final static String REGISTRO = "registro";
	public final static String REGISTRO_EXITO = "user/registro-exito";

	// ADMINISTRADOR

	// RESPONSABLE
	public final static String RESPONSABLE_DASHBOARD = "responsable/dashboard";
    public static final String VISTA_LISTA = "responsable/platos";
    public static final String VISTA_FORM = "responsable/plato-form";
    public static final String REDIRECT_LISTA = "redirect:/responsable/platos";
    public final static String RESPONSABLE_RENTABILIDAD = "responsable/rentabilidad";

	// EMPLEADO
	public final static String EMPLEADO_DASHBOARD = "empleado/dashboard";
	public final static String EMPLEADO_PEDIDOS= "empleado/lista-pedidos";
	public final static String EMPLEADO_NUEVOPEDIDO= "empleado/cargar-pedido";
	

}
