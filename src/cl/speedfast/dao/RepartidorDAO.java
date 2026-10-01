package cl.speedfast.dao;

import cl.speedfast.modelo.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones CRUD sobre la tabla {@code repartidores}.
 */
public class RepartidorDAO {

    /** Código de MySQL para una fila que otra tabla referencia por clave foránea. */
    private static final int ERROR_FILA_REFERENCIADA = 1451;

    private static final String SQL_INSERTAR =
            "INSERT INTO repartidores (nombre) VALUES (?)";
    private static final String SQL_LISTAR =
            "SELECT id, nombre FROM repartidores ORDER BY id";
    private static final String SQL_ACTUALIZAR =
            "UPDATE repartidores SET nombre = ? WHERE id = ?";
    private static final String SQL_ELIMINAR =
            "DELETE FROM repartidores WHERE id = ?";

    /**
     * Inserta el repartidor y le asigna el identificador generado por la base de datos.
     *
     * @param repartidor repartidor que se desea registrar
     * @throws SQLException si la inserción no puede completarse
     */
    public void create(Repartidor repartidor) throws SQLException {
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, repartidor.getNombre());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    repartidor.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("No fue posible registrar el repartidor: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta todos los repartidores registrados.
     *
     * @return los repartidores ordenados por identificador
     * @throws SQLException si la consulta no puede completarse
     */
    public List<Repartidor> readAll() throws SQLException {
        List<Repartidor> repartidores = new ArrayList<>();

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                repartidores.add(new Repartidor(rs.getInt("id"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            throw new SQLException("No fue posible consultar los repartidores: " + e.getMessage(), e);
        }

        return repartidores;
    }

    /**
     * Modifica el nombre del repartidor que tiene el mismo identificador.
     *
     * @param repartidor repartidor con su identificador y el nombre actualizado
     * @return true si el repartidor existía y fue modificado
     * @throws SQLException si la actualización no puede completarse
     */
    public boolean update(Repartidor repartidor) throws SQLException {
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setString(1, repartidor.getNombre());
            ps.setInt(2, repartidor.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("No fue posible actualizar el repartidor: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina el repartidor indicado.
     *
     * @param id identificador del repartidor
     * @return true si el repartidor existía y fue eliminado
     * @throws SQLException si el repartidor tiene entregas registradas o la
     *                      eliminación no puede completarse
     */
    public boolean delete(int id) throws SQLException {
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_FILA_REFERENCIADA) {
                throw new SQLException("El repartidor tiene entregas registradas. Elimina primero esas entregas.", e);
            }

            throw new SQLException("No fue posible eliminar el repartidor: " + e.getMessage(), e);
        }
    }
}
