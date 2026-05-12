package clases_pl2;

import java.io.Serializable;

/**
 *
 * @author david
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
