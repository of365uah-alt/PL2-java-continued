package clases_pl2;

import java.io.Serializable;

/**
 *
 * @author david, samuel
 */
//Herencia de actividad. +Descrpción +Coste
public class ActividadEspecial extends Actividad implements Serializable {
    private static final long serialVersionUID = 1L; //Recomendado para serialización
    private double Precio;
    private String descripcion;

    public ActividadEspecial(double Precio, String descripcion, int id, String titulo, TipoActividad tipo, Sala sala, Horario horario, String monitor) {
        super(id, titulo, tipo, sala, horario, monitor);
        this.Precio = Precio;
        this.descripcion = descripcion;
    }
    
    

    /**
     * Get the value of descripcion
     *
     * @return the value of descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Set the value of descripcion
     *
     * @param descripcion new value of descripcion
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Get the value of Precio
     *
     * @return the value of Precio
     */
    public double getPrecio() {
        return Precio;
    }

    /**
     * Set the value of Precio
     *
     * @param Precio new value of Precio
     */
    public void setPrecio(double Precio) {
        this.Precio = Precio;
    }

    
    @Override
    public boolean esEspecial(){
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " # ActividadEspecial{" + "Precio=" + Precio + ", descripcion=" + descripcion + '}';
    }
    
}
