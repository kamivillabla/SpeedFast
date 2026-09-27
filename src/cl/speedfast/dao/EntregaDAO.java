package cl.speedfast.dao;

import cl.speedfast.modelo.Entrega;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

/**
 * Acceso a la tabla {@code entrega}.
 *
 * Las claves foráneas de la tabla garantizan que cada entrega apunte a un pedido
 * y a un repartidor existentes.
 */
public class EntregaDAO {

    private static final String SQL_INSERTAR =
            "INSERT INTO entrega (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";

    /**
     * Registra la entrega de un pedido a cargo de un repartidor.
     *
     * @param entrega entrega que se desea registrar
     * @throws SQLException si la inserción no puede completarse, por ejemplo
     *                      cuando el pedido o el repartidor no existen
     */
    public void guardar(Entrega entrega) throws SQLException {
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    entrega.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("No fue posible registrar la entrega: " + e.getMessage(), e);
        }
    }
}
