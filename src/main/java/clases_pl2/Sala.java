package clases_pl2;

import java.io.Serializable;

/**
 *
 * @author david
 */
public class Sala implements Serializable {
        private static final long serialVersionUID = 1L;

    
    private String nombre;
    private int aforoMaximo;
    public Sala(String nombre, int aforoMaximo) {
        this.nombre = nombre;
        this.aforoMaximo = aforoMaximo;
    }
    /**
     * Get the value of aforoMaximo
     *
     * @return the value of aforoMaximo
     */
    public int getAforoMaximo() {
        return aforoMaximo;
    }

    /**
     * Set the value of aforoMaximo
     *
     * @param aforoMaximo new value of aforoMaximo
     */
    public void setAforoMaximo(int aforoMaximo) {
        this.aforoMaximo = aforoMaximo;
    }


    /**
     * Get the value of nombre
     *
     * @return the value of nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Set the value of nombre
     *
     * @param nombre new value of nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Sala{" + "nombre=" + nombre + ", aforoMaximo=" + aforoMaximo + '}';
    }
}
