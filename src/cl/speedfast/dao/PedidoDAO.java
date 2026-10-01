package cl.speedfast.dao;

import cl.speedfast.modelo.EstadoPedido;
import cl.speedfast.modelo.Pedido;
import cl.speedfast.modelo.TipoPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones CRUD sobre la tabla {@code pedidos}.
 *
 * La tabla almacena dirección, tipo y estado. La distancia y los datos propios de
 * cada tipo no se almacenan: un pedido leído desde la base de datos los recibe
 * con valores neutros.
 */
public class PedidoDAO {

    /** Código de MySQL para una fila que otra tabla referencia por clave foránea. */
    private static final int ERROR_FILA_REFERENCIADA = 1451;

    private static final String SQL_INSERTAR =
            "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";
    private static final String SQL_LISTAR =
            "SELECT id, direccion, tipo, estado FROM pedidos ORDER BY id";
    private static final String SQL_ACTUALIZAR =
            "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";
    private static final String SQL_ELIMINAR =
            "DELETE FROM pedidos WHERE id = ?";

    /**
     * Inserta el pedido y le asigna el identificador generado por la base de datos.
     *
     * @param pedido pedido que se desea registrar
     * @throws SQLException si la inserción no puede completarse
     */
    public void create(Pedido pedido) throws SQLException {
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pedido.getDireccionEntrega());
            ps.setString(2, TipoPedido.de(pedido).name());
            ps.setString(3, pedido.getEstado().name());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    pedido.setIdPedido(String.valueOf(claves.getInt(1)));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("No fue posible registrar el pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta todos los pedidos registrados.
     *
     * @return los pedidos en el orden en que fueron registrados
     * @throws SQLException si la consulta no puede completarse
     */
    public List<Pedido> readAll() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TipoPedido tipo = TipoPedido.valueOf(rs.getString("tipo"));
                Pedido pedido = tipo.crearPedido(String.valueOf(rs.getInt("id")), rs.getString("direccion"));
                pedido.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            throw new SQLException("No fue posible consultar los pedidos: " + e.getMessage(), e);
        }

        return pedidos;
    }

    /**
     * Modifica la dirección, el tipo y el estado del pedido que tiene el mismo
     * identificador.
     *
     * @param pedido pedido con su identificador y los datos actualizados
     * @return true si el pedido existía y fue modificado
     * @throws SQLException si la actualización no puede completarse
     */
    public boolean update(Pedido pedido) throws SQLException {
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setString(1, pedido.getDireccionEntrega());
            ps.setString(2, TipoPedido.de(pedido).name());
            ps.setString(3, pedido.getEstado().name());
            ps.setInt(4, Integer.parseInt(pedido.getIdPedido()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("No fue posible actualizar el pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina el pedido indicado.
     *
     * @param id identificador del pedido
     * @return true si el pedido existía y fue eliminado
     * @throws SQLException si el pedido tiene entregas registradas o la
     *                      eliminación no puede completarse
     */
    public boolean delete(int id) throws SQLException {
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_FILA_REFERENCIADA) {
                throw new SQLException("El pedido tiene entregas registradas. Elimina primero esas entregas.", e);
            }

            throw new SQLException("No fue posible eliminar el pedido: " + e.getMessage(), e);
        }
    }
}
