package cl.speedfast.model;

/**
 * Clase abstracta base de la jerarquía de pedidos de SpeedFast.
 *
 * Define los atributos comunes a todo pedido, el comportamiento reutilizable
 * (resumen y asignación de repartidor) y declara como abstracto el cálculo del
 * tiempo estimado de entrega, que cada tipo de pedido resuelve con su propia
 * fórmula.
 *
 * No se instancia directamente: representa el concepto general de pedido.
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
     * Imprime los datos básicos del pedido.
     *
     * El resumen es idéntico para todos los tipos de pedido, por lo que se define
     * una sola vez en esta clase.
     */
    public void mostrarResumen() {
        System.out.println(tipoPedido + " #" + idPedido);
        System.out.println("Direccion: " + direccionEntrega);
        System.out.printf("Distancia: %.1f km%n", distanciaKm);
    }

    /**
     * Tiempo estimado de entrega del pedido.
     *
     * Cada tipo de pedido aplica una fórmula distinta, por lo que la
     * implementación corresponde a las clases derivadas.
     *
     * @return tiempo estimado de entrega en minutos
     */
    public abstract int calcularTiempoEntrega();

    /**
     * Aplica el criterio genérico de asignación.
     *
     * Cada subclase sobrescribe este método con el criterio que le corresponde.
     */
    public void asignarRepartidor() {
        mostrarEncabezado();
        System.out.println("Aplicando criterio general de asignacion... OK");
    }

    /**
     * Asigna un repartidor concreto al pedido.
     *
     * Ejecuta la validación propia del tipo de pedido y, si esta se cumple,
     * confirma el repartidor asignado. La llamada interna a
     * {@link #asignarRepartidor()} se resuelve según el tipo real del objeto.
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
     * Un pedido sin restricciones devuelve true; las subclases que sí las tienen
     * sobrescriben este método.
     *
     * @return true si el pedido puede asignarse a un repartidor
     */
    protected boolean cumpleRequisitos() {
        return true;
    }

    /**
     * Imprime el encabezado común a todos los pedidos.
     */
    protected void mostrarEncabezado() {
        System.out.println("Pedido " + idPedido + " (" + tipoPedido + ")");
        System.out.println("Direccion: " + direccionEntrega);
        System.out.println("Asignando repartidor...");
    }
}
