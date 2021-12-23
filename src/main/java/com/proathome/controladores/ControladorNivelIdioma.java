package com.proathome.controladores;

import com.proathome.modelos.NivelIdioma;
import com.proathome.mysql.ConexionMySQL;
import java.sql.Connection;
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
    private Connection conectar;
    
    public void nuevoNivel(JSONObject jsonNivel){
        
        nivel.setNivel(String.valueOf(jsonNivel.get("nivel")));
        
    }//Fin método nuevoNivel.
    
    public void agregarNivel(){
        
        conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            
            try{
                
                String query = "INSERT INTO nivelidioma (nivel) VALUES (?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setString(1, nivel.getNivel());
                agregarDatos.execute();
                
                conectar.close();
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }
            
        }else{
            
            System.out.println("Error en la conexión en agregarNivel");
            
        }
        
    }//Fin método agregarNivel.
    
}
