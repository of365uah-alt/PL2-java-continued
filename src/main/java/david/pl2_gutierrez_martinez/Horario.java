package david.pl2_gutierrez_martinez;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author david
 */
public class Horario implements Serializable {
    
        private static final long serialVersionUID = 1L;

    
    private List<DiaSemana> dias;

    public Horario(List<DiaSemana> dias, LocalTime horaFin, LocalTime horaInicio) {
        this.dias = dias;
        this.horaFin = horaFin;
        this.horaInicio = horaInicio;
    }

    /**
     * Get the value of dias
     *
     * @return the value of dias
     */
    public List<DiaSemana> getDias() {
        return dias;
    }
    
    private LocalTime horaFin;

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }


    private LocalTime horaInicio;

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    /**
     * Set the value of dias
     *
     * @param dias new value of dias
     */
    public void setDias(List<DiaSemana> dias) {
        this.dias = dias;
    }
    
    
    //Codigo Importante
    public boolean incluyeDia(DiaSemana dia){
         return dias.contains(dia);
    }

    @Override
    public String toString() {
        return "Horario{" + "dias=" + dias + ", horaFin=" + horaFin + ", horaInicio=" + horaInicio + '}';
    }
    

}
