package cl.speedfast.vista;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Reglas de validación para los campos de los formularios.
 *
 * Cada regla recibe el contenido de un campo y devuelve el motivo del rechazo, o
 * null si el valor es aceptable. Las reglas se combinan con {@link #todas}.
 */
final class Validaciones {

    private static final Pattern CONTIENE_LETRA = Pattern.compile(".*[A-Za-zÁÉÍÓÚÜÑáéíóúüñ].*");
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

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
     * Exige una fecha válida con el formato {@code dd-mm-aaaa}.
     *
     * @return la regla correspondiente
     */
    static Function<String, String> fecha() {
        return texto -> {
            if (texto.isEmpty()) {
                return null;
            }

            try {
                comoFecha(texto);
            } catch (DateTimeParseException e) {
                return "Fecha no valida. Formato: dd-mm-aaaa";
            }

            return null;
        };
    }

    /**
     * Exige una hora válida con el formato {@code hh:mm}, de 00:00 a 23:59.
     *
     * @return la regla correspondiente
     */
    static Function<String, String> hora() {
        return texto -> {
            if (texto.isEmpty()) {
                return null;
            }

            try {
                comoHora(texto);
            } catch (DateTimeParseException e) {
                return "Hora no valida. Formato: hh:mm";
            }

            return null;
        };
    }

    /**
     * Interpreta un texto con el formato {@code dd-mm-aaaa} como fecha.
     *
     * @param texto contenido a interpretar
     * @return la fecha representada
     * @throws DateTimeParseException si el texto no representa una fecha válida
     */
    static LocalDate comoFecha(String texto) {
        return LocalDate.parse(texto, FORMATO_FECHA);
    }

    /**
     * Interpreta un texto con el formato {@code hh:mm} como hora.
     *
     * @param texto contenido a interpretar
     * @return la hora representada
     * @throws DateTimeParseException si el texto no representa una hora válida
     */
    static LocalTime comoHora(String texto) {
        return LocalTime.parse(texto, FORMATO_HORA);
    }

    /**
     * Escribe una fecha con el formato que aceptan los formularios.
     *
     * @param fecha fecha a escribir
     * @return la fecha como {@code dd-mm-aaaa}
     */
    static String formatear(LocalDate fecha) {
        return FORMATO_FECHA.format(fecha);
    }

    /**
     * Escribe una hora con el formato que aceptan los formularios.
     *
     * @param hora hora a escribir
     * @return la hora como {@code hh:mm}
     */
    static String formatear(LocalTime hora) {
        return FORMATO_HORA.format(hora);
    }
}
