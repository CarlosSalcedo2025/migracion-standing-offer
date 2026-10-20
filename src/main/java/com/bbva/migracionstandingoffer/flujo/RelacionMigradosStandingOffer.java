package com.bbva.migracionstandingoffer.flujo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class RelacionMigradosStandingOffer {
    private static final Logger log = LoggerFactory.getLogger(RelacionMigradosStandingOffer.class);
    private static final String ARCHIVO = "relacion-ee-cm-standing-offer.json";
    private static final TypeReference<LinkedHashMap<String, Map<String, String>>> TIPO =
            new TypeReference<LinkedHashMap<String, Map<String, String>>>() {};

    @Value("${migracion.resumen-dir:logs/migracion-standing-offer}")
    private String resumenDir;

    private final ObjectMapper mapper;

    public RelacionMigradosStandingOffer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public synchronized void registrar(String casoEE, String caseIdCM, String estado, String mensaje) {
        Map<String, Map<String, String>> todo = leer();
        Map<String, String> info = new LinkedHashMap<>();
        info.put("casoEE", casoEE);
        info.put("caseIdCM", caseIdCM);
        info.put("estado", estado);
        info.put("mensaje", mensaje);
        info.put("fecha", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        todo.put(casoEE, info);
        escribir(todo);
        log.info("Relación registrada: casoEE={} -> caseIdCM={} estado={}", casoEE, caseIdCM, estado);
    }

    public synchronized Map<String, String> obtenerPorCasoEE(String casoEE) {
        return leer().get(casoEE);
    }

    public synchronized Map<String, Map<String, String>> obtenerTodos() {
        return leer();
    }

    private File getArchivo() {
        return new File(resumenDir, ARCHIVO);
    }

    private Map<String, Map<String, String>> leer() {
        File f = getArchivo();
        if (!f.isFile()) {
            return new LinkedHashMap<>();
        }
        try {
            Map<String, Map<String, String>> data = mapper.readValue(f, TIPO);
            return data != null ? data : new LinkedHashMap<>();
        } catch (IOException e) {
            log.warn("No se pudo leer archivo de relaciones {}: {}", f.getAbsolutePath(), e.getMessage());
            return new LinkedHashMap<>();
        }
    }

    private void escribir(Map<String, Map<String, String>> data) {
        File f = getArchivo();
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try {
            mapper.copy().enable(SerializationFeature.INDENT_OUTPUT).writeValue(f, data);
        } catch (IOException e) {
            log.error("No se pudo escribir archivo de relaciones {}: {}", f.getAbsolutePath(), e.getMessage());
        }
    }
}
