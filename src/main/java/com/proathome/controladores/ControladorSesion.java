package com.proathome.controladores;

import com.proathome.modelos.Constantes;
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
    
    public void activarTE(int idSesion, int idEstudiante, int progresoTotal){
        
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement activar = conectar.prepareStatement("UPDATE sesiones SET estatus = ?, TE = ?, progresoTE = ?, progresoSegundosTE = ? WHERE idsesiones = ? AND clientes_idclientes = ?");
                activar.setInt(1,14);
                activar.setBoolean(2, true);
                activar.setInt(3, progresoTotal);
                activar.setInt(4, 0);
                activar.setInt(5, idSesion);
                activar.setInt(6, idEstudiante);
                activar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en activarTE.");
        }
        
    }
    
    public JSONObject validarClaseFinalizadaEstudiante(int idSesion, int idEstudiante){
        
        JSONObject validar = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement consultar = conectar.prepareStatement("SELECT finalizado FROM sesiones WHERE idsesiones = ? AND clientes_idclientes = ?");
                consultar.setInt(1, idSesion);
                consultar.setInt(2, idEstudiante);
                ResultSet resultado = consultar.executeQuery();
                
                if(resultado.next()){
                    validar.put("finalizado", resultado.getBoolean("finalizado"));
                }else{
                    validar.put("error", "Error al consultar finalizado.");
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en validarClaseFinalizada.");
        }
        
        return validar;
        
    }
    
    public JSONObject validarClaseFinalizada(int idSesion, int idProfesor){
        
        JSONObject validar = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement consultar = conectar.prepareStatement("SELECT finalizado FROM sesiones WHERE idsesiones = ? AND profesores_idprofesores = ?");
                consultar.setInt(1, idSesion);
                consultar.setInt(2, idProfesor);
                ResultSet resultado = consultar.executeQuery();
                
                if(resultado.next()){
                    validar.put("finalizado", resultado.getBoolean("finalizado"));
                }else{
                    validar.put("error", "Error al consultar finalizado.");
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en validarClaseFinalizada.");
        }
        
        return validar;
        
    }
    
    public void finalizarClase(int idSesion, int idEstudiante){
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement finalizar = conectar.prepareStatement("UPDATE sesiones SET finalizado = ? WHERE idsesiones = ? AND clientes_idclientes = ?");
                finalizar.setBoolean(1, true);
                finalizar.setInt(2, idSesion);
                finalizar.setInt(3, idEstudiante);
                finalizar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en finalizarClase.");
        }
    }
    
    public void actualizarProgresoClase(int idSesion, int idProfesor, int progreso, int progresoSegundos, int tipoDeTiempo){
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                
                if(tipoDeTiempo == Constantes.TIPO_DE_TIEMPO_TE){
                    PreparedStatement consulta = conectar.prepareStatement("UPDATE sesiones SET progresoTE = ?, progresoSegundosTE = ? WHERE idsesiones = ? AND profesores_idprofesores = ?");
                    consulta.setInt(1, progreso);
                    consulta.setInt(2, progresoSegundos);
                    consulta.setInt(3, idSesion);
                    consulta.setInt(4, idProfesor);
                    consulta.execute();
                }else if(tipoDeTiempo == Constantes.TIPO_DE_TIEMPO_NORMAL){
                    PreparedStatement consulta = conectar.prepareStatement("UPDATE sesiones SET progreso = ?, progresoSegundos = ? WHERE idsesiones = ? AND profesores_idprofesores = ?");
                    consulta.setInt(1, progreso);
                    consulta.setInt(2, progresoSegundos);
                    consulta.setInt(3, idSesion);
                    consulta.setInt(4, idProfesor);
                    consulta.execute();
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en actualizarProgresoClase.");
        }
    }
    
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
                
                PreparedStatement estatusTE = conectar.prepareStatement("SELECT TE FROM sesiones WHERE idsesiones = ? AND profesores_idprofesores = ?");
                estatusTE.setInt(1, idSesion);
                estatusTE.setInt(2, idProfesor);
                ResultSet resultadoTE = estatusTE.executeQuery();
                
                if(resultadoTE.next()){
                    if(resultadoTE.getBoolean("TE")){
                        if(estatus == Constantes.ESTATUS_ENCURSO){
                            PreparedStatement consulta = conectar.prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND profesores_idprofesores = ?");
                            consulta.setInt(1, Constantes.ESTATUS_ENCURSO_TE);
                            consulta.setInt(2, idSesion);
                            consulta.setInt(3, idProfesor);
                            consulta.execute();
                        }else if(estatus == Constantes.ESTATUS_ENPAUSA){
                            PreparedStatement consulta = conectar.prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND profesores_idprofesores = ?");
                            consulta.setInt(1, Constantes.ESTATUS_ENPAUSA_TE);
                            consulta.setInt(2, idSesion);
                            consulta.setInt(3, idProfesor);
                            consulta.execute();
                        }else if(estatus == Constantes.ESTATUS_TERMINADO){
                            PreparedStatement consulta = conectar.prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND profesores_idprofesores = ?");
                            consulta.setInt(1, Constantes.ESTATUS_TERMINADO_TE);
                            consulta.setInt(2, idSesion);
                            consulta.setInt(3, idProfesor);
                            consulta.execute();
                        }else{
                            PreparedStatement consulta = conectar.prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND profesores_idprofesores = ?");
                            consulta.setInt(1, estatus);
                            consulta.setInt(2, idSesion);
                            consulta.setInt(3, idProfesor);
                            consulta.execute();
                        }
                    }else{
                        PreparedStatement consulta = conectar.prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND profesores_idprofesores = ?");
                        consulta.setInt(1, estatus);
                        consulta.setInt(2, idSesion);
                        consulta.setInt(3, idProfesor);
                        consulta.execute();
                    }
                }
                
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
                    datosSesion.put("estDisponible", resultado.getBoolean("estDisponible"));
                    datosSesion.put("estatus", resultado.getInt("estatus"));
                    datosSesion.put("progreso", resultado.getInt("progreso"));
                    datosSesion.put("progresoSegundos", resultado.getInt("progresoSegundos"));
                    datosSesion.put("TE", resultado.getBoolean("TE"));
                    datosSesion.put("progresoTE", resultado.getInt("progresoTE"));
                    datosSesion.put("progresoSegundosTE", resultado.getInt("progresoSegundosTE"));
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
                    datosSesion.put("profDisponible", resultado.getBoolean("profDisponible"));
                    datosSesion.put("idSeccion", resultado.getInt("idSeccion"));
                    datosSesion.put("idNivel", resultado.getInt("idNivel"));
                    datosSesion.put("idBloque", resultado.getInt("idBloque"));
                    datosSesion.put("estatus", resultado.getInt("estatus"));
                    datosSesion.put("progreso", resultado.getInt("progreso"));
                    datosSesion.put("progresoSegundos", resultado.getInt("progresoSegundos"));
                    datosSesion.put("TE", resultado.getBoolean("TE"));
                    datosSesion.put("progresoTE", resultado.getInt("progresoTE"));
                    datosSesion.put("progresoSegundosTE", resultado.getInt("progresoSegundosTE"));
                    datosSesion.put("tiempo", resultado.getInt("tiempo"));
                    datosSesion.put("sumar", resultado.getInt("sumar"));
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
                        obtenida.setProfesores_idprofesores(0);
 
                    }else{
                        
                        PreparedStatement profesor = conectar.prepareStatement("SELECT * FROM profesores WHERE idprofesores = ?");
                        profesor.setInt(1 , resultado.getInt("profesores_idprofesores"));
                        ResultSet nombreProfesor = profesor.executeQuery();
                        
                        if(nombreProfesor.next()){ 
                            obtenida.setProfesor(nombreProfesor.getString("nombre"));  
                            obtenida.setCorreoProfesor(nombreProfesor.getString("correo"));
                            obtenida.setDescripcionProfesor(nombreProfesor.getString("descripcion"));
                            obtenida.setFotoProfesor(nombreProfesor.getString("foto"));
                            obtenida.setProfesores_idprofesores(resultado.getInt("profesores_idprofesores"));
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
                    obtenida.setFecha(resultado.getString("fecha"));
                    obtenida.setSumar(resultado.getBoolean("sumar"));
                    obtenida.setFinalizado(resultado.getBoolean("finalizado"));
                    obtenida.setTipoPlan(resultado.getString("tipoPlan"));
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
    
    public JSONObject eliminarSesion(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                
                //Verificar el tipo de plan 
                if(jsonDatos.get("tipoPlan").toString().equalsIgnoreCase("PARTICULAR")){
                    
                    //Regresamos las horas a su lugar.
                    System.out.println(jsonDatos);
                    PreparedStatement consultarMonedero = conectar.prepareStatement("SELECT monedero FROM planes WHERE clientes_idclientes = ?");
                    consultarMonedero.setInt(1, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
                    ResultSet monedero = consultarMonedero.executeQuery();
                    if(monedero.next()){
                        //Actualizar monedero
                        
                        int monederoAct = monedero.getInt("monedero") + Integer.parseInt(jsonDatos.get("horas").toString());
                        PreparedStatement actualizar = conectar.prepareStatement("UPDATE planes SET monedero = ?, tipoPlan = ? WHERE clientes_idclientes = ?");
                        actualizar.setInt(1, monederoAct);
                        actualizar.setString(2, "PARTICULAR_PLAN");
                        actualizar.setInt(3, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
                        actualizar.execute();
                        
                        PreparedStatement eliminar = conectar.prepareStatement("DELETE FROM sesiones WHERE idsesiones = ?");
                        eliminar.setInt(1 , Integer.parseInt(jsonDatos.get("idSesion").toString()));
                        eliminar.execute();
                        
                        respuesta.put("respuesta", true);
                        respuesta.put("mensaje", "Sesión eliminada, horas añadidas al monedero.");
                    }else{
                        respuesta.put("respuesta", false);
                        respuesta.put("mensaje", "Error en al obtener información de PLAN.");
                    }
                    
                    /*
                    PreparedStatement eliminar = conectar.prepareStatement("DELETE FROM sesiones WHERE idsesiones = ?");
                    eliminar.setInt(1 , Integer.parseInt(jsonDatos.get("idSesion").toString()));
                    eliminar.execute();*/
                }else{
                    //Regresamos las horas a su lugar.
                    System.out.println(jsonDatos);
                    PreparedStatement consultarMonedero = conectar.prepareStatement("SELECT monedero FROM planes WHERE clientes_idclientes = ?");
                    consultarMonedero.setInt(1, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
                    ResultSet monedero = consultarMonedero.executeQuery();
                    if(monedero.next()){
                        //Actualizar monedero
                        
                        int monederoAct = monedero.getInt("monedero") + Integer.parseInt(jsonDatos.get("horas").toString());
                        PreparedStatement actualizar = conectar.prepareStatement("UPDATE planes SET monedero = ? WHERE clientes_idclientes = ?");
                        actualizar.setInt(1, monederoAct);
                        actualizar.setInt(2, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
                        actualizar.execute();
                        
                        PreparedStatement eliminar = conectar.prepareStatement("DELETE FROM sesiones WHERE idsesiones = ?");
                        eliminar.setInt(1 , Integer.parseInt(jsonDatos.get("idSesion").toString()));
                        eliminar.execute();
                        
                        respuesta.put("respuesta", true);
                        respuesta.put("mensaje", "Sesión eliminada, horas añadidas al monedero.");
                    }else{
                        respuesta.put("respuesta", false);
                        respuesta.put("mensaje", "Error al obtener información de PLAN.");
                    }
                }
               
            }catch(SQLException ex){
                ex.printStackTrace(); 
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "No se puede eliminar la clase.");
            }
            
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
        
    }//Fin método eliminarSesion.
    
    public void actualizarSesion(JSONObject jsonDatos){

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
