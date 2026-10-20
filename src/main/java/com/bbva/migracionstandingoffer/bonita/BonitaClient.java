package com.bbva.migracionstandingoffer.bonita;

import com.bbva.migracionstandingoffer.config.BonitaProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Component
public class BonitaClient {
    private static final Logger log = LoggerFactory.getLogger(BonitaClient.class);
    private static final ParameterizedTypeReference<List<FlowNodeDto>> FLOW_NODES = new ParameterizedTypeReference<List<FlowNodeDto>>() {};

    private final RestTemplate restTemplate;
    private final BonitaProperties properties;
    private volatile BonitaSession session;

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
            throw new IllegalStateException("Sesión Bonita sin user_id");
        }

        this.session = new BonitaSession(token, jsession, userId);
        log.info("Login Bonita exitoso: user={} userId={}", this.properties.getUsername(), userId);
        return this.session;
    }

    public String obtenerProcesoId(String processName, String version) {
        return withRelogin(s -> {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl() + "/API/bpm/process")
                    .queryParam("f", "name=" + processName)
                    .queryParam("f", "version=" + version)
                    .queryParam("p", "0")
                    .queryParam("c", "1")
                    .build().encode().toUriString();

            JsonNode list = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authHeaders(s)), JsonNode.class).getBody();
            if (list != null && list.isArray() && list.size() > 0) {
                return list.get(0).path("id").asText();
            }
            throw new IllegalStateException("No se encontró en Bonita el proceso '" + processName + "' versión '" + version + "'");
        });
    }

    public String instanciar(final String processId, final Object payload) {
        log.info("Radicando caso en Bonita CM: processId={}", processId);
        return withRelogin(s -> {
            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl() + "/API/bpm/process/" + processId + "/instantiation")
                    .queryParam("user", s.getUserId())
                    .build().encode().toUri();

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
                URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl() + "/API/bpm/humanTask")
                        .queryParam("f", "caseId=" + caseId)
                        .queryParam("f", "state=ready")
                        .queryParam("p", "0")
                        .queryParam("c", "20")
                        .build().encode().toUri();

                return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(authHeaders(s)), FLOW_NODES).getBody();
            });

            if (tasks != null) {
                for (FlowNodeDto task : tasks) {
                    if (taskName.equalsIgnoreCase(task.getName()) || taskName.equalsIgnoreCase(task.getDisplayName())) {
                        log.info("Tarea encontrada: id={} name={} state={} intento={}", task.getId(), task.getName(), task.getState(), i);
                        return task;
                    }
                }
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

    public void ejecutarTarea(final String taskId, final Object payload) {
        log.info("Ejecutando tarea taskId={} en Bonita CM", taskId);
        withRelogin(s -> {
            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl() + "/API/bpm/userTask/" + taskId + "/execution")
                    .queryParam("assign", "false")
                    .queryParam("user", s.getUserId())
                    .build().encode().toUri();

            restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(payload, jsonHeaders(s)), Void.class);
            return null;
        });
        log.info("Tarea taskId={} ejecutada exitosamente", taskId);
    }

    private <T> T withRelogin(Function<BonitaSession, T> action) {
        BonitaSession s = this.session;
        if (s == null) {
            s = login();
        }
        try {
            return action.apply(s);
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                log.warn("Sesión expirada en Bonita. Reintentando login...");
                s = login();
                return action.apply(s);
            }
            throw e;
        }
    }

    private HttpHeaders authHeaders(BonitaSession s) {
        HttpHeaders h = new HttpHeaders();
        h.add("X-Bonita-API-Token", s.getApiToken());
        h.add("Cookie", "JSESSIONID=" + s.getJsessionId() + "; X-Bonita-API-Token=" + s.getApiToken());
        return h;
    }

    private HttpHeaders jsonHeaders(BonitaSession s) {
        HttpHeaders h = authHeaders(s);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private static String cookieValue(String setCookie) {
        int eq = setCookie.indexOf('=');
        int semi = setCookie.indexOf(';');
        if (semi > eq && eq > 0) {
            return setCookie.substring(eq + 1, semi);
        }
        return eq > 0 ? setCookie.substring(eq + 1) : setCookie;
    }

    public String baseUrl() {
        return this.properties.getBaseUrl();
    }
}
