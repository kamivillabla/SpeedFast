package cl.speedfast.vista;

import cl.speedfast.dao.RepartidorDAO;
import cl.speedfast.modelo.Repartidor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;

/**
 * Formulario de registro de repartidores.
 *
 * Solicita el nombre del repartidor y lo guarda mediante {@link RepartidorDAO}.
 */
public class VentanaRegistroRepartidor extends JFrame {

    private static final int ANCHO_PX = 480;
    private static final int MARGEN_PX = 18;
    private static final int SEPARACION_PX = 8;
    private static final int COLUMNAS_CAMPO = 18;

    private static final int LARGO_MINIMO_NOMBRE = 3;
    private static final int LARGO_MAXIMO_NOMBRE = 100;

    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private final CampoValidado campoNombre = new CampoValidado("Nombre:", COLUMNAS_CAMPO, Validaciones.todas(
            Validaciones.obligatorio("Ingresa el nombre del repartidor."),
            Validaciones.longitudEntre(LARGO_MINIMO_NOMBRE, LARGO_MAXIMO_NOMBRE),
            Validaciones.contieneLetras("El nombre debe incluir letras.")));

    /**
     * Construye el formulario de registro de repartidores.
     */
    public VentanaRegistroRepartidor() {
        setTitle("Registrar repartidor");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formulario = new JPanel(new BorderLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(MARGEN_PX, MARGEN_PX, SEPARACION_PX, MARGEN_PX));
        formulario.add(campoNombre.getFila(), BorderLayout.NORTH);

        add(formulario, BorderLayout.NORTH);
        add(crearPanelDeBotones(), BorderLayout.SOUTH);

        pack();
        setSize(ANCHO_PX, getHeight());
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelDeBotones() {
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarRepartidor());

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
     * Guarda el repartidor cuando el nombre es válido. Si la base de datos rechaza
     * la operación, informa el motivo y conserva el nombre.
     */
    private void guardarRepartidor() {
        if (!campoNombre.validar()) {
            campoNombre.enfocar();
            return;
        }

        Repartidor repartidor = new Repartidor(campoNombre.getTexto());

        try {
            repartidorDAO.guardar(repartidor);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        limpiarFormulario();

        JOptionPane.showMessageDialog(this,
                "Repartidor registrado correctamente.\n" + repartidor,
                "Registro exitoso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarFormulario() {
        campoNombre.limpiar();
        campoNombre.enfocar();
    }
}
