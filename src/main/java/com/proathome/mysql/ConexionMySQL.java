package com.proathome.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
*
* Clase que crea la conexión a la Base Datos ProAtHome.
*
*/

public class ConexionMySQL {
    
    public String db, url, user, pass;
    
    public ConexionMySQL(){  
        
         this.db = "proathome?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=America/Mexico_City";
         this.url = "jdbc:mysql://localhost:3306/" +db;
         this.user = "root";
         this.pass = "";
         
    }
    
    public Connection conectar(){
        
        Connection link = null;
        
        try{
            
            String a = db + url +user + pass;
   
            Class.forName("com.mysql.jdbc.Driver");
            link = DriverManager.getConnection(this.url, this.user, this.pass);
            
        }catch(Exception ex){
            
            System.out.println(ex);
            
        }
        
        return link;
    }//Fin método conectar.
    
    public void probarConexion(){
        
        ConexionMySQL mysql = new ConexionMySQL();
        java.sql.Connection conectar = mysql.conectar();
        
        if(conectar != null){
            
            System.out.println("Estás conectado.");
            
            try{
                
                conectar.close();
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }
        }
        
    }//Fin método probarConexión.
    
}
