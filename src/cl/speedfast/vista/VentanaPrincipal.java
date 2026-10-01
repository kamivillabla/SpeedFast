package cl.speedfast.vista;

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
import java.util.function.Supplier;

/**
 * Ventana de entrada del sistema de pedidos de SpeedFast.
 *
 * Abre la gestión de repartidores, pedidos y entregas. Cuando cambian los
 * repartidores o los pedidos, recarga la ventana de entregas para que sus combos
 * reflejen la base de datos.
 */
public class VentanaPrincipal extends JFrame {

    private static final int ANCHO_PX = 420;
    private static final int ALTO_PX = 290;
    private static final int MARGEN_PX = 20;
    private static final int SEPARACION_PX = 10;

    private VentanaRepartidores ventanaRepartidores;
    private VentanaPedidos ventanaPedidos;
    private VentanaEntregas ventanaEntregas;

    /**
     * Construye la ventana principal.
     */
    public VentanaPrincipal() {
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

        acciones.add(crearBoton("Repartidores", e -> mostrar(this::obtenerVentanaRepartidores)));
        acciones.add(crearBoton("Pedidos", e -> mostrar(this::obtenerVentanaPedidos)));
        acciones.add(crearBoton("Entregas", e -> mostrar(this::obtenerVentanaEntregas)));

        return acciones;
    }

    private JButton crearBoton(String texto, java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(0, 40));
        boton.addActionListener(accion);

        return boton;
    }

    /**
     * Muestra la ventana indicada con los datos actuales de la base de datos.
     *
     * @param ventana proveedor de la ventana que se desea mostrar
     */
    private void mostrar(Supplier<? extends VentanaGestion> ventana) {
        VentanaGestion destino = ventana.get();

        destino.refrescar();
        destino.setVisible(true);
        destino.toFront();
    }

    /**
     * Crea la ventana de repartidores en su primera apertura y la reutiliza en
     * las siguientes.
     */
    private VentanaRepartidores obtenerVentanaRepartidores() {
        if (ventanaRepartidores == null) {
            ventanaRepartidores = new VentanaRepartidores();
            ventanaRepartidores.setAlModificarDatos(this::refrescarEntregas);
        }

        return ventanaRepartidores;
    }

    /**
     * Crea la ventana de pedidos en su primera apertura y la reutiliza en las
     * siguientes.
     */
    private VentanaPedidos obtenerVentanaPedidos() {
        if (ventanaPedidos == null) {
            ventanaPedidos = new VentanaPedidos();
            ventanaPedidos.setAlModificarDatos(this::refrescarEntregas);
        }

        return ventanaPedidos;
    }

    /**
     * Crea la ventana de entregas en su primera apertura y la reutiliza en las
     * siguientes.
     */
    private VentanaEntregas obtenerVentanaEntregas() {
        if (ventanaEntregas == null) {
            ventanaEntregas = new VentanaEntregas();
        }

        return ventanaEntregas;
    }

    private void refrescarEntregas() {
        if (ventanaEntregas != null) {
            ventanaEntregas.refrescar();
        }
    }
}
