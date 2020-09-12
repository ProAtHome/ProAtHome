package com.proathome.modelos;

public class CuentaBancaria {
    
    /*
    *
    * Clase Modelo de Cuenta Bancaria.
    * 
    */
    
    private String nombreTitular, tarjeta, mes, ano;
    
    public CuentaBancaria(){
        
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }
      
}
