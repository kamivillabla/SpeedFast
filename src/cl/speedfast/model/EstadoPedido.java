package cl.speedfast.model;

/**
 * Estados por los que atraviesa un pedido dentro del proceso de entrega.
 */
public enum EstadoPedido {

    /** El pedido existe pero aún no tiene repartidor. */
    PENDIENTE,

    /** El pedido tiene un repartidor confirmado y puede despacharse. */
    ASIGNADO,

    /** El pedido salió a reparto y ya no admite cancelación. */
    DESPACHADO,

    /** El pedido fue anulado antes de salir a reparto. */
    CANCELADO
}
