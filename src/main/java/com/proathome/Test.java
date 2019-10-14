
package com.proathome;

import mysql.ConexionMySQL;

public class Test {
    
    public static void main(String args[]){
        ConexionMySQL mysql = new ConexionMySQL();
        mysql.conectar();
        mysql.probarConexion();
        
        
    }
}
