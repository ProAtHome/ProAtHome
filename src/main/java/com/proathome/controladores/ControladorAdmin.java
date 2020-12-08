package com.proathome.controladores;

import com.google.gson.Gson;
import com.proathome.modelos.Admin;
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
    
    public String iniciarSesion(String usuario, String contrasena){
        
        boolean usuarioEncontrado = false;
        gson = new Gson();
        conectar = ConexionMySQL.connection();
        int idAdmin = 0;
        
        if(conectar != null){
            
            try{
                
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM admins WHERE usuario = ? AND contrasena = ?");
                consulta.setString(1 , usuario);
                consulta.setString(2 , contrasena);
                ResultSet resultado = consulta.executeQuery();
                if(resultado.next()){
                    
                    usuarioEncontrado = true;
                    idAdmin = resultado.getInt("idadmins");
                    
                }else{
                
                    usuarioEncontrado = false;
                    
                }
                
                
            }catch(SQLException ex){
                
                ex.printStackTrace();
                
            }
            
        }else{
            
            System.out.println("Error en iniciarSesion.");
            
        }
        
        if(usuarioEncontrado)
            return "{\"result\":true, \"sesion\":" + gson.toJson(datosAdmin(usuario, contrasena, idAdmin)) + "}";
        else
            return "{\"result\":false, \"sesion\":\"Usuario no encontrado.\"}";
        
    }//Fin método iniciarSesion.
    
}
