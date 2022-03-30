package com.proathome.controladores;

import com.proathome.modelos.Constantes;
import com.proathome.modelos.Profesional;
import com.proathome.mysql.DBController;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ControladorProfesional {

    /*
    *
    *Servicio controladora de Profesionales.
    *
     */
    private Profesional profesional = new Profesional();
    private JSONObject jsonMatch = new JSONObject();
    private JSONArray arrayJson = new JSONArray();
    private boolean profesionalRegistrado = false;
    
    public JSONObject getServicios(int rango, int idProfesional){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            JSONObject data = new JSONObject();
            DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            String fechaActual = formateador.format(LocalDateTime.now());
            String fecha5Dias = formateador.format(LocalDateTime.now().plusDays(4));
            
            data.put("fechaActual", formateador.format(LocalDateTime.now()));
            data.put("servicios", getServiciosDisponibles(rango, fechaActual, fecha5Dias));
            data.put("serviciosPendientes", getServiciosAgendados(idProfesional, fechaActual, fecha5Dias));
            
            respuesta.put("mensaje", data);
            respuesta.put("respuesta", false);
        }else{
            respuesta.put("mensaje", "Error");
            respuesta.put("respuesta", false);
        }
        
        return respuesta;
    }
    
    public JSONObject getServiciosDisponibles(int rango, String fechaActual, String fecha5Dias){
        JSONObject servicios = new JSONObject();
        JSONArray dataServicios = new JSONArray();
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formateadorHora = DateTimeFormatter.ofPattern("HH");
        DateTimeFormatter formateadorMinutos = DateTimeFormatter.ofPattern("mm");
        
          String query = null;
        
        if(rango == Constantes.BASICO)
            query = "SELECT * FROM sesiones INNER JOIN clientes WHERE (fecha BETWEEN ? AND ?) AND sesiones.profesionales_idprofesionales IS NULL AND sesiones.idSeccion = ? AND sesiones.clientes_idclientes = clientes.idclientes ";
        else if(rango == Constantes.INTERMEDIO)
            query = "SELECT * FROM sesiones INNER JOIN clientes WHERE (fecha BETWEEN ? AND ?) AND sesiones.profesionales_idprofesionales IS NULL AND (sesiones.idSeccion = ? OR sesiones.idSeccion = ?) AND sesiones.clientes_idclientes = clientes.idclientes";
        else if(rango == Constantes.AVANZADO)
            query = "SELECT * FROM sesiones INNER JOIN clientes WHERE (fecha BETWEEN ? AND ?) AND sesiones.profesionales_idprofesionales IS NULL AND (sesiones.idSeccion = ? OR sesiones.idSeccion = ? OR sesiones.idSeccion = ?) AND sesiones.clientes_idclientes = clientes.idclientes";
        
        if(DBController.getInstance().getConnection() != null){ 
            try{   
                PreparedStatement sesiones = DBController.getInstance().getConnection().prepareStatement(query);
                sesiones.setString(1, fechaActual);
                sesiones.setString(2, fecha5Dias);
                sesiones.setInt(3, 1);
                
                if(rango == Constantes.INTERMEDIO){
                    sesiones.setInt(4, 2);
                }else if(rango == Constantes.AVANZADO){
                    sesiones.setInt(4, 2);
                    sesiones.setInt(5, 3);
                }
                
                ResultSet resultado = sesiones.executeQuery();             
                while(resultado.next()){ 
                    //VALIDAR QUE LA FECHA SEA VALIDA.
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    String fechaHoy = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);        
                    java.util.Date fechaActualDate = sdf.parse(fechaHoy);
                    java.util.Date fechaServicio = sdf.parse(resultado.getDate("fecha").toString());
                    
                    JSONObject json = new JSONObject();
                    if(fechaServicio.equals(fechaActualDate) || fechaServicio.after(fechaActualDate)){
                        json.put("idSesion", resultado.getInt("idsesiones"));
                        json.put("latitud", resultado.getDouble("latitud"));
                        json.put("longitud", resultado.getDouble("longitud"));
                        json.put("nombre", resultado.getString("nombre"));
                        json.put("lugar", resultado.getString("lugar"));
                        json.put("idSeccion", resultado.getInt("idSeccion"));
                        json.put("fecha", resultado.getDate("fecha").toString());
                        json.put("horario", resultado.getString("horario"));
                        json.put("idNivel", resultado.getInt("idNivel"));
                        json.put("idBloque", resultado.getInt("idBloque"));
                        json.put("tipoPlan", resultado.getString("tipoPlan"));
                        dataServicios.add(json);
                    }
                    
                    json = null;  
                } 
                
                int horaActual = Integer.parseInt(formateadorHora.format(LocalDateTime.now()));
                int minutosActuales = Integer.parseInt(formateadorMinutos.format(LocalDateTime.now()));
                
                //HOY
                JSONArray hoy = new JSONArray();
                for(int i = 0; i < dataServicios.size(); i++){
                    JSONObject servicio = (JSONObject) dataServicios.get(i);
                    if(servicio.get("fecha").toString().equals(formateador.format(LocalDateTime.now()))){
                        if(getHorarioNumber(servicio.get("horario").toString()) > horaActual)
                            hoy.add(servicio);
                    }
                }
                
                servicios.put(formateador.format(LocalDateTime.now()), hoy);
                
                //DIA 1
                JSONArray dia1 = new JSONArray();
                for(int i = 0; i < dataServicios.size(); i++){
                    JSONObject servicio = (JSONObject) dataServicios.get(i);
                    if(servicio.get("fecha").toString().equals(formateador.format(LocalDateTime.now().plusDays(1))))
                        dia1.add(servicio);
                }
                
                servicios.put(formateador.format(LocalDateTime.now().plusDays(1)), dia1);
                
                //DIA 2
                JSONArray dia2 = new JSONArray();
                for(int i = 0; i < dataServicios.size(); i++){
                    JSONObject servicio = (JSONObject) dataServicios.get(i);
                    if(servicio.get("fecha").toString().equals(formateador.format(LocalDateTime.now().plusDays(2))))
                        dia2.add(servicio);
                }
                
                servicios.put(formateador.format(LocalDateTime.now().plusDays(2)), dia2);
                
                //DIA 3
                JSONArray dia3 = new JSONArray();
                for(int i = 0; i < dataServicios.size(); i++){
                    JSONObject servicio = (JSONObject) dataServicios.get(i);
                    if(servicio.get("fecha").toString().equals(formateador.format(LocalDateTime.now().plusDays(3))))
                        dia3.add(servicio);
                }
                
                servicios.put(formateador.format(LocalDateTime.now().plusDays(3)), dia3);
                
                //DIA 4
                JSONArray dia4 = new JSONArray();
                for(int i = 0; i < dataServicios.size(); i++){
                    JSONObject servicio = (JSONObject) dataServicios.get(i);
                    if(servicio.get("fecha").toString().equals(formateador.format(LocalDateTime.now().plusDays(4))))
                        dia4.add(servicio);
                }
                
                servicios.put(formateador.format(LocalDateTime.now().plusDays(4)), dia4);
                
            }catch(SQLException ex){
                ex.printStackTrace();
            } catch (ParseException ex) {
                Logger.getLogger(ControladorProfesional.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }else{ 
            System.out.println("Error en obtenerSesionesMovil."); 
        }
        
        return servicios;
    }
    
    public JSONObject getServiciosAgendados(int idProfesional, String fechaActual, String fecha5Dias){
        JSONObject servicios = new JSONObject();
        JSONArray jsonArrayMatch = new JSONArray();
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if(DBController.getInstance().getConnection() != null){
            try{             
                PreparedStatement sesionesMatch = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones INNER JOIN clientes WHERE (sesiones.fecha BETWEEN ? AND ?) AND sesiones.clientes_idclientes = clientes.idclientes AND profesionales_idprofesionales = ?");
                sesionesMatch.setString(1, fechaActual);
                sesionesMatch.setString(2, fecha5Dias);
                sesionesMatch.setInt(3, idProfesional);
                ResultSet resultado = sesionesMatch.executeQuery();
                
                while(resultado.next()){       
                    JSONObject jsonSesionesMatchProfesional = new JSONObject();
                    jsonSesionesMatchProfesional.put("idsesiones", resultado.getInt("idsesiones"));
                    jsonSesionesMatchProfesional.put("nombreCliente", resultado.getString("nombre") + " " + resultado.getString("apellidoPaterno") + " " + resultado.getString("apellidoMaterno"));
                    jsonSesionesMatchProfesional.put("idCliente", resultado.getInt("clientes_idclientes"));
                    jsonSesionesMatchProfesional.put("descripcion", resultado.getString("descripcion"));
                    jsonSesionesMatchProfesional.put("actualizado", resultado.getDate("actualizado").toString());
                    jsonSesionesMatchProfesional.put("fecha", resultado.getDate("fecha").toString());
                    jsonSesionesMatchProfesional.put("correo", resultado.getString("correo"));
                    jsonSesionesMatchProfesional.put("latitud", resultado.getDouble("latitud"));
                    jsonSesionesMatchProfesional.put("longitud", resultado.getDouble("longitud"));
                    jsonSesionesMatchProfesional.put("foto", resultado.getString("foto"));
                    jsonSesionesMatchProfesional.put("lugar", resultado.getString("lugar"));
                    jsonSesionesMatchProfesional.put("tiempo", resultado.getInt("tiempo"));
                    jsonSesionesMatchProfesional.put("idSeccion", resultado.getInt("idSeccion"));
                    jsonSesionesMatchProfesional.put("idNivel", resultado.getInt("idNivel"));
                    jsonSesionesMatchProfesional.put("idBloque", resultado.getInt("idBloque"));
                    jsonSesionesMatchProfesional.put("tipoServicio", resultado.getString("tipoServicio") + ": " + resultado.getString("personas") + " Personas");
                    jsonSesionesMatchProfesional.put("extras", resultado.getString("extras"));
                    jsonSesionesMatchProfesional.put("horario", resultado.getString("horario")); 
                    jsonSesionesMatchProfesional.put("tipoPlan", resultado.getString("tipoPlan"));
                    jsonSesionesMatchProfesional.put("finalizado", resultado.getBoolean("finalizado"));
                    jsonArrayMatch.add(jsonSesionesMatchProfesional); 
                }

                //HOY
                JSONArray hoy = new JSONArray();
                for(int i = 0; i < jsonArrayMatch.size(); i++){
                    JSONObject servicio = (JSONObject) jsonArrayMatch.get(i);
                    if(servicio.get("fecha").toString().equals(formateador.format(LocalDateTime.now()))){
                        hoy.add(servicio);
                    }
                }
                
                servicios.put(formateador.format(LocalDateTime.now()), hoy);
                
                //DIA 1
                JSONArray dia1 = new JSONArray();
                for(int i = 0; i < jsonArrayMatch.size(); i++){
                    JSONObject servicio = (JSONObject) jsonArrayMatch.get(i);
                    if(servicio.get("fecha").toString().equals(formateador.format(LocalDateTime.now().plusDays(1))))
                        dia1.add(servicio);
                }
                
                servicios.put(formateador.format(LocalDateTime.now().plusDays(1)), dia1);
                
                //DIA 2
                JSONArray dia2 = new JSONArray();
                for(int i = 0; i < jsonArrayMatch.size(); i++){
                    JSONObject servicio = (JSONObject) jsonArrayMatch.get(i);
                    if(servicio.get("fecha").toString().equals(formateador.format(LocalDateTime.now().plusDays(2))))
                        dia2.add(servicio);
                }
                
                servicios.put(formateador.format(LocalDateTime.now().plusDays(2)), dia2);
                
                //DIA 3
                JSONArray dia3 = new JSONArray();
                for(int i = 0; i < jsonArrayMatch.size(); i++){
                    JSONObject servicio = (JSONObject) jsonArrayMatch.get(i);
                    if(servicio.get("fecha").toString().equals(formateador.format(LocalDateTime.now().plusDays(3))))
                        dia3.add(servicio);
                }
                
                servicios.put(formateador.format(LocalDateTime.now().plusDays(3)), dia3);
                
                //DIA 4
                JSONArray dia4 = new JSONArray();
                for(int i = 0; i < jsonArrayMatch.size(); i++){
                    JSONObject servicio = (JSONObject) jsonArrayMatch.get(i);
                    if(servicio.get("fecha").toString().equals(formateador.format(LocalDateTime.now().plusDays(4))))
                        dia4.add(servicio);
                }
                
                servicios.put(formateador.format(LocalDateTime.now().plusDays(4)), dia4);
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        
        return servicios;
    }
    
    public int getHorarioNumber(String horario){
        int hora = 0;

        if(horario.equals("00:00 HRS"))
            hora = 0;
        else if(horario.equals("01:00 HRS"))
            hora = 1;
        else if(horario.equals("02:00 HRS"))
            hora = 2;
        else if(horario.equals("03:00 HRS"))
            hora = 3;
        else if(horario.equals("04:00 HRS"))
            hora = 4;
        else if(horario.equals("05:00 HRS"))
            hora = 5;
        else if(horario.equals("06:00 HRS"))
            hora = 6;
        else if(horario.equals("07:00 HRS"))
            hora = 7;
        else if(horario.equals("08:00 HRS"))
            hora = 8;
        else if(horario.equals("09:00 HRS"))
            hora = 9;
        else if(horario.equals("10:00 HRS"))
            hora = 10;
        else if(horario.equals("11:00 HRS"))
            hora = 11;
        else if(horario.equals("12:00 HRS"))
            hora = 12;
        else if(horario.equals("13:00 HRS"))
            hora = 13;
        else if(horario.equals("14:00 HRS"))
            hora = 14;
        else if(horario.equals("15:00 HRS"))
            hora = 15;
        else if(horario.equals("16:00 HRS"))
            hora = 16;
        else if(horario.equals("17:00 HRS"))
            hora = 17;
        else if(horario.equals("18:00 HRS"))
            hora = 18;
        else if(horario.equals("19:00 HRS"))
            hora = 19;
        else if(horario.equals("20:00 HRS"))
            hora = 20;
        else if(horario.equals("21:00 HRS"))
            hora = 21;
        else if(horario.equals("22:00 HRS"))
            hora = 22;
        else if(horario.equals("23:00 HRS"))
            hora = 23;

        return hora;
    }
    
    public JSONObject getVerificacion(String token, String correo){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            //Obtenemos token de correo.
            try{
                PreparedStatement consultar = DBController.getInstance().getConnection().prepareStatement("SELECT token_verificacion, verificado FROM profesionales WHERE correo = ?");
                consultar.setString(1, correo);
                ResultSet resultado = consultar.executeQuery();
                
                if(resultado.next()){
                    String token_bd = resultado.getString("token_verificacion");
                    boolean verificado = resultado.getBoolean("verificado");
                    
                    if(token_bd.equalsIgnoreCase(token)){
                        //Verificamos el correo.
                        PreparedStatement verificar = DBController.getInstance().getConnection().prepareStatement("UPDATE profesionales SET verificado = ? WHERE correo = ?");
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
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                //Consultar si el pass anterior es correcto.
                PreparedStatement passAnterior = DBController.getInstance().getConnection().prepareStatement("SELECT contrasena FROM profesionales WHERE idprofesionales = ?");
                passAnterior.setInt(1, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
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
                        PreparedStatement guardarPass = DBController.getInstance().getConnection().prepareStatement("UPDATE profesionales SET contrasena = ? WHERE idprofesionales = ?");
                        guardarPass.setString(1, nuevaPass);
                        guardarPass.setInt(2, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
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
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement cancelar = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET profesionales_idprofesionales = ? WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                cancelar.setNull(1, 0);
                cancelar.setInt(2, Integer.parseInt(jsonDatos.get("idServicio").toString()));
                cancelar.setInt(3, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
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
        }else if(horario.equals("19:00 HRS"))
            text = fecha + " 19:00";
        
        return text;
    }
    
    public boolean fechaHoy(String hoy, String fechaServicio){
        boolean resp = false;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date fechaServicioDate =  sdf.parse(fechaServicio);
            java.util.Date fechaHoyDate =  sdf.parse(hoy);
            
            if(fechaServicioDate.equals(fechaHoyDate)){
                resp = true;
            }else
                resp = false;
            
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        
        return resp;
    }
    
    public JSONObject solicitudEliminarSesion(int idSesion, int idProfesional){
        JSONObject respuesta = new JSONObject();
        JSONObject eliminar = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                //Consultamos la fecha y el horario;
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT fecha, horario FROM sesiones WHERE idsesiones = ? AND profesionales_idprofesionales = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idProfesional);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    SimpleDateFormat sdfNormal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    //Hoy
                    Calendar calendarHoy = Calendar.getInstance();
                    String fechaHoy = sdfNormal.format(calendarHoy.getTime());
                    java.util.Date hoy = sdfNormal.parse(fechaHoy);
                    
                    if(fechaHoy(fechaHoy ,resultado.getDate("fecha").toString())){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        java.util.Date fechaServicio =  sdf.parse(getFechaString(resultado.getDate("fecha").toString(), resultado.getString("horario")));

                        String fechaServicioS = sdf.format(fechaServicio.getTime());
                        //obtienes la diferencia de las fechas
                        long difference = Math.abs(fechaServicio.getTime() - hoy.getTime());
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
        
        if(DBController.getInstance().getConnection() != null){
            try{
                //Consultar si hay registro.
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM datosfiscalesprofesionales WHERE profesionales_idprofesionales = ?");
                consulta.setInt(1, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    //Actualizamos
                    PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE datosfiscalesprofesionales SET tipoPersona = ?, razonSocial = ?, rfc = ?, cfdi = ? WHERE profesionales_idprofesionales = ?");
                    actualizar.setString(1, jsonDatos.get("tipoPersona").toString());
                    actualizar.setString(2, jsonDatos.get("razonSocial").toString());
                    actualizar.setString(3, jsonDatos.get("rfc").toString());
                    actualizar.setString(4, jsonDatos.get("cfdi").toString());
                    actualizar.setInt(5, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                    actualizar.execute();
                }else{
                    //Guardamos
                    PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO datosfiscalesprofesionales (tipoPersona, razonSocial, rfc, cfdi, profesionales_idprofesionales) VALUES (?,?,?,?,?)");
                    actualizar.setString(1, jsonDatos.get("tipoPersona").toString());
                    actualizar.setString(2, jsonDatos.get("razonSocial").toString());
                    actualizar.setString(3, jsonDatos.get("rfc").toString());
                    actualizar.setString(4, jsonDatos.get("cfdi").toString());
                    actualizar.setInt(5, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
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
    
    public JSONObject getDatosFiscales(int idProfesional){
        JSONObject respuesta = new JSONObject();
        JSONObject datos = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM datosfiscalesprofesionales WHERE profesionales_idprofesionales = ?");
                consulta.setInt(1, idProfesional);
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
    
    public void bloquearPerfil(int idProfesional){
        try{
            PreparedStatement bloquear = DBController.getInstance().getConnection().prepareStatement("UPDATE profesionales SET estado = ? WHERE idprofesionales = ?");
            bloquear.setString(1, "BLOQUEADO");
            bloquear.setInt(2, idProfesional);
            bloquear.execute();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public JSONObject getReportes(int idProfesional){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement reportes = DBController.getInstance().getConnection().prepareStatement("SELECT COUNT(*) AS num FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
                reportes.setInt(1, idProfesional);
                reportes.setString(2, "PROFESIONAL");
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
                            bloquearPerfil(idProfesional, conectar);*/
                        PreparedStatement descripcion = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
                        descripcion.setInt(1, idProfesional);
                        descripcion.setString(2, "PROFESIONAL");
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
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement agendar = DBController.getInstance().getConnection().prepareStatement("UPDATE citas SET fechaAcordada = ?, horarioAcordado = ? WHERE profesionales_idprofesionales = ?");
                agendar.setDate(1, java.sql.Date.valueOf(jsonDatos.get("fechaAcordada").toString()));
                agendar.setString(2, jsonDatos.get("horarioAcordado").toString());
                agendar.setInt(3, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                agendar.execute();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en agendarCita.");
        }
    }
    
    public JSONObject obtenerCita(int idProfesional){
        JSONObject cita = new JSONObject();
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECt * FROM citas WHERE profesionales_idprofesionales = ?");
                consulta.setInt(1, idProfesional);
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    cita.put("fecha1", resultado.getDate("fecha1").toString());
                    cita.put("fecha2", resultado.getDate("fecha2").toString());
                    cita.put("horario1", resultado.getString("horario1"));
                    cita.put("horario2", resultado.getString("horario2"));
                    cita.put("fechaAcordada", resultado.getDate("fechaAcordada") == null ? null : resultado.getDate("fechaAcordada").toString());
                    cita.put("horarioAcordado", resultado.getString("horarioAcordado"));
                    cita.put("tipoCita", resultado.getString("tipoCita"));
                    cita.put("datosAdicionales", resultado.getString("datosAdicionales"));
                }
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", cita);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion a BD.");
            }
        }else{
            System.out.println("Error en obtenerCita.");
        }
        
        return respuesta;
    }
    
    public JSONObject estatusDocumentos(int idProfesional){
        JSONObject estatus = new JSONObject();
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement documentos = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM documentacionprofesional WHERE profesionales_idprofesionales = ?");
                documentos.setInt(1, idProfesional);
                ResultSet resultado = documentos.executeQuery();
                
                if(resultado.next())
                    estatus.put("estatus", true);
                else
                    estatus.put("estatus", false);
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", estatus);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion a BD.");
            }
        }else
            System.out.println("Error en estatusDocumentos.");
        
        return respuesta;
    }
    
    public void nuevoTicket(JSONObject jsonDatos){
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement nuevoTicket;
                //Validar si es tipo General o Servicio
                if(Integer.parseInt(jsonDatos.get("idSesion").toString()) == 0)
                    nuevoTicket = DBController.getInstance().getConnection().prepareStatement("INSERT INTO tickets_ayuda (tipoUsuario, topico, descripcion, fechaCreacion, estatus, idUsuario, categoria) VALUES (?,?,?,?,?,?,?)");
                else nuevoTicket = DBController.getInstance().getConnection().prepareStatement("INSERT INTO tickets_ayuda (tipoUsuario, topico, descripcion, fechaCreacion, estatus, idUsuario, categoria, sesiones_idsesiones) VALUES (?,?,?,?,?,?,?,?)");
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

        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement mensaje = DBController.getInstance().getConnection().prepareStatement("INSERT INTO msg_tickets (mensaje, idUsuario_Operador, operadorBool, tickets_ayuda_idtickets_ayuda) VALUES (?,?,?,?)");
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
    
    public JSONObject obtenerTickets(int idProfesional){
        JSONArray jsonTickets = new JSONArray();
        JSONObject respuesta = new JSONObject();
        boolean vacio = true;
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM tickets_ayuda WHERE idUsuario = ? AND tipoUsuario = ? ORDER BY idtickets_ayuda DESC");
                consulta.setInt(1, idProfesional);
                consulta.setInt(2, Constantes.TIPO_USUARIO_PROFESIONAL);
                ResultSet resultado = consulta.executeQuery();
                
                while(resultado.next()){
                    vacio = false;
                    JSONObject jsonTicket = new JSONObject();
                    jsonTicket.put("noTicket", "00" + resultado.getInt("idtickets_ayuda"));
                    jsonTicket.put("idTicket", resultado.getInt("idtickets_ayuda"));
                    jsonTicket.put("tipoUsuario", resultado.getInt("tipoUsuario"));
                    jsonTicket.put("topico", resultado.getString("topico"));
                    jsonTicket.put("descripcion", resultado.getString("descripcion"));
                    jsonTicket.put("fechaCreacion", resultado.getDate("fechaCreacion").toString());
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
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", jsonTickets);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion a BD.");
            }
        }else{
            System.out.println("Error en obtenerTickets.");
        }

        return respuesta;
    }
    
    public void finalizarTicket(int idTicket){
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement ticket = DBController.getInstance().getConnection().prepareStatement("UPDATE tickets_ayuda SET estatus = ? WHERE idtickets_ayuda = ?");
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
    
    public JSONObject validarValoracion(int idSesion, int idCliente){
    
       JSONObject jsonRespuesta = new JSONObject();
       if(DBController.getInstance().getConnection() != null){
           try{
               PreparedStatement validar = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM valoracioncliente WHERE sesiones_idsesiones = ? AND clientes_idclientes = ?");
               validar.setInt(1, idSesion);
               validar.setInt(2, idCliente);
               ResultSet resultado = validar.executeQuery();
               
               if(resultado.next()){
                   jsonRespuesta.put("valorado", true);
               }else{
                   PreparedStatement sesion = DBController.getInstance().getConnection().prepareStatement("SELECT finalizado FROM sesiones WHERE idsesiones = ?");
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
    
    public void valorarCliente(JSONObject jsonDatos){
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement valorar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO valoracioncliente (clientes_idclientes, profesionales_idprofesionales, valoracion, comentario, sesiones_idsesiones) VALUES (?,?,?,?,?)");
                valorar.setInt(1, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                valorar.setInt(2, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
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
    
    public JSONArray obtenerValoracion(int idCliente){
   
        JSONObject clienteJSON = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if(DBController.getInstance().getConnection() != null){
            //Obtener la info del profesional.
            try{
                PreparedStatement cliente = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM clientes WHERE idclientes = ?");
                cliente.setInt(1, idCliente);
                ResultSet resultado = cliente.executeQuery();
                
                if(resultado.next()){
                    //Obtener promedio
                    clienteJSON.put("idCliente", resultado.getInt("idclientes"));
                    clienteJSON.put("nombre", resultado.getString("nombre"));
                    clienteJSON.put("correo", resultado.getString("correo"));
                    clienteJSON.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro").toString());
                    clienteJSON.put("foto", resultado.getString("foto"));
                    clienteJSON.put("descripcion", resultado.getString("descripcion"));
                    clienteJSON.put("valoraciones", false);
                    jsonArray.add(clienteJSON);
                    
                    //Obtener Valoraciones
                    PreparedStatement valoraciones = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM valoracioncliente WHERE clientes_idclientes = ?");
                    valoraciones.setInt(1, idCliente);
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
        if (DBController.getInstance().getConnection() != null) {
            try {
                MD5 md5 = new MD5();
                String pass = md5.getMD5(contrasena);
                String query = "SELECT * FROM profesionales WHERE BINARY correo = ? AND BINARY contrasena = ?";
                PreparedStatement obtenerDatos = DBController.getInstance().getConnection().prepareStatement(query);
                obtenerDatos.setString(1, correo);
                obtenerDatos.setString(2, pass);
                ResultSet resultado = obtenerDatos.executeQuery();

                if (resultado.next()) {
                    profesional.setToken(JWTController.getInstance().getToken(String.valueOf(resultado.getInt("idprofesionales")), JWTController.PERFIL_PROFESIONAL));
                    profesional.setIdProfesional(resultado.getInt("idprofesionales"));
                    profesional.setNombre(resultado.getString("nombre"));
                    profesional.setEstado(resultado.getString("estado"));
                    profesional.setVerificado(resultado.getBoolean("verificado"));
                    profesional.setRangoServicio(resultado.getInt("rangoServicio"));
                    profesionalRegistrado = true;
                } else 
                    profesionalRegistrado = false;                   
                
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Error en la conexión iniciarSesion.");
        }

    }//Fin método iniciarSesion.

    public void datosActualizarPerfil(JSONObject datos) {

        profesional.setIdProfesional(Integer.parseInt(String.valueOf(datos.get("idProfesional"))));
        profesional.setCelular(datos.get("celular").toString());
        profesional.setTelefonoLocal(datos.get("telefonoLocal").toString());
        profesional.setDireccion(datos.get("direccion").toString());
        profesional.setDescripcion(String.valueOf(datos.get("descripcion")));

    }//Fin método datosActualizarPerfil.
    
    public void matchSesionWeb(int idProfesional, int idSesion){
        
        
        if(DBController.getInstance().getConnection() != null){
            
            try{
                System.out.println("UPDATE sesiones SET profesionales_idprofesionales =" + idProfesional +" WHERE idsesiones = " + idSesion);
                PreparedStatement match = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET profesionales_idprofesionales = ? WHERE idsesiones = ?");
                match.setInt(1 , idProfesional);
                match.setInt(2 , idSesion);
                match.execute();
                
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
            
            System.out.println("Error en matchSesion.");
            
        }
        
    }//Fin método matchSesion.
    
    public JSONObject matchSesion(JSONObject jsonDatos){
        JSONObject response = new JSONObject();
        if(DBController.getInstance().getConnection() != null){   
            try{        
                //VALIDAR QUE NO TENGA PROFESIONAL TODAVIA
                PreparedStatement consultaProfesional = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ?");
                consultaProfesional.setInt(1, Integer.parseInt(jsonDatos.get("idSesion").toString()));
                
                ResultSet consultaResult = consultaProfesional.executeQuery();
                if(consultaResult.next()){
                    if(consultaResult.getInt("profesionales_idprofesionales") == 0){
                        //VALIDAR NUEVAMENTE
                        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String fechaActual = formateador.format(LocalDateTime.now());
                        String fecha5Dias = formateador.format(LocalDateTime.now().plusDays(4));

                        JSONObject servicios = getServiciosDisponibles(Integer.parseInt(jsonDatos.get("rango").toString()), fechaActual, fecha5Dias);
                        JSONObject serviciosAgendados = getServiciosAgendados(Integer.parseInt(jsonDatos.get("idProfesional").toString()), fechaActual, fecha5Dias);

                        //VALIDAR SI LA FECHA DEL SERVICIO TIENE 3 PENDIENTES
                        JSONArray serviciosFecha = (JSONArray) serviciosAgendados.get(consultaResult.getString("fecha"));
                
                        if(serviciosFecha.size() == 3){
                            //AGENDA LLENA
                            response.put("respuesta", false);
                            response.put("mensaje", "AGENDA LLENA EN FECHA: " + consultaResult.getString("fecha"));
                        }else if(serviciosFecha.size() == 0){
                            //LIBRE DE AGENDAR
                            PreparedStatement match = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET profesionales_idprofesionales = ? WHERE idsesiones = ?");
                            match.setInt(1 , Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                            match.setInt(2 , Integer.parseInt(jsonDatos.get("idSesion").toString()));
                            match.execute();  
                            
                            response.put("respuesta", true);
                            response.put("mensaje", "Match realizado!");
                        }else{
                            //RECORREMOS Y VALIDAMOS HORARIOS
                            int horarioServicioElegido = getHorarioNumber(consultaResult.getString("horario"));
                            boolean disponible = true;
                            //VALIDAR SI LOS HORARIOS DE MIS SERVICIOS AGENDADOS TIENEN UYNA DIFERENCIA DE 3 HRS
                            JSONArray serviciosAgendadosFecha = (JSONArray) serviciosAgendados.get(consultaResult.getString("fecha"));
                            for (int i = 0; i < serviciosAgendadosFecha.size(); i++) {
                                JSONObject servicio = (JSONObject) serviciosAgendadosFecha.get(i);
                                int horarioServicioAgendado = getHorarioNumber(servicio.get("horario").toString());
                                int diferencia = horarioServicioAgendado - horarioServicioElegido;
                                diferencia = Math.abs(diferencia);
                                if(!(diferencia >= 3))
                                    disponible = false;
                            }

                            if(disponible){
                                PreparedStatement match = DBController.getInstance().getConnection().prepareStatement("UPDATE sesiones SET profesionales_idprofesionales = ? WHERE idsesiones = ?");
                                match.setInt(1 , Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                                match.setInt(2 , Integer.parseInt(jsonDatos.get("idSesion").toString()));
                                match.execute();  
                                
                                response.put("respuesta", true);
                                response.put("mensaje", "Match realizado!");
                            }else{
                                response.put("respuesta", false);
                                response.put("mensaje", "DEBES TENER UNA DIFERENCIA MINIMA DE 3 HRS ENTRE SERVICIOS EN LA FECHA ELEGIDA.");
                            }
                        }  
                    }else{
                        response.put("respuesta", false);
                        response.put("mensaje", "El servicio elegido ya no se encuentra disponible.");
                    }
                }else{
                    response.put("respuesta", false);
                    response.put("mensaje", "Error en la conexion a BD.");
                }
            }catch(SQLException ex){
                ex.printStackTrace();
                response.put("respuesta", false);
                response.put("mensaje", "Error en la conexion a BD.");
            }   
        }else{
            response.put("respuesta", false);
            response.put("mensaje", "Error en la conexion a BD.");
        }
        
        return response;
    }//Fin método matchSesion.
    
    public JSONObject sesionesMatchProfesional(int idProfesional){
        JSONObject respuesta = new JSONObject();
        JSONArray jsonArrayMatch = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            try{             
                PreparedStatement sesionesMatch = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.clientes_idclientes = clientes.idclientes AND profesionales_idprofesionales = ? ORDER BY idsesiones DESC");
                sesionesMatch.setInt(1 , idProfesional);
                ResultSet resultado = sesionesMatch.executeQuery();
                
                while(resultado.next()){       
                    JSONObject jsonSesionesMatchProfesional = new JSONObject();
                    jsonSesionesMatchProfesional.put("idsesiones", resultado.getInt("idsesiones"));
                    jsonSesionesMatchProfesional.put("nombreCliente", resultado.getString("nombre") + " " + resultado.getString("apellidoPaterno") + " " + resultado.getString("apellidoMaterno"));
                    jsonSesionesMatchProfesional.put("idCliente", resultado.getInt("clientes_idclientes"));
                    jsonSesionesMatchProfesional.put("descripcion", resultado.getString("descripcion"));
                    jsonSesionesMatchProfesional.put("actualizado", resultado.getDate("actualizado").toString());
                    jsonSesionesMatchProfesional.put("fecha", resultado.getDate("fecha").toString());
                    jsonSesionesMatchProfesional.put("correo", resultado.getString("correo"));
                    jsonSesionesMatchProfesional.put("latitud", resultado.getDouble("latitud"));
                    jsonSesionesMatchProfesional.put("longitud", resultado.getDouble("longitud"));
                    jsonSesionesMatchProfesional.put("foto", resultado.getString("foto"));
                    jsonSesionesMatchProfesional.put("lugar", resultado.getString("lugar"));
                    jsonSesionesMatchProfesional.put("tiempo", resultado.getInt("tiempo"));
                    jsonSesionesMatchProfesional.put("idSeccion", resultado.getInt("idSeccion"));
                    jsonSesionesMatchProfesional.put("idNivel", resultado.getInt("idNivel"));
                    jsonSesionesMatchProfesional.put("idBloque", resultado.getInt("idBloque"));
                    jsonSesionesMatchProfesional.put("tipoServicio", resultado.getString("tipoServicio") + ": " + resultado.getString("personas") + " Personas");
                    jsonSesionesMatchProfesional.put("extras", resultado.getString("extras"));
                    jsonSesionesMatchProfesional.put("horario", resultado.getString("horario")); 
                    jsonSesionesMatchProfesional.put("tipoPlan", resultado.getString("tipoPlan"));
                    jsonSesionesMatchProfesional.put("finalizado", resultado.getBoolean("finalizado"));
                    jsonArrayMatch.add(jsonSesionesMatchProfesional); 
                }
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", jsonArrayMatch);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexion a BD.");
            }
        }else{     
            System.out.println("Error en sesionesMatchProfesional.");     
        }
        
        return respuesta;   
    }//Fin método sesionesMatchProfesional.
    
    public JSONObject informacionSesionMatch(int idSesion){
         
        if(DBController.getInstance().getConnection() != null){      
            try{            
                PreparedStatement sesion = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.clientes_idclientes = clientes.idclientes AND idsesiones = ?");
                sesion.setInt(1 , idSesion);
                ResultSet resultado = sesion.executeQuery();
                
                if(resultado.next()){
                    jsonMatch.put("idCliente", resultado.getInt("clientes_idclientes"));
                    jsonMatch.put("idSesion", resultado.getInt("idsesiones"));
                    jsonMatch.put("nombre", resultado.getString("nombre"));
                    jsonMatch.put("idProfesional", resultado.getInt("profesionales_idprofesionales"));
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
                    jsonMatch.put("tipoServicio", resultado.getString("tipoServicio"));
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
        String query = null;
        
        if(rango == Constantes.BASICO)
            query = "SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.profesionales_idprofesionales IS NULL AND sesiones.idSeccion = ? AND sesiones.clientes_idclientes = clientes.idclientes ";
        else if(rango == Constantes.INTERMEDIO)
            query = "SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.profesionales_idprofesionales IS NULL AND (sesiones.idSeccion = ? OR sesiones.idSeccion = ?) AND sesiones.clientes_idclientes = clientes.idclientes";
        else if(rango == Constantes.AVANZADO)
            query = "SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.profesionales_idprofesionales IS NULL AND (sesiones.idSeccion = ? OR sesiones.idSeccion = ? OR sesiones.idSeccion = ?) AND sesiones.clientes_idclientes = clientes.idclientes";
        
        if(DBController.getInstance().getConnection() != null){ 
            try{   
                PreparedStatement sesiones = DBController.getInstance().getConnection().prepareStatement(query);
                sesiones.setInt(1, 1);
                
                if(rango == Constantes.INTERMEDIO){
                    sesiones.setInt(2, 2);
                }else if(rango == Constantes.AVANZADO){
                    sesiones.setInt(2, 2);
                    sesiones.setInt(3, 3);
                }
                ResultSet resultado = sesiones.executeQuery();             
                while(resultado.next()){ 
                    //VALIDAR QUE LA FECHA SEA VALIDA.
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    String fechaHoy = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);        
                    java.util.Date fechaActual = sdf.parse(fechaHoy);
                    java.util.Date fechaServicio = sdf.parse(resultado.getDate("fecha").toString());
                    
                    JSONObject json = new JSONObject();
                    if(fechaServicio.equals(fechaActual) || fechaServicio.after(fechaActual)){
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
                    }
                    
                    json = null;  
                }  
            }catch(SQLException ex){
                ex.printStackTrace();
            } catch (ParseException ex) {
                Logger.getLogger(ControladorProfesional.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }else{ 
            System.out.println("Error en obtenerSesionesMovil."); 
        }
        
        return arrayJson;
        
    }

    public JSONObject actualizarDatosPerfil(){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{               
                String query = "UPDATE profesionales SET celular = ?, telefonoLocal = ?, direccion = ?, descripcion = ? WHERE idprofesionales = ?";
                PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement(query);
                actualizar.setString(1, profesional.getCelular());
                actualizar.setString(2, profesional.getTelefonoLocal());
                actualizar.setString(3, profesional.getDireccion());
                actualizar.setString(4, profesional.getDescripcion());
                actualizar.setInt(5, profesional.getIdProfesional());
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
        profesional.setFoto(String.valueOf(foto.get("nombre")));
        profesional.setIdProfesional(Integer.parseInt(String.valueOf(foto.get("idProfesional"))));

        if (DBController.getInstance().getConnection() != null) {
            try {
                String query = "UPDATE profesionales SET foto = ? WHERE idprofesionales = ?";
                PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement(query);
                actualizar.setString(1, profesional.getFoto());
                actualizar.setInt(2, profesional.getIdProfesional());
                actualizar.executeUpdate();               
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("Error en la conexión actualizarFoto.");
        }

    }//Fin método actualizarFoto.

    public void perfilProfesional(int idProfesional) {

        if (DBController.getInstance().getConnection() != null) {
            try {
                String query = "SELECT * FROM profesionales WHERE idprofesionales = ?";
                PreparedStatement obtenerDatos = DBController.getInstance().getConnection().prepareStatement(query);
                obtenerDatos.setInt(1, idProfesional);
                ResultSet resultado = obtenerDatos.executeQuery();
                if (resultado.next()) {
                    profesional.setIdProfesional(resultado.getInt("idprofesionales"));
                    profesional.setNombre(resultado.getString("nombre") + " " + resultado.getString("apellidoPaterno") + " " + resultado.getString("apellidoMaterno"));
                    profesional.setCelular(resultado.getString("celular"));
                    profesional.setTelefonoLocal(resultado.getString("telefonoLocal"));
                    profesional.setDireccion(resultado.getString("direccion"));
                    profesional.setCorreo(resultado.getString("correo"));
                    profesional.setFechaNacimiento(resultado.getDate("fechaNacimiento").toString());
                    profesional.setFechaRegistro(resultado.getDate("fechaDeRegistro").toString());
                    profesional.setFoto(resultado.getString("foto"));
                    profesional.setDescripcion(resultado.getString("descripcion"));  
                    profesionalRegistrado = true;
                } else {
                    profesionalRegistrado = false;                 
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("Error en la conexión iniciarSesion.");
        }
    }//Fin método perfilProfesional.

    public Profesional datosSesion() {
        if (profesionalRegistrado) {
            return profesional;
        } else {
            return null;
        }
    }//Fin método datosSesion.

    public void nuevoProfesional(JSONObject jsonProfesional) {
        profesional.setNombre(String.valueOf(jsonProfesional.get("nombre")));
        profesional.setApellidoPaterno(jsonProfesional.get("paterno").toString());
        profesional.setApellidMaterno(jsonProfesional.get("materno").toString());
        profesional.setCorreo(String.valueOf(jsonProfesional.get("correo")));
        profesional.setContrasena(String.valueOf(jsonProfesional.get("contrasena")));
        java.sql.Date dateFechaNacimiento = (java.sql.Date.valueOf(String.valueOf(jsonProfesional.get("fechaNacimiento"))));
        profesional.setFechaNacimiento(dateFechaNacimiento.toString());
        profesional.setFechaRegistro(ControladorFechaActual.getFechaActual());
        profesional.setCelular(jsonProfesional.get("celular").toString());
        profesional.setTelefonoLocal(jsonProfesional.get("telefono").toString());
        profesional.setDireccion(jsonProfesional.get("direccion").toString());
        profesional.setGenero(jsonProfesional.get("genero").toString());
    }
 
    public JSONObject guardarProfesional() {
        JSONObject respuesta = new JSONObject();
        
        if (DBController.getInstance().getConnection() != null) {
            try {
                
                //VALIDAMOS QUE NO EXISTA EL CORREO
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM profesionales WHERE correo = ?");
                consulta.setString(1, profesional.getCorreo());
                ResultSet resultadoCorreo = consulta.executeQuery();
                
                if(resultadoCorreo.next()){
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", "Correo electronico ya registrado.");
                }else{
                    
                    //VALIDAMOS QUE NO EXISTA EL TELEFONO
                    PreparedStatement consultaTel = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM profesionales WHERE celular = ? || telefonoLocal = ?");
                    consultaTel.setString(1, profesional.getCelular());
                    consultaTel.setString(2, profesional.getTelefonoLocal());
                    ResultSet resultadoTelefono = consultaTel.executeQuery();
                    
                    if(resultadoTelefono.next()){
                        respuesta.put("respuesta", false);
                        respuesta.put("mensaje", "Teléfono celular o local ya registrado.");
                    }else{
                        //CREAMOS TOKEN
                        MD5 md5 = new MD5();
                        String token = md5.getMD5(profesional.getCorreo() + profesional.getNombre());
                        String pass = md5.getMD5(profesional.getContrasena());
                        String query = "INSERT INTO profesionales (nombre, correo, contrasena, fechaNacimiento, fechaDeRegistro, apellidoPaterno, apellidoMaterno,"
                                + " celular, telefonoLocal, direccion, genero, token_verificacion) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                        PreparedStatement agregarDatos = DBController.getInstance().getConnection().prepareStatement(query);
                        agregarDatos.setString(1, profesional.getNombre());
                        agregarDatos.setString(2, profesional.getCorreo());
                        agregarDatos.setString(3, pass);
                        agregarDatos.setDate(4, java.sql.Date.valueOf(profesional.getFechaNacimiento()));
                        agregarDatos.setDate(5, java.sql.Date.valueOf(profesional.getFechaRegistro()));
                        agregarDatos.setString(6, profesional.getApellidoPaterno());
                        agregarDatos.setString(7, profesional.getApellidMaterno());
                        agregarDatos.setString(8, profesional.getCelular());
                        agregarDatos.setString(9, profesional.getTelefonoLocal());
                        agregarDatos.setString(10, profesional.getDireccion());
                        agregarDatos.setString(11, profesional.getGenero());
                        agregarDatos.setString(12, token);
                        agregarDatos.execute();

                        respuesta.put("token", token);
                        respuesta.put("respuesta", true);
                        respuesta.put("mensaje", "Profesional registrado exitosamente.");
                    }
                    
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
    }//Fin método guardarProfesional.

    public JSONObject obtenerCuentaBancaria(int idProfesional) {
        JSONObject respuesta = new JSONObject();
        JSONObject datos = new JSONObject();

        if (DBController.getInstance().getConnection() != null) {
            try {
                Statement estado = DBController.getInstance().getConnection().createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM datosbancariosprofesionales WHERE profesionales_idprofesionales = " + idProfesional);
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
        JSONObject respuesta = new JSONObject();
        
        if (DBController.getInstance().getConnection() != null) {
            try {
                String query = "INSERT INTO datosbancariosprofesionales (nombreTitular, banco, clabe, profesionales_idprofesionales) VALUES (?,?,?,?)";
                PreparedStatement agregarDatos = DBController.getInstance().getConnection().prepareStatement(query);
                agregarDatos.setString(1, jsonDatos.get("nombreTitular").toString());
                agregarDatos.setString(2, jsonDatos.get("banco").toString());
                agregarDatos.setString(3, jsonDatos.get("clabe").toString());
                agregarDatos.setInt(4, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
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
        JSONObject respuesta = new JSONObject();
        
        if (DBController.getInstance().getConnection() != null) {
            try {               
                PreparedStatement consultaRegistro = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM datosbancariosprofesionales WHERE profesionales_idprofesionales = ?");
                consultaRegistro.setInt(1 , Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                ResultSet resultado = consultaRegistro.executeQuery();
                
                if(resultado.next()){                   
                    String query = "UPDATE datosbancariosprofesionales SET nombreTitular = ?, banco = ?, clabe = ? WHERE profesionales_idprofesionales = ?";
                    PreparedStatement agregarDatos = DBController.getInstance().getConnection().prepareStatement(query);
                    agregarDatos.setString(1, jsonDatos.get("nombreTitular").toString());
                    agregarDatos.setString(2, jsonDatos.get("banco").toString());
                    agregarDatos.setString(3, jsonDatos.get("clabe").toString());
                    agregarDatos.setInt(4, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                    agregarDatos.executeUpdate();
                }else{                 
                    PreparedStatement insert = DBController.getInstance().getConnection().prepareStatement("INSERT INTO datosbancariosprofesionales (nombreTitular, banco, clabe, profesionales_idprofesionales) VALUES (?,?,?,?)");
                    insert.setString(1, jsonDatos.get("nombreTitular").toString());
                    insert.setString(2, jsonDatos.get("banco").toString());
                    insert.setString(3, jsonDatos.get("clabe").toString());
                    insert.setInt(4 , Integer.parseInt(jsonDatos.get("idProfesional").toString()));
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
