package com.bbva.migracionstandingoffer.bonita;

import com.bbva.migracionstandingoffer.config.BonitaProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class BonitaClient {
    private static final Logger log = LoggerFactory.getLogger(BonitaClient.class);
    private static final ParameterizedTypeReference<List<FlowNodeDto>> FLOW_NODES =
            new ParameterizedTypeReference<List<FlowNodeDto>>() {};

    private final RestTemplate restTemplate;
    private final BonitaProperties properties;

    private BonitaSession session;

    public BonitaClient(RestTemplate bonitaRestTemplate, BonitaProperties properties) {
        this.restTemplate = bonitaRestTemplate;
        this.properties = properties;
    }

    public synchronized BonitaSession login() {
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("username", this.properties.getUsername());
        form.add("password", this.properties.getPassword());
        form.add("redirect", "false");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String url = baseUrl() + "/loginservice";
        log.info("Iniciando login en Bonita: user={} url={}", this.properties.getUsername(), url);

        ResponseEntity<Void> response = this.restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(form, headers),
                Void.class
        );

        String token = null;
        String jsession = null;
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith("X-Bonita-API-Token=")) {
                    token = cookieValue(cookie);
                } else if (cookie.startsWith("JSESSIONID=")) {
                    jsession = cookieValue(cookie);
                }
            }
        }

        if (token == null || jsession == null) {
            throw new IllegalStateException("Login Bonita falló: no se recibieron token o JSESSIONID. Set-Cookie: " + cookies);
        }

        BonitaSession parcial = new BonitaSession(token, jsession, null);
        JsonNode sessionInfo = this.restTemplate.exchange(
                baseUrl() + "/API/system/session/unusedid",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(parcial)),
                JsonNode.class
        ).getBody();

        String userId = sessionInfo != null ? sessionInfo.path("user_id").asText(null) : null;
        if (userId == null || userId.isEmpty()) {
            throw new IllegalStateException("No se obtuvo user_id de Bonita: " + sessionInfo);
        }

        this.session = new BonitaSession(token, jsession, userId);
        log.info("Login Bonita exitoso: user={} userId={}", this.properties.getUsername(), userId);
        return this.session;
    }

    /**
     * Consulta el ID del proceso en Bonita por su nombre y versión.
     * En Bonita BPM, la URL de búsqueda por query parameter 'f' no debe ir doblemente codificada
     * como %3D, sino con el formato estándar /API/bpm/process?p=0&c=10&f=name=...&f=version=...
     */
    public String obtenerProcesoId(String processName, String version) {
        return withRelogin(s -> {
            try {
                // 1. Intento con URL literal construida directamente (como el cliente original de StandingOffer)
                String rawUrl = baseUrl() + "/API/bpm/process?p=0&c=10&o=displayName ASC&f=name=" + processName;
                if (version != null && !version.isEmpty()) {
                    rawUrl += "&f=version=" + version;
                }
                URI uri = URI.create(rawUrl.replace(" ", "%20"));

                JsonNode list = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(authHeaders(s)), JsonNode.class).getBody();
                if (list != null && list.isArray() && list.size() > 0) {
                    String id = list.get(0).path("id").asText();
                    log.info("Proceso Bonita encontrado: id={} name={} version={}", id, processName, version);
                    return id;
                }
            } catch (Exception e) {
                log.warn("Búsqueda con filtro exacto name/version falló: {}. Intentando búsqueda amplia...", e.getMessage());
            }

            // 2. Fallback: listar procesos y filtrar en memoria por nombre
            try {
                URI uriTodos = URI.create(baseUrl() + "/API/bpm/process?p=0&c=50");
                JsonNode todos = restTemplate.exchange(uriTodos, HttpMethod.GET, new HttpEntity<>(authHeaders(s)), JsonNode.class).getBody();
                if (todos != null && todos.isArray()) {
                    for (JsonNode proc : todos) {
                        String name = proc.path("name").asText("");
                        String ver = proc.path("version").asText("");
                        if (name.equalsIgnoreCase(processName)) {
                            if (version == null || version.isEmpty() || ver.equalsIgnoreCase(version)) {
                                String id = proc.path("id").asText();
                                log.info("Proceso Bonita encontrado por escaneo: id={} name={} version={}", id, name, ver);
                                return id;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("Fallo escaneando lista de procesos: {}", ex.getMessage());
            }

            throw new IllegalStateException("No se encontró en Bonita el proceso '" + processName + "' versión '" + version + "'");
        });
    }

    public String instanciar(final String processId, final Object payload) {
        log.info("Radicando caso en Bonita CM: processId={}", processId);
        return withRelogin(s -> {
            URI uri = URI.create(baseUrl() + "/API/bpm/process/" + processId + "/instantiation?user=" + s.getUserId());

            JsonNode body = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    new HttpEntity<>(payload, jsonHeaders(s)),
                    JsonNode.class
            ).getBody();

            String caseId = body != null ? body.path("caseId").asText(null) : null;
            if (caseId == null || caseId.isEmpty()) {
                throw new IllegalStateException("Instanciación en Bonita sin caseId en respuesta: " + body);
            }
            log.info("Caso radicado exitosamente en Bonita CM: caseId={}", caseId);
            return caseId;
        });
    }

    public FlowNodeDto esperarTarea(final String caseId, final String taskName) {
        log.info("Esperando tarea '{}' para caseId={}", taskName, caseId);
        int attempts = properties.getPollAttempts();
        long delay = properties.getPollDelayMs();

        for (int i = 1; i <= attempts; i++) {
            List<FlowNodeDto> tasks = withRelogin(s -> {
                URI uri = URI.create(baseUrl() + "/API/bpm/humanTask?p=0&c=20&f=caseId=" + caseId + "&f=state=ready");
                return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(authHeaders(s)), FLOW_NODES).getBody();
            });

            if (tasks != null && !tasks.isEmpty()) {
                StringBuilder nombres = new StringBuilder();
                for (FlowNodeDto t : tasks) {
                    if (nombres.length() > 0) nombres.append(", ");
                    nombres.append("'").append(t.getName()).append("' (").append(t.getId()).append(")");
                }
                log.info("Tareas listas encontradas en caseId={} (intento {}): [{}]", caseId, i, nombres);

                for (FlowNodeDto task : tasks) {
                    if (taskName.equalsIgnoreCase(task.getName()) || taskName.equalsIgnoreCase(task.getDisplayName())) {
                        log.info("Tarea esperada encontrada: id={} name={} state={} intento={}", task.getId(), task.getName(), task.getState(), i);
                        return task;
                    }
                }
            } else {
                log.debug("Aún no hay tareas 'ready' en caseId={} (intento {}/{})", caseId, i, attempts);
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Espera de tarea interrumpida", e);
            }
        }

        throw new IllegalStateException("Tiempo de espera agotado buscando tarea '" + taskName + "' en caseId=" + caseId);
    }

    public void asignarTarea(final String taskId, final String userId) {
        log.info("Asignando tarea taskId={} al usuario userId={}", taskId, userId);
        withRelogin(s -> {
            URI uri = URI.create(baseUrl() + "/API/bpm/humanTask/" + taskId);
            Map<String, Object> body = Collections.singletonMap("assigned_id", userId != null ? userId : s.getUserId());
            restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(body, jsonHeaders(s)), Void.class);
            return null;
        });
    }

    public void ejecutarTarea(final String taskId, final Object payload) {
        log.info("Ejecutando tarea taskId={} en Bonita CM", taskId);
        withRelogin(s -> {
            // Asignar primero la tarea al usuario logueado por si está en la bandeja compartida sin asignar
            try {
                URI assignUri = URI.create(baseUrl() + "/API/bpm/humanTask/" + taskId);
                Map<String, Object> assignBody = Collections.singletonMap("assigned_id", s.getUserId());
                restTemplate.exchange(assignUri, HttpMethod.PUT, new HttpEntity<>(assignBody, jsonHeaders(s)), Void.class);
                log.debug("Tarea taskId={} asignada previamente al usuario={}", taskId, s.getUserId());
            } catch (Exception e) {
                log.warn("No se pudo pre-asignar tarea taskId={} (puede que ya esté asignada): {}", taskId, e.getMessage());
            }

            URI uri = URI.create(baseUrl() + "/API/bpm/userTask/" + taskId + "/execution?assign=true&user=" + s.getUserId());
            restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(payload, jsonHeaders(s)), Void.class);
            return null;
        });
        log.info("Tarea taskId={} ejecutada exitosamente", taskId);
    }

    private <T> T withRelogin(Function<BonitaSession, T> action) {
        BonitaSession act = this.session;
        if (act == null) {
            act = login();
        }
        try {
            return action.apply(act);
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            log.warn("Sesión Bonita expirada (HTTP {}), reautenticando...", e.getStatusCode());
            this.session = login();
            return action.apply(this.session);
        }
    }

    private HttpHeaders authHeaders(BonitaSession s) {
        HttpHeaders h = new HttpHeaders();
        h.set("X-Bonita-API-Token", s.getApiToken());
        h.set("Cookie", "JSESSIONID=" + s.getJsessionId() + "; X-Bonita-API-Token=" + s.getApiToken());
        h.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return h;
    }

    private HttpHeaders jsonHeaders(BonitaSession s) {
        HttpHeaders h = authHeaders(s);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private String cookieValue(String cookieHeader) {
        int eq = cookieHeader.indexOf('=');
        int semi = cookieHeader.indexOf(';');
        if (semi == -1) {
            semi = cookieHeader.length();
        }
        return cookieHeader.substring(eq + 1, semi).trim();
    }

    public String baseUrl() {
        String url = this.properties.getBaseUrl();
        if (url != null && url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
