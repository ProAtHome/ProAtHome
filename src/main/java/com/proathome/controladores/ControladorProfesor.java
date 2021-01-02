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
                PreparedStatement nuevoTicket = conectar.prepareStatement("INSERT INTO tickets_ayuda (tipoUsuario, topico, descripcion, fechaCreacion, estatus, idUsuario) VALUES (?,?,?,?,?,?)");
                nuevoTicket.setInt(1, Integer.parseInt(jsonDatos.get("tipoUsuario").toString()));
                nuevoTicket.setString(2, jsonDatos.get("topico").toString());
                nuevoTicket.setString(3, jsonDatos.get("descripcion").toString());
                nuevoTicket.setDate(4, java.sql.Date.valueOf(jsonDatos.get("fechaCreacion").toString()));
                nuevoTicket.setInt(5, Integer.parseInt(jsonDatos.get("estatus").toString()));
                nuevoTicket.setInt(6, Integer.parseInt(jsonDatos.get("idUsuario").toString()));
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
                String query = "SELECT * FROM profesores WHERE BINARY correo = ? AND BINARY contrasena = ?";
                PreparedStatement obtenerDatos = conectar.prepareStatement(query);
                obtenerDatos.setString(1, correo);
                obtenerDatos.setString(2, contrasena);
                ResultSet resultado = obtenerDatos.executeQuery();

                if (resultado.next()) {

                    profesor.setIdProfesor(resultado.getInt("idprofesores"));
                    profesor.setNombre(resultado.getString("nombre"));
                    profesor.setFoto(resultado.getString("foto"));
                    profesor.setEstado(resultado.getString("estado"));
                    profesorRegistrado = true;
                } else {
                    profesorRegistrado = false;                   
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Error en la conexión iniciarSesion.");
        }

    }//Fin método iniciarSesion.

    public void datosActualizarPerfil(JSONObject datos) {

        profesor.setIdProfesor(Integer.parseInt(String.valueOf(datos.get("idProfesor"))));
        profesor.setNombre(String.valueOf(datos.get("nombre")));
        profesor.setCorreo(String.valueOf(datos.get("correo")));
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
                    jsonSesionesMatchProfesor.put("nombreEstudiante", resultado.getString("nombre"));
                    jsonSesionesMatchProfesor.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                    jsonSesionesMatchProfesor.put("descripcion", resultado.getString("descripcion"));
                    jsonSesionesMatchProfesor.put("correo", resultado.getString("correo"));
                    jsonSesionesMatchProfesor.put("latitud", resultado.getDouble("latitud"));
                    jsonSesionesMatchProfesor.put("longitud", resultado.getDouble("longitud"));
                    jsonSesionesMatchProfesor.put("foto", resultado.getString("foto"));
                    jsonSesionesMatchProfesor.put("lugar", resultado.getString("lugar"));
                    jsonSesionesMatchProfesor.put("tiempo", resultado.getInt("tiempo"));
                    jsonSesionesMatchProfesor.put("idSeccion", resultado.getInt("idSeccion"));
                    jsonSesionesMatchProfesor.put("idNivel", resultado.getInt("idNivel"));
                    jsonSesionesMatchProfesor.put("idBloque", resultado.getInt("idBloque"));
                    jsonSesionesMatchProfesor.put("tipoClase", resultado.getString("tipoClase"));
                    jsonSesionesMatchProfesor.put("extras", resultado.getString("extras"));
                    jsonSesionesMatchProfesor.put("horario", resultado.getString("horario")); 
                    jsonArrayMatch.add(jsonSesionesMatchProfesor);
                    
                }
                
                
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            
            System.out.println("Error en sesionesMatchProfesor.");
            
        }
        
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
                    jsonMatch.put("tiempo", resultado.getInt("tiempo"));
                    jsonMatch.put("idSeccion", resultado.getInt("idSeccion"));
                    jsonMatch.put("idNivel", resultado.getInt("idNivel"));
                    jsonMatch.put("idBloque", resultado.getInt("idBloque"));
                    jsonMatch.put("tipoClase", resultado.getString("tipoClase"));
                    jsonMatch.put("extras", resultado.getString("extras"));
                    jsonMatch.put("horario", resultado.getString("horario"));     
                }
            
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            System.out.println("Error en informacionSesionMatch."); 
        }
        
        return jsonMatch;
        
    }//Fin método informacionSesionMatch.
    
    public JSONArray obtenerSesionesMovil(){
        
        conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            
            try{
                
                PreparedStatement sesiones = conectar.prepareStatement("SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.clientes_idclientes = clientes.idclientes");
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

    public void actualizarDatosPerfil() {

        conectar = ConexionMySQL.connection();

        if (conectar != null) {

            try {

                String query = "UPDATE profesores SET nombre = ?, correo = ?, descripcion = ? WHERE idprofesores = ?";
                PreparedStatement actualizar = conectar.prepareStatement(query);
                actualizar.setString(1, profesor.getNombre());
                actualizar.setString(2, profesor.getCorreo());
                actualizar.setString(3, profesor.getDescripcion());
                actualizar.setInt(4, profesor.getIdProfesor());
                actualizar.executeUpdate();

                

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión en actualizarDatosPerfil.");

        }

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
                    profesor.setNombre(resultado.getString("nombre"));
                    profesor.setCorreo(resultado.getString("correo"));
                    profesor.setContrasena(resultado.getString("contrasena"));
                    profesor.setEdad(resultado.getInt("edad"));
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
        profesor.setCorreo(String.valueOf(jsonProfesor.get("correo")));
        profesor.setContrasena(String.valueOf(jsonProfesor.get("contrasena")));

        //Formateo de fechas a tipo SQL Date.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dateFechaNacimiento = (java.sql.Date.valueOf(String.valueOf(jsonProfesor.get("fechaNacimiento"))));
        java.sql.Date dateFechaRegistro = (java.sql.Date.valueOf(String.valueOf(jsonProfesor.get("fechaRegistro"))));

        profesor.setFechaNacimiento(dateFechaNacimiento);
        profesor.setFechaRegistro(dateFechaRegistro);
        profesor.setEdad(Integer.parseInt(String.valueOf(jsonProfesor.get("edad"))));

    }

    public ArrayList<String> obtenerTodos() {

        ArrayList<String> list = new ArrayList<>();

        conectar = ConexionMySQL.connection();

        if (conectar != null) {

            try {

                Statement estado = conectar.createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM profesores");

                while (resultado.next()) {
                    String nombre = resultado.getString("nombre");
                    String correo = resultado.getString("correo");
                    int id = resultado.getInt("idprofesores");

                    String stringJSON = " \"id" + id + "\" :{\"nombre\" : \"" + nombre + "\", \"correo\" : \"" + correo + "\"}";
                    list.add(stringJSON);

                }

                

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión obtenerTodos");

        }

        return list;

    }//Fin método obtenerTodos.

    public void guardarProfesor() {

        conectar = ConexionMySQL.connection();

        if (conectar != null) {

            try {

                String query = "INSERT INTO profesores (nombre, correo, contrasena, edad, fechaNacimiento, fechaDeRegistro) VALUES (?,?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setString(1, profesor.getNombre());
                agregarDatos.setString(2, profesor.getCorreo());
                agregarDatos.setString(3, profesor.getContrasena());
                agregarDatos.setInt(4, profesor.getEdad());
                agregarDatos.setDate(5, profesor.getFechaNacimiento());
                agregarDatos.setDate(6, profesor.getFechaRegistro());
                agregarDatos.execute();

                

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión agregarProfesor");

        }

    }//Fin método guardarProfesor.

    public CuentaBancaria obtenerCuentaBancaria(int idProfesor) {

        profesor.cuenta = new CuentaBancaria();
        conectar = ConexionMySQL.connection();

        if (conectar != null) {

            try {

                Statement estado = conectar.createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM datosbancariosprofesores WHERE profesores_idprofesores = " + idProfesor);

                if (resultado.next()) {

                    profesor.cuenta.setNombreTitular(resultado.getString("banco"));
                    profesor.cuenta.setTarjeta(resultado.getString("direccionFacturacion"));
                    profesor.cuenta.setMes(resultado.getString("tipoDePago"));
                    profesor.cuenta.setAno(resultado.getString("numeroCuenta"));

                } else {

                    return null;

                }

                

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión obtenerCuentaBancaria.");

        }

        return profesor.cuenta;

    }//Fin método obtenerCuentaBancaria.

    public void nuevaCuentaBancaria(JSONObject jsonCuentaBancaria) {

        profesor.cuenta = new CuentaBancaria();
        profesor.cuenta.setNombreTitular(String.valueOf(jsonCuentaBancaria.get("banco")));
        profesor.cuenta.setTarjeta(String.valueOf(jsonCuentaBancaria.get("direccionFacturacion")));
        profesor.cuenta.setMes(String.valueOf(jsonCuentaBancaria.get("tipoDePago")));
        profesor.cuenta.setAno(String.valueOf(jsonCuentaBancaria.get("numeroCuenta")));

    }//Fin método nuevaCuentaBancaria.

    public void guardarCuentaBancaria(int idProfesor) {

        conectar = ConexionMySQL.connection();

        if (conectar != null) {

            try {

                String query = "INSERT INTO datosbancariosprofesores (profesores_idprofesores, tipoDePago, banco, numeroCuenta, direccionFacturacion) VALUES (?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setInt(1, idProfesor);
                agregarDatos.setString(2, profesor.cuenta.getNombreTitular());
                agregarDatos.setString(3, profesor.cuenta.getTarjeta());
                agregarDatos.setString(4, profesor.cuenta.getMes());
                agregarDatos.setString(5, profesor.cuenta.getAno());
                agregarDatos.execute();

                

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión guardarCuentaBancaria.");

        }

    }//Fin método guardarCuentaBancaria.

    public void actualizarCuentaBancaria(int idProfesor) {

        conectar = ConexionMySQL.connection();

        if (conectar != null) {

            try {
                
                PreparedStatement consultaRegistro = conectar.prepareStatement("SELECT * FROM datosbancariosprofesores WHERE profesores_idprofesores = ?");
                consultaRegistro.setInt(1 , idProfesor);
                ResultSet resultado = consultaRegistro.executeQuery();
                
                if(resultado.next()){
                    
                    String query = "UPDATE datosbancariosprofesores SET tipoDePago = ?, banco = ?, numeroCuenta = ?, direccionFacturacion = ? WHERE profesores_idprofesores = ?";
                    PreparedStatement agregarDatos = conectar.prepareStatement(query);
                    agregarDatos.setString(1, profesor.cuenta.getNombreTitular());
                    agregarDatos.setString(2, profesor.cuenta.getTarjeta());
                    agregarDatos.setString(3, profesor.cuenta.getMes());
                    agregarDatos.setString(4, profesor.cuenta.getAno());
                    agregarDatos.setInt(5, idProfesor);
                    agregarDatos.executeUpdate();

                }else{
                    
                    PreparedStatement insert = conectar.prepareStatement("INSERT INTO datosbancariosprofesores (profesores_idprofesores, tipoDePago, banco, numeroCuenta, direccionFacturacion) VALUES (?,?,?,?,?)");
                    insert.setInt(1 , idProfesor);
                    insert.setString(2, profesor.cuenta.getNombreTitular());
                    insert.setString(3, profesor.cuenta.getTarjeta());
                    insert.setString(4, profesor.cuenta.getMes());
                    insert.setString(5, profesor.cuenta.getAno());
                    insert.execute();
                    
                }

                

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión guardarCuentaBancaria.");

        }

    }//Fin método guardarCuentaBancaria.

}
