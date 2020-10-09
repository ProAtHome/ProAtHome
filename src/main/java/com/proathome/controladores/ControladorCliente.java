package com.proathome.controladores;

import com.proathome.modelos.Cliente;
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
import org.json.simple.JSONObject;

public class ControladorCliente {

    /*
    *
    *Clase controladora de Clientes.
    *
     */
    private Cliente cliente = new Cliente();
    private Sesion sesion = new Sesion();
    private boolean clienteRegistrado = false;
    
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
                        PreparedStatement consulta = conectar.prepareStatement("SELECT sesiones.finalizado, pagos.estatusPago FROM sesiones INNER JOIN pagos WHERE pagos.idSesion = sesiones.idsesiones AND sesiones.clientes_idclientes = ?");
                        consulta.setInt(1, idEstudiante);
                        ResultSet resultado = consulta.executeQuery();

                        while(resultado.next()){
                            String estatusPago = resultado.getString("estatusPago");
                            if(estatusPago == null)
                                estatusPago = "No pagado";

                            if((!resultado.getBoolean("finalizado") || !estatusPago.equalsIgnoreCase("Pagado"))){
                                sesiones_pagadas_finalizadas = false;
                            }
                        }

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
                    System.out.println("El plan del perfil ya fue iniciado.");
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
                PreparedStatement actualizar = conectar.prepareStatement("UPDATE pagos SET costoClase = ?, costoTE = ?, estatusPago = ? WHERE idEstudiante = ? AND idSesion = ?");
                actualizar.setDouble(1, Double.parseDouble(json.get("costoClase").toString()));
                actualizar.setDouble(2, Double.parseDouble(json.get("costoTE").toString()));
                actualizar.setString(3, json.get("estatusPago").toString());
                actualizar.setInt(4, Integer.parseInt(json.get("idEstudiante").toString()));
                actualizar.setInt(5, Integer.parseInt(json.get("idSesion").toString()));
                actualizar.execute();
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
                    System.out.println("La ruta ya fue iniciada prro.");
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
        sesion.setFecha(java.sql.Date.valueOf(datos.get("fecha").toString()));
        sesion.setActualizado(datos.get("actualizado").toString());
        sesion.setSumar(Boolean.valueOf(datos.get("sumar").toString()));
        
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
                
                String query = "INSERT INTO sesiones (clientes_idclientes, horario, lugar, tiempo, extras, tipoClase, latitud, longitud, actualizado, idSeccion, idNivel, idBloque, fecha, progreso, sumar) "
                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
                agregarDatos.setDate(13, sesion.getFecha());
                agregarDatos.setInt(14, sesion.getTiempo());
                agregarDatos.setBoolean(15, sesion.getSumar());
                agregarDatos.execute();
            
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }        
            
        }else{
            
            System.out.println("Error en la conexión en guardarSesion.");
            
        }
        
    }//Fin método guardarSesion.
    
    public CuentaBancaria obtenerCuentaBancaria(int idCliente){
                
        cliente.cuenta = new CuentaBancaria();
         Connection conectar = ConexionMySQL.connection();
        
        if(conectar != null){
            
            try{
                
                Statement estado = conectar.createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM datosbancariosclientes WHERE clientes_idclientes = " + idCliente);
                
                if(resultado.next()){
                
                    cliente.cuenta.setNombreTitular(resultado.getString("nombreTitular"));
                    cliente.cuenta.setTarjeta(resultado.getString("tarjeta"));
                    cliente.cuenta.setMes(resultado.getString("mes"));
                    cliente.cuenta.setAno(resultado.getString("ano"));
                    
                }else{
                    
                    return null;
                    
                }  
                
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }
                    
        }else{
            
            System.out.println("Error en la conexión obtenerCuentaBancaria.");
            
        }
        
        return cliente.cuenta;
        
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
                    cliente.setFoto(resultado.getString("foto"));
                    
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

                String query = "SELECT * FROM clientes INNER JOIN planes WHERE clientes.idclientes = ? AND planes.clientes_idclientes = ?";
                PreparedStatement obtenerDatos = conectar.prepareStatement(query);
                obtenerDatos.setInt(1, idCliente);
                obtenerDatos.setInt(2, idCliente);
                ResultSet resultado = obtenerDatos.executeQuery();

                if (resultado.next()) {

                    cliente.setIdCliente(resultado.getInt("idclientes"));
                    cliente.setNombre(resultado.getString("nombre"));
                    cliente.setCorreo(resultado.getString("correo"));
                    cliente.setContrasena(resultado.getString("contrasena"));
                    cliente.setEdad(resultado.getInt("edad"));
                    cliente.setFechaNacimiento(resultado.getDate("fechaNacimiento"));
                    cliente.setFechaRegistro(resultado.getDate("fechaDeRegistro"));
                    cliente.setFoto(resultado.getString("foto"));
                    cliente.setDescripcion(resultado.getString("descripcion"));
                    cliente.setTipoPlan(resultado.getString("tipoPlan"));
                    cliente.setMonedero(resultado.getInt("monedero"));
                    System.out.println(cliente);
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
        cliente.setCorreo(String.valueOf(jsonCliente.get("correo")));
        cliente.setContrasena(String.valueOf(jsonCliente.get("contrasena")));

        //Formateo de fechas a tipo SQL Date.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dateFechaNacimiento = (java.sql.Date.valueOf(String.valueOf(jsonCliente.get("fechaNacimiento"))));
        java.sql.Date dateFechaRegistro = (java.sql.Date.valueOf(String.valueOf(jsonCliente.get("fechaRegistro"))));

        cliente.setFechaNacimiento(dateFechaNacimiento);
        cliente.setFechaRegistro(dateFechaRegistro);
        cliente.setEdad(Integer.parseInt(String.valueOf(jsonCliente.get("edad"))));

    }//Fin Constructor.

    public void guardarCliente() {

         Connection conectar = ConexionMySQL.connection();

        if (conectar != null) {

            try {

                String query = "INSERT INTO clientes (nombre, correo, contrasena, edad, fechaNacimiento, fechaDeRegistro) VALUES (?,?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setString(1, cliente.getNombre());
                agregarDatos.setString(2, cliente.getCorreo());
                agregarDatos.setString(3, cliente.getContrasena());
                agregarDatos.setInt(4, cliente.getEdad());
                agregarDatos.setDate(5, cliente.getFechaNacimiento());
                agregarDatos.setDate(6, cliente.getFechaRegistro());
                agregarDatos.execute();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión guardarCliente.");

        }

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

    public void actualizarCuentaBancaria(int idCliente) {

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

            } catch (SQLException ex) {

                ex.printStackTrace();

            }

        } else {

            System.out.println("Error en la conexión guardarCuentaBancaria.");

        }

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
