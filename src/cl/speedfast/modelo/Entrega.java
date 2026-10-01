package cl.speedfast.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entrega de SpeedFast: relaciona un pedido con el repartidor que lo lleva.
 *
 * Corresponde a una fila de la tabla {@code entregas}. Un pedido puede tener
 * varias entregas.
 */
public class Entrega {

    private int id;
    private int idPedido;
    private int idRepartidor;
    private LocalDate fecha;
    private LocalTime hora;

    public Entrega(int id, int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this(idPedido, idRepartidor, fecha, hora);
        this.id = id;
    }

    public Entrega(int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getId() {
        return id;
    }

    /**
     * Registra el identificador asignado por la base de datos.
     *
     * @param id identificador generado al guardar la entrega
     */
    public void setId(int id) {
        this.id = id;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public int getIdRepartidor() {
        return idRepartidor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }
}
