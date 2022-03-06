package com.proathome.controladores;

import com.proathome.modelos.NivelIdioma;
import com.proathome.mysql.DBController;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.simple.JSONObject;

public class ControladorNivelIdioma {
    
    /*
    *
    *Servicio controladora del Nivel de Idioma.
    *
    */
    
    private NivelIdioma nivel = new NivelIdioma();
    
    public void nuevoNivel(JSONObject jsonNivel){
        nivel.setNivel(String.valueOf(jsonNivel.get("nivel")));
    }//Fin método nuevoNivel.
    
    public void agregarNivel(){
        if(DBController.getInstance().getConnection() != null){ 
            try{
                String query = "INSERT INTO nivelidioma (nivel) VALUES (?)";
                PreparedStatement agregarDatos = DBController.getInstance().getConnection().prepareStatement(query);
                agregarDatos.setString(1, nivel.getNivel());
                agregarDatos.execute();
            }catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        }else
            System.out.println("Error en la conexión en agregarNivel");
    }//Fin método agregarNivel.
    
}
