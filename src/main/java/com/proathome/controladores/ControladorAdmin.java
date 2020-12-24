package com.proathome.controladores;

import com.google.gson.Gson;
import com.proathome.modelos.Admin;
import com.proathome.modelos.Constantes;
import com.proathome.modelos.Profesor;
import com.proathome.mysql.ConexionMySQL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Marvin
 */
public class ControladorAdmin {
    
    private Gson gson;
    private Profesor profesores[];
    private Connection conectar;
    public static final int ESTUDIANTE = 1;
    public static final int PROFESOR = 2;
    
    public JSONObject agendarCita(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
    
        if(conectar != null){
            try{
                //Verifricar la disponibilidad de la cita en fecha 1.
                PreparedStatement consultaF1 = conectar.prepareStatement("SELECT * FROM citas WHERE fecha1 = ? AND horario1 = ?");
                consultaF1.setDate(1, java.sql.Date.valueOf(jsonDatos.get("fecha1").toString()));
                consultaF1.setString(2, jsonDatos.get("horario1").toString());
                ResultSet resultadoF1 = consultaF1.executeQuery();
                
                if(resultadoF1.next()){
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", "El horario de la FECHA 1 ya tiene una posible cita agendada.");
                }else{
                    //Validamos la fecha 2.
                    PreparedStatement consultaF2 = conectar.prepareStatement("SELECT * FROM citas WHERE fecha2 = ? AND horario2 = ?");
                    consultaF2.setDate(1, java.sql.Date.valueOf(jsonDatos.get("fecha2").toString()));
                    consultaF2.setString(2, jsonDatos.get("horario2").toString());
                    ResultSet resultadoF2 = consultaF2.executeQuery();
                    
                    if(resultadoF2.next()){
                        respuesta.put("respuesta", false);
                        respuesta.put("mensaje", "El horario de la FECHA 2 ya tiene una posible cita agendada.");
                    }else{
                        //Guardar cita
                        PreparedStatement guardar = conectar.prepareStatement("INSERT INTO citas (fecha1, fecha2, horario1, horario2,"
                                + " tipoCita, profesores_idprofesores, operadores_idoperadores, datosAdicionales) VALUES (?,?,?,?,?,?,?,?)");
                        guardar.setDate(1, java.sql.Date.valueOf(jsonDatos.get("fecha1").toString()));
                        guardar.setDate(2, java.sql.Date.valueOf(jsonDatos.get("fecha2").toString()));
                        guardar.setString(3, jsonDatos.get("horario1").toString());
                        guardar.setString(4, jsonDatos.get("horario2").toString());
                        guardar.setString(5, jsonDatos.get("tipoCita").toString());
                        guardar.setInt(6, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                        guardar.setInt(7, Integer.parseInt(jsonDatos.get("idOperador").toString()));
                        guardar.setString(8, jsonDatos.get("datosAdicionales").toString());
                        guardar.execute();
                        //Cambiar estatus
                        PreparedStatement estatus = conectar.prepareStatement("UPDATE profesores SET estado = ? WHERE idprofesores = ?");
                        estatus.setString(1, "cita");
                        estatus.setInt(2, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                        estatus.execute();
                        respuesta.put("respuesta", true);
                        respuesta.put("mensaje", "Hay disponibilidad.");
                    }
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en agendarCita.");
        }
    
        return respuesta;
    }
    
    public void rechazarDocumentacion(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                //Cambiar estatus a registro.
                PreparedStatement registro = conectar.prepareStatement("UPDATE profesores SET estado = ? WHERE idprofesores = ?");
                registro.setString(1, "registro");
                registro.setInt(2, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                registro.execute();
                //Eliminamos la documentación.
                PreparedStatement eliminar = conectar.prepareStatement("DELETE FROM documentacionprofesor WHERE idDocumentacion = ? AND profesores_idprofesores = ?");
                eliminar.setInt(1, Integer.parseInt(jsonDatos.get("idDocumentacion").toString()));
                eliminar.setInt(2, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                eliminar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en rechazarDocumentación.");
        }
    }
    
    public JSONArray getSolicitudesAsignadas(int idOperador){
        Connection conectar = ConexionMySQL.connection();
        JSONArray jsonAsignados = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement asigandos = conectar.prepareCall("SELECT * FROM documentacionprofesor INNER JOIN profesores WHERE documentacionprofesor.operadores_idoperadores = ? AND profesores.idprofesores = documentacionprofesor.profesores_idprofesores");
                asigandos.setInt(1, idOperador);
                ResultSet resultado = asigandos.executeQuery();
                
                while(resultado.next()){
                    JSONObject jsonProf = new JSONObject();
                    jsonProf.put("nombre", resultado.getString("nombre"));
                    jsonProf.put("correo", resultado.getString("correo"));
                    jsonProf.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro"));
                    jsonProf.put("certificado", resultado.getString("certificado"));
                    jsonProf.put("ine", resultado.getString("ine"));
                    jsonProf.put("idDocumentacion", resultado.getInt("idDocumentacion"));
                    System.out.println(resultado.getInt("idDocumentacion"));
                    jsonProf.put("idProfesor", resultado.getInt("profesores_idprofesores"));
                    jsonAsignados.add(jsonProf);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en getSolicitudesAsignadas.");
        }
        
        return jsonAsignados;
    }
    
    public void asociarSolicitud(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement asociar = conectar.prepareStatement("UPDATE documentacionprofesor SET operadores_idoperadores = ? WHERE idDocumentacion = ?");
                asociar.setInt(1, Integer.parseInt(jsonDatos.get("idOperador").toString()));
                asociar.setInt(2, Integer.parseInt(jsonDatos.get("idDocumentacion").toString()));
                asociar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en asociarSolicitud.");
        }
    }
    
    public JSONArray obtenerSolicitudes(){
        Connection conectar = ConexionMySQL.connection();
        JSONArray jsonSolicitudes = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement solicitudes = conectar.prepareStatement("SELECT * FROM profesores INNER JOIN documentacionprofesor WHERE documentacionprofesor.profesores_idprofesores = profesores.idprofesores AND profesores.estado = ? AND documentacionprofesor.operadores_idoperadores IS NULL");
                solicitudes.setString(1, "documentacion");
                ResultSet resultado = solicitudes.executeQuery();
                while(resultado.next()){
                    JSONObject jsonProf = new JSONObject();
                    jsonProf.put("nombre", resultado.getString("nombre"));
                    jsonProf.put("correo", resultado.getString("correo"));
                    jsonProf.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro"));
                    jsonProf.put("certificado", resultado.getString("certificado"));
                    jsonProf.put("ine", resultado.getString("ine"));
                    jsonProf.put("idDocumentacion", resultado.getInt("idDocumentacion"));
                    jsonSolicitudes.add(jsonProf);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en obtenerSOlicitudes.");
        }
        
        return jsonSolicitudes;
    }
    
    public JSONObject ticketSolucionado(int idTicket){
        Connection conectar = ConexionMySQL.connection();
        JSONObject ticketJSON = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement solucionado = conectar.prepareStatement("SELECT estatus FROM tickets_ayuda WHERE idtickets_ayuda = ?");
                solucionado.setInt(1, idTicket);
                ResultSet resultado = solucionado.executeQuery();
                
                if(resultado.next()){
                    if(resultado.getInt("estatus") == Constantes.ESTATUS_SOLUCIONADO)
                        ticketJSON.put("solucionado", true);
                    else
                        ticketJSON.put("solucionado", false);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en ticketSolucionado.");
        }
        
        return ticketJSON;
    }
    
    public void enviarMensaje(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement mensaje = conectar.prepareStatement("INSERT INTO msg_tickets (mensaje, idUsuario_Operador, operadorBool, tickets_ayuda_idtickets_ayuda) VALUES (?,?,?,?)");
                mensaje.setString(1, jsonDatos.get("mensaje").toString());
                mensaje.setInt(2, Integer.parseInt(jsonDatos.get("idUsuario").toString()));
                mensaje.setBoolean(3, Boolean.parseBoolean(jsonDatos.get("operador").toString()));
                mensaje.setInt(4, Integer.parseInt(jsonDatos.get("idTicket").toString()));
                mensaje.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en enviarMensaje.");
        }
    }
    
    public JSONArray obtenerMensajes(int idTicket, int tipoUsuario){
        Connection conectar = ConexionMySQL.connection();
        JSONArray mensajes = new JSONArray();
        
        if(conectar != null){
            try{
                String query = "";
                if(tipoUsuario == Constantes.TIPO_USUARIO_ESTUDIANTE)
                    query = "SELECT * FROM msg_tickets INNER JOIN clientes WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND clientes.idclientes = msg_tickets.idUsuario_Operador";
                else if(tipoUsuario == Constantes.TIPO_USUARIO_PROFESOR)
                    query = "SELECT * FROM msg_tickets INNER JOIN profesores WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND profesores.idprofesores = msg_tickets.idUsuario_Operador";
             
                PreparedStatement mensajesConsulta = conectar.prepareStatement(query);
                mensajesConsulta.setInt(1, idTicket);
                ResultSet resultado = mensajesConsulta.executeQuery();
                
                while(resultado.next()){
                    JSONObject mensaje = new JSONObject();
                    mensaje.put("nombreUsuario", resultado.getString("nombre"));
                    mensaje.put("mensaje", resultado.getString("mensaje"));
                    mensaje.put("idUsuario", resultado.getInt("idUsuario_Operador"));
                    mensaje.put("operadorBool", resultado.getBoolean("operadorBool"));
                    mensajes.add(mensaje);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en obtenerMensajes.");
        }
        
        return mensajes;
    }
    
    public JSONArray ticketsFinalizados(int idOperador){
        Connection conectar = ConexionMySQL.connection();
        JSONArray jsonFinalizados = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement finalizados = conectar.prepareStatement("SELECT * FROM tickets_ayuda WHERE operadores_idoperadores = ? AND estatus = ?");
                finalizados.setInt(1, idOperador);
                finalizados.setInt(2, Constantes.ESTATUS_SOLUCIONADO);
                ResultSet resultado = finalizados.executeQuery();
                
                while(resultado.next()){
                    JSONObject ticket = new JSONObject();
                    ticket.put("idTicket", resultado.getInt("idtickets_ayuda"));
                    ticket.put("noTicket", "00" + resultado.getInt("idtickets_ayuda"));
                    ticket.put("topico", resultado.getString("topico"));
                    ticket.put("descripcion", resultado.getString("descripcion"));
                    ticket.put("fechaCreacion", resultado.getDate("fechaCreacion"));
                    ticket.put("estatus", resultado.getInt("estatus"));
                    ticket.put("idUsuario", resultado.getInt("idUsuario"));
                    ticket.put("tipoUsuario", resultado.getInt("tipoUsuario"));
                    jsonFinalizados.add(ticket);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en ticketsFinalizados.");
        }
        
        return jsonFinalizados;
    }
    
    public JSONArray obtenerTicketsAsociados(int idOperador){
        Connection conectar = ConexionMySQL.connection();
        JSONArray jsonAsociados = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement asociados = conectar.prepareStatement("SELECT * FROM tickets_ayuda WHERE operadores_idoperadores = ? AND estatus = ?");
                asociados.setInt(1, idOperador);
                asociados.setInt(2, Constantes.ESTATUS_EN_CURSO);
                ResultSet resultado = asociados.executeQuery();
                
                while(resultado.next()){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("idTicket", resultado.getInt("idtickets_ayuda"));
                    jsonObject.put("noTicket", "00" + resultado.getInt("idtickets_ayuda"));
                    jsonObject.put("tipoUsuario", resultado.getInt("tipoUsuario"));
                    jsonObject.put("descripcion", resultado.getString("descripcion"));
                    jsonObject.put("topico", resultado.getString("topico"));
                    jsonObject.put("fechaCreacion", resultado.getDate("fechaCreacion"));
                    jsonObject.put("estatus", resultado.getInt("estatus"));
                    jsonObject.put("idUsuario", resultado.getInt("idUsuario"));
                    jsonAsociados.add(jsonObject);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en obtenerTicketsAsociados.");
        }
        
        return jsonAsociados;
    }
    
    public void asociarTicketAdmin(int idTicket, int idOperador){
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement actualizar = conectar.prepareStatement("UPDATE tickets_ayuda SET operadores_idoperadores = ?, estatus = ? WHERE idtickets_ayuda = ?");
                actualizar.setInt(1, idOperador);
                actualizar.setInt(2, Constantes.ESTATUS_EN_CURSO);
                actualizar.setInt(3, idTicket);
                actualizar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error");
        }
    }
    
    public JSONObject infoTicketAdmin(int idTicket, int tipoUsuario){
        Connection conectar = ConexionMySQL.connection();
        JSONObject ticketInfo = new JSONObject();
        
        if(conectar != null){
            try{
                String consulta = "";
                if(tipoUsuario == Constantes.TIPO_USUARIO_ESTUDIANTE)
                    consulta = "SELECT * FROM tickets_ayuda INNER JOIN clientes WHERE tickets_ayuda.idtickets_ayuda = ? AND clientes.idclientes = tickets_ayuda.idUsuario";
                else if(tipoUsuario == Constantes.TIPO_USUARIO_PROFESOR)
                    consulta = "SELECT * FROM tickets_ayuda INNER JOIN profesores WHERE tickets_ayuda.idtickets_ayuda = ? AND profesores.idprofesores = tickets_ayuda.idUsuario";
                
                
                PreparedStatement ticket = conectar.prepareStatement(consulta);
                ticket.setInt(1, idTicket);
                ResultSet resultado = ticket.executeQuery();

                if(resultado.next()){
                    ticketInfo.put("idTicket", resultado.getInt("idtickets_ayuda"));
                    ticketInfo.put("noTicket", "00" + resultado.getInt("idtickets_ayuda"));
                    ticketInfo.put("nombreUsuario", resultado.getString("nombre"));
                    ticketInfo.put("correo", resultado.getString("correo"));
                    ticketInfo.put("estatus", resultado.getInt("estatus"));
                    ticketInfo.put("tipoUsuario", resultado.getInt("tipoUsuario"));
                    ticketInfo.put("topico", resultado.getString("topico"));
                    ticketInfo.put("descripcion", resultado.getString("descripcion"));
                    ticketInfo.put("fechaCreacion", resultado.getDate("fechaCreacion"));
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en infoTicketAdmin.");
        }
        
        System.out.println("AVE: " + ticketInfo);
        return ticketInfo;
    }
    
    public JSONArray obtenerTicketsAdmin(){
        Connection conectar = ConexionMySQL.connection();
        JSONArray tickets = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement ticketsConsulta = conectar.prepareStatement("SELECT * FROM tickets_ayuda WHERE estatus = ?");
                ticketsConsulta.setInt(1, Constantes.ESTATUS_SIN_OPERADOR);
                ResultSet resultado = ticketsConsulta.executeQuery();
                
                while(resultado.next()){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("idTicket", resultado.getInt("idtickets_ayuda"));
                    jsonObject.put("noTicket", "00" + resultado.getInt("idtickets_ayuda"));
                    jsonObject.put("tipoUsuario", resultado.getInt("tipoUsuario"));
                    jsonObject.put("descripcion", resultado.getString("descripcion"));
                    jsonObject.put("topico", resultado.getString("topico"));
                    jsonObject.put("fechaCreacion", resultado.getDate("fechaCreacion"));
                    jsonObject.put("estatus", resultado.getInt("estatus"));
                    jsonObject.put("idUsuario", resultado.getInt("idUsuario"));
                    tickets.add(jsonObject);
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en obtenerTicketsAdmin.");
        }
        
        return tickets;
    }
    
    public JSONArray obtenerMsgTicket(int idUsuario, int tipoUsuario, int idTicket){
        Connection conectar = ConexionMySQL.connection();
        JSONArray mensajesArray = new JSONArray();
        
        if(conectar != null){
            try{
                /*Consultar Ticket*/
                PreparedStatement ticket = conectar.prepareStatement("SELECT * FROM tickets_ayuda WHERE idtickets_ayuda = ? AND tipoUsuario = ?");
                ticket.setInt(1, idTicket);
                ticket.setInt(2, tipoUsuario);
                ResultSet ticketRes = ticket.executeQuery();
                JSONObject jsonObjectTicket = new JSONObject();
                
                if(ticketRes.next()){//Datos ticket
                    jsonObjectTicket.put("topico", ticketRes.getString("topico"));
                    jsonObjectTicket.put("descripcion", ticketRes.getString("descripcion"));
                    jsonObjectTicket.put("fechaCreacion", ticketRes.getDate("fechaCreacion"));
                    jsonObjectTicket.put("estatus", ticketRes.getInt("estatus"));
                    jsonObjectTicket.put("operador", ticketRes.getInt("operadores_idoperadores"));
                    JSONObject jsonTicket = new JSONObject();
                    jsonTicket.put("ticket", jsonObjectTicket);
                    mensajesArray.add(jsonTicket);
                }
                
                /*Consulta de mensajes Ticket*/
                String query = "";
                if(tipoUsuario == Constantes.TIPO_USUARIO_ESTUDIANTE)
                    query = "SELECT * FROM msg_tickets INNER JOIN clientes WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND clientes.idclientes = msg_tickets.idUsuario_Operador";
                else if(tipoUsuario == Constantes.TIPO_USUARIO_PROFESOR)
                    query = "SELECT * FROM msg_tickets INNER JOIN profesores WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND profesores.idprofesores = msg_tickets.idUsuario_Operador";
                
                PreparedStatement mensajes = conectar.prepareStatement(query);
                mensajes.setInt(1, idTicket);
                ResultSet msgRes = mensajes.executeQuery();
                JSONArray jsonMensajesTicket = new JSONArray();
                
                
                while(msgRes.next()){
                    JSONObject msgJSON = new JSONObject();
                    if(msgRes.getBoolean("operadorBool"))
                        msgJSON.put("nombreUsuario", msgRes.getString("nombre"));
                    else
                        msgJSON.put("nombreUsuario", "Yo");
                    msgJSON.put("msg", msgRes.getString("mensaje"));
                    msgJSON.put("operador", msgRes.getBoolean("operadorBool"));
                    msgJSON.put("idUsuario", msgRes.getInt("idUsuario_Operador"));
                    jsonMensajesTicket.add(msgJSON);
                }
                
                
                JSONObject mensajesTotalesJSON = new JSONObject();
                mensajesTotalesJSON.put("mensajes", jsonMensajesTicket);
                mensajesArray.add(mensajesTotalesJSON);
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }

        }else{
            System.out.println("Error en obtenerMsgTicket");
        }
        
        return mensajesArray;
    }
    
    public void cambiarEstado(int idProfesor, boolean estado){
        
        conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement cambiarEstado = conectar.prepareStatement("UPDATE profesores SET estado = ? WHERE idprofesores = ?");
                cambiarEstado.setBoolean(1 , estado);
                cambiarEstado.setInt(2 , idProfesor);
                cambiarEstado.execute();
                
            }catch(SQLException ex){
                
                ex.printStackTrace();
                
            }
            
        }else{
            
            System.out.println("Error en cambiarEstado.");
            
        }
        
    }//Fin método cambiarEstado.
    
    public Admin datosAdmin(String usuario, String contrasena, int idAdmin){ 
        Admin admin = new Admin();
        admin.setUsuario(usuario);
        admin.setContrasena(contrasena);
        admin.setIdAdmin(idAdmin);
        
        return admin;   
    }
    
    public JSONObject iniciarSesion(String usuario, String contrasena){
        conectar = ConexionMySQL.connection();
        JSONObject jsonAdmin = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM operadores WHERE BINARY usuario = ? AND BINARY contrasena = ?");
                consulta.setString(1 , usuario);
                consulta.setString(2 , contrasena);
                ResultSet resultado = consulta.executeQuery();
                if(resultado.next()){
                    jsonAdmin.put("idOperador", resultado.getInt("idoperadores"));  
                    jsonAdmin.put("rango", resultado.getInt("rango"));
                    jsonAdmin.put("result", true);
                }else{
                    jsonAdmin.put("result", false);
                    jsonAdmin.put("error", "Usuario no encontrado.");
                }   
            }catch(SQLException ex){ 
                ex.printStackTrace();
            } 
        }else{ 
            System.out.println("Error en iniciarSesion."); 
        }
        
        return jsonAdmin;     
    }//Fin método iniciarSesion.
    
}
