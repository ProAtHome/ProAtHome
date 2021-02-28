package com.proathome.REST;

import com.proathome.controladores.ControladorAdmin;
import com.proathome.controladores.ControladorNivelIdioma;
import java.sql.SQLException;
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
    @Path("/verClase/{idSesion}")
    public JSONObject verClase(@PathParam("idSesion") int idSesion){
        return admin.verClase(idSesion);
    }
    
    @GET
    @Path("/getPerfilProfesor/{idProfesor}")
    public JSONObject getPerfilProfesor(@PathParam("idProfesor") int idProfesor){
        return admin.getPerfilProfesor(idProfesor);
    }
    
    @GET
    @Path("/getPerfilEstudiante/{idEstudiante}")
    public JSONObject getPerfilEstudiante(@PathParam("idEstudiante") int idEstudiante){
        return admin.getPerfilEstudiante(idEstudiante);
    }
    
    @GET
    @Path("/getProfesoresRegistrados")
    public JSONObject getProfesoresRegistrados(){
        return admin.getProfesoresRegistrados();
    }
    
    @GET
    @Path("/getEstudiantesRegistrados")
    public JSONObject getEstudiantesRegistrados(){
        return admin.getEstudiantesRegistrados();
    }
    
    @GET
    @Path("/eliminarOperador/{idOperador}")
    public JSONObject eliminarOperador(@PathParam("idOperador") int idOperador){
        return admin.eliminarOperador(idOperador);
    }
    
    @GET
    @Path("/getOperadoresUsuario")
    public JSONObject getOperadoresUsuario(){
        return admin.getOperadoresUsuario();
    }
    
    @GET
    @Path("/getOperadoresSoporte")
    public JSONObject getOperadoresSoporte(){
        return admin.getOperadoresSoporte();
    }
    
    @GET
    @Path("/getSolicitudesAsignadasEstudiantes/{idOperador}")
    public JSONArray getSolicitudesAsignadasEstudiantes(@PathParam("idOperador") int idOperador){
        return admin.getSolicitudesAsignadasEstudiantes(idOperador);
    }
    
    @GET
    @Path("/getSolicitudesAsignadas/{idOperador}")
    public JSONArray getSolicitudesAsignadas(@PathParam("idOperador") int idOperador){
        return admin.getSolicitudesAsignadas(idOperador);
    }
    
    @GET
    @Path("/obtenerSolicitudesEstudiantes")
    public JSONArray obtenerSolicitudesEstudiantes(){
        return admin.obtenerSolicitudesEstudiantes();
    }
    
    @GET
    @Path("/obtenerSolicitudes")
    public JSONArray obtenerSolicitudes(){
        return admin.obtenerSolicitudes();
    }
    
    @GET
    @Path("/ticketSolucionado/{idTicket}")
    public JSONObject ticketSolucionado(@PathParam("idTicket") int idTicket){
        return admin.ticketSolucionado(idTicket);
    }
    
    @GET
    @Path("/obtenerMensajes/{idTicket}/{tipoUsuario}")
    public JSONArray obtenerMensajes(@PathParam("idTicket") int idTicket,
            @PathParam("tipoUsuario") int tipoUsuario){
        return admin.obtenerMensajes(idTicket, tipoUsuario);
    }
    
    @GET
    @Path("/ticketsFinalizados/{idOperador}/{categoria}")
    public JSONArray ticketsFinalizados(@PathParam("idOperador") int idOperador, @PathParam("categoria") String categoria){
        return admin.ticketsFinalizados(idOperador, categoria);
    }
    
    @GET
    @Path("/ticketsAsociados/{idOperador}/{categoria}")
    public JSONArray ticketsAsociados (@PathParam("idOperador") int idOperador, @PathParam("categoria") String categoria){
        return admin.obtenerTicketsAsociados(idOperador, categoria);
    }
    
    @GET
    @Path("/obtenerInfoTicket/{idTicket}/{tipoUsuario}")
    public JSONObject obtenerInfoTicket(@PathParam("idTicket") int idTicket,
            @PathParam("tipoUsuario") int tipoUsuario){
        return admin.infoTicketAdmin(idTicket, tipoUsuario);
    }
    
    @GET
    @Path("/obtenerTicketsAdmin/{categoria}")
    public JSONArray obtenerTicketsAdmin(@PathParam("categoria") String categoria){
        System.out.println(categoria);
        return admin.obtenerTicketsAdmin(categoria);
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
    @Path("/sesionAdmin/{usuario}/{contrasena}")
    public JSONObject sesionAdmin(@PathParam("usuario") String usuario,
            @PathParam("contrasena") String contrasena){ 
        return admin.iniciarSesion(usuario, contrasena);
    }//Fin método iniciarSesion.
    
    @POST
    @Path("/crearReporte")
    public JSONObject crearReporte(String datos){
        System.out.println("ss");
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = admin.crearReporte(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta;
    }
    
    @POST
    @Path("/guardarOperador")
    public JSONObject guardarOperador(String datos){
        JSONObject jsonRespuesta = new JSONObject();
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            jsonRespuesta = admin.guardarOperador(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        System.out.println(jsonRespuesta);
        return jsonRespuesta;
    }
    
    @POST
    @Path("/reagendarCita")
    public JSONObject reagendarCita(String datos){
        JSONObject jsonRespuesta = new JSONObject();
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            jsonRespuesta = admin.reagendarCita(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return jsonRespuesta;
    }
    
    @POST
    @Path("/agendarCita")
    public JSONObject agendarCita(String datos){
        JSONObject jsonDatos = new JSONObject();
        try{
            jsonDatos = (JSONObject) parser.parse(datos);
            return admin.agendarCita(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return jsonDatos;
    }
    
    @POST
    @Path("/asociarSolicitudEstudiante")
    public void asociarSolicitudEstudiante(String datos){
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            admin.asociarSolicitudEstudiante(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @POST
    @Path("/asociarSolicitud")
    public void asociarSolicitud(String datos){
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            admin.asociarSolicitud(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @POST
    @Path("/agendaActual")
    public JSONObject agendaActual(String datos){
        JSONObject respuesta = new JSONObject();
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = admin.agendaActual(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta;
    }
    
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
    @Path("/bloquearPerfil")
    public JSONObject bloquearPerfil(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = admin.bloquearPerfil(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta;
    }
    
    @PUT
    @Path("/desbloquearPerfil")
    public JSONObject desbloquearPerfil(String datos){
        JSONObject respuesta = new JSONObject();
        
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = admin.desbloquearPerfil(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta;
    }
    
    @PUT
    @Path("/actualizarOperador")
    public JSONObject actualizarOperador(String datos){
        JSONObject respuesta = new JSONObject();
        
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = admin.actualizarOperador(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta;
    }
    
    @PUT
    @Path("/activarPerfil")
    public void activarPerfil(String datos){
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            admin.activarPerfil(jsonDatos);
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
    @Path("/activarEstudiante")
    public JSONObject activarEstudiante(String datos){
        JSONObject respuesta = new JSONObject();
        try{
            JSONObject datosJSON = (JSONObject) parser.parse(datos);
            respuesta = admin.activarEstudiante(datosJSON);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta;
    }
    
    @PUT
    @Path("/rechazarDocumentacionEstudiante")
    public void rechazarDocumentacionEstudiante(String datos){
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            admin.rechazarDocumentacionEstudiante(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }
    
    @PUT
    @Path("/rechazarDocumentacion")
    public void rechazarDocumentacion(String datos){
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            admin.rechazarDocumentacion(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
    }

}
