package com.proathome.REST;

import com.google.gson.Gson;
import com.proathome.controladores.ControladorProfesor;
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

@Path("/apiProAtHome/profesor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RESTProfesor {

    /*
    *
    * Métodos (Servicios) de Profesores. 
    *
    */
    
    private ControladorProfesor profesor = new ControladorProfesor();
    private ObjetoUbicacion objetoUbicaciones= new ObjetoUbicacion();
    private JSONParser parser = new JSONParser();
    private Gson gson = new Gson();
    
    @GET
    @Path("/obtenerSesiones")
    public String obtenerSesiones(){
        
        objetoUbicaciones.obtenerSesionesEstudiantes();
        String jsonUbicaciones = gson.toJson(objetoUbicaciones);
        
        return jsonUbicaciones;
    
    }//Fin método obtenerSesiones.
    
    @GET
    @Path("/obtenerSesionesMovil")
    public JSONArray obtenerSesionesMovil(){
        
       return profesor.obtenerSesionesMovil(); 
       
    }//Fin método obtenerSesionesMovil.
    
    @GET
    @Path("/sesionProfesor/{correo}/{contrasena}")
    public String sesionCliente(@PathParam("correo") String correo, @PathParam("contrasena") String contrasena){
        
        profesor.iniciarSesion(correo, contrasena);
        return gson.toJson(profesor.datosSesion());
        
    }//Fin método sesionCliente.
    
    @GET
    @Path("obtenerDatosBancarios/{idProfesor}")
    public String obtenerDatosBancarios(@PathParam("idProfesor") int idProfesor){
        
        String jsonDatos = gson.toJson(profesor.obtenerCuentaBancaria(idProfesor));
        
        return jsonDatos;
        
    }//Fin método obtenerDatosBancarios.
   
    @GET
    @Path("perfilProfesor/{idProfesor}")
    public String perfilProfesor(@PathParam("idProfesor") int idProfesor){
        
       profesor.perfilProfesor(idProfesor);
       String perfil = gson.toJson(profesor.datosSesion());
       System.out.println(perfil);
       
       return perfil;
        
    }//Fin método perfilCliente.
    
    @GET
    @Path("informacionSesionMatch/{idSesion}")
    public JSONObject informacionSesionMatch(@PathParam("idSesion") int idSesion){
        
        return profesor.informacionSesionMatch(idSesion);
        
    }
    
    @POST
    @Path("/actualizarFoto")
    public void actualizarFoto(String datos){
        
        try{
            
            JSONObject jsonFoto = (JSONObject)parser.parse(datos);
            profesor.actualizarFoto(jsonFoto);
            
        }catch(ParseException ex){
            
            ex.printStackTrace();
            
        }
        
    }//Fin método actializarFoto.
    
    @POST
    @Path("/agregarProfesorWeb")
    public void agregarProfesorWeb(String datos) {

        try {

            JSONObject jsonProfesor = (JSONObject) parser.parse(datos);
            profesor.nuevoProfesor(jsonProfesor);
            profesor.guardarProfesor();

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

    }//Fin método agregarProfesor.
    
    @POST
    @Path("/agregarProfesor")
    public Response agregarProfesor(String datos) {

        try {

            JSONObject jsonProfesor = (JSONObject) parser.parse(datos);
            profesor.nuevoProfesor(jsonProfesor);
            profesor.guardarProfesor();

        } catch (ParseException ex) {

            ex.printStackTrace();

        }
        
        return Response.ok("Registro Exitoso.",MediaType.APPLICATION_JSON).build();

    }//Fin método agregarProfesor.
    
    @POST
    @Path("/agregarCuentaBancaria")
    public void agregarCuentaBancaria(String datos){
        
        try{
        
            JSONObject jsonCuenta = (JSONObject)parser.parse(datos);
            profesor.nuevaCuentaBancaria(jsonCuenta);
            profesor.guardarCuentaBancaria(Integer.parseInt(String.valueOf(jsonCuenta.get("idProfesor"))));
            
        }catch(ParseException ex){
            
            ex.printStackTrace();
            
        }
        
    }//Fin método agregarCuentaBancaria.
    
    @PUT
    @Path("/informacionPerfilWeb")
    public void actualizaDatosPerfilWeb(String datos){
        
        try{
            
            JSONObject datosJSON = (JSONObject)parser.parse(datos);
            profesor.datosActualizarPerfil(datosJSON);
            profesor.actualizarDatosPerfil();
            
        }catch(ParseException ex){
            
            ex.printStackTrace();
            
        }
        
    }//Fin método actualizarInfoPerfil.
    
    @PUT
    @Path("/informacionPerfil")
    public Response actualizaDatosPerfil(String datos){
        
        try{
            
            JSONObject datosJSON = (JSONObject)parser.parse(datos);
            profesor.datosActualizarPerfil(datosJSON);
            profesor.actualizarDatosPerfil();
            
        }catch(ParseException ex){
            
            ex.printStackTrace();
            
        }
        
        return Response.ok("Actualización exitosa", MediaType.APPLICATION_JSON).build();
        
    }//Fin método actualizarInfoPerfil.
    
    @PUT
    @Path("/actualizarCuentaProfesor")
    public Response actualizarCuentaProfesor(JSONObject jsonDatos){
        
        profesor.nuevaCuentaBancaria(jsonDatos);
        profesor.actualizarCuentaBancaria(Integer.parseInt(jsonDatos.get("idProfesor").toString()));
        
        return Response.ok("Actualización exitosa", MediaType.APPLICATION_JSON).build();
        
    }//Fin método actualizarCuentaProfesor.
    
    @PUT
    @Path("/actualizarCuentaProfesorWeb")
    public void actualizarCuentaProfesorWeb(JSONObject jsonDatos){
        
        profesor.nuevaCuentaBancaria(jsonDatos);
        profesor.actualizarCuentaBancaria(Integer.parseInt(jsonDatos.get("idProfesor").toString()));
        
    }//Fin método actualizarCuentaProfesor.

}
