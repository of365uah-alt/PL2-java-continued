package interfaz_pl2;
import clases_pl2.DiaSemana;
import clases_pl2.Socio;
import clases_pl2.ActividadEspecial;
import clases_pl2.Actividad;
import clases_pl2.Administrador;
import clases_pl2.TipoActividad;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import persistencia.Gestor;

/**
 *
 * @author david, Samuel
 */
public class AdminInterface extends javax.swing.JFrame {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminInterface.class.getName());
    private String CorreoUsuario;
    DefaultTableModel modelTabla = new DefaultTableModel(0, 8);
    
    private final Gestor gestor = Gestor.getInstancia();
    //Modelos de tablas
    DefaultTableModel modelTablaSocios = new DefaultTableModel(0, 5) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    DefaultTableModel modelTablaReservas = new DefaultTableModel(0, 6) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Bloquea la edición manual
        }
    };
    public AdminInterface(Administrador usuario) throws IOException, ClassNotFoundException {
        gestor.cargarDatos();

        initComponents();
        this.setTitle(CorreoUsuario + " - Javafit Interfaz de Usuario");
        
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
        jLabelUsuario.setText("Administrador");
        
        //Tabla
        this.jTable1.setModel(modelTabla);
        
        actualizarTabla(gestor.getActividades());
        TipoComboBoxTipoUsuario.removeAllItems();
        TipoComboBoxTipoUsuario.addItem("Todos");
        TipoComboBoxTipoUsuario.addItem("VIP");
        TipoComboBoxTipoUsuario.addItem("Básico");
        
        //Configurar las columnas de la tabla de socios
        String[] columnasSocios = {"Nombre", "Correo", "Teléfono", "Tipo", "Cuota Mensual"};
        modelTablaSocios.setColumnIdentifiers(columnasSocios);
        this.jTableSocios.setModel(modelTablaSocios);
        
        //Tabla
        this.jTable1.setModel(modelTabla);
        actualizarTabla(gestor.getActividades());
        
        
        //Etiqueta de descripción (Visibilidad) + imagen
        
        // La ocultamos nada más abrir la ventana
        jLabelDescrpción.setVisible(false);
        jLabelImagenVisual.setVisible(false);
        this.pack();
        //añadimos un "escuchador" a la tabla para detectar clics en las filas
        //Los escuchadrores me los enseñó un compañero 
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            // valueIsAdjusting evita que el evento se dispare dos veces por un solo clic
            if (!e.getValueIsAdjusting()) {
                actualizarDetallesActividad();
            }
        });
        // Cargar todos los socios al abrir la ventana
        // Asumimos que gestor.getSocios() devuelve la List<Socio> completa
        actualizarTablaSocios(gestor.getSocios());
     
        // ---> Reservas
        
        //Configurar tabla reservas
        String[] columnasReservas = {"ID", "Socio", "Actividad", "Tipo", "Fecha", "Coste"};
        modelTablaReservas.setColumnIdentifiers(columnasReservas);
        this.jTableReservas.setModel(modelTablaReservas);
        
        //cargar las reservas
        actualizarTablaReservas(gestor.getTodasReservas());
        
        // Ajustes visuales con la ID
        this.jTableReservas.getColumnModel().getColumn(0).setMaxWidth(50);
    }
    //Lógica de actualización de tabla (Actividades)
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
        //Lógica de actualización de tabla (Socios)

    void actualizarTablaSocios(List<Socio> lista) {
        modelTablaSocios.setRowCount(0); // Limpiar datos antiguos
        
        for (Socio s : lista) {
            // Traducimos el booleano 'esVip' a texto
            String tipoUsuario = s.isEsVip() ? "VIP" : "Básico";
            // Formateamos la cuota para que se vea como dinero (ej: 29,99€)
            String cuota = String.format("%.2f€", s.getCuotaMensual());
            
            // Añadimos la fila con los datos (Nombre, Correo, Teléfono, Tipo, Cuota)
            modelTablaSocios.addRow(new Object[]{
                s.getNombre(), s.getCorreo(), s.getTelefono(), tipoUsuario, cuota
            });
        }}
        //Lógica de actualización de tabla (Reservas)

    void actualizarTablaReservas(List<clases_pl2.Reserva> lista) {
        modelTablaReservas.setRowCount(0); 
        
        for (clases_pl2.Reserva r : lista) {
            try {
                String costeFormateado = String.format("%.2f€", r.getCoste());
                String nombreSocio = (r.getSocio() != null) ? r.getSocio().getNombre() : "Socio Borrado";
                String tituloAct = (r.getActividad() != null) ? r.getActividad().getTitulo() : "Actividad Borrada";
                String tipoAct = (r.getActividad() != null && r.getActividad().getTipo() != null) 
                                 ? r.getActividad().getTipo().getDescripcion() 
                                 : "Sin tipo";
                String fechaStr = (r.getFecha() != null) ? r.getFecha().toString() : "Sin fecha";
                modelTablaReservas.addRow(new Object[]{
                    r.getId(),
                    nombreSocio,
                    tituloAct,
                    tipoAct,
                    fechaStr,
                    costeFormateado
                });
                
            } catch (Exception e) {
                System.out.println("Error al cargar la reserva ID " + r.getId() + ": " + e.getMessage());
            }
        }
    
    }
    //Lógica de refresco de tabla actividades
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
            // GESTIÓN DE LA DESCRIPCIÓN
            if (act.esEspecial()) {
                ActividadEspecial ae = (ActividadEspecial) act;
                jLabelDescrpción.setText("<html><b>Descripción:</b> " + ae.getDescripcion() + "</html>");
                jLabelDescrpción.setVisible(true);
            } else {
                jLabelDescrpción.setVisible(false);
            }
            
            //GESTIÓN DE LA IMAGEN
            String ruta = act.getRutaImagen();
            if (ruta != null && !ruta.isBlank()) {
                try {
                    javax.swing.ImageIcon iconoOriginal = new javax.swing.ImageIcon(ruta);
                    
                    // SCALE_SMOOTH hace que la imagen no se vea pixelada al encogerla
                    java.awt.Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);                    
                    // Ponemos la imagen escalada en el JLabel y lo hacemos visible
                    jLabelImagenVisual.setIcon(new javax.swing.ImageIcon(imagenEscalada));
                    jLabelImagenVisual.setVisible(true);
                    
                } catch (Exception e) {
                    // Si el archivo se borró del ordenador o hay un error, lo ocultamos
                    // !!!Esto es importante por si mando actividades con imagénes, ya que tu ordernador no las tiene
                    jLabelImagenVisual.setIcon(null);
                    jLabelImagenVisual.setVisible(false);
                }
            } else {
                // Si la actividad no tiene ruta de imagen, ocultamos el JLabel (para empaquetar)
                jLabelImagenVisual.setIcon(null);
                jLabelImagenVisual.setVisible(false);
            }
            this.pack();
        }}
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
        LimpiarBoton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonEliminar = new javax.swing.JButton();
        jButtonEditar = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabelDescrpción = new javax.swing.JLabel();
        jLabelImagenVisual = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        TipoComboBoxTipoUsuario = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldNombreOCorreo = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableSocios = new javax.swing.JTable();
        jButtonBuscarSocios = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButtonBuscarReservas = new javax.swing.JButton();
        jSpinnerFechaReservas = new javax.swing.JSpinner();
        jCheckBoxFiltroPorFecha = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableReservas = new javax.swing.JTable();
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
        setSize(new java.awt.Dimension(800, 500));

        jPanel1.setName("Actividades"); // NOI18N

        jLabel1.setText("Filtros de búsqueda");

        jLabel2.setText("Tipo:");

        TipoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Monitor:");

        DiaComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        DiaComboBox.addActionListener(this::DiaComboBoxActionPerformed);

        jLabel4.setText("Dia:");

        BuscarBoton.setText("Buscar");
        BuscarBoton.addActionListener(this::BuscarBotonActionPerformed);

        LimpiarBoton.setText("Limpiar");
        LimpiarBoton.addActionListener(this::LimpiarBotonActionPerformed);

        LimpiarBoton1.setText("Actualizar");
        LimpiarBoton1.addActionListener(this::LimpiarBoton1ActionPerformed);

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
                .addGap(18, 18, 18)
                .addComponent(LimpiarBoton1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(168, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
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
                            .addComponent(LimpiarBoton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LimpiarBoton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
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

        jButtonEliminar.setBackground(new java.awt.Color(255, 153, 153));
        jButtonEliminar.setForeground(new java.awt.Color(0, 0, 0));
        jButtonEliminar.setText("Eliminar");
        jButtonEliminar.addActionListener(this::jButtonEliminarActionPerformed);

        jButtonEditar.setBackground(new java.awt.Color(204, 204, 255));
        jButtonEditar.setForeground(new java.awt.Color(0, 0, 0));
        jButtonEditar.setText("Editar");
        jButtonEditar.addActionListener(this::jButtonEditarActionPerformed);

        jButton4.setBackground(new java.awt.Color(0, 255, 0));
        jButton4.setForeground(new java.awt.Color(0, 0, 0));
        jButton4.setText("Crear");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelDescrpción, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonEditar)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonEliminar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelImagenVisual, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelImagenVisual, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabelDescrpción, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEliminar)
                    .addComponent(jButtonEditar)
                    .addComponent(jButton4))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Actividades", jPanel1);

        jLabel6.setText("Filtros de búsqueda");

        jLabel7.setText("Tipo:");

        TipoComboBoxTipoUsuario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        TipoComboBoxTipoUsuario.addActionListener(this::TipoComboBoxTipoUsuarioActionPerformed);

        jLabel8.setText("Nombre/Correo:");

        jTextFieldNombreOCorreo.addActionListener(this::jTextFieldNombreOCorreoActionPerformed);

        jTableSocios.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTableSocios);

        jButtonBuscarSocios.setText("Buscar");
        jButtonBuscarSocios.addActionListener(this::jButtonBuscarSociosActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(TipoComboBoxTipoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldNombreOCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonBuscarSocios)
                                .addGap(0, 379, Short.MAX_VALUE))
                            .addComponent(jScrollPane2))))
                .addGap(29, 29, 29))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(TipoComboBoxTipoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextFieldNombreOCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBuscarSocios))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );

        jTabbedPane1.addTab("Socios", jPanel2);

        jLabel9.setText("Filtrar por fecha");

        jLabel10.setText("Desde:");

        jButtonBuscarReservas.setText("Buscar");
        jButtonBuscarReservas.addActionListener(this::jButtonBuscarReservasActionPerformed);

        jSpinnerFechaReservas.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.ERA));

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
        jScrollPane3.setViewportView(jTableReservas);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jCheckBoxFiltroPorFecha)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(jSpinnerFechaReservas, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonBuscarReservas)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 932, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10)
                    .addComponent(jButtonBuscarReservas)
                    .addComponent(jSpinnerFechaReservas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxFiltroPorFecha))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );

        jTabbedPane1.addTab("Reservas", jPanel3);

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
                .addContainerGap(156, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelUsuario)
                .addGap(12, 12, 12)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(43, 43, 43)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Actividades");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BuscarBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BuscarBotonActionPerformed
        //Lógica de filtrado de actividades
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
        //Botor de limpiar filtros (También actualiza)
        TipoComboBox.setSelectedItem("Todos");
        DiaComboBox.setSelectedItem("Todos");
        monitorFormattedField.setText("");

        actualizarTabla(gestor.getActividades());
    }//GEN-LAST:event_LimpiarBotonActionPerformed

    private void DiaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DiaComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DiaComboBoxActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       //Cerrar Sesión
       InicioSesion InicioSesion = new InicioSesion();
       InicioSesion.setVisible(true);
       InicioSesion.setLocationRelativeTo(null); //Centrar
       this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        //Crear Actividad (Instanciar nueva interfaz)
        CrearActividad crearactividad = new CrearActividad(this, rootPaneCheckingEnabled);
        crearactividad.setLocationRelativeTo(null);
        crearactividad.setVisible(true);
        actualizarTabla(gestor.getActividades());
        
        
        
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void LimpiarBoton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LimpiarBoton1ActionPerformed
        actualizarTabla(gestor.getActividades());
    }//GEN-LAST:event_LimpiarBoton1ActionPerformed

    private void jButtonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEliminarActionPerformed
        //Lógica de eliminado de actividad
        int fila = jTable1.getSelectedRow();
        if (fila < 0) {
            //Excepción de no seleccionar actividad
            JOptionPane.showMessageDialog(this, "Selecciona una actividad.");
            return; 
        }
        int id = (int) modelTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, // Option Panel!
                "¿Eliminar la actividad seleccionada y todas sus reservas?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            gestor.eliminarActividad(id);
            actualizarTabla(gestor.getActividades());
            try { // Guardar Datos
                gestor.guardarDatos();
            } catch (IOException ex) {
                System.getLogger(AdminInterface.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
// TODO add your handling code here:
    }//GEN-LAST:event_jButtonEliminarActionPerformed

    private void jButtonEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditarActionPerformed
        //Lógica de actualizar Actividad
        int fila = jTable1.getSelectedRow();
            //Excepción de no seleccionar actividad
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una actividad."); return; }
        int id = (int) modelTabla.getValueAt(fila, 0);
        Actividad act = gestor.getActividades().stream() //stream
                .filter(a -> a.getId() == id).findFirst().orElse(null);
        if (act == null) return;
        ActualizarActividad actualizaractividad = new ActualizarActividad(this, true, act);
        actualizaractividad.setLocationRelativeTo(null);
        actualizaractividad.setVisible(true);
        
        actualizarTabla(gestor.getActividades());
        try { // Guardar Datos
            gestor.guardarDatos();
        } catch (IOException ex) {
            System.getLogger(AdminInterface.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
    }//GEN-LAST:event_jButtonEditarActionPerformed

    private void TipoComboBoxTipoUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TipoComboBoxTipoUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TipoComboBoxTipoUsuarioActionPerformed

    private void jTextFieldNombreOCorreoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNombreOCorreoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNombreOCorreoActionPerformed

    private void jButtonBuscarSociosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBuscarSociosActionPerformed
        //Lógica de búsqueda de socioss
        String tipoSeleccionado = (String) TipoComboBoxTipoUsuario.getSelectedItem();
        // Lo pasamos a minúsculas para que la búsqueda ignore mayúsculas/minúsculas
        String textoBusqueda = jTextFieldNombreOCorreo.getText().trim().toLowerCase(); 

        List<Socio> todosLosSocios = gestor.getSocios();
        List<Socio> sociosFiltrados = new ArrayList<>();

        //  lanzar sobre todos los socios y aplicar los filtros
        for (Socio s : todosLosSocios) {
            
            //Comprobar el Tipo (VIP / Básico / Todos)
            boolean coincideTipo = true;
            if (tipoSeleccionado.equals("VIP") && !s.isEsVip()) {
                coincideTipo = false; // Queremos VIP pero este no lo es
            } else if (tipoSeleccionado.equals("Básico") && s.isEsVip()) {
                coincideTipo = false; // Queremos Básico pero este es VIP
            }

            // Comprobar Nombre o Correo
            boolean coincideTexto = true;
            if (!textoBusqueda.isEmpty()) {
                boolean enNombre = s.getNombre().toLowerCase().contains(textoBusqueda);
                boolean enCorreo = s.getCorreo().toLowerCase().contains(textoBusqueda);
                
                if (!enNombre && !enCorreo) {
                    coincideTexto = false;
                }
            }

            if (coincideTipo && coincideTexto) {
                sociosFiltrados.add(s);
            }
        }

        // Actualizar la tabla visual con la lista ya filtrada
        actualizarTablaSocios(sociosFiltrados);
    }//GEN-LAST:event_jButtonBuscarSociosActionPerformed

    private void jButtonBuscarReservasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBuscarReservasActionPerformed
        //Comprobar si está activo el filtro
        if (jCheckBoxFiltroPorFecha.isSelected()) {
            
            // Extraer la fecha del JSpinner y convertirla a LocalDate
            Date fechaSeleccionada = (Date) jSpinnerFechaReservas.getValue();
            LocalDate fechaLocal = fechaSeleccionada.toInstant()
                                      .atZone(ZoneId.systemDefault())
                                      .toLocalDate();
            
            // Mandamos la lógica al gestor
            List<clases_pl2.Reserva> reservasFiltradas = gestor.getReservasDesde(fechaLocal);
            
            // Actualizamos la tabla
            actualizarTablaReservas(reservasFiltradas);
            
        } else {
            //Si la casilla no está marcada, mostramos todas las reservas
            actualizarTablaReservas(gestor.getTodasReservas());
        }
    }//GEN-LAST:event_jButtonBuscarReservasActionPerformed
 /* comento el método main para que sepas exactamente que interfaz abrir, 
    public static void main(String args[]) {
        //Método Main para hacer Debug
        // La manera correcta es mediante iniciosesion.java
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new AdminInterface(new Administrador("admin@javafit.com", "1234")).setVisible(true);
            } catch (IOException ex) {
                System.getLogger(AdminInterface.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } catch (ClassNotFoundException ex) {
                System.getLogger(AdminInterface.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
    } */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BuscarBoton;
    private javax.swing.JComboBox<String> DiaComboBox;
    private javax.swing.JButton LimpiarBoton;
    private javax.swing.JButton LimpiarBoton1;
    private javax.swing.JComboBox<String> TipoComboBox;
    private javax.swing.JComboBox<String> TipoComboBoxTipoUsuario;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonBuscarReservas;
    private javax.swing.JButton jButtonBuscarSocios;
    private javax.swing.JButton jButtonEditar;
    private javax.swing.JButton jButtonEliminar;
    private javax.swing.JCheckBox jCheckBoxFiltroPorFecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelDescrpción;
    private javax.swing.JLabel jLabelImagenVisual;
    private javax.swing.JLabel jLabelUsuario;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSpinner jSpinnerFechaReservas;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTableReservas;
    private javax.swing.JTable jTableSocios;
    private javax.swing.JTextField jTextFieldNombreOCorreo;
    private javax.swing.JFormattedTextField monitorFormattedField;
    // End of variables declaration//GEN-END:variables
}
