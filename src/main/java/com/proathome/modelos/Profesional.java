package com.proathome.modelos;

import java.sql.Date;

/*
 *
 * Servicio Modelo Profesional.
 * 
*/

public class Profesional {

    private String token, nombre, apellidoPaterno, apellidMaterno, celular, telefonoLocal, direccion, genero, correo, contrasena, clv, foto, descripcion, estado, fechaNacimiento, fechaRegistro;
    private int idProfesional, rangoServicio;
    public static CuentaBancaria cuenta;
    public static EvaluacionProfesional evaluacion;
    public static Ubicacion ubicacion;
    private boolean verificado;

    public Profesional() {
        
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
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
    
    public int getIdProfesional() {
        return idProfesional;
    }

    public void setIdProfesional(int idProfesional) {
        this.idProfesional = idProfesional;
    }

    public static CuentaBancaria getCuenta() {
        return cuenta;
    }

    public static void setCuenta(CuentaBancaria cuenta) {
        Profesional.cuenta = cuenta;
    }

    public static EvaluacionProfesional getEvaluacion() {
        return evaluacion;
    }

    public static void setEvaluacion(EvaluacionProfesional evaluacion) {
        Profesional.evaluacion = evaluacion;
    }

    public static Ubicacion getUbicacion() {
        return ubicacion;
    }

    public static void setUbicacion(Ubicacion ubicacion) {
        Profesional.ubicacion = ubicacion;
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

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidMaterno() {
        return apellidMaterno;
    }

    public void setApellidMaterno(String apellidMaterno) {
        this.apellidMaterno = apellidMaterno;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getTelefonoLocal() {
        return telefonoLocal;
    }

    public void setTelefonoLocal(String telefonoLocal) {
        this.telefonoLocal = telefonoLocal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getRangoServicio() {
        return rangoServicio;
    }

    public void setRangoServicio(int rangoServicio) {
        this.rangoServicio = rangoServicio;
    }

    
  
}
