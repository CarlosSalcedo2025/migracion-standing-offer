package com.bbva.migracionstandingoffer.etapas.evaluacionsancion;

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
public class EtapaEvaluacionSancion implements EtapaMigracion {
    private static final Logger log = LoggerFactory.getLogger(EtapaEvaluacionSancion.class);

    private final BonitaClient bonitaClient;
    private final EvaluacionSancionMapper mapper;

    public EtapaEvaluacionSancion(BonitaClient bonitaClient, EvaluacionSancionMapper mapper) {
        this.bonitaClient = bonitaClient;
        this.mapper = mapper;
    }

    @Override
    public int getOrden() {
        return 3;
    }

    @Override
    public String getNombreEtapa() {
        return "EVALUACION_SANCION";
    }

    @Override
    public String getNombreTareaBonita() {
        return "Evaluacion y sancion";
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
            log.info("[EVALUACION_SANCION] Esperando tarea '{}' para caseId={}", getNombreTareaBonita(), caseId);

            FlowNodeDto task = bonitaClient.esperarTarea(caseId, getNombreTareaBonita());

            EvaluacionSancionDto dto = (EvaluacionSancionDto) contexto.getContrato().get("_DATOS_EVALUACION_SANCION");
            Map<String, Object> payload = mapper.enriquecerContrato(contexto.getContrato(), dto);

            // Actualizar contrato acumulado en el contexto
            Map<String, Object> contratoActualizado = (Map<String, Object>) payload.get("contrato");
            contexto.fusionarContrato(contratoActualizado);

            log.info("[EVALUACION_SANCION] Ejecutando tarea taskId={} en Bonita CM", task.getId());
            bonitaClient.ejecutarTarea(task.getId(), payload);

            long duracion = System.currentTimeMillis() - inicio;
            return ResultadoEtapa.ok(getNombreEtapa(), task.getId(), "Tarea Evaluación y Sanción ejecutada exitosamente", duracion);
        } catch (Exception e) {
            long duracion = System.currentTimeMillis() - inicio;
            log.error("[EVALUACION_SANCION] Error en etapa Evaluación y Sanción: {}", e.getMessage(), e);
            return ResultadoEtapa.error(getNombreEtapa(), e.getMessage(), duracion);
        }
    }
}
