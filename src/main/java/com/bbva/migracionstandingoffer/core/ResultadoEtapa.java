package com.bbva.migracionstandingoffer.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoEtapa {
    private String nombreEtapa;
    private String taskId;
    private boolean exitoso;
    private String mensaje;
    private long duracionMs;

    public static ResultadoEtapa ok(String nombreEtapa, String taskId, String mensaje, long duracionMs) {
        return ResultadoEtapa.builder()
                .nombreEtapa(nombreEtapa)
                .taskId(taskId)
                .exitoso(true)
                .mensaje(mensaje)
                .duracionMs(duracionMs)
                .build();
    }

    public static ResultadoEtapa error(String nombreEtapa, String mensaje, long duracionMs) {
        return ResultadoEtapa.builder()
                .nombreEtapa(nombreEtapa)
                .exitoso(false)
                .mensaje(mensaje)
                .duracionMs(duracionMs)
                .build();
    }
}
