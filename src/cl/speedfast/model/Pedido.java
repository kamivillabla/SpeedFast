package cl.speedfast.model;

/**
 * Pedido de SpeedFast.
 *
 * Define los datos comunes a todo pedido, el resumen y la asignación de
 * repartidor. El cálculo del tiempo estimado de entrega corresponde a cada tipo
 * de pedido.
 */
public abstract class Pedido {

    private String idPedido;
    private String direccionEntrega;
    private double distanciaKm;
    private String tipoPedido;

    public Pedido(String idPedido, String direccionEntrega, double distanciaKm, String tipoPedido) {
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
        this.tipoPedido = tipoPedido;
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

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    /**
     * Imprime el tipo, identificador, dirección y distancia del pedido.
     */
    public void mostrarResumen() {
        System.out.println(tipoPedido + " #" + idPedido);
        System.out.println("Direccion: " + direccionEntrega);
        System.out.printf("Distancia: %.1f km%n", distanciaKm);
    }

    /**
     * Tiempo estimado de entrega del pedido.
     *
     * @return tiempo estimado de entrega en minutos
     */
    public abstract int calcularTiempoEntrega();

    /**
     * Aplica el criterio de asignación de repartidor del pedido.
     */
    public void asignarRepartidor() {
        mostrarEncabezado();
        System.out.println("Aplicando criterio general de asignacion... OK");
    }

    /**
     * Asigna un repartidor concreto al pedido.
     *
     * Aplica el criterio de asignación y, si el pedido cumple los requisitos,
     * confirma el repartidor.
     *
     * @param nombreRepartidor nombre del repartidor que se desea asignar
     */
    public void asignarRepartidor(String nombreRepartidor) {
        asignarRepartidor();

        if (cumpleRequisitos()) {
            System.out.println("Pedido asignado a " + nombreRepartidor);
        } else {
            System.out.println("No es posible asignar a " + nombreRepartidor + ". Pedido derivado a revision.");
        }
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
}
