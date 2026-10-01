package cl.speedfast.modelo;

/**
 * Repartidor registrado en SpeedFast.
 *
 * Corresponde a una fila de la tabla {@code repartidores}.
 */
public class Repartidor {

    private int id;
    private String nombre;

    public Repartidor(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Repartidor(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    /**
     * Registra el identificador asignado por la base de datos.
     *
     * @param id identificador generado al guardar el repartidor
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Describe al repartidor con su identificador y nombre.
     *
     * @return descripción del repartidor
     */
    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}
