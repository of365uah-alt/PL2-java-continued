package clases_pl2;

import java.io.Serializable;

/**
 * Clase administrador (Solo se crean 2 al inicio de la persistencia)
 * @author david, samuel
 * 
 *  --> admin@javafit.com password: admin
 * 
 */
public class Administrador extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L; //Recomendado
    public Administrador(String correo, String clave) {
        super(correo, clave);
    }

    @Override
    public String toString() {
        return "Administrador{" + super.toString() + '}';
    }
    
    
}
