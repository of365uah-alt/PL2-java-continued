package clases_pl2;

import java.io.Serializable;

/**
 * Clase Socio (Hereda de Usuario)
 * @author david, samuel
 */

//Representa Socio registrado de JavaFit
public class Socio extends Usuario implements Serializable {
    
    private static final long serialVersionUID = 1L; //Recomendado para persistencia
    //Constantes de precio
    private static final double DESCUENTO_VIP = 0.10; //Descuento
    public static final double CUOTA_BASICO = 29.99; //Cuotas
    public static final double CUOTA_VIP = 49.99;

    
    
    // Propiedades
    private String nombre;
    private int telefono;
    private String direccion;
    private String tarjetaDeCredito;
    private boolean esVip;
    
    
    
    
    //Constructor

    /**
     * Constructor
     * @param nombre
     * @param telefono
     * @param direccion
     * @param tarjetaDeCredito
     * @param esVip
     * @param correo
     * @param clave
     */
    
    public Socio(String nombre, int telefono, String direccion, String tarjetaDeCredito, boolean esVip, String correo, String clave) {
        super(correo, clave);
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.tarjetaDeCredito = tarjetaDeCredito;
        this.esVip = esVip;
    }

    /**
     * Get the value of esVip
     *
     * @return the value of esVip
     */
    public boolean isEsVip() {
        return esVip;
    }

    /**
     * Set the value of esVip
     *
     * @param esVip new value of esVip
     */
    public void setEsVip(boolean esVip) {
        this.esVip = esVip;
    }

    /**
     * Get the value of tarjetaDeCredito
     *
     * @return the value of tarjetaDeCredito
     */
    public String getTarjetaDeCredito() {
        return tarjetaDeCredito;
    }

    /**
     * Set the value of tarjetaDeCredito
     *
     * @param tarjetaDeCredito new value of tarjetaDeCredito
     */
    public void setTarjetaDeCredito(String tarjetaDeCredito) {
        this.tarjetaDeCredito = tarjetaDeCredito;
    }

    /**
     * Get the value of direccion
     *
     * @return the value of direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Set the value of direccion
     *
     * @param direccion new value of direccion
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Get the value of telefono
     *
     * @return the value of telefono
     */
    public int getTelefono() {
        return telefono;
    }

    /**
     * Set the value of telefono
     *
     * @param telefono new value of telefono
     */
    public void setTelefono(int telefono) {
        this.telefono = telefono;
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
    
    //Cálculo de precios
    public double calcularPrecio(double precioBase) {
        if (esVip) {
            return precioBase * (1.0 - DESCUENTO_VIP);
        }
        return precioBase;
    }
    
    //Devolver cuota mensual
    public double getCuotaMensual(){
        if (esVip){
            return CUOTA_VIP;
        }else{
            return CUOTA_BASICO;
        }
    }
    
    
    

    @Override
    public String toString() {
        return super.toString() + " # Socio{"+ "VIP=" + esVip + "nombre=" + nombre + ", telefono=" + telefono + ", direccion=" + direccion + ", tarjetaDeCredito=" + tarjetaDeCredito + '}';
    }

}
