package cl.speedfast.concurrencia;

import cl.speedfast.gestores.ControladorDeEnvios;
import cl.speedfast.model.EstadoPedido;
import cl.speedfast.model.Pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Repartidor que recorre su propia ruta de entregas.
 *
 * Implementa {@link Runnable}, de modo que varios repartidores pueden recorrer
 * sus rutas al mismo tiempo. Al ejecutarse despacha uno a uno los pedidos que
 * tiene asignados a través de {@link ControladorDeEnvios} y simula cada trayecto
 * con una pausa de duración aleatoria, informando su avance en consola.
 *
 * Un repartidor solo opera sobre los pedidos de su propia ruta, de modo que dos
 * repartidores nunca modifican el mismo pedido.
 */
public class Repartidor implements Runnable {

    private static final int TRAYECTO_MINIMO_MS = 500;
    private static final int TRAYECTO_MAXIMO_MS = 1500;

    private final String nombre;
    private final List<Pedido> pedidosAsignados = new ArrayList<>();
    private final ControladorDeEnvios controlador;

    /**
     * Crea un repartidor sin pedidos en su ruta.
     *
     * @param nombre      nombre con el que el repartidor se identifica en consola
     * @param controlador controlador a través del cual despacha sus entregas
     */
    public Repartidor(String nombre, ControladorDeEnvios controlador) {
        this.nombre = nombre;
        this.controlador = controlador;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Entrega la ruta del repartidor sin permitir modificarla desde fuera.
     *
     * @return pedidos que el repartidor tiene asignados
     */
    public List<Pedido> getPedidosAsignados() {
        return Collections.unmodifiableList(pedidosAsignados);
    }

    /**
     * Incorpora un pedido a la ruta del repartidor y lo deja asignado a su nombre.
     *
     * @param pedido pedido que pasa a formar parte de la ruta
     */
    public void asignar(Pedido pedido) {
        pedidosAsignados.add(pedido);
        pedido.asignarRepartidor(nombre);
    }

    /**
     * Imprime la ruta del repartidor y el tiempo estimado de cada entrega.
     */
    public void mostrarRuta() {
        System.out.println("Ruta de " + nombre + " (" + pedidosAsignados.size() + " pedidos)");

        for (Pedido pedido : pedidosAsignados) {
            System.out.println("  - " + describir(pedido)
                    + " hacia " + pedido.getDireccionEntrega()
                    + " (" + pedido.calcularTiempoEntrega() + " min estimados)");
        }
    }

    /**
     * Recorre la ruta entregando los pedidos uno tras otro.
     *
     * Los pedidos que ya no están en condiciones de salir a reparto se omiten.
     * Si el repartidor es interrumpido durante un trayecto, conserva la marca de
     * interrupción y regresa a la base sin continuar con el resto de la ruta.
     */
    @Override
    public void run() {
        informar("Inicia su jornada con " + pedidosAsignados.size() + " pedidos asignados.");

        int entregados = 0;

        for (Pedido pedido : pedidosAsignados) {

            if (pedido.getEstado() != EstadoPedido.ASIGNADO) {
                informar("Omite " + describir(pedido) + ": el pedido esta " + pedido.getEstado() + ".");
                continue;
            }

            informar("Entregando " + describir(pedido) + " en " + pedido.getDireccionEntrega() + "...");
            controlador.despachar(pedido);

            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(TRAYECTO_MINIMO_MS, TRAYECTO_MAXIMO_MS + 1));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                informar("Trayecto interrumpido en " + describir(pedido) + ". Regresa a la base.");
                return;
            }

            entregados++;
            informar("Pedido #" + pedido.getIdPedido() + " entregado.");
        }

        informar("Termina su jornada. Entregas realizadas: " + entregados + ".");
    }

    private void informar(String mensaje) {
        System.out.println("[Repartidor: " + nombre + "] " + mensaje);
    }

    private String describir(Pedido pedido) {
        return pedido.getTipoPedido() + " #" + pedido.getIdPedido();
    }
}
