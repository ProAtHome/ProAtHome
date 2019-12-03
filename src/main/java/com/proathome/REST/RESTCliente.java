package com.proathome.REST;

import com.google.gson.Gson;
import com.proathome.controladores.ControladorCliente;
import java.sql.SQLException;
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
    private JSONParser parser = new JSONParser();
    private Gson gson = new Gson();
    
    @GET
    @Path("/sesionCliente/{correo}/{contrasena}")
    public String sesionCliente(@PathParam("correo") String correo, @PathParam("contrasena") String contrasena){
        
        cliente.iniciarSesion(correo, contrasena);
        String sesion = gson.toJson(cliente.datosSesion());
        System.out.println(sesion);
        
        return sesion;
        
    }//Fin método sesionCliente.
    
    @GET
    @Path("perfilCliente/{idCliente}")
    public String perfilCliente(@PathParam("idCliente") int idCliente){
        
       cliente.perfilCliente(idCliente);
       String perfil = gson.toJson(cliente.datosSesion());
       System.out.println(perfil);
       
       return perfil;
        
    }//Fin método perfilCliente.
    
    @POST
    @Path("/actualizarFoto")
    public void actualizarFoto(String datos){
        
        try{
            
            JSONObject jsonFoto = (JSONObject)parser.parse(datos);
            cliente.actualizarFoto(jsonFoto);
            
        }catch(ParseException ex){
            
            System.out.println(ex.getMessage());
            
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

            System.out.println(ex.getMessage());

        }

    }//Fin método agregarCliente.
    
    @GET
    @Path("obtenerDatosBancarios/{idCliente}")
    public String obtenerDatosBancarios(@PathParam("idCliente") int idCliente){
        
        Gson gson = new Gson();
        String jsonDatos = gson.toJson(cliente.obtenerCuentaBancaria(idCliente));
        
        return jsonDatos;
        
    }//Fin método obtenerDatosBancarios.
    
    @POST
    @Path("/agregarCuentaBancaria")
    public void agregarCuentaBancaria(String datos){
        
        try{
        
            JSONObject jsonCuenta = (JSONObject)parser.parse(datos);
            cliente.nuevaCuentaBancaria(jsonCuenta);
            cliente.guardarCuentaBancaria(Integer.parseInt(String.valueOf(jsonCuenta.get("idCliente"))));
            
        }catch(ParseException ex){
            
            System.out.println(ex.getMessage());
            
        }
        
    }//Fin método agregarCuentaBancaria.
    
    @PUT
    @Path("/actualizarCuentaCliente")
    public void actualizarCuentaCliente(String datos){
        
        try {

            JSONObject jsonCuentaBancaria = (JSONObject) parser.parse(datos);
            cliente.nuevaCuentaBancaria(jsonCuentaBancaria);
            cliente.actualizarCuentaBancaria(Integer.parseInt(String.valueOf(jsonCuentaBancaria.get("idCliente"))));

        } catch (ParseException ex) {

            System.out.println(ex.getMessage());

        }
        
    }//Fin método agregarCuentaBancaria.
    
    @POST
    @Path("/agregarEvaluacion")
    public void agregarEvaluacion(String datos){
        
        try{
            
            JSONObject jsonEvaluacion = (JSONObject) parser.parse(datos);
            cliente.nuevaEvaluacion(jsonEvaluacion);
            cliente.guardarEvaluacion(Integer.parseInt(String.valueOf(jsonEvaluacion.get("idCliente"))));
            
        }catch(ParseException ex){
            
            System.out.println(ex.getMessage());
            
        }
        
    }//Fin método agregarEvaluacion.
    
    @PUT
    @Path("/informacionPerfil")
    public void actualizaDatosPerfil(String datos){
        
        try{
            
            JSONObject datosJSON = (JSONObject)parser.parse(datos);
            cliente.datosActualizarPerfil(datosJSON);
            cliente.actualizarDatosPerfil();
            
        }catch(ParseException ex){
            
            System.out.println(ex.getMessage());
            
        }
        
    }//Fin método actualizarInfoPerfil.
    
}
