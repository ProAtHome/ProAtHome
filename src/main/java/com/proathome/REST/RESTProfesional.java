package com.proathome.REST;

import com.google.gson.Gson;
import com.proathome.controladores.ControladorProfesional;
import com.proathome.controladores.ControladorSesion;
import com.proathome.controladores.JWTController;
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
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/profesional")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RESTProfesional {

    /*
    *
    * Métodos (Servicios) de Profesionales. 
    *
    */
    
    private ControladorSesion sesiones = new ControladorSesion();
    private ControladorProfesional profesional = new ControladorProfesional();
    private ObjetoUbicacion objetoUbicaciones = new ObjetoUbicacion();
    private JSONParser parser = new JSONParser();
    private Gson gson = new Gson();
    
    @GET
    @Path("/getServicios/{rango}/{idProfesional}")
    public String getServicios(@PathParam("rango") int rango, @PathParam("idProfesional") int idProfesional){
        return profesional.getServicios(rango, idProfesional).toJSONString();
    }
    
    //SET 1
    @GET
    @Path("/validarTokenSesion/{idProfesional}/{token}")
    public String validarTokenSesion(@PathParam("idProfesional") int idProfesional, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL)){
            JSONObject valido = new JSONObject();
            valido.put("respuesta", true);
            
            return valido.toJSONString();
        }else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/getVerificacion/{token}/{correo}")//NO REQUIETE JWT
    public String getVerificacion(@PathParam("token") String token, @PathParam("correo") String correo){
        return profesional.getVerificacion(token, correo).toJSONString();
    }
    
    @GET
    @Path("/solicitudEliminarSesion/{idSesion}/{idProfesional}/{token}")
    public String solicitudEliminarSesion(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL))
            return profesional.solicitudEliminarSesion(idSesion, idProfesional).toJSONString();
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/getDatosFiscales/{idProfesional}/{token}")
    public String getDatosFiscales(@PathParam("idProfesional") int idProfesional, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL))
            return profesional.getDatosFiscales(idProfesional).toJSONString();
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/getReportes/{idProfesional}/{token}")
    public String getReportes(@PathParam("idProfesional") int idProfesional, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL))
            return profesional.getReportes(idProfesional).toJSONString();
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/obtenerCita/{idProfesional}/{token}")
    public String obtenerCita(@PathParam("idProfesional") int idProfesional, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL))
            return profesional.obtenerCita(idProfesional).toJSONString();
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/estatusDocumentos/{idProfesional}/{token}")
    public String estatusDocumentos(@PathParam("idProfesional") int idProfesional, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL))
            return profesional.estatusDocumentos(idProfesional).toJSONString();
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/finalizarTicket/{idTicket}")//NO REQUIERE JWT
    public void solicitudTicketFinalizado(@PathParam("idTicket") int idTicket){
        profesional.finalizarTicket(idTicket);
    }
    
    @GET
    @Path("/obtenerTickets/{idProfesional}/{token}")
    public String obtenerTickets(@PathParam("idProfesional") int idProfesional, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL))
            return profesional.obtenerTickets(idProfesional).toJSONString();
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/validarValoracion/{idSesion}/{idCliente}")//NO REQUIERE JWT
    public String validarValoracion(@PathParam("idSesion") int idSesion, @PathParam("idCliente") int idCliente){
        return profesional.validarValoracion(idSesion, idCliente).toJSONString();
    }
    
    //SET 2
    @GET
    @Path("/obtenerValoracion/{idCliente}")//NO REQUIERE VERIFICACION JWT
    public String obtenerValoracion(@PathParam("idCliente") int idCliente){
        return profesional.obtenerValoracion(idCliente).toJSONString();
    }
    
    @GET
    @Path("/validarServicioFinalizada/{idSesion}/{idProfesional}/{token}")
    public String validarServicioFinalizada(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional, @PathParam("token") String token){   
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL))
            return sesiones.validarServicioFinalizada(idSesion, idProfesional).toJSONString();
        else
            return JWTController.getInstance().getError().toJSONString();
    }
    
    @GET
    @Path("/validarEstatusServicio/{idSesion}/{idProfesional}/{token}")
    public String validarEstatusServicio(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL))
            return sesiones.validarEstatusServicioProfesional(idSesion, idProfesional).toJSONString();
        else
            return JWTController.getInstance().getError().toJSONString();      
    }
    
    @GET
    @Path("/sincronizarServicio/{idSesion}/{idProfesional}/{token}")
    public String sincronizarServicio(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional, @PathParam("token") String token){    
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL))
            return sesiones.sincronizarServicioProfesional(idSesion, idProfesional).toJSONString();          
        else
            return JWTController.getInstance().getError().toJSONString(); 
    }
    
    @GET
    @Path("/obtenerSesionesProfesionalMatch/{idProfesional}/{token}")
    public String obtenerSesionesProfesionalMatch(@PathParam("idProfesional") int idProfesional, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL))
            return profesional.sesionesMatchProfesional(idProfesional).toJSONString();          
        else
            return JWTController.getInstance().getError().toJSONString(); 
    }//Fin método obtenerSesionesProfesionalMatch.
    
    @GET
    @Path("/obtenerSesionesMaps")//NO REQUIERE JWT
    public String obtenerSesionesMaps(){
        objetoUbicaciones.obtenerSesionesClientes();
        String jsonUbicaciones = gson.toJson(objetoUbicaciones);
        
        return jsonUbicaciones;
    }//Fin método obtenerSesiones.
    
    @GET
    @Path("/obtenerSesionesMovil/{rango}")//NO REQUIERE JWT
    public String obtenerSesionesMovil(@PathParam("rango") int rango){   
        System.out.println(rango);
       return profesional.obtenerSesionesMovil(rango).toJSONString();  
    }//Fin método obtenerSesionesMovil.
    
    @GET
    @Path("/sesionProfesional/{correo}/{contrasena}")//NO REQUIERE JWT
    public String sesionProfesional(@PathParam("correo") String correo, @PathParam("contrasena") String contrasena){  
        profesional.iniciarSesion(correo, contrasena);
        return gson.toJson(profesional.datosSesion());  
    }//Fin método sesionCliente.
    
    @GET
    @Path("obtenerDatosBancarios/{idProfesional}/{token}")
    public String obtenerDatosBancarios(@PathParam("idProfesional") int idProfesional, @PathParam("token") String token){
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL))
            return profesional.obtenerCuentaBancaria(idProfesional).toJSONString();         
        else
            return JWTController.getInstance().getError().toJSONString(); 
    }//Fin método obtenerDatosBancarios.
   
    @GET
    @Path("perfilProfesional/{idProfesional}/{token}")
    public String perfilProfesional(@PathParam("idProfesional") int idProfesional, @PathParam("token") String token){    
        if(JWTController.getInstance().tokenValido(token, String.valueOf(idProfesional), JWTController.PERFIL_PROFESIONAL)){
            profesional.perfilProfesional(idProfesional);
            String perfil = gson.toJson(profesional.datosSesion());

            return perfil;
        }else
            return null; 
    }//Fin método perfilCliente.
    
    @GET
    @Path("informacionSesionMatch/{idSesion}")//NO REQUIERE JWT
    public String informacionSesionMatch(@PathParam("idSesion") int idSesion){  
        return profesional.informacionSesionMatch(idSesion).toJSONString();     
    }
    
    @POST
    @Path("/guardarDatosFiscales")
    public String guardarDatosFiscales(String datos){
        JSONObject respuesta = null;
        
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = profesional.guardarDatosFiscales(jsonDatos);
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
            profesional.enviarMsgTicket(json);
        }catch(ParseException e){
            e.printStackTrace();
        }
    }
    
    @POST
    @Path("/nuevoTicket")
    public void nueboTicket(String jsonDatos){
        try{
            JSONObject json = (JSONObject) parser.parse(jsonDatos);
            profesional.nuevoTicket(json);
        }catch(ParseException e){
            e.printStackTrace();
        }
    }
    
    @POST
    @Path("/valorarCliente")
    public void valorarCliente(String jsonDatos){
        try{
            JSONObject json = (JSONObject) parser.parse(jsonDatos);
            profesional.valorarCliente(json);
        }catch(ParseException e){
            e.printStackTrace();
        }
    }
    
    @POST
    @Path("/actualizarFoto")
    public void actualizarFoto(String datos){
        
        try{
            
            JSONObject jsonFoto = (JSONObject)parser.parse(datos);
            profesional.actualizarFoto(jsonFoto);
            
        }catch(ParseException ex){
            
            ex.printStackTrace();
            
        }
        
    }//Fin método actializarFoto.
    
    @POST
    @Path("/agregarProfesional")
    public String agregarProfesional(String datos) {
        JSONObject respuesta = new JSONObject();
        try {
            JSONObject jsonProfesional = (JSONObject) parser.parse(datos);
            profesional.nuevoProfesional(jsonProfesional);
            respuesta = profesional.guardarProfesional();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();

    }//Fin método agregarProfesional.
    
    @POST
    @Path("/agregarCuentaBancaria")
    public String agregarCuentaBancaria(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonCuenta = (JSONObject)parser.parse(datos);
            respuesta = profesional.guardarCuentaBancaria(jsonCuenta);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        return respuesta.toJSONString();
    }//Fin método agregarCuentaBancaria.
    
    @PUT
    @Path("/actualizarPass")
    public String actualizarPass(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = profesional.actualizarPass(jsonDatos);
        }catch(ParseException ex){          
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
        
    }
    
    @PUT
    @Path("/cancelarServicio")
    public String cancelarSesion(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = profesional.cancelarSesion(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }
    
    @PUT
    @Path("/agendarCita")
    public void agendarCita(String datos){
        try{
            System.out.println(datos);
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            profesional.agendarCita(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @PUT
    @Path("/cambiarEstatusServicio/{idSesion}/{idProfesional}/{estatus}")
    public void cambiarEstatusServicio(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional, @PathParam("estatus") int estatus){    
        sesiones.cambiarEstatusServicioProfesional(idSesion, idProfesional, estatus);
    }
    
    @PUT
    @Path("/cambiarEstatusServicioWeb")
    public void cambiarEstatusServicioWeb(String datos){
        
        try{
            JSONObject json = (JSONObject)parser.parse(datos);
            sesiones.cambiarEstatusServicioProfesional(Integer.parseInt(json.get("idSesion").toString()), Integer.parseInt(json.get("idProfesional").toString()), Integer.parseInt(json.get("estatus").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
    }
    
    @PUT
    @Path("/actualizarProgresoServicio/{idSesion}/{idProfesional}/{progreso}/{progresoSegundos}/{tipoDeTiempo}")
    public String actualizarProgresoServicio(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional, @PathParam("progreso") int progreso, @PathParam("progresoSegundos") int progresoSegundos, @PathParam("tipoDeTiempo") int tipoDeTiempo){
        
        sesiones.actualizarProgresoServicio(idSesion, idProfesional, progreso, progresoSegundos, tipoDeTiempo);
        JSONObject json= new JSONObject();
        json.put("estado", "Progreso guardado.");
        
        return json.toJSONString();
        
    }
    
    @PUT
    @Path("/actualizarProgresoServicioWeb")
    public String actualizarProgresoServicioWeb(String datos){
        
        try{
            JSONObject json = (JSONObject)parser.parse(datos);
            sesiones.actualizarProgresoServicio(Integer.parseInt(json.get("idSesion").toString()), Integer.parseInt(json.get("idProfesional").toString()), Integer.parseInt(json.get("progreso").toString()), Integer.parseInt(json.get("progresoSegundos").toString()), Integer.parseInt(json.get("tipoDeTiempo").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        JSONObject json= new JSONObject();
        json.put("estado", "Progreso guardado.");
        
        return json.toJSONString();
        
    }
    
    @PUT
    @Path("/servicioDisponible/{idSesion}/{idProfesional}/{disponible}")
    public void servicioDsiponible(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional, @PathParam("disponible") boolean dsiponible){
        System.out.println(idSesion);
        sesiones.servicioDisponibleProfesional(idSesion, idProfesional, dsiponible);
        
    }
    
    @PUT
    @Path("/servicioDisponibleWeb")
    public void servicioDsiponibleWeb(String datos){
        
        try{
            JSONObject json = (JSONObject)parser.parse(datos);
            sesiones.servicioDisponibleProfesional(Integer.parseInt(json.get("idSesion").toString()), Integer.parseInt(json.get("idProfesional").toString()), Boolean.parseBoolean(json.get("disponible").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
    }
    
    @PUT
    @Path("/informacionPerfilWeb")
    public void actualizaDatosPerfilWeb(String datos){
        
        try{
            
            JSONObject datosJSON = (JSONObject)parser.parse(datos);
            profesional.datosActualizarPerfil(datosJSON);
            profesional.actualizarDatosPerfil();
            
        }catch(ParseException ex){
            
            ex.printStackTrace();
            
        }
        
    }//Fin método actualizarInfoPerfil.
     
    @PUT
    @Path("/matchSesion/{idProfesional}/{idSesion}")
    public void matchSesionWeb(@PathParam("idProfesional") int idProfesional, @PathParam("idSesion") int idSesion){

        profesional.matchSesionWeb(idProfesional, idSesion);
        
    }//Fin método matchSesion.
    
    @PUT
    @Path("/matchSesion")
    public String matchSesion(String jsonDatos){
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(jsonDatos);
        }catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        return profesional.matchSesion(json).toJSONString();
    }//Fin método matchSesion.
    
    @PUT
    @Path("/actualizarPerfil")
    public String actualizaDatosPerfil(String datos) {
        JSONObject respuesta = null;
        try {
            JSONObject datosJSON = (JSONObject) parser.parse(datos);
            profesional.datosActualizarPerfil(datosJSON);
            respuesta = profesional.actualizarDatosPerfil();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }//Fin método actualizarInfoPerfil.
    
    @PUT
    @Path("/actualizarCuenta")
    public String actualizarCuenta(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = profesional.actualizarCuentaBancaria(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }//Fin método actualizarCuentaProfesional.
    

}
