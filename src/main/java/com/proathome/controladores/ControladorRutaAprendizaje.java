package com.proathome.controladores;

import com.proathome.mysql.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONObject;

/**
 *
 * @author Marvin
 */
public class ControladorRutaAprendizaje {
    
    private JSONObject ruta = new JSONObject();
    private Connection conectar;
    public static final int INICIO_RUTA = 1;
    public static final int RUTA_ENCURSO = 2;
    public static final int RUTA_ACTUALIZADA = 3;
    public static final int SECCIONES = 1;
    public static final int BASICO = 2;
    public static final int INTERMEDIO = 3;
    
    public JSONObject obtenerSesionActual(int idEstudiante){
        conectar = ConexionMySQL.connection();
        ruta.clear();
        
        if(conectar != null){
            
            try{
                //CHECAREMOS PRIMERO EL NIVEL 3
                PreparedStatement estado = conectar.prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ?)");
                estado.setInt(1, idEstudiante);
                estado.setInt(2, 3);
                ResultSet resultado = estado.executeQuery();
                
                if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 3 ENTONCES...
                    ruta.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                    ruta.put("idBloque", resultado.getInt("idBloque"));
                    ruta.put("idNivel", resultado.getInt("idNivel"));
                    ruta.put("idSeccion", resultado.getInt("idSeccion"));
                    ruta.put("horas", resultado.getInt("horas"));
                }else{// SI NO, ENTONCES CHECAREMOS EL NIVEL 2
                    estado = null;
                    resultado = null;
                    estado = conectar.prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ?)");
                    estado.setInt(1, idEstudiante);
                    estado.setInt(2, 2);
                    resultado = estado.executeQuery();
                    
                    if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 2 ENTONCES...
                        ruta.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                        ruta.put("idBloque", resultado.getInt("idBloque"));
                        ruta.put("idNivel", resultado.getInt("idNivel"));
                        ruta.put("idSeccion", resultado.getInt("idSeccion"));
                        ruta.put("horas", resultado.getInt("horas"));
                    }else{//SI NO, ENTONCES CHECAREMOS EL NIVEL 1
                        estado = null;
                        resultado = null;
                        estado = conectar.prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ?)");
                        estado.setInt(1, idEstudiante);
                        estado.setInt(2, 1);
                        resultado = estado.executeQuery();
                        
                        if(resultado.next()){//SI TENEMOS ALGÚN REGISTRO ENTONCES...
                            ruta.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                            ruta.put("idBloque", resultado.getInt("idBloque"));
                            ruta.put("idNivel", resultado.getInt("idNivel"));
                            ruta.put("idSeccion", resultado.getInt("idSeccion"));
                            ruta.put("horas", resultado.getInt("horas"));
                        }else{//SI NO, ES DESDE 0 ESTE PEDO.
                            ruta.put("idEstudiante", idEstudiante);
                            ruta.put("idBloque", 1);
                            ruta.put("idNivel", 1);
                            ruta.put("idSeccion", 1);
                            ruta.put("horas", 0);
                        }
                    }
                }
        
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            System.out.println("Error en obtenerSesionActual.");
        }
        
        return ruta;
    }
    
    public void rutaEnCurso(JSONObject datos){
        
        conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement enCurso = conectar.prepareStatement("INSERT INTO rutaaprendizaje (clientes_idclientes, idBloque, idNivel, idSeccion, horas, fecha_registro) VALUES (?,?,?,?,?,?)");
                enCurso.setInt(1, Integer.parseInt(datos.get("idEstudiante").toString()));
                enCurso.setInt(2, Integer.parseInt(datos.get("idBloque").toString()));
                enCurso.setInt(3, Integer.parseInt(datos.get("idNivel").toString()));
                enCurso.setInt(4, Integer.parseInt(datos.get("idSeccion").toString()));
                enCurso.setInt(5, Integer.parseInt(datos.get("horas").toString()));
                enCurso.setString(6, datos.get("fecha_registro").toString());
                enCurso.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en rutaEnCurso");
        }
        
    }
    
    public JSONObject estadoRutaAprendizaje(int idEstudiante, int tipo){
        
        ruta.clear();
        conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
             
                   PreparedStatement estado = conectar.prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ?)");
                    estado.setInt(1, idEstudiante);
                     ResultSet resultado = estado.executeQuery();
                     if(resultado.next()){
                         ruta.put("estado", ControladorRutaAprendizaje.RUTA_ENCURSO);
                         ruta.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                         ruta.put("idBloque", resultado.getInt("idBloque"));
                         ruta.put("idNivel", resultado.getInt("idNivel"));
                         ruta.put("idSeccion", resultado.getInt("idSeccion"));
                         ruta.put("horas", resultado.getInt("horas"));
                         ruta.put("fecha_registro", resultado.getString("fecha_registro"));
                     }else{
                         ruta.put("estado", ControladorRutaAprendizaje.INICIO_RUTA);
                     }
               
            }catch(SQLException ex){
               ex.printStackTrace();
            }          
        }else{
            System.out.println("Error en estadoRutaAprendizaje.");
        }
        
        return ruta;
        
    }
    
}
