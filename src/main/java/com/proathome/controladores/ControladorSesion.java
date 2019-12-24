package com.proathome.controladores;

import com.proathome.modelos.Sesion;
import com.proathome.mysql.ConexionMySQL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ControladorSesion {

    private Sesion sesiones[];

    public void obtenerSesiones(int idCliente) {

        ConexionMySQL mysql = new ConexionMySQL();
        Connection conectar;

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                Statement estado = conectar.createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM sesiones WHERE clientes_idclientes = " + idCliente);
                int numeroSesiones = 0;
                
                while(resultado.next()){
                    
                    numeroSesiones++;
                    
                }
                
                sesiones = new Sesion[numeroSesiones];
                int aux = 0;
                
                estado = conectar.createStatement();
                resultado = estado.executeQuery("SELECT * FROM sesiones WHERE clientes_idclientes = " + idCliente  + " ORDER BY idsesiones DESC");
                
                
                while(resultado.next()){
                    
                    Sesion obtenida = new Sesion();
                    obtenida.setIdsesiones(resultado.getInt("idsesiones"));
                    String idProfesor = resultado.getString("profesores_idprofesores");
                    System.out.println(idProfesor);
                    
                    if(idProfesor == null){
                        
                        obtenida.setProfesor("Sin profesor asignado");
 
                    }else{
                        
                        obtenida.setProfesor("Con profesor pero todavía no hay función para ver el nombre");
  
                    }
                    
                    obtenida.setClientes_idclientes(resultado.getInt("clientes_idclientes"));
                    obtenida.setHorario(resultado.getString("horario"));
                    obtenida.setLugar(resultado.getString("lugar"));
                    obtenida.setTiempo(resultado.getString("tiempo"));
                    obtenida.setNivel(resultado.getString("nivel"));
                    obtenida.setExtras(resultado.getString("extras"));
                    obtenida.setTipoClase(resultado.getString("tipoClase"));
                    obtenida.setLatitud(resultado.getDouble("latitud"));
                    obtenida.setLongitud(resultado.getDouble("longitud"));
                    sesiones[aux] = obtenida;
                    aux++;
                    
                }
                
                conectar.close();
                
            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la coenxión en obtenerSesiones.");

        }

    }//Fin método obtenerSesiones.

}
