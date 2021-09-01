package com.proathome.controladores;

import com.proathome.modelos.Constantes;
import com.proathome.modelos.CuentaBancaria;
import com.proathome.modelos.Profesor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.proathome.mysql.ConexionMySQL;
import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ControladorProfesor {

    /*
    *
    *Clase controladora de Profesores.
    *
     */
    private Profesor profesor = new Profesor();
    private Connection conectar;
    private JSONObject jsonMatch = new JSONObject();
    private JSONObject jsonSesionesMatchProfesor = new JSONObject();
    private JSONArray arrayJson = new JSONArray();
    private boolean profesorRegistrado = false;
    
    public JSONObject getVerificacion(String token, String correo){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if(conectar != null){
            //Obtenemos token de correo.
            try{
                PreparedStatement consultar = conectar.prepareStatement("SELECT token_verificacion, verificado FROM profesores WHERE correo = ?");
                consultar.setString(1, correo);
                ResultSet resultado = consultar.executeQuery();
                
                if(resultado.next()){
                    String token_bd = resultado.getString("token_verificacion");
                    boolean verificado = resultado.getBoolean("verificado");
                    
                    if(token_bd.equalsIgnoreCase(token)){
                        //Verificamos el correo.
                        PreparedStatement verificar = conectar.prepareStatement("UPDATE profesores SET verificado = ? WHERE correo = ?");
                        verificar.setBoolean(1, true);
                        verificar.setString(2, correo);
                        verificar.execute();
                        
                        respuesta.put("mensaje", "Cuenta verificada, inicia sesión.");
                        respuesta.put("respuesta", true);
                        respuesta.put("verificado", verificado);
                    }else{
                        respuesta.put("mensaje", "Token de confirmación inválido.");
                        respuesta.put("respuesta", false);
                    }
                }else{
                    respuesta.put("mensaje", "No se encontró la información.");
                    respuesta.put("respuesta", false);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("mensaje", "Error en la conexión a BD.");
                respuesta.put("respuesta", false);
            }
        }else{
            respuesta.put("mensaje", "Error en la Peticion");
            respuesta.put("respuesta", false);
        }
        
        return respuesta;
    }
    
    public JSONObject actualizarPass(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if(conectar != null){
            try{
                //Consultar si el pass anterior es correcto.
                PreparedStatement passAnterior = conectar.prepareStatement("SELECT contrasena FROM profesores WHERE idprofesores = ?");
                passAnterior.setInt(1, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                ResultSet resultPassAnt = passAnterior.executeQuery();
                
                if(resultPassAnt.next()){
                    String passAntBD = resultPassAnt.getString("contrasena");
                    MD5 md5 = new MD5();
                    String passAnt = md5.getMD5(jsonDatos.get("actual").toString());
                    System.out.println("ActualBD " + passAntBD);
                    System.out.println(passAnt);
                    //Validamos si son iguales.
                    if(passAnt.equals(passAntBD)){
                        //Guardamos la nueva contrasena.
                        String nuevaPass = md5.getMD5(jsonDatos.get("nueva").toString());
                        PreparedStatement guardarPass = conectar.prepareStatement("UPDATE profesores SET contrasena = ? WHERE idprofesores = ?");
                        guardarPass.setString(1, nuevaPass);
                        guardarPass.setInt(2, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                        guardarPass.execute();
                        
                        respuesta.put("mensaje", "Contraseña actualizada correctamente.");
                        respuesta.put("respuesta", true);
                        
                    }else{
                        respuesta.put("mensaje", "La contraseña anterior es incorrecta.");
                        respuesta.put("respuesta", false);
                    }
                }else{
                    respuesta.put("mensaje", "Error en la consulta a BD.");
                    respuesta.put("respuesta", false);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            respuesta.put("mensaje", "Error en la conexión a BD.");
            respuesta.put("respuesta", false);
        }
        
        return respuesta;
    }
    
    public JSONObject cancelarSesion(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement cancelar = conectar.prepareStatement("UPDATE sesiones SET profesores_idprofesores = ? WHERE idsesiones = ? AND profesores_idprofesores = ?");
                cancelar.setNull(1, 0);
                cancelar.setInt(2, Integer.parseInt(jsonDatos.get("idClase").toString()));
                cancelar.setInt(3, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                cancelar.execute();
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Sesión cancelada exitosamente.");
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD.");
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
    }
    
    public String getFechaString(String fecha, String horario){
        String text = null;

        if(horario.equals("08:00 HRS")){
            text = fecha + " 08:00";
        }else if(horario.equals("09:00 HRS")){
            text = fecha + " 09:00";
        }else if(horario.equals("10:00 HRS")){
            text = fecha + " 10:00";
        }else if(horario.equals("11:00 HRS")){
            text = fecha + " 11:00";
        }else if(horario.equals("12:00 HRS")){
            text = fecha + " 12:00";
        }else if(horario.equals("13:00 HRS")){
            text = fecha + " 13:00";
        }else if(horario.equals("14:00 HRS")){
            text = fecha + " 14:00";
        }else if(horario.equals("15:00 HRS")){
            text = fecha + " 15:00";
        }else if(horario.equals("16:00 HRS")){
            text = fecha + " 16:00";
        }else if(horario.equals("17:00 HRS")){
            text = fecha + " 17:00";
        }else if(horario.equals("18:00 HRS")){
            text = fecha + " 18:00";
        }
        
        return text;
    }
    
    public boolean fechaHoy(String hoy, String fechaClase){
        boolean resp = false;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date fechaClaseDate =  sdf.parse(fechaClase);
            java.util.Date fechaHoyDate =  sdf.parse(hoy);
            
            if(fechaClaseDate.equals(fechaHoyDate)){
                resp = true;
            }else
                resp = false;
            
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return resp;
    }
    
    public JSONObject solicitudEliminarSesion(int idSesion, int idProfesor){
        JSONObject respuesta = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        JSONObject eliminar = new JSONObject();
        
        if(conectar != null){
            try{
                //Consultamos la fecha y el horario;
                PreparedStatement consulta = conectar.prepareStatement("SELECT fecha, horario FROM sesiones WHERE idsesiones = ? AND profesores_idprofesores = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idProfesor);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    SimpleDateFormat sdfNormal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    //Hoy
                    Calendar calendarHoy = Calendar.getInstance();
                    String fechaHoy = sdfNormal.format(calendarHoy.getTime());
                    java.util.Date hoy = sdfNormal.parse(fechaHoy);
                    
                    if(fechaHoy(fechaHoy ,resultado.getDate("fecha").toString())){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        java.util.Date fechaClase =  sdf.parse(getFechaString(resultado.getDate("fecha").toString(), resultado.getString("horario")));

                        String fechaClaseS = sdf.format(fechaClase.getTime());
                        //obtienes la diferencia de las fechas
                        long difference = Math.abs(fechaClase.getTime() - hoy.getTime());
                        //obtienes la diferencia en horas ya que la diferencia anterior esta en milisegundos
                        difference = difference / (60 * 60 * 1000);
                        System.out.println(difference);
                        if(difference >= 24){
                            //Multa
                            eliminar.put("multa", false);
                            eliminar.put("diferencia", "24+ HRS");
                            eliminar.put("eliminar", true);
                        }else if(difference < 24){
                            eliminar.put("multa", false);
                            eliminar.put("diferencia", "-24 HRS");
                            eliminar.put("eliminar", false);
                        }
                    }else{
                        eliminar.put("multa", false);
                        eliminar.put("diferencia", "24+ HRS");
                        eliminar.put("eliminar", true);
                    }
                    
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", eliminar);
                   
                }else{
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", "Error en la conexión a BD.");
                }
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD.");
            }catch(ParseException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", ex.getMessage());
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
    }
    
    public JSONObject guardarDatosFiscales(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                //Consultar si hay registro.
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM datosfiscalesprofesores WHERE profesores_idprofesores = ?");
                consulta.setInt(1, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    //Actualizamos
                    PreparedStatement actualizar = conectar.prepareStatement("UPDATE datosfiscalesprofesores SET tipoPersona = ?, razonSocial = ?, rfc = ?, cfdi = ? WHERE profesores_idprofesores = ?");
                    actualizar.setString(1, jsonDatos.get("tipoPersona").toString());
                    actualizar.setString(2, jsonDatos.get("razonSocial").toString());
                    actualizar.setString(3, jsonDatos.get("rfc").toString());
                    actualizar.setString(4, jsonDatos.get("cfdi").toString());
                    actualizar.setInt(5, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                    actualizar.execute();
                }else{
                    //Guardamos
                    PreparedStatement actualizar = conectar.prepareStatement("INSERT INTO datosfiscalesprofesores (tipoPersona, razonSocial, rfc, cfdi, profesores_idprofesores) VALUES (?,?,?,?,?)");
                    actualizar.setString(1, jsonDatos.get("tipoPersona").toString());
                    actualizar.setString(2, jsonDatos.get("razonSocial").toString());
                    actualizar.setString(3, jsonDatos.get("rfc").toString());
                    actualizar.setString(4, jsonDatos.get("cfdi").toString());
                    actualizar.setInt(5, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                    actualizar.execute();
                }
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Actualización de datos exitosa.");
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD.");
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
    }
    
    public JSONObject getDatosFiscales(int idProfesor){
        JSONObject respuesta = new JSONObject();
        JSONObject datos = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM datosfiscalesprofesores WHERE profesores_idprofesores = ?");
                consulta.setInt(1, idProfesor);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    //Hay registro
                    datos.put("existe", true);
                    datos.put("tipoPersona", resultado.getString("tipoPersona"));
                    datos.put("razonSocial", resultado.getString("razonSocial"));
                    datos.put("rfc", resultado.getString("rfc"));
                    datos.put("cfdi", resultado.getString("cfdi"));
                }else
                    datos.put("existe", false);
                
                respuesta.put("mensaje", datos);
                respuesta.put("respuesta", true);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD.");
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
    }
    
    public void bloquearPerfil(int idProfesor, Connection conectar){
        try{
            PreparedStatement bloquear = conectar.prepareStatement("UPDATE profesores SET estado = ? WHERE idprofesores = ?");
            bloquear.setString(1, "BLOQUEADO");
            bloquear.setInt(2, idProfesor);
            bloquear.execute();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public JSONObject getReportes(int idProfesor){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement reportes = conectar.prepareStatement("SELECT COUNT(*) AS num FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
                reportes.setInt(1, idProfesor);
                reportes.setString(2, "PROFESOR");
                ResultSet resultadoNum = reportes.executeQuery();
                
                if(resultadoNum.next()){
                    int numReportes = resultadoNum.getInt("num");
                    
                    if(numReportes == 0){
                        JSONObject mensaje = new JSONObject();
                        mensaje.put("reportes", numReportes);
                        mensaje.put("aviso", "Sin reportes");
                        respuesta.put("respuesta", true);
                        respuesta.put("mensaje", mensaje);
                    }else{
                        /*Verificar bloqueo
                        if(numReportes >= 3)
                            bloquearPerfil(idProfesor, conectar);*/
                        PreparedStatement descripcion = conectar.prepareStatement("SELECT * FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
                        descripcion.setInt(1, idProfesor);
                        descripcion.setString(2, "PROFESOR");
                        ResultSet resultadoDesc = descripcion.executeQuery();
              
                        String desText = null;
                        while(resultadoDesc.next()){
                            desText = resultadoDesc.getString("descripcion");
                        }

                        JSONObject mensaje = new JSONObject();
                        mensaje.put("reportes", numReportes);
                        mensaje.put("aviso", desText);
                        respuesta.put("respuesta", true);
                        respuesta.put("mensaje", mensaje);
        
                    }
                    
                }else{
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", "Error en la conexión a BD");
                }
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD");
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD");
        }
        
        return respuesta;
    }
    
    public void agendarCita(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement agendar = conectar.prepareStatement("UPDATE citas SET fechaAcordada = ?, horarioAcordado = ? WHERE profesores_idprofesores = ?");
                agendar.setDate(1, java.sql.Date.valueOf(jsonDatos.get("fechaAcordada").toString()));
                agendar.setString(2, jsonDatos.get("horarioAcordado").toString());
                agendar.setInt(3, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                agendar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en agendarCita.");
        }
    }
    
    public JSONObject obtenerCita(int idProfesor){
        Connection conectar = ConexionMySQL.connection();
        JSONObject cita = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("SELECt * FROM citas WHERE profesores_idprofesores = ?");
                consulta.setInt(1, idProfesor);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    cita.put("fecha1", resultado.getDate("fecha1"));
                    cita.put("fecha2", resultado.getDate("fecha2"));
                    cita.put("horario1", resultado.getString("horario1"));
                    cita.put("horario2", resultado.getString("horario2"));
                    cita.put("fechaAcordada", resultado.getDate("fechaAcordada"));
                    cita.put("horarioAcordado", resultado.getString("horarioAcordado"));
                    cita.put("tipoCita", resultado.getString("tipoCita"));
                    cita.put("datosAdicionales", resultado.getString("datosAdicionales"));
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en obtenerCita.");
        }
        
        return cita;
    }
    
    public JSONObject estatusDocumentos(int idProfesor){
        Connection conectar = ConexionMySQL.connection();
        JSONObject estatus = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement documentos = conectar.prepareStatement("SELECT * FROM documentacionprofesor WHERE profesores_idprofesores = ?");
                documentos.setInt(1, idProfesor);
                ResultSet resultado = documentos.executeQuery();
                
                if(resultado.next())
                    estatus.put("estatus", true);
                else
                    estatus.put("estatus", false);
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else
            System.out.println("Error en estatusDocumentos.");
        
        return estatus;
    }
    
    public void nuevoTicket(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement nuevoTicket;
                //Validar si es tipo General o Clase
                if(Integer.parseInt(jsonDatos.get("idSesion").toString()) == 0)
                    nuevoTicket = conectar.prepareStatement("INSERT INTO tickets_ayuda (tipoUsuario, topico, descripcion, fechaCreacion, estatus, idUsuario, categoria) VALUES (?,?,?,?,?,?,?)");
                else nuevoTicket = conectar.prepareStatement("INSERT INTO tickets_ayuda (tipoUsuario, topico, descripcion, fechaCreacion, estatus, idUsuario, categoria, sesiones_idsesiones) VALUES (?,?,?,?,?,?,?,?)");
                nuevoTicket.setInt(1, Integer.parseInt(jsonDatos.get("tipoUsuario").toString()));
                nuevoTicket.setString(2, jsonDatos.get("topico").toString());
                nuevoTicket.setString(3, jsonDatos.get("descripcion").toString());
                nuevoTicket.setDate(4, java.sql.Date.valueOf(jsonDatos.get("fechaCreacion").toString()));
                nuevoTicket.setInt(5, Integer.parseInt(jsonDatos.get("estatus").toString()));
                nuevoTicket.setInt(6, Integer.parseInt(jsonDatos.get("idUsuario").toString()));
                nuevoTicket.setString(7, jsonDatos.get("categoria").toString());
                if(Integer.parseInt(jsonDatos.get("idSesion").toString()) != 0)
                    nuevoTicket.setInt(8, Integer.parseInt(jsonDatos.get("idSesion").toString()));
                nuevoTicket.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en nuevoTicket.");
        }
    }
    
     public void enviarMsgTicket(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();

        if(conectar != null){
            try{
                PreparedStatement mensaje = conectar.prepareStatement("INSERT INTO msg_tickets (mensaje, idUsuario_Operador, operadorBool, tickets_ayuda_idtickets_ayuda) VALUES (?,?,?,?)");
                mensaje.setString(1, jsonDatos.get("mensaje").toString());
                mensaje.setInt(2, Integer.parseInt(jsonDatos.get("idUsuario").toString()));
                mensaje.setBoolean(3, Boolean.valueOf(jsonDatos.get("operador").toString()));
                mensaje.setInt(4, Integer.parseInt(jsonDatos.get("idTicket").toString()));
                mensaje.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en enviarMsgTicket.");
        }
    }
    
    public JSONArray obtenerTickets(int idEstudiante){
        Connection conectar = ConexionMySQL.connection();
        JSONArray jsonTickets = new JSONArray();
        boolean vacio = true;
        
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM tickets_ayuda WHERE idUsuario = ? AND tipoUsuario = ? ORDER BY idtickets_ayuda DESC");
                consulta.setInt(1, idEstudiante);
                consulta.setInt(2, Constantes.TIPO_USUARIO_PROFESOR);
                ResultSet resultado = consulta.executeQuery();
                
                while(resultado.next()){
                    vacio = false;
                    JSONObject jsonTicket = new JSONObject();
                    jsonTicket.put("noTicket", "00" + resultado.getInt("idtickets_ayuda"));
                    jsonTicket.put("idTicket", resultado.getInt("idtickets_ayuda"));
                    jsonTicket.put("tipoUsuario", resultado.getInt("tipoUsuario"));
                    jsonTicket.put("topico", resultado.getString("topico"));
                    jsonTicket.put("descripcion", resultado.getString("descripcion"));
                    jsonTicket.put("fechaCreacion", resultado.getDate("fechaCreacion"));
                    jsonTicket.put("estatus", resultado.getInt("estatus"));
                    jsonTicket.put("idUsuario", resultado.getInt("idUsuario"));
                    jsonTicket.put("idOperador", resultado.getInt("operadores_idoperadores"));
                    jsonTicket.put("categoria", resultado.getString("categoria"));
                    jsonTicket.put("sinTickets", false);
                    jsonTickets.add(jsonTicket);
                }
                       
                if(vacio){
                    JSONObject jsonSinTickets = new JSONObject();
                    jsonSinTickets.put("sinTickets", true);
                    jsonTickets.add(jsonSinTickets);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en obtenerTickets.");
        }

        return jsonTickets;
    }
    
    public void finalizarTicket(int idTicket){
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement ticket = conectar.prepareStatement("UPDATE tickets_ayuda SET estatus = ? WHERE idtickets_ayuda = ?");
                ticket.setInt(1, Constantes.ESTATUS_SOLUCIONADO);
                ticket.setInt(2, idTicket);
                ticket.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en solicitudTicketFinalizado.");
        }
    }
    
    public JSONObject validarValoracion(int idSesion, int idEstudiante){
    
       Connection conectar = ConexionMySQL.connection();
       JSONObject jsonRespuesta = new JSONObject();
       if(conectar != null){
           try{
               PreparedStatement validar = conectar.prepareStatement("SELECT * FROM valoracionestudiante WHERE sesiones_idsesiones = ? AND clientes_idclientes = ?");
               validar.setInt(1, idSesion);
               validar.setInt(2, idEstudiante);
               ResultSet resultado = validar.executeQuery();
               
               if(resultado.next()){
                   jsonRespuesta.put("valorado", true);
               }else{
                   PreparedStatement sesion = conectar.prepareStatement("SELECT finalizado FROM sesiones WHERE idsesiones = ?");
                   sesion.setInt(1, idSesion);
                   ResultSet resSesion = sesion.executeQuery();
                   if(resSesion.next()){
                       if(resSesion.getBoolean("finalizado"))
                           jsonRespuesta.put("valorado", false);
                       else 
                           jsonRespuesta.put("valorado", true);
                   }else{
                       jsonRespuesta.put("valorado", true);
                   }
                   
               }
           }catch(SQLException ex){
               ex.printStackTrace();
           }
       }else{
           System.out.println("Error en validarValoracion.");
       }
       
       return jsonRespuesta;
       
    }
    
    public void valorarEstudiante(JSONObject jsonDatos){
        
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement valorar = conectar.prepareStatement("INSERT INTO valoracionestudiante (clientes_idclientes, profesores_idprofesores, valoracion, comentario, sesiones_idsesiones) VALUES (?,?,?,?,?)");
                valorar.setInt(1, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
                valorar.setInt(2, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                valorar.setFloat(3, Float.parseFloat(jsonDatos.get("valoracion").toString()));
                valorar.setString(4, jsonDatos.get("comentario").toString());
                valorar.setInt(5, Integer.parseInt(jsonDatos.get("idSesion").toString()));
                valorar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            System.out.println("Error en validarValoracion.");
        }
    
    }
    
    public JSONArray obtenerValoracion(int idEstudiante){
        
        Connection conectar = ConexionMySQL.connection();
        JSONObject clienteJSON = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if(conectar != null){
            //Obtener la info del profesor.
            try{
                PreparedStatement estudiante = conectar.prepareStatement("SELECT * FROM clientes WHERE idclientes = ?");
                estudiante.setInt(1, idEstudiante);
                ResultSet resultado = estudiante.executeQuery();
                
                if(resultado.next()){
                    //Obtener promedio
                    clienteJSON.put("idEstudiante", resultado.getInt("idclientes"));
                    clienteJSON.put("nombre", resultado.getString("nombre"));
                    clienteJSON.put("correo", resultado.getString("correo"));
                    clienteJSON.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro"));
                    clienteJSON.put("foto", resultado.getString("foto"));
                    clienteJSON.put("descripcion", resultado.getString("descripcion"));
                    clienteJSON.put("valoraciones", false);
                    jsonArray.add(clienteJSON);
                    
                    //Obtener Valoraciones
                    PreparedStatement valoraciones = conectar.prepareStatement("SELECT * FROM valoracionestudiante WHERE clientes_idclientes = ?");
                    valoraciones.setInt(1, idEstudiante);
                    ResultSet resultadoVal = valoraciones.executeQuery();
                    
                    if(resultadoVal.next()){
                        JSONObject jsonValoraciones1 = new JSONObject();
                            jsonValoraciones1.put("valoraciones", true);
                            jsonValoraciones1.put("valoracion", resultadoVal.getFloat("valoracion"));
                            jsonValoraciones1.put("comentario", resultadoVal.getString("comentario"));
                            jsonValoraciones1.put("error", false);
                            jsonArray.add(jsonValoraciones1);
                        while(resultadoVal.next()){
                            JSONObject jsonValoraciones = new JSONObject();
                            jsonValoraciones.put("valoraciones", true);
                            jsonValoraciones.put("valoracion", resultadoVal.getFloat("valoracion"));
                            jsonValoraciones.put("comentario", resultadoVal.getString("comentario"));
                            jsonValoraciones.put("error", false);
                            jsonArray.add(jsonValoraciones);
                        }
                    }else{
                        JSONObject jsonValoraciones = new JSONObject();
                        jsonValoraciones.put("valoraciones", true);
                        jsonValoraciones.put("error", true);
                        jsonArray.add(jsonValoraciones);
                    }
                    
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
         }else{
            System.out.println("Error en obtenerValoracion.");
        }
        
        return jsonArray;
        
    }

    public void iniciarSesion(String correo, String contrasena) {
        conectar = ConexionMySQL.connection();
        if (conectar != null) {
            try {
                MD5 md5 = new MD5();
                String pass = md5.getMD5(contrasena);
                String query = "SELECT * FROM profesores WHERE BINARY correo = ? AND BINARY contrasena = ?";
                PreparedStatement obtenerDatos = conectar.prepareStatement(query);
                obtenerDatos.setString(1, correo);
                obtenerDatos.setString(2, pass);
                ResultSet resultado = obtenerDatos.executeQuery();

                if (resultado.next()) {
                    profesor.setIdProfesor(resultado.getInt("idprofesores"));
                    profesor.setNombre(resultado.getString("nombre"));
                    profesor.setEstado(resultado.getString("estado"));
                    profesor.setVerificado(resultado.getBoolean("verificado"));
                    profesor.setRangoClase(resultado.getInt("rangoClase"));
                    profesorRegistrado = true;
                } else 
                    profesorRegistrado = false;                   
                
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Error en la conexión iniciarSesion.");
        }

    }//Fin método iniciarSesion.

    public void datosActualizarPerfil(JSONObject datos) {

        profesor.setIdProfesor(Integer.parseInt(String.valueOf(datos.get("idProfesor"))));
        profesor.setCelular(datos.get("celular").toString());
        profesor.setTelefonoLocal(datos.get("telefonoLocal").toString());
        profesor.setDireccion(datos.get("direccion").toString());
        profesor.setDescripcion(String.valueOf(datos.get("descripcion")));

    }//Fin método datosActualizarPerfil.
    
    public void matchSesionWeb(int idProfesor, int idSesion){
        
        conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            
            try{
                System.out.println("UPDATE sesiones SET profesores_idprofesores =" + idProfesor +" WHERE idsesiones = " + idSesion);
                PreparedStatement match = conectar.prepareStatement("UPDATE sesiones SET profesores_idprofesores = ? WHERE idsesiones = ?");
                match.setInt(1 , idProfesor);
                match.setInt(2 , idSesion);
                match.execute();
                
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            
            System.out.println("Error en matchSesion.");
            
        }
        
    }//Fin método matchSesion.
    
    public void matchSesion(JSONObject jsonDatos){
        
        conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement match = conectar.prepareStatement("UPDATE sesiones SET profesores_idprofesores = ? WHERE idsesiones = ?");
                match.setInt(1 , Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                match.setInt(2 , Integer.parseInt(jsonDatos.get("idSesion").toString()));
                match.execute();
                
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            
            System.out.println("Error en matchSesion.");
            
        }
        
    }//Fin método matchSesion.
    
    public JSONArray sesionesMatchProfesor(int idProfesor){
        
        conectar = ConexionMySQL.connection();
        JSONArray jsonArrayMatch = new JSONArray();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement sesionesMatch = conectar.prepareStatement("SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.clientes_idclientes = clientes.idclientes AND profesores_idprofesores = ? ORDER BY idsesiones DESC");
                sesionesMatch.setInt(1 , idProfesor);
                ResultSet resultado = sesionesMatch.executeQuery();
                
                while(resultado.next()){
                    
                    JSONObject jsonSesionesMatchProfesor = new JSONObject();
                    jsonSesionesMatchProfesor.put("idsesiones", resultado.getInt("idsesiones"));
                    jsonSesionesMatchProfesor.put("nombreEstudiante", resultado.getString("nombre") + " " + resultado.getString("apellidoPaterno") + " " + resultado.getString("apellidoMaterno"));
                    jsonSesionesMatchProfesor.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                    jsonSesionesMatchProfesor.put("descripcion", resultado.getString("descripcion"));
                    jsonSesionesMatchProfesor.put("actualizado", resultado.getDate("actualizado"));
                    jsonSesionesMatchProfesor.put("fecha", resultado.getDate("fecha"));
                    jsonSesionesMatchProfesor.put("correo", resultado.getString("correo"));
                    jsonSesionesMatchProfesor.put("latitud", resultado.getDouble("latitud"));
                    jsonSesionesMatchProfesor.put("longitud", resultado.getDouble("longitud"));
                    jsonSesionesMatchProfesor.put("foto", resultado.getString("foto"));
                    jsonSesionesMatchProfesor.put("lugar", resultado.getString("lugar"));
                    jsonSesionesMatchProfesor.put("tiempo", resultado.getInt("tiempo"));
                    jsonSesionesMatchProfesor.put("idSeccion", resultado.getInt("idSeccion"));
                    jsonSesionesMatchProfesor.put("idNivel", resultado.getInt("idNivel"));
                    jsonSesionesMatchProfesor.put("idBloque", resultado.getInt("idBloque"));
                    jsonSesionesMatchProfesor.put("tipoClase", resultado.getString("tipoClase") + ": " + resultado.getString("personas") + " Personas");
                    jsonSesionesMatchProfesor.put("extras", resultado.getString("extras"));
                    jsonSesionesMatchProfesor.put("horario", resultado.getString("horario")); 
                    jsonSesionesMatchProfesor.put("tipoPlan", resultado.getString("tipoPlan"));
                    jsonSesionesMatchProfesor.put("finalizado", resultado.getBoolean("finalizado"));
                    jsonArrayMatch.add(jsonSesionesMatchProfesor);
                    
                }
 
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{     
            System.out.println("Error en sesionesMatchProfesor.");     
        }
        
        System.out.println(jsonArrayMatch);
        
        return jsonArrayMatch;
        
    }//Fin método sesionesMatchProfesor.
    
    public JSONObject informacionSesionMatch(int idSesion){
        
        conectar = ConexionMySQL.connection(); 
        if(conectar != null){      
            try{            
                PreparedStatement sesion = conectar.prepareStatement("SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.clientes_idclientes = clientes.idclientes AND idsesiones = ?");
                sesion.setInt(1 , idSesion);
                ResultSet resultado = sesion.executeQuery();
                
                if(resultado.next()){
                    jsonMatch.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                    jsonMatch.put("idSesion", resultado.getInt("idsesiones"));
                    jsonMatch.put("nombre", resultado.getString("nombre"));
                    jsonMatch.put("idProfesor", resultado.getInt("profesores_idprofesores"));
                    jsonMatch.put("descripcion", resultado.getString("descripcion"));
                    jsonMatch.put("correo", resultado.getString("correo"));
                    jsonMatch.put("latitud", resultado.getDouble("latitud"));
                    jsonMatch.put("longitud", resultado.getDouble("longitud"));
                    jsonMatch.put("foto", resultado.getString("foto"));
                    jsonMatch.put("lugar", resultado.getString("lugar"));
                    jsonMatch.put("fecha", resultado.getDate("fecha").toString());
                    jsonMatch.put("tiempo", resultado.getInt("tiempo"));
                    jsonMatch.put("idSeccion", resultado.getInt("idSeccion"));
                    jsonMatch.put("idNivel", resultado.getInt("idNivel"));
                    jsonMatch.put("idBloque", resultado.getInt("idBloque"));
                    jsonMatch.put("tipoClase", resultado.getString("tipoClase"));
                    jsonMatch.put("extras", resultado.getString("extras"));
                    jsonMatch.put("horario", resultado.getString("horario"));     
                    jsonMatch.put("finalizado", resultado.getBoolean("finalizado"));
                }
            
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            System.out.println("Error en informacionSesionMatch."); 
        }
        
        return jsonMatch;
        
    }//Fin método informacionSesionMatch.
    
    public JSONArray obtenerSesionesMovil(int rango){
        conectar = ConexionMySQL.connection();
        String query = null;
        
        if(rango == Constantes.BASICO)
            query = "SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.profesores_idprofesores IS NULL AND sesiones.idSeccion = ? AND sesiones.clientes_idclientes = clientes.idclientes ";
        else if(rango == Constantes.INTERMEDIO)
            query = "SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.profesores_idprofesores IS NULL AND (sesiones.idSeccion = ? OR sesiones.idSeccion = ?) AND sesiones.clientes_idclientes = clientes.idclientes";
        else if(rango == Constantes.AVANZADO)
            query = "SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.profesores_idprofesores IS NULL AND (sesiones.idSeccion = ? OR sesiones.idSeccion = ? OR sesiones.idSeccion = ?) AND sesiones.clientes_idclientes = clientes.idclientes";
        
        if(conectar != null){ 
            try{   
                PreparedStatement sesiones = conectar.prepareStatement(query);
                sesiones.setInt(1, 1);
                
                if(rango == Constantes.INTERMEDIO){
                    sesiones.setInt(2, 2);
                }else if(rango == Constantes.AVANZADO){
                    sesiones.setInt(2, 2);
                    sesiones.setInt(3, 3);
                }
                ResultSet resultado = sesiones.executeQuery();             
                while(resultado.next()){ 
                    JSONObject json = new JSONObject();
                    json.put("idSesion", resultado.getInt("idsesiones"));
                    json.put("latitud", resultado.getDouble("latitud"));
                    json.put("longitud", resultado.getDouble("longitud"));
                    json.put("nombre", resultado.getString("nombre"));
                    json.put("lugar", resultado.getString("lugar"));
                    json.put("idSeccion", resultado.getInt("idSeccion"));
                    json.put("idNivel", resultado.getInt("idNivel"));
                    json.put("idBloque", resultado.getInt("idBloque"));
                    json.put("tipoPlan", resultado.getString("tipoPlan"));
                    arrayJson.add(json);
                    json = null;  
                }  
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{ 
            System.out.println("Error en obtenerSesionesMovil."); 
        }
        
        return arrayJson;
        
    }

    public JSONObject actualizarDatosPerfil(){
        JSONObject respuesta = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{               
                String query = "UPDATE profesores SET celular = ?, telefonoLocal = ?, direccion = ?, descripcion = ? WHERE idprofesores = ?";
                PreparedStatement actualizar = conectar.prepareStatement(query);
                actualizar.setString(1, profesor.getCelular());
                actualizar.setString(2, profesor.getTelefonoLocal());
                actualizar.setString(3, profesor.getDireccion());
                actualizar.setString(4, profesor.getDescripcion());
                actualizar.setInt(5, profesor.getIdProfesor());
                actualizar.executeUpdate();
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Datos actualizados exitosamente.");                
            }catch(SQLException ex){             
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD.");
            }          
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
    }//Fin método actualizarDatosPerfil.

    public void actualizarFoto(JSONObject foto) {
        profesor.setFoto(String.valueOf(foto.get("nombre")));
        profesor.setIdProfesor(Integer.parseInt(String.valueOf(foto.get("idProfesor"))));
        conectar = ConexionMySQL.connection();

        if (conectar != null) {
            try {
                String query = "UPDATE profesores SET foto = ? WHERE idprofesores = ?";
                PreparedStatement actualizar = conectar.prepareStatement(query);
                actualizar.setString(1, profesor.getFoto());
                actualizar.setInt(2, profesor.getIdProfesor());
                actualizar.executeUpdate();               
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("Error en la conexión actualizarFoto.");
        }

    }//Fin método actualizarFoto.

    public void perfilProfesor(int idProfesor) {
        conectar = ConexionMySQL.connection();

        if (conectar != null) {
            try {
                String query = "SELECT * FROM profesores WHERE idprofesores = ?";
                PreparedStatement obtenerDatos = conectar.prepareStatement(query);
                obtenerDatos.setInt(1, idProfesor);
                ResultSet resultado = obtenerDatos.executeQuery();
                if (resultado.next()) {
                    profesor.setIdProfesor(resultado.getInt("idprofesores"));
                    profesor.setNombre(resultado.getString("nombre") + " " + resultado.getString("apellidoPaterno") + " " + resultado.getString("apellidoMaterno"));
                    profesor.setCelular(resultado.getString("celular"));
                    profesor.setTelefonoLocal(resultado.getString("telefonoLocal"));
                    profesor.setDireccion(resultado.getString("direccion"));
                    profesor.setCorreo(resultado.getString("correo"));
                    profesor.setFechaNacimiento(resultado.getDate("fechaNacimiento"));
                    profesor.setFechaRegistro(resultado.getDate("fechaDeRegistro"));
                    profesor.setFoto(resultado.getString("foto"));
                    profesor.setDescripcion(resultado.getString("descripcion"));  
                    profesorRegistrado = true;
                } else {
                    profesorRegistrado = false;                 
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("Error en la conexión iniciarSesion.");
        }
    }//Fin método perfilProfesor.

    public Profesor datosSesion() {
        if (profesorRegistrado) {
            return profesor;
        } else {
            return null;
        }
    }//Fin método datosSesion.

    public void nuevoProfesor(JSONObject jsonProfesor) {
        profesor.setNombre(String.valueOf(jsonProfesor.get("nombre")));
        profesor.setApellidoPaterno(jsonProfesor.get("paterno").toString());
        profesor.setApellidMaterno(jsonProfesor.get("materno").toString());
        profesor.setCorreo(String.valueOf(jsonProfesor.get("correo")));
        profesor.setContrasena(String.valueOf(jsonProfesor.get("contrasena")));
        java.sql.Date dateFechaNacimiento = (java.sql.Date.valueOf(String.valueOf(jsonProfesor.get("fechaNacimiento"))));
        profesor.setFechaNacimiento(dateFechaNacimiento);
        profesor.setFechaRegistro(java.sql.Date.valueOf("2021-01-27"));
        profesor.setCelular(jsonProfesor.get("celular").toString());
        profesor.setTelefonoLocal(jsonProfesor.get("telefono").toString());
        profesor.setDireccion(jsonProfesor.get("direccion").toString());
        profesor.setGenero(jsonProfesor.get("genero").toString());
    }
 
    public JSONObject guardarProfesor() {
        conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if (conectar != null) {
            try {
                
                //VALIDAMOS QUE NO EXISTA EL CORREO
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM profesores WHERE correo = ?");
                consulta.setString(1, profesor.getCorreo());
                ResultSet resultadoCorreo = consulta.executeQuery();
                
                if(resultadoCorreo.next()){
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", "Correo electronico ya registrado.");
                }else{
                    //CREAMOS TOKEN
                    MD5 md5 = new MD5();
                    String token = md5.getMD5(profesor.getCorreo() + profesor.getNombre());
                    String pass = md5.getMD5(profesor.getContrasena());
                    String query = "INSERT INTO profesores (nombre, correo, contrasena, fechaNacimiento, fechaDeRegistro, apellidoPaterno, apellidoMaterno,"
                            + " celular, telefonoLocal, direccion, genero, token_verificacion) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                    PreparedStatement agregarDatos = conectar.prepareStatement(query);
                    agregarDatos.setString(1, profesor.getNombre());
                    agregarDatos.setString(2, profesor.getCorreo());
                    agregarDatos.setString(3, pass);
                    agregarDatos.setDate(4, profesor.getFechaNacimiento());
                    agregarDatos.setDate(5, profesor.getFechaRegistro());
                    agregarDatos.setString(6, profesor.getApellidoPaterno());
                    agregarDatos.setString(7, profesor.getApellidMaterno());
                    agregarDatos.setString(8, profesor.getCelular());
                    agregarDatos.setString(9, profesor.getTelefonoLocal());
                    agregarDatos.setString(10, profesor.getDireccion());
                    agregarDatos.setString(11, profesor.getGenero());
                    agregarDatos.setString(12, token);
                    agregarDatos.execute();

                    respuesta.put("token", token);
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", "Profesor registrado exitosamente.");
                }
            
            } catch (SQLException ex) {
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD.");
            }
        } else {
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }

        return respuesta;
    }//Fin método guardarProfesor.

    public JSONObject obtenerCuentaBancaria(int idProfesor) {
        JSONObject respuesta = new JSONObject();
        JSONObject datos = new JSONObject();
        conectar = ConexionMySQL.connection();

        if (conectar != null) {
            try {
                Statement estado = conectar.createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM datosbancariosprofesores WHERE profesores_idprofesores = " + idProfesor);
                if (resultado.next()) {
                    datos.put("nombreTitular", resultado.getString("nombreTitular"));
                    datos.put("banco", resultado.getString("banco"));
                    datos.put("clabe", resultado.getString("clabe"));
                    datos.put("existe", true);
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", datos);
                } else {
                    datos.put("existe", false);
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", datos);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error al obtener cuenta registrada.");
            }
        } else {
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error al obtener cuenta registrada.");
        }

        return respuesta;

    }//Fin método obtenerCuentaBancaria.

    public JSONObject guardarCuentaBancaria(JSONObject jsonDatos) {
        conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if (conectar != null) {
            try {
                String query = "INSERT INTO datosbancariosprofesores (nombreTitular, banco, clabe, profesores_idprofesores) VALUES (?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setString(1, jsonDatos.get("nombreTitular").toString());
                agregarDatos.setString(2, jsonDatos.get("banco").toString());
                agregarDatos.setString(3, jsonDatos.get("clabe").toString());
                agregarDatos.setInt(4, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                agregarDatos.execute();    
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Datos bancarios guardados exitosamente.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD.");
            }
        } else {
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }

        return respuesta;
    }//Fin método guardarCuentaBancaria.

    public JSONObject actualizarCuentaBancaria(JSONObject jsonDatos) {
        conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if (conectar != null) {
            try {               
                PreparedStatement consultaRegistro = conectar.prepareStatement("SELECT * FROM datosbancariosprofesores WHERE profesores_idprofesores = ?");
                consultaRegistro.setInt(1 , Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                ResultSet resultado = consultaRegistro.executeQuery();
                
                if(resultado.next()){                   
                    String query = "UPDATE datosbancariosprofesores SET nombreTitular = ?, banco = ?, clabe = ? WHERE profesores_idprofesores = ?";
                    PreparedStatement agregarDatos = conectar.prepareStatement(query);
                    agregarDatos.setString(1, jsonDatos.get("nombreTitular").toString());
                    agregarDatos.setString(2, jsonDatos.get("banco").toString());
                    agregarDatos.setString(3, jsonDatos.get("clabe").toString());
                    agregarDatos.setInt(4, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                    agregarDatos.executeUpdate();
                }else{                 
                    PreparedStatement insert = conectar.prepareStatement("INSERT INTO datosbancariosprofesores (nombreTitular, banco, clabe, profesores_idprofesores) VALUES (?,?,?,?)");
                    insert.setString(1, jsonDatos.get("nombreTitular").toString());
                    insert.setString(2, jsonDatos.get("banco").toString());
                    insert.setString(3, jsonDatos.get("clabe").toString());
                    insert.setInt(4 , Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                    insert.execute();                   
                }     
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Datos actualizados exitosamente.");
            } catch (SQLException ex) {
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD.");
                System.out.println(ex.getMessage());
            }
        } else {
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;

    }//Fin método guardarCuentaBancaria.

}
