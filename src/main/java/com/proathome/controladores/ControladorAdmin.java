package com.proathome.controladores;

import com.proathome.modelos.Admin;
import com.proathome.modelos.Constantes;
import com.proathome.mysql.DBController;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Marvin
 */
public class ControladorAdmin {
    
    public static final int CLIENTE = 1;
    public static final int PROFESIONAL = 2;
    public static boolean primeraVez = true;
    
    public void latidoSQL(){
        try{
            /*
            if(ControladorAdmin.primeraVez){
                System.out.println("Entro por unica vez al prender el server.");
                ControladorAdmin.primeraVez = false;
                Date horaDespertar = new Date(System.currentTimeMillis());
        
                Calendar c = Calendar.getInstance();
                c.setTime(horaDespertar);
                System.out.println(c.get(Calendar.DAY_OF_WEEK));
                // Si la hora es posterior a las 8am se programa la alarma para el dia siguiente
                if (c.get(Calendar.HOUR_OF_DAY) >= 22) {
                    c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
                }

                c.set(Calendar.HOUR_OF_DAY, 18);
                c.set(Calendar.MINUTE, 37);
                c.set(Calendar.SECOND, 0);

                horaDespertar = c.getTime();
                System.out.println(horaDespertar);
                System.out.println(c.get(Calendar.DAY_OF_WEEK));
                // El despertador suena cada 24h (una vez al dia)
                int tiempoRepeticion = 60000; 

                // Programamos el despertador para que "suene" a las 8am todos los dias 
                Timer temporizador = new Timer();
                temporizador.schedule(new Temporizador(), horaDespertar, tiempoRepeticion);
            }*/
            PreparedStatement latido = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM clientes");
            ResultSet resultado = latido.executeQuery();
            while(resultado.next()){}
            System.out.println("Latido SQL");
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public JSONObject historialReportes(String tipoUsuario, int idUsuario){
        JSONObject respuesta = new JSONObject();
        System.out.println(idUsuario);
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
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
        
        if(DBController.getInstance().getConnection() != null){
            try{
                String query = null;
                if(jsonDatos.get("perfil").equals("CLIENTE"))
                    query = "UPDATE clientes SET estado = ? WHERE idclientes = ?";
                else if(jsonDatos.get("perfil").equals("PROFESIONAL"))
                    query = "UPDATE profesionales SET estado = ? WHERE idprofesionales = ?";

                PreparedStatement bloquear = DBController.getInstance().getConnection().prepareStatement(query);
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
    
    public void borrarReportes(JSONObject jsonDatos){
        try{
            PreparedStatement eliminar = DBController.getInstance().getConnection().prepareStatement("DELETE FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
            eliminar.setInt(1, Integer.parseInt(jsonDatos.get("idUsuario").toString()));
            if(Integer.parseInt(jsonDatos.get("tipoPerfil").toString()) == Constantes.TIPO_USUARIO_CLIENTE)
                eliminar.setString(2, "CLIENTE");
            else if(Integer.parseInt(jsonDatos.get("tipoPerfil").toString()) == Constantes.TIPO_USUARIO_PROFESIONAL)
                eliminar.setString(2, "PROFESIONAL");
            
            eliminar.execute();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public JSONObject desbloquearPerfil(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement desbloquear = null;
                if(Integer.parseInt(jsonDatos.get("tipoPerfil").toString()) == Constantes.TIPO_USUARIO_CLIENTE)
                    desbloquear = DBController.getInstance().getConnection().prepareStatement("UPDATE clientes SET estado = ? WHERE idclientes = ?");
                else if(Integer.parseInt(jsonDatos.get("tipoPerfil").toString()) == Constantes.TIPO_USUARIO_PROFESIONAL)
                    desbloquear = DBController.getInstance().getConnection().prepareStatement("UPDATE profesionales SET estado = ? WHERE idprofesionales = ?");
                
                desbloquear.setString(1, "ACTIVO");
                desbloquear.setInt(2, Integer.parseInt(jsonDatos.get("idUsuario").toString()));
                desbloquear.execute();
                
                borrarReportes(jsonDatos);
                
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
    
    public void bloqueoPerfilCliente(int idCliente){
        try{
            PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT COUNT(*) AS num FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
            consulta.setInt(1, idCliente);
            consulta.setString(2, "CLIENTE");
            ResultSet resultado = consulta.executeQuery();
            
            if(resultado.next()){
                int numReportes = resultado.getInt("num");
                if(numReportes >= 3){
                    //Bloqueado
                    PreparedStatement bloquear = DBController.getInstance().getConnection().prepareStatement("UPDATE clientes SET estado = ? WHERE idclientes = ?");
                    bloquear.setString(1, "BLOQUEADO");
                    bloquear.setInt(2, idCliente);
                    bloquear.execute();
                }
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public void bloqueoPerfilProfesional(int idProfesional){
        try{
            PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT COUNT(*) AS num FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
            consulta.setInt(1, idProfesional);
            consulta.setString(2, "PROFESIONAL");
            ResultSet resultado = consulta.executeQuery();
            
            if(resultado.next()){
                int numReportes = resultado.getInt("num");
                if(numReportes >= 3){
                    //Bloqueado
                    PreparedStatement bloquear = DBController.getInstance().getConnection().prepareStatement("UPDATE profesionales SET estado = ? WHERE idprofesionales = ?");
                    bloquear.setString(1, "BLOQUEADO");
                    bloquear.setInt(2, idProfesional);
                    bloquear.execute();
                }
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public void guardarHistorialReportes(JSONObject jsonDatos){
        try{
            PreparedStatement guardar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO historial_reportes (descripcion, idUsuario, tipoUsuario, fecha, tickets_ayuda_idtickets_ayuda) VALUES (?,?,?,?,?)");
            guardar.setString(1, jsonDatos.get("descripcionReporte").toString());
            guardar.setInt(2, Integer.parseInt(jsonDatos.get("idUsuarioReportado").toString()));
            guardar.setString(3, jsonDatos.get("tipoUsuarioReportado").toString());
            guardar.setDate(4, java.sql.Date.valueOf(ControladorFechaActual.getFechaActual()));
            guardar.setInt(5, Integer.parseInt(jsonDatos.get("idTicket").toString()));
            guardar.execute();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public JSONObject crearReporte(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
  
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement guardar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO reportes (descripcion, idUsuario, tipoUsuario, tickets_ayuda_idtickets_ayuda) VALUES (?,?,?,?)");
                guardar.setString(1, jsonDatos.get("descripcionReporte").toString());
                guardar.setInt(2, Integer.parseInt(jsonDatos.get("idUsuarioReportado").toString()));
                guardar.setString(3, jsonDatos.get("tipoUsuarioReportado").toString());
                guardar.setInt(4, Integer.parseInt(jsonDatos.get("idTicket").toString()));
                guardar.execute();
                
                guardarHistorialReportes(jsonDatos);
                
                //Validar bloqueo de perfil
                if(jsonDatos.get("tipoUsuarioReportado").toString().equals("CLIENTE"))
                    bloqueoPerfilCliente(Integer.parseInt(jsonDatos.get("idUsuarioReportado").toString()));
                else if(jsonDatos.get("tipoUsuarioReportado").toString().equals("PROFESIONAL"))
                    bloqueoPerfilProfesional(Integer.parseInt(jsonDatos.get("idUsuarioReportado").toString()));
                
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
    
    public JSONObject getCliente(int idCliente){
        JSONObject cliente = new JSONObject();
        try{
            PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM clientes WHERE idclientes = ?");
            consulta.setInt(1, idCliente);
            ResultSet resultado = consulta.executeQuery();
            
            if(resultado.next()){
                cliente.put("idCliente", resultado.getInt("idclientes"));
                cliente.put("nombre", resultado.getString("nombre") + resultado.getString("apellidoPaterno") + resultado.getString("apellidoMaterno"));
                cliente.put("correo", resultado.getString("correo"));
            }else{
                cliente.put("idCliente", "Error al obtener cliente.");
                cliente.put("nombre", "Error al obtener cliente.");
                cliente.put("correo", "Error al obtener cliente.");
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
        return cliente;
    }
    
    public JSONObject getProfesional(int idProfesional){
        JSONObject profesional = new JSONObject();
        try{
            PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM profesionales WHERE idprofesionales = ?");
            consulta.setInt(1, idProfesional);
            ResultSet resultado = consulta.executeQuery();
            
            if(resultado.next()){
                profesional.put("nombre", resultado.getString("nombre"));
                profesional.put("correo", resultado.getString("correo"));
            }else{
                profesional.put("nombre", "Error al obtener nombre de profesional.");
                profesional.put("correo", "Error al obtener correo de profesional.");
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            profesional.put("nombre", "Error al obtener nombre de profesional.");
            profesional.put("correo", "Error al obtener correo de profesional.");
        }
        
        return profesional;
    }
    
    public JSONObject verServicio(int idSesion){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ?");
                consulta.setInt(1, idSesion);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    JSONObject datosSesion = new JSONObject();
                    JSONObject profesional = getProfesional(resultado.getInt("profesionales_idprofesionales"));
                    JSONObject cliente = getCliente(resultado.getInt("clientes_idclientes"));
                    datosSesion.put("profesional", resultado.getInt("profesionales_idprofesionales") == 0 ? "Sin profesional asignado" : profesional.get("nombre").toString());
                    datosSesion.put("correo",  resultado.getInt("profesionales_idprofesionales") == 0 ? "Sin profesional asignado" : profesional.get("correo").toString());
                    datosSesion.put("idProfesional", resultado.getInt("profesionales_idprofesionales"));
                    datosSesion.put("idCliente", cliente.get("idCliente"));
                    datosSesion.put("cliente", cliente.get("nombre"));
                    datosSesion.put("correoCliente", cliente.get("correo"));
                    datosSesion.put("horario", resultado.getString("horario"));
                    datosSesion.put("lugar", resultado.getString("lugar"));
                    datosSesion.put("tiempo", resultado.getInt("tiempo"));
                    datosSesion.put("extras", resultado.getString("extras"));
                    datosSesion.put("tipoServicio", resultado.getString("tipoServicio"));
                    datosSesion.put("latitud", resultado.getDouble("latitud"));
                    datosSesion.put("longitud", resultado.getDouble("longitud"));
                    datosSesion.put("idSeccion", resultado.getInt("idSeccion"));
                    datosSesion.put("idNivel", resultado.getInt("idNivel"));
                    datosSesion.put("idBloque", resultado.getInt("idBloque"));
                    datosSesion.put("fecha", resultado.getDate("fecha").toString());
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
                    PreparedStatement consultaPago = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM pagos WHERE idpagos = ?");
                    consultaPago.setInt(1, resultado.getInt("pagos_idpagos"));
                    ResultSet resultadoPago = consultaPago.executeQuery();
                    
                    if(resultadoPago.next()){
                        JSONObject pago = new JSONObject();
                        pago.put("token", resultadoPago.getString("token"));
                        pago.put("costoServicio", resultadoPago.getDouble("costoServicio"));
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
    
    public JSONObject activarCliente(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                System.out.println(jsonDatos);
                //Cambiar el estatus activo y foto
                PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE clientes SET foto = ?, estado = ? WHERE idclientes = ?");
                actualizar.setString(1, jsonDatos.get("foto").toString());
                actualizar.setString(2, "ACTIVO");
                actualizar.setInt(3, Integer.parseInt(jsonDatos.get("idCliente").toString()));
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
    
    public JSONObject getPerfilProfesional(int idProfesional){
        JSONObject respuesta = new JSONObject();
        JSONArray sesionesArray = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            //Obtener perfil
            try{
               PreparedStatement perfil = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM profesionales WHERE profesionales.idprofesionales = ?");
               perfil.setInt(1, idProfesional);
               ResultSet resPerfil = perfil.executeQuery();
               
               if(resPerfil.next()){
                   JSONObject jsonPerfil = new JSONObject();
                   jsonPerfil.put("nombre", resPerfil.getString("nombre"));
                   jsonPerfil.put("correo", resPerfil.getString("correo"));
                   jsonPerfil.put("rango", resPerfil.getString("rangoServicio"));
                   jsonPerfil.put("fechaNacimiento", resPerfil.getDate("fechaNacimiento").toString());
                   jsonPerfil.put("estatus", resPerfil.getString("estado"));
                   respuesta.put("perfil", jsonPerfil);
                   
                   //Historial Sesiones.
                  PreparedStatement sesiones = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE profesionales_idprofesionales = ?");
                  sesiones.setInt(1, idProfesional);
                  ResultSet resSesiones = sesiones.executeQuery();
                  
                  while(resSesiones.next()){
                      JSONObject sesionJSON = new JSONObject();
                      sesionJSON.put("idSesion", resSesiones.getInt("idsesiones"));
                      sesionJSON.put("idCliente", resSesiones.getInt("clientes_idclientes"));
                      sesionJSON.put("horario", resSesiones.getString("horario"));
                      sesionJSON.put("lugar", resSesiones.getString("lugar"));
                      sesionJSON.put("tiempo", resSesiones.getInt("tiempo"));
                      sesionJSON.put("extras", resSesiones.getString("extras"));
                      sesionJSON.put("tipoServicio", resSesiones.getString("tipoServicio"));
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
    
    public JSONObject getPerfilCliente(int idCliente){
        JSONObject respuesta = new JSONObject();
        JSONArray sesionesArray = new JSONArray();
        JSONArray planesArray = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            //Obtener perfil
            try{
               PreparedStatement perfil = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM clientes INNER JOIN planes WHERE clientes.idclientes = ? AND planes.clientes_idclientes = clientes.idclientes");
               perfil.setInt(1, idCliente);
               ResultSet resPerfil = perfil.executeQuery();
               
               if(resPerfil.next()){
                   JSONObject jsonPerfil = new JSONObject();
                   jsonPerfil.put("nombre", resPerfil.getString("nombre"));
                   jsonPerfil.put("correo", resPerfil.getString("correo"));
                   jsonPerfil.put("estado", resPerfil.getString("estado"));
                   jsonPerfil.put("tipoPlan", resPerfil.getString("tipoPlan"));
                   jsonPerfil.put("monedero", resPerfil.getInt("monedero"));
                   jsonPerfil.put("fechaNacimiento", resPerfil.getDate("fechaNacimiento").toString());
                   respuesta.put("perfil", jsonPerfil);
                   
                   //Historial Sesiones.
                  PreparedStatement sesiones = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE clientes_idclientes = ?");
                  sesiones.setInt(1, idCliente);
                  ResultSet resSesiones = sesiones.executeQuery();
                  
                  while(resSesiones.next()){
                      JSONObject sesionJSON = new JSONObject();
                      sesionJSON.put("idSesion", resSesiones.getInt("idsesiones"));
                      sesionJSON.put("idProfesional", resSesiones.getInt("profesionales_idprofesionales"));
                      sesionJSON.put("horario", resSesiones.getString("horario"));
                      sesionJSON.put("lugar", resSesiones.getString("lugar"));
                      sesionJSON.put("tiempo", resSesiones.getInt("tiempo"));
                      sesionJSON.put("extras", resSesiones.getString("extras"));
                      sesionJSON.put("tipoServicio", resSesiones.getString("tipoServicio"));
                      sesionJSON.put("idSeccion", resSesiones.getInt("idSeccion"));
                      sesionJSON.put("idNivel", resSesiones.getInt("idNivel"));
                      sesionJSON.put("idBloque", resSesiones.getInt("idBloque"));
                      sesionesArray.add(sesionJSON);
                  }
                  
                  respuesta.put("sesiones", sesionesArray);
                  
                  //Historial de planes
                  PreparedStatement planes = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM historialplanes WHERE clientes_idclientes = ?");
                  planes.setInt(1, idCliente);
                  ResultSet resPlanes = planes.executeQuery();
                  
                  while(resPlanes.next()){
                      JSONObject planesJSON = new JSONObject();
                      planesJSON.put("idPlan", resPlanes.getInt("idHistorialPlanes"));
                      planesJSON.put("tipoPlan", resPlanes.getString("tipoPlan"));
                      planesJSON.put("fechaInicio", resPlanes.getDate("fechaInicio").toString());
                      planesJSON.put("fechFin", resPlanes.getDate("fechaFin").toString());
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
    
    public JSONObject getProfesionalesRegistrados(){
        JSONObject respuesta = new JSONObject();
        JSONArray profesionales = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement registros = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM profesionales");
                ResultSet resultado = registros.executeQuery();
                while(resultado.next()){
                    JSONObject profesional = new JSONObject();
                    profesional.put("idProfesional", resultado.getInt("idprofesionales"));
                    profesional.put("nombre", resultado.getString("nombre"));
                    profesional.put("correo", resultado.getString("correo"));
                    profesional.put("estado", resultado.getString("estado"));
                    profesionales.add(profesional);
                }
                
                respuesta.put("mensaje", profesionales);
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
    
    public JSONObject getClientesRegistrados(){
        JSONObject respuesta = new JSONObject();
        JSONArray clientes = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement registros = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM clientes INNER JOIN planes WHERE planes.clientes_idclientes = clientes.idclientes");
                ResultSet resultado = registros.executeQuery();
                while(resultado.next()){
                    JSONObject cliente = new JSONObject();
                    cliente.put("idCliente", resultado.getInt("idclientes"));
                    cliente.put("nombre", resultado.getString("nombre"));
                    cliente.put("correo", resultado.getString("correo"));
                    cliente.put("tipoPlan", resultado.getString("tipoPlan"));
                    cliente.put("monedero", resultado.getInt("monedero"));
                    clientes.add(cliente);
                }
                
                respuesta.put("mensaje", clientes);
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
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE operadores SET nombres = ?, apellidoPaterno = ?, apellidoMaterno = ?, usuario = ?, contrasena = ?"
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
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement eliminar = DBController.getInstance().getConnection().prepareStatement("DELETE FROM operadores WHERE idoperadores = ?");
                eliminar.setInt(1, idOperador);
                eliminar.execute();
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", "Operador eliminado exitosamente.");
            }catch(SQLException ex){
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error, revisa si el operador está asigando a un Ewstudiante/Profesional.");
                ex.printStackTrace();
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
    }
    
    public JSONObject getOperadoresUsuario(){
        JSONArray respuesta = new JSONArray();
        JSONObject mensaje = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement registros = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM operadores WHERE rango = ?");
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
        JSONArray respuesta = new JSONArray();
        JSONObject mensaje = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement registros = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM operadores WHERE rango = ?");
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
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                
                //VERIFICAR SI NO EXISTE OPERADOR EN EL MISMO RANGO.
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM operadores WHERE usuario = ? AND rango = ?");
                consulta.setString(1, jsonDatos.get("usuario").toString());
                consulta.setInt(2, Integer.parseInt(jsonDatos.get("rango").toString()));
                ResultSet resultado = consulta.executeQuery();
                
                if(!resultado.next()){
                    PreparedStatement guardar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO operadores (nombres, apellidoPaterno, apellidoMaterno, rango, usuario, contrasena)"
                                                                        + " VALUES(?,?,?,?,?,?)");
                    guardar.setString(1, jsonDatos.get("nombre").toString());
                    guardar.setString(2, jsonDatos.get("paterno").toString());
                    guardar.setString(3, jsonDatos.get("materno").toString());
                    guardar.setInt(4, Integer.parseInt(jsonDatos.get("rango").toString()));
                    guardar.setString(5, jsonDatos.get("usuario").toString());
                    guardar.setString(6, jsonDatos.get("contrasena").toString());
                    guardar.execute();
                    System.out.println("ok");
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", "Operador registrado correctamente.");
                }else{
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", "Usuario ya registrado.");
                }
                
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
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                
                //Agendar nuevamente
                //Verifricar la disponibilidad de la cita en fecha 1.
                PreparedStatement consultaF1 = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM citas WHERE fecha1 = ? AND horario1 = ?");
                consultaF1.setDate(1, java.sql.Date.valueOf(jsonDatos.get("fecha1").toString()));
                consultaF1.setString(2, jsonDatos.get("horario1").toString());
                ResultSet resultadoF1 = consultaF1.executeQuery();
                
                if(resultadoF1.next()){
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", "El horario de la FECHA 1 ya tiene una posible cita agendada.");
                }else{
                    //Validamos la fecha 2.
                    PreparedStatement consultaF2 = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM citas WHERE fecha2 = ? AND horario2 = ?");
                    consultaF2.setDate(1, java.sql.Date.valueOf(jsonDatos.get("fecha2").toString()));
                    consultaF2.setString(2, jsonDatos.get("horario2").toString());
                    ResultSet resultadoF2 = consultaF2.executeQuery();
                    
                    if(resultadoF2.next()){
                        respuesta.put("respuesta", false);
                        respuesta.put("mensaje", "El horario de la FECHA 2 ya tiene una posible cita agendada.");
                    }else{
                        //Eliminar previa cita.
                        PreparedStatement eliminar = DBController.getInstance().getConnection().prepareStatement("DELETE FROM citas WHERE profesionales_idprofesionales = ?");
                        eliminar.setInt(1, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                        eliminar.execute();
                        //Guardar cita
                        PreparedStatement guardar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO citas (fecha1, fecha2, horario1, horario2,"
                                + " tipoCita, profesionales_idprofesionales, operadores_idoperadores, datosAdicionales) VALUES (?,?,?,?,?,?,?,?)");
                        guardar.setDate(1, java.sql.Date.valueOf(jsonDatos.get("fecha1").toString()));
                        guardar.setDate(2, java.sql.Date.valueOf(jsonDatos.get("fecha2").toString()));
                        guardar.setString(3, jsonDatos.get("horario1").toString());
                        guardar.setString(4, jsonDatos.get("horario2").toString());
                        guardar.setString(5, jsonDatos.get("tipoCita").toString());
                        guardar.setInt(6, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                        guardar.setInt(7, Integer.parseInt(jsonDatos.get("idOperador").toString()));
                        guardar.setString(8, jsonDatos.get("datosAdicionales").toString());
                        guardar.execute();
                        //Cambiar estatus
                        PreparedStatement estatus = DBController.getInstance().getConnection().prepareStatement("UPDATE profesionales SET estado = ? WHERE idprofesionales = ?");
                        estatus.setString(1, "CITA");
                        estatus.setInt(2, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
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
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement activar = DBController.getInstance().getConnection().prepareStatement("UPDATE profesionales SET foto = ?, estado = ?, rangoServicio = ? WHERE idprofesionales = ?");
                activar.setString(1, jsonDatos.get("foto").toString());
                activar.setString(2, "ACTIVO");
                activar.setInt(3, Integer.parseInt(jsonDatos.get("rango").toString()));
                activar.setInt(4, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                activar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en activarPerfil.");
        }
    }
    
    public JSONArray citasEnFecha(String date, int idOperador){
        JSONArray fecha = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement fechas = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM citas INNER JOIN profesionales WHERE citas.fechaAcordada = ? AND citas.operadores_idoperadores = ? AND profesionales.idprofesionales = citas.profesionales_idprofesionales");
                fechas.setDate(1, java.sql.Date.valueOf(date));
                fechas.setInt(2, idOperador);
                ResultSet resultado = fechas.executeQuery();
                
                while(resultado.next()){
                    JSONObject datos = new JSONObject();
                    datos.put("fechaAcordada", resultado.getDate("fechaAcordada").toString());
                    datos.put("horarioAcordado", resultado.getString("horarioAcordado"));
                    datos.put("profesional", resultado.getString("nombre"));
                    datos.put("correo", resultado.getString("correo"));
                    datos.put("tipoCita", resultado.getString("tipoCita"));
                    datos.put("datosAdicionales", resultado.getString("datosAdicionales"));
                    datos.put("idProfesional", resultado.getInt("idprofesionales"));
                    
                    PreparedStatement consultaFoto = DBController.getInstance().getConnection().prepareStatement("SELECT foto FROM documentacionprofesional WHERE profesionales_idprofesionales = ?");
                    consultaFoto.setInt(1, resultado.getInt("idprofesionales"));
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
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement fechas = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM citas INNER JOIN profesionales WHERE citas.fechaAcordada >= ? AND citas.operadores_idoperadores = ? AND profesionales.idprofesionales = citas.profesionales_idprofesionales");
                fechas.setDate(1, java.sql.Date.valueOf(hoy));
                fechas.setInt(2, idOperador);
                ResultSet resultado = fechas.executeQuery();
                
                while(resultado.next()){
                    JSONObject datos = new JSONObject();
                    datos.put("fechaAcordada", resultado.getDate("fechaAcordada"));
                    datos.put("horarioAcordado", resultado.getString("horarioAcordado"));
                    datos.put("profesional", resultado.getString("nombre"));
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
        JSONObject respuesta = new JSONObject();
    
        if(DBController.getInstance().getConnection() != null){
            try{
                //Verifricar la disponibilidad de la cita en fecha 1.
                PreparedStatement consultaF1 = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM citas WHERE fecha1 = ? AND horario1 = ?");
                consultaF1.setDate(1, java.sql.Date.valueOf(jsonDatos.get("fecha1").toString()));
                consultaF1.setString(2, jsonDatos.get("horario1").toString());
                ResultSet resultadoF1 = consultaF1.executeQuery();
                
                if(resultadoF1.next()){
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", "El horario de la FECHA 1 ya tiene una posible cita agendada.");
                }else{
                    //Validamos la fecha 2.
                    PreparedStatement consultaF2 = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM citas WHERE fecha2 = ? AND horario2 = ?");
                    consultaF2.setDate(1, java.sql.Date.valueOf(jsonDatos.get("fecha2").toString()));
                    consultaF2.setString(2, jsonDatos.get("horario2").toString());
                    ResultSet resultadoF2 = consultaF2.executeQuery();
                    
                    if(resultadoF2.next()){
                        respuesta.put("respuesta", false);
                        respuesta.put("mensaje", "El horario de la FECHA 2 ya tiene una posible cita agendada.");
                    }else{
                        //Guardar cita
                        PreparedStatement guardar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO citas (fecha1, fecha2, horario1, horario2,"
                                + " tipoCita, profesionales_idprofesionales, operadores_idoperadores, datosAdicionales) VALUES (?,?,?,?,?,?,?,?)");
                        guardar.setDate(1, java.sql.Date.valueOf(jsonDatos.get("fecha1").toString()));
                        guardar.setDate(2, java.sql.Date.valueOf(jsonDatos.get("fecha2").toString()));
                        guardar.setString(3, jsonDatos.get("horario1").toString());
                        guardar.setString(4, jsonDatos.get("horario2").toString());
                        guardar.setString(5, jsonDatos.get("tipoCita").toString());
                        guardar.setInt(6, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                        guardar.setInt(7, Integer.parseInt(jsonDatos.get("idOperador").toString()));
                        guardar.setString(8, jsonDatos.get("datosAdicionales").toString());
                        guardar.execute();
                        //Cambiar estatus
                        PreparedStatement estatus = DBController.getInstance().getConnection().prepareStatement("UPDATE profesionales SET estado = ? WHERE idprofesionales = ?");
                        estatus.setString(1, "CITA");
                        estatus.setInt(2, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
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
        
        if(DBController.getInstance().getConnection() != null){
            try{
                //Cambiar estatus a registro.
                PreparedStatement registro = DBController.getInstance().getConnection().prepareStatement("UPDATE profesionales SET estado = ? WHERE idprofesionales = ?");
                registro.setString(1, "REGISTRO");
                registro.setInt(2, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                registro.execute();
                //Eliminamos la documentación.
                PreparedStatement eliminar = DBController.getInstance().getConnection().prepareStatement("DELETE FROM documentacionprofesional WHERE idDocumentacion = ? AND profesionales_idprofesionales = ?");
                eliminar.setInt(1, Integer.parseInt(jsonDatos.get("idDocumentacion").toString()));
                eliminar.setInt(2, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                eliminar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en rechazarDocumentación.");
        }
    }
    
    public void rechazarDocumentacionCliente(JSONObject jsonDatos){
        
        if(DBController.getInstance().getConnection() != null){
            try{
                //Cambiar estatus a registro.
                PreparedStatement registro = DBController.getInstance().getConnection().prepareStatement("UPDATE clientes SET estado = ? WHERE idclientes = ?");
                registro.setString(1, "REGISTRO");
                registro.setInt(2, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                registro.execute();
                //Eliminamos la documentación.
                PreparedStatement eliminar = DBController.getInstance().getConnection().prepareStatement("DELETE FROM documentacioncliente WHERE idDocumentacion = ? AND clientes_idclientes= ?");
                eliminar.setInt(1, Integer.parseInt(jsonDatos.get("idDocumentacion").toString()));
                eliminar.setInt(2, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                eliminar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en rechazarDocumentación.");
        }
    }
    
    public JSONArray getSolicitudesAsignadasClientes(int idOperador){
        JSONArray jsonAsignados = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{ 
                PreparedStatement asigandos = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM documentacioncliente INNER JOIN clientes WHERE documentacioncliente.operadores_idoperadores = ? AND clientes.idclientes = documentacioncliente.clientes_idclientes");
                asigandos.setInt(1, idOperador);
                ResultSet resultado = asigandos.executeQuery();
                
                while(resultado.next()){
                    //Consultar primero si ya esta activo
                    PreparedStatement cita = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM clientes WHERE idclientes = ? AND estado = ?");
                    cita.setInt(1, resultado.getInt("clientes_idclientes"));
                    cita.setString(2, "ACTIVO");
                    ResultSet resultadoCita = cita.executeQuery();
                    if(!resultadoCita.next()){
                        JSONObject jsonCliente = new JSONObject();
                        jsonCliente.put("nombre", resultado.getString("nombre") + " " + resultado.getString("apellidoPaterno") + " " + resultado.getString("apellidoMaterno"));
                        jsonCliente.put("correo", resultado.getString("correo"));
                        jsonCliente.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro").toString());
                        jsonCliente.put("direccion", resultado.getString("direccion"));
                        jsonCliente.put("celular", resultado.getString("celular"));
                        jsonCliente.put("telefono", resultado.getString("telefonoLocal"));
                        jsonCliente.put("fechaNacimiento", resultado.getDate("fechaNacimiento").toString());
                        jsonCliente.put("foto", resultado.getString("foto"));
                        jsonCliente.put("ineFrontal", resultado.getString("ineFrontal"));
                        jsonCliente.put("ineTrasera", resultado.getString("ineTrasera"));
                        jsonCliente.put("vacunacion", resultado.getString("vacunacion_covid"));
                        jsonCliente.put("idDocumentacion", resultado.getInt("idDocumentacion"));
                        jsonCliente.put("idCliente", resultado.getInt("clientes_idclientes"));
                        jsonAsignados.add(jsonCliente);
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
        JSONArray jsonAsignados = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{ 
                PreparedStatement asigandos = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM documentacionprofesional INNER JOIN profesionales WHERE documentacionprofesional.operadores_idoperadores = ? AND profesionales.idprofesionales = documentacionprofesional.profesionales_idprofesionales");
                asigandos.setInt(1, idOperador);
                ResultSet resultado = asigandos.executeQuery();
                
                while(resultado.next()){
                    //Consultar primero si ya hay cita
                    PreparedStatement cita = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM profesionales WHERE idprofesionales = ? AND (estado = ? OR estado = ?)");
                    cita.setInt(1, resultado.getInt("profesionales_idprofesionales"));
                    cita.setString(2, "cita");
                    cita.setString(3, "activo");
                    ResultSet resultadoCita = cita.executeQuery();
                    if(!resultadoCita.next()){
                        JSONObject jsonProf = new JSONObject();
                        jsonProf.put("nombre", resultado.getString("nombre"));
                        jsonProf.put("correo", resultado.getString("correo"));
                        jsonProf.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro").toString());
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
                        jsonProf.put("vacunacion", resultado.getString("vacunacion_covid"));
                        jsonProf.put("idDocumentacion", resultado.getInt("idDocumentacion"));
                        jsonProf.put("idProfesional", resultado.getInt("profesionales_idprofesionales"));
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
    
    public void asociarSolicitudCliente(JSONObject jsonDatos){
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement asociar = DBController.getInstance().getConnection().prepareStatement("UPDATE documentacioncliente SET operadores_idoperadores = ? WHERE idDocumentacion = ?");
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
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement asociar = DBController.getInstance().getConnection().prepareStatement("UPDATE documentacionprofesional SET operadores_idoperadores = ? WHERE idDocumentacion = ?");
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
    
    public JSONArray obtenerSolicitudesClientes(){
        JSONArray jsonSolicitudes = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement solicitudes = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM clientes INNER JOIN documentacioncliente WHERE documentacioncliente.clientes_idclientes = clientes.idclientes AND clientes.estado = ? AND documentacioncliente.operadores_idoperadores IS NULL");
                solicitudes.setString(1, "DOCUMENTACION");
                ResultSet resultado = solicitudes.executeQuery();
                while(resultado.next()){
                    JSONObject jsonCliente = new JSONObject();
                    jsonCliente.put("nombre", resultado.getString("nombre"));
                    jsonCliente.put("correo", resultado.getString("correo"));
                    jsonCliente.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro").toString());
                    jsonCliente.put("foto", resultado.getString("foto"));
                    jsonCliente.put("ineFrontal", resultado.getString("ineFrontal"));
                    jsonCliente.put("ineTrasera", resultado.getString("ineTrasera"));
                    jsonCliente.put("vacinacion", resultado.getString("vacunacion_covid"));
                    jsonCliente.put("idDocumentacion", resultado.getInt("idDocumentacion"));
                    jsonSolicitudes.add(jsonCliente);
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
        JSONArray jsonSolicitudes = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement solicitudes = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM profesionales INNER JOIN documentacionprofesional WHERE documentacionprofesional.profesionales_idprofesionales = profesionales.idprofesionales AND profesionales.estado = ? AND documentacionprofesional.operadores_idoperadores IS NULL");
                solicitudes.setString(1, "DOCUMENTACION");
                ResultSet resultado = solicitudes.executeQuery();
                while(resultado.next()){
                    JSONObject jsonProf = new JSONObject();
                    jsonProf.put("nombre", resultado.getString("nombre"));
                    jsonProf.put("correo", resultado.getString("correo"));
                    jsonProf.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro").toString());
                    jsonProf.put("foto", resultado.getString("foto"));
                    jsonProf.put("acta", resultado.getString("acta"));
                    jsonProf.put("antecedentes", resultado.getString("antecedentes"));
                    jsonProf.put("certificado1", resultado.getString("certificado1"));
                    jsonProf.put("certificado2", resultado.getString("certificado2"));
                    jsonProf.put("certificado3", resultado.getString("certificado3"));
                    jsonProf.put("ine", resultado.getString("ine"));
                    jsonProf.put("idDocumentacion", resultado.getInt("idDocumentacion"));
                    jsonProf.put("vacunacion", resultado.getString("vacunacion_covid"));
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
        JSONObject ticketJSON = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement solucionado = DBController.getInstance().getConnection().prepareStatement("SELECT estatus FROM tickets_ayuda WHERE idtickets_ayuda = ?");
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
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement mensaje = DBController.getInstance().getConnection().prepareStatement("INSERT INTO msg_tickets (mensaje, idUsuario_Operador, operadorBool, tickets_ayuda_idtickets_ayuda) VALUES (?,?,?,?)");
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
        JSONArray mensajes = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                String query = "";
                String tipo = "";
                if(tipoUsuario == Constantes.TIPO_USUARIO_CLIENTE)
                    tipo = "CLIENTE";//query = "SELECT * FROM msg_tickets INNER JOIN clientes WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND clientes.idclientes = msg_tickets.idUsuario_Operador";
                else if(tipoUsuario == Constantes.TIPO_USUARIO_PROFESIONAL)
                    tipo = "PROFESIONAL";//query = "SELECT * FROM msg_tickets INNER JOIN profesionales WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND profesionales.idprofesionales = msg_tickets.idUsuario_Operador";
             
                PreparedStatement mensajesConsulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM msg_tickets WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ?");
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
        JSONArray jsonFinalizados = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement finalizados = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM tickets_ayuda WHERE operadores_idoperadores = ? AND estatus = ? AND categoria = ?");
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
                    ticket.put("fechaCreacion", resultado.getDate("fechaCreacion").toString());
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
        JSONArray jsonAsociados = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement asociados = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM tickets_ayuda WHERE operadores_idoperadores = ? AND estatus = ? AND categoria = ?");
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
                    jsonObject.put("fechaCreacion", resultado.getDate("fechaCreacion").toString());
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
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE tickets_ayuda SET operadores_idoperadores = ?, estatus = ? WHERE idtickets_ayuda = ?");
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
        JSONObject ticketInfo = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                String consulta = "";
                if(tipoUsuario == Constantes.TIPO_USUARIO_CLIENTE)
                    consulta = "SELECT * FROM tickets_ayuda INNER JOIN clientes WHERE tickets_ayuda.idtickets_ayuda = ? AND clientes.idclientes = tickets_ayuda.idUsuario";
                else if(tipoUsuario == Constantes.TIPO_USUARIO_PROFESIONAL)
                    consulta = "SELECT * FROM tickets_ayuda INNER JOIN profesionales WHERE tickets_ayuda.idtickets_ayuda = ? AND profesionales.idprofesionales = tickets_ayuda.idUsuario";
                
                
                PreparedStatement ticket = DBController.getInstance().getConnection().prepareStatement(consulta);
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
                    ticketInfo.put("fechaCreacion", resultado.getDate("fechaCreacion").toString());
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
        JSONArray tickets = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement ticketsConsulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM tickets_ayuda WHERE estatus = ? AND categoria = ?");
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
                    jsonObject.put("fechaCreacion", resultado.getDate("fechaCreacion").toString());
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
        JSONArray mensajesArray = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                /*Consultar Ticket*/
                PreparedStatement ticket = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM tickets_ayuda WHERE idtickets_ayuda = ? AND tipoUsuario = ?");
                ticket.setInt(1, idTicket);
                ticket.setInt(2, tipoUsuario);
                ResultSet ticketRes = ticket.executeQuery();
                JSONObject jsonObjectTicket = new JSONObject();
                
                if(ticketRes.next()){//Datos ticket
                    jsonObjectTicket.put("topico", ticketRes.getString("topico"));
                    jsonObjectTicket.put("descripcion", ticketRes.getString("descripcion"));
                    jsonObjectTicket.put("fechaCreacion", ticketRes.getDate("fechaCreacion").toString());
                    jsonObjectTicket.put("estatus", ticketRes.getInt("estatus"));
                    jsonObjectTicket.put("operador", ticketRes.getInt("operadores_idoperadores"));
                    JSONObject jsonTicket = new JSONObject();
                    jsonTicket.put("ticket", jsonObjectTicket);
                    mensajesArray.add(jsonTicket);
                }
                
                /*Consulta de mensajes Ticket*/
                String query = "";
                String tipo = "";
                if(tipoUsuario == Constantes.TIPO_USUARIO_CLIENTE)
                    tipo = "CLIENTE";//query = "SELECT * FROM msg_tickets INNER JOIN clientes WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND clientes.idclientes = msg_tickets.idUsuario_Operador";
                else if(tipoUsuario == Constantes.TIPO_USUARIO_PROFESIONAL)
                    tipo = "PROFESIONAL";//query = "SELECT * FROM msg_tickets INNER JOIN profesionales WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ? AND profesionales.idprofesionales = msg_tickets.idUsuario_Operador";
                
                PreparedStatement mensajes = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM msg_tickets WHERE msg_tickets.tickets_ayuda_idtickets_ayuda = ?");
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
        JSONObject jsonAdmin = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM operadores WHERE BINARY usuario = ? AND BINARY contrasena = ?");
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
