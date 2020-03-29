package com.proathome.controladores;

import com.google.gson.Gson;
import com.proathome.modelos.Admin;
import com.proathome.mysql.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Marvin
 */
public class ControladorAdmin {
    
    private Gson gson;
    private ConexionMySQL mysql = new ConexionMySQL();
    private Connection conectar;
    
    public Admin datosAdmin(String usuario, String contrasena){
        
        Admin admin = new Admin();
        admin.setUsuario(usuario);
        admin.setContrasena(contrasena);
        
        return admin;
        
    }
    
    public String iniciarSesion(String usuario, String contrasena){
        
        boolean usuarioEncontrado = false;
        gson = new Gson();
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM admins WHERE usuario = ? AND contrasena = ?");
                if(consulta.execute()){
                    
                    usuarioEncontrado = true;
                    
                }else{
                    
                    usuarioEncontrado = false;
                    
                }
                
                conectar.close();
                
            }catch(SQLException ex){
                
                ex.printStackTrace();
                
            }
            
        }else{
            
            System.out.println("Error en iniciarSesion.");
            
        }
        
        if(usuarioEncontrado)
            return gson.toJson(datosAdmin(usuario, contrasena));
        else
            return "Usuario no encontrado.";
        
    }//Fin método iniciarSesion.
    
}
