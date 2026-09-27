package cl.speedfast.vista;

import cl.speedfast.dao.PedidoDAO;
import cl.speedfast.modelo.Pedido;
import cl.speedfast.modelo.PedidoComida;
import cl.speedfast.modelo.PedidoEncomienda;
import cl.speedfast.modelo.PedidoExpress;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Formulario de registro de pedidos.
 *
 * Solicita los datos comunes a todo pedido y los propios del tipo elegido, y
 * guarda el pedido resultante en la base de datos mediante {@link PedidoDAO}. El
 * identificador lo genera la base de datos al registrarlo. Los campos
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

    private static final int ANCHO_PX = 560;
    private static final int MARGEN_PX = 18;
    private static final int SEPARACION_PX = 8;
    private static final int MARGEN_INTERNO_PX = 10;
    private static final int COLUMNAS_CAMPO = 18;
    private static final int ANCHO_ETIQUETA_TIPO_PX = 150;

    private static final int LARGO_MINIMO_DIRECCION = 5;
    private static final int LARGO_MAXIMO_DIRECCION = 120;
    private static final int LARGO_MINIMO_EMBALAJE = 3;
    private static final int LARGO_MAXIMO_EMBALAJE = 50;
    private static final double DISTANCIA_MINIMA_KM = 0.1;
    private static final double DISTANCIA_MAXIMA_KM = 100.0;
    private static final double PESO_MINIMO_KG = 0.1;
    private static final double PESO_MAXIMO_REGISTRABLE_KG = 100.0;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final Runnable alRegistrarPedido;

    private final CampoValidado campoDireccion;
    private final CampoValidado campoDistancia;
    private final CampoValidado campoPeso;
    private final CampoValidado campoEmbalaje;

    private final JComboBox<TipoPedido> cmbTipo = new JComboBox<>(TipoPedido.values());
    private final JCheckBox chkMochilaTermica = new JCheckBox("Requiere mochila termica");
    private final JCheckBox chkDisponibilidadInmediata = new JCheckBox("Disponibilidad inmediata");

    private final CardLayout camposPorTipo = new CardLayout();
    private final JPanel panelPorTipo = crearPanelDeAlturaFija(camposPorTipo);

    /**
     * Construye el formulario de registro.
     *
     * @param alRegistrarPedido acción que notifica al resto de la aplicación que
     *                          hay un pedido nuevo
     */
    public VentanaRegistroPedido(Runnable alRegistrarPedido) {
        this.alRegistrarPedido = alRegistrarPedido;

        campoDireccion = new CampoValidado("Direccion de entrega:", COLUMNAS_CAMPO, Validaciones.todas(
                Validaciones.obligatorio("Ingresa la direccion de entrega."),
                Validaciones.longitudEntre(LARGO_MINIMO_DIRECCION, LARGO_MAXIMO_DIRECCION),
                Validaciones.contieneLetras("La direccion debe incluir el nombre de la calle.")));

        campoDistancia = new CampoValidado("Distancia (km):", COLUMNAS_CAMPO, Validaciones.todas(
                Validaciones.obligatorio("Ingresa la distancia hasta el destino."),
                Validaciones.numeroEntre("La distancia", DISTANCIA_MINIMA_KM, DISTANCIA_MAXIMA_KM)));

        campoPeso = new CampoValidado("Peso (kg):", COLUMNAS_CAMPO, Validaciones.todas(
                Validaciones.obligatorio("Ingresa el peso de la encomienda."),
                Validaciones.numeroEntre("El peso", PESO_MINIMO_KG, PESO_MAXIMO_REGISTRABLE_KG)));
        campoPeso.definirAviso(this::advertirSobrepeso);

        campoEmbalaje = new CampoValidado("Embalaje:", COLUMNAS_CAMPO, Validaciones.todas(
                Validaciones.obligatorio("Indica el embalaje de la encomienda."),
                Validaciones.longitudEntre(LARGO_MINIMO_EMBALAJE, LARGO_MAXIMO_EMBALAJE),
                Validaciones.contieneLetras("Describe el embalaje con palabras. Ejemplo: caja de carton.")));

        setTitle("Registrar pedido");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(crearFormulario(), BorderLayout.NORTH);
        add(crearPanelDeBotones(), BorderLayout.SOUTH);

        cmbTipo.addActionListener(e -> mostrarCamposDelTipo());

        pack();
        setSize(ANCHO_PX, getHeight());
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Arma el formulario apilando las filas de arriba hacia abajo.
     *
     * El contenido se ancla al borde superior: si ocupara el centro, las filas se
     * repartirían el espacio sobrante y cada mensaje de error se despegaría del
     * campo al que pertenece.
     */
    private JPanel crearFormulario() {
        panelPorTipo.add(crearPanelComida(), TipoPedido.COMIDA.name());
        panelPorTipo.add(crearPanelEncomienda(), TipoPedido.ENCOMIENDA.name());
        panelPorTipo.add(crearPanelExpress(), TipoPedido.EXPRESS.name());

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(campoDireccion.getFila());
        contenido.add(campoDistancia.getFila());
        contenido.add(crearFilaTipo());
        contenido.add(Box.createVerticalStrut(SEPARACION_PX));
        contenido.add(panelPorTipo);

        JPanel formulario = new JPanel(new BorderLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(MARGEN_PX, MARGEN_PX, SEPARACION_PX, MARGEN_PX));
        formulario.add(contenido, BorderLayout.NORTH);

        return formulario;
    }

    private JPanel crearFilaTipo() {
        JLabel titulo = new JLabel("Tipo de pedido:");
        titulo.setPreferredSize(new Dimension(ANCHO_ETIQUETA_TIPO_PX, cmbTipo.getPreferredSize().height));

        JPanel fila = crearPanelDeAlturaFija(new BorderLayout());
        fila.add(titulo, BorderLayout.WEST);
        fila.add(cmbTipo, BorderLayout.CENTER);

        return fila;
    }

    /**
     * Crea un panel que no crece más allá de lo que su contenido necesita.
     *
     * Dentro de una pila vertical, un panel sin este límite se reparte el espacio
     * libre y separa sus componentes entre sí.
     *
     * @param layout disposición interna del panel
     * @return el panel, alineado a la izquierda de la pila
     */
    private static JPanel crearPanelDeAlturaFija(LayoutManager layout) {
        JPanel panel = new JPanel(layout) {

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    private JPanel crearPanelComida() {
        JPanel panel = crearPanelDeTipo("Datos del pedido de comida");
        chkMochilaTermica.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        chkDisponibilidadInmediata.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(chkDisponibilidadInmediata);

        return panel;
    }

    private JPanel crearPanelDeTipo(String titulo) {
        JPanel panel = crearPanelDeAlturaFija(null);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(titulo),
                BorderFactory.createEmptyBorder(MARGEN_INTERNO_PX, 0, MARGEN_INTERNO_PX, MARGEN_INTERNO_PX)));

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
     * Guarda el pedido en la base de datos cuando todos los campos exigibles son
     * válidos.
     *
     * Si alguno no lo es, el formulario destaca los campos pendientes y lleva el
     * foco al primero de ellos. Si la base de datos rechaza la operación, el
     * formulario conserva los datos ingresados e informa el motivo.
     */
    private void guardarPedido() {
        if (!formularioValido()) {
            return;
        }

        Pedido pedido = construirPedido();

        try {
            pedidoDAO.guardar(pedido);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
            return List.of(campoDireccion, campoDistancia, campoPeso, campoEmbalaje);
        }

        return List.of(campoDireccion, campoDistancia);
    }

    /**
     * Crea el pedido correspondiente al tipo seleccionado.
     *
     * El identificador queda pendiente hasta que la base de datos lo asigne. Se
     * invoca una vez que el formulario fue validado, por lo que los campos
     * numéricos ya contienen valores interpretables.
     *
     * @return el pedido construido a partir del formulario
     */
    private Pedido construirPedido() {
        String direccion = campoDireccion.getTexto();
        double distanciaKm = Validaciones.comoNumero(campoDistancia.getTexto());

        return switch (tipoSeleccionado()) {
            case COMIDA -> new PedidoComida(null, direccion, distanciaKm, chkMochilaTermica.isSelected());
            case ENCOMIENDA -> new PedidoEncomienda(null, direccion, distanciaKm,
                    Validaciones.comoNumero(campoPeso.getTexto()), campoEmbalaje.getTexto());
            case EXPRESS -> new PedidoExpress(null, direccion, distanciaKm, chkDisponibilidadInmediata.isSelected());
        };
    }

    /**
     * Advierte que una encomienda pesada no podrá llevarla un repartidor.
     *
     * El límite lo define {@link PedidoEncomienda}, que es donde vive la regla:
     * el formulario solo la consulta para anticipar el resultado al usuario, sin
     * impedir el registro.
     *
     * @param peso contenido del campo, ya validado como número
     * @return el texto de la advertencia, o null si el peso no la amerita
     */
    private String advertirSobrepeso(String peso) {
        if (peso.isEmpty() || Validaciones.comoNumero(peso) <= PedidoEncomienda.PESO_MAXIMO_KG) {
            return null;
        }

        return "Sobre " + (int) PedidoEncomienda.PESO_MAXIMO_KG
                + " kg requiere vehiculo de carga: quedara derivada a revision.";
    }

    private TipoPedido tipoSeleccionado() {
        return (TipoPedido) cmbTipo.getSelectedItem();
    }

    private void limpiarFormulario() {
        campoDireccion.limpiar();
        campoDistancia.limpiar();
        campoPeso.limpiar();
        campoEmbalaje.limpiar();
        chkMochilaTermica.setSelected(false);
        chkDisponibilidadInmediata.setSelected(false);
        cmbTipo.setSelectedItem(TipoPedido.COMIDA);
        campoDireccion.enfocar();
    }
}
