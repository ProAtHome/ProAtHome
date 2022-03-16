package com.proathome.controladores;

import com.proathome.modelos.Cliente;
import com.proathome.modelos.Constantes;
import com.proathome.modelos.CuentaBancaria;
import com.proathome.modelos.EvaluacionCliente;
import com.proathome.modelos.Sesion;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import com.proathome.mysql.DBController;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ControladorCliente {

    /*
    *
    *Servicio controladora de Clientes.
    *
     */
    private Cliente cliente = new Cliente();
    private Sesion sesion = new Sesion();
    private boolean clienteRegistrado = false;
    
    public JSONObject getInicioSesion(int idCliente){
        JSONObject respuesta = new JSONObject();
        JSONObject data = new JSONObject();
          
        if(DBController.getInstance().getConnection() != null){
            JSONObject disponibilidad = getDisponibilidadServicio(idCliente);
            
            //GET DISPONIBILIDAD SERVICIO
            if(Boolean.parseBoolean(disponibilidad.get("respuesta").toString())){
                JSONObject dataDisponibilidad = (JSONObject) disponibilidad.get("mensaje");  
                data.put("disponibilidad", dataDisponibilidad.get("disponibilidad"));
                data.put("horasDisponibles", dataDisponibilidad.get("horasDisponibles"));
            }
                 
            //GET SESIONES PAGADAS & FINALIZADAS
            JSONObject sesionesPagadas = verificarSesionesPagadas(idCliente);
            if(Boolean.parseBoolean(sesionesPagadas.get("respuesta").toString())){
                JSONObject dataSesionesPagadas = (JSONObject) sesionesPagadas.get("mensaje");
                data.put("plan_activo", dataSesionesPagadas.get("plan_activo"));
                data.put("sesiones_pagadas_finalizadas", dataSesionesPagadas.get("sesiones_pagadas_finalizadas"));
            }
            
            //GET SESION ACTUAL
            ControladorRutaAprendizaje ruta = new ControladorRutaAprendizaje();
            JSONObject sesionActual = ruta.obtenerSesionActual(idCliente);
            if(Boolean.parseBoolean(sesionActual.get("respuesta").toString())){
                JSONObject dataSesionActual = (JSONObject) sesionActual.get("mensaje");
                
                data.put("idBloque", dataSesionActual.get("idBloque"));
                data.put("idNivel", dataSesionActual.get("idNivel"));
                data.put("idSeccion", dataSesionActual.get("idSeccion"));
                data.put("horas", dataSesionActual.get("horas"));
                data.put("rutaFinalizada", dataSesionActual.get("rutaFinalizada"));
            }
            
            //GET DATOS BANCO
            JSONObject datosBancarios = obtenerCuentaBancaria(idCliente);
            if(Boolean.parseBoolean(datosBancarios.get("respuesta").toString())){
                JSONObject dataDatosBancarios = (JSONObject) datosBancarios.get("mensaje");
                
                data.put("datosBancarios", dataDatosBancarios);
            }
            
            //VALIDAR PLAN
            JSONObject plan = verificarPlan(idCliente);
            if(Boolean.parseBoolean(plan.get("respuesta").toString())){
                JSONObject dataPlan = (JSONObject) plan.get("mensaje");
                
                data.put("plan", dataPlan);
            }
            
            respuesta.put("mensaje", data);
            respuesta.put("respuesta", true);
        }else{
            respuesta.put("mensaje", "Error en la conexión a BD.");
            respuesta.put("respuesta", false);
        }
          
        return respuesta;
    }
    
    public JSONObject iniciarPerfil(int idCliente){
        JSONObject respuesta = new JSONObject();
        JSONObject cliente = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            //INICIAMOS EL PROCESO DE RUTA SI NO EXISTE AUN
            iniciarProcesoRuta(idCliente);
            //CHECK PERFIL BLOQUEADO (PENDIENTE YA QUE NO EVALUAMOS PAGOS EN DEUDA)
            
            //GET PERFIL CLIENTE
            try {
                PreparedStatement consultar = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM planes WHERE clientes_idclientes = ?");
                consultar.setInt(1, idCliente);
                ResultSet resultadoPlan = consultar.executeQuery();
                
                if(!resultadoPlan.next()){
                    PreparedStatement plan = DBController.getInstance().getConnection().prepareStatement("INSERT INTO planes (clientes_idclientes) VALUES (?)");
                    plan.setInt(1, idCliente);
                    plan.execute();
                }

                String query = "SELECT * FROM clientes INNER JOIN planes WHERE clientes.idclientes = ? AND planes.clientes_idclientes = ?";
                PreparedStatement obtenerDatos = DBController.getInstance().getConnection().prepareStatement(query);
                obtenerDatos.setInt(1, idCliente);
                obtenerDatos.setInt(2, idCliente);
                ResultSet resultado = obtenerDatos.executeQuery();

                if(resultado.next()){
                    cliente.put("idCliente", resultado.getInt("idclientes"));
                    cliente.put("nombre", resultado.getString("nombre") + " " + resultado.getString("apellidoPaterno") + " " + resultado.getString("apellidoMaterno"));
                    cliente.put("celular", resultado.getString("celular"));
                    cliente.put("telefono", resultado.getString("telefonoLocal"));
                    cliente.put("direccion", resultado.getString("direccion"));
                    cliente.put("correo", resultado.getString("correo"));
                    cliente.put("fechaNacimiento", resultado.getDate("fechaNacimiento").toString());
                    cliente.put("fechaRegistro", resultado.getDate("fechaDeRegistro").toString());
                    cliente.put("foto", resultado.getString("foto"));
                    cliente.put("descripcion", resultado.getString("descripcion"));
                    
                    //VALIDAR PLAN
                    JSONObject respuestaPlan = verificarPlan(idCliente);

                    if(Boolean.parseBoolean(respuestaPlan.get("respuesta").toString())){
                        JSONObject plan = (JSONObject) respuestaPlan.get("mensaje");
                        cliente.put("fechaInicio", plan.get("fechaInicio") == null ? null : plan.get("fechaInicio").toString());
                        cliente.put("fechaFin", plan.get("fechaFin") == null ? null : plan.get("fechaFin").toString());
                        cliente.put("tipoPlan", plan.get("tipoPlan").toString());
                        cliente.put("monedero", plan.get("monedero").toString());
                    }
                    
                    respuesta.put("mensaje", cliente);
                    respuesta.put("respuesta", true);
                }else{
                    respuesta.put("mensaje", "Ocurrio un error, intente de nuevo mas tarde.");
                    respuesta.put("respuesta", false);
                }
            }catch(SQLException ex) {
                respuesta.put("mensaje", "Ocurrio un error, intente de nuevo mas tarde.");
                respuesta.put("respuesta", false);
            }
        }else{
            respuesta.put("mensaje", "Error en la conexión a BD.");
            respuesta.put("respuesta", false);
        }
        
        return respuesta;
    }
    
    public JSONObject getVerificacion(String token, String correo){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            //Obtenemos token de correo.
            try{
                PreparedStatement consultar = DBController.getInstance().getConnection().prepareStatement("SELECT token_verificacion, verificado FROM clientes WHERE correo = ?");
                consultar.setString(1, correo);
                ResultSet resultado = consultar.executeQuery();
                
                if(resultado.next()){
                    String token_bd = resultado.getString("token_verificacion");
                    boolean verificado = resultado.getBoolean("verificado");
                    
                    if(token_bd.equalsIgnoreCase(token)){
                        //Verificamos el correo.
                        PreparedStatement verificar = DBController.getInstance().getConnection().prepareStatement("UPDATE clientes SET verificado = ? WHERE correo = ?");
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
                PreparedStatement passAnterior = DBController.getInstance().getConnection().prepareStatement("SELECT contrasena FROM clientes WHERE idclientes = ?");
                passAnterior.setInt(1, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                ResultSet resultPassAnt = passAnterior.executeQuery();
                
                if(resultPassAnt.next()){
                    String passAntBD = resultPassAnt.getString("contrasena");
                    MD5 md5 = new MD5();
                    String passAnt = md5.getMD5(jsonDatos.get("actual").toString());
                    //Validamos si son iguales.
                    if(passAnt.equals(passAntBD)){
                        //Guardamos la nueva contrasena.
                        String nuevaPass = md5.getMD5(jsonDatos.get("nueva").toString());
                        PreparedStatement guardarPass = DBController.getInstance().getConnection().prepareStatement("UPDATE clientes SET contrasena = ? WHERE idclientes = ?");
                        guardarPass.setString(1, nuevaPass);
                        guardarPass.setInt(2, Integer.parseInt(jsonDatos.get("idCliente").toString()));
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
    
    public JSONObject getDisponibilidadServicio(int idCliente){
        JSONObject data = new JSONObject();
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
                try{
                    //Obtenemos el nivel actual
                    PreparedStatement nivelActual = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM rutaaprendizaje WHERE idrutaAprendizaje = (SELECT MAX(idrutaAprendizaje) FROM rutaaprendizaje WHERE clientes_idclientes = ? AND enruta = ?)");
                    nivelActual.setInt(1, idCliente);
                    nivelActual.setBoolean(2, true);
                    ResultSet resultadoNivel = nivelActual.executeQuery();
                    
                    if(resultadoNivel.next()){
                        int idNivel = resultadoNivel.getInt("idNivel");
                        int idBloque = resultadoNivel.getInt("idBloque");
                        int idSeccion = resultadoNivel.getInt("idSeccion");
                        int horasFinalizadas = resultadoNivel.getInt("horas");
                        int horasBloque = Constantes.obtenerHorasBloque(idSeccion, idNivel, idBloque);
                        
                        //Obtenemos las horas de las sesiones creadas en este nivel sin finalizar ni sumar.
                        PreparedStatement sesionesCreadas = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE idSeccion = ? AND idNivel = ? AND idBloque = ? AND finalizado = ? AND sumar = ?");
                        sesionesCreadas.setInt(1, idSeccion);
                        sesionesCreadas.setInt(2, idNivel);
                        sesionesCreadas.setInt(3, idBloque);
                        sesionesCreadas.setBoolean(4, false);
                        sesionesCreadas.setBoolean(5, true);
                        ResultSet resultadoSesiones = sesionesCreadas.executeQuery();
                        
                        int horasEspera = 0;
                        while(resultadoSesiones.next()){
                            horasEspera += resultadoSesiones.getInt("tiempo");
                        }
                        
                        int horasRestantes = horasBloque-horasFinalizadas;
                        int horasDisponibles = horasRestantes-horasEspera;
                        
                        if(horasDisponibles >= 1){
                            data.put("disponibilidad", true);
                            data.put("horasDisponibles", horasDisponibles);
                        }else{
                            data.put("disponibilidad", false);
                            data.put("horasDisponibles", horasDisponibles);
                        }
                        respuesta.put("mensaje", data);
                        respuesta.put("respuesta", true);
                        /*
                        respuesta.put("idNivel", resultadoNivel.getInt("idNivel"));
                        respuesta.put("idSeccion", resultadoNivel.getInt("idSeccion"));
                        respuesta.put("idBloque", resultadoNivel.getInt("idBloque"));
                        respuesta.put("horas", resultadoNivel.getInt("horas"));
                        respuesta.put("horasBloque", horasBloque);
                        respuesta.put("horasEspera", horasEspera);
                        respuesta.put("horasRestantes", horasRestantes);
                        respuesta.put("horasDisponibles", horasDisponibles);*/
                    }else{
                        respuesta.put("mensaje", "Error en la consulta a BD.");
                        respuesta.put("respuesta", false);
                    }
                }catch(SQLException ex){
                    ex.printStackTrace();
                    respuesta.put("mensaje", "Error en la consulta a BD.");
                    respuesta.put("respuesta", false);
                }
        }else{
            respuesta.put("mensaje", "Error en la conexión a BD.");
            respuesta.put("respuesta", false);
        }
        
        return respuesta;
    }
    
    public JSONObject actualizarPagoTE(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement pago = DBController.getInstance().getConnection().prepareStatement("SELECT pagos_idpagos FROM sesiones WHERE idsesiones = ?");
                pago.setInt(1, Integer.parseInt(jsonDatos.get("idSesion").toString()));
                ResultSet resultado = pago.executeQuery();
                
                if(resultado.next()){
                    PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE pagos SET costoTE = ? WHERE idpagos = ?");
                    actualizar.setDouble(1, Double.parseDouble(jsonDatos.get("cobro").toString()));
                    actualizar.setInt(2, resultado.getInt("pagos_idpagos"));
                    actualizar.execute();
                    respuesta.put("respuesta", true);
                    respuesta.put("mensaje", "Pago de TE actualziado exitosamente.");
                }else{
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", "Error la consultar el pago.");
                }
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión en BD.");
            }
        }else{
            respuesta.put("respuesta", false);
            respuesta.put("mensaje", "Error en la conexión en BD.");
        }
        
        return respuesta;
    }
    
    public JSONObject sumarMonedero(JSONObject jsonDatos){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                //Obtenemos las horas actuales del plan actual.
               PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT monedero, tipoPlan FROM planes WHERE clientes_idclientes = ?");
               consulta.setInt(1, Integer.parseInt(jsonDatos.get("idCliente").toString()));
               ResultSet resultado = consulta.executeQuery();
               
               if(resultado.next()){
                   int monedero = resultado.getInt("monedero");
                   int nuevoMonedero = monedero + Integer.parseInt(jsonDatos.get("tiempoMonedero").toString());
                   //agregamos
                   String query = null;
                   if(resultado.getString("tipoPlan").equals("PARTICULAR"))
                       query = "UPDATE planes SET tipoPlan = ?, monedero = ? WHERE clientes_idclientes = ?";
                   else
                       query = "UPDATE planes SET monedero = ? WHERE clientes_idclientes = ?";
                   PreparedStatement agregar = DBController.getInstance().getConnection().prepareStatement(query);
                   if(resultado.getString("tipoPlan").equals("PARTICULAR")){
                       agregar.setString(1, "PARTICULAR_PLAN");
                       agregar.setInt(2, nuevoMonedero);
                       agregar.setInt(3, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                   }else{
                       agregar.setInt(1, nuevoMonedero);
                       agregar.setInt(2, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                   }
                   
                   agregar.execute();
                   
                   respuesta.put("respuesta", true);
                   respuesta.put("mensaje", "Tiempo agregago con éxito a el PLAN ACTUAL.");
               }else{
                   respuesta.put("respuesta", false);
                   respuesta.put("mensaje", "Error en la conexión a BD.");
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
    
    public JSONObject getDatosFiscales(int idCliente){
        JSONObject respuesta = new JSONObject();
        JSONObject datos = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM datosfiscalesclientes WHERE clientes_idclientes = ?");
                consulta.setInt(1, idCliente);
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
        
        if(DBController.getInstance().getConnection() != null){
            try{
                //Consultar si hay registro.
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM datosfiscalesclientes WHERE clientes_idclientes = ?");
                consulta.setInt(1, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                ResultSet resultado = consulta.executeQuery();
                
                if(resultado.next()){
                    //Actualizamos
                    PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE datosfiscalesclientes SET tipoPersona = ?, razonSocial = ?, rfc = ?, cfdi = ? WHERE clientes_idclientes = ?");
                    actualizar.setString(1, jsonDatos.get("tipoPersona").toString());
                    actualizar.setString(2, jsonDatos.get("razonSocial").toString());
                    actualizar.setString(3, jsonDatos.get("rfc").toString());
                    actualizar.setString(4, jsonDatos.get("cfdi").toString());
                    actualizar.setInt(5, Integer.parseInt(jsonDatos.get("idCliente").toString()));
                    actualizar.execute();
                }else{
                    //Guardamos
                    PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO datosfiscalesclientes (tipoPersona, razonSocial, rfc, cfdi, clientes_idclientes) VALUES (?,?,?,?,?)");
                    actualizar.setString(1, jsonDatos.get("tipoPersona").toString());
                    actualizar.setString(2, jsonDatos.get("razonSocial").toString());
                    actualizar.setString(3, jsonDatos.get("rfc").toString());
                    actualizar.setString(4, jsonDatos.get("cfdi").toString());
                    actualizar.setInt(5, Integer.parseInt(jsonDatos.get("idCliente").toString()));
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
    
    public void bloquearPerfilEstatus(int idCliente){
        try{
            PreparedStatement bloquear = DBController.getInstance().getConnection().prepareStatement("UPDATE clientes SET estado = ? WHERE idclientes = ?");
            bloquear.setString(1, "BLOQUEADO");
            bloquear.setInt(2, idCliente);
            bloquear.execute();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public JSONObject getReportes(int idCliente){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement reportes = DBController.getInstance().getConnection().prepareStatement("SELECT COUNT(*) AS num FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
                reportes.setInt(1, idCliente);
                reportes.setString(2, "CLIENTE");
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
                            bloquearPerfil(idCliente, conectar);*/
                        PreparedStatement descripcion = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM reportes WHERE idUsuario = ? AND tipoUsuario = ?");
                        descripcion.setInt(1, idCliente);
                        descripcion.setString(2, "CLIENTE");
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
    
    public JSONObject estatusDocumentos(int idCliente){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement documentos = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM documentacioncliente WHERE clientes_idclientes = ?");
                documentos.setInt(1, idCliente  );
                ResultSet resultado = documentos.executeQuery();
                
                respuesta.put("respuesta", true);
                
                if(resultado.next())
                    respuesta.put("estatus", true);
                else
                    respuesta.put("estatus", false);
            }catch(SQLException ex){
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD");
                ex.printStackTrace();
            }
        }else
            System.out.println("Error en estatusDocumentos.");
        
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
    
    public JSONObject obtenerTickets(int idCliente){
        JSONArray jsonTickets = new JSONArray();
        JSONObject respuesta = new JSONObject();
        boolean vacio = true;
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM tickets_ayuda WHERE idUsuario = ? AND tipoUsuario = ? ORDER BY idtickets_ayuda DESC");
                consulta.setInt(1, idCliente);
                consulta.setInt(2, Constantes.TIPO_USUARIO_CLIENTE);
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
                respuesta.put("mensaje", "Error en la conexión a BD");
            }
        }else{
            System.out.println("Error en obtenerTickets.");
        }

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
    
    public void saldarDeuda(JSONObject jsonDatos){
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement saldarDeuda = DBController.getInstance().getConnection().prepareStatement("UPDATE pagos SET estatusPago = ? WHERE idSesion = ?");
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
    
    public JSONObject bloquearPerfil(int idCliente){
        JSONObject bloquearJSON = new JSONObject();
        JSONObject respuesta = new JSONObject();
        boolean bloquear = false;
        
        if(DBController.getInstance().getConnection() != null){
            try{
                //Recorrer cada sesion.
                PreparedStatement sesiones = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM pagos INNER JOIN sesiones WHERE sesiones.clientes_idclientes = ? AND sesiones.pagos_idpagos = pagos.idpagos");
                sesiones.setInt(1, idCliente);
                ResultSet resultado = sesiones.executeQuery();
                
                while(resultado.next()){
                    if(resultado.getBoolean("finalizado") && resultado.getString("estatusPago").equalsIgnoreCase("Deuda")){
                        bloquear = true;
                        bloquearJSON.put("bloquear", bloquear);
                        bloquearJSON.put("idSesion", resultado.getInt("idSesion"));
                        bloquearJSON.put("deuda", resultado.getDouble("costoServicio") + resultado.getDouble("costoTE"));
                        bloquearJSON.put("idSeccion", resultado.getInt("idSeccion"));
                        bloquearJSON.put("idNivel", resultado.getInt("idNivel"));
                        bloquearJSON.put("idBloque", resultado.getInt("idBloque"));
                        bloquearJSON.put("lugar", resultado.getString("lugar"));
                    }
                }
                
                PreparedStatement cliente = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM clientes WHERE idclientes = ?");
                cliente.setInt(1, idCliente);
                ResultSet resultadoEst = cliente.executeQuery();
                
                if(resultadoEst.next()){
                    bloquearJSON.put("nombre", resultadoEst.getString("nombre"));
                    bloquearJSON.put("correo", resultadoEst.getString("correo"));
                }
                
                bloquearJSON.put("bloquear", bloquear);
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", bloquearJSON);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Error en la conexión a BD");
            }
        }else{
            System.out.println("Error en bloquearPerfil.");
        }
        
        return respuesta;
    }
    
    public JSONObject validarValoracion(int idSesion, int idProfesional){
       JSONObject jsonRespuesta = new JSONObject();
       
       if(DBController.getInstance().getConnection() != null){
           try{
               PreparedStatement validar = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM valoracionprofesional WHERE sesiones_idsesiones = ? AND profesionales_idprofesionales = ?");
               validar.setInt(1, idSesion);
               validar.setInt(2, idProfesional);
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
    
    public void valorarProfesional(JSONObject jsonDatos){
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement valorar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO valoracionprofesional (profesionales_idprofesionales, clientes_idclientes, valoracion, comentario, sesiones_idsesiones) VALUES (?,?,?,?,?)");
                valorar.setInt(1, Integer.parseInt(jsonDatos.get("idProfesional").toString()));
                valorar.setInt(2, Integer.parseInt(jsonDatos.get("idCliente").toString()));
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
    
    public JSONArray obtenerValoracion(int idProfesional){
        JSONObject profesionalJSON = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        
        if(DBController.getInstance().getConnection() != null){
            //Obtener la info del profesional.
            try{
                PreparedStatement profesional = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM profesionales WHERE idprofesionales = ?");
                profesional.setInt(1, idProfesional);
                ResultSet resultado = profesional.executeQuery();
                
                if(resultado.next()){
                    //Obtener promedio
                    profesionalJSON.put("idProfesional", resultado.getInt("idprofesionales"));
                    profesionalJSON.put("nombre", resultado.getString("nombre"));
                    profesionalJSON.put("correo", resultado.getString("correo"));
                    profesionalJSON.put("fechaDeRegistro", resultado.getDate("fechaDeRegistro").toString());
                    profesionalJSON.put("foto", resultado.getString("foto"));
                    profesionalJSON.put("descripcion", resultado.getString("descripcion"));
                    profesionalJSON.put("valoraciones", false);
                    jsonArray.add(profesionalJSON);
                    
                    //Obtener Valoraciones
                    PreparedStatement valoraciones = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM valoracionprofesional WHERE profesionales_idprofesionales = ?");
                    valoraciones.setInt(1, idProfesional);
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
    
    public void finalizarPlan(int idCliente){
        boolean finalizar = true;
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement verificar = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE NOT tipoPlan = ? AND clientes_idclientes = ?");
                verificar.setString(1, "PARTICULAR");
                verificar.setInt(2, idCliente);
                ResultSet resultadoVerificar = verificar.executeQuery();
                
                while(resultadoVerificar.next()){
                    if(!resultadoVerificar.getBoolean("finalizado"))
                        finalizar = false;
                }
                
                if(finalizar){
                    PreparedStatement finalizar2 = DBController.getInstance().getConnection().prepareStatement("UPDATE planes SET tipoPlan = ?, fechaInicio = ?, fechaFin = ?, monedero = ? WHERE clientes_idclientes = ?");
                    finalizar2.setString(1, "PARTICULAR");
                    finalizar2.setDate(2, null);
                    finalizar2.setDate(3, null);
                    finalizar2.setInt(4, 0);
                    finalizar2.setInt(5, idCliente);
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
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE planes SET monedero = ? WHERE clientes_idclientes = ?");
                actualizar.setInt(1, Integer.parseInt(jsonMonedero.get("nuevoMonedero").toString()));
                actualizar.setInt(2, Integer.parseInt(jsonMonedero.get("idCliente").toString()));
                actualizar.execute();
                
                //Validar plan.
                PreparedStatement planActivo = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM planes WHERE clientes_idclientes = ?");
                planActivo.setInt(1, Integer.parseInt(jsonMonedero.get("idCliente").toString()));
                ResultSet resultadoPlanActivo = planActivo.executeQuery();
                
                if(resultadoPlanActivo.next()){
                    //¿Hay plan activo?
                    if(!resultadoPlanActivo.getString("tipoPlan").equalsIgnoreCase("PARTICULAR") && !resultadoPlanActivo.getString("tipoPlan").equalsIgnoreCase("PARTICULAR_PLAN")){
                         
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
                                finalizarPlan(Integer.parseInt(jsonMonedero.get("idCliente").toString()));
                            }
                        }else{
                            //Finalizar plan activo y pasarlo a PARTICULAR
                            finalizarPlan(Integer.parseInt(jsonMonedero.get("idCliente").toString()));
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
    
    
    public JSONObject verificarPlan(int idCliente){
        JSONObject response = new JSONObject();
        JSONObject jsonPlan = new JSONObject();
        try{
            PreparedStatement planActivo = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM planes WHERE clientes_idclientes = ?");
            planActivo.setInt(1, idCliente);
            ResultSet resultadoPlanActivo = planActivo.executeQuery();

            if(resultadoPlanActivo.next()){
                //¿Hay plan activo?
                if(resultadoPlanActivo.getString("tipoPlan").equalsIgnoreCase("PARTICULAR")){
                    jsonPlan.put("tipoPlan", resultadoPlanActivo.getString("tipoPlan"));
                    jsonPlan.put("monedero", resultadoPlanActivo.getInt("monedero"));
                    jsonPlan.put("fechaInicio", resultadoPlanActivo.getDate("fechaInicio") == null ? null : resultadoPlanActivo.getDate("fechaInicio").toString());
                    jsonPlan.put("fechaFin", resultadoPlanActivo.getDate("fechaFin") == null ? null : resultadoPlanActivo.getDate("fechaFin").toString());
                    response.put("respuesta", true);
                    response.put("mensaje", jsonPlan);
                }else{//Si
                    //¿Hay horas en el monedero en el PLAN activo?
                    if(resultadoPlanActivo.getInt("monedero") != 0){
                        if(!resultadoPlanActivo.getString("tipoPlan").equalsIgnoreCase("PARTICULAR_PLAN")){
                            //¿El plan ya expiró?
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Calendar calendar = Calendar.getInstance();
                            String fechaHoy = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);

                            Date fechaActual = sdf.parse(fechaHoy);
                            Date fechaFin = sdf.parse(resultadoPlanActivo.getDate("fechaFin").toString());
                            if(fechaFin.equals(fechaActual) || fechaActual.after(fechaFin)){
                                //Finalizar plan activo y pasarlo a PARTICULAR
                                finalizarPlan(idCliente);
                            }else{
                                jsonPlan.put("tipoPlan", resultadoPlanActivo.getString("tipoPlan"));
                                jsonPlan.put("monedero", resultadoPlanActivo.getInt("monedero"));
                                jsonPlan.put("fechaInicio", resultadoPlanActivo.getDate("fechaInicio").toString());
                                jsonPlan.put("fechaFin", resultadoPlanActivo.getDate("fechaFin").toString());
                                response.put("respuesta", true);
                                response.put("mensaje", jsonPlan);
                            }
                        }

                    }else{
                        //Finalizar plan activo y pasarlo a PARTICULAR
                        finalizarPlan(idCliente);
                    }
                }

            }

            PreparedStatement planActivo2 = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM planes WHERE clientes_idclientes = ?");
            planActivo2.setInt(1, idCliente);
            ResultSet resultadoPlanActivo2 = planActivo2.executeQuery();
            if(resultadoPlanActivo2.next()){
                jsonPlan.put("tipoPlan", resultadoPlanActivo2.getString("tipoPlan"));
                jsonPlan.put("monedero", resultadoPlanActivo2.getInt("monedero"));
                jsonPlan.put("fechaInicio", resultadoPlanActivo2.getDate("fechaInicio") == null ? null : resultadoPlanActivo2.getDate("fechaInicio").toString());
                jsonPlan.put("fechaFin", resultadoPlanActivo2.getDate("fechaFin") == null ? null : resultadoPlanActivo2.getDate("fechaFin").toString());
                response.put("respuesta", true);
                response.put("mensaje", jsonPlan);
            }

        }catch(SQLException ex){
            ex.printStackTrace();
            response.put("respuesta", false);
            response.put("mensaje", "Eror en el servidor.");
        }catch(java.text.ParseException ex){
            ex.printStackTrace();
            response.put("respuesta", false);
            response.put("mensaje", "Eror en el servidor.");
        }

        return response;
    }
    
    public void generarPlan(JSONObject jsonPlan){
        if(DBController.getInstance().getConnection() != null){
            try{
                //Actualizar plan en el perfil.
                PreparedStatement actualizarPlan = DBController.getInstance().getConnection().prepareStatement("UPDATE planes SET tipoPlan = ?, fechaInicio = ?, fechaFin = ?, monedero = ? WHERE clientes_idclientes = ?");
                actualizarPlan.setString(1, jsonPlan.get("tipoPlan").toString());
                actualizarPlan.setDate(2, java.sql.Date.valueOf(jsonPlan.get("fechaInicio").toString()));
                actualizarPlan.setDate(3, java.sql.Date.valueOf(jsonPlan.get("fechaFin").toString()));
                actualizarPlan.setInt(4, Integer.parseInt(jsonPlan.get("monedero").toString()));
                actualizarPlan.setInt(5, Integer.parseInt(jsonPlan.get("idCliente").toString()));
                actualizarPlan.execute();
                //Guradar historial.
                PreparedStatement historial = DBController.getInstance().getConnection().prepareStatement("INSERT INTO historialPlanes (tipoPlan, fechaInicio, fechaFin, monedero, clientes_idclientes) VALUES (?,?,?,?,?)");
                historial.setString(1, jsonPlan.get("tipoPlan").toString());
                historial.setDate(2, java.sql.Date.valueOf(jsonPlan.get("fechaInicio").toString()));
                historial.setDate(3, java.sql.Date.valueOf(jsonPlan.get("fechaFin").toString()));
                historial.setInt(4, Integer.parseInt(jsonPlan.get("monedero").toString()));
                historial.setInt(5, Integer.parseInt(jsonPlan.get("idCliente").toString()));
                historial.execute();
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else{
            System.out.println("Error en generar Plan.");
        }
    }
    
    public JSONObject verificarSesionesPagadas(int idCliente){
        boolean plan_activo = false;
        boolean sesiones_pagadas_finalizadas = true;
        JSONObject jsonDatos = new JSONObject();
        JSONObject respuesta = new JSONObject();

        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement contador = DBController.getInstance().getConnection().prepareStatement("SELECT COUNT(*) AS numero FROM pagos INNER JOIN sesiones WHERE sesiones.pagos_idpagos = pagos.idpagos AND sesiones.clientes_idclientes = ?");
                contador.setInt(1, idCliente);
                ResultSet resultadoContador = contador.executeQuery();
                
                if(resultadoContador.next()){
                    if(resultadoContador.getInt("numero") < 3){
                        plan_activo = false;
                        sesiones_pagadas_finalizadas = false;
                    }else{
                        //Contar las sesiones que tenemos y ver que todas esten finalizadas o valen vrg
                        PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE clientes_idclientes = ?");
                        consulta.setInt(1, idCliente);
                        ResultSet resultado = consulta.executeQuery();

                        while(resultado.next()){
                            if(!resultado.getBoolean("finalizado"))
                                sesiones_pagadas_finalizadas = false;
                        }

                        //Aquí obtenemos el tipo PLAN y si está activo.
                        PreparedStatement plan = DBController.getInstance().getConnection().prepareStatement("SELECT tipoPlan FROM planes WHERE clientes_idclientes = ?");
                        plan.setInt(1, idCliente);
                        ResultSet resultadoPlan = plan.executeQuery();

                        if(resultadoPlan.next()){
                            if(!resultadoPlan.getString("tipoPlan").equals("PARTICULAR"))
                                plan_activo = true;
                        }else
                            plan_activo = false;
                    }
                }else{
                    plan_activo = false;
                    sesiones_pagadas_finalizadas = false;
                }
                
                jsonDatos.put("plan_activo", plan_activo);
                jsonDatos.put("sesiones_pagadas_finalizadas", sesiones_pagadas_finalizadas);
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", jsonDatos);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Eror en el servidor.");
            }
        }else{
            System.out.println("Error en verificarSesionesPagadas.");
        }
        
        return respuesta; 
    }
    
    public void iniciarPlan(JSONObject json){
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consultar = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM planes WHERE clientes_idclientes = ?");
                consultar.setInt(1, Integer.parseInt(json.get("idCliente").toString()));
                ResultSet resultado = consultar.executeQuery();
                
                if(resultado.next()){
                }else{
                    PreparedStatement plan = DBController.getInstance().getConnection().prepareStatement("INSERT INTO planes (clientes_idclientes) VALUES (?)");
                    plan.setInt(1, Integer.parseInt(json.get("idCliente").toString()));
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
        if(DBController.getInstance().getConnection() != null){
            try{
                if(json.get("tipoPlan").toString().equalsIgnoreCase("PARTICULAR")){
                    PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("UPDATE pagos SET costoServicio = ?, costoTE = ?, estatusPago = ? WHERE idCliente = ? AND idSesion = ?");
                    actualizar.setDouble(1, Double.parseDouble(json.get("costoServicio").toString()));
                    actualizar.setDouble(2, Double.parseDouble(json.get("costoTE").toString()));
                    actualizar.setString(3, json.get("estatusPago").toString());
                    actualizar.setInt(4, Integer.parseInt(json.get("idCliente").toString()));
                    actualizar.setInt(5, Integer.parseInt(json.get("idSesion").toString()));
                    actualizar.execute();
                }else{
                    PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO pagos (token, costoServicio, costoTE, estatusPago, idCliente, idSesion) VALUES (?,?,?,?,?,?)");
                    actualizar.setString(1, "TIPO PLAN - " + json.get("tipoPlan").toString());
                    actualizar.setDouble(2, Double.parseDouble(json.get("costoServicio").toString()));
                    actualizar.setDouble(3, Double.parseDouble(json.get("costoTE").toString()));
                    actualizar.setString(4, json.get("estatusPago").toString());
                    actualizar.setInt(5, Integer.parseInt(json.get("idCliente").toString()));
                    actualizar.setInt(6, Integer.parseInt(json.get("idSesion").toString()));
                    actualizar.execute();
                } 
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else
            System.out.println("Error en actualizarPago.");
    }
    
    public JSONObject obtenerToken(int idSesion, int idCliente){
        JSONObject respuesta = new JSONObject();
        JSONObject data = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT token FROM pagos WHERE idSesion = ? AND idCliente = ?");
                consulta.setInt(1, idSesion);
                consulta.setInt(2, idCliente);
                ResultSet resultado = consulta.executeQuery();
                if(resultado.next())
                    data.put("token", resultado.getString("token"));
                else
                    data.put("token", null);
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", data);
            }catch(SQLException ex){
                ex.printStackTrace();
                respuesta.put("respuesta", false);
                respuesta.put("mensaje", "Eror en el servidor.");
            }
        }else{
            System.out.println("Error en obtenerToken.");
        }
        
        return respuesta;
    }
    
    public JSONObject registrarServicio(JSONObject data){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
              try{
                //GUARDAR PAGO
                PreparedStatement guardar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO pagos (token, costoServicio, costoTE, estatusPago, idCliente) VALUES (?,?,?,?,?)");
                guardar.setString(1, data.get("token").toString());
                guardar.setDouble(2, Double.parseDouble(data.get("costoServicio").toString()));
                guardar.setDouble(3, Double.parseDouble(data.get("costoTE").toString()));
                guardar.setString(4, data.get("estatusPago").toString());
                guardar.setInt(5, Integer.parseInt(data.get("idCliente").toString()));
                guardar.execute();
                
                //GUARDAR SERVICIO
                sesion = null;
                sesion = new Sesion();
                nuevaSesion(data);
                guardarSesion(); 
                          
                //ACTUALIZAR MONEDERO
                actualizarMonedero(data);
                
                //VALIDAR PLAN
                JSONObject respuestaPlan = verificarPlan(Integer.parseInt(data.get("idCliente").toString()));

                if(Boolean.parseBoolean(respuestaPlan.get("respuesta").toString()))
                    respuesta.put("mensaje", respuestaPlan);
                else
                    respuesta.put("mensaje", "Servicio registrado exitosamente."); 
                              
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
    
    public JSONObject guardarTokenPagoServicio(JSONObject jsonToken){
        JSONObject respuesta = new JSONObject();

        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement guardar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO pagos (token, costoServicio, costoTE, estatusPago, idCliente) VALUES (?,?,?,?,?)");
                guardar.setString(1, jsonToken.get("token").toString());
                guardar.setDouble(2, Double.parseDouble(jsonToken.get("costoServicio").toString()));
                guardar.setDouble(3, Double.parseDouble(jsonToken.get("costoTE").toString()));
                guardar.setString(4, jsonToken.get("estatusPago").toString());
                guardar.setInt(5, Integer.parseInt(jsonToken.get("idCliente").toString()));
                guardar.execute();
                
                respuesta.put("respuesta", true);
                respuesta.put("mensaje", "Pago guardado exitosamente.");                
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
    
    public JSONObject obtenerPreOrden(int idCliente, int idSesion){
        JSONObject respuesta = new JSONObject();
        JSONObject dataResponse = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consultaSesion = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones WHERE idsesiones = ?");
                consultaSesion.setInt(1, idSesion);
                ResultSet resultadoSesiones = consultaSesion.executeQuery();
                
                if(resultadoSesiones.next()){                      
                    PreparedStatement consultaCliente = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM clientes WHERE idclientes = ?");
                    consultaCliente.setInt(1, idCliente);
                    ResultSet resultadoCliente = consultaCliente.executeQuery();
                    
                    if(resultadoCliente.next()){
                        PreparedStatement consultaDatosBancarios = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM datosbancariosclientes WHERE clientes_idclientes = ?");
                        consultaDatosBancarios.setInt(1, idCliente);
                        ResultSet resultadoDatosBancarios = consultaDatosBancarios.executeQuery();

                        if(resultadoDatosBancarios.next()){
                            respuesta.put("idCliente", idCliente);
                            respuesta.put("idSesion", idSesion);
                            respuesta.put("nombreCliente", resultadoCliente.getString("nombre"));
                            respuesta.put("correo", resultadoCliente.getString("correo"));
                            respuesta.put("nombreTitular", resultadoDatosBancarios.getString("nombreTitular"));
                            respuesta.put("tarjeta", resultadoDatosBancarios.getString("tarjeta"));
                            respuesta.put("mes", resultadoDatosBancarios.getString("mes"));
                            respuesta.put("ano", resultadoDatosBancarios.getInt("ano"));
                            respuesta.put("idSeccion", resultadoSesiones.getInt("idSeccion"));
                            respuesta.put("idNivel", resultadoSesiones.getInt("idNivel"));
                            respuesta.put("idBloque", resultadoSesiones.getInt("idBloque"));
                            respuesta.put("tiempo", resultadoSesiones.getInt("tiempo"));
                        }else{
                            respuesta.put("idCliente", idCliente);
                            respuesta.put("idSesion", idSesion);
                            respuesta.put("nombreCliente", resultadoCliente.getString("nombre"));
                            respuesta.put("correo", resultadoCliente.getString("correo"));
                            respuesta.put("nombreTitular", "");
                            respuesta.put("tarjeta", "");
                            respuesta.put("mes", "");
                            respuesta.put("ano", "");
                            respuesta.put("idSeccion", resultadoSesiones.getInt("idSeccion"));
                            respuesta.put("idNivel", resultadoSesiones.getInt("idNivel"));
                            respuesta.put("idBloque", resultadoSesiones.getInt("idBloque"));
                            respuesta.put("tiempo", resultadoSesiones.getInt("tiempo"));
                        }
                        
                        dataResponse.put("respuesta", true);
                        dataResponse.put("mensaje", respuesta);
                    }else{
                        dataResponse.put("respuesta", false);
                        dataResponse.put("mensaje", "Error en la conexión a BD.");
                        respuesta.put("error", "Error en los datos de Sesión."); 
                    }                                     
                }else{
                    dataResponse.put("respuesta", false);
                    dataResponse.put("mensaje", "Error en la conexión a BD.");
                    respuesta.put("error", "Error en los datos de Sesión."); 
                }
            }catch(SQLException ex){
                ex.printStackTrace();
                dataResponse.put("respuesta", false);
                dataResponse.put("mensaje", "Error en la conexión a BD.");
            }
        }else{
            System.out.println("Error obtenerPreOrden.");
        }
        
        return dataResponse;
    }
    
    public void iniciarProcesoRuta(int idCliente){ 
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM rutaaprendizaje WHERE clientes_idclientes = ?");
                consulta.setInt(1, idCliente);
                ResultSet resultado = consulta.executeQuery();
                
                if(!resultado.next()){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    String fechaHoy = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                    PreparedStatement iniciar = DBController.getInstance().getConnection().prepareStatement("INSERT INTO rutaaprendizaje (clientes_idclientes, idBloque, idNivel, idSeccion, horas, fecha_registro, enruta) VALUES (?,?,?,?,?,?,?)");
                    iniciar.setInt(1, idCliente);
                    iniciar.setInt(2, 1);
                    iniciar.setInt(3, 1);
                    iniciar.setInt(4, 1);
                    iniciar.setInt(5, 0);
                    iniciar.setString(6, fechaHoy);
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
    
    public Sesion nuevaSesion(JSONObject datos){
        sesion.setClientes_idclientes(Integer.parseInt(String.valueOf(datos.get("idCliente"))));
        sesion.setHorario(String.valueOf(datos.get("horario")));
        sesion.setLugar(String.valueOf(datos.get("lugar")));
        sesion.setTiempo(Integer.parseInt(datos.get("tiempo").toString()));
        sesion.setIdSeccion(Integer.parseInt(datos.get("idSeccion").toString()));
        sesion.setIdNivel(Integer.parseInt(datos.get("idNivel").toString()));
        sesion.setIdBloque(Integer.parseInt(datos.get("idBloque").toString()));
        sesion.setExtras(String.valueOf(datos.get("extras")));
        sesion.setTipoServicio(String.valueOf(datos.get("tipoServicio")));
        sesion.setLatitud(Double.valueOf(String.valueOf(datos.get("latitud"))));
        sesion.setLongitud(Double.valueOf(String.valueOf(datos.get("longitud"))));
        sesion.setFecha(datos.get("fecha").toString());
        sesion.setActualizado(datos.get("actualizado").toString());
        sesion.setSumar(Boolean.valueOf(datos.get("sumar").toString()));
        sesion.setTipoPlan(datos.get("tipoPlan").toString());
        sesion.setPersonas(Integer.parseInt(datos.get("personas").toString()));
        sesion.setToken(datos.get("token").toString());
        
        return sesion;
    }//Fin método nuevaSesion.
    
    public JSONObject detallesSesion(int idSesion){
        JSONObject jsonDetalles = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{
                PreparedStatement detalles = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM sesiones INNER JOIN clientes WHERE sesiones.clientes_idclientes = clientes.idclientes AND idsesiones = ?");
                detalles.setInt(1 , idSesion);
                ResultSet resultado = detalles.executeQuery();
                
                if(resultado.next()){
                    
                    if(resultado.getString("profesionales_idprofesionales") == null){
                        jsonDetalles.put("profesional", "Sin profesional asignado.");
                        jsonDetalles.put("fotoProfesional", "Sin foto");
                        jsonDetalles.put("descripcionProfesional", "Sin descripcion");
                        jsonDetalles.put("correoProfesional", "Sin correo");
                        jsonDetalles.put("idProfesional", 0);
                    }else{
                        PreparedStatement profesional = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM profesionales WHERE idprofesionales = ?");
                        profesional.setInt(1 , resultado.getInt("profesionales_idprofesionales"));
                        ResultSet resultadoProfesional = profesional.executeQuery();
                        
                        if(resultadoProfesional.next()){
                            jsonDetalles.put("profesional", resultadoProfesional.getString("nombre"));
                            jsonDetalles.put("fotoProfesional", resultadoProfesional.getString("foto"));
                            jsonDetalles.put("descripcionProfesional", resultadoProfesional.getString("descripcion"));
                            jsonDetalles.put("correoProfesional", resultadoProfesional.getString("correo"));
                            jsonDetalles.put("idProfesional", resultadoProfesional.getInt("idprofesionales"));
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
                    jsonDetalles.put("tipoServicio", resultado.getString("tipoServicio"));
                    jsonDetalles.put("latitud", resultado.getDouble("latitud"));
                    jsonDetalles.put("longitud", resultado.getDouble("longitud"));
                    jsonDetalles.put("actualizado", resultado.getDate("actualizado").toString());
                    jsonDetalles.put("nombre", resultado.getString("nombre"));
                    jsonDetalles.put("fecha", resultado.getString("fecha"));
                    jsonDetalles.put("correo", resultado.getString("correo"));
                    jsonDetalles.put("foto", resultado.getString("foto"));
                    jsonDetalles.put("descripcion", resultado.getString("descripcion"));
                    
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }else
            System.out.println("Error en detallesSesion.");

        return jsonDetalles;
        
    }//Fin métod detallesSesion.
    
    public void guardarSesion() throws SQLException{              
        //Consultamos el idPago con el token.
        PreparedStatement token = DBController.getInstance().getConnection().prepareStatement("SELECT idpagos FROM pagos WHERE token = ?");
        token.setString(1, sesion.getToken());
        ResultSet resultado = token.executeQuery();

        if(resultado.next()){
            String query = "INSERT INTO sesiones (clientes_idclientes, horario, lugar, tiempo, extras, tipoServicio,"
                + " latitud, longitud, actualizado, idSeccion, idNivel, idBloque, fecha, progreso, sumar, tipoPlan, personas, pagos_idpagos) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement agregarDatos = DBController.getInstance().getConnection().prepareStatement(query);
            agregarDatos.setInt(1, sesion.getClientes_idclientes());
            agregarDatos.setString(2, sesion.getHorario());
            agregarDatos.setString(3, sesion.getLugar());
            agregarDatos.setInt(4, sesion.getTiempo());
            agregarDatos.setString(5, sesion.getExtras());
            agregarDatos.setString(6, sesion.getTipoServicio());
            agregarDatos.setDouble(7, sesion.getLatitud());
            agregarDatos.setDouble(8, sesion.getLongitud());
            agregarDatos.setString(9, sesion.getActualizado());
            agregarDatos.setInt(10, sesion.getIdSeccion());
            agregarDatos.setInt(11, sesion.getIdNivel());
            agregarDatos.setInt(12, sesion.getIdBloque());
            agregarDatos.setString(13, sesion.getFecha());
            agregarDatos.setInt(14, 3);
            agregarDatos.setBoolean(15, sesion.getSumar());
            agregarDatos.setString(16, sesion.getTipoPlan());
            agregarDatos.setInt(17, sesion.getPersonas());
            agregarDatos.setInt(18, resultado.getInt("idpagos"));
            agregarDatos.execute();
        }       
    }//Fin método guardarSesion.
    
    public JSONObject obtenerCuentaBancaria(int idCliente){
        JSONObject respuesta = new JSONObject();
        JSONObject datos = new JSONObject();
        cliente.cuenta = new CuentaBancaria();
        
        if(DBController.getInstance().getConnection() != null){           
            try{               
                Statement estado = DBController.getInstance().getConnection().createStatement();
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
        cliente.setCelular(datos.get("celular").toString());
        cliente.setTelefonoLocal(datos.get("telefonoLocal").toString());
        cliente.setDireccion(datos.get("direccion").toString());
        cliente.setDescripcion(String.valueOf(datos.get("descripcion")));
        
    }//Fin método datosActualizarPerfil.
    
    public JSONObject actualizarDatosPerfil(){
        JSONObject respuesta = new JSONObject();
        
        if(DBController.getInstance().getConnection() != null){
            try{               
                String query = "UPDATE clientes SET celular = ?, telefonoLocal = ?, direccion = ?, descripcion = ? WHERE idclientes = ?";
                PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement(query);
                actualizar.setString(1, cliente.getCelular());
                actualizar.setString(2, cliente.getTelefonoLocal());
                actualizar.setString(3, cliente.getDireccion());
                actualizar.setString(4, cliente.getDescripcion());
                actualizar.setInt(5, cliente.getIdCliente());
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

    public void iniciarSesion(String correo, String contrasena) {
        try {
            System.out.println(contrasena);
            MD5 md5 = new MD5();
            String pass = md5.getMD5(contrasena);
            String query = "SELECT * FROM clientes WHERE BINARY correo = ? AND contrasena = ?";
            PreparedStatement obtenerDatos = DBController.getInstance().getConnection().prepareStatement(query);
            obtenerDatos.setString(1, correo);
            obtenerDatos.setString(2, pass);
            ResultSet resultado = obtenerDatos.executeQuery();
            System.out.println(pass);

            if (resultado.next()) {
                cliente.setToken(JWTController.getInstance().getToken(String.valueOf(resultado.getInt("idclientes")), JWTController.PERFIL_CLIENTE));
                cliente.setIdCliente(resultado.getInt("idclientes"));
                cliente.setNombre(resultado.getString("nombre"));
                cliente.setEstado(resultado.getString("estado"));
                cliente.setVerificado(resultado.getBoolean("verificado"));
                clienteRegistrado = true;
            } else {
                clienteRegistrado = false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//Fin método iniciarSesion.
    
    
    public void actualizarFoto(JSONObject foto){     
        cliente.setFoto(String.valueOf(foto.get("nombre")));
        cliente.setIdCliente(Integer.parseInt(String.valueOf(foto.get("idCliente"))));
        
        if(DBController.getInstance().getConnection() != null){
            try{
                String query = "UPDATE clientes SET foto = ? WHERE idclientes = ?";
                PreparedStatement actualizar = DBController.getInstance().getConnection().prepareStatement(query);
                actualizar.setString(1, cliente.getFoto());
                actualizar.setInt(2, cliente.getIdCliente());
                actualizar.executeUpdate();
            }catch(SQLException ex){
                System.out.println(ex.getMessage());  
            }
        }else
            System.out.println("Error en la conexión actualizarFoto.");
    }//Fin método actualizarFoto.

    public void perfilCliente(int idCliente) {
        if (DBController.getInstance().getConnection() != null) {
            try {
                PreparedStatement consultar = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM planes WHERE clientes_idclientes = ?");
                consultar.setInt(1, idCliente);
                ResultSet resultadoPlan = consultar.executeQuery();
                
                if(!resultadoPlan.next()){
                    PreparedStatement plan = DBController.getInstance().getConnection().prepareStatement("INSERT INTO planes (clientes_idclientes) VALUES (?)");
                    plan.setInt(1, idCliente);
                    plan.execute();
                }

                String query = "SELECT * FROM clientes INNER JOIN planes WHERE clientes.idclientes = ? AND planes.clientes_idclientes = ?";
                PreparedStatement obtenerDatos = DBController.getInstance().getConnection().prepareStatement(query);
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
                    cliente.setFechaNacimiento(resultado.getDate("fechaNacimiento").toString());
                    cliente.setFechaRegistro(resultado.getDate("fechaDeRegistro").toString());
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
        if (clienteRegistrado)
            return cliente;
        else
            return null;
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

        cliente.setFechaNacimiento(dateFechaNacimiento.toString());
        cliente.setFechaRegistro(java.sql.Date.valueOf(ControladorFechaActual.getFechaActual()).toString());

    }//Fin Constructor.

    public JSONObject guardarCliente() {
         JSONObject respuesta = new JSONObject();
         
        if (DBController.getInstance().getConnection() != null) {
            try {
                
                //VALIDAMOS QUE NO EXISTA EL CORREO
                PreparedStatement consulta = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM clientes WHERE correo = ?");
                consulta.setString(1, cliente.getCorreo());
                ResultSet resultadoCorreo = consulta.executeQuery();
                
                if(resultadoCorreo.next()){
                    respuesta.put("respuesta", false);
                    respuesta.put("mensaje", "Correo electronico ya registrado.");
                }else{
                    //VALIDAMOS QUE NO EXISTA TELEFONO
                    PreparedStatement consultaTel = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM clientes WHERE celular = ? || telefonoLocal = ?");
                    consultaTel.setString(1, cliente.getCelular());
                    consultaTel.setString(2, cliente.getTelefonoLocal());
                    ResultSet resultadoTelefono = consultaTel.executeQuery();
                    
                    if(resultadoTelefono.next()){
                        respuesta.put("respuesta", false);
                        respuesta.put("mensaje", "Teléfono celular o local ya registrado.");
                    }else{
                        //CREAMOS TOKEN
                        MD5 md5 = new MD5();
                        String token = md5.getMD5(cliente.getCorreo() + cliente.getNombre());
                        String pass = md5.getMD5(cliente.getContrasena());
                        String query = "INSERT INTO clientes (nombre, apellidoPaterno, apellidoMaterno, correo, celular, telefonoLocal, direccion, fechaNacimiento, genero, fechaDeRegistro, contrasena, estado, token_verificacion) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        PreparedStatement agregarDatos = DBController.getInstance().getConnection().prepareStatement(query);
                        agregarDatos.setString(1, cliente.getNombre());
                        agregarDatos.setString(2, cliente.getApellidoPaterno());
                        agregarDatos.setString(3, cliente.getApellidoMaterno());
                        agregarDatos.setString(4, cliente.getCorreo());
                        agregarDatos.setString(5, cliente.getCelular());
                        agregarDatos.setString(6, cliente.getTelefonoLocal());
                        agregarDatos.setString(7, cliente.getDireccion());
                        agregarDatos.setDate(8, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
                        agregarDatos.setString(9, cliente.getGenero());
                        agregarDatos.setDate(10, java.sql.Date.valueOf(cliente.getFechaRegistro()));
                        agregarDatos.setString(11, pass);
                        agregarDatos.setString(12, cliente.getEstado());
                        agregarDatos.setString(13, token);
                        agregarDatos.execute();

                        respuesta.put("token", token);
                        respuesta.put("respuesta", true);
                        respuesta.put("mensaje", "Cliente registrado exitosamente.");
                    }
                }
                
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
        if (DBController.getInstance().getConnection() != null) {
            try {
                String query = "INSERT INTO datosbancariosclientes (clientes_idclientes, nombreTitular, tarjeta, mes, ano) VALUES (?,?,?,?,?)";
                PreparedStatement agregarDatos = DBController.getInstance().getConnection().prepareStatement(query);
                agregarDatos.setInt(1, idCliente);
                agregarDatos.setString(2, cliente.cuenta.getNombreTitular());
                agregarDatos.setString(3, cliente.cuenta.getTarjeta());
                agregarDatos.setString(4, cliente.cuenta.getMes());
                agregarDatos.setString(5, cliente.cuenta.getAno());
                agregarDatos.execute();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }else
            System.out.println("Error en la conexión guardarCuentaBancaria.");
    }//Fin método guardarCuentaBancaria.

    public JSONObject actualizarCuentaBancaria(int idCliente) {
        JSONObject respuesta = new JSONObject();

        if (DBController.getInstance().getConnection() != null) {
            try {
                PreparedStatement consultaRegistro = DBController.getInstance().getConnection().prepareStatement("SELECT * FROM datosbancariosclientes WHERE clientes_idclientes = ?");
                consultaRegistro.setInt(1 , idCliente);
                ResultSet resultado = consultaRegistro.executeQuery();
                
                if(resultado.next()){                  
                    String query = "UPDATE datosbancariosclientes SET nombreTitular = ?, tarjeta = ?, mes = ?, ano = ? WHERE clientes_idclientes = ?";
                    PreparedStatement agregarDatos = DBController.getInstance().getConnection().prepareStatement(query);
                    agregarDatos.setString(1, cliente.cuenta.getNombreTitular());
                    agregarDatos.setString(2, cliente.cuenta.getTarjeta());
                    agregarDatos.setString(3, cliente.cuenta.getMes());
                    agregarDatos.setString(4, cliente.cuenta.getAno());
                    agregarDatos.setInt(5, idCliente);
                    agregarDatos.executeUpdate();                  
                }else{                 
                    String query = "INSERT INTO datosbancariosclientes (clientes_idclientes, nombreTitular, tarjeta, mes, ano) VALUES (?,?,?,?,?)";
                    PreparedStatement agregarDatos = DBController.getInstance().getConnection().prepareStatement(query);
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
        cliente.evaluacion.setTiempoServicio(String.valueOf(jsonEvaluacionCliente.get("tiempoServicio")));
        cliente.evaluacion.setTipoServicio(String.valueOf(jsonEvaluacionCliente.get("tipoServicio")));
    }//Fin método nuevaEvaluacion.

    public void guardarEvaluacion(int idCliente) {
        if (DBController.getInstance().getConnection() != null) {
            try {
                String query = "INSERT INTO evaluacioncliente (clientes_idclientes, tipoServicio, horario, tiempoServicio, lugar, nivelIdioma_idnivelidioma) VALUES (?,?,?,?,?,?)";
                PreparedStatement agregarDatos = DBController.getInstance().getConnection().prepareStatement(query);
                agregarDatos.setInt(1, idCliente);
                agregarDatos.setString(2, cliente.evaluacion.getTipoServicio());
                agregarDatos.setString(3, cliente.evaluacion.getHorario());
                agregarDatos.setString(4, cliente.evaluacion.getTiempoServicio());
                agregarDatos.setString(5, cliente.evaluacion.getLugar());
                agregarDatos.setInt(6, cliente.evaluacion.getNivelIdioma());
                agregarDatos.execute();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }else
            System.out.println("Error en la conexión agregarCuentaBancaria.");
    }//Fin método guardarEvaluacion.

}
