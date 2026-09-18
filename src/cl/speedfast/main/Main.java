package cl.speedfast.main;

import cl.speedfast.gestores.ControladorDeEnvios;
import cl.speedfast.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del sistema de pedidos de SpeedFast.
 *
 * Crea el controlador de envíos que da servicio a toda la aplicación y abre la
 * ventana principal en el hilo de despacho de eventos de Swing.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(new ControladorDeEnvios()).setVisible(true));
    }
}
