package com.proathome.REST;

import com.proathome.modelos.Profesor;
import com.proathome.controladores.ControladorProfesor;
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
    
    @POST
    @Path("/agregarCuentaProfesor")
    public void agregarCuentaProfesor(String datos){
        
        try {

            JSONObject jsonCuentaBancaria = (JSONObject) parser.parse(datos);
            profesor.nuevaCuentaBancaria(jsonCuentaBancaria);
            profesor.guardarCuentaBancaria(Integer.parseInt(String.valueOf(jsonCuentaBancaria.get("idProfesor"))));

        } catch (ParseException ex) {

            System.out.println(ex.getMessage());

        }
        
    }//Fin método agregarCuentaBancaria.

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
