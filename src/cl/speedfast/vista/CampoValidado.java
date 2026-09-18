package cl.speedfast.vista;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Function;

/**
 * Campo de formulario que informa sus errores a medida que el usuario escribe.
 *
 * Reúne la etiqueta, el cuadro de texto y el mensaje de error en una sola fila, y
 * aplica su regla de validación en cada modificación del contenido. Cuando el
 * valor no es aceptable, el cuadro de texto se destaca con un borde rojo y el
 * motivo aparece bajo el campo.
 *
 * El mensaje ocupa siempre una línea, con o sin error, para que el formulario no
 * cambie de tamaño mientras se completa. La validación se dispara solo ante
 * cambios hechos por el usuario: un formulario recién abierto o recién limpiado
 * no se presenta lleno de advertencias.
 */
class CampoValidado {

    private static final Color COLOR_ERROR = new Color(178, 34, 34);
    private static final float TAMANO_MENSAJE_PT = 11f;
    private static final int ANCHO_ETIQUETA_PX = 150;
    private static final int SEPARACION_MENSAJE_PX = 2;
    private static final int SEPARACION_FILAS_PX = 6;
    private static final String SIN_MENSAJE = " ";

    private final JTextField campo;
    private final JLabel mensajeError = new JLabel(SIN_MENSAJE);
    private final JPanel fila = new JPanel(new BorderLayout()) {

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }
    };
    private final Function<String, String> regla;
    private final Border bordeOriginal;

    private boolean validacionSuspendida;

    /**
     * Crea un campo que se valida a medida que se escribe en él.
     *
     * @param etiqueta nombre del dato, mostrado a la izquierda del cuadro de texto
     * @param columnas ancho del cuadro de texto, en columnas de caracteres
     * @param regla    regla que recibe el contenido del campo y devuelve el mensaje
     *                 de error, o null si el valor es aceptable
     */
    CampoValidado(String etiqueta, int columnas, Function<String, String> regla) {
        this.regla = regla;
        this.campo = new JTextField(columnas);
        this.bordeOriginal = campo.getBorder();

        JLabel titulo = new JLabel(etiqueta);
        titulo.setPreferredSize(new Dimension(ANCHO_ETIQUETA_PX, campo.getPreferredSize().height));
        titulo.setVerticalAlignment(JLabel.TOP);

        mensajeError.setForeground(COLOR_ERROR);
        mensajeError.setFont(mensajeError.getFont().deriveFont(Font.PLAIN, TAMANO_MENSAJE_PT));

        JPanel entrada = new JPanel(new BorderLayout(0, SEPARACION_MENSAJE_PX));
        entrada.add(campo, BorderLayout.NORTH);
        entrada.add(mensajeError, BorderLayout.SOUTH);

        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setBorder(BorderFactory.createEmptyBorder(0, 0, SEPARACION_FILAS_PX, 0));
        fila.add(titulo, BorderLayout.WEST);
        fila.add(entrada, BorderLayout.CENTER);

        campo.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                validarCambio();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validarCambio();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validarCambio();
            }
        });
    }

    /**
     * Entrega la fila lista para incorporarse al formulario.
     *
     * @return el panel que agrupa etiqueta, cuadro de texto y mensaje de error
     */
    JPanel getFila() {
        return fila;
    }

    /**
     * Entrega el contenido del campo sin espacios sobrantes.
     *
     * @return el texto ingresado por el usuario
     */
    String getTexto() {
        return campo.getText().trim();
    }

    /**
     * Comprueba el contenido del campo y muestra el resultado.
     *
     * @return true si el valor cumple la regla de validación
     */
    boolean validar() {
        String error = regla.apply(getTexto());

        mensajeError.setText(error == null ? SIN_MENSAJE : error);
        campo.setBorder(error == null ? bordeOriginal : BorderFactory.createLineBorder(COLOR_ERROR));

        return error == null;
    }

    /**
     * Devuelve el campo a su estado inicial: sin contenido y sin advertencias.
     */
    void limpiar() {
        validacionSuspendida = true;
        campo.setText("");
        validacionSuspendida = false;

        descartarAdvertencia();
    }

    /**
     * Retira la advertencia visible sin alterar el contenido del campo.
     *
     * Se aplica a los campos que dejan de ser exigibles al cambiar el tipo de
     * pedido, para que no sigan señalando un error que ya no corresponde.
     */
    void descartarAdvertencia() {
        mensajeError.setText(SIN_MENSAJE);
        campo.setBorder(bordeOriginal);
    }

    /**
     * Lleva el foco a este campo y selecciona su contenido.
     */
    void enfocar() {
        campo.requestFocusInWindow();
        campo.selectAll();
    }

    private void validarCambio() {
        if (!validacionSuspendida) {
            validar();
        }
    }
}
