/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaz_pl2;
import clases_pl2.DiaSemana;
import clases_pl2.Socio;
import clases_pl2.ActividadEspecial;
import clases_pl2.Actividad;
import java.time.LocalTime;
import clases_pl2.TipoActividad;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import persistencia.EstadoAplicacion;
import persistencia.Gestor;

/**
 *
 * @author david
 */
public class UserInterface extends javax.swing.JFrame {
    /*debug
    
    Sala sala1 = new Sala("sala1", 20);
    Horario horario1 = new Horario(List.of(DiaSemana.L), LocalTime.of(18, 0), LocalTime.of(20,0));
    Actividad actividad1 = new Actividad(1, "Clase yoga", TipoActividad.YOGA, sala1, horario1, "monitor67");
    ArrayList<Actividad> actividades = new ArrayList<Actividad>();

*/ //debug
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UserInterface.class.getName());
    private String CorreoUsuario;
    DefaultTableModel modelTabla = new DefaultTableModel(0, 8) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Evita que el usuario edite la tabla por accidente
        }
    };
    private Socio usuarioActual;
    private final Gestor gestor = Gestor.getInstancia();
    DefaultTableModel modelTablaReservas = new DefaultTableModel(0, 7) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Hacemos que las celdas no se puedan editar haciendo doble clic
        }
    };
    
    
    

    /**
     * Creates new form UserInterface
     */
    public UserInterface(Socio usuario) throws IOException, ClassNotFoundException {
        gestor.cargarDatos();
        initComponents();
        this.setSize(860, 600);
        this.usuarioActual = usuario;
        this.CorreoUsuario = CorreoUsuario;
        this.setTitle(CorreoUsuario + " - Javafit Interfaz de Usuario");
        cargarDatosPerfil();
        //Verificador de String
        
        //Dia ComboBox
        DiaComboBox.removeAllItems();
        DiaComboBox.addItem("Todos");

        for (int i = 0; i < DiaSemana.values().length;i++){
            DiaComboBox.addItem(DiaSemana.values()[i].getNombre());
        }

        //Tipo ComboBox
        TipoComboBox.removeAllItems();
        TipoComboBox.addItem("Todos");
        for (int i = 0; i < TipoActividad.values().length;i++){
            TipoComboBox.addItem(TipoActividad.values()[i].getDescripcion());
        }
        
        //usuario Label
        if (usuario.isEsVip()){
            jLabelUsuario.setText(usuario.getNombre() + "[vip]");
        }else{
            jLabelUsuario.setText(usuario.getNombre());
        }
        
        // --- Tabla de Actividades ---
        // 1. Asignamos los nombres de las columnas para que aparezca la fila de información
        String[] columnasActividades = {"ID", "Clase", "Tipo", "Monitor", "Sala", "Horario", "Vip", "Precio"};
        modelTabla.setColumnIdentifiers(columnasActividades);
        
        // 2. Le asignamos el modelo ya configurado a la tabla visual
        this.jTable1.setModel(modelTabla);
        
        
        
        
        // 3. Rellenamos la tabla con los datos reales usando tu Gestor
        actualizarTabla(gestor.getActividades());
        
        // --- NUEVO: Configuración de la Tabla de Reservas ---
        // Asignamos los nombres de las columnas que nos has pedido
        String[] columnasReservas = {"ID", "Actividad", "Tipo", "Monitor", "Sala", "Fecha", "Coste"};
        modelTablaReservas.setColumnIdentifiers(columnasReservas);
        
        // Le decimos al componente visual que use nuestro modelo
        this.jTableReservas.setModel(modelTablaReservas);
        // Rellenamos la tabla con las reservas del usuario
        actualizarTablaReservas();
        
        this.jTable1.getColumnModel().getColumn(0).setMinWidth(30);
        this.jTable1.getColumnModel().getColumn(0).setMaxWidth(50);
        this.jTable1.getColumnModel().getColumn(0).setPreferredWidth(40);
        
        this.jTableReservas.getColumnModel().getColumn(0).setMinWidth(30);
        this.jTableReservas.getColumnModel().getColumn(0).setMaxWidth(50);
        this.jTableReservas.getColumnModel().getColumn(0).setPreferredWidth(40);
        //Tabla
        this.jTable1.setModel(modelTabla);
        actualizarTabla(gestor.getActividades());
        
        // --- NUEVO: Configuración de la etiqueta dinámica de Descripción ---
        
        // 1. La ocultamos nada más abrir la ventana
        jLabelDescrpción.setVisible(false);
        jLabelImagenVisual.setVisible(false);
        // 2. Le añadimos un "escuchador" a la tabla para detectar clics en las filas
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            // valueIsAdjusting evita que el evento se dispare dos veces por un solo clic
            if (!e.getValueIsAdjusting()) {
                actualizarDetallesActividad();
            }
        });
        // -------------------------------------------------------------------
        
        // ... (el resto de tu constructor sigue igual)
    }
    void actualizarTabla(List<Actividad> lista) {
        modelTabla.setRowCount(0);
        for (Actividad a : lista) {
            String precio = a.esEspecial()
                    ? String.format("%.2f€", ((ActividadEspecial)a).getPrecio()) : "-";
            modelTabla.addRow(new Object[]{
                a.getId(), a.getTitulo(), a.getTipo(), a.getMonitor(),
                a.getSala().getNombre(), a.getHorario(),
                a.esEspecial() ? "Sí" : "No", precio
            });
           
        }
        
    }
    
    private void actualizarTablaReservas() {
        // 1. Limpiamos la tabla por si tuviera datos antiguos
        modelTablaReservas.setRowCount(0);
        
        // 2. Le pedimos al gestor las reservas específicas de este usuario
        // Asegúrate de importar clases_pl2.Reserva en la parte superior si NetBeans te lo pide
        List<clases_pl2.Reserva> misReservas = gestor.getReservasDeSocio(usuarioActual);
        
        // 3. Recorremos cada reserva para extraer sus datos
        for (clases_pl2.Reserva r : misReservas) {
            clases_pl2.Actividad a = r.getActividad();
            
            // Damos formato al precio (ej: 29,99€)
            String costeFormateado = String.format("%.2f€", r.getCoste());
            
            // 4. Añadimos la fila completa con el orden exacto de columnas que pediste
            modelTablaReservas.addRow(new Object[]{
                r.getId(),
                a.getTitulo(),
                a.getTipo(),
                a.getMonitor(),
                a.getSala().getNombre(),
                r.getFecha().toString(),
                costeFormateado
            });
        }
    }
    private void actualizarDetallesActividad() {
        int filaSeleccionada = jTable1.getSelectedRow();
        
        // Si no hay ninguna fila seleccionada, ocultamos todo y salimos
        if (filaSeleccionada < 0) {
            jLabelDescrpción.setVisible(false);
            jLabelImagenVisual.setVisible(false); // Ocultamos la imagen
            return;
        }
        
        // Extraemos el ID de la columna 0
        int id = (int) modelTabla.getValueAt(filaSeleccionada, 0);
        
        // Buscamos la actividad en el Gestor
        Actividad act = gestor.getActividades().stream()
                .filter(a -> a.getId() == id).findFirst().orElse(null);
                
        if (act != null) {
            // --- 1. GESTIÓN DE LA DESCRIPCIÓN (Especiales) ---
            if (act.esEspecial()) {
                ActividadEspecial ae = (ActividadEspecial) act;
                jLabelDescrpción.setText("<html><b>Descripción:</b> " + ae.getDescripcion() + "</html>");
                jLabelDescrpción.setVisible(true);
            } else {
                jLabelDescrpción.setVisible(false);
            }
            
            // --- 2. GESTIÓN DE LA IMAGEN ---
            String ruta = act.getRutaImagen();
            if (ruta != null && !ruta.isBlank()) {
                try {
                    // Cargamos el archivo de imagen original
                    javax.swing.ImageIcon iconoOriginal = new javax.swing.ImageIcon(ruta);
                    
                    // Escalamos la imagen para que quepa en un recuadro de 150x150 píxeles (puedes ajustar estos números)
                    // SCALE_SMOOTH hace que la imagen no se vea pixelada al encogerla
                    java.awt.Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
                    
                    // Ponemos la imagen escalada en el JLabel y lo hacemos visible
                    jLabelImagenVisual.setIcon(new javax.swing.ImageIcon(imagenEscalada));
                    jLabelImagenVisual.setVisible(true);
                    
                } catch (Exception e) {
                    // Si el archivo se borró del ordenador o hay un error, lo ocultamos
                    jLabelImagenVisual.setIcon(null);
                    jLabelImagenVisual.setVisible(false);
                }
            } else {
                // Si la actividad no tiene ruta de imagen, ocultamos el JLabel
                jLabelImagenVisual.setIcon(null);
                jLabelImagenVisual.setVisible(false);
            }
        }}
    private void cargarDatosPerfil() {
        jTextFieldCorreo.setText(usuarioActual.getCorreo());
        jTextFieldCorreo.setEditable(false); // Opcional: Bloqueamos el correo para que no se pueda cambiar, suele ser la clave primaria
        
        jTextFieldNombre.setText(usuarioActual.getNombre());
        // Suponiendo que estos métodos (getTelefono, getDireccion, etc.) existen en tu clase Socio
        jTextFieldTelefono.setText( "" +usuarioActual.getTelefono());
        jTextFieldDireccion.setText(usuarioActual.getDireccion());
        jTextFieldTarjeta.setText(usuarioActual.getTarjetaDeCredito());
        
        jCheckBoxSuscripciónVIP.setSelected(usuarioActual.isEsVip());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        TipoComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        DiaComboBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        BuscarBoton = new javax.swing.JButton();
        LimpiarBoton = new javax.swing.JButton();
        monitorFormattedField = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonReservar = new javax.swing.JButton();
        jLabelDescrpción = new javax.swing.JLabel();
        jLabelImagenVisual = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableReservas = new javax.swing.JTable();
        jButtonCancelarReserva = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabelMiPerfil = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jCheckBoxSuscripciónVIP = new javax.swing.JCheckBox();
        jTextFieldCorreo = new javax.swing.JTextField();
        jTextFieldNombre = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextFieldTelefono = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldDireccion = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTextFieldTarjeta = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextFieldNuevaContraseña = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextFieldRepetirContraseña = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabelUsuario = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Javafit Interfaz de Usuario");
        setMinimumSize(new java.awt.Dimension(500, 300));
        setPreferredSize(new java.awt.Dimension(800, 500));
        setSize(new java.awt.Dimension(800, 500));

        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jPanel1.setName("Actividades"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(850, 600));

        jLabel1.setText("Filtros de búsqueda");

        jLabel2.setText("Tipo:");

        TipoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        TipoComboBox.addActionListener(this::TipoComboBoxActionPerformed);

        jLabel3.setText("Monitor:");

        DiaComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        DiaComboBox.addActionListener(this::DiaComboBoxActionPerformed);

        jLabel4.setText("Dia:");

        BuscarBoton.setText("Buscar");
        BuscarBoton.addActionListener(this::BuscarBotonActionPerformed);

        LimpiarBoton.setText("Limpiar");
        LimpiarBoton.addActionListener(this::LimpiarBotonActionPerformed);

        monitorFormattedField.addActionListener(this::monitorFormattedFieldActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TipoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(monitorFormattedField, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DiaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(BuscarBoton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LimpiarBoton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(186, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel2)
                            .addComponent(TipoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(DiaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(monitorFormattedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BuscarBoton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LimpiarBoton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null,  new Boolean(false), null},
                {null, null, null, null, null, null,  new Boolean(false), null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "Clase", "Tipo", "Monitor", "Sala", "Horario", "Vip", "Precio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setToolTipText("");
        jScrollPane1.setViewportView(jTable1);

        jButtonReservar.setText("Reservar Actividad");
        jButtonReservar.addActionListener(this::jButtonReservarActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabelDescrpción, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelImagenVisual, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButtonReservar)
                        .addGap(37, 37, 37))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabelImagenVisual, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabelDescrpción, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButtonReservar)
                .addGap(38, 38, 38))
        );

        jTabbedPane1.addTab("Actividades", jPanel1);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setText("Mis Reservas");

        jTableReservas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTableReservas);

        jButtonCancelarReserva.setText("CancelarReserva");
        jButtonCancelarReserva.addActionListener(this::jButtonCancelarReservaActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 799, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(56, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonCancelarReserva)
                .addGap(58, 58, 58))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonCancelarReserva)
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Mis reservas", jPanel2);

        jLabelMiPerfil.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabelMiPerfil.setText("Mi Perfil");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Correo:");

        jCheckBoxSuscripciónVIP.setSelected(true);
        jCheckBoxSuscripciónVIP.setText("Suscripción VIP (49.99€/mes)");
        jCheckBoxSuscripciónVIP.addActionListener(this::jCheckBoxSuscripciónVIPActionPerformed);

        jTextFieldCorreo.addActionListener(this::jTextFieldCorreoActionPerformed);

        jTextFieldNombre.addActionListener(this::jTextFieldNombreActionPerformed);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel11.setText("Nombre:");

        jTextFieldTelefono.addActionListener(this::jTextFieldTelefonoActionPerformed);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel14.setText("Telefono:");

        jTextFieldDireccion.addActionListener(this::jTextFieldDireccionActionPerformed);

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setText("Dirección:");

        jTextFieldTarjeta.addActionListener(this::jTextFieldTarjetaActionPerformed);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setText("Tarjeta:");

        jTextFieldNuevaContraseña.addActionListener(this::jTextFieldNuevaContraseñaActionPerformed);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel17.setText("Nueva contraseña:");

        jTextFieldRepetirContraseña.addActionListener(this::jTextFieldRepetirContraseñaActionPerformed);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel18.setText("Repetir contraseña:");

        jButton2.setText("Guardar cambios");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelMiPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
                                .addGap(39, 39, 39)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldNuevaContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldRepetirContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jCheckBoxSuscripciónVIP, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldTarjeta))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDireccion))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldTelefono))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldNombre))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldCorreo)))))
                .addGap(121, 121, 121))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelMiPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldCorreo)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldNombre)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldTelefono)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldDireccion)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldTarjeta)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jCheckBoxSuscripciónVIP, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldNuevaContraseña)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldRepetirContraseña)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jButton2)
                .addContainerGap(142, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Mi Perfil", jPanel3);

        jLabel5.setFont(new java.awt.Font("Trebuchet MS", 1, 48)); // NOI18N
        jLabel5.setText("JavaFit");

        jLabelUsuario.setText("jLabel6");

        jButton1.setText("Cerrar Sesión");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelUsuario)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(69, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Actividades");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BuscarBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BuscarBotonActionPerformed
        String tipoStr = (String) TipoComboBox.getSelectedItem();
        String diaStr = (String) DiaComboBox.getSelectedItem();
        String monitor = monitorFormattedField.getText().trim();
        //Traducir el  String a Enum
        TipoActividad tipo = null; 
        if (tipoStr != null && !tipoStr.equals("Todos")) {
            for (TipoActividad t : TipoActividad.values()) {
                if (t.getDescripcion().equals(tipoStr)) {
                    tipo = t;
                    break;
                }
            }
        }
        
        DiaSemana dia = null;
        if (diaStr != null && !diaStr.equals("Todos")) {
            for (DiaSemana d : DiaSemana.values()) {
                if (d.getNombre().equals(diaStr)) {
                    dia = d;
                    break;
                }
            }
        }
        
        if (monitor.isEmpty()) {
            monitor = null;
        }
        List<Actividad> actividadesFiltradas = gestor.buscarActividades(tipo, monitor, dia);

        actualizarTabla(actividadesFiltradas);
    }//GEN-LAST:event_BuscarBotonActionPerformed

    private void LimpiarBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LimpiarBotonActionPerformed
        TipoComboBox.setSelectedItem("Todos");
        DiaComboBox.setSelectedItem("Todos");
        monitorFormattedField.setText("");

        actualizarTabla(gestor.getActividades());
    }//GEN-LAST:event_LimpiarBotonActionPerformed

    private void DiaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DiaComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DiaComboBoxActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
       InicioSesion InicioSesion = new InicioSesion();
       InicioSesion.setVisible(true);
       InicioSesion.setLocationRelativeTo(null); //Centrar
       this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void TipoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TipoComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TipoComboBoxActionPerformed

    private void monitorFormattedFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monitorFormattedFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_monitorFormattedFieldActionPerformed

    private void jTextFieldCorreoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCorreoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCorreoActionPerformed

    private void jTextFieldNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNombreActionPerformed

    private void jTextFieldTelefonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTelefonoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldTelefonoActionPerformed

    private void jTextFieldDireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldDireccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldDireccionActionPerformed

    private void jTextFieldTarjetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTarjetaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldTarjetaActionPerformed

    private void jTextFieldNuevaContraseñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNuevaContraseñaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNuevaContraseñaActionPerformed

    private void jTextFieldRepetirContraseñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldRepetirContraseñaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldRepetirContraseñaActionPerformed

    private void jCheckBoxSuscripciónVIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSuscripciónVIPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxSuscripciónVIPActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            String nombre = jTextFieldNombre.getText().trim();
            String telefonoStr = jTextFieldTelefono.getText().trim();
            String direccion = jTextFieldDireccion.getText().trim();
            String tarjetaStr = jTextFieldTarjeta.getText().trim();

            if (nombre.isEmpty() || telefonoStr.isEmpty() || direccion.isEmpty() || tarjetaStr.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Por favor, rellena todos los datos personales básicos.", 
                    "Faltan datos", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return; // Detenemos la ejecución aquí, no seguimos guardando
            }

            if (!telefonoStr.matches("\\d+")) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "El teléfono solo debe contener números.", 
                    "Error de formato", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!tarjetaStr.matches("\\d+")) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "La tarjeta solo debe contener números.", 
                    "Error de formato", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            String nuevaClave = jTextFieldNuevaContraseña.getText();
            String repetirClave = jTextFieldRepetirContraseña.getText();

            if (!nuevaClave.isEmpty() || !repetirClave.isEmpty()) {
                if (nuevaClave.equals(repetirClave)) {
                    usuarioActual.setClave(nuevaClave); 
                    jTextFieldNuevaContraseña.setText("");
                    jTextFieldRepetirContraseña.setText("");
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Las contraseñas no coinciden. Inténtalo de nuevo.", 
                        "Error de Contraseña", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                    return; // Detenemos la ejecución si las contraseñas están mal
                }
            }

            usuarioActual.setNombre(nombre);
            usuarioActual.setDireccion(direccion);
            usuarioActual.setEsVip(jCheckBoxSuscripciónVIP.isSelected());
            
            usuarioActual.setTelefono(Integer.parseInt(telefonoStr)); 
            usuarioActual.setTarjetaDeCredito(tarjetaStr);

            if (usuarioActual.isEsVip()){
                jLabelUsuario.setText(usuarioActual.getNombre() + " [vip]");
            } else {
                jLabelUsuario.setText(usuarioActual.getNombre());
            }
            gestor.actualizarSocio(usuarioActual); 
            gestor.guardarDatos();
            
            javax.swing.JOptionPane.showMessageDialog(this, 
                "¡Perfil actualizado correctamente!", 
                "Éxito", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Se ha producido un error inesperado al guardar: " + ex.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, "Error al guardar perfil", ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButtonReservarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReservarActionPerformed
        // 1. Averiguar qué fila de la tabla ha seleccionado el usuario
        int filaSeleccionada = jTable1.getSelectedRow();

        // 2. Si no hay ninguna seleccionada (devuelve -1), avisamos al usuario
        if (filaSeleccionada == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Por favor, selecciona primero una actividad de la tabla haciendo clic en ella.", 
                "Ninguna actividad seleccionada", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return; // Detenemos la ejecución
        }

        // 3. Extraer el ID de la actividad de esa fila (está en la columna 0)
        // Hacemos un cast a (Integer) porque sabemos que esa columna guarda enteros
        int idActividad = (Integer) jTable1.getValueAt(filaSeleccionada, 0);

        // 4. Buscar el objeto Actividad real en la lista de tu Gestor usando ese ID
        Actividad actividadSeleccionada = null;
        for (Actividad a : gestor.getActividades()) {
            if (a.getId() == idActividad) {
                actividadSeleccionada = a;
                break; // Cuando la encontramos, paramos de buscar
            }
        }

        // 5. Si la hemos encontrado, abrimos la nueva ventana
        if (actividadSeleccionada != null) {
            // Usamos nuestro nuevo constructor pasándole la actividad y el usuario logueado
            ReservarActividad ventanaReserva = new ReservarActividad(actividadSeleccionada, usuarioActual);
            ventanaReserva.setVisible(true); // Mostramos la ventana

        } else {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Ha ocurrido un error interno. No se ha encontrado la actividad.", 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonReservarActionPerformed

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        actualizarTablaReservas();
        // TODO add your handling code here:
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void jButtonCancelarReservaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarReservaActionPerformed
        try {
            // 1. Averiguar qué fila de la tabla de reservas está seleccionada
            int filaSeleccionada = jTableReservas.getSelectedRow();

            // 2. Si no hay ninguna seleccionada (devuelve -1), avisamos al usuario y paramos
            if (filaSeleccionada == -1) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Por favor, selecciona primero la reserva que deseas cancelar haciendo clic en la tabla.", 
                    "Ninguna reserva seleccionada", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return; // Detenemos la ejecución aquí
            }

            // 3. Confirmar con el usuario si realmente desea cancelar (¡Buena práctica!)
            int confirmacion = javax.swing.JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas cancelar esta reserva?",
                    "Confirmar cancelación",
                    javax.swing.JOptionPane.YES_NO_OPTION);

            // Si el usuario elige "Sí" (YES_OPTION), procedemos a borrarla
            if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
                
                // 4. Extraer el ID de la reserva (está en la columna 0 de la tabla)
                // Usamos (Integer) para transformar el Objeto de la tabla a un número entero
                int idReserva = (Integer) jTableReservas.getValueAt(filaSeleccionada, 0);

                // 5. Llamar al Gestor para cancelar la reserva
                boolean cancelada = gestor.cancelarReserva(idReserva);

                if (cancelada) {
                    // 6. Si se borró correctamente, guardamos los cambios en el archivo de datos
                    gestor.guardarDatos();

                    // 7. Actualizamos la tabla visualmente para que desaparezca la fila
                    actualizarTablaReservas();

                    // 8. Mostramos un mensaje de éxito
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Reserva cancelada con éxito.", 
                        "Éxito", 
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Por si la reserva ya no existiera en el gestor por algún motivo
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "No se pudo encontrar la reserva en el sistema.", 
                        "Error", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception ex) {
            // Capturamos cualquier error inesperado para que el programa no se cierre
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Se produjo un error al cancelar la reserva: " + ex.getMessage(), 
                "Error Interno", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonCancelarReservaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new UserInterface(new Socio("Debug",2,"","",true,"","")).setVisible(true);
            } catch (IOException ex) {
                System.getLogger(UserInterface.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } catch (ClassNotFoundException ex) {
                System.getLogger(UserInterface.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }); 
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BuscarBoton;
    private javax.swing.JComboBox<String> DiaComboBox;
    private javax.swing.JButton LimpiarBoton;
    private javax.swing.JComboBox<String> TipoComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonCancelarReserva;
    private javax.swing.JButton jButtonReservar;
    private javax.swing.JCheckBox jCheckBoxSuscripciónVIP;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelDescrpción;
    private javax.swing.JLabel jLabelImagenVisual;
    private javax.swing.JLabel jLabelMiPerfil;
    private javax.swing.JLabel jLabelUsuario;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTableReservas;
    private javax.swing.JTextField jTextFieldCorreo;
    private javax.swing.JTextField jTextFieldDireccion;
    private javax.swing.JTextField jTextFieldNombre;
    private javax.swing.JTextField jTextFieldNuevaContraseña;
    private javax.swing.JTextField jTextFieldRepetirContraseña;
    private javax.swing.JTextField jTextFieldTarjeta;
    private javax.swing.JTextField jTextFieldTelefono;
    private javax.swing.JFormattedTextField monitorFormattedField;
    // End of variables declaration//GEN-END:variables
}
