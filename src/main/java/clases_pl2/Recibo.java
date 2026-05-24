/**
 *
 * @author david, Samuel
 */
package clases_pl2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Clase para crear un recibo. Archivo .txt en carpeta recibos
 * al confirmar una reserva.
 */
public class Recibo {


    /** Formato de fecha para los recibos. */
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    //Como está pensado como clase utilidad, no se instancia
    private Recibo() {}
    /**
     * Genera un fichero de texto con el recibo de la reserva.
     *
     * @param reserva    la reserva confirmada
     * @param directorioRecibos ruta de la carpeta donde guardar el fichero
     * @return la ruta del fichero generado
     * @throws IOException si no se puede crear el fichero
     */
    public static String generarRecibo(Reserva reserva, String directorioRecibos) throws IOException {
        // Existe direcitorio
        File dir = new File(directorioRecibos);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String nombreFichero = "recibo_" + reserva.getId() + ".txt";
        File fichero = new File(dir, nombreFichero);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fichero))) {
            bw.write("============================================");
            bw.newLine();
            bw.write("          RECIBO - JAVAFIT GIMNASIO         ");
            bw.newLine();
            bw.write("============================================");
            bw.newLine();
            bw.write("Número de reserva : #" + reserva.getId());
            bw.newLine();
            bw.write("Fecha de emisión   : " + reserva.getFechaReserva().format(FMT_DATETIME));
            bw.newLine();
            bw.write("--------------------------------------------");
            bw.newLine();
            bw.write("SOCIO");
            bw.newLine();
            bw.write("  Nombre  : " + reserva.getSocio().getNombre());
            bw.newLine();
            bw.write("  Correo  : " + reserva.getSocio().getCorreo());
            bw.newLine();
            bw.write("  Tipo    : " + (reserva.getSocio().isEsVip()? "VIP" : "Básico"));
            bw.newLine();
            bw.write("--------------------------------------------");
            bw.newLine();
            bw.write("ACTIVIDAD");
            bw.newLine();
            bw.write("  Título  : " + reserva.getActividad().getTitulo());
            bw.newLine();
            bw.write("  Tipo    : " + reserva.getActividad().getTipo());
            bw.newLine();
            bw.write("  Monitor : " + reserva.getActividad().getMonitor());
            bw.newLine();
            bw.write("  Sala    : " + reserva.getActividad().getSala().getNombre());
            bw.newLine();
            bw.write("  Horario : " + reserva.getActividad().getHorario());
            bw.newLine();
            bw.write("  Fecha   : " + reserva.getFecha().format(FMT_FECHA));
            bw.newLine();
            if (reserva.getActividad().esEspecial()) {
                ActividadEspecial ae = (ActividadEspecial) reserva.getActividad();
                bw.write("  Precio base: " + String.format("%.2f€", ae.getPrecio()));
                bw.newLine();
                if (reserva.getSocio().isEsVip()) {
                    bw.write("  Descuento VIP (10%): -" +
                            String.format("%.2f€", ae.getPrecio() * 0.10));
                    bw.newLine();
                }
            }
            bw.write("--------------------------------------------");
            bw.newLine();
            bw.write("  TOTAL COBRADO: " + String.format("%.2f€", reserva.getCoste()));
            bw.newLine();
            bw.write("  Tarjeta: **** **** **** " +
                    reserva.getSocio().getTarjetaDeCredito().substring(
                            Math.max(0, reserva.getSocio().getTarjetaDeCredito().length() - 4)));
            bw.newLine();
            bw.write("============================================");
            bw.newLine();
            bw.write("       Gracias por elegir Javafit           ");
            bw.newLine();
            bw.write("============================================");
        }

        return fichero.getAbsolutePath();
    }
}
