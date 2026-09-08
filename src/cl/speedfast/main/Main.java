package cl.speedfast.main;

import cl.speedfast.concurrencia.Repartidor;
import cl.speedfast.gestores.ControladorDeEnvios;
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
 * Simula una jornada completa de reparto. Registra los pedidos del día y arma
 * la ruta de cada repartidor, ejecuta a los repartidores de forma concurrente y
 * cierra la jornada con el historial de entregas.
 *
 * El registro de envíos se completa antes de iniciar la ejecución concurrente y
 * cada repartidor atiende únicamente su propia ruta, de modo que ningún hilo
 * modifica datos que otro esté usando.
 */
public class Main {

    private static final int ESPERA_MAXIMA_MINUTOS = 1;

    public static void main(String[] args) {

        System.out.println("SpeedFast - Simulacion de entregas concurrentes");
        System.out.println();

        ControladorDeEnvios controlador = new ControladorDeEnvios();

        Pedido comidaProvidencia = new PedidoComida("PED-001", "Av. Providencia 1234, Santiago", 4.0, true);
        Pedido expressLaCisterna = new PedidoExpress("PED-002", "Gran Avenida 5500, La Cisterna", 3.2, true);
        Pedido encomiendaMaipu = new PedidoEncomienda("PED-003", "Los Aromos 45, Maipu", 6.0, 8.5, "caja de carton");
        Pedido comidaNunoa = new PedidoComida("PED-004", "Irarrazaval 2020, Nunoa", 2.5, true);
        Pedido expressElRoble = new PedidoExpress("PED-005", "Pasaje El Roble 780, Nunoa", 7.0, true);
        Pedido encomiendaCerrillos = new PedidoEncomienda("PED-006", "Camino Melipilla 900, Cerrillos", 9.0, 12.0, "caja reforzada");
        Pedido comidaSanJoaquin = new PedidoComida("PED-007", "Vicuna Mackenna 3400, San Joaquin", 5.5, false);

        List<Pedido> pedidos = List.of(
                comidaProvidencia,
                expressLaCisterna,
                encomiendaMaipu,
                comidaNunoa,
                expressElRoble,
                encomiendaCerrillos,
                comidaSanJoaquin);

        for (Pedido pedido : pedidos) {
            controlador.registrar(pedido);
        }

        System.out.println("1. Pedidos de la jornada");
        System.out.println();

        for (Pedido pedido : pedidos) {
            pedido.mostrarResumen();
            System.out.println();
        }

        System.out.println("2. Asignacion de repartidores");
        System.out.println();

        Repartidor usagi = new Repartidor("Usagi Tsukino", controlador);
        usagi.asignar(comidaProvidencia);
        usagi.asignar(expressLaCisterna);
        System.out.println();

        Repartidor makoto = new Repartidor("Makoto Kino", controlador);
        makoto.asignar(encomiendaMaipu);
        makoto.asignar(comidaNunoa);
        makoto.asignar(comidaSanJoaquin);
        System.out.println();

        Repartidor minako = new Repartidor("Minako Aino", controlador);
        minako.asignar(expressElRoble);
        minako.asignar(encomiendaCerrillos);
        System.out.println();

        List<Repartidor> repartidores = List.of(usagi, makoto, minako);

        for (Repartidor repartidor : repartidores) {
            repartidor.mostrarRuta();
            System.out.println();
        }

        System.out.println("3. Cambios antes de salir a reparto");
        System.out.println();

        System.out.println("El cliente del pedido PED-007 anula su compra.");
        controlador.cancelar(comidaSanJoaquin);
        System.out.println();

        System.out.println("4. Salida a reparto");
        System.out.println();

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
        System.out.println("Todos los repartidores terminaron su ruta.");
        System.out.println();

        System.out.println("5. Cierre de la jornada");
        System.out.println();

        controlador.verHistorial();
        System.out.println();

        for (Pedido pedido : pedidos) {
            pedido.verHistorial();
            System.out.println();
        }

        System.out.println("Fin de la jornada.");
    }
}
