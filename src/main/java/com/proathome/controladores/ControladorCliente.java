package com.proathome.controladores;

import com.mysql.cj.MysqlConnection;
import com.proathome.modelos.Cliente;
import com.proathome.modelos.Constantes;
import com.proathome.modelos.CuentaBancaria;
import com.proathome.modelos.EvaluacionCliente;
import com.proathome.modelos.Sesion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import com.proathome.mysql.ConexionMySQL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class ControladorCliente {

    /*
    *
    *Clase controladora de Clientes.
    *
     */
    private Cliente cliente = new Cliente();
    private Sesion sesion = new Sesion();
    private boolean clienteRegistrado = false;
    
    public JSONObject getDatosFiscales(int idEstudiante){
        JSONObject respuesta = new JSONObject();
        JSONObject datos = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM datosfiscalesestudiantes WHERE clientes_idclientes = ?");
                consulta.setInt(1, idEstudiante);
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
    
    public JSONObject guardarDatosFiscales(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            try{
                //Consultar si hay registro.
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM datosfiscalesestudiantes WHERE clientes_idclientes = ?");
                consulta.setInt(1, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    //Actualizamos
                    PreparedStatement actualizar = conectar.prepareStatement("UPDATE datosfiscalesestudiantes SET tipoPersona = ?, razonSocial = ?, rfc = ?, cfdi = ? WHERE clientes_idclientes = ?");
                    actualizar.setString(1, jsonDatos.get("tipoPersona").toString());
                    actualizar.setString(2, jsonDatos.get("razonSocial").toString());
                    actualizar.setString(3, jsonDatos.get("rfc").toString());
                    actualizar.setString(4, jsonDatos.get("cfdi").toString());
                    actualizar.setInt(5, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
                    actualizar.execute();
                }else{
                    //Guardamos
                    PreparedStatement actualizar = conectar.prepareStatement("INSERT INTO datosfiscalesestudiantes (tipoPersona, razonSocial, rfc, cfdi, clientes_idclientes) VALUES (?,?,?,?,?)");
                    actualizar.setString(1, jsonDatos.get("tipoPersona").toString());
                    actualizar.setString(2, jsonDatos.get("razonSocial").toString());
                    actualizar.setString(3, jsonDatos.get("rfc").toString());
                    actualizar.setString(4, jsonDatos.get("cfdi").toString());
                    actualizar.setInt(5, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
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
    
    public void bloquearPerfil(int idEstudiante, Connection conectar){
        try{
            PreparedStatement bloquear = conectar.prepareStatement("UPDATE clientes SET estado = ? WHERE idclientes = ?");
            bloquear.setString(1, "BLOQUEADO");
            bloquear.setInt(2, idEstudiante);
            bloquear.execute();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public JSONObject getReportes(int idEstudiante){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement reportes = conectar.prepareStatement("SELECT COUNT(*) AS num FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
                reportes.setInt(1, idEstudiante);
                reportes.setString(2, "ESTUDIANTE");
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
                            bloquearPerfil(idEstudiante, conectar);*/
                        PreparedStatement descripcion = conectar.prepareStatement("SELECT * FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
                        descripcion.setInt(1, idEstudiante);
                        descripcion.setString(2, "ESTUDIANTE");
                        ResultSet resultadoDesc = descripcion.executeQuery();
                        if(resultadoDesc.next()){
                            String desText = resultadoDesc.getString("descripcion");
                            JSONObject mensaje = new JSONObject();
                            mensaje.put("reportes", numReportes);
                            mensaje.put("aviso", desText);
                            respuesta.put("respuesta", true);
                            respuesta.put("mensaje", mensaje);
                        }else{
                            respuesta.put("respuesta", false);
                            respuesta.put("mensaje", "Error en la conexión a BD");
                        }
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
    
    public JSONObject estatusDocumentos(int idCliente){
        Connection conectar = ConexionMySQL.connection();
        JSONObject estatus = new JSONObject();
        
        if(conectar != null){
            try{
                PreparedStatement documentos = conectar.prepareStatement("SELECT * FROM documentacioncliente WHERE clientes_idclientes = ?");
                documentos.setInt(1, idCliente  );
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
                consulta.setInt(2, Constantes.TIPO_USUARIO_ESTUDIANTE);
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
    
    public void saldarDeuda(JSONObject jsonDatos){
       
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement saldarDeuda = conectar.prepareStatement("UPDATE pagos SET estatusPago = ? WHERE idSesion = ?");
                saldarDeuda.setString(1, "Pagado");
                saldarDeuda.setInt(2, Integer.parseInt(jsonDatos.get("idSesion").toString()));
                saldarDeuda.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en saldarDeuda.");
        }
        
    }
    
    public JSONObject bloquearPerfil(int idEstudiante){
        
        Connection conectar = ConexionMySQL.connection();
        JSONObject bloquearJSON = new JSONObject();
        boolean bloquear = false;
        if(conectar != null){
            try{
                //Recorrer cada sesion.
                PreparedStatement sesiones = conectar.prepareStatement("SELECT * FROM sesiones INNER JOIN pagos WHERE sesiones.clientes_idclientes = ? AND pagos.idSesion = sesiones.idsesiones");
                sesiones.setInt(1, idEstudiante);
                ResultSet resultado = sesiones.executeQuery();
                
                while(resultado.next()){
                    if(resultado.getBoolean("finalizado") && resultado.getString("estatusPago").equalsIgnoreCase("Deuda")){
                        bloquear = true;
                        bloquearJSON.put("bloquear", bloquear);
                        bloquearJSON.put("idSesion", resultado.getInt("idSesion"));
                        bloquearJSON.put("deuda", resultado.getDouble("costoClase") + resultado.getDouble("costoTE"));
                        bloquearJSON.put("idSeccion", resultado.getInt("idSeccion"));
                        bloquearJSON.put("idNivel", resultado.getInt("idNivel"));
                        bloquearJSON.put("idBloque", resultado.getInt("idBloque"));
                        bloquearJSON.put("lugar", resultado.getString("lugar"));
                    }
                }
                
                PreparedStatement estudiante = conectar.prepareStatement("SELECT * FROM clientes WHERE idclientes = ?");
                estudiante.setInt(1, idEstudiante);
                ResultSet resultadoEst = estudiante.executeQuery();
                
                if(resultadoEst.next()){
                    bloquearJSON.put("nombre", resultadoEst.getString("nombre"));
                    bloquearJSON.put("correo", resultadoEst.getString("correo"));
                }
                
                bloquearJSON.put("bloquear", bloquear);
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en bloquearPerfil.");
        }
        
        return bloquearJSON;
    }
    
    public JSONObject validarValoracion(int idSesion, int idProfesor){
    
       Connection conectar = ConexionMySQL.connection();
       JSONObject jsonRespuesta = new JSONObject();
       if(conectar != null){
           try{
               PreparedStatement validar = conectar.prepareStatement("SELECT * FROM valoracionprofesor WHERE sesiones_idsesiones = ? AND profesores_idprofesores = ?");
               validar.setInt(1, idSesion);
               validar.setInt(2, idProfesor);
               ResultSet resultado = validar.executeQuery();
               
               if(resultado.next()){
                   jsonRespuesta.put("valorado", true);
               }else{
                   jsonRespuesta.put("valorado", false);
               }
           }catch(SQLException ex){
               ex.printStackTrace();
           }
       }else{
           System.out.println("Error en validarValoracion.");
       }
       
       return jsonRespuesta;
       
    }
    
    public void valorarProfesor(JSONObject jsonDatos){
        
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement valorar = conectar.prepareStatement("INSERT INTO valoracionprofesor (profesores_idprofesores, clientes_idclientes, valoracion, comentario, sesiones_idsesiones) VALUES (?,?,?,?,?)");
                valorar.setInt(1, Integer.parseInt(jsonDatos.get("idProfesor").toString()));
                valorar.setInt(2, Integer.parseInt(jsonDatos.get("idEstudiante").toString()));
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
    
    public JSONArray obtenerValoracion(int idProfesor){
        
        Connection conectar = ConexionMySQL.connection();
        JSONObject profesorJSON = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if(conectar != null){
            //Obtener la info del profesor.
            try{
                PreparedStatement profesor = conectar.prepareStatement("SELECT * FROM profesores WHERE idprofesores = ?");
                profesor.setInt(1, idProfesor);
                ResultSet resultado = profesor.executeQuery();
                
                if(resultado.next()){
                    //Obtener promedio
                    profesorJSON.put("idProfesor", resultado.getInt("idprofesores"));
                    profesorJSON.put("nombre", resultado.getString("nombre"));
                    profesorJSON.put("correo", resultado.getString("correo"));
                    profesorJSON.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro"));
                    profesorJSON.put("foto", resultado.getString("foto"));
                    profesorJSON.put("descripcion", resultado.getString("descripcion"));
                    profesorJSON.put("valoraciones", false);
                    jsonArray.add(profesorJSON);
                    
                    //Obtener Valoraciones
                    PreparedStatement valoraciones = conectar.prepareStatement("SELECT * FROM valoracionprofesor WHERE profesores_idprofesores = ?");
                    valoraciones.setInt(1, idProfesor);
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
    
    public void finalizarPlan(int idEstudiante){
    
        boolean finalizar = true;
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement verificar = conectar.prepareStatement("SELECT * FROM sesiones WHERE NOT tipoPlan = ? AND clientes_idclientes = ?");
                verificar.setString(1, "PARTICULAR");
                verificar.setInt(2, idEstudiante);
                ResultSet resultadoVerificar = verificar.executeQuery();
                
                while(resultadoVerificar.next()){
                    if(!resultadoVerificar.getBoolean("finalizado"))
                        finalizar = false;
                }
                
                if(finalizar){
                    System.out.println("Plan finalizado");
                    PreparedStatement finalizar2 = conectar.prepareStatement("UPDATE planes SET tipoPlan = ?, fechaInicio = ?, fechaFin = ?, monedero = ? WHERE clientes_idclientes = ?");
                    finalizar2.setString(1, "PARTICULAR");
                    finalizar2.setDate(2, null);
                    finalizar2.setDate(3, null);
                    finalizar2.setInt(4, 0);
                    finalizar2.setInt(5, idEstudiante);
                    finalizar2.execute();
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en finalizarPlan.");
        }
    
    }
    
    public void actualizarMonedero(JSONObject jsonMonedero){

        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement actualizar = conectar.prepareStatement("UPDATE planes SET monedero = ? WHERE clientes_idclientes = ?");
                actualizar.setInt(1, Integer.parseInt(jsonMonedero.get("nuevoMonedero").toString()));
                actualizar.setInt(2, Integer.parseInt(jsonMonedero.get("idEstudiante").toString()));
                actualizar.execute();
                
                //Validar plan.
                PreparedStatement planActivo = conectar.prepareStatement("SELECT * FROM planes WHERE clientes_idclientes = ?");
                planActivo.setInt(1, Integer.parseInt(jsonMonedero.get("idEstudiante").toString()));
                ResultSet resultadoPlanActivo = planActivo.executeQuery();
                
                if(resultadoPlanActivo.next()){
                    //¿Hay plan activo?
                    if(!resultadoPlanActivo.getString("tipoPlan").equalsIgnoreCase("PARTICULAR")){
                         
                        //¿Hay horas en el monedero en el PLAN activo?
                        if(resultadoPlanActivo.getInt("monedero") != 0){
                            //¿El plan ya expiró?
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Calendar calendar = Calendar.getInstance();
                            String fechaHoy = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                            
                            Date fechaActual = sdf.parse(fechaHoy);
                            Date fechaFin = sdf.parse(resultadoPlanActivo.getDate("fechaFin").toString());
                            if(fechaFin.equals(fechaActual) || fechaActual.after(fechaFin)){
                                //Finalizar plan activo y pasarlo a PARTICULAR
                                finalizarPlan(Integer.parseInt(jsonMonedero.get("idEstudiante").toString()));
                            }
                        }else{
                            //Finalizar plan activo y pasarlo a PARTICULAR
                            finalizarPlan(Integer.parseInt(jsonMonedero.get("idEstudiante").toString()));
                        }
                    }
                    
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }catch(java.text.ParseException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en actualizarMonedero.");
        }
        
    }
    
    public JSONObject verificarPlan(int idEstudiante){
    
        Connection conectar = ConexionMySQL.connection();
        JSONObject jsonPlan = new JSONObject();
        if(conectar != null){
            try{
                
                PreparedStatement planActivo = conectar.prepareStatement("SELECT * FROM planes WHERE clientes_idclientes = ?");
                planActivo.setInt(1, idEstudiante);
                ResultSet resultadoPlanActivo = planActivo.executeQuery();
                
                if(resultadoPlanActivo.next()){
                    //¿Hay plan activo?
                    if(resultadoPlanActivo.getString("tipoPlan").equalsIgnoreCase("PARTICULAR")){
                         jsonPlan.put("tipoPlan", resultadoPlanActivo.getString("tipoPlan"));
                         jsonPlan.put("monedero", resultadoPlanActivo.getInt("monedero"));
                         jsonPlan.put("fechaInicio", resultadoPlanActivo.getDate("fechaInicio"));
                         jsonPlan.put("fechaFin", resultadoPlanActivo.getDate("fechaFin"));
                    }else{//Si
                        //¿Hay horas en el monedero en el PLAN activo?
                        if(resultadoPlanActivo.getInt("monedero") != 0){
                            //¿El plan ya expiró?
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Calendar calendar = Calendar.getInstance();
                            String fechaHoy = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                            
                            Date fechaActual = sdf.parse(fechaHoy);
                            Date fechaFin = sdf.parse(resultadoPlanActivo.getDate("fechaFin").toString());
                            if(fechaFin.equals(fechaActual) || fechaActual.after(fechaFin)){
                                //Finalizar plan activo y pasarlo a PARTICULAR
                                finalizarPlan(idEstudiante);
                            }else{
                                jsonPlan.put("tipoPlan", resultadoPlanActivo.getString("tipoPlan"));
                                jsonPlan.put("monedero", resultadoPlanActivo.getInt("monedero"));
                                jsonPlan.put("fechaInicio", resultadoPlanActivo.getDate("fechaInicio"));
                                jsonPlan.put("fechaFin", resultadoPlanActivo.getDate("fechaFin"));
                            }
                        }else{
                            //Finalizar plan activo y pasarlo a PARTICULAR
                            finalizarPlan(idEstudiante);
                        }
                    }
                    
                }
                
                Connection conectar2 = ConexionMySQL.connection();
                if(conectar2 != null){
                    PreparedStatement planActivo2 = conectar2.prepareStatement("SELECT * FROM planes WHERE clientes_idclientes = ?");
                    planActivo2.setInt(1, idEstudiante);
                    ResultSet resultadoPlanActivo2 = planActivo2.executeQuery();
                    if(resultadoPlanActivo2.next()){
                        jsonPlan.put("tipoPlan", resultadoPlanActivo2.getString("tipoPlan"));
                        jsonPlan.put("monedero", resultadoPlanActivo2.getInt("monedero"));
                        jsonPlan.put("fechaInicio", resultadoPlanActivo2.getDate("fechaInicio"));
                        jsonPlan.put("fechaFin", resultadoPlanActivo2.getDate("fechaFin"));
                    }
                }else{
                    jsonPlan.put("tipoPlan", "Error");
                    jsonPlan.put("monedero", 0);
                }
                
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }catch(java.text.ParseException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en verificarPlan.");
        }
        System.out.println("Va: " + jsonPlan);
        return jsonPlan;
    
    }
    
    public void generarPlan(JSONObject jsonPlan){
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                //Actualizar plan en el perfil.
                System.out.println("Etnro generar");
                PreparedStatement actualizarPlan = conectar.prepareStatement("UPDATE planes SET tipoPlan = ?, fechaInicio = ?, fechaFin = ?, monedero = ? WHERE clientes_idclientes = ?");
                actualizarPlan.setString(1, jsonPlan.get("tipoPlan").toString());
                actualizarPlan.setDate(2, java.sql.Date.valueOf(jsonPlan.get("fechaInicio").toString()));
                actualizarPlan.setDate(3, java.sql.Date.valueOf(jsonPlan.get("fechaFin").toString()));
                actualizarPlan.setInt(4, Integer.parseInt(jsonPlan.get("monedero").toString()));
                actualizarPlan.setInt(5, Integer.parseInt(jsonPlan.get("idEstudiante").toString()));
                actualizarPlan.execute();
                //Guradar historial.
                PreparedStatement historial = conectar.prepareStatement("INSERT INTO historialPlanes (tipoPlan, fechaInicio, fechaFin, monedero, clientes_idclientes) VALUES (?,?,?,?,?)");
                historial.setString(1, jsonPlan.get("tipoPlan").toString());
                historial.setDate(2, java.sql.Date.valueOf(jsonPlan.get("fechaInicio").toString()));
                historial.setDate(3, java.sql.Date.valueOf(jsonPlan.get("fechaFin").toString()));
                historial.setInt(4, Integer.parseInt(jsonPlan.get("monedero").toString()));
                historial.setInt(5, Integer.parseInt(jsonPlan.get("idEstudiante").toString()));
                historial.execute();
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en generar Plan.");
        }
    }
    
    public JSONObject verificarSesionesPagadas(int idEstudiante){
    
        boolean plan_activo = false;
        boolean sesiones_pagadas_finalizadas = true;
        JSONObject jsonDatos = new JSONObject();
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
        
            try{
                
                PreparedStatement contador = conectar.prepareStatement("SELECT COUNT(*) AS numero FROM sesiones INNER JOIN pagos WHERE pagos.idSesion = sesiones.idsesiones AND sesiones.clientes_idclientes = ?");
                contador.setInt(1, idEstudiante);
                ResultSet resultadoContador = contador.executeQuery();
                
                if(resultadoContador.next()){
                    if(resultadoContador.getInt("numero") < 3){
                        plan_activo = false;
                        sesiones_pagadas_finalizadas = false;
                    }else{
                        //Contar las sesiones que tenemos y ver que todas esten finalizadas o valen vrg
                        PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM sesiones WHERE clientes_idclientes = ?");
                        consulta.setInt(1, idEstudiante);
                        ResultSet resultado = consulta.executeQuery();

                        while(resultado.next()){
                            if(!resultado.getBoolean("finalizado")){
                                sesiones_pagadas_finalizadas = false;
                            }
                        }

                        //Aquí obtenemos el tipo PLAN y si está activo.
                        PreparedStatement plan = conectar.prepareStatement("SELECT tipoPlan FROM planes WHERE clientes_idclientes = ?");
                        plan.setInt(1, idEstudiante);
                        ResultSet resultadoPlan = plan.executeQuery();

                        if(resultadoPlan.next()){
                            if(!resultadoPlan.getString("tipoPlan").equalsIgnoreCase("PARTICULAR"))
                                plan_activo = true;
                        }else{
                            plan_activo = false;
                        }
                    }
                }else{
                    plan_activo = false;
                    sesiones_pagadas_finalizadas = false;
                }
                
                jsonDatos.put("plan_activo", plan_activo);
                jsonDatos.put("sesiones_pagadas_finalizadas", sesiones_pagadas_finalizadas);
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            System.out.println("Error en verificarSesionesPagadas.");
        }
        
        return jsonDatos;
        
    }
    
    public void iniciarPlan(JSONObject json){
    
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                PreparedStatement consultar = conectar.prepareStatement("SELECT * FROM planes WHERE clientes_idclientes = ?");
                consultar.setInt(1, Integer.parseInt(json.get("idEstudiante").toString()));
                ResultSet resultado = consultar.executeQuery();
                
                if(resultado.next()){
                }else{
                    PreparedStatement plan = conectar.prepareStatement("INSERT INTO planes (clientes_idclientes) VALUES (?)");
                    plan.setInt(1, Integer.parseInt(json.get("idEstudiante").toString()));
                    plan.execute();
                
                }
            
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en iniciarPlan.");
        }
        
    }
    
    public void actualizarPago(JSONObject json){
    
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
        
            try{
                if(json.get("tipoPlan").toString().equalsIgnoreCase("PARTICULAR")){
                    PreparedStatement actualizar = conectar.prepareStatement("UPDATE pagos SET costoClase = ?, costoTE = ?, estatusPago = ? WHERE idEstudiante = ? AND idSesion = ?");
                    actualizar.setDouble(1, Double.parseDouble(json.get("costoClase").toString()));
                    actualizar.setDouble(2, Double.parseDouble(json.get("costoTE").toString()));
                    actualizar.setString(3, json.get("estatusPago").toString());
                    actualizar.setInt(4, Integer.parseInt(json.get("idEstudiante").toString()));
                    actualizar.setInt(5, Integer.parseInt(json.get("idSesion").toString()));
                    actualizar.execute();
                }else{
                    PreparedStatement actualizar = conectar.prepareStatement("INSERT INTO pagos (token, costoClase, costoTE, estatusPago, idEstudiante, idSesion) VALUES (?,?,?,?,?,?)");
                    actualizar.setString(1, "TIPO PLAN - " + json.get("tipoPlan").toString());
                    actualizar.setDouble(2, Double.parseDouble(json.get("costoClase").toString()));
                    actualizar.setDouble(3, Double.parseDouble(json.get("costoTE").toString()));
                    actualizar.setString(4, json.get("estatusPago").toString());
                    actualizar.setInt(5, Integer.parseInt(json.get("idEstudiante").toString()));
                    actualizar.setInt(6, Integer.parseInt(json.get("idSesion").toString()));
                    actualizar.execute();
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            System.out.println("Error en actualizarPago.");
        }
        
    }
    
    public JSONObject obtenerToken(int idSesion, int idEstudiante){
    
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        
        if(conectar != null){
        
            try{
                PreparedStatement consulta = conectar.prepareStatement("SELECT token FROM pagos WHERE idSesion = ? AND idEstudiante = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idEstudiante);
                ResultSet resultado = consulta.executeQuery();
                if(resultado.next()){
                    respuesta.put("token", resultado.getString("token"));
                }else{
                    respuesta.put("token", null);
                }
        
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            System.out.println("Error en obtenerToken.");
        }
        
        return respuesta;
    
    }
    
    public void actualizarToken(JSONObject jsonToken){
    
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
        
            try{
                
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM pagos WHERE idSesion = ? AND idEstudiante = ?");
                consulta.setInt(1, Integer.parseInt(jsonToken.get("idSesion").toString()));
                consulta.setInt(2, Integer.parseInt(jsonToken.get("idEstudiante").toString()));
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                
                    PreparedStatement actualizar = conectar.prepareStatement("UPDATE pagos SET token = ? WHERE idEstudiante = ? AND  idSesion = ?");
                    actualizar.setString(1, jsonToken.get("token").toString());
                    actualizar.setInt(2, Integer.parseInt(jsonToken.get("idEstudiante").toString()));
                    actualizar.setInt(3, Integer.parseInt(jsonToken.get("idSesion").toString()));
                    actualizar.execute();
                    
                }else{
                
                    PreparedStatement actualizar = conectar.prepareStatement("INSERT INTO pagos (token, idEstudiante, idSesion) VALUES (?,?,?)");
                    actualizar.setString(1, jsonToken.get("token").toString());
                    actualizar.setInt(2, Integer.parseInt(jsonToken.get("idEstudiante").toString()));
                    actualizar.setInt(3, Integer.parseInt(jsonToken.get("idSesion").toString()));
                    actualizar.execute();
                
                }
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        
        }else{
            System.out.println("Error en actualizarToken.");
        }
        
    }
    
    public JSONObject obtenerPreOrden(int idEstudiante, int idSesion){
        Connection conectar = ConexionMySQL.connection();
        JSONObject respuesta = new JSONObject();
        if(conectar != null){
            try{
                PreparedStatement consultaSesion = conectar.prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ?");
                consultaSesion.setInt(1, idSesion);
                ResultSet resultadoSesiones = consultaSesion.executeQuery();
                
                if(resultadoSesiones.next()){   
                    
                    PreparedStatement consultaEstudiante = conectar.prepareStatement("SELECT * FROM clientes WHERE idclientes = ?");
                    consultaEstudiante.setInt(1, idEstudiante);
                    ResultSet resultadoEstudiante = consultaEstudiante.executeQuery();
                        
                    if(resultadoEstudiante.next()){

                        PreparedStatement consultaDatosBancarios = conectar.prepareStatement("SELECT * FROM datosbancariosclientes WHERE clientes_idclientes = ?");
                        consultaDatosBancarios.setInt(1, idEstudiante);
                        ResultSet resultadoDatosBancarios = consultaDatosBancarios.executeQuery();

                        if(resultadoDatosBancarios.next()){
                            respuesta.put("idEstudiante", idEstudiante);
                            respuesta.put("idSesion", idSesion);
                            respuesta.put("nombreEstudiante", resultadoEstudiante.getString("nombre"));
                            respuesta.put("correo", resultadoEstudiante.getString("correo"));
                            respuesta.put("nombreTitular", resultadoDatosBancarios.getString("nombreTitular"));
                            respuesta.put("tarjeta", resultadoDatosBancarios.getString("tarjeta"));
                            respuesta.put("mes", resultadoDatosBancarios.getString("mes"));
                            respuesta.put("ano", resultadoDatosBancarios.getInt("ano"));
                            respuesta.put("idSeccion", resultadoSesiones.getInt("idSeccion"));
                            respuesta.put("idNivel", resultadoSesiones.getInt("idNivel"));
                            respuesta.put("idBloque", resultadoSesiones.getInt("idBloque"));
                            respuesta.put("tiempo", resultadoSesiones.getInt("tiempo"));
                        }else{
                            respuesta.put("idEstudiante", idEstudiante);
                            respuesta.put("idSesion", idSesion);
                            respuesta.put("nombreEstudiante", resultadoEstudiante.getString("nombre"));
                            respuesta.put("correo", resultadoEstudiante.getString("correo"));
                            respuesta.put("nombreTitular", "");
                            respuesta.put("tarjeta", "");
                            respuesta.put("mes", "");
                            respuesta.put("ano", "");
                            respuesta.put("idSeccion", resultadoSesiones.getInt("idSeccion"));
                            respuesta.put("idNivel", resultadoSesiones.getInt("idNivel"));
                            respuesta.put("idBloque", resultadoSesiones.getInt("idBloque"));
                            respuesta.put("tiempo", resultadoSesiones.getInt("tiempo"));
                        }

                    }else{  

                        respuesta.put("error", "Error en los datos de Sesión.");

                    }
                
                }else{
                    respuesta.put("error", "Error en los datos de Sesión.");
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error obtenerPreOrden.");
        }
        
        return respuesta;
    }
    
    public void iniciarProcesoRuta(JSONObject json){
    
        Connection conectar = ConexionMySQL.connection();
        if(conectar != null){
            try{
                
                PreparedStatement consulta = conectar.prepareStatement("SELECT * FROM rutaaprendizaje WHERE clientes_idclientes = ?");
                consulta.setInt(1, Integer.valueOf(json.get("idEstudiante").toString()));
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                }else{
                    PreparedStatement iniciar = conectar.prepareStatement("INSERT INTO rutaaprendizaje (clientes_idclientes, idBloque, idNivel, idSeccion, horas, fecha_registro, enruta) VALUES (?,?,?,?,?,?,?)");
                    iniciar.setInt(1, Integer.parseInt(json.get("idEstudiante").toString()));
                    iniciar.setInt(2, 1);
                    iniciar.setInt(3, 1);
                    iniciar.setInt(4, 1);
                    iniciar.setInt(5, 0);
                    iniciar.setString(6, "Hoy");
                    iniciar.setBoolean(7, true);
                    iniciar.execute();
                }
               
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en iniciarProcesoRuta.");
        }
        
    }
    
    public void nuevaSesion(JSONObject datos){
        
        sesion.setClientes_idclientes(Integer.parseInt(String.valueOf(datos.get("idCliente"))));
        sesion.setHorario(String.valueOf(datos.get("horario")));
        sesion.setLugar(String.valueOf(datos.get("lugar")));
        sesion.setTiempo(Integer.parseInt(datos.get("tiempo").toString()));
        sesion.setIdSeccion(Integer.parseInt(datos.get("idSeccion").toString()));
        sesion.setIdNivel(Integer.parseInt(datos.get("idNivel").toString()));
        sesion.setIdBloque(Integer.parseInt(datos.get("idBloque").toString()));
        sesion.setExtras(String.valueOf(datos.get("extras")));
        sesion.setTipoClase(String.valueOf(datos.get("tipoClase")));
        sesion.setLatitud(Double.valueOf(String.valueOf(datos.get("latitud"))));
        sesion.setLongitud(Double.valueOf(String.valueOf(datos.get("longitud"))));
        sesion.setFecha(datos.get("fecha").toString());
        sesion.setActualizado(datos.get("actualizado").toString());
        sesion.setSumar(Boolean.valueOf(datos.get("sumar").toString()));
        sesion.setTipoPlan(datos.get("tipoPlan").toString());
        sesion.setPersonas(Integer.parseInt(datos.get("personas").toString()));
        
    }//Fin método nuevaSesion.
    
    public JSONObject detallesSesion(int idSesion){
      
        Connection conectar = ConexionMySQL.connection();
        JSONObject jsonDetalles = new JSONObject();
        if(conectar != null){
            
            try{
                
                PreparedStatement detalles = conectar.prepareStatement("SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.clientes_idclientes = clientes.idclientes AND idsesiones = ?");
                detalles.setInt(1 , idSesion);
                ResultSet resultado = detalles.executeQuery();
                
                if(resultado.next()){
                    
                    if(resultado.getString("profesores_idprofesores") == null){
                        jsonDetalles.put("profesor", "Sin profesor asignado.");
                        jsonDetalles.put("fotoProfesor", "Sin foto");
                        jsonDetalles.put("descripcionProfesor", "Sin descripcion");
                        jsonDetalles.put("correoProfesor", "Sin correo");
                        jsonDetalles.put("idProfesor", 0);
                    }else{
                        PreparedStatement profesor = conectar.prepareStatement("SELECT * FROM profesores WHERE idprofesores = ?");
                        profesor.setInt(1 , resultado.getInt("profesores_idprofesores"));
                        ResultSet resultadoProfesor = profesor.executeQuery();
                        
                        if(resultadoProfesor.next()){
                            jsonDetalles.put("profesor", resultadoProfesor.getString("nombre"));
                            jsonDetalles.put("fotoProfesor", resultadoProfesor.getString("foto"));
                            jsonDetalles.put("descripcionProfesor", resultadoProfesor.getString("descripcion"));
                            jsonDetalles.put("correoProfesor", resultadoProfesor.getString("correo"));
                            jsonDetalles.put("idProfesor", resultadoProfesor.getInt("idprofesores"));
                        }
                    }
                    
                    jsonDetalles.put("idSesion", resultado.getString("clientes_idclientes"));
                    jsonDetalles.put("horario", resultado.getString("horario"));
                    jsonDetalles.put("lugar", resultado.getString("lugar"));
                    jsonDetalles.put("tiempo", resultado.getInt("tiempo"));
                    jsonDetalles.put("idSeccion", resultado.getInt("idSeccion"));
                    jsonDetalles.put("idNivel", resultado.getInt("idNivel"));
                    jsonDetalles.put("idBloque", resultado.getInt("idBloque"));
                    jsonDetalles.put("extras", resultado.getString("extras"));
                    jsonDetalles.put("tipoClase", resultado.getString("tipoClase"));
                    jsonDetalles.put("latitud", resultado.getDouble("latitud"));
                    jsonDetalles.put("longitud", resultado.getDouble("longitud"));
                    jsonDetalles.put("actualizado", resultado.getDate("actualizado"));
                    jsonDetalles.put("nombre", resultado.getString("nombre"));
                    jsonDetalles.put("fecha", resultado.getString("fecha"));
                    jsonDetalles.put("correo", resultado.getString("correo"));
                    jsonDetalles.put("foto", resultado.getString("foto"));
                    jsonDetalles.put("descripcion", resultado.getString("descripcion"));
                    
                }
               
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
        
            System.out.println("Error en detallesSesion.");
            
        }
        
        return jsonDetalles;
        
    }//Fin métod detallesSesion.
    
    public void guardarSesion(){
        
         Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){        
            try{          
                String query = "INSERT INTO sesiones (clientes_idclientes, horario, lugar, tiempo, extras, tipoClase, latitud, longitud, actualizado, idSeccion, idNivel, idBloque, fecha, progreso, sumar, tipoPlan, personas) "
                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setInt(1, sesion.getClientes_idclientes());
                agregarDatos.setString(2, sesion.getHorario());
                agregarDatos.setString(3, sesion.getLugar());
                agregarDatos.setInt(4, sesion.getTiempo());
                agregarDatos.setString(5, sesion.getExtras());
                agregarDatos.setString(6, sesion.getTipoClase());
                agregarDatos.setDouble(7, sesion.getLatitud());
                agregarDatos.setDouble(8, sesion.getLongitud());
                agregarDatos.setString(9, sesion.getActualizado());
                agregarDatos.setInt(10, sesion.getIdSeccion());
                agregarDatos.setInt(11, sesion.getIdNivel());
                agregarDatos.setInt(12, sesion.getIdBloque());
                agregarDatos.setString(13, sesion.getFecha());
                agregarDatos.setInt(14, sesion.getTiempo());
                agregarDatos.setBoolean(15, sesion.getSumar());
                agregarDatos.setString(16, sesion.getTipoPlan());
                agregarDatos.setInt(17, sesion.getPersonas());
                agregarDatos.execute();
   
            }catch(SQLException ex){  
                ex.printStackTrace();
            }        
            
        }else{     
            System.out.println("Error en la conexión en guardarSesion.");         
        }
        
    }//Fin método guardarSesion.
    
    public JSONObject obtenerCuentaBancaria(int idCliente){
        JSONObject respuesta = new JSONObject();
        JSONObject datos = new JSONObject();
        cliente.cuenta = new CuentaBancaria();
        Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){           
            try{               
                Statement estado = conectar.createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM datosbancariosclientes WHERE clientes_idclientes = " + idCliente);
                
                if(resultado.next()){               
                    datos.put("nombreTitular", resultado.getString("nombreTitular"));
                    datos.put("tarjeta", resultado.getString("tarjeta"));
                    datos.put("mes", resultado.getString("mes"));
                    datos.put("ano", resultado.getString("ano"));    
                    datos.put("existe", true);
                }else
                    datos.put("existe", false);
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", datos);
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
        
    }//Fin método obtenerCuentaBancaria.
    
    public void datosActualizarPerfil(JSONObject datos){
        
        cliente.setIdCliente(Integer.parseInt(String.valueOf(datos.get("idCliente"))));
        cliente.setNombre(String.valueOf(datos.get("nombre")));
        cliente.setCorreo(String.valueOf(datos.get("correo")));
        cliente.setDescripcion(String.valueOf(datos.get("descripcion")));
        
    }//Fin método datosActualizarPerfil.
    
    public void actualizarDatosPerfil(){
        
         Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            
            try{
                
                String query = "UPDATE clientes SET nombre = ?, correo = ?, descripcion = ? WHERE idclientes = ?";
                PreparedStatement actualizar = conectar.prepareStatement(query);
                actualizar.setString(1, cliente.getNombre());
                actualizar.setString(2, cliente.getCorreo());
                actualizar.setString(3, cliente.getDescripcion());
                actualizar.setInt(4, cliente.getIdCliente());
                actualizar.executeUpdate();
            
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }
            
        }else{
            
            System.out.println("Error en la conexión en actualizarDatosPerfil.");
            
        }
        
    }//Fin método actualizarDatosPerfil.

    public void iniciarSesion(String correo, String contrasena) {
         Connection conectar = ConexionMySQL.connection();
        if (conectar != null) {
            try {
                String query = "SELECT * FROM clientes WHERE BINARY correo = ? AND BINARY contrasena = ?";
                PreparedStatement obtenerDatos = conectar.prepareStatement(query);
                obtenerDatos.setString(1, correo);
                obtenerDatos.setString(2, contrasena);
                ResultSet resultado = obtenerDatos.executeQuery();

                if (resultado.next()) {
                    cliente.setIdCliente(resultado.getInt("idclientes"));
                    cliente.setNombre(resultado.getString("nombre"));
                    cliente.setEstado(resultado.getString("estado"));
                    clienteRegistrado = true;
                } else {
                    clienteRegistrado = false;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        } else 
            System.out.println("Error en la conexión iniciarSesion.");

    }//Fin método iniciarSesion.
    
    
    public void actualizarFoto(JSONObject foto){
        
        cliente.setFoto(String.valueOf(foto.get("nombre")));
        cliente.setIdCliente(Integer.parseInt(String.valueOf(foto.get("idCliente"))));
        
         Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            
            try{
                
                String query = "UPDATE clientes SET foto = ? WHERE idclientes = ?";
                PreparedStatement actualizar = conectar.prepareStatement(query);
                actualizar.setString(1, cliente.getFoto());
                actualizar.setInt(2, cliente.getIdCliente());
                actualizar.executeUpdate();
            
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }
            
        }else{
            
            System.out.println("Error en la conexión actualizarFoto.");
            
        }
        
    }//Fin método actualizarFoto.

    public void perfilCliente(int idCliente) {

         Connection conectar = ConexionMySQL.connection();

        if (conectar != null) {

            try {
                
                PreparedStatement consultar = conectar.prepareStatement("SELECT * FROM planes WHERE clientes_idclientes = ?");
                consultar.setInt(1, idCliente);
                ResultSet resultadoPlan = consultar.executeQuery();
                
                if(resultadoPlan.next()){
                }else{
                    PreparedStatement plan = conectar.prepareStatement("INSERT INTO planes (clientes_idclientes) VALUES (?)");
                    plan.setInt(1, idCliente);
                    plan.execute();
                }

                String query = "SELECT * FROM clientes INNER JOIN planes WHERE clientes.idclientes = ? AND planes.clientes_idclientes = ?";
                PreparedStatement obtenerDatos = conectar.prepareStatement(query);
                obtenerDatos.setInt(1, idCliente);
                obtenerDatos.setInt(2, idCliente);
                ResultSet resultado = obtenerDatos.executeQuery();

                if (resultado.next()) {

                    cliente.setIdCliente(resultado.getInt("idclientes"));
                    cliente.setNombre(resultado.getString("nombre") + " " + resultado.getString("apellidoPaterno") + " " + resultado.getString("apellidoMaterno"));
                    cliente.setCelular(resultado.getString("celular"));
                    cliente.setTelefonoLocal(resultado.getString("telefonoLocal"));
                    cliente.setDireccion(resultado.getString("direccion"));
                    cliente.setCorreo(resultado.getString("correo"));
                    cliente.setFechaNacimiento(resultado.getDate("fechaNacimiento"));
                    cliente.setFechaRegistro(resultado.getDate("fechaDeRegistro"));
                    cliente.setFoto(resultado.getString("foto"));
                    cliente.setDescripcion(resultado.getString("descripcion"));
                    cliente.setTipoPlan(resultado.getString("tipoPlan"));
                    cliente.setMonedero(resultado.getInt("monedero"));
                    clienteRegistrado = true;

                } else {

                    clienteRegistrado = false;
          

                }

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión iniciarSesion.");

        }

    }//Fin método perfilCliente.

    public Cliente datosSesion() {

        if (clienteRegistrado) {

            return cliente;

        } else {

            return null;

        }

    }//Fin método datosSesion.

    public void nuevoCliente(JSONObject jsonCliente) {

        cliente.setNombre(String.valueOf(jsonCliente.get("nombre")));
        cliente.setApellidoPaterno(jsonCliente.get("paterno").toString());
        cliente.setApellidoMaterno(jsonCliente.get("materno").toString());
        cliente.setCorreo(String.valueOf(jsonCliente.get("correo")));
        cliente.setCelular(jsonCliente.get("celular").toString());
        cliente.setTelefonoLocal(jsonCliente.get("telefono").toString());
        cliente.setDireccion(jsonCliente.get("direccion").toString());
        cliente.setGenero(jsonCliente.get("genero").toString());
        cliente.setEstado("REGISTRO");
        cliente.setContrasena(String.valueOf(jsonCliente.get("contrasena")));

        //Formateo de fechas a tipo SQL Date.
        java.sql.Date dateFechaNacimiento = (java.sql.Date.valueOf(String.valueOf(jsonCliente.get("fechaNacimiento"))));

        cliente.setFechaNacimiento(dateFechaNacimiento);
        cliente.setFechaRegistro(java.sql.Date.valueOf("2021-01-27"));

    }//Fin Constructor.

    public JSONObject guardarCliente() {
         Connection conectar = ConexionMySQL.connection();
         JSONObject respuesta = new JSONObject();
         
        if (conectar != null) {
            try {
                String query = "INSERT INTO clientes (nombre, apellidoPaterno, apellidoMaterno, correo, celular, telefonoLocal, direccion, fechaNacimiento, genero, fechaDeRegistro, contrasena, estado) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setString(1, cliente.getNombre());
                agregarDatos.setString(2, cliente.getApellidoPaterno());
                agregarDatos.setString(3, cliente.getApellidoMaterno());
                agregarDatos.setString(4, cliente.getCorreo());
                agregarDatos.setString(5, cliente.getCelular());
                agregarDatos.setString(6, cliente.getTelefonoLocal());
                agregarDatos.setString(7, cliente.getDireccion());
                agregarDatos.setDate(8, cliente.getFechaNacimiento());
                agregarDatos.setString(9, cliente.getGenero());
                agregarDatos.setDate(10, cliente.getFechaRegistro());
                agregarDatos.setString(11, cliente.getContrasena());
                agregarDatos.setString(12, cliente.getEstado());
                agregarDatos.execute();
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Estudiante registrado exitosamente.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD.");
            }
        } else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD.");
        }
        
        return respuesta;
    }//Fin método guardarCliente.

    public void nuevaCuentaBancaria(JSONObject jsonCuentaBancaria) {

        cliente.cuenta = new CuentaBancaria();
        cliente.cuenta.setNombreTitular(String.valueOf(jsonCuentaBancaria.get("nombreTitular")));
        cliente.cuenta.setTarjeta(String.valueOf(jsonCuentaBancaria.get("tarjeta")));
        cliente.cuenta.setMes(String.valueOf(jsonCuentaBancaria.get("mes")));
        cliente.cuenta.setAno(String.valueOf(jsonCuentaBancaria.get("ano")));

    }//Fin método nuevaCuentaBancaria.
    
    public void guardarCuentaBancaria(int idCliente) {

        Connection conectar = ConexionMySQL.connection();

        if (conectar != null) {

            try {

                String query = "INSERT INTO datosbancariosclientes (clientes_idclientes, nombreTitular, tarjeta, mes, ano) VALUES (?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setInt(1, idCliente);
                agregarDatos.setString(2, cliente.cuenta.getNombreTitular());
                agregarDatos.setString(3, cliente.cuenta.getTarjeta());
                agregarDatos.setString(4, cliente.cuenta.getMes());
                agregarDatos.setString(5, cliente.cuenta.getAno());
                agregarDatos.execute();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión guardarCuentaBancaria.");

        }

    }//Fin método guardarCuentaBancaria.

    public JSONObject actualizarCuentaBancaria(int idCliente) {
        JSONObject respuesta = new JSONObject();
        Connection conectar = ConexionMySQL.connection();

        if (conectar != null) {
            try {
                PreparedStatement consultaRegistro = conectar.prepareStatement("SELECT * FROM datosbancariosclientes WHERE clientes_idclientes = ?");
                consultaRegistro.setInt(1 , idCliente);
                ResultSet resultado = consultaRegistro.executeQuery();
                
                if(resultado.next()){                  
                    String query = "UPDATE datosbancariosclientes SET nombreTitular = ?, tarjeta = ?, mes = ?, ano = ? WHERE clientes_idclientes = ?";
                    PreparedStatement agregarDatos = conectar.prepareStatement(query);
                    agregarDatos.setString(1, cliente.cuenta.getNombreTitular());
                    agregarDatos.setString(2, cliente.cuenta.getTarjeta());
                    agregarDatos.setString(3, cliente.cuenta.getMes());
                    agregarDatos.setString(4, cliente.cuenta.getAno());
                    agregarDatos.setInt(5, idCliente);
                    agregarDatos.executeUpdate();                  
                }else{                 
                    String query = "INSERT INTO datosbancariosclientes (clientes_idclientes, nombreTitular, tarjeta, mes, ano) VALUES (?,?,?,?,?)";
                    PreparedStatement agregarDatos = conectar.prepareStatement(query);
                    agregarDatos.setInt(1, idCliente);
                    agregarDatos.setString(2, cliente.cuenta.getNombreTitular());
                    agregarDatos.setString(3, cliente.cuenta.getTarjeta());
                    agregarDatos.setString(4, cliente.cuenta.getMes());
                    agregarDatos.setString(5, cliente.cuenta.getAno());
                    agregarDatos.execute();
                }
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Datos acutalizados exitosamente.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD");
            }
        } else {
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión a BD");
        }
        
        return respuesta;
    }//Fin método guardarCuentaBancaria.

    public void nuevaEvaluacion(JSONObject jsonEvaluacionCliente) {

        cliente.evaluacion = new EvaluacionCliente();
        cliente.evaluacion.setHorario(String.valueOf(jsonEvaluacionCliente.get("horario")));
        cliente.evaluacion.setLugar(String.valueOf(jsonEvaluacionCliente.get("lugar")));
        cliente.evaluacion.setNivelIdioma(Integer.parseInt(String.valueOf(jsonEvaluacionCliente.get("idNivelIdioma"))));
        cliente.evaluacion.setTiempoClase(String.valueOf(jsonEvaluacionCliente.get("tiempoClase")));
        cliente.evaluacion.setTipoClase(String.valueOf(jsonEvaluacionCliente.get("tipoClase")));

    }//Fin método nuevaEvaluacion.

    public void guardarEvaluacion(int idCliente) {

         Connection conectar = ConexionMySQL.connection();

        if (conectar != null) {

            try {

                String query = "INSERT INTO evaluacioncliente (clientes_idclientes, tipoClase, horario, tiempoClase, lugar, nivelIdioma_idnivelidioma) VALUES (?,?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setInt(1, idCliente);
                agregarDatos.setString(2, cliente.evaluacion.getTipoClase());
                agregarDatos.setString(3, cliente.evaluacion.getHorario());
                agregarDatos.setString(4, cliente.evaluacion.getTiempoClase());
                agregarDatos.setString(5, cliente.evaluacion.getLugar());
                agregarDatos.setInt(6, cliente.evaluacion.getNivelIdioma());
                agregarDatos.execute();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión agregarCuentaBancaria.");

        }

    }//Fin método guardarEvaluacion.

}
