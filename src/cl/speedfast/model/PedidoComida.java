package cl.speedfast.model;

/**
 * Pedido de comida desde un restaurante.
 *
 * Criterio de asignación: el repartidor debe contar con mochila térmica.
 */
public class PedidoComida extends Pedido {

    private boolean requiereMochilaTermica;

    public PedidoComida(String idPedido, String direccionEntrega, boolean requiereMochilaTermica) {
        super(idPedido, direccionEntrega, "Pedido Comida");
        this.requiereMochilaTermica = requiereMochilaTermica;
    }

    public boolean isRequiereMochilaTermica() {
        return requiereMochilaTermica;
    }

    @Override
    public void asignarRepartidor() {
        mostrarEncabezado();

        if (requiereMochilaTermica) {
            System.out.println("Verificando mochila termica... OK");
        } else {
            System.out.println("El pedido no requiere mochila termica. Repartidor sin restriccion.");
        }
    }
}
