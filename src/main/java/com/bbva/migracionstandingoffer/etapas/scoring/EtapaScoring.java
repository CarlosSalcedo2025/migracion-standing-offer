package com.bbva.migracionstandingoffer.etapas.scoring;

import com.bbva.migracionstandingoffer.bonita.BonitaClient;
import com.bbva.migracionstandingoffer.bonita.FlowNodeDto;
import com.bbva.migracionstandingoffer.core.ContextoMigracion;
import com.bbva.migracionstandingoffer.core.EtapaMigracion;
import com.bbva.migracionstandingoffer.core.ResultadoEtapa;
import com.bbva.migracionstandingoffer.modelo.ScoringContratoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EtapaScoring implements EtapaMigracion {
    private static final Logger log = LoggerFactory.getLogger(EtapaScoring.class);

    private final BonitaClient bonitaClient;
    private final ScoringMapper mapper;

    public EtapaScoring(BonitaClient bonitaClient, ScoringMapper mapper) {
        this.bonitaClient = bonitaClient;
        this.mapper = mapper;
    }

    @Override
    public int getOrden() {
        return 2;
    }

    @Override
    public String getNombreEtapa() {
        return "SCORING";
    }

    @Override
    public String getNombreTareaBonita() {
        return "Scoring";
    }

    @Override
    public boolean debeEjecutar(ContextoMigracion contexto) {
        // Requiere tener caseIdCM generado
        return contexto.getCaseIdCM() != null && !contexto.getCaseIdCM().isEmpty();
    }

    @Override
    public ResultadoEtapa procesar(ContextoMigracion contexto) {
        long inicio = System.currentTimeMillis();
        try {
            String caseId = contexto.getCaseIdCM();
            log.info("[SCORING] Esperando tarea '{}' para caseId={}", getNombreTareaBonita(), caseId);

            FlowNodeDto task = bonitaClient.esperarTarea(caseId, getNombreTareaBonita());

            // Enriquecer contrato acumulado
            ScoringContratoDto datosScoring = (ScoringContratoDto) contexto.getContrato().get("_DATOS_SCORING");
            Map<String, Object> payload = mapper.enriquecerContrato(contexto.getContrato(), datosScoring);

            // Actualizar contrato acumulado en el contexto para las siguientes etapas
            Map<String, Object> contratoActualizado = (Map<String, Object>) payload.get("contrato");
            contexto.fusionarContrato(contratoActualizado);

            log.info("[SCORING] Ejecutando tarea taskId={} en Bonita CM", task.getId());
            bonitaClient.ejecutarTarea(task.getId(), payload);

            long duracion = System.currentTimeMillis() - inicio;
            return ResultadoEtapa.ok(getNombreEtapa(), task.getId(), "Tarea Scoring ejecutada exitosamente", duracion);
        } catch (Exception e) {
            long duracion = System.currentTimeMillis() - inicio;
            log.error("[SCORING] Error al ejecutar etapa Scoring: {}", e.getMessage(), e);
            return ResultadoEtapa.error(getNombreEtapa(), e.getMessage(), duracion);
        }
    }
}
