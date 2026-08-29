package cl.speedfast.main;

import cl.speedfast.gestores.ControladorDeEnvios;
import cl.speedfast.model.Pedido;
import cl.speedfast.model.PedidoComida;
import cl.speedfast.model.PedidoEncomienda;
import cl.speedfast.model.PedidoExpress;

/**
 * Punto de entrada del sistema de pedidos de SpeedFast.
 *
 * Registra un conjunto de pedidos en el controlador de envíos y ejecuta la
 * simulación completa: resumen y tiempo estimado, asignación de repartidor
 * automática y manual, despacho, cancelación e historial. Incluye pedidos que
 * no superan la validación para mostrar el comportamiento de cada tipo ante un
 * caso rechazado.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("SpeedFast - Sistema de pedidos");
        System.out.println();

        Pedido comida = new PedidoComida("PED-001", "Av. Providencia 1234, Santiago", 4.0, true);
        Pedido encomienda = new PedidoEncomienda("PED-002", "Los Aromos 45, Maipu", 6.0, 8.5, "caja de carton");
        Pedido express = new PedidoExpress("PED-003", "Pasaje El Roble 780, Nunoa", 7.0, true);
        Pedido encomiendaPesada = new PedidoEncomienda("PED-004", "Camino Melipilla 900, Cerrillos", 12.0, 35.0, "pallet");
        Pedido expressSinStock = new PedidoExpress("PED-005", "Gran Avenida 5500, La Cisterna", 4.8, false);

        Pedido[] pedidos = { comida, encomienda, express, encomiendaPesada, expressSinStock };

        ControladorDeEnvios controlador = new ControladorDeEnvios();

        for (Pedido pedido : pedidos) {
            controlador.registrar(pedido);
        }

        System.out.println("1. Resumen y tiempo estimado de entrega");
        System.out.println();

        for (Pedido pedido : pedidos) {
            pedido.mostrarResumen();
            System.out.println();
        }

        System.out.printf("%-20s %-10s %10s %12s%n", "Tipo", "Pedido", "Distancia", "Tiempo");

        for (Pedido pedido : pedidos) {
            System.out.printf("%-20s %-10s %7.1f km %8d min%n",
                    pedido.getTipoPedido(),
                    pedido.getIdPedido(),
                    pedido.getDistanciaKm(),
                    pedido.calcularTiempoEntrega());
        }

        System.out.println();

        System.out.println("2. Asignacion automatica de repartidor");
        System.out.println();

        for (Pedido pedido : pedidos) {
            pedido.asignarRepartidor();
            System.out.println();
        }

        System.out.println("3. Asignacion manual de repartidor");
        System.out.println();

        comida.asignarRepartidor("Usagi Tsukino");
        System.out.println();

        encomiendaPesada.asignarRepartidor("Minako Aino");
        System.out.println();

        System.out.println("4. Despacho de envios");
        System.out.println();

        for (Pedido pedido : pedidos) {
            controlador.despachar(pedido);
            System.out.println();
        }

        System.out.println("5. Cancelacion de envios");
        System.out.println();

        controlador.cancelar(comida);
        System.out.println();

        encomiendaPesada.cancelar("Peso fuera del limite permitido");
        System.out.println();

        controlador.cancelar(expressSinStock);
        System.out.println();

        System.out.println("6. Historial");
        System.out.println();

        controlador.verHistorial();
        System.out.println();

        System.out.println("Seguimiento de cada pedido");
        System.out.println();

        for (Pedido pedido : pedidos) {
            pedido.verHistorial();
            System.out.println();
        }

        System.out.println("Fin del proceso.");
    }
}
