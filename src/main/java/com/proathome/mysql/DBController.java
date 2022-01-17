package com.proathome.mysql;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Marvin
 */
public class DBController {
    
    public String db = "proathome?autoReconnect=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=America/Mexico_City";
    private String url =  "jdbc:mysql://localhost:3306/" + db;
    private String user = "root";
    private String password = "";
    private Connection connection = null;
    private static DBController instance;
    
    private DBController(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
        }catch(Exception ex){ 
            ex.printStackTrace();
        }
    }
    
    public static DBController getInstance(){
        if(instance == null)
            instance = new DBController();
        
        return instance;
    }
    
    public Connection getConnection(){
        return connection;
    }
    
}
