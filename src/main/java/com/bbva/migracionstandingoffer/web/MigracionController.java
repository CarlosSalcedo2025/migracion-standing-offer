package com.bbva.migracionstandingoffer.web;

import com.bbva.migracionstandingoffer.bonita.BonitaClient;
import com.bbva.migracionstandingoffer.bonita.BonitaSession;
import com.bbva.migracionstandingoffer.comun.MigracionConstantes;
import com.bbva.migracionstandingoffer.core.ContextoMigracion;
import com.bbva.migracionstandingoffer.flujo.MigracionStandingOfferService;
import com.bbva.migracionstandingoffer.flujo.RelacionMigradosStandingOffer;
import com.bbva.migracionstandingoffer.seguridad.InputSanitizer;
import com.bbva.migracionstandingoffer.web.dto.DiagnosticoResponseDto;
import com.bbva.migracionstandingoffer.web.dto.SolicitudLoteDto;
import com.bbva.migracionstandingoffer.web.dto.SolicitudMigracionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para orquestar la migración de casos de Standing Offer.
 * Protegido contra ataques de Cross-Site Scripting (XSS), inyección y Content-Sniffing.
 */
@RestController
@RequestMapping(value = "/migracion", produces = MediaType.APPLICATION_JSON_VALUE)
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

    /**
     * Valida conectividad y sesión activa contra la API de Bonita Community.
     */
    @GetMapping("/diagnostico")
    public ResponseEntity<DiagnosticoResponseDto> diagnostico() {
        try {
            BonitaSession session = bonitaClient.login();
            return ResponseEntity.ok(DiagnosticoResponseDto.ok(
                    session.getUserId(),
                    bonitaClient.baseUrl(),
                    "Conexión y autenticación con Bonita Community exitosa."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DiagnosticoResponseDto.error("Fallo de comunicación con Bonita"));
        }
    }

    /**
     * Endpoint principal para migrar un caso pasándole el número de caso de Enterprise.
     * Por defecto ejecuta el FLUJO COMPLETO (todas las 5 etapas hasta Formalización).
     * Opcionalmente se puede indicar 'etapaObjetivo' para detenerse en una etapa intermedia.
     */
    @PostMapping("/caso-ee/{casoEE}")
    public ResponseEntity<ContextoMigracion> migrarPorCasoEE(
            @PathVariable("casoEE") String casoEE,
            @RequestParam(value = "etapaObjetivo", defaultValue = MigracionConstantes.ETAPA_FORMALIZACION) String etapaObjetivo) {

        String casoLimpio = InputSanitizer.sanitizarIdentificador(casoEE);
        String etapaLimpia = InputSanitizer.sanitizarIdentificador(etapaObjetivo);

        ContextoMigracion ctx = service.migrarPorNumeroCasoEE(casoLimpio, etapaLimpia);
        if (MigracionConstantes.ESTADO_NO_ENCONTRADO.equalsIgnoreCase(ctx.getEstadoFinal())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ctx);
        }
        if (MigracionConstantes.ESTADO_ERROR.equalsIgnoreCase(ctx.getEstadoFinal())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ctx);
        }
        return ResponseEntity.ok(ctx);
    }

    /**
     * Migración masiva por lote de números de casos existentes en Enterprise.
     * Sanitiza cada identificador de caso antes de procesarlo concurrentemente.
     */
    @PostMapping(value = "/lote", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextoMigracion>> migrarLote(@RequestBody SolicitudLoteDto request) {
        if (request == null || request.getCasos() == null || request.getCasos().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<String> casosSanitizados = new ArrayList<>(request.getCasos().size());
        for (String caso : request.getCasos()) {
            casosSanitizados.add(InputSanitizer.sanitizarIdentificador(caso));
        }

        String etapaLimpia = request.getEtapaObjetivo() != null 
                ? InputSanitizer.sanitizarIdentificador(request.getEtapaObjetivo()) 
                : MigracionConstantes.ETAPA_RADICACION;

        List<ContextoMigracion> resultados = service.migrarLoteCasosEE(casosSanitizados, etapaLimpia);
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
    @PostMapping(value = "/caso", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextoMigracion> migrarCasoManual(@RequestBody SolicitudMigracionDto request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        String casoLimpio = request.getCasoNegocio() != null 
                ? InputSanitizer.sanitizarIdentificador(request.getCasoNegocio()) 
                : null;

        String etapaLimpia = request.getEtapaObjetivo() != null 
                ? InputSanitizer.sanitizarIdentificador(request.getEtapaObjetivo()) 
                : MigracionConstantes.ETAPA_RADICACION;

        ContextoMigracion ctx = service.migrarCaso(
                casoLimpio,
                request.getPrecarga(),
                request.getScoring(),
                request.getEvaluacionSancion(),
                request.getVoBoPagador(),
                request.getFormalizacion(),
                etapaLimpia
        );

        if (MigracionConstantes.ESTADO_ERROR.equalsIgnoreCase(ctx.getEstadoFinal())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ctx);
        }
        return ResponseEntity.ok(ctx);
    }

    /**
     * Manejador de excepciones de validación para entradas no permitidas.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DiagnosticoResponseDto> manejarEntradaInvalida(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(DiagnosticoResponseDto.error(ex.getMessage()));
    }
}
