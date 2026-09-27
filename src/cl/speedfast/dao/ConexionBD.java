package cl.speedfast.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto único de acceso a la base de datos {@code speedfast_db}.
 *
 * Centraliza la URL JDBC y las credenciales para que ninguna otra clase las
 * repita. Cada llamada entrega una conexión nueva, que quien la solicita debe
 * cerrar al terminar su operación.
 */
public final class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "tu_contrasena";

    private ConexionBD() {
    }

    /**
     * Abre una conexión con la base de datos mediante el driver MySQL.
     *
     * @return una conexión activa con {@code speedfast_db}
     * @throws SQLException si el servidor no responde, las credenciales son
     *                      rechazadas o la base de datos no existe
     */
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
