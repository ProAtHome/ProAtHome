package com.proathome.modelos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import com.proathome.mysql.ConexionMySQL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
 *
 * Clase Modelo Cliente.
 * 
 */

public class Cliente {
   
    private String nombre, correo, contrasena;
    private java.sql.Date fechaNacimiento, fechaRegistro;
    private int edad;
    public static CuentaBancaria cuenta;
    public static EvaluacionCliente evaluacion;
    
    public Cliente(){
        
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
    
}
