package cl.speedfast.dao;

import cl.speedfast.modelo.Entrega;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones CRUD sobre la tabla {@code entregas}.
 */
public class EntregaDAO {

    private static final String SQL_INSERTAR =
            "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";
    private static final String SQL_LISTAR =
            "SELECT id, id_pedido, id_repartidor, fecha, hora FROM entregas ORDER BY id";
    private static final String SQL_ACTUALIZAR =
            "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";
    private static final String SQL_ELIMINAR =
            "DELETE FROM entregas WHERE id = ?";

    /**
     * Registra la entrega y le asigna el identificador generado por la base de datos.
     *
     * @param entrega entrega que se desea registrar
     * @throws SQLException si la inserción no puede completarse, por ejemplo
     *                      cuando el pedido o el repartidor no existen
     */
    public void create(Entrega entrega) throws SQLException {
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            asignarDatos(ps, entrega);
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

    /**
     * Consulta todas las entregas registradas.
     *
     * @return las entregas en el orden en que fueron registradas
     * @throws SQLException si la consulta no puede completarse
     */
    public List<Entrega> readAll() throws SQLException {
        List<Entrega> entregas = new ArrayList<>();

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                entregas.add(new Entrega(
                        rs.getInt("id"),
                        rs.getInt("id_pedido"),
                        rs.getInt("id_repartidor"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora").toLocalTime()));
            }
        } catch (SQLException e) {
            throw new SQLException("No fue posible consultar las entregas: " + e.getMessage(), e);
        }

        return entregas;
    }

    /**
     * Modifica el pedido, el repartidor, la fecha y la hora de la entrega que
     * tiene el mismo identificador.
     *
     * @param entrega entrega con su identificador y los datos actualizados
     * @return true si la entrega existía y fue modificada
     * @throws SQLException si la actualización no puede completarse
     */
    public boolean update(Entrega entrega) throws SQLException {
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarDatos(ps, entrega);
            ps.setInt(5, entrega.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("No fue posible actualizar la entrega: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina la entrega indicada.
     *
     * @param id identificador de la entrega
     * @return true si la entrega existía y fue eliminada
     * @throws SQLException si la eliminación no puede completarse
     */
    public boolean delete(int id) throws SQLException {
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("No fue posible eliminar la entrega: " + e.getMessage(), e);
        }
    }

    /**
     * Asigna pedido, repartidor, fecha y hora a los cuatro primeros parámetros de
     * la sentencia, en ese orden.
     */
    private static void asignarDatos(PreparedStatement ps, Entrega entrega) throws SQLException {
        ps.setInt(1, entrega.getIdPedido());
        ps.setInt(2, entrega.getIdRepartidor());
        ps.setDate(3, Date.valueOf(entrega.getFecha()));
        ps.setTime(4, Time.valueOf(entrega.getHora()));
    }
}
