package cl.speedfast.gestores;

import cl.speedfast.interfaces.Cancelable;
import cl.speedfast.interfaces.Despachable;
import cl.speedfast.interfaces.Rastreable;
import cl.speedfast.modelo.EstadoPedido;
import cl.speedfast.modelo.Pedido;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordina las operaciones sobre los envíos de SpeedFast.
 *
 * Despacha y cancela a través de los contratos {@link Despachable} y
 * {@link Cancelable} y mantiene el registro de las entregas realizadas.
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
     * Entrega los pedidos bajo gestión del controlador.
     *
     * @return los envíos registrados, en el orden en que fueron incorporados
     */
    public List<Pedido> getEnvios() {
        return List.copyOf(envios);
    }

    /**
     * Busca un identificador ya en uso, sin distinguir mayúsculas de minúsculas.
     *
     * @param idPedido identificador que se desea comprobar
     * @return el identificador tal como quedó registrado, o null si está disponible
     */
    public String buscarIdRegistrado(String idPedido) {
        for (Pedido envio : envios) {
            if (envio.getIdPedido().equalsIgnoreCase(idPedido)) {
                return envio.getIdPedido();
            }
        }

        return null;
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
     * Imprime los pedidos ya entregados y el repartidor que se hizo cargo de cada uno.
     */
    @Override
    public void verHistorial() {
        System.out.println("Historial de entregas realizadas");

        int entregas = 0;

        for (Pedido envio : envios) {
            if (envio.getEstado() == EstadoPedido.ENTREGADO) {
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
