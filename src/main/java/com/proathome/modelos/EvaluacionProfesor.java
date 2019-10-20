
package com.proathome.modelos;

public class EvaluacionProfesor {
    
    /*
    *
    * Clase Modelo EvaluacionProfesor.
    * 
    */
    
    private String nivelClase;
    private int numeroValoraciones;
    private double valoracion;

    public String getNivelClase() {
        return nivelClase;
    }

    public void setNivelClase(String nivelClase) {
        this.nivelClase = nivelClase;
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
