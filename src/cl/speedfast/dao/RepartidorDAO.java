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
 * Acceso a la tabla {@code repartidor}.
 */
public class RepartidorDAO {

    private static final String SQL_INSERTAR =
            "INSERT INTO repartidor (nombre) VALUES (?)";
    private static final String SQL_LISTAR =
            "SELECT id, nombre FROM repartidor ORDER BY nombre";

    /**
     * Inserta el repartidor y le asigna el identificador generado por la base de datos.
     *
     * @param repartidor repartidor que se desea registrar
     * @throws SQLException si la inserción no puede completarse
     */
    public void guardar(Repartidor repartidor) throws SQLException {
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, repartidor.getNombre());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    repartidor.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("No fue posible guardar el repartidor: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta todos los repartidores registrados.
     *
     * @return los repartidores ordenados por nombre
     * @throws SQLException si la consulta no puede completarse
     */
    public List<Repartidor> listarTodos() throws SQLException {
        List<Repartidor> repartidores = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
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
}
