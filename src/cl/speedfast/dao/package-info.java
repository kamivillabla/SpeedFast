/**
 * Acceso a la base de datos de SpeedFast mediante JDBC.
 *
 * Contiene {@link cl.speedfast.dao.ConexionBD}, que centraliza la conexión con
 * MySQL, y una clase DAO por tabla. Cada operación abre su propia conexión, usa
 * {@link java.sql.PreparedStatement} y cierra sus recursos con try-with-resources.
 */
package cl.speedfast.dao;
