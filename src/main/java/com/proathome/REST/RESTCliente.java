package com.proathome.REST;

import com.proathome.controladores.ControladorCliente;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
    
    @POST
    @Path("/agregarCuentaCliente")
    public void agregarCuentaCliente(String datos){
        
        try {

            JSONObject jsonCuentaBancaria = (JSONObject) parser.parse(datos);
            cliente.nuevaCuentaBancaria(jsonCuentaBancaria);
            cliente.guardarCuentaBancaria(Integer.parseInt(String.valueOf(jsonCuentaBancaria.get("idCliente"))));

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
    
    @POST
    @Path("/agregarUbicacion")
    public void agregarUbicacion(String datos){
        
        
        
    }//Fin método agregarUbicacion,

}
