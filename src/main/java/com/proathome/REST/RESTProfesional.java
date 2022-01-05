package com.proathome.REST;

import com.google.gson.Gson;
import com.proathome.controladores.ControladorProfesional;
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

@Path("/apiProAtHome/profesional")
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
    @Path("/getVerificacion/{token}/{correo}")
    public JSONObject getVerificacion(@PathParam("token") String token, @PathParam("correo") String correo){
        return profesional.getVerificacion(token, correo);
    }
    
    @GET
    @Path("/solicitudEliminarSesion/{idSesion}/{idProfesional}")
    public JSONObject solicitudEliminarSesion(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional){
        return profesional.solicitudEliminarSesion(idSesion, idProfesional);
    }
    
    @GET
    @Path("/getDatosFiscales/{idProfesional}")
    public JSONObject getDatosFiscales(@PathParam("idProfesional") int idProfesional){
        return profesional.getDatosFiscales(idProfesional);
    }
    
    @GET
    @Path("/getReportes/{idProfesional}")
    public JSONObject getReportes(@PathParam("idProfesional") int idProfesional){
        return profesional.getReportes(idProfesional);
    }
    
    @GET
    @Path("/obtenerCita/{idProfesional}")
    public JSONObject obtenerCita(@PathParam("idProfesional") int idProfesional){
        return profesional.obtenerCita(idProfesional);
    }
    
    @GET
    @Path("/estatusDocumentos/{idProfesional}")
    public JSONObject estatusDocumentos(@PathParam("idProfesional") int idProfesional){
        return profesional.estatusDocumentos(idProfesional);
    }
    
    @GET
    @Path("/finalizarTicket/{idTicket}")
    public void solicitudTicketFinalizado(@PathParam("idTicket") int idTicket){
        profesional.finalizarTicket(idTicket);
    }
    
    @GET
    @Path("/obtenerTickets/{idCliente}")
    public JSONArray obtenerTickets(@PathParam("idCliente") int idCliente){
        return profesional.obtenerTickets(idCliente);
    }
    
    @GET
    @Path("/validarValoracion/{idSesion}/{idCliente}")
    public JSONObject validarValoracion(@PathParam("idSesion") int idSesion, @PathParam("idCliente") int idCliente){
        return profesional.validarValoracion(idSesion, idCliente);
    }
    
    @GET
    @Path("/obtenerValoracion/{idCliente}")
    public JSONArray obtenerValoracion(@PathParam("idCliente") int idCliente){
        return profesional.obtenerValoracion(idCliente);
    }
    
    @GET
    @Path("/validarServicioFinalizada/{idSesion}/{idProfesional}")
    public JSONObject validarServicioFinalizada(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional){      
        return sesiones.validarServicioFinalizada(idSesion, idProfesional);
    }
    
    @GET
    @Path("/validarEstatusServicio/{idSesion}/{idProfesional}")
    public JSONObject validarEstatusServicio(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional){
        
        JSONObject json = sesiones.validarEstatusServicioProfesional(idSesion, idProfesional);
        System.out.println(json);
        
        return json;
        
    }
    
    @GET
    @Path("/sincronizarServicio/{idSesion}/{idProfesional}")
    public JSONObject sincronizarServicio(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional){    
       return sesiones.sincronizarServicioProfesional(idSesion, idProfesional);  
    }
    
    @GET
    @Path("/obtenerSesionesProfesionalMatch/{idProfesional}")
    public JSONArray obtenerSesionesProfesionalMatch(@PathParam("idProfesional") int idProfesional){
        return profesional.sesionesMatchProfesional(idProfesional);
    }//Fin método obtenerSesionesProfesionalMatch.
    
    @GET
    @Path("/obtenerSesionesMaps")
    public String obtenerSesionesMaps(){
        objetoUbicaciones.obtenerSesionesClientes();
        String jsonUbicaciones = gson.toJson(objetoUbicaciones);
        
        return jsonUbicaciones;
    }//Fin método obtenerSesiones.
    
    @GET
    @Path("/obtenerSesionesMovil/{rango}")
    public JSONArray obtenerSesionesMovil(@PathParam("rango") int rango){   
        System.out.println(rango);
       return profesional.obtenerSesionesMovil(rango);  
    }//Fin método obtenerSesionesMovil.
    
    @GET
    @Path("/sesionProfesional/{correo}/{contrasena}")
    public String sesionProfesional(@PathParam("correo") String correo, @PathParam("contrasena") String contrasena){  
        profesional.iniciarSesion(correo, contrasena);
        return gson.toJson(profesional.datosSesion());  
    }//Fin método sesionCliente.
    
    @GET
    @Path("obtenerDatosBancarios/{idProfesional}")
    public JSONObject obtenerDatosBancarios(@PathParam("idProfesional") int idProfesional){
        JSONObject respuesta = profesional.obtenerCuentaBancaria(idProfesional);
        return respuesta; 
    }//Fin método obtenerDatosBancarios.
   
    @GET
    @Path("perfilProfesional/{idProfesional}")
    public String perfilProfesional(@PathParam("idProfesional") int idProfesional){
        
       profesional.perfilProfesional(idProfesional);
       String perfil = gson.toJson(profesional.datosSesion());
       
       return perfil;
        
    }//Fin método perfilCliente.
    
    @GET
    @Path("informacionSesionMatch/{idSesion}")
    public JSONObject informacionSesionMatch(@PathParam("idSesion") int idSesion){  
        return profesional.informacionSesionMatch(idSesion);     
    }
    
    @POST
    @Path("/guardarDatosFiscales")
    public JSONObject guardarDatosFiscales(String datos){
        JSONObject respuesta = null;
        
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = profesional.guardarDatosFiscales(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta;
    }
    
    @POST
    @Path("/enviarMsgTicket")
    public void enviarMsgTicket(JSONObject jsonDatos){
        profesional.enviarMsgTicket(jsonDatos);
    }
    
    @POST
    @Path("/nuevoTicket")
    public void nueboTicket(JSONObject jsonDatos){
        profesional.nuevoTicket(jsonDatos);
    }
    
    @POST
    @Path("/valorarCliente")
    public void valorarCliente(JSONObject jsonDatos){
        profesional.valorarCliente(jsonDatos);
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
    public JSONObject agregarProfesional(String datos) {
        JSONObject respuesta = new JSONObject();
        try {
            JSONObject jsonProfesional = (JSONObject) parser.parse(datos);
            profesional.nuevoProfesional(jsonProfesional);
            respuesta = profesional.guardarProfesional();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        return respuesta;

    }//Fin método agregarProfesional.
    
    @POST
    @Path("/agregarCuentaBancaria")
    public JSONObject agregarCuentaBancaria(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonCuenta = (JSONObject)parser.parse(datos);
            respuesta = profesional.guardarCuentaBancaria(jsonCuenta);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        return respuesta;
    }//Fin método agregarCuentaBancaria.
    
    @PUT
    @Path("/actualizarPass")
    public JSONObject actualizarPass(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = profesional.actualizarPass(jsonDatos);
        }catch(ParseException ex){          
            ex.printStackTrace();
        }
        
        return respuesta;
        
    }
    
    @PUT
    @Path("/cancelarServicio")
    public JSONObject cancelarSesion(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = profesional.cancelarSesion(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta;
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
    public JSONObject actualizarProgresoServicio(@PathParam("idSesion") int idSesion, @PathParam("idProfesional") int idProfesional, @PathParam("progreso") int progreso, @PathParam("progresoSegundos") int progresoSegundos, @PathParam("tipoDeTiempo") int tipoDeTiempo){
        
        sesiones.actualizarProgresoServicio(idSesion, idProfesional, progreso, progresoSegundos, tipoDeTiempo);
        JSONObject json= new JSONObject();
        json.put("estado", "Progreso guardado.");
        
        return json;
        
    }
    
    @PUT
    @Path("/actualizarProgresoServicioWeb")
    public JSONObject actualizarProgresoServicioWeb(String datos){
        
        try{
            JSONObject json = (JSONObject)parser.parse(datos);
            sesiones.actualizarProgresoServicio(Integer.parseInt(json.get("idSesion").toString()), Integer.parseInt(json.get("idProfesional").toString()), Integer.parseInt(json.get("progreso").toString()), Integer.parseInt(json.get("progresoSegundos").toString()), Integer.parseInt(json.get("tipoDeTiempo").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        JSONObject json= new JSONObject();
        json.put("estado", "Progreso guardado.");
        
        return json;
        
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
    public Response matchSesion(JSONObject jsonDatos){
 
        profesional.matchSesion(jsonDatos);
        
        return Response.ok("Solicitud enviada.", MediaType.APPLICATION_JSON).build();
        
    }//Fin método matchSesion.
    
    @PUT
    @Path("/actualizarPerfil")
    public JSONObject actualizaDatosPerfil(String datos) {
        JSONObject respuesta = null;
        try {
            JSONObject datosJSON = (JSONObject) parser.parse(datos);
            profesional.datosActualizarPerfil(datosJSON);
            respuesta = profesional.actualizarDatosPerfil();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        return respuesta;
    }//Fin método actualizarInfoPerfil.
    
    @PUT
    @Path("/actualizarCuenta")
    public JSONObject actualizarCuenta(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = profesional.actualizarCuentaBancaria(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta;
    }//Fin método actualizarCuentaProfesional.
    

}
