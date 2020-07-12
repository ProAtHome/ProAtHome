package com.proathome.modelos;

import java.sql.Date;

public class Sesion {
    
    private int idsesiones, profesores_idprofesores, clientes_idclientes, ubicacion_idubicacion, tiempo, idSeccion, idNivel, idBloque;
    private String horario, lugar, extras, tipoClase, profesor, actualizado, fotoProfesor, correoProfesor, descripcionProfesor;
    private double latitud, longitud;
    private java.sql.Date fecha;

    public String getFotoProfesor() {
        return fotoProfesor;
    }

    public void setFotoProfesor(String fotoProfesor) {
        this.fotoProfesor = fotoProfesor;
    }

    public String getCorreoProfesor() {
        return correoProfesor;
    }

    public void setCorreoProfesor(String correoProfesor) {
        this.correoProfesor = correoProfesor;
    }

    public String getDescripcionProfesor() {
        return descripcionProfesor;
    }

    public void setDescripcionProfesor(String descripcionProfesor) {
        this.descripcionProfesor = descripcionProfesor;
    }

    public String getActualizado() {
        return actualizado;
    }

    public void setActualizado(String actualizado) {
        this.actualizado = actualizado;
    }

    public int getIdsesiones() {
        return idsesiones;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public int getUbicacion_idubicacion() {
        return ubicacion_idubicacion;
    }

    public void setUbicacion_idubicacion(int ubicacion_idubicacion) {
        this.ubicacion_idubicacion = ubicacion_idubicacion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void setIdsesiones(int idsesiones) {
        this.idsesiones = idsesiones;
    }

    public int getProfesores_idprofesores() {
        return profesores_idprofesores;
    }

    public void setProfesores_idprofesores(int profesores_idprofesores) {
        this.profesores_idprofesores = profesores_idprofesores;
    }

    public int getClientes_idclientes() {
        return clientes_idclientes;
    }

    public void setClientes_idclientes(int clientes_idclientes) {
        this.clientes_idclientes = clientes_idclientes;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public int getIdNivel() {
        return idNivel;
    }

    public void setIdNivel(int idNivel) {
        this.idNivel = idNivel;
    }

    public int getIdBloque() {
        return idBloque;
    }

    public void setIdBloque(int idBloque) {
        this.idBloque = idBloque;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getTipoClase() {
        return tipoClase;
    }

    public void setTipoClase(String tipoClase) {
        this.tipoClase = tipoClase;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
      
}
