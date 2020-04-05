package com.proathome.REST;

import com.google.gson.Gson;
import com.proathome.controladores.ControladorCliente;
import com.proathome.controladores.ControladorSesion;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    private ControladorSesion sesiones = new ControladorSesion();
    private JSONParser parser = new JSONParser();
    private Gson gson = new Gson();

    @GET
    @Path("/sesionCliente/{correo}/{contrasena}")
    public String sesionCliente(@PathParam("correo") String correo, @PathParam("contrasena") String contrasena) {

        cliente.iniciarSesion(correo, contrasena);
        gson.toJson(cliente.datosSesion());
        
        return gson.toJson(cliente.datosSesion());

    }//Fin método sesionCliente.

    @GET
    @Path("/perfilCliente/{idCliente}")
    public String perfilCliente(@PathParam("idCliente") int idCliente) {

        cliente.perfilCliente(idCliente);
        String perfil = gson.toJson(cliente.datosSesion());

        return perfil;

    }//Fin método perfilCliente.
    
    @GET
    @Path("/obtenerDatosBancarios/{idCliente}")
    public String obtenerDatosBancarios(@PathParam("idCliente") int idCliente) {

        Gson gson = new Gson();
        String jsonDatos = gson.toJson(cliente.obtenerCuentaBancaria(idCliente));

        return jsonDatos;

    }//Fin método obtenerDatosBancarios.
    
    @GET
    @Path("/obtenerDatosBancariosWeb/{idCliente}")
    public String obtenerDatosBancariosWeb(@PathParam("idCliente") int idCliente) {

        Gson gson = new Gson();
        String jsonDatos = gson.toJson(cliente.obtenerCuentaBancaria(idCliente));

        return jsonDatos;

    }//Fin método obtenerDatosBancarios.

    @GET
    @Path("/obtenerSesiones/{idCliente}")
    public String obtenerSesiones(@PathParam("idCliente") int idCliente){
        
        Gson gson = new Gson();
        String jsonArray = "";
        
        sesiones.obtenerSesiones(idCliente);
        jsonArray = gson.toJson(sesiones);
        
        return jsonArray;
        
    }//Fin método obtenerSesiones.

    @POST
    @Path("/actualizarFoto")
    public void actualizarFoto(String datos) {

        try {

            JSONObject jsonFoto = (JSONObject) parser.parse(datos);
            cliente.actualizarFoto(jsonFoto);

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

    }//Fin método actializarFoto.
    
    @POST
    @Path("/agregarClienteWeb")
    public void agregarClienteWeb(String datos) {

        System.out.println(datos);
        try {

            JSONObject jsonCliente = (JSONObject) parser.parse(datos);
            cliente.nuevoCliente(jsonCliente);
            cliente.guardarCliente();

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

    }//Fin método agregarCliente.

    @POST
    @Path("/agregarCliente")
    public Response agregarCliente(String datos) {

        System.out.println(datos);
        try {

            JSONObject jsonCliente = (JSONObject) parser.parse(datos);
            cliente.nuevoCliente(jsonCliente);
            cliente.guardarCliente();

        } catch (ParseException ex) {

            ex.printStackTrace();

        }
        
         return Response.ok("Registro Exitoso.",MediaType.APPLICATION_JSON).build();

    }//Fin método agregarCliente.
    
    @POST
    @Path("eliminarSesion")
    public Response eliminarSesion(JSONObject jsonDatos){
        
        sesiones.eliminarSesion(Integer.parseInt(jsonDatos.get("idSesion").toString()));
        
        return Response.ok("Sesión eliminada exitosamente.", MediaType.APPLICATION_JSON).build();
    
    }
    
    @POST
    @Path("/agregarCuentaBancaria")
    public void agregarCuentaBancaria(String datos) {

        try {

            JSONObject jsonCuenta = (JSONObject) parser.parse(datos);
            cliente.nuevaCuentaBancaria(jsonCuenta);
            cliente.guardarCuentaBancaria(Integer.parseInt(String.valueOf(jsonCuenta.get("idCliente"))));

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

    }//Fin método agregarCuentaBancaria.
    
    @POST
    @Path("/agregarEvaluacion")
    public void agregarEvaluacion(String datos) {

        try {

            JSONObject jsonEvaluacion = (JSONObject) parser.parse(datos);
            cliente.nuevaEvaluacion(jsonEvaluacion);
            cliente.guardarEvaluacion(Integer.parseInt(String.valueOf(jsonEvaluacion.get("idCliente"))));

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

    }//Fin método agregarEvaluacion.

    @POST
    @Path("/agregarSesion")
    public void agregarSesion(String datos) {

        try {

            JSONObject datosJSON = (JSONObject)parser.parse(datos);
            cliente.nuevaSesion(datosJSON);
            cliente.guardarSesion();
            
        }catch(ParseException ex){
            
            ex.printStackTrace();
            
        }

    }//Fin método agregarSesion.
    
    @PUT
    @Path("/actualizarSesion")
    public Response actualizarSesion(JSONObject jsonDatos){
        
        sesiones.actualizarSesion(jsonDatos);
        
        return Response.ok("Sesión actualizada correctamente", MediaType.APPLICATION_JSON).build();
        
    }

    @PUT
    @Path("/actualizarCuentaClienteWeb")
    public void actualizarCuentaClienteWeb(String datos) {

        try {

            JSONObject jsonCuentaBancaria = (JSONObject) parser.parse(datos);
            cliente.nuevaCuentaBancaria(jsonCuentaBancaria);
            cliente.actualizarCuentaBancaria(Integer.parseInt(String.valueOf(jsonCuentaBancaria.get("idCliente"))));

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

    }//Fin método agregarCuentaBancariaWeb.
    
    @PUT
    @Path("/actualizarCuentaCliente")
    public Response actualizarCuentaCliente(JSONObject jsonDatos) {

        cliente.nuevaCuentaBancaria(jsonDatos);
        cliente.actualizarCuentaBancaria(Integer.parseInt(jsonDatos.get("idCliente").toString()));

        return Response.ok("Actualización exitosa.", MediaType.APPLICATION_JSON).build();

    }//Fin método agregarCuentaBancaria.
    
    @PUT
    @Path("/informacionPerfilWeb")
    public void actualizaDatosPerfilWeb(String datos) {

        try {

            JSONObject datosJSON = (JSONObject) parser.parse(datos);
            cliente.datosActualizarPerfil(datosJSON);
            cliente.actualizarDatosPerfil();

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

    }//Fin método informacionPerfilWeb.

    @PUT
    @Path("/informacionPerfil")
    public Response actualizaDatosPerfil(String datos) {

        try {

            JSONObject datosJSON = (JSONObject) parser.parse(datos);
            cliente.datosActualizarPerfil(datosJSON);
            cliente.actualizarDatosPerfil();

        } catch (ParseException ex) {

            ex.printStackTrace();

        }
        
        return Response.ok("Actualización exitosa", MediaType.APPLICATION_JSON).build();

    }//Fin método actualizarInfoPerfil.

}
