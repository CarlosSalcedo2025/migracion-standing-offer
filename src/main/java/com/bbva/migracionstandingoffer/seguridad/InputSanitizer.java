package com.bbva.migracionstandingoffer.seguridad;

import java.util.regex.Pattern;

/**
 * Utilidad de sanitización y validación estricta de entradas para prevenir
 * vectores de inyección XSS (Cross-Site Scripting) y manipulación de datos.
 */
public final class InputSanitizer {

    private static final Pattern PATRON_ALFANUMERICO_SEGURO = Pattern.compile("^[a-zA-Z0-9_\\-\\.]+$");
    private static final Pattern PATRON_CARACTERES_PELIGROSOS = Pattern.compile("[<>\"'%;()&+/\\]\\[\\\\`]");

    private InputSanitizer() {
        // Prevenir instanciación
    }

    /**
     * Valida y sanitiza identificadores o números de caso.
     * Solo permite caracteres alfanuméricos, guiones y puntos.
     *
     * @param entrada Texto a evaluar
     * @return Texto limpio
     * @throws IllegalArgumentException si contiene caracteres no permitidos
     */
    public static String sanitizarIdentificador(String entrada) {
        if (entrada == null) {
            return null;
        }
        String limpio = entrada.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("El identificador proporcionado no puede estar vacío");
        }
        if (!PATRON_ALFANUMERICO_SEGURO.matcher(limpio).matches()) {
            throw new IllegalArgumentException("Identificador inválido: solo se permiten caracteres alfanuméricos, guiones y puntos");
        }
        return limpio;
    }

    /**
     * Escapa caracteres HTML críticos para evitar que un contenido inyectado sea interpretado
     * como código ejecutable por navegadores web.
     *
     * @param entrada Texto libre
     * @return Texto con entidades HTML codificadas
     */
    public static String escaparHtml(String entrada) {
        if (entrada == null) {
            return null;
        }
        return entrada.replace("&", "&amp;")
                      .replace("<", "&lt;")
                      .replace(">", "&gt;")
                      .replace("\"", "&quot;")
                      .replace("'", "&#x27;")
                      .replace("/", "&#x2F;");
    }
}
