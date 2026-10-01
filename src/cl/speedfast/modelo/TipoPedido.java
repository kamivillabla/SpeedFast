package cl.speedfast.modelo;

/**
 * Tipos de pedido que ofrece SpeedFast.
 *
 * Cada valor corresponde a una subclase de {@link Pedido} y a un valor de la
 * columna {@code pedidos.tipo}.
 */
public enum TipoPedido {

    COMIDA,
    ENCOMIENDA,
    EXPRESS;

    /**
     * Crea un pedido de este tipo. La distancia y los datos propios del tipo
     * quedan con valores neutros.
     *
     * @param idPedido  identificador del pedido, o null si aún no se guarda
     * @param direccion dirección de entrega
     * @return el pedido de la subclase que corresponde al tipo
     */
    public Pedido crearPedido(String idPedido, String direccion) {
        return switch (this) {
            case COMIDA -> new PedidoComida(idPedido, direccion, 0, false);
            case ENCOMIENDA -> new PedidoEncomienda(idPedido, direccion, 0, 0, "");
            case EXPRESS -> new PedidoExpress(idPedido, direccion, 0, false);
        };
    }

    /**
     * Identifica el tipo de un pedido a partir de su subclase.
     *
     * @param pedido pedido que se desea clasificar
     * @return el tipo del pedido
     */
    public static TipoPedido de(Pedido pedido) {
        if (pedido instanceof PedidoEncomienda) {
            return ENCOMIENDA;
        }

        if (pedido instanceof PedidoExpress) {
            return EXPRESS;
        }

        return COMIDA;
    }
}
