package com.proathome.controladores;

import com.proathome.modelos.Constantes;
import com.proathome.modelos.Sesion;
import com.proathome.mysql.DBController;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.simple.JSONObject;

public class ControladorSesion {

    private Sesion sesiones[];
    
    public void activarTE(int idSesion, int idCliente, int progresoTotal){
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement activar = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET estatus = ?, TE = ?, progresoTE = ?, progresoSegundosTE = ? WHERE idsesiones = ? AND clientes_idclientes = ?");
                activar.setInt(1,14);
                activar.setBoolean(2, true);
                activar.setInt(3, 3);
                activar.setInt(4, 0);
                activar.setInt(5, idSesion);
                activar.setInt(6, idCliente);
                activar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en activarTE.");
        }
        
    }
    
    public JSONObject validarServicioFinalizadaCliente(int idSesion, int idCliente){       
        JSONObject validar = new JSONObject();
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consultar = DBController.getInstance().getConnection().prepareStatement("SELECT finalizado FROM sesiones WHERE idsesiones = ? AND clientes_idclientes = ?");
                consultar.setInt(1, idSesion);
                consultar.setInt(2, idCliente);
                ResultSet resultado = consultar.executeQuery();
                
                if(resultado.next())
                    validar.put("finalizado", resultado.getBoolean("finalizado"));
                else
                    validar.put("error", "Error al consultar finalizado.");
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", validar);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion a BD.");
            }
        }else{
            System.out.println("Error en validarServicioFinalizada.");
        }
        
        return respuesta;       
    }
    
    public JSONObject validarServicioFinalizada(int idSesion, int idProfesional){
        JSONObject validar = new JSONObject();
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consultar = DBController.getInstance().getConnection().prepareStatement("SELECT finalizado FROM sesiones WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                consultar.setInt(1, idSesion);
                consultar.setInt(2, idProfesional);
                ResultSet resultado = consultar.executeQuery();
                
                if(resultado.next())
                    validar.put("finalizado", resultado.getBoolean("finalizado"));
                else
                    validar.put("error", "Error al consultar finalizado.");
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", validar);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion a BD.");
            }
        }else{
            System.out.println("Error en validarServicioFinalizada.");
        }
        
        return respuesta;
        
    }
    
    public void finalizarServicio(int idSesion, int idCliente){
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement finalizar = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET finalizado = ? WHERE idsesiones = ? AND clientes_idclientes = ?");
                finalizar.setBoolean(1, true);
                finalizar.setInt(2, idSesion);
                finalizar.setInt(3, idCliente);
                finalizar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en finalizarServicio.");
        }
    }
    
    public void actualizarProgresoServicio(int idSesion, int idProfesional, int progreso, int progresoSegundos, int tipoDeTiempo){
        if(DBController.getInstance().getConnection() != null){
            try{
                
                if(tipoDeTiempo == Constantes.TIPO_DE_TIEMPO_TE){
                    PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET progresoTE = ?, progresoSegundosTE = ? WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                    consulta.setInt(1, progreso);
                    consulta.setInt(2, progresoSegundos);
                    consulta.setInt(3, idSesion);
                    consulta.setInt(4, idProfesional);
                    consulta.execute();
                }else if(tipoDeTiempo == Constantes.TIPO_DE_TIEMPO_NORMAL){
                    PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET progreso = ?, progresoSegundos = ? WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                    consulta.setInt(1, progreso);
                    consulta.setInt(2, progresoSegundos);
                    consulta.setInt(3, idSesion);
                    consulta.setInt(4, idProfesional);
                    consulta.execute();
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en actualizarProgresoServicio.");
        }
    }
    
    public void cambiarEstatusServicioCliente(int idSesion, int idCliente, int estatus){
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND clientes_idclientes = ?");
                consulta.setInt(1, estatus);
                consulta.setInt(2, idSesion);
                consulta.setInt(3, idCliente);
                consulta.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en cambiarEstatusServicioCliente.");
        }
        
    }
    
    public void cambiarEstatusServicioProfesional(int idSesion, int idProfesional, int estatus){
        
        if(DBController.getInstance().getConnection() != null){
            try{
                
                PreparedStatement estatusTE = DBController.getInstance().getConnection().prepareStatement("SELECT TE FROM sesiones WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                estatusTE.setInt(1, idSesion);
                estatusTE.setInt(2, idProfesional);
                ResultSet resultadoTE = estatusTE.executeQuery();
                
                if(resultadoTE.next()){
                    if(resultadoTE.getBoolean("TE")){
                        if(estatus == Constantes.ESTATUS_ENCURSO){
                            PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                            consulta.setInt(1, Constantes.ESTATUS_ENCURSO_TE);
                            consulta.setInt(2, idSesion);
                            consulta.setInt(3, idProfesional);
                            consulta.execute();
                        }else if(estatus == Constantes.ESTATUS_ENPAUSA){
                            PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                            consulta.setInt(1, Constantes.ESTATUS_ENPAUSA_TE);
                            consulta.setInt(2, idSesion);
                            consulta.setInt(3, idProfesional);
                            consulta.execute();
                        }else if(estatus == Constantes.ESTATUS_TERMINADO){
                            PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                            consulta.setInt(1, Constantes.ESTATUS_TERMINADO_TE);
                            consulta.setInt(2, idSesion);
                            consulta.setInt(3, idProfesional);
                            consulta.execute();
                        }else{
                            PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                            consulta.setInt(1, estatus);
                            consulta.setInt(2, idSesion);
                            consulta.setInt(3, idProfesional);
                            consulta.execute();
                        }
                    }else{
                        PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET estatus = ? WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                        consulta.setInt(1, estatus);
                        consulta.setInt(2, idSesion);
                        consulta.setInt(3, idProfesional);
                        consulta.execute();
                    }
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en cambiarEstatusServicioCliente.");
        }
        
    }
    
       public JSONObject validarEstatusServicioProfesional(int idSesion, int idProfesional){       
        JSONObject datosSesion = new JSONObject();
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idProfesional);
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
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", datosSesion);
                }else{
                    datosSesion.put("error", "Error en la consulta.");
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", datosSesion);
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", datosSesion);
            }      
        }else{
            System.out.println("Error en validarEstatusServicio.");
        }
        
        return respuesta;      
    }
    
    public JSONObject validarEstatusServicioCliente(int idSesion, int idCliente){
        JSONObject datosSesion = new JSONObject();
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ? AND clientes_idclientes = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idCliente);
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
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", datosSesion);
                }else{
                    datosSesion.put("error", "Error en la consulta.");
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", datosSesion);
                }             
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion a BD.");
            }       
        }else{
            System.out.println("Error en validarEstatusServicio.");
        }
        
        return respuesta;     
    }
    
    public void servicioDisponibleProfesional(int idSesion, int idProfesional, boolean disponible){
        
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET profDisponible = ? WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                consulta.setBoolean(1, disponible);
                consulta.setInt(2, idSesion);
                consulta.setInt(3, idProfesional);
                consulta.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en servicioDsiponible");
        }
        
    }
    
    public JSONObject sincronizarServicioProfesional(int idSesion, int idPrfoesor){
        JSONObject respuesta = new JSONObject();
        JSONObject jsonResultado = new JSONObject();
        PreparedStatement consulta;
        
        if(DBController.getInstance().getConnection() != null){
            try{
                consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idPrfoesor);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next())
                    jsonResultado.put("dispCliente", resultado.getBoolean("estDisponible"));
                else
                    jsonResultado.put("error", "Error en la consulta.");
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", jsonResultado);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion a BD.");
            }
        }else{
            System.out.println("Error en sincronixarServicio.");
        }
        
        return respuesta;
        
    }
    
    public void servicioDisponible(int idSesion, int idCliente, boolean disponible){
        
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET estDisponible = ? WHERE idsesiones = ? AND clientes_idclientes = ?");
                consulta.setBoolean(1, disponible);
                consulta.setInt(2, idSesion);
                consulta.setInt(3, idCliente);
                consulta.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en servicioDsiponible");
        }
        
    }
    
    public JSONObject sincronizarServicio(int idSesion, int idCliente){   
        JSONObject jsonResultado = new JSONObject();
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ? AND clientes_idclientes = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idCliente);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    jsonResultado.put("dispProfesional", resultado.getBoolean("profDisponible"));
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", jsonResultado);
                }else{
                    jsonResultado.put("error", "Error en la consulta.");
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", jsonResultado);
                }
                         
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion a BD.");
            }
        }else{
            System.out.println("Error en sincronixarServicio.");
        }
        
        return respuesta;  
    }

    public void obtenerSesiones(int idCliente) {

        if (DBController.getInstance().getConnection() != null) {
            try {
                Statement estado = DBController.getInstance().getConnection().createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM sesiones WHERE clientes_idclientes = " + idCliente);
                int numeroSesiones = 0;
                
                while(resultado.next()){
                    numeroSesiones++;                 
                }
                
                sesiones = new Sesion[numeroSesiones];
                int aux = 0;
                estado = DBController.getInstance().getConnection().createStatement();
                resultado = estado.executeQuery("SELECT * FROM sesiones WHERE clientes_idclientes = " + idCliente  + " ORDER BY idsesiones DESC");
                
                while(resultado.next()){              
                    Sesion obtenida = new Sesion();
                    obtenida.setIdsesiones(resultado.getInt("idsesiones"));
                    String idProfesional = resultado.getString("profesionales_idprofesionales");
                    
                    if(idProfesional == null){
                        obtenida.setProfesional("Sin profesional asignado.");
                        obtenida.setFotoProfesional("Sin foto");
                        obtenida.setDescripcionProfesional("Sin descripcion");
                        obtenida.setCorreoProfesional("Sin correo");
                        obtenida.setProfesionales_idprofesionales(0);
                    }else{
                        PreparedStatement profesional = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM profesionales WHERE idprofesionales = ?");
                        profesional.setInt(1 , resultado.getInt("profesionales_idprofesionales"));
                        ResultSet nombreProfesional = profesional.executeQuery();
                        
                        if(nombreProfesional.next()){ 
                            obtenida.setProfesional(nombreProfesional.getString("nombre"));  
                            obtenida.setCorreoProfesional(nombreProfesional.getString("correo"));
                            obtenida.setDescripcionProfesional(nombreProfesional.getString("descripcion"));
                            obtenida.setFotoProfesional(nombreProfesional.getString("foto"));
                            obtenida.setProfesionales_idprofesionales(resultado.getInt("profesionales_idprofesionales"));
                        }else              
                            obtenida.setProfesional("Error al obtener profesional.");                    
                    }
                    
                    obtenida.setClientes_idclientes(resultado.getInt("clientes_idclientes"));
                    obtenida.setHorario(resultado.getString("horario"));
                    obtenida.setLugar(resultado.getString("lugar"));
                    obtenida.setTiempo(resultado.getInt("tiempo"));
                    obtenida.setExtras(resultado.getString("extras"));
                    obtenida.setTipoServicio(resultado.getString("tipoServicio"));
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
        }else {
            System.out.println("Error en la coenxión en obtenerSesiones.");
        }

    }//Fin método obtenerSesiones.
    
    public JSONObject eliminarSesion(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        if(DBController.getInstance().getConnection() != null){
            try{
                
                //Verificar el tipo de plan 
                if(jsonDatos.get("tipoPlan").toString().equalsIgnoreCase("PARTICULAR")){
                    
                    //Regresamos las horas a su lugar.
                    System.out.println(jsonDatos);
                    PreparedStatement consultarMonedero = DBController.getInstance().getConnection().prepareStatement("SELECT monedero FROM planes WHERE clientes_idclientes = ?");
                    consultarMonedero.setInt(1, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                    ResultSet monedero = consultarMonedero.executeQuery();
                    if(monedero.next()){
                        //Actualizar monedero
                        
                        int monederoAct = monedero.getInt("monedero") + Integer.parseInt(jsonDatos.get("horas").toString());
                        PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE planes SET monedero = ?, tipoPlan = ? WHERE clientes_idclientes = ?");
                        actualizar.setInt(1, monederoAct);
                        actualizar.setString(2, "PARTICULAR_PLAN");
                        actualizar.setInt(3, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                        actualizar.execute();
                        
                        PreparedStatement eliminar = DBController.getInstance().getConnection().prepareStatement("DELETE FROM sesiones WHERE idsesiones = ?");
                        eliminar.setInt(1 , Integer.parseInt(jsonDatos.get("idSesion").toString()));
                        eliminar.execute();
                        
                        respuesta.put("respuesta", true);
                        respuesta.put("mensaje", "Sesión eliminada, horas añadidas al monedero.");
                    }else{
                        respuesta.put("respuesta", false);
                        respuesta.put("mensaje", "Error en al obtener información de PLAN.");
                    }
                    
                    /*
                    PreparedStatement eliminar = DBController.getInstance().getConnection().prepareStatement("DELETE FROM sesiones WHERE idsesiones = ?");
                    eliminar.setInt(1 , Integer.parseInt(jsonDatos.get("idSesion").toString()));
                    eliminar.execute();*/
                }else{
                    //Regresamos las horas a su lugar.
                    System.out.println(jsonDatos);
                    PreparedStatement consultarMonedero = DBController.getInstance().getConnection().prepareStatement("SELECT monedero FROM planes WHERE clientes_idclientes = ?");
                    consultarMonedero.setInt(1, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                    ResultSet monedero = consultarMonedero.executeQuery();
                    if(monedero.next()){
                        //Actualizar monedero
                        
                        int monederoAct = monedero.getInt("monedero") + Integer.parseInt(jsonDatos.get("horas").toString());
                        PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE planes SET monedero = ? WHERE clientes_idclientes = ?");
                        actualizar.setInt(1, monederoAct);
                        actualizar.setInt(2, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                        actualizar.execute();
                        
                        PreparedStatement eliminar = DBController.getInstance().getConnection().prepareStatement("DELETE FROM sesiones WHERE idsesiones = ?");
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
                respuesta.put("mensaje", "No se puede eliminar el servicio.");
            }
            
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
        
    }//Fin método eliminarSesion.
    
    public JSONObject actualizarSesion(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement actualizar;
                //VALIDAR CONTRA AGENDA SI HAY CAMBIO DE FECHA U HORARIO
                                
                if(Boolean.valueOf(jsonDatos.get("existeProfesional").toString()) && (Boolean.valueOf(jsonDatos.get("cambioFecha").toString()) || Boolean.valueOf(jsonDatos.get("cambioHorario").toString()))){
                    actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET profesionales_idprofesionales = ?, horario = ?, lugar = ?, tiempo = ?, tipoServicio = ?, extras = ?, latitud = ?, longitud = ?, actualizado = ?, idSeccion = ?, idNivel = ?, idBloque = ?, fecha = ? WHERE idsesiones = ?");
                    actualizar.setNull(1, 0);
                    actualizar.setString(2 , jsonDatos.get("horario").toString());
                    actualizar.setString(3 , jsonDatos.get("lugar").toString());
                    actualizar.setString(4 , jsonDatos.get("tiempo").toString());
                    actualizar.setString(5 , jsonDatos.get("tipoServicio").toString());
                    actualizar.setString(6 , jsonDatos.get("observaciones").toString());
                    actualizar.setDouble(7 , Double.parseDouble(jsonDatos.get("latitud").toString()));
                    actualizar.setDouble(8 , Double.parseDouble(jsonDatos.get("longitud").toString()));
                    actualizar.setString(9 , jsonDatos.get("actualizado").toString());
                    actualizar.setInt(10, Integer.valueOf(jsonDatos.get("idSeccion").toString()));
                    actualizar.setInt(11, Integer.valueOf(jsonDatos.get("idNivel").toString()));
                    actualizar.setInt(12, Integer.valueOf(jsonDatos.get("idBloque").toString()));
                    actualizar.setDate(13, java.sql.Date.valueOf(jsonDatos.get("fecha").toString()));
                    actualizar.setInt(14, Integer.parseInt(jsonDatos.get("idSesion").toString()));
                }else{
                    actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET horario = ?, lugar = ?, tiempo = ?, tipoServicio = ?, extras = ?, latitud = ?, longitud = ?, actualizado = ?, idSeccion = ?, idNivel = ?, idBloque = ?, fecha = ? WHERE idsesiones = ?");
                    actualizar.setString(1 , jsonDatos.get("horario").toString());
                    actualizar.setString(2 , jsonDatos.get("lugar").toString());
                    actualizar.setString(3 , jsonDatos.get("tiempo").toString());
                    actualizar.setString(4 , jsonDatos.get("tipoServicio").toString());
                    actualizar.setString(5 , jsonDatos.get("observaciones").toString());
                    actualizar.setDouble(6 , Double.parseDouble(jsonDatos.get("latitud").toString()));
                    actualizar.setDouble(7 , Double.parseDouble(jsonDatos.get("longitud").toString()));
                    actualizar.setString(8 , jsonDatos.get("actualizado").toString());
                    actualizar.setInt(9, Integer.valueOf(jsonDatos.get("idSeccion").toString()));
                    actualizar.setInt(10, Integer.valueOf(jsonDatos.get("idNivel").toString()));
                    actualizar.setInt(11, Integer.valueOf(jsonDatos.get("idBloque").toString()));
                    actualizar.setString(12, jsonDatos.get("fecha").toString());
                    actualizar.setInt(13, Integer.parseInt(jsonDatos.get("idSesion").toString()));
                }

                actualizar.execute();
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Servicio actualizado exitosamente.");
            }catch(SQLException ex){
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", ex.getMessage());  
                ex.printStackTrace();
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }   
        
        return respuesta;
    }//Fin método actualizarSesion.

}
