package com.proathome.modelos;

public class EvaluacionCliente {

    /*
    *
    * Servicio Modelo EvaluacionCliente.
    * 
    */
    
    private String tipoServicio, horario, tiempoServicio, lugar;
    private int nivelIdioma;

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getTiempoServicio() {
        return tiempoServicio;
    }

    public void setTiempoServicio(String tiempoServicio) {
        this.tiempoServicio = tiempoServicio;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public int getNivelIdioma() {
        return nivelIdioma;
    }

    public void setNivelIdioma(int nivelIdioma) {
        this.nivelIdioma = nivelIdioma;
    }

}
