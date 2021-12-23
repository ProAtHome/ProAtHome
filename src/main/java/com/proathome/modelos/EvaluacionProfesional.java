package com.proathome.modelos;

public class EvaluacionProfesional {
    
    /*
    *
    * Servicio Modelo de Evaluacion Profesional.
    * 
    */
    
    private String nivelServicio;
    private int numeroValoraciones;
    private double valoracion;

    public String getNivelServicio() {
        return nivelServicio;
    }

    public void setNivelServicio(String nivelServicio) {
        this.nivelServicio = nivelServicio;
    }

    public int getNumeroValoraciones() {
        return numeroValoraciones;
    }

    public void setNumeroValoraciones(int numeroValoraciones) {
        this.numeroValoraciones = numeroValoraciones;
    }

    public double getValoracion() {
        return valoracion;
    }

    public void setValoracion(double valoracion) {
        this.valoracion = valoracion;
    }

}
