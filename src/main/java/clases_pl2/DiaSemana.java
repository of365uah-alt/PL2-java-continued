package clases_pl2;

/**
 * Enumeración de días de la semana
 * @author david, samuel
 */
public enum DiaSemana {
    L("Lunes"),
    M("Martes"),
    X("Miércoles"),
    J("Jueves"),
    V("Viernes"),
    S("Sábado"),
    D("Domingo");
    
    private final String nombre;
    DiaSemana(String nombre){
        this.nombre = nombre;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    @Override
    public String toString(){
        return nombre;
    }
}
