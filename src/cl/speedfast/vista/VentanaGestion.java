package cl.speedfast.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;

/**
 * Ventana de gestión de una entidad: formulario, tabla y botones Registrar,
 * Editar, Eliminar y Limpiar.
 *
 * Resuelve el flujo común a todas las entidades: validar antes de operar,
 * confirmar la eliminación, informar el resultado, recargar la tabla y avisar al
 * resto de la aplicación que los datos cambiaron. Cada subclase define su
 * formulario y las llamadas a su DAO.
 *
 * La primera columna de la tabla contiene el identificador del registro.
 */
abstract class VentanaGestion extends JFrame {

    private static final int ANCHO_PX = 760;
    private static final int ALTO_PX = 560;
    private static final int MARGEN_PX = 15;
    private static final int SEPARACION_PX = 8;
    private static final int SEPARACION_FILAS_PX = 6;
    private static final int ANCHO_ETIQUETA_PX = 150;
    private static final int COLUMNA_ID = 0;

    protected final DefaultTableModel modeloTabla;
    protected final JTable tabla;

    private final String entidad;
    private Runnable alModificarDatos = () -> {
    };

    /**
     * Construye la ventana con su formulario y una tabla de las columnas indicadas.
     *
     * @param titulo   título de la ventana
     * @param entidad  nombre de la entidad en singular, usado en los mensajes
     * @param columnas encabezados de la tabla, comenzando por el identificador
     */
    protected VentanaGestion(String titulo, String entidad, String[] columnas) {
        this.entidad = entidad;
        this.modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        this.tabla = new JTable(modeloTabla);

        setTitle(titulo);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(ANCHO_PX, ALTO_PX);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(24);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = filaSeleccionada();

            if (!e.getValueIsAdjusting() && fila >= 0) {
                mostrarSeleccion(fila);
            }
        });
    }

    /**
     * Ubica el formulario sobre la tabla y los botones bajo ella.
     *
     * @param formulario campos propios de la entidad
     */
    protected void construir(JPanel formulario) {
        formulario.setBorder(BorderFactory.createEmptyBorder(MARGEN_PX, MARGEN_PX, 0, MARGEN_PX));

        JScrollPane contenedorTabla = new JScrollPane(tabla);
        contenedorTabla.setBorder(BorderFactory.createEmptyBorder(SEPARACION_PX, MARGEN_PX, SEPARACION_PX, MARGEN_PX));

        add(formulario, BorderLayout.NORTH);
        add(contenedorTabla, BorderLayout.CENTER);
        add(crearPanelDeBotones(), BorderLayout.SOUTH);
    }

    /**
     * Define la acción que se ejecuta cada vez que se registra, edita o elimina
     * un registro.
     *
     * @param alModificarDatos acción que notifica el cambio al resto de la aplicación
     */
    void setAlModificarDatos(Runnable alModificarDatos) {
        this.alModificarDatos = alModificarDatos;
    }

    /**
     * Vuelve a cargar desde la base de datos la tabla y los datos que usa el
     * formulario.
     */
    abstract void refrescar();

    /**
     * Comprueba los campos del formulario y destaca los pendientes.
     *
     * @return true si los datos pueden enviarse a la base de datos
     */
    protected abstract boolean formularioValido();

    /**
     * Registra en la base de datos el contenido del formulario ya validado.
     *
     * @return descripción del registro creado
     * @throws SQLException si la base de datos rechaza la operación
     */
    protected abstract String insertar() throws SQLException;

    /**
     * Reemplaza en la base de datos el registro indicado por el contenido del
     * formulario ya validado.
     *
     * @param id identificador del registro
     * @return true si el registro existía y fue modificado
     * @throws SQLException si la base de datos rechaza la operación
     */
    protected abstract boolean actualizar(int id) throws SQLException;

    /**
     * Elimina de la base de datos el registro indicado.
     *
     * @param id identificador del registro
     * @return true si el registro existía y fue eliminado
     * @throws SQLException si la base de datos rechaza la operación
     */
    protected abstract boolean eliminar(int id) throws SQLException;

    /**
     * Carga en el formulario los datos de una fila de la tabla.
     *
     * @param fila índice de la fila en el modelo de la tabla
     */
    protected abstract void mostrarSeleccion(int fila);

    /**
     * Devuelve el formulario a su estado inicial.
     */
    protected abstract void limpiarCampos();

    /**
     * Crea una fila de formulario con una etiqueta alineada a la de los campos
     * de texto.
     *
     * @param etiqueta   nombre del dato
     * @param componente componente que recibe el dato
     * @return la fila lista para incorporarse al formulario
     */
    protected static JPanel crearFila(String etiqueta, JComponent componente) {
        JLabel titulo = new JLabel(etiqueta);
        titulo.setPreferredSize(new Dimension(ANCHO_ETIQUETA_PX, componente.getPreferredSize().height));

        JPanel fila = new JPanel(new BorderLayout()) {

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setBorder(BorderFactory.createEmptyBorder(0, 0, SEPARACION_FILAS_PX, 0));
        fila.add(titulo, BorderLayout.WEST);
        fila.add(componente, BorderLayout.CENTER);

        return fila;
    }

    /**
     * Muestra el mensaje de una operación rechazada por la base de datos.
     *
     * @param e excepción con el motivo del rechazo
     */
    protected void informarError(SQLException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra una advertencia sobre los datos del formulario.
     *
     * @param mensaje motivo de la advertencia
     */
    protected void advertir(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Datos incompletos", JOptionPane.WARNING_MESSAGE);
    }

    private JPanel crearPanelDeBotones() {
        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> registrar());

        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editar());

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, SEPARACION_PX, SEPARACION_PX));
        botones.setBorder(BorderFactory.createEmptyBorder(0, MARGEN_PX, SEPARACION_PX, MARGEN_PX));
        botones.add(btnLimpiar);
        botones.add(btnEliminar);
        botones.add(btnEditar);
        botones.add(btnRegistrar);

        return botones;
    }

    private void registrar() {
        if (!formularioValido()) {
            return;
        }

        String registro;

        try {
            registro = insertar();
        } catch (SQLException e) {
            informarError(e);
            return;
        }

        completarOperacion("Registro guardado correctamente.\n" + registro);
    }

    private void editar() {
        int id = idSeleccionado();

        if (id < 0 || !formularioValido()) {
            return;
        }

        try {
            if (!actualizar(id)) {
                completarOperacion(entidad + " #" + id + " ya no existe en la base de datos.");
                return;
            }
        } catch (SQLException e) {
            informarError(e);
            return;
        }

        completarOperacion("Cambios guardados en " + entidad.toLowerCase() + " #" + id + ".");
    }

    private void eliminarSeleccionado() {
        int id = idSeleccionado();

        if (id < 0) {
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "Eliminar " + entidad.toLowerCase() + " #" + id + "?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (!eliminar(id)) {
                completarOperacion(entidad + " #" + id + " ya no existe en la base de datos.");
                return;
            }
        } catch (SQLException e) {
            informarError(e);
            return;
        }

        completarOperacion("Registro eliminado: " + entidad.toLowerCase() + " #" + id + ".");
    }

    /**
     * Recarga la tabla, limpia el formulario, notifica el cambio e informa el
     * resultado.
     */
    private void completarOperacion(String mensaje) {
        refrescar();
        limpiarFormulario();
        alModificarDatos.run();

        JOptionPane.showMessageDialog(this, mensaje, "Operacion completada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarFormulario() {
        tabla.clearSelection();
        limpiarCampos();
    }

    /**
     * Identificador del registro seleccionado. Si no hay selección, lo advierte.
     *
     * @return el identificador, o -1 si no hay un registro seleccionado
     */
    private int idSeleccionado() {
        int fila = filaSeleccionada();

        if (fila < 0) {
            advertir("Selecciona un registro de la tabla.");
            return -1;
        }

        return (Integer) modeloTabla.getValueAt(fila, COLUMNA_ID);
    }

    /**
     * Fila seleccionada, expresada como índice del modelo aunque la tabla esté
     * ordenada por otra columna.
     */
    private int filaSeleccionada() {
        int fila = tabla.getSelectedRow();

        return fila < 0 ? -1 : tabla.convertRowIndexToModel(fila);
    }
}
