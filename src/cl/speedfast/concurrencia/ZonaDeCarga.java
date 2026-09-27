package cl.speedfast.concurrencia;

import cl.speedfast.modelo.Pedido;

import java.util.ArrayList;
import java.util.List;

/**
 * Zona de carga desde la que los repartidores retiran los pedidos.
 *
 * Recurso compartido por los repartidores. Las operaciones que consultan y
 * modifican los pedidos en espera están sincronizadas.
 */
public class ZonaDeCarga {

    private final List<Pedido> pedidosEnEspera = new ArrayList<>();

    /**
     * Deposita un pedido en la zona de carga.
     *
     * @param pedido pedido que queda a la espera de un repartidor
     */
    public synchronized void agregarPedido(Pedido pedido) {
        pedidosEnEspera.add(pedido);
        System.out.println("Pedido #" + pedido.getIdPedido()
                + " agregado. Destino: " + pedido.getDireccionEntrega());
    }

    /**
     * Entrega el siguiente pedido en espera a un único repartidor.
     *
     * La comprobación de pedidos disponibles y el retiro forman una sola
     * operación sincronizada.
     *
     * @return el siguiente pedido en espera, o {@code null} si la zona está vacía
     */
    public synchronized Pedido retirarPedido() {
        if (pedidosEnEspera.isEmpty()) {
            return null;
        }

        return pedidosEnEspera.remove(0);
    }

    /**
     * Indica cuántos pedidos siguen esperando un repartidor.
     *
     * @return cantidad de pedidos en espera
     */
    public synchronized int pedidosEnEspera() {
        return pedidosEnEspera.size();
    }
}
