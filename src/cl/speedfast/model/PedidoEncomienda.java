package cl.speedfast.model;

/**
 * Pedido de encomienda: documentos o paquetes.
 *
 * Criterio de asignación: se valida el peso y el embalaje antes de asignar.
 */
public class PedidoEncomienda extends Pedido {

    private static final double PESO_MAXIMO_KG = 20.0;

    private double pesoKg;
    private String embalaje;

    public PedidoEncomienda(String idPedido, String direccionEntrega, double pesoKg, String embalaje) {
        super(idPedido, direccionEntrega, "Pedido Encomienda");
        this.pesoKg = pesoKg;
        this.embalaje = embalaje;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public String getEmbalaje() {
        return embalaje;
    }

    @Override
    public void asignarRepartidor() {
        mostrarEncabezado();

        if (cumpleRequisitos()) {
            System.out.println("Validando peso y embalaje: " + pesoKg + " kg en " + embalaje + "... OK");
        } else {
            System.out.println("Peso fuera de rango: " + pesoKg + " kg. Maximo permitido: " + PESO_MAXIMO_KG + " kg.");
            System.out.println("Se requiere vehiculo de carga.");
        }
    }

    /**
     * Una encomienda solo se asigna si su peso está dentro del límite permitido.
     */
    @Override
    protected boolean cumpleRequisitos() {
        return pesoKg <= PESO_MAXIMO_KG;
    }
}
