package cl.speedfast.vista;

import cl.speedfast.gestores.ControladorDeEnvios;
import cl.speedfast.model.EstadoPedido;
import cl.speedfast.model.Pedido;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Listado de los pedidos registrados en el sistema.
 *
 * Muestra en una tabla los datos vigentes de cada pedido y permite asignarle un
 * repartidor e iniciar su entrega. Ambas operaciones exigen elegir antes un
 * pedido, por lo que se resuelven sobre la fila seleccionada.
 */
public class VentanaListaPedidos extends JFrame {

    private static final String[] COLUMNAS = {
            "ID", "Tipo", "Direccion", "Distancia (km)", "Repartidor", "Tiempo estimado (min)", "Estado"};

    private static final String SIN_REPARTIDOR = "Sin asignar";

    private static final int ANCHO_PX = 820;
    private static final int ALTO_PX = 360;
    private static final int MARGEN_PX = 15;
    private static final int SEPARACION_PX = 8;
    private static final int COLUMNA_ID = 0;

    private final ControladorDeEnvios controlador;

    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tblPedidos = new JTable(modeloTabla);

    /**
     * Construye el listado sobre el controlador indicado.
     *
     * @param controlador controlador que mantiene los pedidos del sistema
     */
    public VentanaListaPedidos(ControladorDeEnvios controlador) {
        this.controlador = controlador;

        setTitle("Pedidos registrados");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(ANCHO_PX, ALTO_PX);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tblPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPedidos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblPedidos.setRowHeight(24);

        JScrollPane contenedorTabla = new JScrollPane(tblPedidos);
        contenedorTabla.setBorder(BorderFactory.createEmptyBorder(MARGEN_PX, MARGEN_PX, SEPARACION_PX, MARGEN_PX));

        add(contenedorTabla, BorderLayout.CENTER);
        add(crearPanelDeBotones(), BorderLayout.SOUTH);

        refrescar();
    }

    /**
     * Vuelve a cargar la tabla con los datos vigentes de cada pedido.
     *
     * Conserva la fila seleccionada para que las operaciones sucesivas sobre un
     * mismo pedido no obliguen a elegirlo de nuevo.
     */
    public void refrescar() {
        int filaSeleccionada = tblPedidos.getSelectedRow();

        modeloTabla.setRowCount(0);

        for (Pedido pedido : controlador.getEnvios()) {
            modeloTabla.addRow(new Object[]{
                    pedido.getIdPedido(),
                    pedido.getTipoPedido(),
                    pedido.getDireccionEntrega(),
                    String.format("%.1f", pedido.getDistanciaKm()),
                    pedido.getRepartidor() == null ? SIN_REPARTIDOR : pedido.getRepartidor(),
                    pedido.calcularTiempoEntrega(),
                    pedido.getEstado()});
        }

        if (filaSeleccionada >= 0 && filaSeleccionada < modeloTabla.getRowCount()) {
            tblPedidos.setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
        }
    }

    private JPanel crearPanelDeBotones() {
        JButton btnAsignar = new JButton("Asignar repartidor");
        btnAsignar.addActionListener(e -> asignarRepartidor());

        JButton btnIniciarEntrega = new JButton("Iniciar entrega");
        btnIniciarEntrega.addActionListener(e -> iniciarEntrega());

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> refrescar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, SEPARACION_PX, SEPARACION_PX));
        botones.setBorder(BorderFactory.createEmptyBorder(0, MARGEN_PX, SEPARACION_PX, MARGEN_PX));
        botones.add(btnActualizar);
        botones.add(btnAsignar);
        botones.add(btnIniciarEntrega);

        return botones;
    }

    /**
     * Asigna un repartidor al pedido seleccionado.
     *
     * Si el usuario indica un nombre, el pedido se asigna a esa persona; si deja
     * el campo vacío, cada tipo de pedido aplica su propio criterio de asignación
     * automática.
     */
    private void asignarRepartidor() {
        Pedido pedido = pedidoSeleccionado();

        if (pedido == null) {
            return;
        }

        String nombreRepartidor = JOptionPane.showInputDialog(this,
                "Nombre del repartidor.\nDejalo en blanco para asignarlo automaticamente.",
                "Asignar repartidor para " + pedido.getIdPedido(),
                JOptionPane.QUESTION_MESSAGE);

        if (nombreRepartidor == null) {
            return;
        }

        if (nombreRepartidor.isBlank()) {
            pedido.asignarRepartidor();
        } else {
            pedido.asignarRepartidor(nombreRepartidor.trim());
        }

        refrescar();
        informarResultado(pedido, EstadoPedido.ASIGNADO,
                "Pedido asignado a " + pedido.getRepartidor() + ".",
                "El pedido no cumple los requisitos de su tipo y quedo derivado a revision.");
    }

    /**
     * Despacha el pedido seleccionado hacia su dirección de entrega.
     */
    private void iniciarEntrega() {
        Pedido pedido = pedidoSeleccionado();

        if (pedido == null) {
            return;
        }

        pedido.despachar();

        refrescar();
        informarResultado(pedido, EstadoPedido.DESPACHADO,
                "Entrega iniciada. Tiempo estimado: " + pedido.calcularTiempoEntrega() + " minutos.",
                "Solo se despachan los pedidos con repartidor asignado.");
    }

    /**
     * Obtiene el pedido correspondiente a la fila seleccionada en la tabla.
     *
     * @return el pedido elegido, o null si no hay ninguna fila seleccionada
     */
    private Pedido pedidoSeleccionado() {
        int fila = tblPedidos.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un pedido de la tabla.",
                    "Ningun pedido seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String idPedido = modeloTabla.getValueAt(fila, COLUMNA_ID).toString();
        List<Pedido> envios = controlador.getEnvios();

        for (Pedido envio : envios) {
            if (envio.getIdPedido().equals(idPedido)) {
                return envio;
            }
        }

        return null;
    }

    /**
     * Informa al usuario si la operación surtió efecto.
     *
     * @param pedido           pedido sobre el que se operó
     * @param estadoEsperado   estado que alcanza el pedido cuando la operación prospera
     * @param mensajeDeExito   detalle que se muestra al alcanzar ese estado
     * @param mensajeDeRechazo detalle que se muestra cuando el pedido no lo admite
     */
    private void informarResultado(Pedido pedido, EstadoPedido estadoEsperado,
                                   String mensajeDeExito, String mensajeDeRechazo) {
        boolean operacionAceptada = pedido.getEstado() == estadoEsperado;

        JOptionPane.showMessageDialog(this,
                operacionAceptada
                        ? mensajeDeExito
                        : mensajeDeRechazo + "\nEstado actual: " + pedido.getEstado() + ".",
                pedido.getTipoPedido() + " " + pedido.getIdPedido(),
                operacionAceptada ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }
}
