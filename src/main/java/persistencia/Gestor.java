//hola
package persistencia;

import clases_pl2.DiaSemana;
import clases_pl2.Reserva;
import clases_pl2.TipoActividad;
import clases_pl2.Socio;
import clases_pl2.ActividadEspecial;
import clases_pl2.Administrador;
import clases_pl2.Actividad;
import clases_pl2.Recibo;
import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author david
 * 
 * Este gestor sirve como única instancia para todas las clases, tieniendo información de los datos para todas las clases a la vez
 * Para su correcto uso se debe cargar al empezar
 * 
 */
public class Gestor {

    private static final String RUTA_DATOS = "data/javafit.dat";
    private static Gestor instancia;
    //Definición de datos a trabajar
    private List<Actividad> actividades = new ArrayList<>();
    private List<Socio> socios = new ArrayList<>();
    private List<Administrador> administradores = new ArrayList<>();
    private List<Reserva> reservas = new ArrayList<>();
    private int nextActividadId = 1;
    private int nextReservaId = 1;
        private static final String DIR_RECIBOS = "recibos";

    
    
    //Debug!
    public static void main(String[] args) {
        try {
            
            Gestor g = Gestor.getInstancia();
            
             g.guardar(new EstadoAplicacion());
            // System.out.println("Datos guardados con éxito.");
        } catch (Exception e) {
            e.printStackTrace(); // Esto te dirá exactamente qué falla
        }
    }
    
    

    public static void guardar(EstadoAplicacion estado) throws IOException {
        // Asegurar que existe el directorio
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(RUTA_DATOS)))) {
            oos.writeObject(estado);
        }
    }

    public static EstadoAplicacion cargar() throws IOException, ClassNotFoundException {
        File fichero = new File(RUTA_DATOS);
        if (!fichero.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(fichero)))) {
            return (EstadoAplicacion) ois.readObject();
        }
    }
    public void cargarDatos() throws IOException, ClassNotFoundException {
            EstadoAplicacion estado = cargar();
            if (estado != null) {
                actividades     = new ArrayList<>(estado.actividades);
                socios          = new ArrayList<>(estado.socios);
                administradores = new ArrayList<>(estado.administradores);
                reservas        = new ArrayList<>(estado.reservas);
                nextActividadId = estado.nextActividadId;
                nextReservaId   = estado.nextReservaId;
            } else {
                inicializarAdminPorDefecto();
                guardarDatos();
                // Primera ejecución: datos de demostración
            }
        }
        public void guardarDatos() throws IOException {
        EstadoAplicacion estado = new EstadoAplicacion();
        estado.actividades     = actividades;
        estado.socios          = socios;
        estado.administradores = administradores;
        estado.reservas        = reservas;
        estado.nextActividadId = nextActividadId;
        estado.nextReservaId   = nextReservaId;
        guardar(estado);
    }
    public void agregarActividad(Actividad a) {
        a.setId(nextActividadId++);
        actividades.add(a);
    }
    public boolean eliminarActividad(int id) { // Optional 
        Optional<Actividad> act = actividades.stream().filter(a -> a.getId() == id).findFirst();
        if (act.isPresent()) {
            reservas.removeIf(r -> r.getActividad().getId() == id);
            actividades.remove(act.get());
            return true;
        }
        return false;
    }
    public List<Actividad> getActividades() {
        return Collections.unmodifiableList(actividades); // Lista inmodificable
    }
    
    
    
    //Filtrar actividades
    public List<Actividad> buscarPorTipo(TipoActividad tipo) {
        return actividades.stream()
                .filter(a -> a.getTipo() == tipo)
                .collect(Collectors.toList());
    }
    public List<Actividad> buscarPorMonitor(String monitor) {
        String busq = monitor.toLowerCase();
        return actividades.stream()
                .filter(a -> a.getMonitor().toLowerCase().contains(busq))
                .collect(Collectors.toList());
    }
    public List<Actividad> buscarPorDia(DiaSemana dia) {
        return actividades.stream()
                .filter(a -> a.getHorario().incluyeDia(dia))
                .collect(Collectors.toList());
    }
    public List<Actividad> buscarActividades(TipoActividad tipo, String monitor, DiaSemana dia) {
        return actividades.stream()
                .filter(a -> tipo    == null || a.getTipo() == tipo)
                .filter(a -> monitor == null || monitor.isBlank()
                           || a.getMonitor().toLowerCase().contains(monitor.toLowerCase()))
                .filter(a -> dia     == null || a.getHorario().incluyeDia(dia))
                .collect(Collectors.toList());
    }
    
    //Socios
    
    public void registrarSocio(Socio socio) throws IllegalArgumentException {
        boolean existe = socios.stream()
                .anyMatch(s -> s.getCorreo().equalsIgnoreCase(socio.getCorreo()));
        if (existe) {
            throw new IllegalArgumentException("Ya existe un socio con ese correo: " + socio.getCorreo());
        }
        socios.add(socio);
    }
        public Socio autenticarSocio(String correo, String clave) throws IllegalArgumentException {
        return socios.stream()
                .filter(s -> s.autenticar(correo, clave))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos."));
    }
     public Administrador autenticarAdmin(String correo, String clave) throws IllegalArgumentException {
        return administradores.stream()
                .filter(a -> a.autenticar(correo, clave))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos."));
    }
    public List<Socio> getSocios() {
        return Collections.unmodifiableList(socios);
    }
    /**
     * Crea una reserva para un socio en una actividad y fecha dadas.
     * Verifica aforo y que la fecha coincida con el horario de la actividad.
     * Si es actividad especial, calcula el coste aplicando descuento VIP.
     *
     * @return la reserva creada
     * @throws IllegalArgumentException si hay algún problema de validación
     */
    public Reserva crearReserva(Socio socio, Actividad actividad, LocalDate fecha) throws IllegalArgumentException {

    // Verificar que la fecha coincide con el horario de la actividad
    DiaSemana diaSemana = mapDayOfWeek(fecha.getDayOfWeek());
    if (!actividad.getHorario().incluyeDia(diaSemana)) {
        throw new IllegalArgumentException(
                "La fecha seleccionada (" + fecha + ") no corresponde a un día "
                + "en que se imparte esta actividad.\nDías disponibles: "
                + actividad.getHorario());
    }

    // Verificar aforo: contar reservas para esta actividad en esta fecha
    long reservasEnFecha = reservas.stream()
            .filter(r -> r.getActividad().getId() == actividad.getId()
                      && r.getFecha().equals(fecha))
            .count();
    if (reservasEnFecha >= actividad.getSala().getAforoMaximo()) {
        throw new IllegalArgumentException(
                "No hay aforo disponible para esta actividad en la fecha seleccionada.");
    }

    // Verificar que el socio no tiene ya una reserva para esta actividad y fecha
    boolean yaReservado = reservas.stream().anyMatch(r ->
            r.getSocio().getCorreo().equalsIgnoreCase(socio.getCorreo())
            && r.getActividad().getId() == actividad.getId()
            && r.getFecha().equals(fecha));
    if (yaReservado) {
        throw new IllegalArgumentException("Ya tienes una reserva para esta actividad en esa fecha.");
    }

    // Calcular coste
    double coste = 0.0;
    if (actividad.esEspecial()) {
        ActividadEspecial ae = (ActividadEspecial) actividad;
        coste = socio.calcularPrecio(ae.getPrecio());
    }

    Reserva reserva = new Reserva(nextReservaId++, socio, actividad, fecha, coste);
    reservas.add(reserva);
    
    
    //Generar Recibo!
    try {
            Recibo.generarRecibo(reserva, DIR_RECIBOS);
        } catch (IOException e) {
            // No es crítico, la reserva se registra igualmente
        }

    
    
    
    //Generar Recibo!
    
    return reserva;
    
    }
    
    //Cancelar Reserva
    public boolean cancelarReserva(int reservaId) {
        Optional<Reserva> r = reservas.stream().filter(res -> res.getId() == reservaId).findFirst();
        if (r.isPresent()) {
            reservas.remove(r.get());
            return true;
        }
        return false;
    }
    
        /** Devuelve todas las reservas de un socio concreto, ordenadas por fecha. */
    public List<Reserva> getReservasDeSocio(Socio socio) {
        return reservas.stream()
                .filter(r -> r.getSocio().getCorreo().equalsIgnoreCase(socio.getCorreo()))
                .sorted()
                .collect(Collectors.toList());
    }
    //Devuelve las reservas, ordenadas por fecha
    public List<Reserva> getTodasReservas() {
        return reservas.stream().sorted().collect(Collectors.toList());
    }
    
    //Reservas ordenadas desde una fecha
        public List<Reserva> getReservasDesde(LocalDate desde) {
        return reservas.stream()
                .filter(r -> !r.getFecha().isBefore(desde))
                .sorted()
                .collect(Collectors.toList());
    }
    //Devuelve el número de participantes de una actividad
    public long getAforo(Actividad actividad, LocalDate fecha) {
        return reservas.stream()
                .filter(r -> r.getActividad().getId() == actividad.getId()
                          && r.getFecha().equals(fecha))
                .count();
    }
    
    
    public static Gestor getInstancia() {
        if (instancia == null) {
            instancia = new Gestor();
        }
        return instancia;
    }

    //covierte la enumeración en day of the week
    private DiaSemana mapDayOfWeek(java.time.DayOfWeek dow) {
        return switch (dow) {
            case MONDAY    -> DiaSemana.L;
            case TUESDAY   -> DiaSemana.M;
            case WEDNESDAY -> DiaSemana.X;
            case THURSDAY  -> DiaSemana.J;
            case FRIDAY    -> DiaSemana.V;
            case SATURDAY  -> DiaSemana.S;
            case SUNDAY    -> DiaSemana.D;
        };
    }
    
    
    private void inicializarAdminPorDefecto() { // Admins!
        administradores.add(new Administrador("admin@javafit.com", "admin"));
        administradores.add(new Administrador("admin2@javafit.com", "admin2"));

    }
    public void actualizarSocio(Socio socioActualizado) {
        // Recorremos la lista de socios guardada en el Gestor
        for (int i = 0; i < socios.size(); i++) {
            Socio socioEnGestor = socios.get(i);
            
            // Comparamos los correos electrónicos ignorando mayúsculas/minúsculas
            if (socioEnGestor.getCorreo().equalsIgnoreCase(socioActualizado.getCorreo())) {
                
                // Si encontramos coincidencia, reemplazamos el socio viejo por el actualizado
                socios.set(i, socioActualizado);
                
                // Salimos del bucle porque ya hemos encontrado y actualizado al usuario
                break; 
            }
        }
    }
}
