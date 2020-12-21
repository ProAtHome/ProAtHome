package com.proathome.modelos;

import java.sql.Date;

/*
 *
 * Clase Modelo Profesor.
 * 
*/

public class Profesor {

    private String nombre, correo, contrasena, clv, foto, descripcion, estado;
    private java.sql.Date fechaNacimiento, fechaRegistro;
    private int edad, idProfesor;
    public static CuentaBancaria cuenta;
    public static EvaluacionProfesor evaluacion;
    public static Ubicacion ubicacion;

    public Profesor() {
        
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
    
    public int getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(int idProfesor) {
        this.idProfesor = idProfesor;
    }

    public static CuentaBancaria getCuenta() {
        return cuenta;
    }

    public static void setCuenta(CuentaBancaria cuenta) {
        Profesor.cuenta = cuenta;
    }

    public static EvaluacionProfesor getEvaluacion() {
        return evaluacion;
    }

    public static void setEvaluacion(EvaluacionProfesor evaluacion) {
        Profesor.evaluacion = evaluacion;
    }

    public static Ubicacion getUbicacion() {
        return ubicacion;
    }

    public static void setUbicacion(Ubicacion ubicacion) {
        Profesor.ubicacion = ubicacion;
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

    public String getClv() {
        return clv;
    }

    public void setClv(String clv) {
        this.clv = clv;
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
