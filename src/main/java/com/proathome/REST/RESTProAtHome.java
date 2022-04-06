package com.proathome.REST;

import com.proathome.controladores.ControladorAdmin;
import java.util.Calendar;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RESTProAtHome {

    /*
    *
    * Métodos (Servicios) para Administrar.
    *
    */
    
    private ControladorAdmin admin = new ControladorAdmin();
    private JSONParser parser = new JSONParser();
    
    @GET
    @Path("/latidoSQL")
    public void latidoSQL(){
        admin.latidoSQL();
    }
    
    @GET
    @Path("/historialReportes/{tipoUsuario}/{idUsuario}")
    public String historialReportes(@PathParam("tipoUsuario") String tipoUsuario, @PathParam("idUsuario") int idUsuario){
        return admin.historialReportes(tipoUsuario, idUsuario).toJSONString();
    }
    
    @GET
    @Path("/verServicio/{idSesion}")
    public String verServicio(@PathParam("idSesion") int idSesion){
        return admin.verServicio(idSesion).toJSONString();
    }
    
    @GET
    @Path("/getPerfilProfesional/{idProfesional}")
    public String getPerfilProfesional(@PathParam("idProfesional") int idProfesional){
        return admin.getPerfilProfesional(idProfesional).toJSONString();
    }
    
    @GET
    @Path("/getPerfilCliente/{idCliente}")
    public String getPerfilCliente(@PathParam("idCliente") int idCliente){
        return admin.getPerfilCliente(idCliente).toJSONString();
    }
    
    @GET
    @Path("/getProfesionalesRegistrados")
    public String getProfesionalesRegistrados(){
        return admin.getProfesionalesRegistrados().toJSONString();
    }
    
    @GET
    @Path("/getClientesRegistrados")
    public String getClientesRegistrados(){
        return admin.getClientesRegistrados().toJSONString();
    }
    
    @GET
    @Path("/eliminarOperador/{idOperador}")
    public String eliminarOperador(@PathParam("idOperador") int idOperador){
        return admin.eliminarOperador(idOperador).toJSONString();
    }
    
    @GET
    @Path("/getOperadoresUsuario")
    public String getOperadoresUsuario(){
        return admin.getOperadoresUsuario().toJSONString();
    }
    
    @GET
    @Path("/getOperadoresSoporte")
    public String getOperadoresSoporte(){
        return admin.getOperadoresSoporte().toJSONString();
    }
    
    @GET
    @Path("/getSolicitudesAsignadasClientes/{idOperador}")
    public String getSolicitudesAsignadasClientes(@PathParam("idOperador") int idOperador){
        return admin.getSolicitudesAsignadasClientes(idOperador).toJSONString();
    }
    
    @GET
    @Path("/getSolicitudesAsignadas/{idOperador}")
    public String getSolicitudesAsignadas(@PathParam("idOperador") int idOperador){
        return admin.getSolicitudesAsignadas(idOperador).toJSONString();
    }
    
    @GET
    @Path("/obtenerSolicitudesClientes")
    public String obtenerSolicitudesClientes(){
        return admin.obtenerSolicitudesClientes().toJSONString();
    }
    
    @GET
    @Path("/obtenerSolicitudes")
    public String obtenerSolicitudes(){
        return admin.obtenerSolicitudes().toJSONString();
    }
    
    @GET
    @Path("/ticketSolucionado/{idTicket}")
    public String ticketSolucionado(@PathParam("idTicket") int idTicket){
        return admin.ticketSolucionado(idTicket).toJSONString();
    }
    
    @GET
    @Path("/obtenerMensajes/{idTicket}/{tipoUsuario}")
    public String obtenerMensajes(@PathParam("idTicket") int idTicket,
            @PathParam("tipoUsuario") int tipoUsuario){
        return admin.obtenerMensajes(idTicket, tipoUsuario).toJSONString();
    }
    
    @GET
    @Path("/ticketsFinalizados/{idOperador}/{categoria}")
    public String ticketsFinalizados(@PathParam("idOperador") int idOperador, @PathParam("categoria") String categoria){
        return admin.ticketsFinalizados(idOperador, categoria).toJSONString();
    }
    
    @GET
    @Path("/ticketsAsociados/{idOperador}/{categoria}")
    public String ticketsAsociados (@PathParam("idOperador") int idOperador, @PathParam("categoria") String categoria){
        return admin.obtenerTicketsAsociados(idOperador, categoria).toJSONString();
    }
    
    @GET
    @Path("/obtenerInfoTicket/{idTicket}/{tipoUsuario}")
    public String obtenerInfoTicket(@PathParam("idTicket") int idTicket,
            @PathParam("tipoUsuario") int tipoUsuario){
        return admin.infoTicketAdmin(idTicket, tipoUsuario).toJSONString();
    }
    
    @GET
    @Path("/obtenerTicketsAdmin/{categoria}")
    public String obtenerTicketsAdmin(@PathParam("categoria") String categoria){
        System.out.println(categoria);
        return admin.obtenerTicketsAdmin(categoria).toJSONString();
    }
    
    @GET
    @Path("/obtenerMsgTicket/{idUsuario}/{tipoUsuario}/{idTicket}")
    public String obtenerMsgTicket(@PathParam("idUsuario") int idUsuario,
            @PathParam("tipoUsuario") int tipoUsuario, @PathParam("idTicket") int idTicket){
        return admin.obtenerMsgTicket(idUsuario, tipoUsuario, idTicket).toJSONString();
    }
    
    @GET
    @Path("/fechaServidor")
    public String fechaServidor(){
        Calendar calendar = Calendar.getInstance();
        String fecha = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) +
                "-" + calendar.get(Calendar.DAY_OF_MONTH);
        JSONObject fechaServidor = new JSONObject();
        fechaServidor.put("fechaServidor", fecha);
        
        return fechaServidor.toJSONString();
    }
    
    @GET
    @Path("/sesionAdmin/{usuario}/{contrasena}")
    public String sesionAdmin(@PathParam("usuario") String usuario,
            @PathParam("contrasena") String contrasena){ 
        return admin.iniciarSesion(usuario, contrasena).toJSONString();
    }//Fin método iniciarSesion.
    
    @POST
    @Path("/crearReporte")
    public String crearReporte(String datos){
        System.out.println("ss");
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = admin.crearReporte(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }
    
    @POST
    @Path("/guardarOperador")
    public String guardarOperador(String datos){
        JSONObject jsonRespuesta = new JSONObject();
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            jsonRespuesta = admin.guardarOperador(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        System.out.println(jsonRespuesta);
        return jsonRespuesta.toJSONString();
    }
    
    @POST
    @Path("/reagendarCita")
    public String reagendarCita(String datos){
        JSONObject jsonRespuesta = new JSONObject();
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            jsonRespuesta = admin.reagendarCita(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return jsonRespuesta.toJSONString();
    }
    
    @POST
    @Path("/agendarCita")
    public String agendarCita(String datos){
        JSONObject jsonDatos = new JSONObject();
        try{
            jsonDatos = (JSONObject) parser.parse(datos);
            return admin.agendarCita(jsonDatos).toJSONString();
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return jsonDatos.toJSONString();
    }
    
    @POST
    @Path("/asociarSolicitudCliente")
    public void asociarSolicitudCliente(String datos){
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            admin.asociarSolicitudCliente(jsonDatos);
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
    public String agendaActual(String datos){
        JSONObject respuesta = new JSONObject();
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = admin.agendaActual(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
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
    public String bloquearPerfil(String datos){
        JSONObject respuesta = null;
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = admin.bloquearPerfil(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }
    
    @PUT
    @Path("/desbloquearPerfil")
    public String desbloquearPerfil(String datos){
        JSONObject respuesta = new JSONObject();
        
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = admin.desbloquearPerfil(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }
    
    @PUT
    @Path("/actualizarOperador")
    public String actualizarOperador(String datos){
        JSONObject respuesta = new JSONObject();
        
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            respuesta = admin.actualizarOperador(jsonDatos);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
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
    @Path("/activarCliente")
    public String activarCliente(String datos){
        JSONObject respuesta = new JSONObject();
        try{
            JSONObject datosJSON = (JSONObject) parser.parse(datos);
            respuesta = admin.activarCliente(datosJSON);
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return respuesta.toJSONString();
    }
    
    @PUT
    @Path("/rechazarDocumentacionCliente")
    public void rechazarDocumentacionCliente(String datos){
        try{
            JSONObject jsonDatos = (JSONObject) parser.parse(datos);
            admin.rechazarDocumentacionCliente(jsonDatos);
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
