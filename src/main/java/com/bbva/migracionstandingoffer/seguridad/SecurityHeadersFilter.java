package com.bbva.migracionstandingoffer.seguridad;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro de seguridad HTTP que inyecta cabeceras de protección contra
 * ataques XSS (Cross-Site Scripting), Content-Sniffing y Clickjacking.
 */
@Component
@Order(1)
public class SecurityHeadersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // Inicialización de filtro no requerida
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // Previene ejecución de scripts inyectados y restringe la carga de fuentes
            httpResponse.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'none'; object-src 'none';");

            // Bloquea páginas si el navegador detecta un ataque de Cross-Site Scripting reflejado
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

            // Evita ataques MIME-sniffing obligando a respetar el Content-Type declarado
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");

            // Previene ataques de clickjacking
            httpResponse.setHeader("X-Frame-Options", "DENY");

            // Política de referrer segura
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpieza de filtro no requerida
    }
}
