package com.proathome.modelos;

import java.sql.Date;

public class Sesion {
    
    private int idsesiones, profesionales_idprofesionales, clientes_idclientes, ubicacion_idubicacion, tiempo, idSeccion, idNivel, idBloque, personas;
    private String tipoPlan, horario, lugar, extras, tipoServicio, profesional, actualizado, fotoProfesional, correoProfesional, descripcionProfesional;
    private double latitud, longitud;
    private String fecha, token;
    private boolean sumar, finalizado;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPersonas() {
        return personas;
    }

    public void setPersonas(int personas) {
        this.personas = personas;
    }

    public String getTipoPlan() {
        return tipoPlan;
    }

    public void setTipoPlan(String tipoPlan) {
        this.tipoPlan = tipoPlan;
    }

    public boolean getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public boolean getSumar() {
        return sumar;
    }

    public void setSumar(boolean sumar) {
        this.sumar = sumar;
    }
    
    public String getFotoProfesional() {
        return fotoProfesional;
    }

    public void setFotoProfesional(String fotoProfesional) {
        this.fotoProfesional = fotoProfesional;
    }

    public String getCorreoProfesional() {
        return correoProfesional;
    }

    public void setCorreoProfesional(String correoProfesional) {
        this.correoProfesional = correoProfesional;
    }

    public String getDescripcionProfesional() {
        return descripcionProfesional;
    }

    public void setDescripcionProfesional(String descripcionProfesional) {
        this.descripcionProfesional = descripcionProfesional;
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

    public String getProfesional() {
        return profesional;
    }

    public void setProfesional(String profesional) {
        this.profesional = profesional;
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

    public int getProfesionales_idprofesionales() {
        return profesionales_idprofesionales;
    }

    public void setProfesionales_idprofesionales(int profesionales_idprofesionales) {
        this.profesionales_idprofesionales = profesionales_idprofesionales;
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

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
      
}
