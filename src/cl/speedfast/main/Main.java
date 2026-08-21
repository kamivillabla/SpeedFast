package cl.speedfast.main;

import cl.speedfast.model.Pedido;
import cl.speedfast.model.PedidoComida;
import cl.speedfast.model.PedidoEncomienda;
import cl.speedfast.model.PedidoExpress;

/**
 * Punto de entrada del sistema de pedidos de SpeedFast.
 *
 * Crea un objeto de cada tipo de pedido, imprime su resumen y su tiempo estimado
 * de entrega, y ejecuta la asignación de repartidor sobre toda la jerarquía,
 * incluidos los pedidos que no superan la validación.
 *
 * Los objetos se declaran con el tipo Pedido y se crean a partir de sus clases
 * derivadas, ya que Pedido es abstracta.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("SpeedFast - Sistema de pedidos");
        System.out.println();

        Pedido[] pedidos = {
                new PedidoComida("PED-001", "Av. Providencia 1234, Santiago", 4.0, true),
                new PedidoEncomienda("PED-002", "Los Aromos 45, Maipu", 6.0, 8.5, "caja de carton"),
                new PedidoExpress("PED-003", "Pasaje El Roble 780, Nunoa", 7.0, true)
        };

        String[] repartidores = { "Usagi Tsukino", "Ami Mizuno", "Rei Hino" };

        System.out.println("1. Resumen y tiempo estimado de entrega");
        System.out.println();

        for (Pedido pedido : pedidos) {
            pedido.mostrarResumen();
            System.out.println("Tiempo estimado de entrega: " + pedido.calcularTiempoEntrega() + " minutos");
            System.out.println();
        }

        System.out.println("Comparativa de tiempos estimados");
        System.out.println();
        System.out.printf("%-20s %-10s %10s %12s%n", "Tipo", "Pedido", "Distancia", "Tiempo");

        for (Pedido pedido : pedidos) {
            System.out.printf("%-20s %-10s %7.1f km %8d min%n",
                    pedido.getTipoPedido(),
                    pedido.getIdPedido(),
                    pedido.getDistanciaKm(),
                    pedido.calcularTiempoEntrega());
        }

        System.out.println();

        System.out.println("2. Metodo sobrescrito asignarRepartidor()");
        System.out.println();

        for (Pedido pedido : pedidos) {
            pedido.asignarRepartidor();
            System.out.println();
        }

        System.out.println("3. Metodo sobrecargado asignarRepartidor(String)");
        System.out.println();

        for (int i = 0; i < pedidos.length; i++) {
            pedidos[i].asignarRepartidor(repartidores[i]);
            System.out.println();
        }

        System.out.println("4. Pedidos que no superan la validacion");
        System.out.println();

        Pedido encomiendaPesada = new PedidoEncomienda("PED-004", "Camino Melipilla 900, Cerrillos", 12.0, 35.0, "pallet");
        encomiendaPesada.asignarRepartidor("Minako Aino");
        System.out.println();

        Pedido expressSinStock = new PedidoExpress("PED-005", "Gran Avenida 5500, La Cisterna", 4.8, false);
        expressSinStock.asignarRepartidor("Michiru Kaiou");
        System.out.println();

        System.out.println("Fin del proceso.");
    }
}
