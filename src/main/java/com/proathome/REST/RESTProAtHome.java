
package com.proathome.REST;

import com.proathome.Cliente;
import com.proathome.Profesor;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * Capa de servicios REST.
 * 
 */

@Path("/apiProAtHome")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RESTProAtHome {
    
    /*
    *
    * Métodos (Servicios) de Clientes. 
    *
    */
    
    @POST
    @Path("/agregarCliente")
    public void agregarCliente(String datos){
        
        try{
            
            JSONParser parser = new JSONParser();
            JSONObject jsonCliente = (JSONObject)parser.parse(datos);
            Cliente nuevoCliente = new Cliente(jsonCliente);
            nuevoCliente.agregarCliente();
            
        }catch(ParseException ex){
            
            System.out.println(ex.getMessage());
            
        }
        
    }//Fin método agregarCliente.
    
    
    
    
    /*
    *
    * Métodos (Servicios) de Profesores. 
    *
    */
    
    @POST
    @Path("/agregarProfesor")
    public void agregarProfesor(String datos){
        
        try{
            
            JSONParser parser = new JSONParser();
            JSONObject jsonProfesor = (JSONObject)parser.parse(datos);
            Profesor nuevoProfesor = new Profesor(jsonProfesor);
            nuevoProfesor.agregarProfesor();
            
        }catch(ParseException ex){
            
            System.out.println(ex.getMessage());
            
        }
        
    }//Fin método agregarProfesor.
    
    @GET
    @Path("/obtenerProfesores/{clv}")
    public String obtenerProfesores(@PathParam("clv") String clv){
        Profesor todosProfes = new Profesor(clv);
        
        String jsonProfesores = "{";
        
        for (int i = 0; i < todosProfes.obtenerTodos().size(); i++) {
            if(i < todosProfes.obtenerTodos().size()-1)
                jsonProfesores += todosProfes.obtenerTodos().get(i) + ",";
            else
                jsonProfesores += todosProfes.obtenerTodos().get(i) + "}";
        }
        
        System.out.println(jsonProfesores);
        try{
            
        JSONParser parser = new JSONParser();
        JSONObject jsonProfesor = (JSONObject)parser.parse(jsonProfesores);
            
        }catch(ParseException ex){
            
            System.out.println(ex.getMessage());
            
        }
       
        return jsonProfesores;
    }
}
