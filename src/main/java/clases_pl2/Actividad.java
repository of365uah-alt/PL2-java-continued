package clases_pl2;

import java.io.Serializable;
/**
 *
 * @author david, samuel
 */
//Activadad de gimnasio, puede ser especializada a ActividadEspecial (herencia)
public class Actividad implements Serializable {
    
    private static final long serialVersionUID = 1L; //Persistencia

    private int id;
    private String titulo;
    private TipoActividad tipo; //Enumeración de 4 tipos
    private Sala sala; //adelante
    private Horario horario;
    private String monitor;
    private String rutaImagen; //Opcional, no estará en el constructor

    public Actividad(int id, String titulo, TipoActividad tipo, Sala sala, Horario horario, String monitor) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.sala = sala;
        this.horario = horario;
        this.monitor = monitor;
    }
    

    /**
     * Get the value of rutaImagen
     *
     * @return the value of rutaImagen
     */
    public String getRutaImagen() {
        return rutaImagen;
    }

    /**
     * Set the value of rutaImagen
     *
     * @param rutaImagen new value of rutaImagen
     */
    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }


    /**
     * Get the value of monitor
     *
     * @return the value of monitor
     */
    
    public String getMonitor() {
        return monitor;
    }

    /**
     * Set the value of monitor
     *
     * @param monitor new value of monitor
     */
    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    /**
     * Get the value of horario
     *
     * @return the value of horario
     */
    public Horario getHorario() {
        return horario;
    }

    /**
     * Set the value of horario
     *
     * @param horario new value of horario
     */
    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    /**
     * Get the value of sala
     *
     * @return the value of sala
     */
    public Sala getSala() {
        return sala;
    }

    /**
     * Set the value of sala
     *
     * @param sala new value of sala
     */
    public void setSala(Sala sala) {
        this.sala = sala;
    }

    /**
     * Get the value of tipo
     *
     * @return the value of tipo
     */
    public TipoActividad getTipo() {
        return tipo;
    }

    /**
     * Set the value of tipo
     *
     * @param tipo new value of tipo
     */
    public void setTipo(TipoActividad tipo) {
        this.tipo = tipo;
    }

    /**
     * Get the value of titulo
     *
     * @return the value of titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Set the value of titulo
     *
     * @param titulo new value of titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
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
    
    //SobreEscribir en actividades especiales
    public boolean esEspecial(){
        return false;
    }

    @Override
    public String toString() {
        return "Actividad{" + "id=" + id + ", titulo=" + titulo + ", tipo=" + tipo + ", sala=" + sala + ", horario=" + horario + ", monitor=" + monitor + '}';
    }
    
    

}
