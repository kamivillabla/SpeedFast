package cl.speedfast.vista;

import cl.speedfast.gestores.ControladorDeEnvios;
import cl.speedfast.model.Pedido;
import cl.speedfast.model.PedidoComida;
import cl.speedfast.model.PedidoEncomienda;
import cl.speedfast.model.PedidoExpress;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Formulario de registro de pedidos.
 *
 * Solicita los datos comunes a todo pedido y los propios del tipo elegido, y
 * entrega el pedido resultante al {@link ControladorDeEnvios}. Los campos
 * específicos de cada tipo se muestran según la selección del combo, de modo que
 * el formulario solo pide lo que el pedido necesita.
 *
 * Cada campo se valida mientras el usuario escribe y señala su propio error, por
 * lo que un pedido inválido nunca llega a construirse.
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

    private static final int ANCHO_PX = 520;
    private static final int ALTO_PX = 430;
    private static final int MARGEN_PX = 15;
    private static final int SEPARACION_PX = 8;
    private static final int COLUMNAS_CAMPO = 18;
    private static final int ANCHO_ETIQUETA_TIPO_PX = 150;

    private static final int LARGO_MINIMO_ID = 3;
    private static final int LARGO_MAXIMO_ID = 20;
    private static final int LARGO_MINIMO_DIRECCION = 5;
    private static final int LARGO_MAXIMO_DIRECCION = 120;
    private static final int LARGO_MINIMO_EMBALAJE = 3;
    private static final int LARGO_MAXIMO_EMBALAJE = 50;
    private static final double DISTANCIA_MINIMA_KM = 0.1;
    private static final double DISTANCIA_MAXIMA_KM = 100.0;
    private static final double PESO_MINIMO_KG = 0.1;
    private static final double PESO_MAXIMO_KG = 100.0;

    private final ControladorDeEnvios controlador;
    private final Runnable alRegistrarPedido;

    private final CampoValidado campoId;
    private final CampoValidado campoDireccion;
    private final CampoValidado campoDistancia;
    private final CampoValidado campoPeso;
    private final CampoValidado campoEmbalaje;

    private final JComboBox<TipoPedido> cmbTipo = new JComboBox<>(TipoPedido.values());
    private final JCheckBox chkMochilaTermica = new JCheckBox("Requiere mochila termica");
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

        campoId = new CampoValidado("ID del pedido:", COLUMNAS_CAMPO, Validaciones.todas(
                Validaciones.obligatorio("Ingresa el ID del pedido."),
                Validaciones.longitudEntre(LARGO_MINIMO_ID, LARGO_MAXIMO_ID),
                Validaciones.formatoDeCodigo(),
                this::validarDisponibilidadDelId));

        campoDireccion = new CampoValidado("Direccion de entrega:", COLUMNAS_CAMPO, Validaciones.todas(
                Validaciones.obligatorio("Ingresa la direccion de entrega."),
                Validaciones.longitudEntre(LARGO_MINIMO_DIRECCION, LARGO_MAXIMO_DIRECCION),
                Validaciones.contieneLetras("La direccion debe incluir el nombre de la calle.")));

        campoDistancia = new CampoValidado("Distancia (km):", COLUMNAS_CAMPO, Validaciones.todas(
                Validaciones.obligatorio("Ingresa la distancia hasta el destino."),
                Validaciones.numeroEntre("La distancia", DISTANCIA_MINIMA_KM, DISTANCIA_MAXIMA_KM)));

        campoPeso = new CampoValidado("Peso (kg):", COLUMNAS_CAMPO, Validaciones.todas(
                Validaciones.obligatorio("Ingresa el peso de la encomienda."),
                Validaciones.numeroEntre("El peso", PESO_MINIMO_KG, PESO_MAXIMO_KG)));

        campoEmbalaje = new CampoValidado("Embalaje:", COLUMNAS_CAMPO, Validaciones.todas(
                Validaciones.obligatorio("Indica el embalaje de la encomienda."),
                Validaciones.longitudEntre(LARGO_MINIMO_EMBALAJE, LARGO_MAXIMO_EMBALAJE),
                Validaciones.contieneLetras("Describe el embalaje con palabras. Ejemplo: caja de carton.")));

        setTitle("Registrar pedido");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(ANCHO_PX, ALTO_PX);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearFormulario(), BorderLayout.CENTER);
        add(crearPanelDeBotones(), BorderLayout.SOUTH);

        cmbTipo.addActionListener(e -> mostrarCamposDelTipo());
    }

    private JPanel crearFormulario() {
        JPanel datosComunes = new JPanel();
        datosComunes.setLayout(new BoxLayout(datosComunes, BoxLayout.Y_AXIS));
        datosComunes.add(campoId.getFila());
        datosComunes.add(campoDireccion.getFila());
        datosComunes.add(campoDistancia.getFila());
        datosComunes.add(crearFilaTipo());

        panelPorTipo.add(crearPanelComida(), TipoPedido.COMIDA.name());
        panelPorTipo.add(crearPanelEncomienda(), TipoPedido.ENCOMIENDA.name());
        panelPorTipo.add(crearPanelExpress(), TipoPedido.EXPRESS.name());

        JPanel formulario = new JPanel(new BorderLayout(0, SEPARACION_PX));
        formulario.setBorder(BorderFactory.createEmptyBorder(MARGEN_PX, MARGEN_PX, SEPARACION_PX, MARGEN_PX));
        formulario.add(datosComunes, BorderLayout.NORTH);
        formulario.add(panelPorTipo, BorderLayout.CENTER);

        return formulario;
    }

    private JPanel crearFilaTipo() {
        JLabel titulo = new JLabel("Tipo de pedido:");
        titulo.setPreferredSize(new Dimension(ANCHO_ETIQUETA_TIPO_PX, cmbTipo.getPreferredSize().height));

        JPanel fila = new JPanel(new BorderLayout());
        fila.add(titulo, BorderLayout.WEST);
        fila.add(cmbTipo, BorderLayout.CENTER);

        return fila;
    }

    private JPanel crearPanelComida() {
        JPanel panel = crearPanelDeTipo("Datos del pedido de comida");
        panel.add(chkMochilaTermica);

        return panel;
    }

    private JPanel crearPanelEncomienda() {
        JPanel panel = crearPanelDeTipo("Datos de la encomienda");
        panel.add(campoPeso.getFila());
        panel.add(campoEmbalaje.getFila());

        return panel;
    }

    private JPanel crearPanelExpress() {
        JPanel panel = crearPanelDeTipo("Datos del pedido express");
        panel.add(chkDisponibilidadInmediata);

        return panel;
    }

    private JPanel crearPanelDeTipo(String titulo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
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
     * Muestra los campos propios del tipo elegido y retira las advertencias de los
     * que dejan de ser exigibles.
     */
    private void mostrarCamposDelTipo() {
        camposPorTipo.show(panelPorTipo, tipoSeleccionado().name());

        if (tipoSeleccionado() != TipoPedido.ENCOMIENDA) {
            campoPeso.descartarAdvertencia();
            campoEmbalaje.descartarAdvertencia();
        }
    }

    /**
     * Registra el pedido cuando todos los campos exigibles son válidos.
     *
     * Si alguno no lo es, el formulario destaca los campos pendientes y lleva el
     * foco al primero de ellos.
     */
    private void guardarPedido() {
        if (!formularioValido()) {
            return;
        }

        Pedido pedido = construirPedido();

        controlador.registrar(pedido);
        alRegistrarPedido.run();
        limpiarFormulario();

        JOptionPane.showMessageDialog(this,
                "Pedido registrado correctamente.\n" + pedido,
                "Registro exitoso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Comprueba todos los campos exigibles y destaca los que estén pendientes.
     *
     * @return true si el formulario puede dar origen a un pedido
     */
    private boolean formularioValido() {
        CampoValidado primerCampoInvalido = null;

        for (CampoValidado campo : camposExigibles()) {
            if (!campo.validar() && primerCampoInvalido == null) {
                primerCampoInvalido = campo;
            }
        }

        if (primerCampoInvalido == null) {
            return true;
        }

        primerCampoInvalido.enfocar();

        return false;
    }

    /**
     * Entrega los campos que deben completarse para el tipo de pedido elegido.
     *
     * @return los campos exigibles en el orden en que aparecen en el formulario
     */
    private List<CampoValidado> camposExigibles() {
        if (tipoSeleccionado() == TipoPedido.ENCOMIENDA) {
            return List.of(campoId, campoDireccion, campoDistancia, campoPeso, campoEmbalaje);
        }

        return List.of(campoId, campoDireccion, campoDistancia);
    }

    /**
     * Crea el pedido correspondiente al tipo seleccionado.
     *
     * Se invoca una vez que el formulario fue validado, por lo que los campos
     * numéricos ya contienen valores interpretables.
     *
     * @return el pedido construido a partir del formulario
     */
    private Pedido construirPedido() {
        String id = campoId.getTexto();
        String direccion = campoDireccion.getTexto();
        double distanciaKm = Validaciones.comoNumero(campoDistancia.getTexto());

        return switch (tipoSeleccionado()) {
            case COMIDA -> new PedidoComida(id, direccion, distanciaKm, chkMochilaTermica.isSelected());
            case ENCOMIENDA -> new PedidoEncomienda(id, direccion, distanciaKm,
                    Validaciones.comoNumero(campoPeso.getTexto()), campoEmbalaje.getTexto());
            case EXPRESS -> new PedidoExpress(id, direccion, distanciaKm, chkDisponibilidadInmediata.isSelected());
        };
    }

    /**
     * Comprueba que el identificador no esté tomado por otro pedido.
     *
     * @param id contenido del campo
     * @return el motivo del rechazo, o null si el identificador está disponible
     */
    private String validarDisponibilidadDelId(String id) {
        if (controlador.existeIdPedido(id)) {
            return "Ya existe un pedido con el ID " + id + ".";
        }

        return null;
    }

    private TipoPedido tipoSeleccionado() {
        return (TipoPedido) cmbTipo.getSelectedItem();
    }

    private void limpiarFormulario() {
        campoId.limpiar();
        campoDireccion.limpiar();
        campoDistancia.limpiar();
        campoPeso.limpiar();
        campoEmbalaje.limpiar();
        chkMochilaTermica.setSelected(false);
        chkDisponibilidadInmediata.setSelected(false);
        cmbTipo.setSelectedItem(TipoPedido.COMIDA);
        campoId.enfocar();
    }
}
