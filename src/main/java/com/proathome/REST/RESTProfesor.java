package com.proathome.REST;

import com.google.gson.Gson;
import com.proathome.controladores.ControladorProfesor;
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

@Path("/apiProAtHome/profesor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RESTProfesor {

    /*
    *
    * Métodos (Servicios) de Profesores. 
    *
    */
    
    private ControladorSesion sesiones = new ControladorSesion();
    private ControladorProfesor profesor = new ControladorProfesor();
    private ObjetoUbicacion objetoUbicaciones = new ObjetoUbicacion();
    private JSONParser parser = new JSONParser();
    private Gson gson = new Gson();
    
    @GET
    @Path("/validarEstatusClase/{idSesion}/{idProfesor}")
    public JSONObject validarEstatusClase(@PathParam("idSesion") int idSesion, @PathParam("idProfesor") int idProfesor){
        
        JSONObject json = sesiones.validarEstatusClaseProfesor(idSesion, idProfesor);
        System.out.println(json);
        
        return json;
        
    }
    
    @GET
    @Path("/sincronizarClase/{idSesion}/{idProfesor}")
    public JSONObject sincronizarClase(@PathParam("idSesion") int idSesion, @PathParam("idProfesor") int idProfesor){
        
       return sesiones.sincronizarClaseProfesor(idSesion, idProfesor);
        
    }
    
    @GET
    @Path("/obtenerSesionesProfesorMatch/{idProfesor}")
    public JSONArray obtenerSesionesProfesorMatch(@PathParam("idProfesor") int idProfesor){
        
        return profesor.sesionesMatchProfesor(idProfesor);
        
    }//Fin método obtenerSesionesProfesorMatch.
    
    @GET
    @Path("/obtenerSesionesMaps")
    public String obtenerSesionesMaps(){
        
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
    @Path("/cambiarEstatusClase/{idSesion}/{idProfesor}/{estatus}")
    public void cambiarEstatusClase(@PathParam("idSesion") int idSesion, @PathParam("idProfesor") int idProfesor, @PathParam("estatus") int estatus){
        
        System.out.println("PROFESOOOOO");
        sesiones.cambiarEstatusClaseProfesor(idSesion, idProfesor, estatus);
        
    }
    
    @PUT
    @Path("/actualizarProgresoClase/{idSesion}/{idProfesor}/{progreso}/{progresoSegundos}")
    public JSONObject actualizarProgresoClase(@PathParam("idSesion") int idSesion, @PathParam("idProfesor") int idProfesor, @PathParam("progreso") int progreso, @PathParam("progresoSegundos") int progresoSegundos){
        
        sesiones.actualizarProgresoClase(idSesion, idProfesor, progreso, progresoSegundos);
        JSONObject json= new JSONObject();
        json.put("estado", "Progreso guardado.");
        
        return json;
        
    }
    
    @PUT
    @Path("/claseDisponible/{idSesion}/{idProfesor}/{disponible}")
    public void claseDsiponible(@PathParam("idSesion") int idSesion, @PathParam("idProfesor") int idProfesor, @PathParam("disponible") boolean dsiponible){
        
        sesiones.claseDisponibleProfesor(idSesion, idProfesor, dsiponible);
        
    }
    
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
    @Path("/matchSesion/{idProfesor}/{idSesion}")
    public void matchSesionWeb(@PathParam("idProfesor") int idProfesor, @PathParam("idSesion") int idSesion){

        profesor.matchSesionWeb(idProfesor, idSesion);
        
    }//Fin método matchSesion.
    
    @PUT
    @Path("/matchSesion")
    public Response matchSesion(JSONObject jsonDatos){
 
        profesor.matchSesion(jsonDatos);
        
        return Response.ok("Solicitud enviada.", MediaType.APPLICATION_JSON).build();
        
    }//Fin método matchSesion.
    
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
