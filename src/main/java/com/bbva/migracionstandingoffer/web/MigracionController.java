package com.bbva.migracionstandingoffer.web;

import com.bbva.migracionstandingoffer.bonita.BonitaClient;
import com.bbva.migracionstandingoffer.bonita.BonitaSession;
import com.bbva.migracionstandingoffer.core.ContextoMigracion;
import com.bbva.migracionstandingoffer.flujo.MigracionStandingOfferService;
import com.bbva.migracionstandingoffer.flujo.RelacionMigradosStandingOffer;
import com.bbva.migracionstandingoffer.modelo.CasoRadicacionDto;
import com.bbva.migracionstandingoffer.modelo.ScoringContratoDto;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/migracion")
@CrossOrigin(origins = "*")
public class MigracionController {

    private final MigracionStandingOfferService service;
    private final BonitaClient bonitaClient;
    private final RelacionMigradosStandingOffer relacionMigrados;

    public MigracionController(MigracionStandingOfferService service,
                               BonitaClient bonitaClient,
                               RelacionMigradosStandingOffer relacionMigrados) {
        this.service = service;
        this.bonitaClient = bonitaClient;
        this.relacionMigrados = relacionMigrados;
    }

    @GetMapping("/diagnostico")
    public ResponseEntity<Map<String, Object>> diagnostico() {
        Map<String, Object> resp = new HashMap<>();
        try {
            BonitaSession s = bonitaClient.login();
            resp.put("estado", "OK");
            resp.put("userId", s.getUserId());
            resp.put("bonitaUrl", bonitaClient.baseUrl());
            resp.put("mensaje", "Conexión y autenticación con Bonita Community exitosa.");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("estado", "ERROR");
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * Endpoint principal para migrar un caso pasándole el número de caso de Enterprise.
     * Consulta la base de datos de Enterprise, arma el nuevo JSON y radica en Community.
     */
    @PostMapping("/caso-ee/{casoEE}")
    public ResponseEntity<ContextoMigracion> migrarPorCasoEE(
            @PathVariable("casoEE") String casoEE,
            @RequestParam(value = "etapaObjetivo", defaultValue = "RADICACION") String etapaObjetivo) {
        ContextoMigracion ctx = service.migrarPorNumeroCasoEE(casoEE, etapaObjetivo);
        if ("ERROR".equalsIgnoreCase(ctx.getEstadoFinal()) || "NO_ENCONTRADO".equalsIgnoreCase(ctx.getEstadoFinal())) {
            return ResponseEntity.status(ctx.getEstadoFinal().equals("NO_ENCONTRADO") ? 404 : 500).body(ctx);
        }
        return ResponseEntity.ok(ctx);
    }

    /**
     * Migración masiva por lote de números de casos existentes en Enterprise.
     */
    @PostMapping("/lote")
    public ResponseEntity<List<ContextoMigracion>> migrarLote(@RequestBody SolicitudLote request) {
        List<ContextoMigracion> resultados = service.migrarLoteCasosEE(request.getCasos(), request.getEtapaObjetivo());
        return ResponseEntity.ok(resultados);
    }

    /**
     * Consulta el histórico de casos migrados (casoEE -> caseIdCM).
     */
    @GetMapping("/relaciones")
    public ResponseEntity<Map<String, Map<String, String>>> obtenerRelaciones() {
        return ResponseEntity.ok(relacionMigrados.obtenerTodos());
    }

    /**
     * Migración manual pasando el body completo directamente.
     */
    @PostMapping("/caso")
    public ResponseEntity<ContextoMigracion> migrarCasoManual(@RequestBody SolicitudMigracion request) {
        ContextoMigracion ctx = service.migrarCaso(
                request.getCasoNegocio(),
                request.getPrecarga(),
                request.getScoring(),
                request.getEvaluacionSancion(),
                request.getVoBoPagador(),
                request.getFormalizacion(),
                request.getEtapaObjetivo()
        );

        if ("ERROR".equalsIgnoreCase(ctx.getEstadoFinal())) {
            return ResponseEntity.status(500).body(ctx);
        }
        return ResponseEntity.ok(ctx);
    }

    @Data
    public static class SolicitudMigracion {
        private String casoNegocio;
        private String etapaObjetivo;
        private CasoRadicacionDto precarga;
        private ScoringContratoDto scoring;
        private com.bbva.migracionstandingoffer.etapas.evaluacionsancion.EvaluacionSancionDto evaluacionSancion;
        private com.bbva.migracionstandingoffer.etapas.vobopagador.VoBoPagadorDto voBoPagador;
        private com.bbva.migracionstandingoffer.etapas.formalizacion.FormalizacionDto formalizacion;
    }

    @Data
    public static class SolicitudLote {
        private List<String> casos;
        private String etapaObjetivo; // "RADICACION", "SCORING", etc.
    }
}
