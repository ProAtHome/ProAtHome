package com.proathome.controladores;

import com.proathome.modelos.Constantes;
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
    private JSONObject rutaAprendizaje = new JSONObject();
    private JSONObject rutaSumar = new JSONObject();
    private Connection conectar;
    public static final int INICIO_RUTA = 1;
    public static final int RUTA_ENCURSO = 2;
    public static final int RUTA_ACTUALIZADA = 3;
    public static final int SECCIONES = 1;
    public static final int BASICO = 2;
    public static final int INTERMEDIO = 3;
    
    public void sumarClaseRuta(JSONObject json){
        conectar = ConexionMySQL.connection();
        rutaSumar.clear();
        
        if(conectar != null){
            try{
                 //CHECAREMOS PRIMERO EL NIVEL 3
                PreparedStatement estado = conectar.prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ?)");
                estado.setInt(1, Integer.parseInt(json.get("idEstudiante").toString()));
                estado.setInt(2, 3);
                ResultSet resultado = estado.executeQuery();
                
                if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 3 ENTONCES...
                    rutaSumar.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                    rutaSumar.put("idBloque", resultado.getInt("idBloque"));
                    rutaSumar.put("idNivel", resultado.getInt("idNivel"));
                    rutaSumar.put("idSeccion", resultado.getInt("idSeccion"));
                    rutaSumar.put("horas", resultado.getInt("horas"));
                }else{// SI NO, ENTONCES CHECAREMOS EL NIVEL 2
                    estado = null;
                    resultado = null;
                    estado = conectar.prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ?)");
                    estado.setInt(1, Integer.parseInt(json.get("idEstudiante").toString()));
                    estado.setInt(2, 2);
                    resultado = estado.executeQuery();
                    
                    if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 2 ENTONCES...
                        rutaSumar.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                        rutaSumar.put("idBloque", resultado.getInt("idBloque"));
                        rutaSumar.put("idNivel", resultado.getInt("idNivel"));
                        rutaSumar.put("idSeccion", resultado.getInt("idSeccion"));
                        rutaSumar.put("horas", resultado.getInt("horas"));
                    }else{//SI NO, ENTONCES CHECAREMOS EL NIVEL 1
                        estado = null;
                        resultado = null;
                        estado = conectar.prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ?)");
                        estado.setInt(1, Integer.parseInt(json.get("idEstudiante").toString()));
                        estado.setInt(2, 1);
                        resultado = estado.executeQuery();
                        
                        if(resultado.next()){//SI TENEMOS ALGÚN REGISTRO ENTONCES...
                            rutaSumar.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                            rutaSumar.put("idBloque", resultado.getInt("idBloque"));
                            rutaSumar.put("idNivel", resultado.getInt("idNivel"));
                            rutaSumar.put("idSeccion", resultado.getInt("idSeccion"));
                            rutaSumar.put("horas", resultado.getInt("horas"));
                        }else{//SI NO, ES DESDE 0 ESTE PEDO.
                            
                        }
                    }
                }
                
                int horasDeBloque = Constantes.obtenerHorasBloque(Integer.parseInt(json.get("idSeccion").toString()), Integer.parseInt(json.get("idNivel").toString()), Integer.parseInt(json.get("idBloque").toString()));
                int horasA_sumar = Integer.parseInt(json.get("horasA_sumar").toString());
                int horasRegistradas = Integer.parseInt(rutaSumar.get("horas").toString());
                
                int horasTotalesA_sumar = horasRegistradas + horasA_sumar;
                System.out.println("Horar totales a sumar: " + horasTotalesA_sumar);
                System.out.println("Horas registradas más altas: " + horasRegistradas);
                if(Boolean.parseBoolean(json.get("sumar").toString())){
                    if(horasTotalesA_sumar < horasDeBloque){
                        //Registro normal
                        System.out.println("Entro :P");
                        PreparedStatement enCurso = conectar.prepareStatement("INSERT INTO rutaaprendizaje (clientes_idclientes, idBloque, idNivel, idSeccion, horas, fecha_registro) VALUES (?,?,?,?,?,?)");
                        enCurso.setInt(1, Integer.parseInt(json.get("idEstudiante").toString()));
                        enCurso.setInt(2, Integer.parseInt(json.get("idBloque").toString()));
                        enCurso.setInt(3, Integer.parseInt(json.get("idNivel").toString()));
                        enCurso.setInt(4, Integer.parseInt(json.get("idSeccion").toString()));
                        enCurso.setInt(5, horasTotalesA_sumar);
                        enCurso.setString(6, json.get("fecha_registro").toString());
                        enCurso.execute();

                    }else{
                        //Registro de un nuevo bloque.
                        JSONObject nuevoRegistro = Constantes.nuevoRegistro(Integer.parseInt(json.get("idSeccion").toString()), Integer.parseInt(json.get("idNivel").toString()), Integer.parseInt(json.get("idBloque").toString()));
                        PreparedStatement enCurso = conectar.prepareStatement("INSERT INTO rutaaprendizaje (clientes_idclientes, idBloque, idNivel, idSeccion, horas, fecha_registro) VALUES (?,?,?,?,?,?)");
                        enCurso.setInt(1, Integer.parseInt(json.get("idEstudiante").toString()));
                        enCurso.setInt(2, Integer.parseInt(nuevoRegistro.get("idBloque").toString()));
                        enCurso.setInt(3, Integer.parseInt(nuevoRegistro.get("idNivel").toString()));
                        enCurso.setInt(4, Integer.parseInt(nuevoRegistro.get("idSeccion").toString()));
                        enCurso.setInt(5, 0);
                        enCurso.setString(6, json.get("fecha_registro").toString());
                        enCurso.execute();
                    }
                }else{//Registro sin sumar
                    System.out.println("Registro sin sumar.");
                        PreparedStatement enCurso = conectar.prepareStatement("INSERT INTO rutaaprendizaje (clientes_idclientes, idBloque, idNivel, idSeccion, horas, fecha_registro) VALUES (?,?,?,?,?,?)");
                        enCurso.setInt(1, Integer.parseInt(json.get("idEstudiante").toString()));
                        enCurso.setInt(2, Integer.parseInt(json.get("idBloque").toString()));
                        enCurso.setInt(3, Integer.parseInt(json.get("idNivel").toString()));
                        enCurso.setInt(4, Integer.parseInt(json.get("idSeccion").toString()));
                        enCurso.setInt(5, Integer.parseInt(json.get("horasA_sumar").toString()));
                        enCurso.setString(6, json.get("fecha_registro").toString());
                        enCurso.execute();
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en sumarClaseRuta.");
        }
    }
    
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
        
        rutaAprendizaje.clear();
        conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                //CHECAREMOS PRIMERO EL NIVEL 3
                PreparedStatement estado = conectar.prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ?)");
                estado.setInt(1, idEstudiante);
                estado.setInt(2, 3);
                ResultSet resultado = estado.executeQuery();
                
                if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 3 ENTONCES...
                    rutaAprendizaje.put("estado", ControladorRutaAprendizaje.RUTA_ENCURSO);
                    rutaAprendizaje.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                    rutaAprendizaje.put("idBloque", resultado.getInt("idBloque"));
                    rutaAprendizaje.put("idNivel", resultado.getInt("idNivel"));
                    rutaAprendizaje.put("idSeccion", resultado.getInt("idSeccion"));
                    rutaAprendizaje.put("horas", resultado.getInt("horas"));
                    rutaAprendizaje.put("fecha_registro", resultado.getString("fecha_registro"));
                }else{// SI NO, ENTONCES CHECAREMOS EL NIVEL 2
                    estado = null;
                    resultado = null;
                    estado = conectar.prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ?)");
                    estado.setInt(1, idEstudiante);
                    estado.setInt(2, 2);
                    resultado = estado.executeQuery();
                    
                    if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 2 ENTONCES...
                        rutaAprendizaje.put("estado", ControladorRutaAprendizaje.RUTA_ENCURSO);
                        rutaAprendizaje.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                        rutaAprendizaje.put("idBloque", resultado.getInt("idBloque"));
                        rutaAprendizaje.put("idNivel", resultado.getInt("idNivel"));
                        rutaAprendizaje.put("idSeccion", resultado.getInt("idSeccion"));
                        rutaAprendizaje.put("horas", resultado.getInt("horas"));
                        rutaAprendizaje.put("fecha_registro", resultado.getString("fecha_registro"));
                    }else{//SI NO, ENTONCES CHECAREMOS EL NIVEL 1
                        estado = null;
                        resultado = null;
                        estado = conectar.prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ?)");
                        estado.setInt(1, idEstudiante);
                        estado.setInt(2, 1);
                        resultado = estado.executeQuery();
                        
                        if(resultado.next()){//SI TENEMOS ALGÚN REGISTRO ENTONCES...
                            rutaAprendizaje.put("estado", ControladorRutaAprendizaje.RUTA_ENCURSO);
                            rutaAprendizaje.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                            rutaAprendizaje.put("idBloque", resultado.getInt("idBloque"));
                            rutaAprendizaje.put("idNivel", resultado.getInt("idNivel"));
                            rutaAprendizaje.put("idSeccion", resultado.getInt("idSeccion"));
                            rutaAprendizaje.put("horas", resultado.getInt("horas"));
                            rutaAprendizaje.put("fecha_registro", resultado.getString("fecha_registro"));
                        }else{//SI NO, ES DESDE 0 ESTE PEDO.
                            rutaAprendizaje.put("estado", ControladorRutaAprendizaje.INICIO_RUTA);
                        }
                    }
                }
               
            }catch(SQLException ex){
               ex.printStackTrace();
            }          
        }else{
            System.out.println("Error en estadoRutaAprendizaje.");
        }
        
        return rutaAprendizaje;
        
    }
    
}
