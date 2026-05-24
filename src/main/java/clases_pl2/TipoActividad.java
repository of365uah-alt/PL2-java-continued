package clases_pl2;

import java.io.Serializable;

/**
 * Enumeración de tipos de actividades (Solo hay 4)
 * @author david, samuel
 */
//Enumeración de actividades
public enum TipoActividad implements Serializable{
    YOGA("Yoga"),
    MUSCULACION("Musculación"),
    CARDIO("Cardio"),
    NATACION("Natación");
    
    private final String descripcion;
    
    TipoActividad(String descripcion){
        this.descripcion = descripcion;
    }
    
    public String getDescripcion(){
        return descripcion;
    }
    
    @Override
    public String toString(){
        return descripcion;
    }
}
