package cl.speedfast.main;

import cl.speedfast.concurrencia.Repartidor;
import cl.speedfast.concurrencia.ZonaDeCarga;
import cl.speedfast.gestores.ControladorDeEnvios;
import cl.speedfast.model.EstadoPedido;
import cl.speedfast.model.Pedido;
import cl.speedfast.model.PedidoComida;
import cl.speedfast.model.PedidoEncomienda;
import cl.speedfast.model.PedidoExpress;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Punto de entrada del sistema de pedidos de SpeedFast.
 *
 * Simula una jornada de reparto en la que los pedidos llegan a una zona de carga
 * común y tres repartidores los retiran de forma concurrente. Deposita los
 * pedidos, ejecuta a los repartidores, espera a que terminen y comprueba que
 * cada pedido haya sido entregado una sola vez antes de cerrar la jornada.
 *
 * Los tres repartidores comparten la misma instancia de {@link ZonaDeCarga}: ese
 * es el recurso sobre el que se aplica la sincronización.
 */
public class Main {

    private static final int ESPERA_MAXIMA_MINUTOS = 1;

    public static void main(String[] args) {

        System.out.println("SpeedFast - Coordinacion de entregas desde la zona de carga");
        System.out.println();

        System.out.println("1. Zona de carga inicializada");
        System.out.println();

        ZonaDeCarga zonaDeCarga = new ZonaDeCarga();
        ControladorDeEnvios controlador = new ControladorDeEnvios();

        List<Pedido> pedidos = List.of(
                new PedidoComida("PED-001", "Av. Providencia 1234, Santiago", 4.0, true),
                new PedidoExpress("PED-002", "Gran Avenida 5500, La Cisterna", 3.2, true),
                new PedidoEncomienda("PED-003", "Los Aromos 45, Maipu", 6.0, 8.5, "caja de carton"),
                new PedidoComida("PED-004", "Irarrazaval 2020, Nunoa", 2.5, true),
                new PedidoExpress("PED-005", "Pasaje El Roble 780, Nunoa", 7.0, true),
                new PedidoEncomienda("PED-006", "Camino Melipilla 900, Cerrillos", 9.0, 12.0, "caja reforzada"),
                new PedidoComida("PED-007", "Vicuna Mackenna 3400, San Joaquin", 5.5, true));

        for (Pedido pedido : pedidos) {
            controlador.registrar(pedido);
            zonaDeCarga.agregarPedido(pedido);
        }

        System.out.println();
        System.out.println("Pedidos en espera: " + zonaDeCarga.pedidosEnEspera());
        System.out.println();

        System.out.println("2. Retiro y entrega concurrente");
        System.out.println();

        List<Repartidor> repartidores = List.of(
                new Repartidor("Usagi Tsukino", zonaDeCarga),
                new Repartidor("Makoto Kino", zonaDeCarga),
                new Repartidor("Minako Aino", zonaDeCarga));

        ExecutorService executor = Executors.newFixedThreadPool(repartidores.size());

        for (Repartidor repartidor : repartidores) {
            executor.submit(repartidor);
        }

        executor.shutdown();

        try {

            if (!executor.awaitTermination(ESPERA_MAXIMA_MINUTOS, TimeUnit.MINUTES)) {
                executor.shutdownNow();
                System.out.println("La jornada supero el tiempo maximo previsto. Se ordena el regreso a la base.");
            }

        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("La simulacion fue interrumpida. Se ordena el regreso a la base.");
        }

        System.out.println();
        System.out.println("3. Cierre de la jornada");
        System.out.println();

        System.out.println("Pedidos en espera: " + zonaDeCarga.pedidosEnEspera());
        System.out.println();

        System.out.println("Estado final de los pedidos");

        for (Pedido pedido : pedidos) {
            System.out.println("- " + pedido);
        }

        System.out.println();

        controlador.verHistorial();
        System.out.println();

        System.out.printf("%-16s %s%n", "Repartidor", "Entregas");

        int entregasRepartidores = 0;

        for (Repartidor repartidor : repartidores) {
            System.out.printf("%-16s %8d%n", repartidor.getNombre(), repartidor.getEntregasRealizadas());
            entregasRepartidores += repartidor.getEntregasRealizadas();
        }

        System.out.println();

        informarResultado(pedidos, zonaDeCarga, entregasRepartidores);
    }

    /**
     * Comprueba que la jornada haya terminado de forma consistente.
     *
     * La simulación es correcta cuando la zona de carga quedó vacía, todos los
     * pedidos están entregados y el total de entregas informado por los
     * repartidores coincide con la cantidad de pedidos. Si alguna de esas
     * condiciones falla, se informa el problema en lugar de darla por exitosa.
     *
     * @param pedidos               pedidos que participaron en la jornada
     * @param zonaDeCarga           zona de carga utilizada durante la simulación
     * @param entregasRepartidores  total de entregas informado por los repartidores
     */
    private static void informarResultado(List<Pedido> pedidos, ZonaDeCarga zonaDeCarga, int entregasRepartidores) {

        int entregados = 0;

        for (Pedido pedido : pedidos) {
            if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
                entregados++;
            } else {
                System.out.println("Pedido sin entregar: " + pedido);
            }
        }

        boolean zonaVacia = zonaDeCarga.pedidosEnEspera() == 0;
        boolean totalesCoinciden = entregados == pedidos.size() && entregasRepartidores == pedidos.size();

        if (zonaVacia && totalesCoinciden) {
            System.out.println("[Zona de carga vacia]");
            System.out.println("Todos los pedidos han sido entregados correctamente.");
            return;
        }

        System.out.println("La jornada termino de forma inconsistente.");
        System.out.println("Pedidos entregados: " + entregados + " de " + pedidos.size());
        System.out.println("Entregas informadas por los repartidores: " + entregasRepartidores);
        System.out.println("Pedidos aun en la zona de carga: " + zonaDeCarga.pedidosEnEspera());
    }
}
