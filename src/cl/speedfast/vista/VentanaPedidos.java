package cl.speedfast.vista;

import cl.speedfast.dao.PedidoDAO;
import cl.speedfast.modelo.EstadoPedido;
import cl.speedfast.modelo.Pedido;
import cl.speedfast.modelo.TipoPedido;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.sql.SQLException;
import java.util.List;

/**
 * Gestión de pedidos: registro, edición, eliminación y listado mediante
 * {@link PedidoDAO}.
 *
 * El formulario solicita la dirección, el tipo y el estado del pedido.
 */
public class VentanaPedidos extends VentanaGestion {

    /** Estados que admite la columna {@code pedidos.estado}. */
    private static final EstadoPedido[] ESTADOS = {
            EstadoPedido.PENDIENTE, EstadoPedido.EN_REPARTO, EstadoPedido.ENTREGADO};

    private static final String[] COLUMNAS = {"ID", "Direccion", "Tipo", "Estado"};
    private static final int COLUMNAS_CAMPO = 18;
    private static final int COLUMNA_DIRECCION = 1;
    private static final int COLUMNA_TIPO = 2;
    private static final int COLUMNA_ESTADO = 3;
    private static final int LARGO_MINIMO_DIRECCION = 5;
    private static final int LARGO_MAXIMO_DIRECCION = 100;

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private final CampoValidado campoDireccion = new CampoValidado("Direccion:", COLUMNAS_CAMPO, Validaciones.todas(
            Validaciones.obligatorio("Ingresa la direccion de entrega."),
            Validaciones.longitudEntre(LARGO_MINIMO_DIRECCION, LARGO_MAXIMO_DIRECCION),
            Validaciones.contieneLetras("La direccion debe incluir el nombre de la calle.")));

    private final JComboBox<TipoPedido> cmbTipo = new JComboBox<>(TipoPedido.values());
    private final JComboBox<EstadoPedido> cmbEstado = new JComboBox<>(ESTADOS);

    /**
     * Construye la ventana. Los datos se cargan con {@link #refrescar()}.
     */
    public VentanaPedidos() {
        super("Pedidos", "Pedido", COLUMNAS);

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.add(campoDireccion.getFila());
        formulario.add(crearFila("Tipo:", cmbTipo));
        formulario.add(crearFila("Estado:", cmbEstado));

        construir(formulario);
    }

    @Override
    void refrescar() {
        List<Pedido> pedidos;

        try {
            pedidos = pedidoDAO.readAll();
        } catch (SQLException e) {
            informarError(e);
            return;
        }

        modeloTabla.setRowCount(0);

        for (Pedido pedido : pedidos) {
            modeloTabla.addRow(new Object[]{
                    Integer.valueOf(pedido.getIdPedido()),
                    pedido.getDireccionEntrega(),
                    TipoPedido.de(pedido),
                    pedido.getEstado()});
        }
    }

    @Override
    protected boolean formularioValido() {
        if (campoDireccion.validar()) {
            return true;
        }

        campoDireccion.enfocar();

        return false;
    }

    @Override
    protected String insertar() throws SQLException {
        Pedido pedido = construirPedido(null);
        pedidoDAO.create(pedido);

        return pedido.getIdPedido() + " - " + pedido.getDireccionEntrega();
    }

    @Override
    protected boolean actualizar(int id) throws SQLException {
        return pedidoDAO.update(construirPedido(String.valueOf(id)));
    }

    @Override
    protected boolean eliminar(int id) throws SQLException {
        return pedidoDAO.delete(id);
    }

    @Override
    protected void mostrarSeleccion(int fila) {
        campoDireccion.setTexto(modeloTabla.getValueAt(fila, COLUMNA_DIRECCION).toString());
        cmbTipo.setSelectedItem(modeloTabla.getValueAt(fila, COLUMNA_TIPO));
        cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, COLUMNA_ESTADO));
    }

    @Override
    protected void limpiarCampos() {
        campoDireccion.limpiar();
        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        campoDireccion.enfocar();
    }

    /**
     * Crea el pedido a partir del formulario ya validado.
     *
     * @param idPedido identificador del pedido, o null si aún no se guarda
     * @return el pedido del tipo y estado seleccionados
     */
    private Pedido construirPedido(String idPedido) {
        TipoPedido tipo = (TipoPedido) cmbTipo.getSelectedItem();
        Pedido pedido = tipo.crearPedido(idPedido, campoDireccion.getTexto());
        pedido.setEstado((EstadoPedido) cmbEstado.getSelectedItem());

        return pedido;
    }
}
