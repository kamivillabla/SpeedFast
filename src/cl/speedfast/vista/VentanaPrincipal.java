package cl.speedfast.vista;

import cl.speedfast.gestores.ControladorDeEnvios;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;

/**
 * Ventana de entrada del sistema de pedidos de SpeedFast.
 *
 * Reúne las operaciones disponibles para el usuario y abre la ventana que
 * corresponde a cada una. Todas las ventanas comparten el mismo
 * {@link ControladorDeEnvios}, de modo que los pedidos registrados quedan
 * visibles de inmediato en el listado.
 */
public class VentanaPrincipal extends JFrame {

    private static final int ANCHO_PX = 420;
    private static final int ALTO_PX = 280;
    private static final int MARGEN_PX = 20;
    private static final int SEPARACION_PX = 10;

    private final ControladorDeEnvios controlador;

    private VentanaListaPedidos ventanaListaPedidos;
    private VentanaRegistroPedido ventanaRegistroPedido;

    /**
     * Construye la ventana principal sobre el controlador indicado.
     *
     * @param controlador controlador que mantiene los pedidos del sistema
     */
    public VentanaPrincipal(ControladorDeEnvios controlador) {
        this.controlador = controlador;

        setTitle("SpeedFast - Gestion de entregas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(ANCHO_PX, ALTO_PX);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearPanelDeAcciones(), BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        JLabel titulo = new JLabel("SpeedFast", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel bajada = new JLabel("Gestion de pedidos y entregas", SwingConstants.CENTER);
        bajada.setAlignmentX(CENTER_ALIGNMENT);

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBorder(BorderFactory.createEmptyBorder(MARGEN_PX, MARGEN_PX, SEPARACION_PX, MARGEN_PX));
        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(SEPARACION_PX / 2));
        encabezado.add(bajada);

        return encabezado;
    }

    private JPanel crearPanelDeAcciones() {
        JPanel acciones = new JPanel(new GridLayout(3, 1, SEPARACION_PX, SEPARACION_PX));
        acciones.setBorder(BorderFactory.createEmptyBorder(SEPARACION_PX, MARGEN_PX, MARGEN_PX, MARGEN_PX));

        acciones.add(crearBoton("Registrar pedido", e -> mostrarRegistroPedido()));
        acciones.add(crearBoton("Listar pedidos", e -> mostrarPedidos(VentanaListaPedidos::mostrarTodos)));
        acciones.add(crearBoton("Asignar repartidor / Iniciar entrega",
                e -> mostrarPedidos(VentanaListaPedidos::mostrarPorGestionar)));

        return acciones;
    }

    private JButton crearBoton(String texto, java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(0, 40));
        boton.addActionListener(accion);

        return boton;
    }

    /**
     * Muestra el formulario de registro, reutilizando la ventana si ya está abierta.
     */
    private void mostrarRegistroPedido() {
        if (ventanaRegistroPedido == null) {
            ventanaRegistroPedido = new VentanaRegistroPedido(controlador, this::refrescarListaPedidos);
        }

        ventanaRegistroPedido.setVisible(true);
        ventanaRegistroPedido.toFront();
    }

    /**
     * Muestra el listado de pedidos con el alcance indicado.
     *
     * Consultar el inventario y gestionar las entregas ocurren sobre la misma
     * tabla, porque asignar un repartidor o despachar un pedido exige elegirlo
     * antes. Lo que distingue a una acción de la otra es qué pedidos presenta.
     *
     * @param alcance selección de pedidos que debe mostrar la ventana
     */
    private void mostrarPedidos(Consumer<VentanaListaPedidos> alcance) {
        if (ventanaListaPedidos == null) {
            ventanaListaPedidos = new VentanaListaPedidos(controlador);
        }

        alcance.accept(ventanaListaPedidos);
        ventanaListaPedidos.setVisible(true);
        ventanaListaPedidos.toFront();
    }

    private void refrescarListaPedidos() {
        if (ventanaListaPedidos != null) {
            ventanaListaPedidos.refrescar();
        }
    }
}
