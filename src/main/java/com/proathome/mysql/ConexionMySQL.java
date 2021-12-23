package com.proathome.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
*
* Servicio que crea la conexión a la Base Datos ProAtHome.
*
*/

public class ConexionMySQL {
    
    public String db, url, user, pass;
    static Connection link = null;
    static ConexionMySQL mysql = new ConexionMySQL();
    
    public ConexionMySQL(){  
        
        this.db = "proathome?autoReconnect=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=America/Mexico_City";
        this.url = "jdbc:mysql://localhost:3306/" +db;
        this.user = "root";
        this.pass = "";
        try{
            
            String a = db + url + user + pass;
           
            Class.forName("com.mysql.jdbc.Driver");
            link = DriverManager.getConnection(this.url, this.user, this.pass);
            
        }catch(Exception ex){
            
            System.out.println(ex);
            
        }
         
    }
   
    
    public Connection conectar(){
 
        return link;
        
    }//Fin método conectar.
    
    public static Connection connection(){
        
        link = mysql.conectar();
        
        return link;
        
    }
    
}
