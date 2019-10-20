package com.proathome.REST;

import com.proathome.controladores.ControladorNivelIdioma;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/apiProAtHome")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RESTProAtHome {

    /*
    *
    * Métodos (Servicios) para Administrar.
    *
    */
    
    private ControladorNivelIdioma nivel = new ControladorNivelIdioma();
    private JSONParser parser = new JSONParser();

    @POST
    @Path("/agregarNivelIdioma")
    public void agregarNivelIdioma(String datos) {
        
        try{
            
            JSONObject jsonNivel = (JSONObject)parser.parse(datos);
            nivel.nuevoNivel(jsonNivel);
            nivel.agregarNivel();
            
        }catch(ParseException ex){
            
            System.out.println(ex.getMessage());
            
        }

    }//Fin método agregarNivelIdioma.

}
