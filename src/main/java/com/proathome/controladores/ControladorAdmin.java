package com.proathome.controladores;

import com.google.gson.Gson;
import com.proathome.modelos.Admin;
import com.proathome.modelos.Constantes;
import com.proathome.modelos.Profesor;
import com.proathome.mysql.ConexionMySQL;
import java.sql.Connection;
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
                    ticketInfo.put("nombreUsuario", resultado.getString("nombre"));
                    ticketInfo.put("correo", resultado.getString("correo"));
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
                PreparedStatement mensajes = conectar.prepareStatement("SELECT * FROM msg_tickets WHERE tickets_ayuda_idtickets_ayuda = ?");
                mensajes.setInt(1, idTicket);
                ResultSet msgRes = mensajes.executeQuery();
                JSONArray jsonMensajesTicket = new JSONArray();
                
                
                while(msgRes.next()){
                    JSONObject msgJSON = new JSONObject();
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
    
    public JSONArray obtenerMensajes(int tipoCliente){
        
        conectar = ConexionMySQL.connection();
        JSONArray arrayMensajes = new JSONArray();
        
        if(conectar != null){
            try{
                if(tipoCliente == ControladorAdmin.ESTUDIANTE){
                    PreparedStatement mensajesConsulta = conectar.prepareStatement("SELECT * FROM ayuda INNER JOIN clientes WHERE clientes.idclientes = ayuda.idCliente AND ayuda.tipoCliente = ?");
                    mensajesConsulta.setInt(1, tipoCliente);
                    ResultSet resultado = mensajesConsulta.executeQuery();
                    while(resultado.next()){
                        JSONObject mensajes = new JSONObject();
                        mensajes.put("nombre", resultado.getString("nombre"));
                        mensajes.put("correo", resultado.getString("correo"));
                        mensajes.put("mensaje", resultado.getString("mensaje"));
                        arrayMensajes.add(mensajes);
                    }
                }else if(tipoCliente == ControladorAdmin.PROFESOR){
                    PreparedStatement mensajesConsulta = conectar.prepareStatement("SELECT * FROM ayuda INNER JOIN profesores WHERE profesores.idprofesores = ayuda.idCliente AND ayuda.tipoCliente = ?");
                    mensajesConsulta.setInt(1, tipoCliente);
                    ResultSet resultado = mensajesConsulta.executeQuery();
                    while(resultado.next()){
                        JSONObject mensajes = new JSONObject();
                        mensajes.put("nombre", resultado.getString("nombre"));
                        mensajes.put("correo", resultado.getString("correo"));
                        mensajes.put("mensaje", resultado.getString("mensaje"));
                        arrayMensajes.add(mensajes);
                    }
                }
          
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en obtenerMensajes");
        }
        
        return arrayMensajes;
        
    }
    
    public void enviarMensaje(JSONObject mensaje){
        
        conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement enviar = conectar.prepareStatement("INSERT INTO ayuda (mensaje, idCliente, tipoCliente) VALUES (?,?,?)");
                enviar.setString(1, mensaje.get("mensaje").toString());
                enviar.setInt(2, Integer.parseInt(mensaje.get("idCliente").toString()));
                enviar.setInt(3, Integer.parseInt(mensaje.get("tipoCliente").toString()));
                enviar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en enviarMensaje");
        }
        
    }//Fin método enviarMensaje.
    
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
    
    public String obtenerSolicitudes(){
        
        gson = new Gson();
        conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement registros = conectar.prepareStatement("SELECT COUNT(*) AS registros FROM profesores");
                ResultSet resultadoRegistros = registros.executeQuery();
                
                
                if(resultadoRegistros.next()){
                    
                    profesores = new Profesor[resultadoRegistros.getInt("registros")];
                    PreparedStatement solicitudes = conectar.prepareStatement("SELECT * FROM profesores ORDER BY fechaDeRegistro DESC");
                    ResultSet resultado = solicitudes.executeQuery();
                    int aux = 0;
                    
                    while(resultado.next()){
                        
                        Profesor profesor = new Profesor();
                        profesor.setIdProfesor(resultado.getInt("idprofesores"));
                        profesor.setNombre(resultado.getString("nombre"));
                        profesor.setCorreo(resultado.getString("correo"));
                        profesor.setEdad(resultado.getInt("edad"));
                        profesor.setFechaRegistro(resultado.getDate("fechaDeRegistro"));
                        profesor.setEstado(resultado.getBoolean("estado"));
                        profesores[aux] = profesor;
                        aux++;
                        
                    }
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            
            System.out.println("Error en obtenerSolicitudes.");
            
        }
        
        return gson.toJson(profesores);
        
    }
    
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
