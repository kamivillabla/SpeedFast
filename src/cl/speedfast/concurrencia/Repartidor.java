package cl.speedfast.concurrencia;

import cl.speedfast.modelo.EstadoPedido;
import cl.speedfast.modelo.Pedido;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Repartidor que retira pedidos de la zona de carga y los entrega.
 *
 * Implementa {@link Runnable}, de modo que varios repartidores pueden trabajar
 * al mismo tiempo sobre la misma {@link ZonaDeCarga}. Cada uno retira un pedido,
 * lo marca en reparto, simula el trayecto con una pausa de duración aleatoria y
 * lo da por entregado, repitiendo el ciclo hasta que la zona queda vacía.
 *
 * Un pedido retirado ya no está en la zona, por lo que desde ese momento un solo
 * repartidor trabaja sobre él.
 */
public class Repartidor implements Runnable {

    private static final int TRAYECTO_MINIMO_MS = 500;
    private static final int TRAYECTO_MAXIMO_MS = 1500;

    private final String nombre;
    private final ZonaDeCarga zonaDeCarga;
    private int entregasRealizadas;

    /**
     * Crea un repartidor que trabaja contra la zona de carga indicada.
     *
     * @param nombre      nombre con el que el repartidor se identifica en consola
     * @param zonaDeCarga zona de carga compartida desde la que retira sus pedidos
     */
    public Repartidor(String nombre, ZonaDeCarga zonaDeCarga) {
        this.nombre = nombre;
        this.zonaDeCarga = zonaDeCarga;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Indica cuántos pedidos alcanzó a entregar el repartidor.
     *
     * @return cantidad de entregas completadas
     */
    public int getEntregasRealizadas() {
        return entregasRealizadas;
    }

    /**
     * Retira y entrega pedidos hasta que la zona de carga queda vacía.
     *
     * Si el repartidor es interrumpido durante un trayecto, conserva la marca de
     * interrupción y termina su turno sin tomar más pedidos.
     */
    @Override
    public void run() {

        Pedido pedido = zonaDeCarga.retirarPedido();

        while (pedido != null) {

            informar("Retirando pedido #" + pedido.getIdPedido() + "...");

            pedido.setRepartidor(nombre);
            pedido.setEstado(EstadoPedido.EN_REPARTO);
            informar("Estado: " + pedido.getEstado());

            informar("Entregando pedido #" + pedido.getIdPedido()
                    + " en " + pedido.getDireccionEntrega() + "...");

            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(TRAYECTO_MINIMO_MS, TRAYECTO_MAXIMO_MS + 1));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                informar("Trayecto interrumpido con el pedido #" + pedido.getIdPedido() + ". Termina su turno.");
                return;
            }

            pedido.setEstado(EstadoPedido.ENTREGADO);
            entregasRealizadas++;
            informar("Estado: " + pedido.getEstado());

            pedido = zonaDeCarga.retirarPedido();
        }

        informar("La zona de carga esta vacia. Termina su turno con "
                + entregasRealizadas + " entregas.");
    }

    private void informar(String mensaje) {
        System.out.println("[Repartidor - " + nombre + "] " + mensaje);
    }
}
