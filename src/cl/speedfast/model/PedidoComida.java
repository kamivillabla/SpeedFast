package cl.speedfast.model;

/**
 * Pedido de comida desde un restaurante.
 *
 * Criterio de asignación: el repartidor debe contar con mochila térmica.
 * Tiempo de entrega: 15 minutos de preparación más 2 minutos por kilómetro.
 */
public class PedidoComida extends Pedido {

    private static final String REPARTIDOR_AUTOMATICO = "Makoto Kino";
    private static final int TIEMPO_BASE_MIN = 15;
    private static final double MINUTOS_POR_KM = 2.0;

    private boolean requiereMochilaTermica;

    public PedidoComida(String idPedido, String direccionEntrega, double distanciaKm, boolean requiereMochilaTermica) {
        super(idPedido, direccionEntrega, distanciaKm, "Pedido Comida");
        this.requiereMochilaTermica = requiereMochilaTermica;
    }

    public boolean isRequiereMochilaTermica() {
        return requiereMochilaTermica;
    }

    /**
     * 15 minutos base más 2 minutos por cada kilómetro de distancia.
     */
    /**
     * Agrega a la ficha si el pedido exige mochila térmica.
     */
    @Override
    public void mostrarResumen() {
        super.mostrarResumen();
        System.out.println("Mochila termica: " + (isRequiereMochilaTermica() ? "requerida" : "no requerida"));
    }

    @Override
    public int calcularTiempoEntrega() {
        return (int) Math.round(TIEMPO_BASE_MIN + MINUTOS_POR_KM * getDistanciaKm());
    }

    @Override
    public void asignarRepartidor() {
        mostrarEncabezado();

        if (requiereMochilaTermica) {
            System.out.println("Verificando mochila termica... OK");
        } else {
            System.out.println("El pedido no requiere mochila termica. Repartidor sin restriccion.");
        }

        confirmarAsignacion(REPARTIDOR_AUTOMATICO);
    }
}
