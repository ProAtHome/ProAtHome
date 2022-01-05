package com.proathome.controladores;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Marvin
 */
public class ControladorFechaActual {
    
    public static String getFechaActual(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fechaActual = dtf.format(LocalDateTime.now());
       

        return fechaActual;
    }
    
}
