/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import david.pl2_gutierrez_martinez.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author david
 */
public class EstadoAplicacion implements Serializable {

    private static final long serialVersionUID = 1L;
    public List<Actividad> actividades;
    public List<Socio> socios;
    public List<Administrador> administradores;
    public List<Reserva> reservas;
    public int nextActividadId;
    public int nextReservaId;

    public EstadoAplicacion() {
        actividades = new ArrayList<>();
        socios = new ArrayList<>();
        administradores = new ArrayList<>();
        reservas = new ArrayList<>();
        nextActividadId = 1;
        nextReservaId = 1;
    }
}
