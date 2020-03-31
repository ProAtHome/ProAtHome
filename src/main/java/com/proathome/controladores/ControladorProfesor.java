package com.proathome.controladores;

import com.proathome.modelos.CuentaBancaria;
import com.proathome.modelos.Profesor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.proathome.mysql.ConexionMySQL;
import org.json.simple.JSONObject;

public class ControladorProfesor {

    /*
    *
    *Clase controladora de Profesores.
    *
     */
    private Profesor profesor = new Profesor();
    private ConexionMySQL mysql = new ConexionMySQL();
    private Connection conectar;
    private JSONObject jsonTodos;
    private boolean profesorRegistrado = false;

    public void iniciarSesion(String correo, String contrasena) {

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                String query = "SELECT * FROM profesores WHERE correo = ? AND contrasena = ?";
                PreparedStatement obtenerDatos = conectar.prepareStatement(query);
                obtenerDatos.setString(1, correo);
                obtenerDatos.setString(2, contrasena);
                ResultSet resultado = obtenerDatos.executeQuery();

                if (resultado.next()) {

                    profesor.setIdProfesor(resultado.getInt("idprofesores"));
                    profesor.setNombre(resultado.getString("nombre"));
                    profesor.setFoto(resultado.getString("foto"));
                    profesor.setEstado(resultado.getBoolean("estado"));

                    conectar.close();
                    profesorRegistrado = true;

                } else {

                    profesorRegistrado = false;
                    conectar.close();

                }

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión iniciarSesion.");

        }

    }//Fin método iniciarSesion.

    public void datosActualizarPerfil(JSONObject datos) {

        profesor.setIdProfesor(Integer.parseInt(String.valueOf(datos.get("idProfesor"))));
        profesor.setNombre(String.valueOf(datos.get("nombre")));
        profesor.setCorreo(String.valueOf(datos.get("correo")));
        profesor.setDescripcion(String.valueOf(datos.get("descripcion")));

    }//Fin método datosActualizarPerfil.

    public void actualizarDatosPerfil() {

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                String query = "UPDATE profesores SET nombre = ?, correo = ?, descripcion = ? WHERE idprofesores = ?";
                PreparedStatement actualizar = conectar.prepareStatement(query);
                actualizar.setString(1, profesor.getNombre());
                actualizar.setString(2, profesor.getCorreo());
                actualizar.setString(3, profesor.getDescripcion());
                actualizar.setInt(4, profesor.getIdProfesor());
                actualizar.executeUpdate();

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión en actualizarDatosPerfil.");

        }

    }//Fin método actualizarDatosPerfil.

    public void actualizarFoto(JSONObject foto) {

        profesor.setFoto(String.valueOf(foto.get("nombre")));
        profesor.setIdProfesor(Integer.parseInt(String.valueOf(foto.get("idProfesor"))));

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                String query = "UPDATE profesores SET foto = ? WHERE idprofesores = ?";
                PreparedStatement actualizar = conectar.prepareStatement(query);
                actualizar.setString(1, profesor.getFoto());
                actualizar.setInt(2, profesor.getIdProfesor());
                actualizar.executeUpdate();

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión actualizarFoto.");

        }

    }//Fin método actualizarFoto.

    public void perfilProfesor(int idProfesor) {

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                String query = "SELECT * FROM profesores WHERE idprofesores = ?";
                PreparedStatement obtenerDatos = conectar.prepareStatement(query);
                obtenerDatos.setInt(1, idProfesor);
                ResultSet resultado = obtenerDatos.executeQuery();

                if (resultado.next()) {

                    profesor.setIdProfesor(resultado.getInt("idprofesores"));
                    profesor.setNombre(resultado.getString("nombre"));
                    profesor.setCorreo(resultado.getString("correo"));
                    profesor.setContrasena(resultado.getString("contrasena"));
                    profesor.setEdad(resultado.getInt("edad"));
                    profesor.setFechaNacimiento(resultado.getDate("fechaNacimiento"));
                    profesor.setFechaRegistro(resultado.getDate("fechaDeRegistro"));
                    profesor.setFoto(resultado.getString("foto"));
                    profesor.setDescripcion(resultado.getString("descripcion"));

                    conectar.close();
                    profesorRegistrado = true;

                } else {

                    profesorRegistrado = false;
                    conectar.close();

                }

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión iniciarSesion.");

        }

    }//Fin método perfilProfesor.

    public Profesor datosSesion() {

        if (profesorRegistrado) {

            return profesor;

        } else {

            return null;

        }

    }//Fin método datosSesion.

    public void nuevoProfesor(JSONObject jsonProfesor) {

        profesor.setNombre(String.valueOf(jsonProfesor.get("nombre")));
        profesor.setCorreo(String.valueOf(jsonProfesor.get("correo")));
        profesor.setContrasena(String.valueOf(jsonProfesor.get("contrasena")));

        //Formateo de fechas a tipo SQL Date.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date dateFechaNacimiento = (java.sql.Date.valueOf(String.valueOf(jsonProfesor.get("fechaNacimiento"))));
        java.sql.Date dateFechaRegistro = (java.sql.Date.valueOf(String.valueOf(jsonProfesor.get("fechaRegistro"))));

        profesor.setFechaNacimiento(dateFechaNacimiento);
        profesor.setFechaRegistro(dateFechaRegistro);
        profesor.setEdad(Integer.parseInt(String.valueOf(jsonProfesor.get("edad"))));

    }

    public ArrayList<String> obtenerTodos() {

        ArrayList<String> list = new ArrayList<>();

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                Statement estado = conectar.createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM profesores");

                while (resultado.next()) {
                    String nombre = resultado.getString("nombre");
                    String correo = resultado.getString("correo");
                    int id = resultado.getInt("idprofesores");

                    String stringJSON = " \"id" + id + "\" :{\"nombre\" : \"" + nombre + "\", \"correo\" : \"" + correo + "\"}";
                    list.add(stringJSON);

                }

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión obtenerTodos");

        }

        return list;

    }//Fin método obtenerTodos.

    public void guardarProfesor() {

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                String query = "INSERT INTO profesores (nombre, correo, contrasena, edad, fechaNacimiento, fechaDeRegistro) VALUES (?,?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setString(1, profesor.getNombre());
                agregarDatos.setString(2, profesor.getCorreo());
                agregarDatos.setString(3, profesor.getContrasena());
                agregarDatos.setInt(4, profesor.getEdad());
                agregarDatos.setDate(5, profesor.getFechaNacimiento());
                agregarDatos.setDate(6, profesor.getFechaRegistro());
                agregarDatos.execute();

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión agregarProfesor");

        }

    }//Fin método guardarProfesor.

    public CuentaBancaria obtenerCuentaBancaria(int idProfesor) {

        profesor.cuenta = new CuentaBancaria();
        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                Statement estado = conectar.createStatement();
                ResultSet resultado = estado.executeQuery("SELECT * FROM datosbancariosprofesores WHERE profesores_idprofesores = " + idProfesor);

                if (resultado.next()) {

                    profesor.cuenta.setBanco(resultado.getString("banco"));
                    profesor.cuenta.setDireccionFacturacion(resultado.getString("direccionFacturacion"));
                    profesor.cuenta.setTipoPago(resultado.getString("tipoDePago"));
                    profesor.cuenta.setNumeroCuenta(resultado.getString("numeroCuenta"));

                } else {

                    return null;

                }

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión obtenerCuentaBancaria.");

        }

        return profesor.cuenta;

    }//Fin método obtenerCuentaBancaria.

    public void nuevaCuentaBancaria(JSONObject jsonCuentaBancaria) {

        profesor.cuenta = new CuentaBancaria();
        profesor.cuenta.setBanco(String.valueOf(jsonCuentaBancaria.get("banco")));
        profesor.cuenta.setDireccionFacturacion(String.valueOf(jsonCuentaBancaria.get("direccionFacturacion")));
        profesor.cuenta.setTipoPago(String.valueOf(jsonCuentaBancaria.get("tipoDePago")));
        profesor.cuenta.setNumeroCuenta(String.valueOf(jsonCuentaBancaria.get("numeroCuenta")));

    }//Fin método nuevaCuentaBancaria.

    public void guardarCuentaBancaria(int idProfesor) {

        conectar = mysql.conectar();

        if (conectar != null) {

            try {

                String query = "INSERT INTO datosbancariosprofesores (profesores_idprofesores, tipoDePago, banco, numeroCuenta, direccionFacturacion) VALUES (?,?,?,?,?)";
                PreparedStatement agregarDatos = conectar.prepareStatement(query);
                agregarDatos.setInt(1, idProfesor);
                agregarDatos.setString(2, profesor.cuenta.getTipoPago());
                agregarDatos.setString(3, profesor.cuenta.getBanco());
                agregarDatos.setString(4, profesor.cuenta.getNumeroCuenta());
                agregarDatos.setString(5, profesor.cuenta.getDireccionFacturacion());
                agregarDatos.execute();

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión guardarCuentaBancaria.");

        }

    }//Fin método guardarCuentaBancaria.

    public void actualizarCuentaBancaria(int idProfesor) {

        conectar = mysql.conectar();

        if (conectar != null) {

            try {
                
                PreparedStatement consultaRegistro = conectar.prepareStatement("SELECT * FROM datosbancariosprofesores WHERE profesores_idprofesores = ?");
                consultaRegistro.setInt(1 , idProfesor);
                ResultSet resultado = consultaRegistro.executeQuery();
                
                if(resultado.next()){
                    
                    String query = "UPDATE datosbancariosprofesores SET tipoDePago = ?, banco = ?, numeroCuenta = ?, direccionFacturacion = ? WHERE profesores_idprofesores = ?";
                    PreparedStatement agregarDatos = conectar.prepareStatement(query);
                    agregarDatos.setString(1, profesor.cuenta.getTipoPago());
                    agregarDatos.setString(2, profesor.cuenta.getBanco());
                    agregarDatos.setString(3, profesor.cuenta.getNumeroCuenta());
                    agregarDatos.setString(4, profesor.cuenta.getDireccionFacturacion());
                    agregarDatos.setInt(5, idProfesor);
                    agregarDatos.executeUpdate();

                }else{
                    
                    PreparedStatement insert = conectar.prepareStatement("INSERT INTO datosbancariosprofesores (profesores_idprofesores, tipoDePago, banco, numeroCuenta, direccionFacturacion) VALUES (?,?,?,?,?)");
                    insert.setInt(1 , idProfesor);
                    insert.setString(2, profesor.cuenta.getTipoPago());
                    insert.setString(3, profesor.cuenta.getBanco());
                    insert.setString(4, profesor.cuenta.getNumeroCuenta());
                    insert.setString(5, profesor.cuenta.getDireccionFacturacion());
                    insert.execute();
                    
                }

                conectar.close();

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());

            }

        } else {

            System.out.println("Error en la conexión guardarCuentaBancaria.");

        }

    }//Fin método guardarCuentaBancaria.

}
