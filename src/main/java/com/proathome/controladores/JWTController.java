package com.proathome.controladores;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONObject;

/**
 *
 * @author Marvin
 */
public class JWTController {
    
    private static String CLAVE_PRIVADA = "ProAtHome_2022*";
    public static final String PERFIL_PROFESIONAL = "PROFESIONAL";
    public static final String PERFIL_CLIENTE = "CLIENTE";
    public static final String ADMIN = "ADMIN";
    public static JWTController instance;
    private Algorithm algorithm;
    private JSONObject errorJSON;
    private JWTVerifier verifier;
    private DecodedJWT originToken;
    
    private JWTController(){
        // Dado un algoritmo, como HmacSHA-256
        algorithm = Algorithm.HMAC256(CLAVE_PRIVADA);
        errorJSON = new JSONObject();
    }
    
    public static JWTController getInstance(){
        if(instance == null)
            instance = new JWTController();
        
        return instance;
    }
    
    public JSONObject getError(){
        errorJSON.clear();
        errorJSON.put("respuesta", false);
        errorJSON.put("mensaje", "Token de sesion no valido.");
        
        return errorJSON;
    }
    
    public boolean tokenValido(String token, String idUsuario, String perfil){
        boolean valido = false;
        verifier = null;
        originToken = null;
         // 1 token de emisión
        verifier = com.auth0.jwt.JWT.require(algorithm)
        .withIssuer("ProAtHome Server")
        .withAudience(perfil)
        .build();
        try{
            verifier.verify(token);
            originToken = com.auth0.jwt.JWT.decode(token);
            List<String> listAudience = originToken.getAudience();
            //VALIDAMOS QUE EL PERFIL CORRESPONDA
            if(perfil == PERFIL_CLIENTE){
                if(originToken.getSubject().equals(idUsuario) && listAudience.get(0).equals(PERFIL_CLIENTE))
                    valido = true;
            }else if(perfil == PERFIL_PROFESIONAL){
                if(originToken.getSubject().equals(idUsuario) && listAudience.get(0).equals(PERFIL_PROFESIONAL))
                    valido = true;
            }else if(perfil == ADMIN){
                if(originToken.getSubject().equals(idUsuario) && listAudience.get(0).equals(ADMIN))
                    valido = true;
            }
        } catch (JWTVerificationException e) {
            valido = false;
            e.printStackTrace();
        }
        
        return valido;
    }
    
    public String getToken(String idCliente, String perfil){
        // 1 token de emisión
        Date currentTime = new Date();
        String token = com.auth0.jwt.JWT.create()
                .withIssuer("ProAtHome Server") // Emisor
                .withSubject(idCliente) // ID de usuario
                .withAudience(perfil) // Unidades de usuario
                .withIssuedAt(currentTime) // hora de emisión
                .withExpiresAt(new Date(currentTime.getTime() + 24*3600*1000L)) // Validez de un día
                .withJWTId(idCliente) // Asignar ID de JWTController
                .withClaim("PublicClaim", "ProAtHome Server") // Definir información de dominio público
                .sign(algorithm);

        return token;
    }
    
}
