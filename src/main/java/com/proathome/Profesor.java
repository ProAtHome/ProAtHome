package com.proathome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import mysql.ConexionMySQL;
import org.json.simple.JSONObject;

/*
 *
 * Clase que creará un nuevo Profesor y podrá realizar distintas acciones con él.
 * 
 */
public class Profesor {

    final String TODOS = "all";
    JSONObject jsonTodos;
    String nombre, correo, contrasena, clv;
    java.sql.Date fechaNacimiento, fechaRegistro;
    int edad;

    public Profesor(JSONObject jsonProfesor) {

        this.nombre = String.valueOf(jsonProfesor.get("nombre"));
        this.correo = String.valueOf(jsonProfesor.get("correo"));
        this.contrasena = String.valueOf(jsonProfesor.get("contrasena"));

        //Formateo de fechas a tipo SQL Date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dateFechaNacimiento = (java.sql.Date.valueOf(String.valueOf(jsonProfesor.get("fechaNacimiento"))));
        java.sql.Date dateFechaRegistro = (java.sql.Date.valueOf(String.valueOf(jsonProfesor.get("fechaRegistro"))));

        this.fechaNacimiento = dateFechaNacimiento;
        this.fechaRegistro = dateFechaRegistro;
        this.edad = Integer.parseInt(String.valueOf(jsonProfesor.get("edad")));

    }

    //Constructor que se llama al obtener todos los Profesores.
    public Profesor(String clv) {
        this.clv = clv;
    }

    public ArrayList<String> obtenerTodos() {

        ArrayList<String> list = new ArrayList<>();

        if (this.clv.equals(TODOS)) {

            ConexionMySQL mysql = new ConexionMySQL();
            Connection conectar = mysql.conectar();

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

        }

        return list;
    }

    public void agregarProfesor() {

        ConexionMySQL mysql = new ConexionMySQL();
        Connection conectar = mysql.conectar();

        if (conectar != null) {

            try {

                String query = "INSERT INTO profesores (nombre, correo, contrasena, edad, fechaNacimiento, fechaDeRegistro) VALUES (?,?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setString(1, this.nombre);
                agregarDatos.setString(2, this.correo);
                agregarDatos.setString(3, this.contrasena);
                agregarDatos.setInt(4, this.edad);
                agregarDatos.setDate(5, this.fechaNacimiento);
                agregarDatos.setDate(6, this.fechaRegistro);
                agregarDatos.execute();

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión agregarProfesor");

        }

    }
}
