/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proathome.controladores;

import java.util.TimerTask;

/**
 *
 * @author Marvin
 */
public class Temporizador extends TimerTask{

    @Override
    public void run() {
        System.out.println("Entro peticion programada");
    }
    
}
