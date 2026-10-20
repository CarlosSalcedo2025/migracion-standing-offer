package com.bbva.migracionstandingoffer.core;

public class ResultadoEtapa {
    private String nombreEtapa;
    private String taskId;
    private boolean exitoso;
    private String mensaje;
    private long duracionMs;

    public ResultadoEtapa() {
    }

    public ResultadoEtapa(String nombreEtapa, String taskId, boolean exitoso, String mensaje, long duracionMs) {
        this.nombreEtapa = nombreEtapa;
        this.taskId = taskId;
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.duracionMs = duracionMs;
    }

    public static ResultadoEtapa ok(String nombreEtapa, String taskId, String mensaje, long duracionMs) {
        return new ResultadoEtapa(nombreEtapa, taskId, true, mensaje, duracionMs);
    }

    public static ResultadoEtapa error(String nombreEtapa, String mensaje, long duracionMs) {
        return new ResultadoEtapa(nombreEtapa, null, false, mensaje, duracionMs);
    }

    public String getNombreEtapa() {
        return nombreEtapa;
    }

    public void setNombreEtapa(String nombreEtapa) {
        this.nombreEtapa = nombreEtapa;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public long getDuracionMs() {
        return duracionMs;
    }

    public void setDuracionMs(long duracionMs) {
        this.duracionMs = duracionMs;
    }
}
