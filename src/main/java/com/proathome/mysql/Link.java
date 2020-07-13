/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proathome.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marvin
 */
public class Link {
    
    static Connection link;
    
    public static Connection link (){
        try {
            String db = "proathome?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=America/Mexico_City";
            String url = "jdbc:mysql://localhost:3306/" +db;
            String user = "root";
            String pass = "";
            link = DriverManager.getConnection(url, user, pass);
            System.out.println("Errrr");
            
        } catch (SQLException ex) {
            Logger.getLogger(Link.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Connection con = link;
        
        return con;
        
    }
    
}
