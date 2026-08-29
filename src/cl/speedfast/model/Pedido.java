package cl.speedfast.model;

import cl.speedfast.interfaces.Cancelable;
import cl.speedfast.interfaces.Despachable;
import cl.speedfast.interfaces.Rastreable;

import java.util.ArrayList;
import java.util.List;

/**
 * Pedido de SpeedFast.
 *
 * Define los datos comunes a todo pedido, el resumen, la asignación de
 * repartidor y el ciclo de despacho y cancelación. Cada operación queda
 * anotada en el historial del pedido. El cálculo del tiempo estimado de entrega
 * corresponde a cada tipo de pedido.
 */
public abstract class Pedido implements Despachable, Cancelable, Rastreable {

    private static final String MOTIVO_NO_INFORMADO = "No informado";
    private static final String REPARTIDOR_DE_TURNO = "Repartidor de turno";

    private final List<String> historial = new ArrayList<>();

    private String idPedido;
    private String direccionEntrega;
    private double distanciaKm;
    private String tipoPedido;
    private String repartidor;
    private EstadoPedido estado;

    public Pedido(String idPedido, String direccionEntrega, double distanciaKm, String tipoPedido) {
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
        this.tipoPedido = tipoPedido;
        this.estado = EstadoPedido.PENDIENTE;
        this.historial.add("Pedido registrado en el sistema.");
    }

    public String getIdPedido() {
        return idPedido;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public String getTipoPedido() {
        return tipoPedido;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    /**
     * Imprime la ficha del pedido: tipo, identificador, dirección, distancia,
     * repartidor, tiempo estimado de entrega y estado.
     */
    public void mostrarResumen() {
        System.out.println(tipoPedido + " #" + idPedido);
        System.out.println("Direccion: " + direccionEntrega);
        System.out.printf("Distancia: %.1f km%n", distanciaKm);
        System.out.println("Repartidor asignado: " + (repartidor == null ? "sin asignar" : repartidor));
        System.out.println("Tiempo estimado: " + calcularTiempoEntrega() + " minutos");
        System.out.println("Estado: " + estado);
    }

    /**
     * Tiempo estimado de entrega del pedido.
     *
     * @return tiempo estimado de entrega en minutos
     */
    public abstract int calcularTiempoEntrega();

    /**
     * Asigna automáticamente un repartidor al pedido.
     *
     * Aplica el criterio general de asignación. Cada tipo de pedido sustituye
     * este criterio por el suyo y selecciona el repartidor que le corresponde.
     */
    public void asignarRepartidor() {
        mostrarEncabezado();
        System.out.println("Aplicando criterio general de asignacion... OK");
        confirmarAsignacion(REPARTIDOR_DE_TURNO);
    }

    /**
     * Asigna manualmente el pedido al repartidor indicado.
     *
     * @param nombreRepartidor nombre del repartidor que se desea asignar
     */
    public void asignarRepartidor(String nombreRepartidor) {
        mostrarEncabezado();
        confirmarAsignacion(nombreRepartidor);
    }

    /**
     * Despacha el pedido hacia la dirección de entrega.
     *
     * Solo se despachan los pedidos que ya tienen un repartidor asignado.
     */
    @Override
    public void despachar() {
        System.out.println("Despachando " + tipoPedido + " #" + idPedido + "...");

        if (estado != EstadoPedido.ASIGNADO) {
            System.out.println("-> Pedido en estado " + estado + ". No corresponde despacharlo.");
            return;
        }

        int tiempoEstimado = calcularTiempoEntrega();
        estado = EstadoPedido.DESPACHADO;
        registrarEvento("Despachado con entrega estimada en " + tiempoEstimado + " minutos.");

        System.out.println("-> Pedido despachado correctamente. Entrega estimada en " + tiempoEstimado + " minutos.");
    }

    /**
     * Cancela el pedido sin dejar constancia de un motivo.
     */
    @Override
    public void cancelar() {
        cancelar(MOTIVO_NO_INFORMADO);
    }

    /**
     * Cancela el pedido dejando constancia del motivo.
     *
     * Un pedido ya despachado no admite cancelación.
     *
     * @param motivo razón por la que se anula el pedido
     */
    public void cancelar(String motivo) {
        System.out.println("Cancelando " + tipoPedido + " #" + idPedido + "...");

        if (estado == EstadoPedido.DESPACHADO || estado == EstadoPedido.CANCELADO) {
            System.out.println("-> Pedido en estado " + estado + ". No admite cancelacion.");
            return;
        }

        estado = EstadoPedido.CANCELADO;
        registrarEvento("Cancelado. Motivo: " + motivo + ".");

        System.out.println("-> Pedido cancelado exitosamente. Motivo: " + motivo);
    }

    /**
     * Imprime los eventos registrados por el pedido, en orden de ocurrencia.
     */
    @Override
    public void verHistorial() {
        System.out.println(tipoPedido + " #" + idPedido + " - " + estado);

        for (int i = 0; i < historial.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + historial.get(i));
        }
    }

    /**
     * Confirma la asignación cuando el pedido cumple los requisitos de su tipo.
     *
     * @param nombreRepartidor repartidor que se intenta asignar
     */
    protected void confirmarAsignacion(String nombreRepartidor) {
        if (!cumpleRequisitos()) {
            registrarEvento("Asignacion rechazada para " + nombreRepartidor + ". Pedido derivado a revision.");
            System.out.println("No es posible asignar a " + nombreRepartidor + ". Pedido derivado a revision.");
            return;
        }

        repartidor = nombreRepartidor;
        estado = EstadoPedido.ASIGNADO;
        registrarEvento("Repartidor asignado: " + nombreRepartidor + ".");
        System.out.println("Repartidor asignado: " + nombreRepartidor);
    }

    /**
     * Indica si el pedido cumple las condiciones para ser asignado.
     *
     * @return true si el pedido puede asignarse a un repartidor
     */
    protected boolean cumpleRequisitos() {
        return true;
    }

    /**
     * Imprime el identificador, tipo y dirección del pedido.
     */
    protected void mostrarEncabezado() {
        System.out.println("Pedido " + idPedido + " (" + tipoPedido + ")");
        System.out.println("Direccion: " + direccionEntrega);
        System.out.println("Asignando repartidor...");
    }

    private void registrarEvento(String descripcion) {
        historial.add(descripcion);
    }
}
