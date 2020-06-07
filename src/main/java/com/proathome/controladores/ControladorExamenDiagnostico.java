package com.proathome.controladores;

import com.proathome.mysql.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONObject;

public class ControladorExamenDiagnostico {
    
    private ConexionMySQL mysql = new ConexionMySQL();
    private Connection conectar;
    private JSONObject examen = new JSONObject();
    public static final int INICIO = 1;
    public static final int ENCURSO = 2;
    public static final int CANCELADO = 3;
    
    public void inicioExamenDiagnostico(JSONObject examen){

        conectar = mysql.conectar();
        if(conectar != null){
            try{
                PreparedStatement validar = conectar.prepareStatement("SELECT * FROM diagnostico WHERE clientes_idclientes = ?");
                validar.setInt(1, Integer.parseInt(examen.get("idEstudiante").toString()));
                ResultSet resultado = validar.executeQuery();
                
                if(!resultado.next()){
                    PreparedStatement iniciar = conectar.prepareStatement("INSERT INTO diagnostico (clientes_idclientes, aciertos, preguntaActual, estatus) VALUES (?,?,?,?)");
                    iniciar.setInt(1, Integer.parseInt(examen.get("idEstudiante").toString()));
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

        conectar = mysql.conectar();
        if(conectar != null){
            try{
                PreparedStatement validar = conectar.prepareStatement("SELECT * FROM diagnostico WHERE clientes_idclientes = ?");
                validar.setInt(1, Integer.parseInt(examen.get("idEstudiante").toString()));
                ResultSet resultado = validar.executeQuery();
                
                if(!resultado.next()){
                    PreparedStatement cancelar = conectar.prepareStatement("INSERT INTO diagnostico (clientes_idclientes, aciertos, preguntaActual, estatus) VALUES (?,?,?,?)");
                    cancelar.setInt(1, Integer.parseInt(examen.get("idEstudiante").toString()));
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

        conectar = mysql.conectar();
        if(conectar != null){
            try{
                PreparedStatement encurso = conectar.prepareStatement("UPDATE diagnostico SET aciertos = ?, preguntaActual = ?, estatus = ? WHERE clientes_idclientes = ?");
                encurso.setInt(1, Integer.parseInt(examen.get("aciertos").toString()));
                encurso.setInt(2, Integer.parseInt(examen.get("preguntaActual").toString()));
                encurso.setInt(3, ControladorExamenDiagnostico.ENCURSO);
                encurso.setInt(4, Integer.parseInt(examen.get("idEstudiante").toString()));
                encurso.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en enCursoExamenDiagnostico.");
        }
       
    }    
    
    public JSONObject estatusExamenDiagnostico(int idCliente){

        examen.clear();
        conectar = mysql.conectar();
        System.out.println("Entro");
        if(conectar != null){
            try{
                PreparedStatement estatus = conectar.prepareStatement("SELECT * FROM diagnostico WHERE clientes_idclientes = ?");
                estatus.setInt(1, idCliente);
                ResultSet resultado = estatus.executeQuery();
                
                if(resultado.next()){
                    if(resultado.getInt("estatus") == ControladorExamenDiagnostico.CANCELADO)
                        examen.put("estatus", ControladorExamenDiagnostico.CANCELADO);
                    else if(resultado.getInt("estatus") == ControladorExamenDiagnostico.ENCURSO)
                        examen.put("estatus", ControladorExamenDiagnostico.ENCURSO);
                }else{
                     examen.put("estatus", ControladorExamenDiagnostico.INICIO);
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en statusExamenDiagnostico.");
        }
        System.out.println(examen);
        return examen;
       
    }
    
}
