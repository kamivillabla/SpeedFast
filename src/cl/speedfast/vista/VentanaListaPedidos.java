package cl.speedfast.vista;

import cl.speedfast.dao.EntregaDAO;
import cl.speedfast.dao.PedidoDAO;
import cl.speedfast.dao.RepartidorDAO;
import cl.speedfast.modelo.Entrega;
import cl.speedfast.modelo.EstadoPedido;
import cl.speedfast.modelo.Pedido;
import cl.speedfast.modelo.Repartidor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Predicate;

/**
 * Listado de los pedidos almacenados en la base de datos.
 *
 * Cada vez que se refresca, la tabla consulta los pedidos mediante
 * {@link PedidoDAO}, por lo que refleja lo que realmente existe en la base de
 * datos. Desde el listado de gestión se registra la entrega de un pedido a cargo
 * de un repartidor, lo que exige elegir antes el pedido en la tabla.
 *
 * La ventana se abre con el alcance que pida quien la invoca: el inventario
 * completo, o solo los pedidos que esperan una gestión.
 */
public class VentanaListaPedidos extends JFrame {

    /**
     * Conjunto de pedidos que la ventana presenta.
     */
    private enum Alcance {

        /** Todos los pedidos registrados, cualquiera sea su estado, en solo lectura. */
        TODOS("Pedidos registrados", pedido -> true, false),

        /** Los pedidos que aún esperan su entrega, con la acción de registrarla a mano. */
        POR_GESTIONAR("Pedidos por gestionar",
                pedido -> pedido.getEstado() == EstadoPedido.PENDIENTE
                        || pedido.getEstado() == EstadoPedido.ASIGNADO,
                true);

        private final String titulo;
        private final Predicate<Pedido> criterio;
        private final boolean admiteGestion;

        Alcance(String titulo, Predicate<Pedido> criterio, boolean admiteGestion) {
            this.titulo = titulo;
            this.criterio = criterio;
            this.admiteGestion = admiteGestion;
        }
    }

    private static final String[] COLUMNAS = {"ID", "Tipo", "Direccion", "Estado"};

    private static final int ANCHO_PX = 720;
    private static final int ALTO_PX = 360;
    private static final int MARGEN_PX = 15;
    private static final int SEPARACION_PX = 8;
    private static final int COLUMNA_ID = 0;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tblPedidos = new JTable(modeloTabla);
    private final JPanel accionesDeGestion = new JPanel(new FlowLayout(FlowLayout.RIGHT, SEPARACION_PX, 0));

    private Alcance alcance = Alcance.TODOS;

    /**
     * Construye el listado de pedidos.
     */
    public VentanaListaPedidos() {
        setTitle(alcance.titulo);
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
    }

    /**
     * Presenta el inventario completo de pedidos, solo para consulta.
     */
    public void mostrarTodos() {
        aplicar(Alcance.TODOS);
    }

    /**
     * Presenta los pedidos que esperan su entrega, junto a la acción que permite
     * registrarla.
     */
    public void mostrarPorGestionar() {
        aplicar(Alcance.POR_GESTIONAR);
    }

    /**
     * Vuelve a cargar la tabla con los pedidos almacenados en la base de datos.
     *
     * Conserva el pedido elegido, y no la posición que ocupaba, para que las
     * operaciones sucesivas sobre uno mismo no obliguen a buscarlo de nuevo
     * cuando la tabla cambia de contenido.
     */
    public void refrescar() {
        List<Pedido> pedidos;

        try {
            pedidos = pedidoDAO.listarTodos();
        } catch (SQLException e) {
            informarError(e);
            return;
        }

        String idSeleccionado = idDelPedidoSeleccionado();

        modeloTabla.setRowCount(0);

        for (Pedido pedido : pedidos) {
            if (alcance.criterio.test(pedido)) {
                modeloTabla.addRow(new Object[]{
                        pedido.getIdPedido(),
                        pedido.getTipoPedido(),
                        pedido.getDireccionEntrega(),
                        pedido.getEstado()});
            }
        }

        volverASeleccionar(idSeleccionado);
    }

    /**
     * Ajusta la ventana al alcance indicado.
     *
     * Las acciones sobre los pedidos solo se ofrecen donde corresponde operar: en
     * el listado completo la ventana es de consulta, y sus botones no aparecen.
     *
     * @param alcance selección de pedidos y permisos que rigen la ventana
     */
    private void aplicar(Alcance alcance) {
        this.alcance = alcance;

        setTitle(alcance.titulo);
        accionesDeGestion.setVisible(alcance.admiteGestion);
        refrescar();
    }

    private String idDelPedidoSeleccionado() {
        int fila = tblPedidos.getSelectedRow();

        return fila < 0 ? null : modeloTabla.getValueAt(fila, COLUMNA_ID).toString();
    }

    private void volverASeleccionar(String idPedido) {
        if (idPedido == null) {
            return;
        }

        for (int fila = 0; fila < modeloTabla.getRowCount(); fila++) {
            if (modeloTabla.getValueAt(fila, COLUMNA_ID).equals(idPedido)) {
                tblPedidos.setRowSelectionInterval(fila, fila);
                return;
            }
        }
    }

    private JPanel crearPanelDeBotones() {
        JButton btnRegistrarEntrega = new JButton("Registrar entrega");
        btnRegistrarEntrega.addActionListener(e -> registrarEntrega());

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> refrescar());

        accionesDeGestion.add(btnRegistrarEntrega);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, SEPARACION_PX, SEPARACION_PX));
        botones.setBorder(BorderFactory.createEmptyBorder(0, MARGEN_PX, SEPARACION_PX, MARGEN_PX));
        botones.add(btnActualizar);
        botones.add(accionesDeGestion);

        return botones;
    }

    /**
     * Registra la entrega del pedido seleccionado a cargo del repartidor que elija
     * el usuario.
     *
     * Los repartidores disponibles se consultan en la base de datos, y la entrega
     * queda registrada con la fecha y hora del momento.
     */
    private void registrarEntrega() {
        String idPedido = idDelPedidoSeleccionado();

        if (idPedido == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un pedido de la tabla.",
                    "Ningun pedido seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Repartidor> repartidores;

        try {
            repartidores = repartidorDAO.listarTodos();
        } catch (SQLException e) {
            informarError(e);
            return;
        }

        if (repartidores.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay repartidores registrados. Registra uno antes de asignar la entrega.",
                    "Sin repartidores",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Repartidor> cmbRepartidores = new JComboBox<>(repartidores.toArray(new Repartidor[0]));

        int opcion = JOptionPane.showConfirmDialog(this, cmbRepartidores,
                "Repartidor para el pedido #" + idPedido,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        Repartidor repartidor = (Repartidor) cmbRepartidores.getSelectedItem();
        Entrega entrega = new Entrega(Integer.parseInt(idPedido), repartidor.getId(),
                LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.SECONDS));

        try {
            entregaDAO.guardar(entrega);
        } catch (SQLException e) {
            informarError(e);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Entrega #" + entrega.getId() + " registrada: pedido #" + idPedido
                        + " a cargo de " + repartidor.getNombre() + ".",
                "Registro exitoso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void informarError(SQLException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }
}
