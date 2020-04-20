package com.proathome.modelos;

import com.proathome.modelos.Features.Features;
import com.proathome.modelos.Features.Geometry;
import com.proathome.modelos.Features.Properties;
import com.proathome.mysql.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ObjetoUbicacion {
    
    private String type = "FeatureCollection";
    public Features features[];
    
    public void obtenerSesionesMaps(int idSesiones){
        
        ConexionMySQL mysql = new ConexionMySQL();
        Connection conectar;
        
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement detalles = conectar.prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ?");
                detalles.setInt(1 , idSesiones);
                ResultSet resultado = detalles.executeQuery();
                features = new Features[1];
                
                while(resultado.next()){
                    
                    double doubleLatitud = resultado.getDouble("latitud");
                    double doubleLongitud = resultado.getDouble("longitud");
                    Features feature = new Features();
                    Properties propertie = new Properties();
                    Geometry geometry = new Geometry();
                    geometry.coordinates = new double[1][2];
                    geometry.coordinates[0][0] = doubleLongitud;
                    geometry.coordinates[0][1] = doubleLatitud;
                    propertie.setIdSesion(resultado.getInt("idsesiones"));
                    propertie.setDescription(resultado.getString("nivel"));
                    propertie.setTitle("Nombre");
                    propertie.setLink("Ver Sesión");
                    propertie.setUrl("url");
                    feature.setProperties(propertie);
                    feature.setGeometry(geometry);
                    features[0] = feature;
                    
                }
                
                conectar.close();
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            
            System.out.println("Error en la conexión en obtenerSesionesEstudiantes.");
            
        }
        
    }//Fin método obtenerSesionesMaps.
    
    public void obtenerSesionesEstudiantes(){
        
        ConexionMySQL mysql = new ConexionMySQL();
        Connection conectar;
        
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                Statement obtenerDatos = conectar.createStatement();
                ResultSet resultado = obtenerDatos.executeQuery("SELECT * FROM sesiones");
                
                int numFeatures = 0;
                
                while(resultado.next()){
                    
                    numFeatures++;
                    
                }
                
                features = new Features[numFeatures];
                int aux = 0;
                
                obtenerDatos = conectar.createStatement();
                resultado = obtenerDatos.executeQuery("SELECT * FROM sesiones");
                
                while(resultado.next()){
                    
                    double doubleLatitud = resultado.getDouble("latitud");
                    double doubleLongitud = resultado.getDouble("longitud");
                    Features feature = new Features();
                    Properties propertie = new Properties();
                    Geometry geometry = new Geometry();
                    geometry.coordinates = new double[1][2];
                    geometry.coordinates[0][0] = doubleLongitud;
                    geometry.coordinates[0][1] = doubleLatitud;
                    propertie.setIdSesion(resultado.getInt("idsesiones"));
                    propertie.setDescription(resultado.getString("horario"));
                    propertie.setTitle("Nombre");
                    propertie.setLink("Ver Sesión");
                    propertie.setUrl("url");
                    feature.setProperties(propertie);
                    feature.setGeometry(geometry);
                    features[aux] = feature;
                    aux++;
                    
                }
                
                conectar.close();
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            
            System.out.println("Error en la conexión en obtenerSesionesEstudiantes.");
            
        }
        
    }//Fin método obtenerSesionesEstudiantes.
    
}
