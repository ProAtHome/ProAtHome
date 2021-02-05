package com.proathome.REST;

import com.google.gson.Gson;
import com.proathome.controladores.ControladorCliente;
import com.proathome.controladores.ControladorExamenDiagnostico;
import com.proathome.controladores.ControladorRutaAprendizaje;
import com.proathome.controladores.ControladorSesion;
import com.proathome.modelos.ObjetoUbicacion;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/apiProAtHome/cliente")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON) 
public class RESTCliente {

    /*
    *
    * Métodos (Servicios) de Clientes. 
    *
     */
    private ControladorCliente cliente = new ControladorCliente();
    private ControladorSesion sesiones = new ControladorSesion();
    private ObjetoUbicacion objetoUbicaciones = new ObjetoUbicacion();
    private ControladorExamenDiagnostico examen = new ControladorExamenDiagnostico();
    private ControladorRutaAprendizaje ruta = new ControladorRutaAprendizaje();
    private JSONParser parser = new JSONParser();
    private Gson gson = new Gson();
    
    @GET
    @Path("/getReportes/{idEstudiante}")
    public JSONObject getReportes(@PathParam("idEstudiante") int idEstudiante){
        return cliente.getReportes(idEstudiante);
    }
    
    @GET
    @Path("/estatusDocumentos/{idCliente}")
    public JSONObject estatusDocumentos(@PathParam("idCliente") int idCliente){
        return cliente.estatusDocumentos(idCliente);
    }
    
    @GET
    @Path("/finalizarTicket/{idTicket}")
    public void solicitudTicketFinalizado(@PathParam("idTicket") int idTicket){
        cliente.finalizarTicket(idTicket);
    }
    
    @GET
    @Path("/obtenerTickets/{idEstudiante}")
    public JSONArray obtenerTickets(@PathParam("idEstudiante") int idEstudiante){
        return cliente.obtenerTickets(idEstudiante);
    }
    
    @GET
    @Path("/bloquearPerfil/{idEstudiante}")
    public JSONObject bloquearPerfil(@PathParam("idEstudiante") int idEstudiante){
        return cliente.bloquearPerfil(idEstudiante);
    }
    
    @GET
    @Path("/validarValoracion/{idSesion}/{idProfesor}")
    public JSONObject validarValoracion(@PathParam("idSesion") int idSesion, @PathParam("idProfesor") int idProfesor){
        return cliente.validarValoracion(idSesion, idProfesor);
    }
    
    @GET
    @Path("/obtenerValoracion/{idProfesor}")
    public JSONArray obtenerValoracion(@PathParam("idProfesor") int idProfesor){
        return cliente.obtenerValoracion(idProfesor);
    }
    
    @GET
    @Path("/verificarPlan/{idEstudiante}")
    public JSONObject verificarPlan(@PathParam("idEstudiante") int idEstudiante){
        return cliente.verificarPlan(idEstudiante);        
    }
    
    @GET
    @Path("/verificarSesionesPagadas/{idEstudiante}")
    public JSONObject verificarSesionesPagadas(@PathParam("idEstudiante") int idEstudiante){  
        return cliente.verificarSesionesPagadas(idEstudiante);     
    }
    
    @GET
    @Path("/obtenerToken/{idSesion}/{idEstudiante}")
    public JSONObject obtenerToken(@PathParam("idSesion") int idSesion, @PathParam("idEstudiante") int idEstudiante){  
        return cliente.obtenerToken(idSesion, idEstudiante);
    }
    
    @GET
    @Path("/obtenerPreOrden/{idEstudiante}/{idSesion}")
    public JSONObject obtenerPreOrden(@PathParam("idEstudiante") int idEstudiante, @PathParam("idSesion") int idSesion){       
        return cliente.obtenerPreOrden(idEstudiante, idSesion); 
    }
    
    @GET
    @Path("/validarClaseFinalizada/{idSesion}/{idEstudiante}")
    public JSONObject validarClaseFinalizada(@PathParam("idSesion") int idSesion, @PathParam("idEstudiante") int idEstudiante){      
        return sesiones.validarClaseFinalizadaEstudiante(idSesion, idEstudiante);     
    }
    
    @GET
    @Path("/validarEstatusClase/{idSesion}/{idEstudiante}")
    public JSONObject validarEstatusClase(@PathParam("idSesion") int idSesion, @PathParam("idEstudiante") int idEstudiante){
        return sesiones.validarEstatusClaseEstudiante(idSesion, idEstudiante);      
    }
    
    @GET
    @Path("/sincronizarClase/{idSesion}/{idEstudiante}")
    public JSONObject sincronizarClase(@PathParam("idSesion") int idSesion, @PathParam("idEstudiante") int idEstudiante){       
       return sesiones.sincronizarClase(idSesion, idEstudiante);      
    }
    
    @GET
    @Path("/obtenerSesionActual/{idEstudiante}")
    public JSONObject obtenerRutaAprendizaje(@PathParam("idEstudiante") int idEstudiante){     
        return ruta.obtenerSesionActual(idEstudiante);   
    }
    
    @GET
    @Path("estadoRutaAprendizaje/{idCliente}/{tipo}")
    public JSONObject estadoRutaAprendizaje(@PathParam("idCliente") int idCliente, @PathParam("tipo") int tipo){    
        return ruta.estadoRutaAprendizaje(idCliente, tipo); 
    }
    
    @GET
    @Path("/estatusExamenDiagnostico/{idCliente}")
    public JSONObject estatusExamenDiagnostico(@PathParam("idCliente") int idCliente){      
        return examen.estatusExamenDiagnostico(idCliente);    
    }//Fin método enCursoExamenDiagnostico.
    
    @GET
    @Path("infoExamenDiagnostico/{idCliente}")
    public JSONObject infoExamenDiagnostico(@PathParam("idCliente") int idCliente){     
        return examen.infoExamenDiagnostico(idCliente);     
    }//Fin método infoExamenDiagnostico.
    
    @GET
    @Path("infoExamenDiagnosticoFinal/{idCliente}")
    public JSONObject infoExamenDiagnosticoFinal(@PathParam("idCliente") int idCliente){ 
        return examen.infoExamenDiagnosticoFinal(idCliente);    
    }//Fin método infoExamenDiagnostico.
    
    @GET
    @Path("/obtenerSesionesMaps/{idSesion}")
    public String obtenerSesionesMaps(@PathParam("idSesion") int idSesion){
        objetoUbicaciones.obtenerSesionesMaps(idSesion);
        String jsonUbicaciones = gson.toJson(objetoUbicaciones);
        
        return jsonUbicaciones;
    }//Fin método obtenerSesionesMaps.

    @GET
    @Path("/sesionCliente/{correo}/{contrasena}")
    public String sesionCliente(@PathParam("correo") String correo, @PathParam("contrasena") String contrasena) {
        cliente.iniciarSesion(correo, contrasena);
        return gson.toJson(cliente.datosSesion());
    }//Fin método sesionCliente.

    @GET
    @Path("/perfilCliente/{idCliente}")
    public String perfilCliente(@PathParam("idCliente") int idCliente) {
        cliente.perfilCliente(idCliente);
        String perfil = gson.toJson(cliente.datosSesion());

        return perfil;
    }//Fin método perfilCliente.
    
    @GET
    @Path("/obtenerDatosBancarios/{idCliente}")
    public String obtenerDatosBancarios(@PathParam("idCliente") int idCliente) {
        Gson gson = new Gson();
        String jsonDatos = gson.toJson(cliente.obtenerCuentaBancaria(idCliente));

        return jsonDatos;
    }//Fin método obtenerDatosBancarios.
    
    @GET
    @Path("/obtenerDatosBancariosWeb/{idCliente}")
    public String obtenerDatosBancariosWeb(@PathParam("idCliente") int idCliente) {
        Gson gson = new Gson();
        String jsonDatos = gson.toJson(cliente.obtenerCuentaBancaria(idCliente));

        return jsonDatos;
    }//Fin método obtenerDatosBancarios.

    @GET
    @Path("/detallesSesion/{idSesion}")
    public JSONObject detallesSesion(@PathParam("idSesion") int idSesion){
        return cliente.detallesSesion(idSesion);
    }//Fin método detallesSesion.
    
    @GET
    @Path("/obtenerSesiones/{idCliente}")
    public String obtenerSesiones(@PathParam("idCliente") int idCliente){
        Gson gson = new Gson();
        String jsonArray = "";
        sesiones.obtenerSesiones(idCliente);
        jsonArray = gson.toJson(sesiones);
        
        return jsonArray;
    }//Fin método obtenerSesiones.
    
    @POST
    @Path("/enviarMsgTicket")
    public void enviarMsgTicket(JSONObject jsonDatos){
        cliente.enviarMsgTicket(jsonDatos);
    }
    
    @POST
    @Path("/nuevoTicket")
    public void nueboTicket(JSONObject jsonDatos){
        cliente.nuevoTicket(jsonDatos);
    }
    
    @POST
    @Path("/valorarProfesor")
    public void valorarProfesor(JSONObject jsonDatos){
        cliente.valorarProfesor(jsonDatos);
    }
    
    @POST
    @Path("/generarPlan")
    public void generarPlan(String datos){
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            cliente.generarPlan(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @POST
    @Path("/iniciarPlan")
    public void iniciarPlan(String datos){
    
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            cliente.iniciarPlan(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
    }
    
    @POST()
    @Path("/iniciarProcesoRuta")
    public void iniciarProcesoRuta(String datos){
        try{
            JSONObject json = (JSONObject)parser.parse(datos);
            cliente.iniciarProcesoRuta(json);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @POST
    @Path("/nuevaRuta")
    public void nuevaRuta(String datos){
        try{
            JSONObject json = (JSONObject) parser.parse(datos);
            ruta.nuevaRuta(json);
        }catch(ParseException ex){
            ex.printStackTrace();
        } 
    }
    
    @POST
    @Path("/sumarClaseRuta")
    public void sumarClaseRuta(String datos){
        try{
            JSONObject json = (JSONObject) parser.parse(datos);
            ruta.sumarClaseRuta(json);
        }catch(ParseException ex){
            ex.printStackTrace();
        }  
    }
    
    @POST
    @Path("/rutaEnCurso")
    public JSONObject rutaEnCurso(String datos){
        JSONObject estado = new JSONObject();
        estado.put("estado", ControladorRutaAprendizaje.RUTA_ACTUALIZADA);
        
        try{
            JSONObject rutaJSON = (JSONObject)parser.parse(datos);
            ruta.rutaEnCurso(rutaJSON);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return estado; 
    }

    @POST
    @Path("/actualizarFoto")
    public void actualizarFoto(String datos) {
        try {
            JSONObject jsonFoto = (JSONObject) parser.parse(datos);
            cliente.actualizarFoto(jsonFoto);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }//Fin método actializarFoto.

    @POST
    @Path("/agregarCliente")
    public void agregarCliente(String datos) {
        
        try {
            JSONObject jsonCliente = (JSONObject) parser.parse(datos);
            cliente.nuevoCliente(jsonCliente);
            cliente.guardarCliente();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        System.out.println(datos);
    }//Fin método agregarCliente.
    
    @POST
    @Path("eliminarSesionWeb")
    public void eliminarSesionWeb(String datos){
        try{
            JSONObject eliminar = (JSONObject) parser.parse(datos);
            sesiones.eliminarSesion(eliminar);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @POST
    @Path("eliminarSesion")
    public Response eliminarSesion(JSONObject jsonDatos){
        sesiones.eliminarSesion(jsonDatos);
        
        return Response.ok("Sesión eliminada exitosamente.", MediaType.APPLICATION_JSON).build();
    }
    
    @POST
    @Path("/agregarCuentaBancaria")
    public void agregarCuentaBancaria(String datos) {
        try {
            JSONObject jsonCuenta = (JSONObject) parser.parse(datos);
            cliente.nuevaCuentaBancaria(jsonCuenta);
            cliente.guardarCuentaBancaria(Integer.parseInt(String.valueOf(jsonCuenta.get("idCliente"))));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }//Fin método agregarCuentaBancaria.
    
    @POST
    @Path("/agregarEvaluacion")
    public void agregarEvaluacion(String datos) {
        try {
            JSONObject jsonEvaluacion = (JSONObject) parser.parse(datos);
            cliente.nuevaEvaluacion(jsonEvaluacion);
            cliente.guardarEvaluacion(Integer.parseInt(String.valueOf(jsonEvaluacion.get("idCliente"))));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }//Fin método agregarEvaluacion.

    @POST
    @Path("/agregarSesion")
    public void agregarSesion(String datos) {
        try {
            JSONObject datosJSON = (JSONObject)parser.parse(datos);
            cliente.nuevaSesion(datosJSON);
            cliente.guardarSesion(); 
        }catch(ParseException ex){ 
            ex.printStackTrace();  
        }
    }//Fin método agregarSesion.
    
    @POST
    @Path("/examenDiagnostico")
    public JSONObject examenDiagnostico(String datos){
        JSONObject estatus = new JSONObject();
        estatus.clear();
        
        try{
            JSONObject examenJSON = (JSONObject) parser.parse(datos);
            if(Integer.parseInt(examenJSON.get("estatus").toString()) == ControladorExamenDiagnostico.INICIO){
                examen.inicioExamenDiagnostico(examenJSON);
                estatus.put("estatus", ControladorExamenDiagnostico.EXAMEN_GUARDADO);
            }
            else if(Integer.parseInt(examenJSON.get("estatus").toString()) == ControladorExamenDiagnostico.CANCELADO){
                examen.cancelarExamenDiagnostico(examenJSON);
                estatus.put("estatus", ControladorExamenDiagnostico.CANCELADO);
            }
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return estatus; 
    }//Fin método examenDiagnostico.
    
    @PUT
    @Path("/saldarDeuda")
    public void saldarDeuda(String datos){
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            cliente.saldarDeuda(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @PUT()
    @Path("/actualizarMonedero")
    public void actualizarMonedero(String datos){
        try{
            System.out.println("Llega: " + datos);
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            cliente.actualizarMonedero(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @PUT
    @Path("/actualizarPago")
    public void actualizarPago(String datos){
    
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            cliente.actualizarPago(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    
    }
    
    @PUT
    @Path("/actualizarToken")
    public void guardarToken(String datos){
    
        try{
            JSONObject jsonToken = (JSONObject) parser.parse(datos);
            cliente.actualizarToken(jsonToken);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    
    }
    
    @PUT
    @Path("/activarTE/{idSesion}/{idEstudiante}/{progresoTotal}")
    public void activarTE(@PathParam("idSesion") int idSesion, @PathParam("idEstudiante") int idEstudiante, @PathParam("progresoTotal") int progresoTotal){
        
        sesiones.activarTE(idSesion, idEstudiante, progresoTotal);
        
    }
    
    @PUT
    @Path("/activarTEWeb")
    public void activarTEWeb(String datos){
        
        try{
            JSONObject json = (JSONObject) parser.parse(datos);
            sesiones.activarTE(Integer.parseInt(json.get("idSesion").toString()), Integer.parseInt(json.get("idEstudiante").toString()), Integer.parseInt(json.get("progresoTotal").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
    }
    
    @PUT
    @Path("/finalizarClase/{idSesion}/{idEstudiante}")
    public void finalizarClase(@PathParam("idSesion") int idSesion, @PathParam("idEstudiante") int idEstudiante){
        
        sesiones.finalizarClase(idSesion, idEstudiante);
        
    }
    
    @PUT
    @Path("/finalizarClaseWeb")
    public void finalizarClaseWeb(String datos){
        
        try{
            JSONObject json = (JSONObject)parser.parse(datos);
            sesiones.finalizarClase(Integer.parseInt(json.get("idSesion").toString()), Integer.parseInt(json.get("idEstudiante").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
    }
    
    @PUT
    @Path("/cambiarEstatusClase/{idSesion}/{idEstudiante}/{estatus}")
    public void cambiarEstatusClase(@PathParam("idSesion") int idSesion, @PathParam("idEstudiante") int idEstudiante, @PathParam("estatus") int estatus){
        
        sesiones.cambiarEstatusClaseEstudiante(idSesion, idEstudiante, estatus);
        
    }
    
    @PUT
    @Path("/claseDisponible/{idSesion}/{idEstudiante}/{disponible}")
    public void claseDsiponible(@PathParam("idSesion") int idSesion, @PathParam("idEstudiante") int idEstudiante, @PathParam("disponible") boolean dsiponible){
        
        sesiones.claseDisponible(idSesion, idEstudiante, dsiponible);
        
    }
    
    @PUT
    @Path("/claseDisponibleWeb")
    public void claseDsiponibleWeb(String datos){
        
        try{
            JSONObject json = (JSONObject)parser.parse(datos);
            sesiones.claseDisponible(Integer.parseInt(json.get("idSesion").toString()), Integer.parseInt(json.get("idEstudiante").toString()), Boolean.parseBoolean(json.get("disponible").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
    }
    
    @PUT
    @Path("/reiniciarExamenDiagnostico")
    public JSONObject reiniciarExamenDiagnostico(String datos){
        
        JSONObject estatus = null;
        try{
            JSONObject examenJSON = (JSONObject) parser.parse(datos);
            estatus = examen.reiniciarExamenDiagnostico(examenJSON);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return estatus;
        
    }
    
    @PUT
    @Path("/enCursoExamenDiagnostico")
    public JSONObject enCursoExamenDiagnostico(String datos){
        
        JSONObject estatus = new JSONObject();
        estatus.clear();
        try{
            JSONObject examenJSON = (JSONObject) parser.parse(datos);
            examen.enCursoExamenDiagnostico(examenJSON);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        estatus.put("estatus", ControladorExamenDiagnostico.EXAMEN_GUARDADO);
        
        return estatus;
        
    }//Fin método enCursoExamenDiagnostico.
    
    @PUT
    @Path("/actualizarSesionWeb")
    public void actualizarSesionWeb(String datos){
        
        try{
            
            JSONObject actualizar = (JSONObject) parser.parse(datos);
            sesiones.actualizarSesion(actualizar);
            
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
    }
    
    @PUT
    @Path("/actualizarSesion")
    public Response actualizarSesion(JSONObject jsonDatos){
        
        sesiones.actualizarSesion(jsonDatos);
        
        return Response.ok("Sesión actualizada correctamente", MediaType.APPLICATION_JSON).build();
        
    }

    @PUT
    @Path("/actualizarCuentaClienteWeb")
    public void actualizarCuentaClienteWeb(String datos) {

        try {

            JSONObject jsonCuentaBancaria = (JSONObject) parser.parse(datos);
            cliente.nuevaCuentaBancaria(jsonCuentaBancaria);
            cliente.actualizarCuentaBancaria(Integer.parseInt(String.valueOf(jsonCuentaBancaria.get("idCliente"))));

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

    }//Fin método agregarCuentaBancariaWeb.
    
    @PUT
    @Path("/actualizarCuentaCliente")
    public Response actualizarCuentaCliente(JSONObject jsonDatos) {

        System.out.println(jsonDatos);
        cliente.nuevaCuentaBancaria(jsonDatos);
        cliente.actualizarCuentaBancaria(Integer.parseInt(jsonDatos.get("idCliente").toString()));

        return Response.ok("Actualización exitosa.", MediaType.APPLICATION_JSON).build();

    }//Fin método agregarCuentaBancaria.
    
    @PUT
    @Path("/informacionPerfilWeb")
    public void actualizaDatosPerfilWeb(String datos) {

        try {

            JSONObject datosJSON = (JSONObject) parser.parse(datos);
            cliente.datosActualizarPerfil(datosJSON);
            cliente.actualizarDatosPerfil();

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

    }//Fin método informacionPerfilWeb.

    @PUT
    @Path("/informacionPerfil")
    public Response actualizaDatosPerfil(String datos) {

        try {

            JSONObject datosJSON = (JSONObject) parser.parse(datos);
            cliente.datosActualizarPerfil(datosJSON);
            cliente.actualizarDatosPerfil();

        } catch (ParseException ex) {

            ex.printStackTrace();

        }
        
        return Response.ok("Actualización exitosa", MediaType.APPLICATION_JSON).build();

    }//Fin método actualizarInfoPerfil.

}
