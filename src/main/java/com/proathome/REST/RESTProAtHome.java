package com.proathome.REST;

import com.proathome.controladores.ControladorAdmin;
import com.proathome.controladores.ControladorNivelIdioma;
import java.util.Calendar;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/apiProAtHome/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RESTProAtHome {

    /*
    *
    * Métodos (Servicios) para Administrar.
    *
    */
    
    private ControladorNivelIdioma nivel = new ControladorNivelIdioma();
    private ControladorAdmin admin = new ControladorAdmin();
    private JSONParser parser = new JSONParser();
    
    
    // CREAR UN LATIDO DE CORAZON MYSQL.
    
    @GET
    @Path("/ticketsAsociados/{idOperador}")
    public JSONArray ticketsAsociados (@PathParam("idOperador") int idOperador){
        return admin.obtenerTicketsAsociados(idOperador);
    }
    
    @GET
    @Path("/obtenerInfoTicket/{idTicket}/{tipoUsuario}")
    public JSONObject obtenerInfoTicket(@PathParam("idTicket") int idTicket,
            @PathParam("tipoUsuario") int tipoUsuario){
        return admin.infoTicketAdmin(idTicket, tipoUsuario);
    }
    
    @GET
    @Path("/obtenerTicketsAdmin")
    public JSONArray obtenerTicketsAdmin(){
        return admin.obtenerTicketsAdmin();
    }
    
    @GET
    @Path("/obtenerMsgTicket/{idUsuario}/{tipoUsuario}/{idTicket}")
    public JSONArray obtenerMsgTicket(@PathParam("idUsuario") int idUsuario,
            @PathParam("tipoUsuario") int tipoUsuario, @PathParam("idTicket") int idTicket){
        return admin.obtenerMsgTicket(idUsuario, tipoUsuario, idTicket);
    }
    
    @GET
    @Path("/fechaServidor")
    public JSONObject fechaServidor(){
        Calendar calendar = Calendar.getInstance();
        String fecha = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) +
                "-" + calendar.get(Calendar.DAY_OF_MONTH);
        JSONObject fechaServidor = new JSONObject();
        fechaServidor.put("fechaServidor", fecha);
        
        return fechaServidor;
    }
    
    @GET
    @Path("/obtenerMensajes/{tipoCliente}")
    public JSONArray obtenerMensajes(@PathParam("tipoCliente") int tipoCliente){
        return admin.obtenerMensajes(tipoCliente);  
    }
    
    @GET
    @Path("/obtenerSolicitudes")
    public String obtenerSolicitudes(){ 
        return admin.obtenerSolicitudes(); 
    }//Fin método obtenerSolicitudes.
    
    @GET
    @Path("/sesionAdmin/{usuario}/{contrasena}")
    public JSONObject sesionAdmin(@PathParam("usuario") String usuario,
            @PathParam("contrasena") String contrasena){ 
        return admin.iniciarSesion(usuario, contrasena);
    }//Fin método iniciarSesion.
    
    @POST
    @Path("/enviarMensaje")
    public void enviarMensaje(String datos){
        try{
            JSONObject mensaje = (JSONObject) parser.parse(datos);
            admin.enviarMensaje(mensaje);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @PUT
    @Path("/asociarTicket")
    public void asociarTicket(String datos){
        try{
            JSONObject asociarJSON = (JSONObject) parser.parse(datos);
            admin.asociarTicketAdmin(Integer.parseInt(asociarJSON.get("idTicket").toString()),
                    Integer.parseInt(asociarJSON.get("idOperador").toString()));
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @PUT
    @Path("/cambiarEstado/{idProfesor}/{estado}")
    public void cambiarEstado(@PathParam("idProfesor") int idProfesor,
            @PathParam("estado") boolean estado){
        admin.cambiarEstado(idProfesor, estado);
    }//Fin método cambiarEstado.

}
