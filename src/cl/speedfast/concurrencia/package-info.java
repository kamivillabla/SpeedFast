/**
 * Ejecución concurrente de las entregas de SpeedFast.
 *
 * Contiene {@link cl.speedfast.concurrencia.ZonaDeCarga}, el recurso que los
 * repartidores comparten, y {@link cl.speedfast.concurrencia.Repartidor}, que
 * modela a cada repartidor como una tarea independiente. El acceso a los pedidos
 * en espera está sincronizado, de modo que cada pedido lo retira y entrega un
 * único repartidor.
 */
package cl.speedfast.concurrencia;
