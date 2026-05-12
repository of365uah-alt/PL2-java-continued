package clases_pl2;

import java.io.Serializable;

/**
 *
 * @author david
 */
public abstract class Usuario implements Serializable {
    private String correo;
    private String clave;
    
    // Constructor
    public Usuario(String correo, String clave) {
        this.correo = correo;
        this.clave = clave;
    }
    
    /**
     * Get the value of clave
     *
     * @return the value of clave
     */
    

    public String getClave() {
        return clave;
    }

    /**
     * Set the value of clave
     *
     * @param clave new value of clave
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * Get the value of correo
     *
     * @return the value of correo
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Set the value of correo
     *
     * @param correo new value of correo
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public boolean autenticar(String correo, String clave){
        return this.correo.equalsIgnoreCase(correo) && this.clave.equalsIgnoreCase(clave);
        //autenticar que su correo y clave son los correctos
    }

    @Override
    public String toString() {
        return "Usuario{" + "correo=" + correo + '}';
    }
}
