package cl.speedfast.model;

/**
 * Clase base de la jerarquía de pedidos de SpeedFast.
 *
 * Define los atributos comunes a todo pedido y el comportamiento genérico de
 * asignación de repartidor, que las subclases especializan.
 */
public class Pedido {

    private String idPedido;
    private String direccionEntrega;
    private String tipoPedido;

    public Pedido(String idPedido, String direccionEntrega, String tipoPedido) {
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.tipoPedido = tipoPedido;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public String getTipoPedido() {
        return tipoPedido;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

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
     * Sobrecarga de {@link #asignarRepartidor()}: mismo nombre, distinta lista
     * de parámetros.
     *
     * Ejecuta la validación propia del tipo de pedido y, si esta se cumple,
     * confirma el repartidor asignado. La llamada interna se resuelve en tiempo
     * de ejecución según el tipo real del objeto, por lo que utiliza la versión
     * sobrescrita de la subclase correspondiente.
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
     * Un pedido genérico no tiene restricciones; las subclases que sí las tienen
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
