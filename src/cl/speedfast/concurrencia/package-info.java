/**
 * Ejecución concurrente de las entregas de SpeedFast.
 *
 * Contiene {@link cl.speedfast.concurrencia.Repartidor}, que modela a cada
 * repartidor como una tarea independiente. Cada tarea opera únicamente sobre
 * los pedidos que tiene asignados, por lo que la simulación no comparte estado
 * mutable entre hilos.
 */
package cl.speedfast.concurrencia;
