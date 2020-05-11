package com.proathome.controladores;

import com.proathome.modelos.Sesion;
import com.proathome.mysql.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.simple.JSONObject;

public class ControladorSesion {

    private Sesion sesiones[];

    public void obtenerSesiones(int idCliente) {

        ConexionMySQL mysql = new ConexionMySQL();
        Connection conectar;

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                Statement estado = conectar.createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM sesiones WHERE clientes_idclientes = " + idCliente);
                int numeroSesiones = 0;
                
                while(resultado.next()){
                    
                    numeroSesiones++;
                    
                }
                
                sesiones = new Sesion[numeroSesiones];
                int aux = 0;
                
                estado = conectar.createStatement();
                resultado = estado.executeQuery("SELECT * FROM sesiones WHERE clientes_idclientes = " + idCliente  + " ORDER BY idsesiones DESC");
                
                
                while(resultado.next()){
                    
                    Sesion obtenida = new Sesion();
                    obtenida.setIdsesiones(resultado.getInt("idsesiones"));
                    String idProfesor = resultado.getString("profesores_idprofesores");
                    
                    if(idProfesor == null){
                        
                        obtenida.setProfesor("Sin profesor asignado.");
 
                    }else{
                        
                        PreparedStatement profesor = conectar.prepareStatement("SELECT nombre FROM profesores WHERE idprofesores = ?");
                        profesor.setInt(1 , resultado.getInt("profesores_idprofesores"));
                        ResultSet nombreProfesor = profesor.executeQuery();
                        
                        if(nombreProfesor.next()){
                            
                            obtenida.setProfesor(nombreProfesor.getString("nombre"));
                            
                        }else{
                            
                            obtenida.setProfesor("Error al obtener profesor.");
                            
                        }
  
                    }
                    
                    obtenida.setClientes_idclientes(resultado.getInt("clientes_idclientes"));
                    obtenida.setHorario(resultado.getString("horario"));
                    obtenida.setLugar(resultado.getString("lugar"));
                    obtenida.setTiempo(resultado.getString("tiempo"));
                    obtenida.setNivel(resultado.getString("nivel"));
                    obtenida.setExtras(resultado.getString("extras"));
                    obtenida.setTipoClase(resultado.getString("tipoClase"));
                    obtenida.setLatitud(resultado.getDouble("latitud"));
                    obtenida.setLongitud(resultado.getDouble("longitud"));
                    obtenida.setActualizado(resultado.getString("actualizado"));
                    sesiones[aux] = obtenida;
                    aux++;
                    
                }
                
                conectar.close();
                
            } catch (SQLException ex) {

                ex.printStackTrace();

            }

        } else {

            System.out.println("Error en la coenxión en obtenerSesiones.");

        }

    }//Fin método obtenerSesiones.
    
    public void eliminarSesion(int idSesion){
        
        ConexionMySQL mysql = new ConexionMySQL();
        Connection conectar;
        
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement eliminar = conectar.prepareStatement("DELETE FROM sesiones WHERE idsesiones = ?");
                eliminar.setInt(1 , idSesion);
                eliminar.execute();
                conectar.close();
                
            }catch(SQLException ex){
                
                ex.printStackTrace();
                
            }
            
        }else{
            
            System.out.println("Error en eliminarSesion.");
            
        }
        
    }//Fin método eliminarSesion.
    
    public void actualizarSesion(JSONObject jsonDatos){
        System.out.println(jsonDatos);
        ConexionMySQL mysql = new ConexionMySQL();
        Connection conectar;
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement actualizar = conectar.prepareStatement("UPDATE sesiones SET horario = ?, lugar = ?, tiempo = ?, nivel = ?, tipoClase = ?, extras = ?, latitud = ?, longitud = ?, actualizado = ? WHERE idsesiones = ?");
                actualizar.setString(1 , jsonDatos.get("horario").toString());
                actualizar.setString(2 , jsonDatos.get("lugar").toString());
                actualizar.setString(3 , jsonDatos.get("tiempo").toString());
                actualizar.setString(4 , jsonDatos.get("nivel").toString());
                actualizar.setString(5 , jsonDatos.get("tipoClase").toString());
                actualizar.setString(6 , jsonDatos.get("observaciones").toString());
                actualizar.setDouble(7 , Double.parseDouble(jsonDatos.get("latitud").toString()));
                actualizar.setDouble(8 , Double.parseDouble(jsonDatos.get("longitud").toString()));
                actualizar.setString(9 , jsonDatos.get("actualizado").toString());
                actualizar.setInt(10 , Integer.parseInt(jsonDatos.get("idSesion").toString()));
                actualizar.execute();
                conectar.close();
                
            }catch(SQLException ex){
                
                ex.printStackTrace();
                
            }
            
        }else{
            
            System.out.println("Error en actualizarSesion.");
            
        }   
        
    }//Fin método actualizarSesion.

}
