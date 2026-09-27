package cl.speedfast.dao;

import cl.speedfast.modelo.EstadoPedido;
import cl.speedfast.modelo.Pedido;
import cl.speedfast.modelo.PedidoComida;
import cl.speedfast.modelo.PedidoEncomienda;
import cl.speedfast.modelo.PedidoExpress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla {@code pedido}.
 *
 * La tabla almacena dirección, tipo y estado. La distancia y los datos propios de
 * cada tipo no se almacenan: un pedido leído desde la base de datos los recibe
 * con valores neutros.
 */
public class PedidoDAO {

    private static final String COMIDA = "COMIDA";
    private static final String ENCOMIENDA = "ENCOMIENDA";
    private static final String EXPRESS = "EXPRESS";

    private static final String SQL_INSERTAR =
            "INSERT INTO pedido (direccion, tipo, estado) VALUES (?, ?, ?)";
    private static final String SQL_LISTAR =
            "SELECT id, direccion, tipo, estado FROM pedido ORDER BY id";

    /**
     * Inserta el pedido y le asigna el identificador generado por la base de datos.
     *
     * @param pedido pedido que se desea registrar
     * @throws SQLException si la inserción no puede completarse
     */
    public void guardar(Pedido pedido) throws SQLException {
        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pedido.getDireccionEntrega());
            ps.setString(2, codigoDeTipo(pedido));
            ps.setString(3, pedido.getEstado().name());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    pedido.setIdPedido(String.valueOf(claves.getInt(1)));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("No fue posible guardar el pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta todos los pedidos registrados.
     *
     * @return los pedidos en el orden en que fueron registrados
     * @throws SQLException si la consulta no puede completarse
     */
    public List<Pedido> listarTodos() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = crearPedido(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("direccion"),
                        rs.getString("tipo"));
                pedido.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            throw new SQLException("No fue posible consultar los pedidos: " + e.getMessage(), e);
        }

        return pedidos;
    }

    /**
     * Traduce el tipo de pedido al código que almacena la columna {@code tipo}.
     */
    private static String codigoDeTipo(Pedido pedido) {
        if (pedido instanceof PedidoEncomienda) {
            return ENCOMIENDA;
        }

        if (pedido instanceof PedidoExpress) {
            return EXPRESS;
        }

        return COMIDA;
    }

    /**
     * Reconstruye el pedido que corresponde al código almacenado en la columna {@code tipo}.
     */
    private static Pedido crearPedido(String id, String direccion, String tipo) {
        return switch (tipo) {
            case ENCOMIENDA -> new PedidoEncomienda(id, direccion, 0, 0, "");
            case EXPRESS -> new PedidoExpress(id, direccion, 0, false);
            default -> new PedidoComida(id, direccion, 0, false);
        };
    }
}
