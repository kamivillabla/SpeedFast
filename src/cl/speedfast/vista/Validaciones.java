package cl.speedfast.vista;

import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Reglas de validación para los campos de los formularios.
 *
 * Cada regla recibe el contenido de un campo y devuelve el motivo del rechazo, o
 * null si el valor es aceptable, que es el contrato que espera
 * {@link CampoValidado}. Las reglas se combinan con {@link #todas} para que un
 * mismo campo exija varias condiciones y muestre la primera que incumple.
 */
final class Validaciones {

    private static final Pattern CONTIENE_LETRA = Pattern.compile(".*[A-Za-zÁÉÍÓÚÜÑáéíóúüñ].*");
    private static final Locale LOCALE_CHILE = Locale.of("es", "CL");

    private Validaciones() {
    }

    /**
     * Combina varias reglas en una sola.
     *
     * @param reglas reglas que debe cumplir el campo, en orden de comprobación
     * @return una regla que devuelve el primer motivo de rechazo encontrado
     */
    @SafeVarargs
    static Function<String, String> todas(Function<String, String>... reglas) {
        return texto -> {
            for (Function<String, String> regla : reglas) {
                String motivo = regla.apply(texto);

                if (motivo != null) {
                    return motivo;
                }
            }

            return null;
        };
    }

    /**
     * Exige que el campo tenga contenido.
     *
     * @param mensaje motivo que se muestra cuando el campo está vacío
     * @return la regla correspondiente
     */
    static Function<String, String> obligatorio(String mensaje) {
        return texto -> texto.isEmpty() ? mensaje : null;
    }

    /**
     * Acota la extensión del contenido.
     *
     * @param minimo cantidad mínima de caracteres
     * @param maximo cantidad máxima de caracteres
     * @return la regla correspondiente
     */
    static Function<String, String> longitudEntre(int minimo, int maximo) {
        return texto -> {
            if (texto.isEmpty() || (texto.length() >= minimo && texto.length() <= maximo)) {
                return null;
            }

            return "Debe tener entre " + minimo + " y " + maximo + " caracteres.";
        };
    }

    /**
     * Exige que el contenido incluya al menos una letra.
     *
     * @param mensaje motivo que se muestra cuando el texto no contiene ninguna
     * @return la regla correspondiente
     */
    static Function<String, String> contieneLetras(String mensaje) {
        return texto -> {
            if (texto.isEmpty() || CONTIENE_LETRA.matcher(texto).matches()) {
                return null;
            }

            return mensaje;
        };
    }

    /**
     * Exige un número dentro del rango indicado.
     *
     * Admite coma o punto como separador decimal y rechaza los valores que no
     * representan una cantidad, como los infinitos.
     *
     * @param concepto nombre del dato, usado para redactar el motivo del rechazo
     * @param minimo   valor mínimo aceptado
     * @param maximo   valor máximo aceptado
     * @return la regla correspondiente
     */
    static Function<String, String> numeroEntre(String concepto, double minimo, double maximo) {
        return texto -> {
            if (texto.isEmpty()) {
                return null;
            }

            double valor;

            try {
                valor = comoNumero(texto);
            } catch (NumberFormatException e) {
                return concepto + " debe ser un numero. Ejemplo: 4,5";
            }

            if (!Double.isFinite(valor)) {
                return concepto + " debe ser un numero. Ejemplo: 4,5";
            }

            if (valor < minimo || valor > maximo) {
                return concepto + " debe estar entre " + formatear(minimo) + " y " + formatear(maximo) + ".";
            }

            return null;
        };
    }

    /**
     * Interpreta un texto como número decimal, admitiendo coma como separador.
     *
     * @param texto contenido a interpretar
     * @return el valor numérico representado
     * @throws NumberFormatException si el texto no representa un número
     */
    static double comoNumero(String texto) {
        return Double.parseDouble(texto.replace(',', '.'));
    }

    /**
     * Escribe un número como se muestra al usuario, sin decimales sobrantes.
     *
     * @param valor número a escribir
     * @return el número con coma decimal, o sin decimales si no los necesita
     */
    private static String formatear(double valor) {
        if (valor == Math.rint(valor)) {
            return String.valueOf((long) valor);
        }

        return String.format(LOCALE_CHILE, "%.1f", valor);
    }
}
