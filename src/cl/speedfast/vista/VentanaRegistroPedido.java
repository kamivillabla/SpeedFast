package cl.speedfast.vista;

import cl.speedfast.gestores.ControladorDeEnvios;
import cl.speedfast.model.Pedido;
import cl.speedfast.model.PedidoComida;
import cl.speedfast.model.PedidoEncomienda;
import cl.speedfast.model.PedidoExpress;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Formulario de registro de pedidos.
 *
 * Solicita los datos comunes a todo pedido y los propios del tipo elegido,
 * valida lo ingresado y entrega el pedido resultante al
 * {@link ControladorDeEnvios}. Los campos específicos de cada tipo se muestran
 * según la selección del combo, de modo que el formulario solo pide lo que el
 * pedido necesita.
 */
public class VentanaRegistroPedido extends JFrame {

    /**
     * Tipos de pedido que ofrece el formulario.
     *
     * Cada valor corresponde a una subclase de {@link Pedido} y da nombre al
     * panel de campos específicos que se muestra al seleccionarlo.
     */
    private enum TipoPedido {

        COMIDA("Comida"),
        ENCOMIENDA("Encomienda"),
        EXPRESS("Express");

        private final String etiqueta;

        TipoPedido(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        @Override
        public String toString() {
            return etiqueta;
        }
    }

    private static final int ANCHO_PX = 460;
    private static final int ALTO_PX = 340;
    private static final int MARGEN_PX = 15;
    private static final int SEPARACION_PX = 8;
    private static final int COLUMNAS_CAMPO = 18;

    private final ControladorDeEnvios controlador;
    private final Runnable alRegistrarPedido;

    private final JTextField txtId = new JTextField(COLUMNAS_CAMPO);
    private final JTextField txtDireccion = new JTextField(COLUMNAS_CAMPO);
    private final JTextField txtDistancia = new JTextField(COLUMNAS_CAMPO);
    private final JComboBox<TipoPedido> cmbTipo = new JComboBox<>(TipoPedido.values());

    private final JCheckBox chkMochilaTermica = new JCheckBox("Requiere mochila termica");
    private final JTextField txtPeso = new JTextField(COLUMNAS_CAMPO);
    private final JTextField txtEmbalaje = new JTextField(COLUMNAS_CAMPO);
    private final JCheckBox chkDisponibilidadInmediata = new JCheckBox("Disponibilidad inmediata");

    private final CardLayout camposPorTipo = new CardLayout();
    private final JPanel panelPorTipo = new JPanel(camposPorTipo);

    /**
     * Construye el formulario de registro.
     *
     * @param controlador       controlador que incorpora los pedidos creados
     * @param alRegistrarPedido acción que notifica al resto de la aplicación que
     *                          hay un pedido nuevo
     */
    public VentanaRegistroPedido(ControladorDeEnvios controlador, Runnable alRegistrarPedido) {
        this.controlador = controlador;
        this.alRegistrarPedido = alRegistrarPedido;

        setTitle("Registrar pedido");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(ANCHO_PX, ALTO_PX);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearFormulario(), BorderLayout.CENTER);
        add(crearPanelDeBotones(), BorderLayout.SOUTH);

        cmbTipo.addActionListener(e -> camposPorTipo.show(panelPorTipo, tipoSeleccionado().name()));
    }

    private JPanel crearFormulario() {
        JPanel datosComunes = new JPanel(new GridLayout(0, 2, SEPARACION_PX, SEPARACION_PX));
        datosComunes.add(new JLabel("ID del pedido:"));
        datosComunes.add(txtId);
        datosComunes.add(new JLabel("Direccion de entrega:"));
        datosComunes.add(txtDireccion);
        datosComunes.add(new JLabel("Distancia (km):"));
        datosComunes.add(txtDistancia);
        datosComunes.add(new JLabel("Tipo de pedido:"));
        datosComunes.add(cmbTipo);

        panelPorTipo.add(crearPanelComida(), TipoPedido.COMIDA.name());
        panelPorTipo.add(crearPanelEncomienda(), TipoPedido.ENCOMIENDA.name());
        panelPorTipo.add(crearPanelExpress(), TipoPedido.EXPRESS.name());

        JPanel formulario = new JPanel(new BorderLayout(0, SEPARACION_PX));
        formulario.setBorder(BorderFactory.createEmptyBorder(MARGEN_PX, MARGEN_PX, SEPARACION_PX, MARGEN_PX));
        formulario.add(datosComunes, BorderLayout.NORTH);
        formulario.add(panelPorTipo, BorderLayout.CENTER);

        return formulario;
    }

    private JPanel crearPanelComida() {
        JPanel panel = crearPanelDeTipo("Datos del pedido de comida");
        panel.add(new JLabel());
        panel.add(chkMochilaTermica);

        return panel;
    }

    private JPanel crearPanelEncomienda() {
        JPanel panel = crearPanelDeTipo("Datos de la encomienda");
        panel.add(new JLabel("Peso (kg):"));
        panel.add(txtPeso);
        panel.add(new JLabel("Embalaje:"));
        panel.add(txtEmbalaje);

        return panel;
    }

    private JPanel crearPanelExpress() {
        JPanel panel = crearPanelDeTipo("Datos del pedido express");
        panel.add(new JLabel());
        panel.add(chkDisponibilidadInmediata);

        return panel;
    }

    private JPanel crearPanelDeTipo(String titulo) {
        JPanel panel = new JPanel(new GridLayout(0, 2, SEPARACION_PX, SEPARACION_PX));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));

        return panel;
    }

    private JPanel crearPanelDeBotones() {
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarPedido());

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, SEPARACION_PX, SEPARACION_PX));
        botones.setBorder(BorderFactory.createEmptyBorder(0, MARGEN_PX, SEPARACION_PX, MARGEN_PX));
        botones.add(btnLimpiar);
        botones.add(btnCerrar);
        botones.add(btnGuardar);

        getRootPane().setDefaultButton(btnGuardar);

        return botones;
    }

    /**
     * Valida el formulario y, si los datos son correctos, incorpora el pedido al
     * controlador y deja el formulario listo para el siguiente registro.
     */
    private void guardarPedido() {
        Pedido pedido = construirPedido();

        if (pedido == null) {
            return;
        }

        controlador.registrar(pedido);
        alRegistrarPedido.run();
        limpiarFormulario();

        JOptionPane.showMessageDialog(this,
                "Pedido registrado correctamente.\n" + pedido,
                "Registro exitoso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Crea el pedido correspondiente al tipo seleccionado.
     *
     * @return el pedido construido, o null si algún dato no supera la validación
     */
    private Pedido construirPedido() {
        String id = txtId.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (id.isEmpty()) {
            advertir("Ingresa el ID del pedido.", txtId);
            return null;
        }

        if (controlador.existeIdPedido(id)) {
            advertir("Ya existe un pedido con el ID " + id + ".", txtId);
            return null;
        }

        if (direccion.isEmpty()) {
            advertir("Ingresa la direccion de entrega.", txtDireccion);
            return null;
        }

        double distanciaKm = leerNumeroPositivo(txtDistancia, "La distancia");

        if (distanciaKm <= 0) {
            return null;
        }

        return switch (tipoSeleccionado()) {
            case COMIDA -> new PedidoComida(id, direccion, distanciaKm, chkMochilaTermica.isSelected());
            case ENCOMIENDA -> construirEncomienda(id, direccion, distanciaKm);
            case EXPRESS -> new PedidoExpress(id, direccion, distanciaKm, chkDisponibilidadInmediata.isSelected());
        };
    }

    private Pedido construirEncomienda(String id, String direccion, double distanciaKm) {
        double pesoKg = leerNumeroPositivo(txtPeso, "El peso");

        if (pesoKg <= 0) {
            return null;
        }

        String embalaje = txtEmbalaje.getText().trim();

        if (embalaje.isEmpty()) {
            advertir("Indica el embalaje de la encomienda.", txtEmbalaje);
            return null;
        }

        return new PedidoEncomienda(id, direccion, distanciaKm, pesoKg, embalaje);
    }

    /**
     * Interpreta el contenido de un campo como un número mayor que cero.
     *
     * @param campo     campo del formulario que contiene el valor
     * @param concepto  nombre del dato, usado en el mensaje de advertencia
     * @return el valor ingresado, o cero si el campo no contiene un número válido
     */
    private double leerNumeroPositivo(JTextField campo, String concepto) {
        String valorIngresado = campo.getText().trim().replace(',', '.');

        try {
            double valor = Double.parseDouble(valorIngresado);

            if (valor <= 0) {
                advertir(concepto + " debe ser mayor que cero.", campo);
                return 0;
            }

            return valor;
        } catch (NumberFormatException e) {
            advertir(concepto + " debe ser un numero.", campo);
            return 0;
        }
    }

    private void advertir(String mensaje, JTextField campoADestacar) {
        JOptionPane.showMessageDialog(this, mensaje, "Datos incompletos", JOptionPane.WARNING_MESSAGE);
        campoADestacar.requestFocusInWindow();
        campoADestacar.selectAll();
    }

    private TipoPedido tipoSeleccionado() {
        return (TipoPedido) cmbTipo.getSelectedItem();
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtDireccion.setText("");
        txtDistancia.setText("");
        txtPeso.setText("");
        txtEmbalaje.setText("");
        chkMochilaTermica.setSelected(false);
        chkDisponibilidadInmediata.setSelected(false);
        cmbTipo.setSelectedItem(TipoPedido.COMIDA);
        txtId.requestFocusInWindow();
    }
}
