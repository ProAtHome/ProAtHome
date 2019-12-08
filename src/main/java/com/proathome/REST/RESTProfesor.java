package com.proathome.REST;

import com.google.gson.Gson;
import com.proathome.controladores.ControladorProfesor;
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
    private JSONParser parser = new JSONParser();
    private Gson gson = new Gson();
    
    @GET
    @Path("/sesionProfesor/{correo}/{contrasena}")
    public String sesionCliente(@PathParam("correo") String correo, @PathParam("contrasena") String contrasena){
        
        profesor.iniciarSesion(correo, contrasena);
        String sesion = gson.toJson(profesor.datosSesion());
        System.out.println(sesion);
        
        return sesion;
        
    }//Fin método sesionCliente.

    
    @GET
    @Path("perfilProfesor/{idProfesor}")
    public String perfilProfesor(@PathParam("idProfesor") int idProfesor){
        
       profesor.perfilProfesor(idProfesor);
       String perfil = gson.toJson(profesor.datosSesion());
       System.out.println(perfil);
       
       return perfil;
        
    }//Fin método perfilCliente.
    
    @POST
    @Path("/actualizarFoto")
    public void actualizarFoto(String datos){
        
        try{
            
            JSONObject jsonFoto = (JSONObject)parser.parse(datos);
            profesor.actualizarFoto(jsonFoto);
            
        }catch(ParseException ex){
            
            System.out.println(ex.getMessage());
            
        }
        
    }//Fin método actializarFoto.
    
    @POST
    @Path("/agregarProfesor")
    public void agregarProfesor(String datos) {

        try {

            JSONObject jsonProfesor = (JSONObject) parser.parse(datos);
            profesor.nuevoProfesor(jsonProfesor);
            profesor.guardarProfesor();

        } catch (ParseException ex) {

            System.out.println(ex.getMessage());

        }

    }//Fin método agregarProfesor.
    
    @GET
    @Path("obtenerDatosBancarios/{idProfesor}")
    public String obtenerDatosBancarios(@PathParam("idProfesor") int idProfesor){
        
        Gson gson = new Gson();
        String jsonDatos = gson.toJson(profesor.obtenerCuentaBancaria(idProfesor));
        
        return jsonDatos;
        
    }//Fin método obtenerDatosBancarios.
    
    @POST
    @Path("/agregarCuentaBancaria")
    public void agregarCuentaBancaria(String datos){
        
        try{
        
            JSONObject jsonCuenta = (JSONObject)parser.parse(datos);
            profesor.nuevaCuentaBancaria(jsonCuenta);
            profesor.guardarCuentaBancaria(Integer.parseInt(String.valueOf(jsonCuenta.get("idProfesor"))));
            
        }catch(ParseException ex){
            
            System.out.println(ex.getMessage());
            
        }
        
    }//Fin método agregarCuentaBancaria.
    
    @PUT
    @Path("/informacionPerfil")
    public void actualizaDatosPerfil(String datos){
        
        try{
            
            JSONObject datosJSON = (JSONObject)parser.parse(datos);
            profesor.datosActualizarPerfil(datosJSON);
            profesor.actualizarDatosPerfil();
            
        }catch(ParseException ex){
            
            System.out.println(ex.getMessage());
            
        }
        
    }//Fin método actualizarInfoPerfil.

    @GET
    @Path("/obtenerProfesores/{clv}")
    public String obtenerProfesores(@PathParam("clv") String clv) {

        if (clv.equals("all")) {

            String jsonProfesores = "{";

            for (int i = 0; i < profesor.obtenerTodos().size(); i++) {
                if (i < profesor.obtenerTodos().size() - 1) {
                    jsonProfesores += profesor.obtenerTodos().get(i) + ",";
                } else {
                    jsonProfesores += profesor.obtenerTodos().get(i) + "}";
                }
            }

            System.out.println(jsonProfesores);
            try {

                JSONObject jsonProfesor = (JSONObject) parser.parse(jsonProfesores);

            } catch (ParseException ex) {

                System.out.println(ex.getMessage());

            }
            
            return jsonProfesores;

        }else{
            
            return "{\"mensaje\" : \"Clave incorrecta\"}";
        }
        
    }//Fin método obtenerProfesores.

}
