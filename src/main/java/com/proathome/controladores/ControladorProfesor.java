package com.proathome.controladores;

import com.proathome.modelos.CuentaBancaria;
import com.proathome.modelos.EvaluacionProfesor;
import com.proathome.modelos.Profesor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.proathome.mysql.ConexionMySQL;
import org.json.simple.JSONObject;

public class ControladorProfesor {

    /*
    *
    *Clase controladora de Profesores.
    *
    */
    
    private Profesor profesor = new Profesor();
    private ConexionMySQL mysql = new ConexionMySQL();
    private Connection conectar;
    private JSONObject jsonTodos;

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

        conectar = mysql.conectar();

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

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión obtenerTodos");

        }

        return list;

    }//Fin método obtenerTodos.

    public void guardarProfesor() {

        conectar = mysql.conectar();

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

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión agregarProfesor");

        }

    }//Fin método guardarProfesor.
    
    public void nuevaCuentaBancaria(JSONObject jsonCuentaBancaria){
        
        profesor.cuenta = new CuentaBancaria();
        profesor.cuenta.setBanco(String.valueOf(jsonCuentaBancaria.get("banco")));
        profesor.cuenta.setDireccionFacturacion(String.valueOf(jsonCuentaBancaria.get("direccionFacturacion")));
        profesor.cuenta.setTipoPago(String.valueOf(jsonCuentaBancaria.get("tipoDePago")));
        profesor.cuenta.setNumeroCuenta(Integer.parseInt(String.valueOf(jsonCuentaBancaria.get("numeroCuenta"))));
        
    }//Fin método nuevaCuentaBancaria.

    public void guardarCuentaBancaria(int idProfesor) {

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                String query = "INSERT INTO datosbancariosprofesores (profesores_idprofesores, tipoDePago, banco, numeroCuenta, direccionFacturacion) VALUES (?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setInt(1, idProfesor);
                agregarDatos.setString(2, profesor.cuenta.getTipoPago());
                agregarDatos.setString(3, profesor.cuenta.getBanco());
                agregarDatos.setInt(4, profesor.cuenta.getNumeroCuenta());
                agregarDatos.setString(5, profesor.cuenta.getDireccionFacturacion());
                agregarDatos.execute();

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión agregarCuentaBancaria.");

        }

    }//Fin método guardarCuentaBancaria.
    
    public void nuevaEvaluacion(JSONObject jsonEvaluacionProfesor){
        
        
        
    }
    
}
