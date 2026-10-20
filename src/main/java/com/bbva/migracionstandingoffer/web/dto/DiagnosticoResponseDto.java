package com.bbva.migracionstandingoffer.web.dto;

import java.util.Map;

public class DiagnosticoResponseDto {

    private String estado;
    private String userId;
    private String bonitaUrl;
    private String mensaje;
    private String error;

    public DiagnosticoResponseDto() {
    }

    public static DiagnosticoResponseDto ok(String userId, String bonitaUrl, String mensaje) {
        DiagnosticoResponseDto dto = new DiagnosticoResponseDto();
        dto.setEstado("OK");
        dto.setUserId(userId);
        dto.setBonitaUrl(bonitaUrl);
        dto.setMensaje(mensaje);
        return dto;
    }

    public static DiagnosticoResponseDto error(String error) {
        DiagnosticoResponseDto dto = new DiagnosticoResponseDto();
        dto.setEstado("ERROR");
        dto.setError(error);
        return dto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBonitaUrl() {
        return bonitaUrl;
    }

    public void setBonitaUrl(String bonitaUrl) {
        this.bonitaUrl = bonitaUrl;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
