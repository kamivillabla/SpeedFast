package cl.speedfast.main;

import cl.speedfast.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del sistema de pedidos de SpeedFast.
 *
 * Abre la ventana principal en el hilo de despacho de eventos de Swing. Los datos
 * se leen y guardan en la base de datos {@code speedfast_db}.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
