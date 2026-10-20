package com.bbva.migracionstandingoffer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "migracion")
public class MigracionProperties {
    private int hilos = 8;
    private String resumenDir = "logs/migracion-standing-offer";
    private Reintentos reintentos = new Reintentos();

    public MigracionProperties() {
    }

    public int getHilos() {
        return hilos;
    }

    public void setHilos(int hilos) {
        this.hilos = hilos;
    }

    public String getResumenDir() {
        return resumenDir;
    }

    public void setResumenDir(String resumenDir) {
        this.resumenDir = resumenDir;
    }

    public Reintentos getReintentos() {
        return reintentos;
    }

    public void setReintentos(Reintentos reintentos) {
        this.reintentos = reintentos;
    }

    public static class Reintentos {
        private int maxIntentos = 3;
        private long esperaMs = 3000L;

        public Reintentos() {
        }

        public int getMaxIntentos() {
            return maxIntentos;
        }

        public void setMaxIntentos(int maxIntentos) {
            this.maxIntentos = maxIntentos;
        }

        public long getEsperaMs() {
            return esperaMs;
        }

        public void setEsperaMs(long esperaMs) {
            this.esperaMs = esperaMs;
        }
    }
}
