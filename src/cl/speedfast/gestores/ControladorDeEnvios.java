package cl.speedfast.gestores;

import cl.speedfast.interfaces.Cancelable;
import cl.speedfast.interfaces.Despachable;
import cl.speedfast.interfaces.Rastreable;
import cl.speedfast.model.EstadoPedido;
import cl.speedfast.model.Pedido;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordina las operaciones sobre los envíos de SpeedFast.
 *
 * Despacha y cancela a través de los contratos {@link Despachable} y
 * {@link Cancelable}, por lo que no depende del tipo concreto de pedido, y
 * mantiene el registro de las entregas realizadas.
 */
public class ControladorDeEnvios implements Rastreable {

    private final List<Pedido> envios = new ArrayList<>();

    /**
     * Incorpora un pedido a la gestión del controlador.
     *
     * @param pedido pedido que pasa a formar parte del registro de envíos
     */
    public void registrar(Pedido pedido) {
        envios.add(pedido);
    }

    /**
     * Envía a reparto el envío indicado.
     *
     * @param envio envío que debe salir a reparto
     */
    public void despachar(Despachable envio) {
        envio.despachar();
    }

    /**
     * Anula el envío indicado.
     *
     * @param envio envío que debe cancelarse
     */
    public void cancelar(Cancelable envio) {
        envio.cancelar();
    }

    /**
     * Imprime las entregas realizadas y el repartidor que se hizo cargo de cada una.
     */
    @Override
    public void verHistorial() {
        System.out.println("Historial de entregas realizadas");

        int entregas = 0;

        for (Pedido envio : envios) {
            if (envio.getEstado() == EstadoPedido.DESPACHADO) {
                System.out.println("- " + envio.getTipoPedido() + " #" + envio.getIdPedido()
                        + " - entregado por " + envio.getRepartidor());
                entregas++;
            }
        }

        if (entregas == 0) {
            System.out.println("- Sin entregas registradas.");
        }
    }
}
