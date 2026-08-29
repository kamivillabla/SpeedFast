package cl.speedfast.interfaces;

/**
 * Envío cuya entrega puede anularse mientras no haya sido despachado.
 */
public interface Cancelable {

    /**
     * Cancela el envío.
     */
    void cancelar();
}
