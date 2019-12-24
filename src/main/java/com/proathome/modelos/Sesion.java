package com.proathome.modelos;

public class Sesion {
    
    private int idsesiones, profesores_idprofesores, clientes_idclientes, ubicacion_idubicacion;
    private String horario, lugar, tiempo, extras, tipoClase, nivel, profesor;
    private double latitud, longitud;

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

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
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

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
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
      
}
