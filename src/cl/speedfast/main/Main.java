package cl.speedfast.main;

import cl.speedfast.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del sistema de pedidos de SpeedFast.
 *
 * Abre la ventana principal en el hilo de despacho de eventos de Swing.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
