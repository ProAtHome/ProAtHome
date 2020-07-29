package com.proathome.modelos;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Marvin
 */
public class Constantes {
    
    public static final int ESTATUS_ENCURSO = 11;
    public static final int ESTATUS_ENPAUSA = 12;
    public static final int ESTATUS_TERMINADO = 13;
    public static final int ESTATUS_ENCURSO_TE = 14;
    public static final int ESTATUS_ENPAUSA_TE = 15;
    public static final int ESTATUS_TERMINADO_TE = 16;
    public static final int TIPO_DE_TIEMPO_NORMAL = 1;
    public static final int TIPO_DE_TIEMPO_TE = 2;
     /*RUTA NIVELES*/
    public static final int BASICO = 1;
    public static final int INTERMEDIO = 2;
    public static final int AVANZADO = 3;
    public static final int BASICO_1 = 1;
    public static final int BASICO_2 = 2;
    public static final int BASICO_3 = 3;
    public static final int BASICO_4 = 4;
    public static final int BASICO_5 = 5;
    public static final int AVANZADO_1 = 1;
    public static final int AVANZADO_2 = 2;
    public static final int AVANZADO_3 = 3;
    public static final int AVANZADO_4 = 4;
    public static final int AVANZADO_5 = 5;
    public static final int INTERMEDIO_1 = 1;
    public static final int INTERMEDIO_2 = 2;
    public static final int INTERMEDIO_3 = 3;
    public static final int INTERMEDIO_4 = 4;
    public static final int INTERMEDIO_5 = 5;
    public static final  int BLOQUE1_BASICO1 = 1;
    public static final  int BLOQUE2_BASICO1 = 2;
    public static final  int BLOQUE3_BASICO1 = 3;
    public static final  int BLOQUE4_BASICO1 = 4;
    public static final  int BLOQUE5_BASICO1 = 5;
    public static final  int BLOQUE6_BASICO1 = 6;
    public static final  int BLOQUE1_BASICO2 = 1;
    public static final  int BLOQUE2_BASICO2 = 2;
    public static final  int BLOQUE3_BASICO2 = 3;
    public static final  int BLOQUE4_BASICO2 = 4;
    public static final  int BLOQUE5_BASICO2 = 5;
    public static final  int BLOQUE6_BASICO2 = 6;
    public static final  int BLOQUE1_BASICO3 = 1;
    public static final  int BLOQUE2_BASICO3 = 2;
    public static final  int BLOQUE3_BASICO3 = 3;
    public static final  int BLOQUE4_BASICO3 = 4;
    public static final  int BLOQUE5_BASICO3 = 5;
    public static final  int BLOQUE6_BASICO3 = 6;
    public static final  int BLOQUE7_BASICO3 = 7;
    public static final  int BLOQUE1_BASICO4 = 1;
    public static final  int BLOQUE2_BASICO4 = 2;
    public static final  int BLOQUE3_BASICO4 = 3;
    public static final  int BLOQUE4_BASICO4 = 4;
    public static final  int BLOQUE5_BASICO4 = 5;
    public static final  int BLOQUE6_BASICO4 = 6;
    public static final  int BLOQUE7_BASICO4 = 7;
    public static final  int BLOQUE1_BASICO5 = 1;
    public static final  int BLOQUE2_BASICO5 = 2;
    public static final  int BLOQUE3_BASICO5 = 3;
    public static final  int BLOQUE4_BASICO5 = 4;
    public static final  int BLOQUE5_BASICO5 = 5;
    public static final  int BLOQUE6_BASICO5 = 6;
    public static final  int BLOQUE7_BASICO5 = 7;
    public static final  int BLOQUE1_INTERMEDIO1= 1;
    public static final  int BLOQUE2_INTERMEDIO1= 2;
    public static final  int BLOQUE3_INTERMEDIO1= 3;
    public static final  int BLOQUE4_INTERMEDIO1= 4;
    public static final  int BLOQUE5_INTERMEDIO1= 5;
    public static final  int BLOQUE6_INTERMEDIO1= 6;
    public static final  int BLOQUE7_INTERMEDIO1= 7;
    public static final  int BLOQUE1_INTERMEDIO2= 1;
    public static final  int BLOQUE2_INTERMEDIO2= 2;
    public static final  int BLOQUE3_INTERMEDIO2= 3;
    public static final  int BLOQUE4_INTERMEDIO2= 4;
    public static final  int BLOQUE5_INTERMEDIO2= 5;
    public static final  int BLOQUE6_INTERMEDIO2= 6;
    public static final  int BLOQUE1_INTERMEDIO3= 1;
    public static final  int BLOQUE2_INTERMEDIO3= 2;
    public static final  int BLOQUE3_INTERMEDIO3= 3;
    public static final  int BLOQUE4_INTERMEDIO3= 4;
    public static final  int BLOQUE5_INTERMEDIO3= 5;
    public static final  int BLOQUE6_INTERMEDIO3= 6;
    public static final  int BLOQUE1_INTERMEDIO4= 1;
    public static final  int BLOQUE2_INTERMEDIO4= 2;
    public static final  int BLOQUE3_INTERMEDIO4= 3;
    public static final  int BLOQUE4_INTERMEDIO4= 4;
    public static final  int BLOQUE5_INTERMEDIO4= 5;
    public static final  int BLOQUE6_INTERMEDIO4= 6;
    public static final  int BLOQUE1_INTERMEDIO5= 1;
    public static final  int BLOQUE2_INTERMEDIO5= 2;
    public static final  int BLOQUE3_INTERMEDIO5= 3;
    public static final  int BLOQUE4_INTERMEDIO5= 4;
    public static final  int BLOQUE5_INTERMEDIO5= 5;
    public static final  int BLOQUE6_INTERMEDIO5= 6;
    public static final  int BLOQUE1_AVANZADO1= 1;
    public static final  int BLOQUE2_AVANZADO1= 2;
    public static final  int BLOQUE1_AVANZADO2= 1;
    public static final  int BLOQUE2_AVANZADO2= 2;
    public static final  int BLOQUE1_AVANZADO3= 1;
    public static final  int BLOQUE2_AVANZADO3= 2;
    public static final  int BLOQUE1_AVANZADO4= 1;
    public static final  int BLOQUE2_AVANZADO4= 2;
    public static final  int BLOQUE1_AVANZADO5= 1;
    public static final  int BLOQUE2_AVANZADO5= 2;
    /*FIN RUTA NIVELES*/
    static int valores[][] = {{1,1,1},{1,1,2},{1,1,3},{1,1,4},{1,1,5},{1,1,6},
                           {1,2,1},{1,2,2},{1,2,3},{1,2,4},{1,2,5},{1,2,6},
                           {1,3,1},{1,3,2},{1,3,3},{1,3,4},{1,3,5},{1,3,6},{1,3,7},
                           {1,4,1},{1,4,2},{1,4,3},{1,4,4},{1,4,5},{1,4,6},{1,4,7},
                           {1,5,1},{1,5,2},{1,5,3},{1,5,4},{1,5,5},{1,5,6},{1,5,7},
                           {2,1,1},{2,1,2},{2,1,3},{2,1,4},{2,1,5},{2,1,6},{2,1,7},
                           {2,2,1},{2,2,2},{2,2,3},{2,2,4},{2,2,5},{2,2,6},
                           {2,3,1},{2,3,2},{2,3,3},{2,3,4},{2,3,5},{2,3,6},
                           {2,4,1},{2,4,2},{2,4,3},{2,4,4},{2,4,5},{2,4,6},
                           {2,5,1},{2,5,2},{2,5,3},{2,5,4},{2,5,5},{2,5,6},
                           {3,1,1},{3,1,2},
                           {3,2,1},{3,2,2},
                           {3,3,1},{3,3,2},
                           {3,4,1},{3,4,2},
                           {3,5,1},{3,5,2}};
    
    
    public static JSONObject nuevoRegistro(int idSeccion, int idNivel, int idBloque){
        JSONObject ruta = new JSONObject();
        for(int i = 0; i < valores.length; i ++){ 
            if(idSeccion == valores[i][0] && idNivel == valores[i][1] && idBloque == valores[i][2]){
                ruta.put("idSeccion", valores[i+1][0]);
                ruta.put("idNivel", valores[i+1][1]);
                ruta.put("idBloque", valores[i+1][2]);
            }
        }
        
        return ruta;
    }
    
    public static int obtenerHorasBloque(int seccion, int nivel, int bloque){
        
        int minutos = 0;
        
        if(seccion == Constantes.BASICO){
            if(nivel == Constantes.BASICO_1){
                if(bloque == Constantes.BLOQUE1_BASICO1){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE2_BASICO1){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE3_BASICO1){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE4_BASICO1){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE5_BASICO1){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE6_BASICO1){
                    minutos = 660;
                }
            }else if(nivel == Constantes.BASICO_2){
                if(bloque == Constantes.BLOQUE1_BASICO2){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE2_BASICO2){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE3_BASICO2){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE4_BASICO2){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE5_BASICO2){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE6_BASICO2){
                    minutos = 660;
                }
            }else if(nivel == Constantes.BASICO_3){
                if(bloque == Constantes.BLOQUE1_BASICO3){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE2_BASICO3){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE3_BASICO3){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE4_BASICO3){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE5_BASICO3){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE6_BASICO3){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE7_BASICO3){
                    minutos = 540;
                }
            }else if(nivel == Constantes.BASICO_4){
                if(bloque == Constantes.BLOQUE1_BASICO4){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE2_BASICO4){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE3_BASICO4){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE4_BASICO4){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE5_BASICO4){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE6_BASICO4){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE7_BASICO4){
                    minutos = 600;
                }
            }else if(nivel == Constantes.BASICO_5){
                if(bloque == Constantes.BLOQUE1_BASICO5){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE2_BASICO5){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE3_BASICO5){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE4_BASICO5){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE5_BASICO5){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE6_BASICO5){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE7_BASICO5){
                    minutos = 540;
                }
            }
        }else if(seccion == Constantes.INTERMEDIO){
            if(nivel == Constantes.INTERMEDIO_1){
                if(bloque == Constantes.BLOQUE1_INTERMEDIO1){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE2_INTERMEDIO1){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE3_INTERMEDIO1){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE4_INTERMEDIO1){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE5_INTERMEDIO1){
                    minutos = 540;
                }else if(bloque == Constantes.BLOQUE6_INTERMEDIO1){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE7_INTERMEDIO1){
                    minutos = 540;
                }
            }else if(nivel == Constantes.INTERMEDIO_2){
                if(bloque == Constantes.BLOQUE1_INTERMEDIO2){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE2_INTERMEDIO2){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE3_INTERMEDIO2){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE4_INTERMEDIO2){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE5_INTERMEDIO2){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE6_INTERMEDIO2){
                    minutos = 600;
                }
            }else if(nivel == Constantes.INTERMEDIO_3){
                if(bloque == Constantes.BLOQUE1_INTERMEDIO1){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE2_INTERMEDIO3){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE3_INTERMEDIO3){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE4_INTERMEDIO3){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE5_INTERMEDIO3){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE6_INTERMEDIO3){
                    minutos = 600;
                }
            }else if(nivel == Constantes.INTERMEDIO_4){
                if(bloque == Constantes.BLOQUE1_INTERMEDIO4){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE2_INTERMEDIO4){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE3_INTERMEDIO4){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE4_INTERMEDIO4){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE5_INTERMEDIO4){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE6_INTERMEDIO4){
                    minutos = 660;
                }
            }else if(nivel == Constantes.INTERMEDIO_5){
                if(bloque == Constantes.BLOQUE1_INTERMEDIO5){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE2_INTERMEDIO5){
                    minutos = 600;
                }else if(bloque == Constantes.BLOQUE3_INTERMEDIO5){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE4_INTERMEDIO5){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE5_INTERMEDIO5){
                    minutos = 660;
                }else if(bloque == Constantes.BLOQUE6_INTERMEDIO5){
                    minutos = 660;
                }
            }
        }else if(seccion == Constantes.AVANZADO){
            if(nivel == Constantes.AVANZADO_1){
                if(bloque == Constantes.BLOQUE1_AVANZADO1){
                    minutos = 780;
                }else if(bloque == Constantes.BLOQUE2_AVANZADO1){
                    minutos = 720;
                }
            }else if(nivel == Constantes.AVANZADO_2){
                if(bloque == Constantes.BLOQUE1_AVANZADO2){
                    minutos = 720;
                }else if(bloque == Constantes.BLOQUE2_AVANZADO2){
                    minutos = 780;
                }
            }else if(nivel == Constantes.AVANZADO_3){
                if(bloque == Constantes.BLOQUE1_AVANZADO3){
                    minutos = 780;
                }else if(bloque == Constantes.BLOQUE2_AVANZADO3){
                    minutos = 720;
                }
            }else if(nivel == Constantes.AVANZADO_4){
                if(bloque == Constantes.BLOQUE1_AVANZADO4){
                    minutos = 720;
                }else if(bloque == Constantes.BLOQUE2_AVANZADO4){
                    minutos = 780;
                }
            }else if(nivel == Constantes.AVANZADO_5){
                if(bloque == Constantes.BLOQUE1_AVANZADO5){
                    minutos = 720;
                }else if(bloque == Constantes.BLOQUE2_AVANZADO5){
                    minutos = 780;
                }
            }
        }
        
        return minutos;
    }
    
}
