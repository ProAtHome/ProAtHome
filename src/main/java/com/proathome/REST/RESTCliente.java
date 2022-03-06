package com.proathome.REST;

import com.google.gson.Gson;
import com.proathome.controladores.ControladorCliente;
import com.proathome.controladores.ControladorExamenDiagnostico;
import com.proathome.controladores.ControladorRutaAprendizaje;
import com.proathome.controladores.ControladorSesion;
import com.proathome.controladores.JWTController;
import com.proathome.modelos.ObjetoUbicacion;
import java.util.logging.Level;
import java.util.logging.Logger;
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

@Path("/cliente")
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
    @Path("/getInicioSesion/{idCliente}/{token}")
    public String getInfoSesion(@PathParam("idCliente") int idCliente, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.getInicioSesion(idCliente).toString();
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/iniciarPerfil/{idCliente}/{token}")
    public String iniciarPerfil(@PathParam("idCliente") int idCliente, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.iniciarPerfil(idCliente).toJSONString();
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/validarTokenSesion/{idCliente}/{token}")
    public String validarTokenSesion(@PathParam("idCliente") int idCliente, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE)){
            JSONObject valido = new JSONObject();
            valido.put("respuesta", true);
            
            return valido.toJSONString();
        }else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/getVerificacion/{token}/{correo}")//NO REQUIERE VERIFICACION TOKEN JWT
    public String getVerificacion(@PathParam("token") String token, @PathParam("correo") String correo){
        return cliente.getVerificacion(token, correo).toJSONString();
    }
    
    @GET
    @Path("/getDisponibilidadServicio/{idCliente}/{token}")
    public String getDisponibilidadServicios(@PathParam("idCliente") int idCliente, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.getDisponibilidadServicio(idCliente).toJSONString();      
        else
            return JWTController.getInstance().getError().toJSONString();

    }
    
    @GET
    @Path("/getDatosFiscales/{idCliente}/{token}")
    public String getDatosFiscales(@PathParam("idCliente") int idCliente, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.getDatosFiscales(idCliente).toJSONString();     
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/getReportes/{idCliente}/{token}")
    public String getReportes(@PathParam("idCliente") int idCliente, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.getReportes(idCliente).toJSONString();    
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/estatusDocumentos/{idCliente}/{token}")
    public String estatusDocumentos(@PathParam("idCliente") int idCliente, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.estatusDocumentos(idCliente).toJSONString();  
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/finalizarTicket/{idTicket}")//NO REQUIERE VERIFICACION TOKEN JWT
    public void solicitudTicketFinalizado(@PathParam("idTicket") int idTicket){
        cliente.finalizarTicket(idTicket);
    }
    
    @GET
    @Path("/obtenerTickets/{idCliente}/{token}")
    public String obtenerTickets(@PathParam("idCliente") int idCliente,  @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.obtenerTickets(idCliente).toJSONString();  
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/bloquearPerfil/{idCliente}/{token}")
    public String bloquearPerfil(@PathParam("idCliente") int idCliente, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.bloquearPerfil(idCliente).toJSONString(); 
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/validarValoracion/{idSesion}/{idProfesional}")//NO REQUIERE VERIFICACION JWT
    public String validarValoracion(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional){
        return cliente.validarValoracion(idSesion, idProfesional).toJSONString();
    }
    
    @GET
    @Path("/obtenerValoracion/{idProfesional}")//NO REQUIERE VERIFICACION JWT
    public String obtenerValoracion(@PathParam("idProfesional") int idProfesional){
        return cliente.obtenerValoracion(idProfesional).toJSONString();
    }
    //SEGUNDO SET
    @GET
    @Path("/verificarPlan/{idCliente}/{token}")
    public String verificarPlan(@PathParam("idCliente") int idCliente, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.verificarPlan(idCliente).toJSONString();        
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/verificarSesionesPagadas/{idCliente}/{token}")
    public String verificarSesionesPagadas(@PathParam("idCliente") int idCliente, @PathParam("token") String token){  
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.verificarSesionesPagadas(idCliente).toJSONString();           
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/obtenerToken/{idSesion}/{idCliente}/{token}")//SIN USO
    public String obtenerToken(@PathParam("idSesion") int idSesion, @PathParam("idCliente") int idCliente, @PathParam("token") String token){ 
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.obtenerToken(idSesion, idCliente).toJSONString();           
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/obtenerPreOrden/{idCliente}/{idSesion}/{token}")
    public String obtenerPreOrden(@PathParam("idCliente") int idCliente, @PathParam("idSesion") int idSesion, @PathParam("token") String token){   
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return cliente.obtenerPreOrden(idCliente, idSesion).toJSONString();            
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/validarServicioFinalizada/{idSesion}/{idCliente}/{token}")
    public String validarServicioFinalizada(@PathParam("idSesion") int idSesion, @PathParam("idCliente") int idCliente, @PathParam("token") String token){ 
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return sesiones.validarServicioFinalizadaCliente(idSesion, idCliente).toJSONString();            
        else
            return JWTController.getInstance().getError().toJSONString();     
    }
    
    @GET
    @Path("/validarEstatusServicio/{idSesion}/{idCliente}/{token}")
    public String validarEstatusServicio(@PathParam("idSesion") int idSesion, @PathParam("idCliente") int idCliente, @PathParam("token") String token){    
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return sesiones.validarEstatusServicioCliente(idSesion, idCliente).toJSONString();            
        else
            return JWTController.getInstance().getError().toJSONString();  
    }
    
    @GET
    @Path("/sincronizarServicio/{idSesion}/{idCliente}/{token}")
    public String sincronizarServicio(@PathParam("idSesion") int idSesion, @PathParam("idCliente") int idCliente, @PathParam("token") String token){   
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return sesiones.sincronizarServicio(idSesion, idCliente).toJSONString();             
        else
            return JWTController.getInstance().getError().toJSONString();       
    }
    
    @GET
    @Path("/obtenerSesionActual/{idCliente}/{token}")
    public String obtenerRutaAprendizaje(@PathParam("idCliente") int idCliente, @PathParam("token") String token){   
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return ruta.obtenerSesionActual(idCliente).toJSONString();            
        else
            return JWTController.getInstance().getError().toJSONString();    
    }
    
    @GET
    @Path("estadoRutaAprendizaje/{idCliente}/{tipo}/{token}")
    public String estadoRutaAprendizaje(@PathParam("idCliente") int idCliente, @PathParam("tipo") int tipo, @PathParam("token") String token){  
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return ruta.estadoRutaAprendizaje(idCliente, tipo).toJSONString();            
        else
            return JWTController.getInstance().getError().toJSONString();    
    }
    
    @GET
    @Path("/estatusExamenDiagnostico/{idCliente}/{token}")
    public String estatusExamenDiagnostico(@PathParam("idCliente") int idCliente, @PathParam("token") String token){      
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return examen.estatusExamenDiagnostico(idCliente).toJSONString();            
        else
            return JWTController.getInstance().getError().toJSONString();      
    }//Fin método enCursoExamenDiagnostico.
    
    //SET 3
    @GET
    @Path("infoExamenDiagnostico/{idCliente}/{token}")
    public String infoExamenDiagnostico(@PathParam("idCliente") int idCliente, @PathParam("token") String token){    
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return examen.infoExamenDiagnostico(idCliente).toJSONString();           
        else
            return JWTController.getInstance().getError().toJSONString();      
    }//Fin método infoExamenDiagnostico.
    
    @GET
    @Path("infoExamenDiagnosticoFinal/{idCliente}/{token}")
    public String infoExamenDiagnosticoFinal(@PathParam("idCliente") int idCliente, @PathParam("token") String token){ 
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE))
            return examen.infoExamenDiagnosticoFinal(idCliente).toJSONString();            
        else
            return JWTController.getInstance().getError().toJSONString();     
    }//Fin método infoExamenDiagnostico.
    
    @GET
    @Path("/obtenerSesionesMaps/{idSesion}")//NO REQUERIMOS TOKEN
    public String obtenerSesionesMaps(@PathParam("idSesion") int idSesion){
        objetoUbicaciones.obtenerSesionesMaps(idSesion);
        String jsonUbicaciones = gson.toJson(objetoUbicaciones);
        
        return jsonUbicaciones;
    }//Fin método obtenerSesionesMaps.

    @GET
    @Path("/sesionCliente/{correo}/{contrasena}")//NO REQUERIMOS TOKEN
    public String sesionCliente(@PathParam("correo") String correo, @PathParam("contrasena") String contrasena) {
        cliente.iniciarSesion(correo, contrasena);
        return gson.toJson(cliente.datosSesion());
    }//Fin método sesionCliente.

    @GET
    @Path("/perfilCliente/{idCliente}/{token}")
    public String perfilCliente(@PathParam("idCliente") int idCliente, @PathParam("token") String token) {
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE)){
            cliente.perfilCliente(idCliente);
            String perfil = gson.toJson(cliente.datosSesion());
            
            return perfil;         
        }else
            return null;  
    }//Fin método perfilCliente.
    
    @GET
    @Path("/obtenerDatosBancarios/{idCliente}/{token}")
    public String obtenerDatosBancarios(@PathParam("idCliente") int idCliente, @PathParam("token") String token) {
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE)){
           return cliente.obtenerCuentaBancaria(idCliente).toJSONString();          
        }else
            return JWTController.getInstance().getError().toJSONString();  
    }//Fin método obtenerDatosBancarios.
    

    @GET
    @Path("/detallesSesion/{idSesion}")//NO REQUERIMOS TOKEN
    public String detallesSesion(@PathParam("idSesion") int idSesion){
        return cliente.detallesSesion(idSesion).toJSONString();
    }//Fin método detallesSesion.
    
    @GET
    @Path("/obtenerSesiones/{idCliente}/{token}")
    public String obtenerSesiones(@PathParam("idCliente") int idCliente, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idCliente), JWTController.PERFIL_CLIENTE)){
            Gson gson = new Gson();
            String jsonArray = "";
            sesiones.obtenerSesiones(idCliente);
            jsonArray = gson.toJson(sesiones);
            JSONObject sesiones = null;
            try {
                sesiones = (JSONObject) parser.parse(jsonArray);
            } catch (ParseException ex) {
                Logger.getLogger(RESTCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            JSONObject respuesta = new JSONObject();
            respuesta.put("respuesta", true);
            respuesta.put("mensaje", sesiones);
            
            return respuesta.toJSONString();    
        }else
            return JWTController.getInstance().getError().toJSONString();  
    }//Fin método obtenerSesiones.
    
    @POST
    @Path("/guardarDatosFiscales")
    public String guardarDatosFiscales(String datos){
        JSONObject respuesta = null;
        
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = cliente.guardarDatosFiscales(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }
    
    @POST
    @Path("/enviarMsgTicket")
    public void enviarMsgTicket(String jsonDatos){
        try{
            JSONObject json = (JSONObject) parser.parse(jsonDatos);
            cliente.enviarMsgTicket(json);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @POST
    @Path("/nuevoTicket")
    public void nueboTicket(String jsonDatos){
        try{
            JSONObject json = (JSONObject) parser.parse(jsonDatos);
            cliente.nuevoTicket(json);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @POST
    @Path("/valorarProfesional")
    public void valorarProfesional(String jsonDatos){
        try{
            JSONObject json = (JSONObject) parser.parse(jsonDatos);
            cliente.valorarProfesional(json);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
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
            int idCliente = Integer.parseInt(json.get("idCliente").toString());
            cliente.iniciarProcesoRuta(idCliente);
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
    @Path("/sumarServicioRuta")
    public String sumarServicioRuta(String datos){
        JSONObject json = null;
        try{
            json = (JSONObject) parser.parse(datos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }  
        
        return ruta.sumarServicioRuta(json).toJSONString();
    }
    
    @POST
    @Path("/rutaEnCurso")
    public String rutaEnCurso(String datos){
        JSONObject estado = new JSONObject();
        estado.put("estado", ControladorRutaAprendizaje.RUTA_ACTUALIZADA);
        
        try{
            JSONObject rutaJSON = (JSONObject)parser.parse(datos);
            ruta.rutaEnCurso(rutaJSON);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return estado.toJSONString(); 
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
    public String agregarCliente(String datos) {
        JSONObject respuesta = new JSONObject();
        try {
            JSONObject jsonCliente = (JSONObject) parser.parse(datos);
            cliente.nuevoCliente(jsonCliente);
            respuesta = cliente.guardarCliente();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }//Fin método agregarCliente.
    
    @POST
    @Path("/eliminarSesion")
    public String eliminarSesion(String jsonDatos){
        JSONObject respuesta = null;
        try{
            JSONObject json = (JSONObject) parser.parse(jsonDatos);
            respuesta = sesiones.eliminarSesion(json);
        }catch(ParseException e){
            e.printStackTrace();
        }
        
        return respuesta.toJSONString();
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

    /*
    @POST
    @Path("/agregarSesion")
    public String agregarSesion(String datos) {
        JSONObject respuesta = null;
        try {
            JSONObject datosJSON = (JSONObject)parser.parse(datos);
            cliente.nuevaSesion(datosJSON);
            //respuesta = cliente.guardarSesion(); 
        }catch(ParseException ex){ 
            ex.printStackTrace();  
        }
        
        return respuesta.toJSONString();
    }//Fin método agregarSesion.*/
    
    @POST
    @Path("/examenDiagnostico")
    public String examenDiagnostico(String datos){
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
        
        return estatus.toJSONString(); 
    }//Fin método examenDiagnostico.
    
    @POST
    @Path("/registrarServicio")
    public String registrarServicio(String datos){
        JSONObject data = null;
        JSONObject respuesta = null;
        try {
            data = (JSONObject) parser.parse(datos);
            respuesta = cliente.registrarServicio(data);
        } catch (ParseException ex) {
            Logger.getLogger(RESTCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return respuesta.toJSONString();
    }
    
    @PUT
    @Path("/actualizarPass")
    public String actualizarPass(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = cliente.actualizarPass(jsonDatos);
        }catch(ParseException ex){          
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
        
    }
    
    @PUT
    @Path("/sumarMonedero")
    public String sumarMonedero(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = cliente.sumarMonedero(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }
    
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
    @Path("/guardarTokenPagoServicio")
    public String guardarToken(String datos){
        JSONObject respuesta  = null;
        try{
            JSONObject jsonToken = (JSONObject) parser.parse(datos);
            respuesta = cliente.guardarTokenPagoServicio(jsonToken);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }
    
    @PUT
    @Path("/actualizarPagoTE")
    public String actualizarPagoTE(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = cliente.actualizarPagoTE(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }
    
    @PUT
    @Path("/activarTE/{idSesion}/{idCliente}/{progresoTotal}")
    public void activarTE(@PathParam("idSesion") int idSesion, @PathParam("idCliente") int idCliente, @PathParam("progresoTotal") int progresoTotal){
        
        sesiones.activarTE(idSesion, idCliente, progresoTotal);
        
    }
    
    @PUT
    @Path("/activarTEWeb")
    public void activarTEWeb(String datos){
        
        try{
            JSONObject json = (JSONObject) parser.parse(datos);
            sesiones.activarTE(Integer.parseInt(json.get("idSesion").toString()), Integer.parseInt(json.get("idCliente").toString()), Integer.parseInt(json.get("progresoTotal").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
    }
    
    @PUT
    @Path("/finalizarServicio/{idSesion}/{idCliente}")
    public void finalizarServicio(@PathParam("idSesion") int idSesion, @PathParam("idCliente") int idCliente){
        
        sesiones.finalizarServicio(idSesion, idCliente);
        
    }
    
    @PUT
    @Path("/finalizarServicioWeb")
    public void finalizarServicioWeb(String datos){
        
        try{
            JSONObject json = (JSONObject)parser.parse(datos);
            sesiones.finalizarServicio(Integer.parseInt(json.get("idSesion").toString()), Integer.parseInt(json.get("idCliente").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
    }
    
    @PUT
    @Path("/cambiarEstatusServicio/{idSesion}/{idCliente}/{estatus}")
    public void cambiarEstatusServicio(@PathParam("idSesion") int idSesion, @PathParam("idCliente") int idCliente, @PathParam("estatus") int estatus){
        
        sesiones.cambiarEstatusServicioCliente(idSesion, idCliente, estatus);
        
    }
    
    @PUT
    @Path("/servicioDisponible/{idSesion}/{idCliente}/{disponible}")
    public void servicioDsiponible(@PathParam("idSesion") int idSesion, @PathParam("idCliente") int idCliente, @PathParam("disponible") boolean dsiponible){
        System.out.println(idSesion);
        sesiones.servicioDisponible(idSesion, idCliente, dsiponible);
        
    }
    
    @PUT
    @Path("/servicioDisponibleWeb")
    public void servicioDsiponibleWeb(String datos){
        
        try{
            JSONObject json = (JSONObject)parser.parse(datos);
            sesiones.servicioDisponible(Integer.parseInt(json.get("idSesion").toString()), Integer.parseInt(json.get("idCliente").toString()), Boolean.parseBoolean(json.get("disponible").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
    }
    
    @PUT
    @Path("/reiniciarExamenDiagnostico")
    public String reiniciarExamenDiagnostico(String datos){
        
        JSONObject estatus = null;
        try{
            JSONObject examenJSON = (JSONObject) parser.parse(datos);
            estatus = examen.reiniciarExamenDiagnostico(examenJSON);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return estatus.toJSONString();
        
    }
    
    @PUT
    @Path("/enCursoExamenDiagnostico")
    public String enCursoExamenDiagnostico(String datos){
        
        JSONObject estatus = new JSONObject();
        estatus.clear();
        try{
            JSONObject examenJSON = (JSONObject) parser.parse(datos);
            examen.enCursoExamenDiagnostico(examenJSON);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        estatus.put("estatus", ControladorExamenDiagnostico.EXAMEN_GUARDADO);
        
        return estatus.toJSONString();
        
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
    public Response actualizarSesion(String jsonDatos){
        try{
            JSONObject json = (JSONObject) parser.parse(jsonDatos);
            sesiones.actualizarSesion(json);
        }catch(ParseException e){
            e.printStackTrace();
        }    
        
        return Response.ok("Sesión actualizada correctamente", MediaType.APPLICATION_JSON).build();
        
    }
    
    @PUT
    @Path("/actualizarCuentaCliente")
    public String actualizarCuentaCliente(String datos) {
        JSONObject respuesta = new JSONObject();
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            cliente.nuevaCuentaBancaria(jsonDatos);
            respuesta = cliente.actualizarCuentaBancaria(Integer.parseInt(jsonDatos.get("idCliente").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }//Fin método agregarCuentaBancaria.

    @PUT
    @Path("/actualizarPerfil")
    public String actualizaDatosPerfil(String datos) {
        JSONObject respuesta = null;
        try {
            JSONObject datosJSON = (JSONObject) parser.parse(datos);
            cliente.datosActualizarPerfil(datosJSON);
            respuesta = cliente.actualizarDatosPerfil();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }//Fin método actualizarInfoPerfil.

}
