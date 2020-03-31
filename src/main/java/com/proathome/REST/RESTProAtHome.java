package com.proathome.REST;

import com.proathome.controladores.ControladorAdmin;
import com.proathome.controladores.ControladorNivelIdioma;
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
    
    @GET
    @Path("/obtenerSolicitudes")
    public String obtenerSolicitudes(){
        
        return admin.obtenerSolicitudes();
        
    }//Fin método obtenerSolicitudes.
    
    @GET
    @Path("/sesionAdmin/{usuario}/{contrasena}")
    public String sesionAdmin(@PathParam("usuario") String usuario, @PathParam("contrasena") String contrasena){
        
        return admin.iniciarSesion(usuario, contrasena);
        
    }//Fin método iniciarSesion.

    @POST
    @Path("/agregarNivelIdioma")
    public void agregarNivelIdioma(String datos) {
        
        try{
            
            JSONObject jsonNivel = (JSONObject)parser.parse(datos);
            nivel.nuevoNivel(jsonNivel);
            nivel.agregarNivel();
            
        }catch(ParseException ex){
            
            ex.printStackTrace();
            
        }

    }//Fin método agregarNivelIdioma.
    
    @PUT
    @Path("/cambiarEstado/{idProfesor}/{estado}")
    public void cambiarEstado(@PathParam("idProfesor") int idProfesor, @PathParam("estado") boolean estado){
        
        admin.cambiarEstado(idProfesor, estado);
        
    }//Fin método cambiarEstado.

}
