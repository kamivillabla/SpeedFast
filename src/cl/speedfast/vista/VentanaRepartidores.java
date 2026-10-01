package cl.speedfast.vista;

import cl.speedfast.dao.RepartidorDAO;
import cl.speedfast.modelo.Repartidor;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.sql.SQLException;
import java.util.List;

/**
 * Gestión de repartidores: registro, edición, eliminación y listado mediante
 * {@link RepartidorDAO}.
 */
public class VentanaRepartidores extends VentanaGestion {

    private static final String[] COLUMNAS = {"ID", "Nombre"};
    private static final int COLUMNAS_CAMPO = 18;
    private static final int LARGO_MINIMO_NOMBRE = 3;
    private static final int LARGO_MAXIMO_NOMBRE = 100;

    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private final CampoValidado campoNombre = new CampoValidado("Nombre:", COLUMNAS_CAMPO, Validaciones.todas(
            Validaciones.obligatorio("Ingresa el nombre del repartidor."),
            Validaciones.longitudEntre(LARGO_MINIMO_NOMBRE, LARGO_MAXIMO_NOMBRE),
            Validaciones.contieneLetras("El nombre debe incluir letras.")));

    /**
     * Construye la ventana. Los datos se cargan con {@link #refrescar()}.
     */
    public VentanaRepartidores() {
        super("Repartidores", "Repartidor", COLUMNAS);

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.add(campoNombre.getFila());

        construir(formulario);
    }

    @Override
    void refrescar() {
        List<Repartidor> repartidores;

        try {
            repartidores = repartidorDAO.readAll();
        } catch (SQLException e) {
            informarError(e);
            return;
        }

        modeloTabla.setRowCount(0);

        for (Repartidor repartidor : repartidores) {
            modeloTabla.addRow(new Object[]{repartidor.getId(), repartidor.getNombre()});
        }
    }

    @Override
    protected boolean formularioValido() {
        if (campoNombre.validar()) {
            return true;
        }

        campoNombre.enfocar();

        return false;
    }

    @Override
    protected String insertar() throws SQLException {
        Repartidor repartidor = new Repartidor(campoNombre.getTexto());
        repartidorDAO.create(repartidor);

        return repartidor.toString();
    }

    @Override
    protected boolean actualizar(int id) throws SQLException {
        return repartidorDAO.update(new Repartidor(id, campoNombre.getTexto()));
    }

    @Override
    protected boolean eliminar(int id) throws SQLException {
        return repartidorDAO.delete(id);
    }

    @Override
    protected void mostrarSeleccion(int fila) {
        campoNombre.setTexto(modeloTabla.getValueAt(fila, 1).toString());
    }

    @Override
    protected void limpiarCampos() {
        campoNombre.limpiar();
        campoNombre.enfocar();
    }
}
