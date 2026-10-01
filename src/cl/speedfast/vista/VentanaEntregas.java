package cl.speedfast.vista;

import cl.speedfast.dao.EntregaDAO;
import cl.speedfast.dao.PedidoDAO;
import cl.speedfast.dao.RepartidorDAO;
import cl.speedfast.modelo.Entrega;
import cl.speedfast.modelo.Pedido;
import cl.speedfast.modelo.Repartidor;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.Component;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestión de entregas: registro, edición, eliminación y listado mediante
 * {@link EntregaDAO}.
 *
 * El pedido y el repartidor se eligen en combos cargados desde la base de datos.
 * Cada combo muestra el identificador y la dirección o el nombre, y conserva el
 * objeto completo para obtener su identificador.
 */
public class VentanaEntregas extends VentanaGestion {

    private static final String[] COLUMNAS = {"ID", "Pedido", "Repartidor", "Fecha", "Hora"};
    private static final int COLUMNAS_CAMPO = 18;

    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private final DefaultComboBoxModel<Pedido> pedidos = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<Repartidor> repartidores = new DefaultComboBoxModel<>();
    private final JComboBox<Pedido> cmbPedido = new JComboBox<>(pedidos);
    private final JComboBox<Repartidor> cmbRepartidor = new JComboBox<>(repartidores);

    private final CampoValidado campoFecha = new CampoValidado("Fecha (dd-mm-aaaa):", COLUMNAS_CAMPO,
            Validaciones.todas(Validaciones.obligatorio("Ingresa la fecha de la entrega."), Validaciones.fecha()));
    private final CampoValidado campoHora = new CampoValidado("Hora (hh:mm):", COLUMNAS_CAMPO,
            Validaciones.todas(Validaciones.obligatorio("Ingresa la hora de la entrega."), Validaciones.hora()));

    /** Entregas en el mismo orden que las filas del modelo de la tabla. */
    private List<Entrega> entregas = new ArrayList<>();

    /**
     * Construye la ventana. Los datos se cargan con {@link #refrescar()}.
     */
    public VentanaEntregas() {
        super("Entregas", "Entrega", COLUMNAS);

        cmbPedido.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean seleccionado, boolean conFoco) {
                Object texto = valor instanceof Pedido pedido ? describir(pedido) : valor;

                return super.getListCellRendererComponent(lista, texto, indice, seleccionado, conFoco);
            }
        });

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.add(crearFila("Pedido:", cmbPedido));
        formulario.add(crearFila("Repartidor:", cmbRepartidor));
        formulario.add(campoFecha.getFila());
        formulario.add(campoHora.getFila());

        construir(formulario);
        limpiarCampos();
    }

    /**
     * Recarga los combos de pedidos y repartidores y la tabla de entregas. Los
     * combos mantienen el pedido y el repartidor elegidos si siguen existiendo.
     */
    @Override
    void refrescar() {
        List<Pedido> listaPedidos;
        List<Repartidor> listaRepartidores;

        try {
            listaPedidos = pedidoDAO.readAll();
            listaRepartidores = repartidorDAO.readAll();
            entregas = entregaDAO.readAll();
        } catch (SQLException e) {
            informarError(e);
            return;
        }

        Pedido pedidoElegido = pedidoSeleccionado();
        Repartidor repartidorElegido = repartidorSeleccionado();

        pedidos.removeAllElements();
        pedidos.addAll(listaPedidos);
        repartidores.removeAllElements();
        repartidores.addAll(listaRepartidores);

        seleccionar(cmbPedido, pedidoElegido == null ? null
                : buscarPedido(Integer.parseInt(pedidoElegido.getIdPedido())));
        seleccionar(cmbRepartidor, repartidorElegido == null ? null
                : buscarRepartidor(repartidorElegido.getId()));

        modeloTabla.setRowCount(0);

        for (Entrega entrega : entregas) {
            Pedido pedido = buscarPedido(entrega.getIdPedido());
            Repartidor repartidor = buscarRepartidor(entrega.getIdRepartidor());

            modeloTabla.addRow(new Object[]{
                    entrega.getId(),
                    pedido == null ? entrega.getIdPedido() : describir(pedido),
                    repartidor == null ? entrega.getIdRepartidor() : repartidor.toString(),
                    Validaciones.formatear(entrega.getFecha()),
                    Validaciones.formatear(entrega.getHora())});
        }
    }

    @Override
    protected boolean formularioValido() {
        if (cmbPedido.getSelectedItem() == null) {
            advertir("Selecciona un pedido. Si la lista esta vacia, registra primero un pedido.");
            return false;
        }

        if (cmbRepartidor.getSelectedItem() == null) {
            advertir("Selecciona un repartidor. Si la lista esta vacia, registra primero un repartidor.");
            return false;
        }

        boolean fechaValida = campoFecha.validar();
        boolean horaValida = campoHora.validar();

        if (!fechaValida) {
            campoFecha.enfocar();
        } else if (!horaValida) {
            campoHora.enfocar();
        }

        return fechaValida && horaValida;
    }

    @Override
    protected String insertar() throws SQLException {
        Entrega entrega = construirEntrega(0);
        entregaDAO.create(entrega);

        return "Entrega #" + entrega.getId() + ": pedido " + describir(pedidoSeleccionado())
                + ", repartidor " + repartidorSeleccionado() + ".";
    }

    @Override
    protected boolean actualizar(int id) throws SQLException {
        return entregaDAO.update(construirEntrega(id));
    }

    @Override
    protected boolean eliminar(int id) throws SQLException {
        return entregaDAO.delete(id);
    }

    /**
     * Carga la entrega de la fila en el formulario y selecciona en los combos su
     * pedido y su repartidor.
     */
    @Override
    protected void mostrarSeleccion(int fila) {
        Entrega entrega = entregas.get(fila);

        cmbPedido.setSelectedItem(buscarPedido(entrega.getIdPedido()));
        cmbRepartidor.setSelectedItem(buscarRepartidor(entrega.getIdRepartidor()));
        campoFecha.setTexto(Validaciones.formatear(entrega.getFecha()));
        campoHora.setTexto(Validaciones.formatear(entrega.getHora()));
    }

    /**
     * Deja el formulario con el primer pedido y repartidor de cada combo y la
     * fecha y hora actuales.
     */
    @Override
    protected void limpiarCampos() {
        seleccionar(cmbPedido, null);
        seleccionar(cmbRepartidor, null);
        campoFecha.setTexto(Validaciones.formatear(LocalDate.now()));
        campoHora.setTexto(Validaciones.formatear(LocalTime.now()));
    }

    /**
     * Crea la entrega a partir del formulario ya validado.
     *
     * @param id identificador de la entrega, o 0 si aún no se guarda
     * @return la entrega con los identificadores del pedido y repartidor elegidos
     */
    private Entrega construirEntrega(int id) {
        return new Entrega(id,
                Integer.parseInt(pedidoSeleccionado().getIdPedido()),
                repartidorSeleccionado().getId(),
                Validaciones.comoFecha(campoFecha.getTexto()),
                Validaciones.comoHora(campoHora.getTexto()));
    }

    /**
     * Selecciona el elemento indicado o, si es null, el primero del combo.
     */
    private static <T> void seleccionar(JComboBox<T> combo, T elemento) {
        if (elemento != null) {
            combo.setSelectedItem(elemento);
        } else {
            combo.setSelectedIndex(combo.getItemCount() > 0 ? 0 : -1);
        }
    }

    private Pedido pedidoSeleccionado() {
        return (Pedido) cmbPedido.getSelectedItem();
    }

    private Repartidor repartidorSeleccionado() {
        return (Repartidor) cmbRepartidor.getSelectedItem();
    }

    private Pedido buscarPedido(int id) {
        for (int i = 0; i < pedidos.getSize(); i++) {
            if (pedidos.getElementAt(i).getIdPedido().equals(String.valueOf(id))) {
                return pedidos.getElementAt(i);
            }
        }

        return null;
    }

    private Repartidor buscarRepartidor(int id) {
        for (int i = 0; i < repartidores.getSize(); i++) {
            if (repartidores.getElementAt(i).getId() == id) {
                return repartidores.getElementAt(i);
            }
        }

        return null;
    }

    /**
     * Describe el pedido con su identificador y dirección.
     */
    private static String describir(Pedido pedido) {
        return pedido.getIdPedido() + " - " + pedido.getDireccionEntrega();
    }
}
