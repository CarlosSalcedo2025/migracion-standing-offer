package com.bbva.migracionstandingoffer.etapas.formalizacion;

import com.bbva.migracionstandingoffer.bonita.BonitaClient;
import com.bbva.migracionstandingoffer.bonita.FlowNodeDto;
import com.bbva.migracionstandingoffer.core.ContextoMigracion;
import com.bbva.migracionstandingoffer.core.EtapaMigracion;
import com.bbva.migracionstandingoffer.core.ResultadoEtapa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EtapaFormalizacion implements EtapaMigracion {
    private static final Logger log = LoggerFactory.getLogger(EtapaFormalizacion.class);

    private final BonitaClient bonitaClient;
    private final FormalizacionMapper mapper;

    public EtapaFormalizacion(BonitaClient bonitaClient, FormalizacionMapper mapper) {
        this.bonitaClient = bonitaClient;
        this.mapper = mapper;
    }

    @Override
    public int getOrden() {
        return 5;
    }

    @Override
    public String getNombreEtapa() {
        return "FORMALIZACION";
    }

    @Override
    public String getNombreTareaBonita() {
        return "Formalización";
    }

    @Override
    public boolean debeEjecutar(ContextoMigracion contexto) {
        return contexto.getCaseIdCM() != null && !contexto.getCaseIdCM().isEmpty();
    }

    @Override
    public ResultadoEtapa procesar(ContextoMigracion contexto) {
        long inicio = System.currentTimeMillis();
        try {
            String caseId = contexto.getCaseIdCM();
            log.info("[FORMALIZACION] Esperando tarea '{}' para caseId={}", getNombreTareaBonita(), caseId);

            FlowNodeDto task;
            try {
                task = bonitaClient.esperarTarea(caseId, getNombreTareaBonita());
            } catch (Exception e) {
                // Intento fallback sin tilde
                log.info("[FORMALIZACION] Reintentando buscar tarea como 'Formalizacion' (sin tilde)");
                task = bonitaClient.esperarTarea(caseId, "Formalizacion");
            }

            FormalizacionDto dto = (FormalizacionDto) contexto.getContrato().get("_DATOS_FORMALIZACION");
            Map<String, Object> payload = mapper.enriquecerContrato(contexto.getContrato(), task.getId(), dto);

            Map<String, Object> contratoActualizado = (Map<String, Object>) payload.get("contrato");
            contexto.fusionarContrato(contratoActualizado);

            log.info("[FORMALIZACION] Ejecutando tarea taskId={} en Bonita CM", task.getId());
            bonitaClient.ejecutarTarea(task.getId(), payload);

            long duracion = System.currentTimeMillis() - inicio;
            return ResultadoEtapa.ok(getNombreEtapa(), task.getId(), "Tarea Formalización ejecutada exitosamente. Proceso completado.", duracion);
        } catch (Exception e) {
            long duracion = System.currentTimeMillis() - inicio;
            log.error("[FORMALIZACION] Error en etapa Formalización: {}", e.getMessage(), e);
            return ResultadoEtapa.error(getNombreEtapa(), e.getMessage(), duracion);
        }
    }
}
