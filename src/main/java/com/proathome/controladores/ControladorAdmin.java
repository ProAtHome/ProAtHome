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
    
    private Connection conectar;
    public static final int ESTUDIANTE = 1;
    public static final int PROFESOR = 2;
    
    public void latidoSQL(){
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement latido = conectar.prepareStatement("SELECT * FROM clientes");
                ResultSet resultado = latido.executeQuery();
                while(resultado.next()){
                    System.out.println("Latido SQL");
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }
    
    public JSONObject historialReportes(String tipoUsuario, int idUsuario){
        JSONObject respuesta = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        System.out.println(idUsuario);
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
                consulta.setInt(1, idUsuario);
                consulta.setString(2, tipoUsuario);
                ResultSet resultado = consulta.executeQuery();
                JSONArray reportes = new JSONArray();
                int num = 1;
                while(resultado.next()){
                    JSONObject mensaje = new JSONObject();
                    mensaje.put("descripcion", resultado.getString("descripcion"));
                    mensaje.put("num", num);
                    num++;
                    reportes.add(mensaje);
                }
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", reportes);
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
    
    public JSONObject bloquearPerfil(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                String query = null;
                if(jsonDatos.get("perfil").equals("ESTUDIANTE"))
                    query = "UPDATE clientes SET estado = ? WHERE idclientes = ?";
                else if(jsonDatos.get("perfil").equals("PROFESOR"))
                    query = "UPDATE profesores SET estado = ? WHERE idprofesores = ?";

                PreparedStatement bloquear = conectar.prepareStatement(query);
                bloquear.setString(1, "BLOQUEADO");
                bloquear.setInt(2, Integer.parseInt(jsonDatos.get("idPerfil").toString()));
                bloquear.execute();
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Perfil bloqueado.");
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
    
    public void borrarReportes(JSONObject jsonDatos, Connection conectar){
        try{
            PreparedStatement eliminar = conectar.prepareStatement("DELETE FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
            eliminar.setInt(1, Integer.parseInt(jsonDatos.get("idUsuario").toString()));
            if(Integer.parseInt(jsonDatos.get("tipoPerfil").toString()) == Constantes.TIPO_USUARIO_ESTUDIANTE)
                eliminar.setString(2, "ESTUDIANTE");
            else if(Integer.parseInt(jsonDatos.get("tipoPerfil").toString()) == Constantes.TIPO_USUARIO_PROFESOR)
                eliminar.setString(2, "PROFESOR");
            
            eliminar.execute();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public JSONObject desbloquearPerfil(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement desbloquear = null;
                if(Integer.parseInt(jsonDatos.get("tipoPerfil").toString()) == Constantes.TIPO_USUARIO_ESTUDIANTE)
                    desbloquear = conectar.prepareStatement("UPDATE clientes SET estado = ? WHERE idclientes = ?");
                else if(Integer.parseInt(jsonDatos.get("tipoPerfil").toString()) == Constantes.TIPO_USUARIO_PROFESOR)
                    desbloquear = conectar.prepareStatement("UPDATE profesores SET estado = ? WHERE idprofesores = ?");
                
                desbloquear.setString(1, "ACTIVO");
                desbloquear.setInt(2, Integer.parseInt(jsonDatos.get("idUsuario").toString()));
                desbloquear.execute();
                
                borrarReportes(jsonDatos, conectar);
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Perfil desbloqueado correctamente.");
            }catch(SQLException ex){
                respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
                ex.printStackTrace();
            }
            
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
    }
    
    public void bloqueoPerfilEstudiante(int idEstudiante, Connection conectar){
        try{
            PreparedStatement consulta = conectar.prepareStatement("SELECT COUNT(*) AS num FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
            consulta.setInt(1, idEstudiante);
            consulta.setString(2, "ESTUDIANTE");
            ResultSet resultado = consulta.executeQuery();
            
            if(resultado.next()){
                int numReportes = resultado.getInt("num");
                if(numReportes >= 3){
                    //Bloqueado
                    PreparedStatement bloquear = conectar.prepareStatement("UPDATE clientes SET estado = ? WHERE idclientes = ?");
                    bloquear.setString(1, "BLOQUEADO");
                    bloquear.setInt(2, idEstudiante);
                    bloquear.execute();
                }
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public void bloqueoPerfilProfesor(int idProfesor, Connection conectar){
        try{
            PreparedStatement consulta = conectar.prepareStatement("SELECT COUNT(*) AS num FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
            consulta.setInt(1, idProfesor);
            consulta.setString(2, "PROFESOR");
            ResultSet resultado = consulta.executeQuery();
            
            if(resultado.next()){
                int numReportes = resultado.getInt("num");
                if(numReportes >= 3){
                    //Bloqueado
                    PreparedStatement bloquear = conectar.prepareStatement("UPDATE profesores SET estado = ? WHERE idprofesores = ?");
                    bloquear.setString(1, "BLOQUEADO");
                    bloquear.setInt(2, idProfesor);
                    bloquear.execute();
                }
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public void guardarHistorialReportes(JSONObject jsonDatos, Connection conectar){
        try{
            PreparedStatement guardar = conectar.prepareStatement("INSERT INTO historial_reportes (descripcion, idUsuario, tipoUsuario, fecha, tickets_ayuda_idtickets_ayuda) VALUES (?,?,?,?,?)");
            guardar.setString(1, jsonDatos.get("descripcionReporte").toString());
            guardar.setInt(2, Integer.parseInt(jsonDatos.get("idUsuarioReportado").toString()));
            guardar.setString(3, jsonDatos.get("tipoUsuarioReportado").toString());
            guardar.setDate(4, java.sql.Date.valueOf("2020-02-21"));
            guardar.setInt(5, Integer.parseInt(jsonDatos.get("idTicket").toString()));
            guardar.execute();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public JSONObject crearReporte(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
  
        if(conectar != null){
            try{
                PreparedStatement guardar = conectar.prepareStatement("INSERT INTO reportes (descripcion, idUsuario, tipoUsuario, tickets_ayuda_idtickets_ayuda) VALUES (?,?,?,?)");
                guardar.setString(1, jsonDatos.get("descripcionReporte").toString());
                guardar.setInt(2, Integer.parseInt(jsonDatos.get("idUsuarioReportado").toString()));
                guardar.setString(3, jsonDatos.get("tipoUsuarioReportado").toString());
                guardar.setInt(4, Integer.parseInt(jsonDatos.get("idTicket").toString()));
                guardar.execute();
                
                guardarHistorialReportes(jsonDatos, conectar);
                
                //Validar bloqueo de perfil
                if(jsonDatos.get("tipoUsuarioReportado").toString().equals("ESTUDIANTE"))
                    bloqueoPerfilEstudiante(Integer.parseInt(jsonDatos.get("idUsuarioReportado").toString()), conectar);
                else if(jsonDatos.get("tipoUsuarioReportado").toString().equals("PROFESOR"))
                    bloqueoPerfilProfesor(Integer.parseInt(jsonDatos.get("idUsuarioReportado").toString()), conectar);
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Reporte creado correctamente,");
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD,");
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD,");
        }      
        
        return respuesta;
    }
    
    public JSONObject getEstudiante(int idEstudiante, Connection conectar){
        JSONObject estudiante = new JSONObject();
        try{
            PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM clientes WHERE idclientes = ?");
            consulta.setInt(1, idEstudiante);
            ResultSet resultado = consulta.executeQuery();
            
            if(resultado.next()){
                estudiante.put("idEstudiante", resultado.getInt("idclientes"));
                estudiante.put("nombre", resultado.getString("nombre") + resultado.getString("apellidoPaterno") + resultado.getString("apellidoMaterno"));
                estudiante.put("correo", resultado.getString("correo"));
            }else{
                estudiante.put("idEstudiante", "Error al obtener estudiante.");
                estudiante.put("nombre", "Error al obtener estudiante.");
                estudiante.put("correo", "Error al obtener estudiante.");
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
        return estudiante;
    }
    
    public JSONObject getProfesor(int idProfesor, Connection conectar){
        JSONObject profesor = new JSONObject();
        try{
            PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM profesores WHERE idprofesores = ?");
            consulta.setInt(1, idProfesor);
            ResultSet resultado = consulta.executeQuery();
            
            if(resultado.next()){
                profesor.put("nombre", resultado.getString("nombre"));
                profesor.put("correo", resultado.getString("correo"));
            }else{
                profesor.put("nombre", "Error al obtener nombre de profesor.");
                profesor.put("correo", "Error al obtener correo de profesor.");
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            profesor.put("nombre", "Error al obtener nombre de profesor.");
            profesor.put("correo", "Error al obtener correo de profesor.");
        }
        
        return profesor;
    }
    
    public JSONObject verClase(int idSesion){
        JSONObject respuesta = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ?");
                consulta.setInt(1, idSesion);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    JSONObject datosSesion = new JSONObject();
                    JSONObject profesor = getProfesor(resultado.getInt("profesores_idprofesores"), conectar);
                    JSONObject estudiante = getEstudiante(resultado.getInt("clientes_idclientes"), conectar);
                    datosSesion.put("profesor", resultado.getInt("profesores_idprofesores") == 0 ? "Sin profesor asignado" : profesor.get("nombre").toString());
                    datosSesion.put("correo",  resultado.getInt("profesores_idprofesores") == 0 ? "Sin profesor asignado" : profesor.get("correo").toString());
                    datosSesion.put("idProfesor", resultado.getInt("profesores_idprofesores"));
                    datosSesion.put("idEstudiante", estudiante.get("idEstudiante"));
                    datosSesion.put("estudiante", estudiante.get("nombre"));
                    datosSesion.put("correoEstudiante", estudiante.get("correo"));
                    datosSesion.put("horario", resultado.getString("horario"));
                    datosSesion.put("lugar", resultado.getString("lugar"));
                    datosSesion.put("tiempo", resultado.getInt("tiempo"));
                    datosSesion.put("extras", resultado.getString("extras"));
                    datosSesion.put("tipoClase", resultado.getString("tipoClase"));
                    datosSesion.put("latitud", resultado.getDouble("latitud"));
                    datosSesion.put("longitud", resultado.getDouble("longitud"));
                    datosSesion.put("idSeccion", resultado.getInt("idSeccion"));
                    datosSesion.put("idNivel", resultado.getInt("idNivel"));
                    datosSesion.put("idBloque", resultado.getInt("idBloque"));
                    datosSesion.put("fecha", resultado.getDate("fecha"));
                    datosSesion.put("estatus", resultado.getInt("estatus"));
                    datosSesion.put("progreso", resultado.getInt("progreso"));
                    datosSesion.put("progresoSegundos", resultado.getInt("progresoSegundos"));
                    datosSesion.put("finalizado", resultado.getBoolean("finalizado"));
                    datosSesion.put("TE", resultado.getBoolean("TE"));
                    datosSesion.put("progresoTE", resultado.getInt("progresoTE"));
                    datosSesion.put("progresoSegundosTE", resultado.getInt("progresoSegundosTE"));
                    datosSesion.put("sumar", resultado.getBoolean("sumar"));
                    datosSesion.put("tipoPlan", resultado.getString("tipoPlan"));
                    
                    //Consultar pago
                    PreparedStatement consultaPago = conectar.prepareStatement("SELECT * FROM pagos WHERE idpagos = ?");
                    consultaPago.setInt(1, resultado.getInt("pagos_idpagos"));
                    ResultSet resultadoPago = consultaPago.executeQuery();
                    
                    if(resultadoPago.next()){
                        JSONObject pago = new JSONObject();
                        pago.put("token", resultadoPago.getString("token"));
                        pago.put("costoClase", resultadoPago.getDouble("costoClase"));
                        pago.put("costoTE", resultadoPago.getDouble("costoTE"));
                        pago.put("estatusPago", resultadoPago.getString("estatusPago"));
                        datosSesion.put("pago", true);
                        datosSesion.put("datosPago", pago);
                    }else
                        datosSesion.put("pago", false);
                    
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", datosSesion);
                }else{
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", "Error en la conexión, sin resultados.");
                }
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
    
    public JSONObject activarEstudiante(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if(conectar != null){
            try{
                System.out.println(jsonDatos);
                //Cambiar el estatus activo y foto
                PreparedStatement actualizar = conectar.prepareStatement("UPDATE clientes SET foto = ?, estado = ? WHERE idclientes = ?");
                actualizar.setString(1, jsonDatos.get("foto").toString());
                actualizar.setString(2, "ACTIVO");
                actualizar.setInt(3, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
                actualizar.execute();
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Perfil activado correctamente.");
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Ocurrió un error al consultar en BD.");
            }
            
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Ocurrió un error en el servidor.");
        }
        
        return respuesta;
    }
    
    public JSONObject getPerfilProfesor(int idProfesor){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        JSONArray sesionesArray = new JSONArray();
        
        if(conectar != null){
            //Obtener perfil
            try{
               PreparedStatement perfil = conectar.prepareStatement("SELECT * FROM profesores WHERE profesores.idprofesores = ?");
               perfil.setInt(1, idProfesor);
               ResultSet resPerfil = perfil.executeQuery();
               
               if(resPerfil.next()){
                   JSONObject jsonPerfil = new JSONObject();
                   jsonPerfil.put("nombre", resPerfil.getString("nombre"));
                   jsonPerfil.put("correo", resPerfil.getString("correo"));
                   jsonPerfil.put("rango", resPerfil.getString("rangoClase"));
                   jsonPerfil.put("fechaNacimiento", resPerfil.getDate("fechaNacimiento"));
                   jsonPerfil.put("estatus", resPerfil.getString("estado"));
                   respuesta.put("perfil", jsonPerfil);
                   
                   //Historial Sesiones.
                  PreparedStatement sesiones = conectar.prepareStatement("SELECT * FROM sesiones WHERE profesores_idprofesores = ?");
                  sesiones.setInt(1, idProfesor);
                  ResultSet resSesiones = sesiones.executeQuery();
                  
                  while(resSesiones.next()){
                      JSONObject sesionJSON = new JSONObject();
                      sesionJSON.put("idSesion", resSesiones.getInt("idsesiones"));
                      sesionJSON.put("idEstudiante", resSesiones.getInt("clientes_idclientes"));
                      sesionJSON.put("horario", resSesiones.getString("horario"));
                      sesionJSON.put("lugar", resSesiones.getString("lugar"));
                      sesionJSON.put("tiempo", resSesiones.getInt("tiempo"));
                      sesionJSON.put("extras", resSesiones.getString("extras"));
                      sesionJSON.put("tipoClase", resSesiones.getString("tipoClase"));
                      sesionJSON.put("idSeccion", resSesiones.getInt("idSeccion"));
                      sesionJSON.put("idNivel", resSesiones.getInt("idNivel"));
                      sesionJSON.put("idBloque", resSesiones.getInt("idBloque"));
                      sesionesArray.add(sesionJSON);
                  }
                  
                  respuesta.put("sesiones", sesionesArray);
                  respuesta.put("respuesta", true);
               }else{
                    respuesta.put("mensaje", "Error en la cosnsulta a BD.");
                    respuesta.put("respuesta", false);
               }
            }catch(SQLException ex){
                respuesta.put("mensaje", "Error en la coenxión a BD.");
                respuesta.put("respuesta", false);
                ex.printStackTrace();
            }
            
        }else{
            respuesta.put("mensaje", "Error en la coenxión a BD.");
            respuesta.put("respuesta", false);
        }
        
        return respuesta;
    }
    
    public JSONObject getPerfilEstudiante(int idEstudiante){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        JSONArray sesionesArray = new JSONArray();
        JSONArray planesArray = new JSONArray();
        
        if(conectar != null){
            //Obtener perfil
            try{
               PreparedStatement perfil = conectar.prepareStatement("SELECT * FROM clientes INNER JOIN planes WHERE clientes.idclientes = ? AND planes.clientes_idclientes = clientes.idclientes");
               perfil.setInt(1, idEstudiante);
               ResultSet resPerfil = perfil.executeQuery();
               
               if(resPerfil.next()){
                   JSONObject jsonPerfil = new JSONObject();
                   jsonPerfil.put("nombre", resPerfil.getString("nombre"));
                   jsonPerfil.put("correo", resPerfil.getString("correo"));
                   jsonPerfil.put("estado", resPerfil.getString("estado"));
                   jsonPerfil.put("tipoPlan", resPerfil.getString("tipoPlan"));
                   jsonPerfil.put("monedero", resPerfil.getInt("monedero"));
                   jsonPerfil.put("fechaNacimiento", resPerfil.getDate("fechaNacimiento"));
                   respuesta.put("perfil", jsonPerfil);
                   
                   //Historial Sesiones.
                  PreparedStatement sesiones = conectar.prepareStatement("SELECT * FROM sesiones WHERE clientes_idclientes = ?");
                  sesiones.setInt(1, idEstudiante);
                  ResultSet resSesiones = sesiones.executeQuery();
                  
                  while(resSesiones.next()){
                      JSONObject sesionJSON = new JSONObject();
                      sesionJSON.put("idSesion", resSesiones.getInt("idsesiones"));
                      sesionJSON.put("idProfesor", resSesiones.getInt("profesores_idprofesores"));
                      sesionJSON.put("horario", resSesiones.getString("horario"));
                      sesionJSON.put("lugar", resSesiones.getString("lugar"));
                      sesionJSON.put("tiempo", resSesiones.getInt("tiempo"));
                      sesionJSON.put("extras", resSesiones.getString("extras"));
                      sesionJSON.put("tipoClase", resSesiones.getString("tipoClase"));
                      sesionJSON.put("idSeccion", resSesiones.getInt("idSeccion"));
                      sesionJSON.put("idNivel", resSesiones.getInt("idNivel"));
                      sesionJSON.put("idBloque", resSesiones.getInt("idBloque"));
                      sesionesArray.add(sesionJSON);
                  }
                  
                  respuesta.put("sesiones", sesionesArray);
                  
                  //Historial de planes
                  PreparedStatement planes = conectar.prepareStatement("SELECT * FROM historialplanes WHERE clientes_idclientes = ?");
                  planes.setInt(1, idEstudiante);
                  ResultSet resPlanes = planes.executeQuery();
                  
                  while(resPlanes.next()){
                      JSONObject planesJSON = new JSONObject();
                      planesJSON.put("idPlan", resPlanes.getInt("idHistorialPlanes"));
                      planesJSON.put("tipoPlan", resPlanes.getString("tipoPlan"));
                      planesJSON.put("fechaInicio", resPlanes.getDate("fechaInicio"));
                      planesJSON.put("fechFin", resPlanes.getDate("fechaFin"));
                      planesJSON.put("monedero", resPlanes.getInt("monedero"));
                      planesArray.add(planesJSON);
                  }
                  
                  respuesta.put("planes", planesArray);
                  respuesta.put("respuesta", true);
               }else{
                    respuesta.put("mensaje", "Error en la cosnsulta a BD.");
                    respuesta.put("respuesta", false);
               }
            }catch(SQLException ex){
                respuesta.put("mensaje", "Error en la coenxión a BD.");
                respuesta.put("respuesta", false);
                ex.printStackTrace();
            }
            
        }else{
            respuesta.put("mensaje", "Error en la coenxión a BD.");
            respuesta.put("respuesta", false);
        }
        
        return respuesta;
    }
    
    public JSONObject getProfesoresRegistrados(){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        JSONArray profesores = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement registros = conectar.prepareStatement("SELECT * FROM profesores");
                ResultSet resultado = registros.executeQuery();
                while(resultado.next()){
                    JSONObject profesor = new JSONObject();
                    profesor.put("idProfesor", resultado.getInt("idprofesores"));
                    profesor.put("nombre", resultado.getString("nombre"));
                    profesor.put("correo", resultado.getString("correo"));
                    profesor.put("estado", resultado.getString("estado"));
                    profesores.add(profesor);
                }
                
                respuesta.put("mensaje", profesores);
                respuesta.put("respuesta", true);
            }catch(SQLException ex){
                respuesta.put("mensaje", "Error en la conexión a BD.");
                respuesta.put("respuesta", true);
                ex.printStackTrace();
            }
        }else{
            respuesta.put("mensaje", "Error en la conexión a BD.");
            respuesta.put("respuesta", true);
        }
        
        return respuesta;
    }
    
    public JSONObject getEstudiantesRegistrados(){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        JSONArray estudiantes = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement registros = conectar.prepareStatement("SELECT * FROM clientes INNER JOIN planes WHERE planes.clientes_idclientes = clientes.idclientes");
                ResultSet resultado = registros.executeQuery();
                while(resultado.next()){
                    JSONObject estudiante = new JSONObject();
                    estudiante.put("idEstudiante", resultado.getInt("idclientes"));
                    estudiante.put("nombre", resultado.getString("nombre"));
                    estudiante.put("correo", resultado.getString("correo"));
                    estudiante.put("tipoPlan", resultado.getString("tipoPlan"));
                    estudiante.put("monedero", resultado.getInt("monedero"));
                    estudiantes.add(estudiante);
                }
                
                respuesta.put("mensaje", estudiantes);
            respuesta.put("respuesta", true);
            }catch(SQLException ex){
                respuesta.put("mensaje", "Error en la conexión a BD.");
                respuesta.put("respuesta", true);
                ex.printStackTrace();
            }
        }else{
            respuesta.put("mensaje", "Error en la conexión a BD.");
            respuesta.put("respuesta", true);
        }
        
        return respuesta;
    }
    
    public JSONObject actualizarOperador(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement actualizar = conectar.prepareStatement("UPDATE operadores SET nombres = ?, apellidoPaterno = ?, apellidoMaterno = ?, usuario = ?, contrasena = ?"
                                                                            + " WHERE idoperadores = ?");
                actualizar.setString(1, jsonDatos.get("nombre").toString());
                actualizar.setString(2, jsonDatos.get("paterno").toString());
                actualizar.setString(3, jsonDatos.get("materno").toString());
                actualizar.setString(4, jsonDatos.get("usuario").toString());
                actualizar.setString(5, jsonDatos.get("contrasena").toString());
                actualizar.setInt(6, Integer.parseInt(jsonDatos.get("idOperador").toString()));
                actualizar.execute();
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Operador actualizado correctamente.");
            }catch(SQLException ex){
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD.");
                ex.printStackTrace();
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
    }
    
    public JSONObject eliminarOperador(int idOperador){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement eliminar = conectar.prepareStatement("DELETE FROM operadores WHERE idoperadores = ?");
                eliminar.setInt(1, idOperador);
                eliminar.execute();
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", "Operador eliminado exitosamente.");
            }catch(SQLException ex){
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error, revisa si el operador está asigando a un Ewstudiante/Profesor.");
                ex.printStackTrace();
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
    }
    
    public JSONObject getOperadoresUsuario(){
        Connection conectar = ConexionMySQL.connection();
        JSONArray respuesta = new JSONArray();
        JSONObject mensaje = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement registros = conectar.prepareStatement("SELECT * FROM operadores WHERE rango = ?");
                registros.setInt(1, 3);
                ResultSet resultado = registros.executeQuery();
                
                while(resultado.next()){
                    JSONObject operador = new JSONObject();
                    operador.put("nombre", resultado.getString("nombres"));
                    operador.put("paterno", resultado.getString("apellidoPaterno"));
                    operador.put("materno", resultado.getString("apellidoMaterno"));
                    operador.put("usuario", resultado.getString("usuario"));
                    operador.put("contrasena", resultado.getString("contrasena"));
                    operador.put("idOperador", resultado.getInt("idoperadores"));
                    respuesta.add(operador);
                }
                
                mensaje.put("respuesta", true);
                mensaje.put("mensaje", respuesta);
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            mensaje.put("respuesta", false);
            mensaje.put("mensaje", "Ocurrió un problema en la conexión a BD.");
        }
        
        return mensaje;
    }
    
    public JSONObject getOperadoresSoporte(){
        Connection conectar = ConexionMySQL.connection();
        JSONArray respuesta = new JSONArray();
        JSONObject mensaje = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement registros = conectar.prepareStatement("SELECT * FROM operadores WHERE rango = ?");
                registros.setInt(1, 2);
                ResultSet resultado = registros.executeQuery();
                
                while(resultado.next()){
                    JSONObject operador = new JSONObject();
                    operador.put("nombre", resultado.getString("nombres"));
                    operador.put("paterno", resultado.getString("apellidoPaterno"));
                    operador.put("materno", resultado.getString("apellidoMaterno"));
                    operador.put("usuario", resultado.getString("usuario"));
                    operador.put("contrasena", resultado.getString("contrasena"));
                    operador.put("idOperador", resultado.getInt("idoperadores"));
                    respuesta.add(operador);
                }
                
                mensaje.put("respuesta", true);
                mensaje.put("mensaje", respuesta);
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            mensaje.put("respuesta", false);
            mensaje.put("mensaje", "Ocurrió un problema en la conexión a BD.");
        }
        
        return mensaje;
    }
    
    public JSONObject guardarOperador(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement guardar = conectar.prepareStatement("INSERT INTO operadores (nombres, apellidoPaterno, apellidoMaterno, rango, usuario, contrasena)"
                                                                        + " VALUES(?,?,?,?,?,?)");
                guardar.setString(1, jsonDatos.get("nombre").toString());
                guardar.setString(2, jsonDatos.get("paterno").toString());
                guardar.setString(3, jsonDatos.get("materno").toString());
                guardar.setInt(4, Integer.parseInt(jsonDatos.get("rango").toString()));
                guardar.setString(5, jsonDatos.get("usuario").toString());
                guardar.setString(6, jsonDatos.get("contrasena").toString());
                guardar.execute();
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Operador registrado correctamente.");
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión con BD.");
        }
        
        return respuesta;
    }
    
    public JSONObject reagendarCita(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if(conectar != null){
            try{
                
                //Agendar nuevamente
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
                        //Eliminar previa cita.
                        PreparedStatement eliminar = conectar.prepareStatement("DELETE FROM citas WHERE profesores_idprofesores = ?");
                        eliminar.setInt(1, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                        eliminar.execute();
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
                        estatus.setString(1, "CITA");
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
            System.out.println("Error en reagendarCita.");
        }
        
        return respuesta;
    }
    
    public void activarPerfil(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement activar = conectar.prepareStatement("UPDATE profesores SET foto = ?, estado = ?, rangoClase = ? WHERE idprofesores = ?");
                activar.setString(1, jsonDatos.get("foto").toString());
                activar.setString(2, "ACTIVO");
                activar.setInt(3, Integer.parseInt(jsonDatos.get("rango").toString()));
                activar.setInt(4, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                activar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en activarPerfil.");
        }
    }
    
    public JSONArray citasEnFecha(String date, int idOperador){
        Connection conectar = ConexionMySQL.connection();
        JSONArray fecha = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement fechas = conectar.prepareStatement("SELECT * FROM citas INNER JOIN profesores WHERE citas.fechaAcordada = ? AND citas.operadores_idoperadores = ? AND profesores.idprofesores = citas.profesores_idprofesores");
                fechas.setDate(1, java.sql.Date.valueOf(date));
                fechas.setInt(2, idOperador);
                ResultSet resultado = fechas.executeQuery();
                
                while(resultado.next()){
                    JSONObject datos = new JSONObject();
                    datos.put("fechaAcordada", resultado.getDate("fechaAcordada"));
                    datos.put("horarioAcordado", resultado.getString("horarioAcordado"));
                    datos.put("profesor", resultado.getString("nombre"));
                    datos.put("correo", resultado.getString("correo"));
                    datos.put("tipoCita", resultado.getString("tipoCita"));
                    datos.put("datosAdicionales", resultado.getString("datosAdicionales"));
                    datos.put("idProfesor", resultado.getInt("idprofesores"));
                    
                    PreparedStatement consultaFoto = conectar.prepareStatement("SELECT foto FROM documentacionprofesor WHERE profesores_idprofesores = ?");
                    consultaFoto.setInt(1, resultado.getInt("idprofesores"));
                    ResultSet resultadoFoto = consultaFoto.executeQuery();
                    
                    if(resultadoFoto.next())
                        datos.put("foto", resultadoFoto.getString("foto"));
                    fecha.add(datos);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en citasEnFecha.");
        }
        
        return fecha;
    }
    
    public JSONObject agendaActual(JSONObject jsonDatos){
        
        JSONObject agenda = new JSONObject();
        int idOperador = Integer.parseInt(jsonDatos.get("idOperador").toString());
        //Hoy
        agenda.put("hoy", citasEnFecha(jsonDatos.get("hoy").toString(), idOperador));
        agenda.put("dia2", citasEnFecha(jsonDatos.get("dia2").toString(), idOperador));
        agenda.put("dia3", citasEnFecha(jsonDatos.get("dia3").toString(), idOperador));
        agenda.put("dia4", citasEnFecha(jsonDatos.get("dia4").toString(), idOperador));
        agenda.put("dia5", citasEnFecha(jsonDatos.get("dia5").toString(), idOperador));
        agenda.put("dia6", citasEnFecha(jsonDatos.get("dia6").toString(), idOperador));
        agenda.put("dia7", citasEnFecha(jsonDatos.get("dia7").toString(), idOperador));
        /*
        if(conectar != null){
            try{
                PreparedStatement fechas = conectar.prepareStatement("SELECT * FROM citas INNER JOIN profesores WHERE citas.fechaAcordada >= ? AND citas.operadores_idoperadores = ? AND profesores.idprofesores = citas.profesores_idprofesores");
                fechas.setDate(1, java.sql.Date.valueOf(hoy));
                fechas.setInt(2, idOperador);
                ResultSet resultado = fechas.executeQuery();
                
                while(resultado.next()){
                    JSONObject datos = new JSONObject();
                    datos.put("fechaAcordada", resultado.getDate("fechaAcordada"));
                    datos.put("horarioAcordado", resultado.getString("horarioAcordado"));
                    datos.put("profesor", resultado.getString("nombre"));
                    datos.put("correo", resultado.getString("correo"));
                    datos.put("tipoCita", resultado.getString("tipoCita"));
                    datos.put("datosAdicionales", resultado.getString("datosAdicionales"));
                    agenda.add(datos);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en agendaActual.");
        }*/
        System.out.println(jsonDatos);
        
        return agenda;
    }
    
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
                        estatus.setString(1, "CITA");
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
                registro.setString(1, "REGISTRO");
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
    
    public void rechazarDocumentacionEstudiante(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                //Cambiar estatus a registro.
                PreparedStatement registro = conectar.prepareStatement("UPDATE clientes SET estado = ? WHERE idclientes = ?");
                registro.setString(1, "REGISTRO");
                registro.setInt(2, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
                registro.execute();
                //Eliminamos la documentación.
                PreparedStatement eliminar = conectar.prepareStatement("DELETE FROM documentacioncliente WHERE idDocumentacion = ? AND clientes_idclientes= ?");
                eliminar.setInt(1, Integer.parseInt(jsonDatos.get("idDocumentacion").toString()));
                eliminar.setInt(2, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
                eliminar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en rechazarDocumentación.");
        }
    }
    
    public JSONArray getSolicitudesAsignadasEstudiantes(int idOperador){
        Connection conectar = ConexionMySQL.connection();
        JSONArray jsonAsignados = new JSONArray();
        
        if(conectar != null){
            try{ 
                PreparedStatement asigandos = conectar.prepareStatement("SELECT * FROM documentacioncliente INNER JOIN clientes WHERE documentacioncliente.operadores_idoperadores = ? AND clientes.idclientes = documentacioncliente.clientes_idclientes");
                asigandos.setInt(1, idOperador);
                ResultSet resultado = asigandos.executeQuery();
                
                while(resultado.next()){
                    //Consultar primero si ya esta activo
                    PreparedStatement cita = conectar.prepareStatement("SELECT * FROM clientes WHERE idclientes = ? AND estado = ?");
                    cita.setInt(1, resultado.getInt("clientes_idclientes"));
                    cita.setString(2, "ACTIVO");
                    ResultSet resultadoCita = cita.executeQuery();
                    if(!resultadoCita.next()){
                        JSONObject jsonProf = new JSONObject();
                        jsonProf.put("nombre", resultado.getString("nombre") + " " + resultado.getString("apellidoPaterno") + " " + resultado.getString("apellidoMaterno"));
                        jsonProf.put("correo", resultado.getString("correo"));
                        jsonProf.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro"));
                        jsonProf.put("direccion", resultado.getString("direccion"));
                        jsonProf.put("celular", resultado.getString("celular"));
                        jsonProf.put("telefono", resultado.getString("telefonoLocal"));
                        jsonProf.put("fechaNacimiento", resultado.getDate("fechaNacimiento"));
                        jsonProf.put("foto", resultado.getString("foto"));
                        jsonProf.put("ineFrontal", resultado.getString("ineFrontal"));
                        jsonProf.put("ineTrasera", resultado.getString("ineTrasera"));
                        jsonProf.put("idDocumentacion", resultado.getInt("idDocumentacion"));
                        jsonProf.put("idEstudiante", resultado.getInt("clientes_idclientes"));
                        jsonAsignados.add(jsonProf);
                    } 
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en getSolicitudesAsignadas.");
        }
        
        return jsonAsignados;
    }
    
    public JSONArray getSolicitudesAsignadas(int idOperador){
        Connection conectar = ConexionMySQL.connection();
        JSONArray jsonAsignados = new JSONArray();
        
        if(conectar != null){
            try{ 
                PreparedStatement asigandos = conectar.prepareStatement("SELECT * FROM documentacionprofesor INNER JOIN profesores WHERE documentacionprofesor.operadores_idoperadores = ? AND profesores.idprofesores = documentacionprofesor.profesores_idprofesores");
                asigandos.setInt(1, idOperador);
                ResultSet resultado = asigandos.executeQuery();
                
                while(resultado.next()){
                    //Consultar primero si ya hay cita
                    PreparedStatement cita = conectar.prepareStatement("SELECT * FROM profesores WHERE idprofesores = ? AND (estado = ? OR estado = ?)");
                    cita.setInt(1, resultado.getInt("profesores_idprofesores"));
                    cita.setString(2, "cita");
                    cita.setString(3, "activo");
                    ResultSet resultadoCita = cita.executeQuery();
                    if(!resultadoCita.next()){
                        JSONObject jsonProf = new JSONObject();
                        jsonProf.put("nombre", resultado.getString("nombre"));
                        jsonProf.put("correo", resultado.getString("correo"));
                        jsonProf.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro"));
                        jsonProf.put("foto", resultado.getString("foto"));
                        jsonProf.put("acta", resultado.getString("acta"));
                        jsonProf.put("antecedentes", resultado.getString("antecedentes"));
                        jsonProf.put("certificado1", resultado.getString("certificado1"));
                        jsonProf.put("certificado2", resultado.getString("certificado2"));
                        jsonProf.put("certificado3", resultado.getString("certificado3"));
                        jsonProf.put("certificado4", resultado.getString("certificado4"));
                        jsonProf.put("certificado5", resultado.getString("certificado5"));
                        jsonProf.put("certificado6", resultado.getString("certificado6"));
                        jsonProf.put("certificado7", resultado.getString("certificado7"));
                        jsonProf.put("certificado8", resultado.getString("certificado8"));
                        jsonProf.put("ine", resultado.getString("ine"));
                        jsonProf.put("idDocumentacion", resultado.getInt("idDocumentacion"));
                        jsonProf.put("idProfesor", resultado.getInt("profesores_idprofesores"));
                        jsonAsignados.add(jsonProf);
                    } 
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en getSolicitudesAsignadas.");
        }
        
        return jsonAsignados;
    }
    
    public void asociarSolicitudEstudiante(JSONObject jsonDatos){
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement asociar = conectar.prepareStatement("UPDATE documentacioncliente SET operadores_idoperadores = ? WHERE idDocumentacion = ?");
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
    
    public JSONArray obtenerSolicitudesEstudiantes(){
        Connection conectar = ConexionMySQL.connection();
        JSONArray jsonSolicitudes = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement solicitudes = conectar.prepareStatement("SELECT * FROM clientes INNER JOIN documentacioncliente WHERE documentacioncliente.clientes_idclientes = clientes.idclientes AND clientes.estado = ? AND documentacioncliente.operadores_idoperadores IS NULL");
                solicitudes.setString(1, "DOCUMENTACION");
                ResultSet resultado = solicitudes.executeQuery();
                while(resultado.next()){
                    JSONObject jsonProf = new JSONObject();
                    jsonProf.put("nombre", resultado.getString("nombre"));
                    jsonProf.put("correo", resultado.getString("correo"));
                    jsonProf.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro"));
                    jsonProf.put("foto", resultado.getString("foto"));
                    jsonProf.put("ineFrontal", resultado.getString("ineFrontal"));
                     jsonProf.put("ineTrasera", resultado.getString("ineTrasera"));
                    jsonProf.put("idDocumentacion", resultado.getInt("idDocumentacion"));
                    jsonSolicitudes.add(jsonProf);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en obtenerSolicitudes.");
        }
        
        return jsonSolicitudes;
    }
    
    public JSONArray obtenerSolicitudes(){
        Connection conectar = ConexionMySQL.connection();
        JSONArray jsonSolicitudes = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement solicitudes = conectar.prepareStatement("SELECT * FROM profesores INNER JOIN documentacionprofesor WHERE documentacionprofesor.profesores_idprofesores = profesores.idprofesores AND profesores.estado = ? AND documentacionprofesor.operadores_idoperadores IS NULL");
                solicitudes.setString(1, "DOCUMENTACION");
                ResultSet resultado = solicitudes.executeQuery();
                while(resultado.next()){
                    JSONObject jsonProf = new JSONObject();
                    jsonProf.put("nombre", resultado.getString("nombre"));
                    jsonProf.put("correo", resultado.getString("correo"));
                    jsonProf.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro"));
                    jsonProf.put("foto", resultado.getString("foto"));
                    jsonProf.put("acta", resultado.getString("acta"));
                    jsonProf.put("antecedentes", resultado.getString("antecedentes"));
                    jsonProf.put("certificado1", resultado.getString("certificado1"));
                    jsonProf.put("certificado2", resultado.getString("certificado2"));
                    jsonProf.put("certificado3", resultado.getString("certificado3"));
                    jsonProf.put("ine", resultado.getString("ine"));
                    jsonProf.put("idDocumentacion", resultado.getInt("idDocumentacion"));
                    jsonSolicitudes.add(jsonProf);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en obtenerSolicitudes.");
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
                String tipo = "";
                if(tipoUsuario == Constantes.TIPO_USUARIO_ESTUDIANTE)
                    tipo = "ESTUDIANTE";//query = "SELECT * FROM msg_tickets INNER JOIN clientes WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND clientes.idclientes = msg_tickets.idUsuario_Operador";
                else if(tipoUsuario == Constantes.TIPO_USUARIO_PROFESOR)
                    tipo = "PROFESOR";//query = "SELECT * FROM msg_tickets INNER JOIN profesores WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND profesores.idprofesores = msg_tickets.idUsuario_Operador";
             
                PreparedStatement mensajesConsulta = conectar.prepareStatement("SELECT * FROM msg_tickets WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ?");
                mensajesConsulta.setInt(1, idTicket);
                ResultSet resultado = mensajesConsulta.executeQuery();
                
                while(resultado.next()){
                    JSONObject mensaje = new JSONObject();
                    mensaje.put("nombreUsuario", tipo);
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
    
    public JSONArray ticketsFinalizados(int idOperador, String categoria){
        Connection conectar = ConexionMySQL.connection();
        JSONArray jsonFinalizados = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement finalizados = conectar.prepareStatement("SELECT * FROM tickets_ayuda WHERE operadores_idoperadores = ? AND estatus = ? AND categoria = ?");
                finalizados.setInt(1, idOperador);
                finalizados.setInt(2, Constantes.ESTATUS_SOLUCIONADO);
                finalizados.setString(3, categoria);
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
                    ticket.put("idSesion", resultado.getInt("sesiones_idsesiones"));
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
    
    public JSONArray obtenerTicketsAsociados(int idOperador, String categoria){
        Connection conectar = ConexionMySQL.connection();
        JSONArray jsonAsociados = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement asociados = conectar.prepareStatement("SELECT * FROM tickets_ayuda WHERE operadores_idoperadores = ? AND estatus = ? AND categoria = ?");
                asociados.setInt(1, idOperador);
                asociados.setInt(2, Constantes.ESTATUS_EN_CURSO);
                asociados.setString(3, categoria);
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
                    jsonObject.put("idSesion", resultado.getInt("sesiones_idsesiones"));
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
                    ticketInfo.put("categoria", resultado.getString("categoria"));
                    ticketInfo.put("idSesion", resultado.getInt("sesiones_idsesiones"));
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
    
    public JSONArray obtenerTicketsAdmin(String categoria){
        Connection conectar = ConexionMySQL.connection();
        JSONArray tickets = new JSONArray();
        
        if(conectar != null){
            try{
                PreparedStatement ticketsConsulta = conectar.prepareStatement("SELECT * FROM tickets_ayuda WHERE estatus = ? AND categoria = ?");
                ticketsConsulta.setInt(1, Constantes.ESTATUS_SIN_OPERADOR);
                ticketsConsulta.setString(2, categoria);
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
                    jsonObject.put("idSesion", resultado.getInt("sesiones_idsesiones"));
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
                String tipo = "";
                if(tipoUsuario == Constantes.TIPO_USUARIO_ESTUDIANTE)
                    tipo = "ESTUDIANTE";//query = "SELECT * FROM msg_tickets INNER JOIN clientes WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND clientes.idclientes = msg_tickets.idUsuario_Operador";
                else if(tipoUsuario == Constantes.TIPO_USUARIO_PROFESOR)
                    tipo = "PROFESOR";//query = "SELECT * FROM msg_tickets INNER JOIN profesores WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND profesores.idprofesores = msg_tickets.idUsuario_Operador";
                
                PreparedStatement mensajes = conectar.prepareStatement("SELECT * FROM msg_tickets WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ?");
                mensajes.setInt(1, idTicket);
                ResultSet msgRes = mensajes.executeQuery();
                JSONArray jsonMensajesTicket = new JSONArray();
                
                
                while(msgRes.next()){
                    JSONObject msgJSON = new JSONObject();
                    if(msgRes.getBoolean("operadorBool"))
                        msgJSON.put("nombreUsuario", tipo);
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
        System.out.println(mensajesArray);
        
        return mensajesArray;
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
