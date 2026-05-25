package persistencia;
import clases_pl2.Reserva;
import clases_pl2.Socio;
import clases_pl2.Administrador;
import clases_pl2.Actividad;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * La clase con todos los datos que se guardan en data.dat
 * @author david, samuel
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
