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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Este gestor es la clase más importante del programa, Solo hay una instancia en todo momento.
 * Gestiona la conexión interfaz - clases y la persistencia
 * En tiempo de ejecución tiene todas las clases del programa guardadas
 * 
 * @author david, Samuel
 * 
 * Este gestor sirve como única instancia para todas las clases, tieniendo información de los datos para todas las clases a la vez
 * 
 */
public class Gestor {

    private static final String RUTA_DATOS = "data/javafit.dat"; //Directorio data
    private static Gestor instancia;
    //Definición de datos a trabajar
    private List<Actividad> actividades = new ArrayList<>();
    private List<Socio> socios = new ArrayList<>();
    private List<Administrador> administradores = new ArrayList<>();
    private List<Reserva> reservas = new ArrayList<>();
    private int nextActividadId = 1;
    private int nextReservaId = 1;
    private static final String DIR_RECIBOS = "recibos"; //Directorio Recibo

    /**
     *
     * @param estado
     * @throws IOException
     */
    public static void guardar(EstadoAplicacion estado) throws IOException {
        // Asegurar que existe el directorio (para primera vez guardando datos)
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(RUTA_DATOS)))) {
            oos.writeObject(estado);
        }
    }

    /**
     *
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * Carga los datos de datos.dat a memoria
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
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
                // Primera ejecución: datos de administradores
            }
        }

    /**
     * Guarda los datos del gestor en persistencia (datos.dat)
     * @throws IOException
     */
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
        
    /**
     * Agrega Actividad a las actividades en tiempo de ejecución (usa gardarDatos() para persistencia)
     * @param a Actividad que se quiere agregar
     */
    public void agregarActividad(Actividad a) {
        a.setId(nextActividadId++);
        actividades.add(a);
    }

    /**
     * Sirve para actualizar una actividad sin necesidad de borrarla y crearlade nuevo (eso causaría la eliminación de las reservas)
     * @param idOriginal
     * @param nuevaActividad
     */
    public void actualizarActividad(int idOriginal, Actividad nuevaActividad) {
    for (int i = 0; i < actividades.size(); i++) {
        Actividad original = actividades.get(i);
        if (original.getId() == idOriginal) {
            // Preservar el ID original
            nuevaActividad.setId(idOriginal);
            
            // Reemplazar la actividad vieja por la nueva en el catálogo
            actividades.set(i, nuevaActividad);
            
            // Actualizar la referencia de la actividad en todas las reservas existentes
            for (Reserva r : reservas) {
                if (r.getActividad().getId() == idOriginal) {
                    r.setActividad(nuevaActividad);
                }
            }
            break;
        }
    }
}

    /**
     * Elimina la actividad y todas sus reservas (mediantre el uso de streams)
     * @param id
     * @return true si se ha encontrado la actividad y borrada
     */
    public boolean eliminarActividad(int id) { // Optional 
        Optional<Actividad> act = actividades.stream().filter(a -> a.getId() == id).findFirst();
        if (act.isPresent()) {
            reservas.removeIf(r -> r.getActividad().getId() == id);
            actividades.remove(act.get());
            return true;
        }
        return false;
    }

    /**
     *
     * @return lista de actividades no modificable
     */
    public List<Actividad> getActividades() {
        return Collections.unmodifiableList(actividades); // Lista inmodificable
    }
    
    
    
            //Filtrar actividades (Se utilizan Streams)

    /**
     * buscar actividades por TipoActividad
     * @param tipo
     * @return lista de las actividades que coinciden con ese tipo
     */
    
    public List<Actividad> buscarPorTipo(TipoActividad tipo) {
        return actividades.stream()
                .filter(a -> a.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    /**
     * Busca actividades con un monitor específico
     * @param monitor
     * @return Actividades con ese monitor
     */
    public List<Actividad> buscarPorMonitor(String monitor) {
        String busq = monitor.toLowerCase();
        return actividades.stream()
                .filter(a -> a.getMonitor().toLowerCase().contains(busq))
                .collect(Collectors.toList());
    }

    /**
     *Busca actividades por el día 
     * @param dia
     * @return Actividades de ese día
     */
    public List<Actividad> buscarPorDia(DiaSemana dia) {
        return actividades.stream()
                .filter(a -> a.getHorario().incluyeDia(dia))
                .collect(Collectors.toList());
    }

    /**
     * Filtro genérico, en caso de buscar por los 3 filtros
     * @param tipo
     * @param monitor
     * @param dia
     * @return Actividades que cumplan las 3 condiciones
     */
    public List<Actividad> buscarActividades(TipoActividad tipo, String monitor, DiaSemana dia) {
        return actividades.stream()
                .filter(a -> tipo    == null || a.getTipo() == tipo)
                .filter(a -> monitor == null || monitor.isBlank()
                           || a.getMonitor().toLowerCase().contains(monitor.toLowerCase()))
                .filter(a -> dia     == null || a.getHorario().incluyeDia(dia))
                .collect(Collectors.toList());
    }
    
    //Socios

    /**
     * Registra el socio en el gestor
     * @param socio
     * @throws IllegalArgumentException En caso de que ya exista el socio
     */
    
    public void registrarSocio(Socio socio) throws IllegalArgumentException {
        boolean existe = socios.stream()
                .anyMatch(s -> s.getCorreo().equalsIgnoreCase(socio.getCorreo()));
        if (existe) {
            throw new IllegalArgumentException("Ya existe un socio con ese correo: " + socio.getCorreo());
        }
        socios.add(socio);
    }

    /**
     * Autentica el socio (Para incio de sesión)
     * @param correo
     * @param clave
     * @return el socio autenticado
     * @throws IllegalArgumentException si no se encuentra ningún socio con la contraseña y correo
     */
    public Socio autenticarSocio(String correo, String clave) throws IllegalArgumentException {
        return socios.stream()
                .filter(s -> s.autenticar(correo, clave))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos."));
    }

    /**
     * Autentica el admin
     * @param correo
     * @param clave
     * @return el admin autenticado
     * @throws IllegalArgumentException Si no se encuentra admin con la contraseña y correo
     */
    public Administrador autenticarAdmin(String correo, String clave) throws IllegalArgumentException {
        return administradores.stream()
                .filter(a -> a.autenticar(correo, clave))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos."));
    }

    /**
     *
     * @return lista de todos los socio en memoria 
     */
    public List<Socio> getSocios() {
        return Collections.unmodifiableList(socios);
    }
    /**
     * Crea una reserva para un socio en una actividad y fecha dadas.
     * Verifica aforo y que la fecha coincida con el horario de la actividad.
     * Si es actividad especial, calcula el coste aplicando descuento VIP.
     *
     * @param socio
     * @param actividad
     * @param fecha
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

    // Verificar aforo (stream .count)
    long reservasEnFecha = reservas.stream()
            .filter(r -> r.getActividad().getId() == actividad.getId()
                      && r.getFecha().equals(fecha))
            .count();
    if (reservasEnFecha >= actividad.getSala().getAforoMaximo()) { //lanzar excepción
        throw new IllegalArgumentException(
                "No hay aforo disponible para esta actividad en la fecha seleccionada.");
    }

    // Verificar que el socio no tiene ya una reserva para esta actividad y fecha (Streams)
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
    // generar Recibo
    try {
            Recibo.generarRecibo(reserva, DIR_RECIBOS);
        } catch (IOException e) {
            // No es crítico, la reserva se registra igualmente
        }
    return reserva;
    }
    
    /**
     * Cancela una reserva (la elimina)
     * El .txt con el recibo seguirá existiendo
     * @param reservaId
     * @return true si se encuentra la reserva para cancelar
     */
    public boolean cancelarReserva(int reservaId) {
        Optional<Reserva> r = reservas.stream().filter(res -> res.getId() == reservaId).findFirst();
        if (r.isPresent()) {
            reservas.remove(r.get());
            return true;
        }
        return false;
    }
    
    /**
     * Devuelve todas las reservas de un socio
     * @param socio
     * @return lista de reservas del socio
     */
    public List<Reserva> getReservasDeSocio(Socio socio) {
        return reservas.stream()
                .filter(r -> r.getSocio().getCorreo().equalsIgnoreCase(socio.getCorreo()))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Devuelve las reservas, ordenadas por fecha
     * @return las reservas ordenadas por fecha
     */
    public List<Reserva> getTodasReservas() {
        return reservas.stream().sorted().collect(Collectors.toList());
    }
    

    /**
     * Reservas ordenadas desde una fecha
     * @param desde fecha desde la que quieres ver las reservas
     * @return lista de reservas filtradas desde fecha
     */
        public List<Reserva> getReservasDesde(LocalDate desde) {
        return reservas.stream()
                .filter(r -> !r.getFecha().isBefore(desde))
                .sorted()
                .collect(Collectors.toList());
    }
    /**
     * Devuelve el número de participantes de una actividad
     * @param actividad
     * @param fecha
     * @return Devuelve el número de participantes de una actividad
     */
    public long getAforo(Actividad actividad, LocalDate fecha) {
        return reservas.stream()
                .filter(r -> r.getActividad().getId() == actividad.getId()
                          && r.getFecha().equals(fecha))
                .count();
    }
    
    /**
     * Devuelve la instancia única, y si no existe la crea
     * @return la instancia
     */
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
        administradores.add(new Administrador("admin@javafit.com", "admin")); // Correo - contraseña !!
        administradores.add(new Administrador("admin2@javafit.com", "admin2"));

    }

    /**
     * Metodo de actualizar Socio
     * @param socioActualizado
     */
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
