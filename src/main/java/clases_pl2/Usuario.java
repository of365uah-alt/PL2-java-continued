package clases_pl2;

import java.io.Serializable;

/**
 * Clase abstractica, De la que heredan socio y admin
 * @author david, samuel
 */
public abstract class Usuario implements Serializable {
    private String correo;
    private String clave;
    
    // Constructor

    /**
     *
     * @param correo
     * @param clave
     */
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
    
    /**
     * autenticar que su correo y clave son los correctos
     * @param correo
     * @param clave
     * @return True si se autentica correctamente
     */
    public boolean autenticar(String correo, String clave){
        return this.correo.equalsIgnoreCase(correo) && this.clave.equalsIgnoreCase(clave);
    }

    @Override
    public String toString() {
        return "Usuario{" + "correo=" + correo + '}';
    }
}
