package com.proathome.controladores;

import com.proathome.mysql.DBController;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONObject;

public class ControladorExamenDiagnostico {
    
    private JSONObject examen = new JSONObject();
    public static final int INICIO = 1;
    public static final int ENCURSO = 2;
    public static final int CANCELADO = 3;
    public static final int EXAMEN_GUARDADO = 7;
    public static final int INFO_EXAMEN = 5;
    public static final int INFO_EXAMEN_FINAL = 6;
    public static final int CONTINUAR_EXAMEN = 8;
    public static final int EXAMEN_FINALIZADO = 10;
    public static final int REINICIAR_EXAMEN = 11;
    
    public JSONObject reiniciarExamenDiagnostico(JSONObject examen){
        
        JSONObject estatus = new JSONObject();
        estatus.clear();
        
        if(DBController.getInstance().getConnection() != null){
            try{            
                PreparedStatement reiniciar = DBController.getInstance().getConnection().prepareStatement("DELETE FROM diagnostico WHERE clientes_idclientes = ?");
                reiniciar.setInt(1, Integer.parseInt(examen.get("idCliente").toString()));
                reiniciar.execute();
                estatus.put("estatus", ControladorExamenDiagnostico.REINICIAR_EXAMEN);
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en reiniciarExamenDiagnostico.");
        }
        
        return estatus;
    }
    
    public JSONObject infoExamenDiagnostico(int idCliente){   
        examen.clear();
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){           
            try{               
                PreparedStatement info = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM diagnostico WHERE clientes_idclientes = ?");
                info.setInt(1, idCliente);
                ResultSet resultado = info.executeQuery();
                
                if(resultado.next()){
                    examen.put("idCliente", resultado.getInt("clientes_idclientes"));
                    examen.put("aciertos", resultado.getInt("aciertos"));
                    examen.put("preguntaActual", resultado.getInt("preguntaActual"));
                    examen.put("estatus", ControladorExamenDiagnostico.CONTINUAR_EXAMEN);
                }   
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", examen);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion a BD.");
            }    
        }else{
            System.out.println("Error en infoExamenDiagnostico.");
        }
        
        return respuesta; 
    }
    
    public JSONObject infoExamenDiagnosticoFinal(int idCliente){       
        examen.clear();
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){    
            try{
                PreparedStatement info = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM diagnostico WHERE clientes_idclientes = ?");
                info.setInt(1, idCliente);
                ResultSet resultado = info.executeQuery();
                
                if(resultado.next()){
                    examen.put("idCliente", resultado.getInt("clientes_idclientes"));
                    examen.put("aciertos", resultado.getInt("aciertos"));
                    examen.put("preguntaActual", resultado.getInt("preguntaActual"));
                    examen.put("estatus", ControladorExamenDiagnostico.INFO_EXAMEN_FINAL);
                }
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", examen);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion a BD.");
            }
        }else{
            System.out.println("Error en infoExamenDiagnostico.");
        }
        
        return respuesta;
    }
    
    public void inicioExamenDiagnostico(JSONObject examen){

        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement validar = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM diagnostico WHERE clientes_idclientes = ?");
                validar.setInt(1, Integer.parseInt(examen.get("idCliente").toString()));
                ResultSet resultado = validar.executeQuery();
                
                if(!resultado.next()){
                    PreparedStatement iniciar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO diagnostico (clientes_idclientes, aciertos, preguntaActual, estatus) VALUES (?,?,?,?)");
                    iniciar.setInt(1, Integer.parseInt(examen.get("idCliente").toString()));
                    iniciar.setInt(2, Integer.parseInt(examen.get("aciertos").toString()));
                    iniciar.setInt(3, Integer.parseInt(examen.get("preguntaActual").toString()));
                    iniciar.setInt(4, ControladorExamenDiagnostico.ENCURSO);
                    iniciar.execute();
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en inicioExamenDiagnostico.");
        }
       
    }
        
    public void cancelarExamenDiagnostico(JSONObject examen){

        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement validar = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM diagnostico WHERE clientes_idclientes = ?");
                validar.setInt(1, Integer.parseInt(examen.get("idCliente").toString()));
                ResultSet resultado = validar.executeQuery();
                
                if(!resultado.next()){
                    PreparedStatement cancelar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO diagnostico (clientes_idclientes, aciertos, preguntaActual, estatus) VALUES (?,?,?,?)");
                    cancelar.setInt(1, Integer.parseInt(examen.get("idCliente").toString()));
                    cancelar.setInt(2, Integer.parseInt(examen.get("aciertos").toString()));
                    cancelar.setInt(3, Integer.parseInt(examen.get("preguntaActual").toString()));
                    cancelar.setInt(4, ControladorExamenDiagnostico.CANCELADO);
                    cancelar.execute();
                }
               
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en examenDiagnóstico.");
        }
       
    }
        
    public void enCursoExamenDiagnostico(JSONObject examen){

        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement aciertos = DBController.getInstance().getConnection().prepareStatement("SELECT aciertos FROM diagnostico WHERE clientes_idclientes = ?");
                aciertos.setInt(1, Integer.parseInt(examen.get("idCliente").toString()));
                ResultSet resultadoAciertos = aciertos.executeQuery();
                
                if(resultadoAciertos.next()){
                    int aciertosAnterior = resultadoAciertos.getInt("aciertos");
                    int aciertosNuevos = aciertosAnterior + Integer.parseInt(examen.get("aciertos").toString());
                    PreparedStatement encurso = DBController.getInstance().getConnection().prepareStatement("UPDATE diagnostico SET aciertos = ?, preguntaActual = ?, estatus = ? WHERE clientes_idclientes = ?");
                    encurso.setInt(1, aciertosNuevos);
                    encurso.setInt(2, Integer.parseInt(examen.get("preguntaActual").toString()));
                    encurso.setInt(3, Integer.parseInt(examen.get("estatus").toString()));
                    encurso.setInt(4, Integer.parseInt(examen.get("idCliente").toString()));
                    encurso.execute();
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en enCursoExamenDiagnostico.");
        }
       
    }    
    
    public JSONObject estatusExamenDiagnostico(int idCliente){
        examen.clear();
        JSONObject respuesta = new JSONObject();
        boolean sesionesFinalizadas = true;
        
        if(DBController.getInstance().getConnection() != null){
            try{               
                //VALIDAR QUE NO EXISTAN SESIONES PENDIENTES DE FINALIZAR
                PreparedStatement sesiones = DBController.getInstance().getConnection().prepareStatement("SELECT finalizado FROM sesiones WHERE clientes_idclientes = ?");
                sesiones.setInt(1, idCliente);
                ResultSet resultadoSesiones = sesiones.executeQuery();
                while(resultadoSesiones.next()){
                    if(!resultadoSesiones.getBoolean("finalizado")){
                        sesionesFinalizadas = false;
                        break;
                    }
                }
                
                PreparedStatement estatus = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM diagnostico WHERE clientes_idclientes = ?");
                estatus.setInt(1, idCliente);
                ResultSet resultado = estatus.executeQuery();
                
                if(resultado.next()){
                    if(resultado.getInt("estatus") == ControladorExamenDiagnostico.CANCELADO)
                        examen.put("estatus", ControladorExamenDiagnostico.CANCELADO);
                    else if(resultado.getInt("estatus") == ControladorExamenDiagnostico.ENCURSO)
                        examen.put("estatus", ControladorExamenDiagnostico.ENCURSO);
                    else if(resultado.getInt("estatus") == ControladorExamenDiagnostico.EXAMEN_FINALIZADO)
                        examen.put("estatus", ControladorExamenDiagnostico.EXAMEN_FINALIZADO);
                }else{
                     examen.put("estatus", ControladorExamenDiagnostico.INICIO);
                }
                examen.put("sesionesFinalizadas", sesionesFinalizadas);
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", examen);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion en BD.");
            }
        }else{
            System.out.println("Error en statusExamenDiagnostico.");
        }

        return respuesta;      
    }
    
}
