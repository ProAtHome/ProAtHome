package com.proathome.controladores;

import com.proathome.modelos.Constantes;
import com.proathome.mysql.DBController;
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
    public static final int INICIO_RUTA = 1;
    public static final int RUTA_ENCURSO = 2;
    public static final int RUTA_ACTUALIZADA = 3;
    public static final int SECCIONES = 1;
    public static final int BASICO = 2;
    public static final int INTERMEDIO = 3;
    
    public boolean esRutaMenor(int idSeccionRecomendado, int idNivelRecomendado, int idBloqueRecomendado, int idCliente){
        boolean menor = false;
        
        //CONSULTAMOS NIVEL ACTUAL.
        JSONObject data = obtenerSesionActual(idCliente);
        JSONObject jsonNivelActual = (JSONObject) data.get("mensaje");
        int idSeccionActual = Integer.parseInt(jsonNivelActual.get("idSeccion").toString());
        int idNivelActual = Integer.parseInt(jsonNivelActual.get("idNivel").toString());
        int idBloqueActual = Integer.parseInt(jsonNivelActual.get("idBloque").toString());
        
        if(idSeccionActual < idSeccionRecomendado){
            //REGISTRAMOS POR QUE LA SECCION RECOMENDADA ES MAYOR Y NO EXISTEN REGISTRO DE ESA NUEVA SECCION.
            menor = false;
        }else if(idSeccionActual == idSeccionRecomendado){
            //EVALUAMOS EL NIVEL
            if(idNivelActual < idNivelRecomendado){
                //REGISTRAMOS POR QUE EL NIVEL RECOMENDADO ES MAYOR Y NO EXISTEN REGISTRO DE ESA NUEVA SECCION.
                menor = false;
            }else if(idNivelActual == idNivelRecomendado){
                    //NO REGISTRAMOS POR QUE SI EL BLOQUE ACTUAL ES 0, ENTONCES NO VAMOS A REPETIR REGISTRO
                    // Y SI EL BLOQUE ACTUAL TIENE HORAS REALIZADAS ES MAYOR Y VAMOS ADELANTADOS.
                     System.out.println("NO REGISTRAMOS POR EL BLOQUE");
                menor = true;
            }else if(idNivelActual > idNivelRecomendado){
                //NO REGISTRAMOS POR QUE VAMOS ADELANTADOS Y SOLO TENDREMOS QUE REPASAR.
                System.out.println("NO REGISTRAMOS");
                menor = true;
            }
        }else if(idSeccionActual > idSeccionRecomendado){
            //NO REGISTRAMOS POR QUE VAMOS ADELANTADOS Y SOLO TENDREMOS QUE REPASAR.
             System.out.println("NO REGISTRAMOS");
            menor = true;
        }
         
        
        return menor;
    }
    
    public void nuevaRuta(JSONObject json){
        
        if(DBController.getInstance().getConnection() != null){
            try{
                
                //EVALUAR QUE la RUTA RECOMENTADA NO SEA MENOR QUE LA RUTA ACTUAL
                if(!esRutaMenor(Integer.parseInt(json.get("idSeccion").toString()), Integer.parseInt(json.get("idNivel").toString()),
                        Integer.parseInt(json.get("idBloque").toString()), Integer.parseInt(json.get("idCliente").toString()))){
                    PreparedStatement nueva = DBController.getInstance().getConnection().prepareStatement("INSERT INTO rutaaprendizaje (clientes_idclientes, idBloque, idNivel, idSeccion, horas, fecha_registro, enruta) VALUES (?,?,?,?,?,?,?)");
                    nueva.setInt(1, Integer.parseInt(json.get("idCliente").toString()));
                    nueva.setInt(2, Integer.parseInt(json.get("idBloque").toString()));
                    nueva.setInt(3, Integer.parseInt(json.get("idNivel").toString()));
                    nueva.setInt(4, Integer.parseInt(json.get("idSeccion").toString()));
                    nueva.setInt(5, Integer.parseInt(json.get("horas").toString()));
                    nueva.setString(6, json.get("fecha_registro").toString());
                    nueva.setBoolean(7, Boolean.parseBoolean(json.get("sumar").toString()));
                    nueva.execute();
                }
              
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en la conexión nuevaRuta");
        }
        
    }
    
    public JSONObject sumarServicioRuta(JSONObject json){
        rutaSumar.clear();
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                 //CHECAREMOS PRIMERO EL NIVEL 3
                PreparedStatement estado = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ? AND enruta = ?)");
                estado.setInt(1, Integer.parseInt(json.get("idCliente").toString()));
                estado.setInt(2, 3);
                estado.setBoolean(3, true);
                ResultSet resultado = estado.executeQuery();
                
                if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 3 ENTONCES...
                    rutaSumar.put("idCliente", resultado.getInt("clientes_idclientes"));
                    rutaSumar.put("idBloque", resultado.getInt("idBloque"));
                    rutaSumar.put("idNivel", resultado.getInt("idNivel"));
                    rutaSumar.put("idSeccion", resultado.getInt("idSeccion"));
                    rutaSumar.put("horas", resultado.getInt("horas"));
                }else{// SI NO, ENTONCES CHECAREMOS EL NIVEL 2
                    estado = null;
                    resultado = null;
                    estado = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ? AND enruta = ?)");
                    estado.setInt(1, Integer.parseInt(json.get("idCliente").toString()));
                    estado.setInt(2, 2);
                    estado.setBoolean(3, true);
                    resultado = estado.executeQuery();
                    
                    if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 2 ENTONCES...
                        rutaSumar.put("idCliente", resultado.getInt("clientes_idclientes"));
                        rutaSumar.put("idBloque", resultado.getInt("idBloque"));
                        rutaSumar.put("idNivel", resultado.getInt("idNivel"));
                        rutaSumar.put("idSeccion", resultado.getInt("idSeccion"));
                        rutaSumar.put("horas", resultado.getInt("horas"));
                    }else{//SI NO, ENTONCES CHECAREMOS EL NIVEL 1
                        estado = null;
                        resultado = null;
                        estado = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ? AND enruta = ?)");
                        estado.setInt(1, Integer.parseInt(json.get("idCliente").toString()));
                        estado.setInt(2, 1);
                        estado.setBoolean(3, true);
                        resultado = estado.executeQuery();
                        
                        if(resultado.next()){//SI TENEMOS ALGÚN REGISTRO ENTONCES...
                            rutaSumar.put("idCliente", resultado.getInt("clientes_idclientes"));
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
                boolean ultimaSesion = false;
                
                int horasTotalesA_sumar = horasRegistradas + horasA_sumar;
                System.out.println("Horar totales a sumar: " + horasTotalesA_sumar);
                System.out.println("Horas registradas más altas: " + horasRegistradas);
                if(Boolean.parseBoolean(json.get("sumar").toString())){
                    if(horasTotalesA_sumar < horasDeBloque){
                        //Registro normal
                        System.out.println("Entro :P");
                        PreparedStatement enCurso = DBController.getInstance().getConnection().prepareStatement("INSERT INTO rutaaprendizaje (clientes_idclientes, idBloque, idNivel, idSeccion, horas, fecha_registro, enruta) VALUES (?,?,?,?,?,?,?)");
                        enCurso.setInt(1, Integer.parseInt(json.get("idCliente").toString()));
                        enCurso.setInt(2, Integer.parseInt(json.get("idBloque").toString()));
                        enCurso.setInt(3, Integer.parseInt(json.get("idNivel").toString()));
                        enCurso.setInt(4, Integer.parseInt(json.get("idSeccion").toString()));
                        enCurso.setInt(5, horasTotalesA_sumar);
                        enCurso.setString(6, json.get("fecha_registro").toString());
                        enCurso.setBoolean(7, true);
                        enCurso.execute();

                    }else{
                        
                        //Si es el último nivel
                        if(Integer.parseInt(json.get("idSeccion").toString()) == 3 && Integer.parseInt(json.get("idNivel").toString()) == 5
                                && Integer.parseInt(json.get("idBloque").toString()) == 2)
                                    ultimaSesion = true;
                        //Registro de un nuevo bloque siempre y cuando no sea el último.
                        if(ultimaSesion){
                            //CAMBIAR A FALSE EL ESTATUS DE LA RUTA
                            System.out.println("FALSE ESTATUS RUTA");
                            PreparedStatement estatusRuta = DBController.getInstance().getConnection().prepareStatement("UPDATE clientes SET ruta_finalizada = ? WHERE idclientes = ?");
                            estatusRuta.setBoolean(1, true);
                            estatusRuta.setInt(2, Integer.parseInt(json.get("idCliente").toString()));
                            estatusRuta.execute();
                        }else{
                            JSONObject nuevoRegistro = Constantes.nuevoRegistro(Integer.parseInt(json.get("idSeccion").toString()), Integer.parseInt(json.get("idNivel").toString()), Integer.parseInt(json.get("idBloque").toString()));
                            PreparedStatement enCurso = DBController.getInstance().getConnection().prepareStatement("INSERT INTO rutaaprendizaje (clientes_idclientes, idBloque, idNivel, idSeccion, horas, fecha_registro, enruta) VALUES (?,?,?,?,?,?,?)");
                            enCurso.setInt(1, Integer.parseInt(json.get("idCliente").toString()));
                            enCurso.setInt(2, Integer.parseInt(nuevoRegistro.get("idBloque").toString()));
                            enCurso.setInt(3, Integer.parseInt(nuevoRegistro.get("idNivel").toString()));
                            enCurso.setInt(4, Integer.parseInt(nuevoRegistro.get("idSeccion").toString()));
                            enCurso.setInt(5, 0);
                            enCurso.setString(6, json.get("fecha_registro").toString());
                            enCurso.setBoolean(7, true);
                            enCurso.execute();
                        }
                    }
                }else{//Registro sin sumar
                    System.out.println("Registro sin sumar.");
                        PreparedStatement enCurso = DBController.getInstance().getConnection().prepareStatement("INSERT INTO rutaaprendizaje (clientes_idclientes, idBloque, idNivel, idSeccion, horas, fecha_registro) VALUES (?,?,?,?,?,?)");
                        enCurso.setInt(1, Integer.parseInt(json.get("idCliente").toString()));
                        enCurso.setInt(2, Integer.parseInt(json.get("idBloque").toString()));
                        enCurso.setInt(3, Integer.parseInt(json.get("idNivel").toString()));
                        enCurso.setInt(4, Integer.parseInt(json.get("idSeccion").toString()));
                        enCurso.setInt(5, Integer.parseInt(json.get("horasA_sumar").toString()));
                        enCurso.setString(6, json.get("fecha_registro").toString());
                        enCurso.execute();
                }
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", ultimaSesion);
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error, intente de nuevo más tarde.");
            System.out.println("Error en sumarServicioRuta.");
        }
        
        return respuesta;
    }
    
    public boolean getEstatusRutaFinalizada(int idCliente){
        boolean finalizado = false;
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT ruta_finalizada FROM clientes WHERE idclientes = ?");
                consulta.setInt(1, idCliente);
                ResultSet resultado = consulta.executeQuery();
            if(resultado.next())
                finalizado = resultado.getBoolean("ruta_finalizada");
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else
            System.out.println("Error en getEstatusRutaFinalizada");
        
        return finalizado;
    }
    
    public JSONObject obtenerSesionActual(int idCliente){
        JSONObject respuesta = new JSONObject();
        ruta.clear();
        
        if(DBController.getInstance().getConnection() != null){           
            try{
                //Obtener valor RUTA_FINALIZADA
                boolean estatusRuta = getEstatusRutaFinalizada(idCliente);
                
                //CHECAREMOS PRIMERO EL NIVEL 3
                PreparedStatement estado = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ? AND enruta = ?)");
                estado.setInt(1, idCliente);
                estado.setInt(2, 3);
                estado.setBoolean(3, true);
                ResultSet resultado = estado.executeQuery();
                
                if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 3 ENTONCES...
                    ruta.put("idCliente", resultado.getInt("clientes_idclientes"));
                    ruta.put("idBloque", resultado.getInt("idBloque"));
                    ruta.put("idNivel", resultado.getInt("idNivel"));
                    ruta.put("idSeccion", resultado.getInt("idSeccion"));
                    ruta.put("horas", resultado.getInt("horas"));
                }else{// SI NO, ENTONCES CHECAREMOS EL NIVEL 2
                    estado = null;
                    resultado = null;
                    estado = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ? AND enruta = ?)");
                    estado.setInt(1, idCliente);
                    estado.setInt(2, 2);
                    estado.setBoolean(3, true);
                    resultado = estado.executeQuery();
                    
                    if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 2 ENTONCES...
                        ruta.put("idCliente", resultado.getInt("clientes_idclientes"));
                        ruta.put("idBloque", resultado.getInt("idBloque"));
                        ruta.put("idNivel", resultado.getInt("idNivel"));
                        ruta.put("idSeccion", resultado.getInt("idSeccion"));
                        ruta.put("horas", resultado.getInt("horas"));
                    }else{//SI NO, ENTONCES CHECAREMOS EL NIVEL 1
                        estado = null;
                        resultado = null;
                        estado = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ? AND enruta = ?)");
                        estado.setInt(1, idCliente);
                        estado.setInt(2, 1);
                        estado.setBoolean(3, true);
                        resultado = estado.executeQuery();
                        
                        if(resultado.next()){//SI TENEMOS ALGÚN REGISTRO ENTONCES...
                            ruta.put("idCliente", resultado.getInt("clientes_idclientes"));
                            ruta.put("idBloque", resultado.getInt("idBloque"));
                            ruta.put("idNivel", resultado.getInt("idNivel"));
                            ruta.put("idSeccion", resultado.getInt("idSeccion"));
                            ruta.put("horas", resultado.getInt("horas"));
                        }else{//SI NO, ES DESDE 0 ESTE PEDO.
                            ruta.put("idCliente", idCliente);
                            ruta.put("idBloque", 1);
                            ruta.put("idNivel", 1);
                            ruta.put("idSeccion", 1);
                            ruta.put("horas", 0);
                        }
                    }
                }
                
                ruta.put("rutaFinalizada", estatusRuta);
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", ruta);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error, intente de nuevo más tarde.");
            }           
        }else{
            System.out.println("Error en obtenerSesionActual.");
        }
        
        return respuesta;
    }
    
    public void rutaEnCurso(JSONObject datos){
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement enCurso = DBController.getInstance().getConnection().prepareStatement("INSERT INTO rutaaprendizaje (clientes_idclientes, idBloque, idNivel, idSeccion, horas, fecha_registro) VALUES (?,?,?,?,?,?)");
                enCurso.setInt(1, Integer.parseInt(datos.get("idCliente").toString()));
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
    
    public JSONObject estadoRutaAprendizaje(int idCliente, int tipo){
        JSONObject respuesta = new JSONObject();
        rutaAprendizaje.clear();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                //CHECAREMOS PRIMERO EL NIVEL 3
                PreparedStatement estado = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ? AND enruta = ?)");
                estado.setInt(1, idCliente);
                estado.setInt(2, 3);
                estado.setBoolean(3, true);
                ResultSet resultado = estado.executeQuery();
                
                if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 3 ENTONCES...
                    rutaAprendizaje.put("estado", ControladorRutaAprendizaje.RUTA_ENCURSO);
                    rutaAprendizaje.put("idCliente", resultado.getInt("clientes_idclientes"));
                    rutaAprendizaje.put("idBloque", resultado.getInt("idBloque"));
                    rutaAprendizaje.put("idNivel", resultado.getInt("idNivel"));
                    rutaAprendizaje.put("idSeccion", resultado.getInt("idSeccion"));
                    rutaAprendizaje.put("horas", resultado.getInt("horas"));
                    rutaAprendizaje.put("fecha_registro", resultado.getString("fecha_registro"));
                }else{// SI NO, ENTONCES CHECAREMOS EL NIVEL 2
                    estado = null;
                    resultado = null;
                    estado = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ? AND enruta = ?)");
                    estado.setInt(1, idCliente);
                    estado.setInt(2, 2);
                    estado.setBoolean(3, true);
                    resultado = estado.executeQuery();
                    
                    if(resultado.next()){//SI TENEMOS EN CURSO EL NIVEL 2 ENTONCES...
                        rutaAprendizaje.put("estado", ControladorRutaAprendizaje.RUTA_ENCURSO);
                        rutaAprendizaje.put("idCliente", resultado.getInt("clientes_idclientes"));
                        rutaAprendizaje.put("idBloque", resultado.getInt("idBloque"));
                        rutaAprendizaje.put("idNivel", resultado.getInt("idNivel"));
                        rutaAprendizaje.put("idSeccion", resultado.getInt("idSeccion"));
                        rutaAprendizaje.put("horas", resultado.getInt("horas"));
                        rutaAprendizaje.put("fecha_registro", resultado.getString("fecha_registro"));
                    }else{//SI NO, ENTONCES CHECAREMOS EL NIVEL 1
                        estado = null;
                        resultado = null;
                        estado = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND idSeccion = ? AND enruta = ?)");
                        estado.setInt(1, idCliente);
                        estado.setInt(2, 1);
                        estado.setBoolean(3, true);
                        resultado = estado.executeQuery();
                        
                        if(resultado.next()){//SI TENEMOS ALGÚN REGISTRO ENTONCES...
                            rutaAprendizaje.put("estado", ControladorRutaAprendizaje.RUTA_ENCURSO);
                            rutaAprendizaje.put("idCliente", resultado.getInt("clientes_idclientes"));
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
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", rutaAprendizaje);
            }catch(SQLException ex){
               ex.printStackTrace();
               respuesta.put("respuesta", false);
               respuesta.put("mensaje", "Error en la conexion a BD.");
            }          
        }else{
            System.out.println("Error en estadoRutaAprendizaje.");
        }
        
        return respuesta;
        
    }
    
}
