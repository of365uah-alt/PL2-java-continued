package david.pl2_gutierrez_martinez;

import java.io.Serializable;

/**
 *
 * @author david
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
