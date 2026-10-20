package com.bbva.migracionstandingoffer.etapas.radicacion;

import com.bbva.migracionstandingoffer.modelo.CasoRadicacionDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RadicacionMapper {

    public Map<String, Object> construirContrato(CasoRadicacionDto caso) {
        Map<String, Object> inner = new LinkedHashMap<>();
        inner.put("nombreCliente", safe(caso.getNombreCliente()));
        inner.put("tipoDocumento", safe(caso.getTipoDocumento()));
        inner.put("numeroDocumento", safe(caso.getNumeroDocumento()));
        inner.put("digitoVerificacion", safe(caso.getDigitoVerificacion(), "0"));
        inner.put("correoCliente", safe(caso.getCorreoCliente()));
        inner.put("numeroCelular", safe(caso.getNumeroCelular()));
        inner.put("numeroAltamiraCliente", safe(caso.getNumeroAltamiraCliente()));
        inner.put("fechaVigenciaCampana", safe(caso.getFechaVigenciaCampana()));
        inner.put("fechaHoraSolicitud", safe(caso.getFechaHoraSolicitud()));
        inner.put("numeroProducto", safe(caso.getNumeroProducto()));
        inner.put("subProducto", safe(caso.getSubProducto()));
        inner.put("codigoSubproducto", safe(caso.getCodigoSubproducto()));
        inner.put("marcaOneClic", safe(caso.getMarcaOneClic()));
        inner.put("valorNuevaOperacion", safe(caso.getValorNuevaOperacion()));
        inner.put("valorCuotaNuevaOperacion", safe(caso.getValorCuotaNuevaOperacion()));
        inner.put("tasaCreditoNuevo", safe(caso.getTasaCreditoNuevo()));
        inner.put("plazo", safe(caso.getPlazo()));
        inner.put("cuentaAbono", safe(caso.getCuentaAbono()));
        inner.put("canal", safe(caso.getCanal()));
        inner.put("oficinaSegmentacion", safe(caso.getOficinaSegmentacion()));
        inner.put("creRetanquearLibranza", safe(caso.getCreRetanquearLibranza()));
        inner.put("creRetanquearConsumo", safe(caso.getCreRetanquearConsumo()));
        inner.put("creRetanquearHipoteca", safe(caso.getCreRetanquearHipoteca()));
        inner.put("creRetanquearVehiculo", safe(caso.getCreRetanquearVehiculo()));
        inner.put("creRetanquearLeasing", safe(caso.getCreRetanquearLeasing()));
        inner.put("codigoConvenio", safe(caso.getCodigoConvenio()));
        inner.put("nombreConvenio", safe(caso.getNombreConvenio()));
        inner.put("tipoConvenio", safe(caso.getTipoConvenio(), "1"));
        inner.put("numeroConsecutivo", safe(caso.getNumeroConsecutivo(), "00001"));
        inner.put("direccionIp", safe(caso.getDireccionIp()));
        inner.put("descripcion", safe(caso.getDescripcion(), "T"));

        Map<String, Object> root = new HashMap<>();
        root.put("contrato", inner);
        return root;
    }

    private String safe(String s) {
        return s != null ? s : "";
    }

    private String safe(String s, String def) {
        return (s != null && !s.trim().isEmpty()) ? s : def;
    }
}
