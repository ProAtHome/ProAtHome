package com.proathome.modelos;

public class EvaluacionCliente {

    /*
    *
    * Clase Modelo EvaluacionCliente.
    * 
    */
    
    private String tipoClase, horario, tiempoClase, lugar;
    private int nivelIdioma;

    public String getTipoClase() {
        return tipoClase;
    }

    public void setTipoClase(String tipoClase) {
        this.tipoClase = tipoClase;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getTiempoClase() {
        return tiempoClase;
    }

    public void setTiempoClase(String tiempoClase) {
        this.tiempoClase = tiempoClase;
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
