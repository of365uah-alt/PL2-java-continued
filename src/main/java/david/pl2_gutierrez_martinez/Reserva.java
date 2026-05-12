/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package david.pl2_gutierrez_martinez;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author david
 */
public class Reserva implements Comparable<Reserva>, Serializable {
    private static final long serialVersionUID = 1L; //Recomendado
    
    private int id;
    private Socio socio;
    private Actividad actividad;
    //Fecha de la actividad
    private LocalDate fecha;
    //Coste de la reserva
    private double coste;
    //Fecha de tomar la reserva
    private LocalDateTime fechaReserva;

    public Reserva(int id, Socio socio, Actividad actividad, LocalDate fecha, double coste) {
        this.id = id;
        this.socio = socio;
        this.actividad = actividad;
        this.fecha = fecha;
        this.coste = coste;
        this.fechaReserva = LocalDateTime.now();
    }

    
    
    /**
     * Get the value of fechaReserva
     *
     * @return the value of fechaReserva
     */
    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    /**
     * Set the value of fechaReserva
     *
     * @param fechaReserva new value of fechaReserva
     */
    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }


    /**
     * Get the value of coste
     *
     * @return the value of coste
     */
    public double getCoste() {
        return coste;
    }

    /**
     * Set the value of coste
     *
     * @param coste new value of coste
     */
    public void setCoste(double coste) {
        this.coste = coste;
    }


    /**
     * Get the value of fecha
     *
     * @return the value of fecha
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Set the value of fecha
     *
     * @param fecha new value of fecha
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }


    /**
     * Get the value of actividad
     *
     * @return the value of actividad
     */
    public Actividad getActividad() {
        return actividad;
    }

    /**
     * Set the value of actividad
     *
     * @param actividad new value of actividad
     */
    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    /**
     * Get the value of socio
     *
     * @return the value of socio
     */
    public Socio getSocio() {
        return socio;
    }

    /**
     * Set the value of socio
     *
     * @param socio new value of socio
     */
    public void setSocio(Socio socio) {
        this.socio = socio;
    }


    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Reserva otra){
        return this.fecha.compareTo(otra.fecha);
    }

    @Override
    public String toString() {
        return "Reserva{" + "id=" + id + ", socio=" + socio + ", actividad=" + actividad + ", fecha=" + fecha + ", coste=" + coste + ", fechaReserva=" + fechaReserva + '}';
    }
    
    
}
