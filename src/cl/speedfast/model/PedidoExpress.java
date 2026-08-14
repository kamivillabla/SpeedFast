package cl.speedfast.model;

/**
 * Compra express: supermercado o farmacia.
 *
 * Criterio de asignación: repartidor más cercano con disponibilidad inmediata.
 */
public class PedidoExpress extends Pedido {

    private double distanciaKm;
    private boolean disponibilidadInmediata;

    public PedidoExpress(String idPedido, String direccionEntrega, double distanciaKm, boolean disponibilidadInmediata) {
        super(idPedido, direccionEntrega, "Pedido Express");
        this.distanciaKm = distanciaKm;
        this.disponibilidadInmediata = disponibilidadInmediata;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public boolean isDisponibilidadInmediata() {
        return disponibilidadInmediata;
    }

    @Override
    public void asignarRepartidor() {
        mostrarEncabezado();

        if (cumpleRequisitos()) {
            System.out.println("Repartidor mas cercano encontrado a " + distanciaKm + " km, disponible de inmediato.");
        } else {
            System.out.println("Sin repartidores disponibles de inmediato. Pedido en cola de espera.");
        }
    }

    /**
     * Una compra express solo se asigna si hay disponibilidad inmediata.
     */
    @Override
    protected boolean cumpleRequisitos() {
        return disponibilidadInmediata;
    }
}
