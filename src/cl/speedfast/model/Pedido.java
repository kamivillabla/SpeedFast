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
     * Imprime el encabezado común a todos los pedidos.
     */
    protected void mostrarEncabezado() {
        System.out.println("Pedido " + idPedido + " (" + tipoPedido + ")");
        System.out.println("Direccion: " + direccionEntrega);
        System.out.println("Asignando repartidor...");
    }
}
