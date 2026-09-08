package cl.speedfast.model;

/**
 * Estados por los que atraviesa un pedido dentro del proceso de entrega.
 *
 * El recorrido habitual va desde {@code PENDIENTE} hasta {@code ENTREGADO}.
 * {@code CANCELADO} es la única salida anticipada y solo se admite mientras el
 * pedido no haya salido a reparto.
 */
public enum EstadoPedido {

    /** El pedido existe pero aún no tiene repartidor. */
    PENDIENTE,

    /** El pedido tiene un repartidor confirmado y puede despacharse. */
    ASIGNADO,

    /** El pedido salió a reparto y ya no admite cancelación. */
    DESPACHADO,

    /** El pedido fue retirado de la zona de carga y va en camino a su destino. */
    EN_REPARTO,

    /** El pedido llegó a su destino. */
    ENTREGADO,

    /** El pedido fue anulado antes de salir a reparto. */
    CANCELADO
}
