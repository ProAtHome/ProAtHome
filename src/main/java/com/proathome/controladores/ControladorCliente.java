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
    private ConexionMySQL mysql = new ConexionMySQL();
    private Connection conectar;
    private boolean clienteRegistrado = false;
    
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
        
    }//Fin método nuevaSesion.
    
    public JSONObject detallesSesion(int idSesion){
      
        conectar = mysql.conectar();
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
                    jsonDetalles.put("tiempo", resultado.getString("tiempo"));
                    jsonDetalles.put("nivel", resultado.getString("nivel"));
                    jsonDetalles.put("extras", resultado.getString("extras"));
                    jsonDetalles.put("tipoClase", resultado.getString("tipoClase"));
                    jsonDetalles.put("latitud", resultado.getDouble("latitud"));
                    jsonDetalles.put("longitud", resultado.getDouble("longitud"));
                    jsonDetalles.put("actualizado", resultado.getDate("actualizado"));
                    jsonDetalles.put("nombre", resultado.getString("nombre"));
                    jsonDetalles.put("correo", resultado.getString("correo"));
                    jsonDetalles.put("foto", resultado.getString("foto"));
                    jsonDetalles.put("descripcion", resultado.getString("descripcion"));
                    
                }
                
                conectar.close();
                
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            
        }else{
        
            System.out.println("Error en detallesSesion.");
            
        }
        
        return jsonDetalles;
        
    }//Fin métod detallesSesion.
    
    public void guardarSesion(){
        
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                String query = "INSERT INTO sesiones (clientes_idclientes, horario, lugar, tiempo, extras, tipoClase, latitud, longitud, actualizado, idSeccion, idNivel, idBloque, fecha) "
                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
                agregarDatos.execute();
                
                conectar.close();
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }        
            
        }else{
            
            System.out.println("Error en la conexión en guardarSesion.");
            
        }
        
    }//Fin método guardarSesion.
    
    public CuentaBancaria obtenerCuentaBancaria(int idCliente){
                
        cliente.cuenta = new CuentaBancaria();
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                Statement estado = conectar.createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM datosbancariosclientes WHERE clientes_idclientes = " + idCliente);
                
                if(resultado.next()){
                
                    cliente.cuenta.setBanco(resultado.getString("banco"));
                    cliente.cuenta.setDireccionFacturacion(resultado.getString("direccionFacturacion"));
                    cliente.cuenta.setTipoPago(resultado.getString("tipoDePago"));
                    cliente.cuenta.setNumeroCuenta(resultado.getString("numeroCuenta"));
                    
                }else{
                    
                    return null;
                    
                }  
                
                conectar.close();
                
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
        
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                String query = "UPDATE clientes SET nombre = ?, correo = ?, descripcion = ? WHERE idclientes = ?";
                PreparedStatement actualizar = conectar.prepareStatement(query);
                actualizar.setString(1, cliente.getNombre());
                actualizar.setString(2, cliente.getCorreo());
                actualizar.setString(3, cliente.getDescripcion());
                actualizar.setInt(4, cliente.getIdCliente());
                actualizar.executeUpdate();
                
                conectar.close();
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }
            
        }else{
            
            System.out.println("Error en la conexión en actualizarDatosPerfil.");
            
        }
        
    }//Fin método actualizarDatosPerfil.

    public void iniciarSesion(String correo, String contrasena) {

        conectar = mysql.conectar();

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
                    
                    conectar.close();
                    clienteRegistrado = true;

                } else {

                    clienteRegistrado = false;
                    conectar.close();

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
        
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                String query = "UPDATE clientes SET foto = ? WHERE idclientes = ?";
                PreparedStatement actualizar = conectar.prepareStatement(query);
                actualizar.setString(1, cliente.getFoto());
                actualizar.setInt(2, cliente.getIdCliente());
                actualizar.executeUpdate();
                
                conectar.close();
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }
            
        }else{
            
            System.out.println("Error en la conexión actualizarFoto.");
            
        }
        
    }//Fin método actualizarFoto.

    public void perfilCliente(int idCliente) {

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                String query = "SELECT * FROM clientes WHERE idclientes = ?";
                PreparedStatement obtenerDatos = conectar.prepareStatement(query);
                obtenerDatos.setInt(1, idCliente);
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
                    
                    conectar.close();
                    clienteRegistrado = true;

                } else {

                    clienteRegistrado = false;
                    conectar.close();

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

        conectar = mysql.conectar();

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
                
                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión guardarCliente.");

        }

    }//Fin método guardarCliente.

    public void nuevaCuentaBancaria(JSONObject jsonCuentaBancaria) {

        cliente.cuenta = new CuentaBancaria();
        cliente.cuenta.setBanco(String.valueOf(jsonCuentaBancaria.get("banco")));
        cliente.cuenta.setDireccionFacturacion(String.valueOf(jsonCuentaBancaria.get("direccionFacturacion")));
        cliente.cuenta.setTipoPago(String.valueOf(jsonCuentaBancaria.get("tipoDePago")));
        cliente.cuenta.setNumeroCuenta(String.valueOf(jsonCuentaBancaria.get("numeroCuenta")));

    }//Fin método nuevaCuentaBancaria.
    
    public void guardarCuentaBancaria(int idCliente) {

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                String query = "INSERT INTO datosbancariosclientes (clientes_idclientes, tipoDePago, banco, numeroCuenta, direccionFacturacion) VALUES (?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setInt(1, idCliente);
                agregarDatos.setString(2, cliente.cuenta.getTipoPago());
                agregarDatos.setString(3, cliente.cuenta.getBanco());
                agregarDatos.setString(4, cliente.cuenta.getNumeroCuenta());
                agregarDatos.setString(5, cliente.cuenta.getDireccionFacturacion());
                agregarDatos.execute();

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión guardarCuentaBancaria.");

        }

    }//Fin método guardarCuentaBancaria.

    public void actualizarCuentaBancaria(int idCliente) {

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                PreparedStatement consultaRegistro = conectar.prepareStatement("SELECT * FROM datosbancariosclientes WHERE clientes_idclientes = ?");
                consultaRegistro.setInt(1 , idCliente);
                ResultSet resultado = consultaRegistro.executeQuery();
                
                if(resultado.next()){
                    
                    String query = "UPDATE datosbancariosclientes SET tipoDePago = ?, banco = ?, numeroCuenta = ?, direccionFacturacion = ? WHERE clientes_idclientes = ?";
                    PreparedStatement agregarDatos = conectar.prepareStatement(query);
                    agregarDatos.setString(1, cliente.cuenta.getTipoPago());
                    agregarDatos.setString(2, cliente.cuenta.getBanco());
                    agregarDatos.setString(3, cliente.cuenta.getNumeroCuenta());
                    agregarDatos.setString(4, cliente.cuenta.getDireccionFacturacion());
                    agregarDatos.setInt(5, idCliente);
                    agregarDatos.executeUpdate();
                    
                }else{
                    
                    String query = "INSERT INTO datosbancariosclientes (clientes_idclientes, tipoDePago, banco, numeroCuenta, direccionFacturacion) VALUES (?,?,?,?,?)";
                    PreparedStatement agregarDatos = conectar.prepareStatement(query);
                    agregarDatos.setInt(1, idCliente);
                    agregarDatos.setString(2, cliente.cuenta.getTipoPago());
                    agregarDatos.setString(3, cliente.cuenta.getBanco());
                    agregarDatos.setString(4, cliente.cuenta.getNumeroCuenta());
                    agregarDatos.setString(5, cliente.cuenta.getDireccionFacturacion());
                    agregarDatos.execute();

                }

                conectar.close();

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

        conectar = mysql.conectar();

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

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión agregarCuentaBancaria.");

        }

    }//Fin método guardarEvaluacion.

}
