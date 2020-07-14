package com.proathome.controladores;

import com.proathome.modelos.Sesion;
import com.proathome.mysql.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

public class ControladorSesion {

    private Sesion sesiones[];
    
    public void cambiarEstatusClaseEstudiante(int idSesion, int idEstudiante, int estatus){
        
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND clientes_idclientes = ?");
                consulta.setInt(1, estatus);
                consulta.setInt(2, idSesion);
                consulta.setInt(3, idEstudiante);
                consulta.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en cambiarEstatusClaseEstudiante.");
        }
        
    }
    
    public void cambiarEstatusClaseProfesor(int idSesion, int idProfesor, int estatus){
        
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND profesores_idprofesores = ?");
                consulta.setInt(1, estatus);
                consulta.setInt(2, idSesion);
                consulta.setInt(3, idProfesor);
                consulta.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en cambiarEstatusClaseEstudiante.");
        }
        
    }
    
       public JSONObject validarEstatusClaseProfesor(int idSesion, int idProfesor){
        
        Connection conectar = ConexionMySQL.connection();
        JSONObject datosSesion = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ? AND profesores_idprofesores = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idProfesor);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    datosSesion.put("idSeccion", resultado.getInt("idSeccion"));
                    datosSesion.put("idNivel", resultado.getInt("idNivel"));
                    datosSesion.put("idBloque", resultado.getInt("idBloque"));
                    datosSesion.put("profDsiponible", resultado.getBoolean("profDisponible"));
                    datosSesion.put("estaus", resultado.getInt("estatus"));
                    datosSesion.put("progreso", resultado.getInt("progreso"));
                }else{
                    datosSesion.put("error", "Error en la consulta.");
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
         
        }else{
            System.out.println("Error en validarEstatusClase.");
        }
        
        return datosSesion;
        
    }
    
    public JSONObject validarEstatusClaseEstudiante(int idSesion, int idEstudiante){
        
        Connection conectar = ConexionMySQL.connection();
        JSONObject datosSesion = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ? AND clientes_idclientes = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idEstudiante);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    datosSesion.put("idSeccion", resultado.getInt("idSeccion"));
                    datosSesion.put("idNivel", resultado.getInt("idNivel"));
                    datosSesion.put("idBloque", resultado.getInt("idBloque"));
                    datosSesion.put("profDsiponible", resultado.getBoolean("profDisponible"));
                    datosSesion.put("estaus", resultado.getInt("estatus"));
                    datosSesion.put("progreso", resultado.getInt("progreso"));
                }else{
                    datosSesion.put("error", "Error en la consulta.");
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
         
        }else{
            System.out.println("Error en validarEstatusClase.");
        }
        
        return datosSesion;
        
    }
    
    public void claseDisponibleProfesor(int idSesion, int idProfesor, boolean disponible){
        
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("UPDATE sesiones SET profDisponible = ? WHERE idsesiones = ? AND profesores_idprofesores = ?");
                consulta.setBoolean(1, disponible);
                consulta.setInt(2, idSesion);
                consulta.setInt(3, idProfesor);
                consulta.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en claseDsiponible");
        }
        
    }
    
    public JSONObject sincronizarClaseProfesor(int idSesion, int idPrfoesor){
    
        JSONObject jsonResultado = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        PreparedStatement consulta;
        
        if(conectar != null){
            try{
                consulta = conectar.prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ? AND profesores_idprofesores = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idPrfoesor);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    jsonResultado.put("dispEstudiante", resultado.getBoolean("estDisponible"));
                }else{
                    jsonResultado.put("error", "Error en la consulta.");
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en sincronixarClase.");
        }
        
        return jsonResultado;
        
    }
    
    public void claseDisponible(int idSesion, int idEstudiante, boolean disponible){
        
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("UPDATE sesiones SET estDisponible = ? WHERE idsesiones = ? AND clientes_idclientes = ?");
                consulta.setBoolean(1, disponible);
                consulta.setInt(2, idSesion);
                consulta.setInt(3, idEstudiante);
                consulta.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en claseDsiponible");
        }
        
    }
    
    public JSONObject sincronizarClase(int idSesion, int idEstudiante){
    
        Connection conectar = ConexionMySQL.connection();
        JSONObject jsonResultado = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ? AND clientes_idclientes = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idEstudiante);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    jsonResultado.put("dispProfesor", resultado.getBoolean("profDisponible"));
                }else{
                    jsonResultado.put("error", "Error en la consulta.");
                }
             
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en sincronixarClase.");
        }
        
        return jsonResultado;
        
    }

    public void obtenerSesiones(int idCliente) {

        Connection conectar = ConexionMySQL.connection();

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
                        obtenida.setFotoProfesor("Sin foto");
                        obtenida.setDescripcionProfesor("Sin descripcion");
                        obtenida.setCorreoProfesor("Sin correo");
 
                    }else{
                        
                        PreparedStatement profesor = conectar.prepareStatement("SELECT * FROM profesores WHERE idprofesores = ?");
                        profesor.setInt(1 , resultado.getInt("profesores_idprofesores"));
                        ResultSet nombreProfesor = profesor.executeQuery();
                        
                        if(nombreProfesor.next()){ 
                            obtenida.setProfesor(nombreProfesor.getString("nombre"));  
                            obtenida.setCorreoProfesor(nombreProfesor.getString("correo"));
                            obtenida.setDescripcionProfesor(nombreProfesor.getString("descripcion"));
                            obtenida.setFotoProfesor(nombreProfesor.getString("foto"));
                        }else{                  
                            obtenida.setProfesor("Error al obtener profesor.");                    
                        }
  
                    }
                    
                    obtenida.setClientes_idclientes(resultado.getInt("clientes_idclientes"));
                    obtenida.setHorario(resultado.getString("horario"));
                    obtenida.setLugar(resultado.getString("lugar"));
                    obtenida.setTiempo(resultado.getInt("tiempo"));
                    obtenida.setExtras(resultado.getString("extras"));
                    obtenida.setTipoClase(resultado.getString("tipoClase"));
                    obtenida.setLatitud(resultado.getDouble("latitud"));
                    obtenida.setLongitud(resultado.getDouble("longitud"));
                    obtenida.setActualizado(resultado.getString("actualizado"));
                    obtenida.setIdSeccion(resultado.getInt("idSeccion"));
                    obtenida.setIdNivel(resultado.getInt("idNivel"));
                    obtenida.setIdBloque(resultado.getInt("idBloque"));
                    obtenida.setFecha(resultado.getDate("fecha"));
                    sesiones[aux] = obtenida;
                    aux++;
                    
                }
                

                
            } catch (SQLException ex) {

                ex.printStackTrace();

            }

        } else {

            System.out.println("Error en la coenxión en obtenerSesiones.");

        }

    }//Fin método obtenerSesiones.
    
    public void eliminarSesion(int idSesion){
        
        Connection conectar;
        
        conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement eliminar = conectar.prepareStatement("DELETE FROM sesiones WHERE idsesiones = ?");
                eliminar.setInt(1 , idSesion);
                eliminar.execute();
               
                
            }catch(SQLException ex){
                
                ex.printStackTrace();
                
            }
            
        }else{
            
            System.out.println("Error en eliminarSesion.");
            
        }
        
    }//Fin método eliminarSesion.
    
    public void actualizarSesion(JSONObject jsonDatos){
        System.out.println(jsonDatos);
        Connection conectar;
        conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            
            try{
                PreparedStatement actualizar;
                
                if(Boolean.valueOf(jsonDatos.get("cambioFecha").toString()))
                    actualizar = conectar.prepareStatement("UPDATE sesiones SET horario = ?, lugar = ?, tiempo = ?, tipoClase = ?, extras = ?, latitud = ?, longitud = ?, actualizado = ?, idSeccion = ?, idNivel = ?, idBloque = ?, fecha = ? WHERE idsesiones = ?");
                else
                    actualizar = conectar.prepareStatement("UPDATE sesiones SET horario = ?, lugar = ?, tiempo = ?, tipoClase = ?, extras = ?, latitud = ?, longitud = ?, actualizado = ?, idSeccion = ?, idNivel = ?, idBloque = ? WHERE idsesiones = ?");
               
                actualizar.setString(1 , jsonDatos.get("horario").toString());
                actualizar.setString(2 , jsonDatos.get("lugar").toString());
                actualizar.setString(3 , jsonDatos.get("tiempo").toString());
                actualizar.setString(4 , jsonDatos.get("tipoClase").toString());
                actualizar.setString(5 , jsonDatos.get("observaciones").toString());
                actualizar.setDouble(6 , Double.parseDouble(jsonDatos.get("latitud").toString()));
                actualizar.setDouble(7 , Double.parseDouble(jsonDatos.get("longitud").toString()));
                actualizar.setString(8 , jsonDatos.get("actualizado").toString());
                actualizar.setInt(9, Integer.valueOf(jsonDatos.get("idSeccion").toString()));
                actualizar.setInt(10, Integer.valueOf(jsonDatos.get("idNivel").toString()));
                actualizar.setInt(11, Integer.valueOf(jsonDatos.get("idBloque").toString()));
                if(Boolean.valueOf(jsonDatos.get("cambioFecha").toString())){
                    actualizar.setDate(12, java.sql.Date.valueOf(jsonDatos.get("fecha").toString()));
                    actualizar.setInt(13, Integer.parseInt(jsonDatos.get("idSesion").toString()));
                }else{
                     actualizar.setInt(12, Integer.parseInt(jsonDatos.get("idSesion").toString()));
                }

                actualizar.execute();
                
            }catch(SQLException ex){
                
                ex.printStackTrace();
                
            }
            
        }else{
            
            System.out.println("Error en actualizarSesion.");
            
        }   
        
    }//Fin método actualizarSesion.

}
