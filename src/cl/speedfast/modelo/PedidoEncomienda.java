package cl.speedfast.modelo;

/**
 * Pedido de encomienda: documentos o paquetes.
 *
 * Criterio de asignación: se valida el peso y el embalaje antes de asignar.
 * Tiempo de entrega: 20 minutos de gestión más 1,5 minutos por kilómetro,
 * ajustado a un número entero de minutos.
 */
public class PedidoEncomienda extends Pedido {

    /**
     * Peso máximo, en kilos, que un repartidor puede llevar consigo.
     *
     * Una encomienda más pesada se registra, pero no admite asignación y queda
     * derivada a revisión.
     */
    public static final double PESO_MAXIMO_KG = 20.0;

    private static final String REPARTIDOR_AUTOMATICO = "Setsuna Meiou";
    private static final int TIEMPO_BASE_MIN = 20;
    private static final double MINUTOS_POR_KM = 1.5;

    private double pesoKg;
    private String embalaje;

    public PedidoEncomienda(String idPedido, String direccionEntrega, double distanciaKm, double pesoKg, String embalaje) {
        super(idPedido, direccionEntrega, distanciaKm, "Pedido Encomienda");
        this.pesoKg = pesoKg;
        this.embalaje = embalaje;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public String getEmbalaje() {
        return embalaje;
    }

    /**
     * Agrega a la ficha el peso y el embalaje de la encomienda.
     */
    @Override
    public void mostrarResumen() {
        super.mostrarResumen();
        System.out.printf("Contenido: %.1f kg en %s%n", getPesoKg(), getEmbalaje());
    }

    /**
     * 20 minutos base más 1,5 minutos por kilómetro, redondeado a entero.
     */
    @Override
    public int calcularTiempoEntrega() {
        return (int) Math.round(TIEMPO_BASE_MIN + MINUTOS_POR_KM * getDistanciaKm());
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

        confirmarAsignacion(REPARTIDOR_AUTOMATICO);
    }

    /**
     * Una encomienda solo se asigna si su peso está dentro del límite permitido.
     */
    @Override
    protected boolean cumpleRequisitos() {
        return pesoKg <= PESO_MAXIMO_KG;
    }
}
