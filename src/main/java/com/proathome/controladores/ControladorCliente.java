package com.proathome.controladores;

import com.proathome.modelos.Cliente;
import com.proathome.modelos.CuentaBancaria;
import com.proathome.modelos.EvaluacionCliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import com.proathome.mysql.ConexionMySQL;
import org.json.simple.JSONObject;

public class ControladorCliente {
    
    /*
    *
    *Clase controladora de Clientes.
    *
    */

    private Cliente cliente = new Cliente();
    private ConexionMySQL mysql = new ConexionMySQL();
    private Connection conectar;

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
    
    public void nuevaCuentaBancaria(JSONObject jsonCuentaBancaria){
        
        cliente.cuenta = new CuentaBancaria();
        cliente.cuenta.setBanco(String.valueOf(jsonCuentaBancaria.get("banco")));
        cliente.cuenta.setDireccionFacturacion(String.valueOf(jsonCuentaBancaria.get("direccionFacturacion")));
        cliente.cuenta.setTipoPago(String.valueOf(jsonCuentaBancaria.get("tipoDePago")));
        cliente.cuenta.setNumeroCuenta(Integer.parseInt(String.valueOf(jsonCuentaBancaria.get("numeroCuenta"))));
        
    }//Fin método nuevaCuentaBancaria.
    
    public void guardarCuentaBancaria(int idCliente){
        
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
                String query = "INSERT INTO datosbancariosclientes (clientes_idclientes, tipoDePago, banco, numeroCuenta, direccionFacturacion) VALUES (?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setInt(1, idCliente);
                agregarDatos.setString(2, cliente.cuenta.getTipoPago());
                agregarDatos.setString(3, cliente.cuenta.getBanco());
                agregarDatos.setInt(4, cliente.cuenta.getNumeroCuenta());
                agregarDatos.setString(5, cliente.cuenta.getDireccionFacturacion());
                agregarDatos.execute();
                
                conectar.close();
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }
            
            
            
        }else{
            
            System.out.println("Error en la conexión guardarCuentaBancaria.");
            
        }
        
    }//Fin método guardarCuentaBancaria.
    
    public void nuevaEvaluacion(JSONObject jsonEvaluacionCliente){
        
        cliente.evaluacion = new EvaluacionCliente();
        cliente.evaluacion.setHorario(String.valueOf(jsonEvaluacionCliente.get("horario")));
        cliente.evaluacion.setLugar(String.valueOf(jsonEvaluacionCliente.get("lugar")));
        cliente.evaluacion.setNivelIdioma(Integer.parseInt(String.valueOf(jsonEvaluacionCliente.get("idNivelIdioma"))));
        cliente.evaluacion.setTiempoClase(String.valueOf(jsonEvaluacionCliente.get("tiempoClase")));
        cliente.evaluacion.setTipoClase(String.valueOf(jsonEvaluacionCliente.get("tipoClase")));
        
    }//Fin método nuevaEvaluacion.
    
    public void guardarEvaluacion(int idCliente){
        
        conectar = mysql.conectar();
        
        if(conectar != null){
            
            try{
                
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
                
            }catch(SQLException ex){
                
                System.out.println(ex.getMessage());
                
            }
            
        }else{
            
            System.out.println("Error en la conexión agregarCuentaBancaria.");
            
        }
        
    }//Fin método guardarEvaluacion.

}
