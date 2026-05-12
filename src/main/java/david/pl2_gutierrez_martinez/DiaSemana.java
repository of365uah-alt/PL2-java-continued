package david.pl2_gutierrez_martinez;

/**
 *
 * @author david
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
