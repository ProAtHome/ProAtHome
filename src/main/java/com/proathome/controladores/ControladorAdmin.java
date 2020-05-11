package com.proathome.controladores;

import com.google.gson.Gson;
import com.proathome.modelos.Admin;
import com.proathome.modelos.Profesor;
import com.proathome.mysql.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Marvin
 */
public class ControladorAdmin {
    
    private Gson gson;
    private ConexionMySQL mysql = new ConexionMySQL();
    private Profesor profesores[];
    private Connection conectar;
    
    public void cambiarEstado(int idProfesor, boolean estado){
        
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement cambiarEstado = conectar.prepareStatement("UPDATE profesores SET estado = ? WHERE idprofesores = ?");
                cambiarEstado.setBoolean(1 , estado);
                cambiarEstado.setInt(2 , idProfesor);
                cambiarEstado.execute();
                conectar.close();
                
            }catch(SQLException ex){
                
                ex.printStackTrace();
                
            }
            
        }else{
            
            System.out.println("Error en cambiarEstado.");
            
        }
        
    }//Fin método cambiarEstado.
    
    public String obtenerSolicitudes(){
        
        gson = new Gson();
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement registros = conectar.prepareStatement("SELECT COUNT(*) AS registros FROM profesores");
                ResultSet resultadoRegistros = registros.executeQuery();
                
                
                if(resultadoRegistros.next()){
                    
                    profesores = new Profesor[resultadoRegistros.getInt("registros")];
                    PreparedStatement solicitudes = conectar.prepareStatement("SELECT * FROM profesores ORDER BY fechaDeRegistro DESC");
                    ResultSet resultado = solicitudes.executeQuery();
                    int aux = 0;
                    
                    while(resultado.next()){
                        
                        Profesor profesor = new Profesor();
                        profesor.setIdProfesor(resultado.getInt("idprofesores"));
                        profesor.setNombre(resultado.getString("nombre"));
                        profesor.setCorreo(resultado.getString("correo"));
                        profesor.setEdad(resultado.getInt("edad"));
                        profesor.setFechaRegistro(resultado.getDate("fechaDeRegistro"));
                        profesor.setEstado(resultado.getBoolean("estado"));
                        profesores[aux] = profesor;
                        aux++;
                        
                    }
                }
                conectar.close();
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            
            System.out.println("Error en obtenerSolicitudes.");
            
        }
        
        return gson.toJson(profesores);
        
    }
    
    public Admin datosAdmin(String usuario, String contrasena, int idAdmin){
        
        Admin admin = new Admin();
        admin.setUsuario(usuario);
        admin.setContrasena(contrasena);
        admin.setIdAdmin(idAdmin);
        
        return admin;
        
    }
    
    public String iniciarSesion(String usuario, String contrasena){
        
        boolean usuarioEncontrado = false;
        gson = new Gson();
        conectar = mysql.conectar();
        int idAdmin = 0;
        
        if(conectar != null){
            
            try{
                
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM admins WHERE usuario = ? AND contrasena = ?");
                consulta.setString(1 , usuario);
                consulta.setString(2 , contrasena);
                ResultSet resultado = consulta.executeQuery();
                if(resultado.next()){
                    
                    usuarioEncontrado = true;
                    idAdmin = resultado.getInt("idadmins");
                    
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
            return "{\"result\":true, \"sesion\":" + gson.toJson(datosAdmin(usuario, contrasena, idAdmin)) + "}";
        else
            return "{\"result\":false, \"sesion\":\"Usuario no encontrado.\"}";
        
    }//Fin método iniciarSesion.
    
}
