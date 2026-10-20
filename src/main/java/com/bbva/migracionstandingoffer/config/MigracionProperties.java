package com.bbva.migracionstandingoffer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "migracion")
public class MigracionProperties {
    private int hilos = 8;
    private String resumenDir = "logs/migracion-standing-offer";
    private Reintentos reintentos = new Reintentos();

    @Data
    public static class Reintentos {
        private int maxIntentos = 3;
        private long esperaMs = 3000L;
    }
}
