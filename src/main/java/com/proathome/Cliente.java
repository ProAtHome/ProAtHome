
package com.proathome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import mysql.ConexionMySQL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
 *
 * Clase que creará un nuevo cliente y podrá realizar distintas acciones con él.
 * 
 */
public class Cliente {
   
    String nombre, correo, contrasena;
    java.sql.Date fechaNacimiento, fechaRegistro;
    int edad;
    
    public Cliente(JSONObject jsonCliente){
        
        this.nombre = String.valueOf(jsonCliente.get("nombre"));
        this.correo = String.valueOf(jsonCliente.get("correo"));
        this.contrasena = String.valueOf(jsonCliente.get("contrasena"));
        
        //Formateo de fechas a tipo SQL Date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dateFechaNacimiento = (java.sql.Date.valueOf(String.valueOf(jsonCliente.get("fechaNacimiento"))));
        java.sql.Date dateFechaRegistro = (java.sql.Date.valueOf(String.valueOf(jsonCliente.get("fechaRegistro"))));
        
        this.fechaNacimiento = dateFechaNacimiento;
        this.fechaRegistro = dateFechaRegistro;
        this.edad = Integer.parseInt(String.valueOf(jsonCliente.get("edad")));
        
        
        
    }
    
    public void agregarCliente(){
        
        ConexionMySQL mysql = new ConexionMySQL();
        Connection conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                String query = "INSERT INTO clientes (nombre, correo, contrasena, edad, fechaNacimiento, fechaDeRegistro) VALUES (?,?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setString(1, this.nombre);
                agregarDatos.setString(2, this.correo);
                agregarDatos.setString(3, this.contrasena);
                agregarDatos.setInt(4, this.edad);
                agregarDatos.setDate(5, this.fechaNacimiento);
                agregarDatos.setDate(6, this.fechaRegistro);
                agregarDatos.execute();
                
                conectar.close();
                
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }
            
            
        }else{
            
            System.out.println("Error en la conexión agregarCliente");
            
        }
        
    }
}
