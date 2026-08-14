package cl.speedfast.main;

import cl.speedfast.model.Pedido;
import cl.speedfast.model.PedidoComida;
import cl.speedfast.model.PedidoEncomienda;
import cl.speedfast.model.PedidoExpress;

/**
 * Prueba del sistema de asignación de repartidores de SpeedFast.
 *
 * Demuestra el polimorfismo por sobrescritura recorriendo un arreglo de tipo
 * Pedido con objetos de las tres subclases, la sobrecarga del método
 * asignarRepartidor(String), el comportamiento genérico de la clase base y los
 * casos que no superan la validación de cada tipo de pedido.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("SpeedFast - Sistema de asignacion de repartidores");
        System.out.println();

        Pedido[] pedidos = {
                new PedidoComida("PED-001", "Av. Providencia 1234, Santiago", true),
                new PedidoEncomienda("PED-002", "Los Aromos 45, Maipu", 8.5, "caja de carton"),
                new PedidoExpress("PED-003", "Pasaje El Roble 780, Nunoa", 1.2, true)
        };

        String[] repartidores = { "Usagi Tsukino", "Ami Mizuno", "Rei Hino" };

        System.out.println("1. Metodo sobrescrito asignarRepartidor()");
        System.out.println();

        for (Pedido pedido : pedidos) {
            pedido.asignarRepartidor();
            System.out.println();
        }

        System.out.println("2. Metodo sobrecargado asignarRepartidor(String)");
        System.out.println();

        for (int i = 0; i < pedidos.length; i++) {
            pedidos[i].asignarRepartidor(repartidores[i]);
            System.out.println();
        }

        System.out.println("3. Pedido generico de la clase base");
        System.out.println();

        Pedido pedidoGenerico = new Pedido("PED-004", "Ruta 5 Sur km 12, Buin", "Pedido Generico");
        pedidoGenerico.asignarRepartidor("Makoto Kino");
        System.out.println();

        System.out.println("4. Pedidos que no superan la validacion");
        System.out.println();

        Pedido encomiendaPesada = new PedidoEncomienda("PED-005", "Camino Melipilla 900, Cerrillos", 35.0, "pallet");
        encomiendaPesada.asignarRepartidor("Minako Aino");
        System.out.println();

        Pedido expressSinStock = new PedidoExpress("PED-006", "Gran Avenida 5500, La Cisterna", 4.8, false);
        expressSinStock.asignarRepartidor("Michiru Kaiou");
        System.out.println();

        System.out.println("Fin del proceso.");
    }
}
