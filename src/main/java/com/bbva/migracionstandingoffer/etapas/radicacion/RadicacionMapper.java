package com.bbva.migracionstandingoffer.etapas.radicacion;

import com.bbva.migracionstandingoffer.modelo.CasoRadicacionDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mapeador del contrato de Radicación para Standing Offer en Bonita BPM.
 * Cada campo del contrato debe tener la estructura { valor: "...", descripcion: "..." },
 * a excepción de 'consecutivo' que va aplanado como valor directo para cumplir con el contrato de entrada.
 */
@Component
public class RadicacionMapper {

    public Map<String, Object> construirContrato(CasoRadicacionDto caso) {
        Map<String, Object> inner = new LinkedHashMap<>();

        // 1. Campo 'consecutivo': Bonita lo espera directo en el contrato (aplanado)
        String consecutivoValor = caso.getConsecutivo() != null && !caso.getConsecutivo().isEmpty()
                ? caso.getConsecutivo()
                : (caso.getNumeroConsecutivo() != null && !caso.getNumeroConsecutivo().isEmpty() ? caso.getNumeroConsecutivo() : "1");
        inner.put("consecutivo", consecutivoValor);

        // 2. Todos los campos de negocio que el script Groovy 'inicializarObjeto()' de Bonita evalúa como .valor
        inner.put("nombreCliente", campo(caso.getNombreCliente(), "Nombre de Cliente"));
        inner.put("tipoDocumento", campo(caso.getTipoDocumento(), "tipo de documento"));
        inner.put("numeroDocumento", campo(caso.getNumeroDocumento(), "Número de documento"));
        inner.put("digitoVerificacion", campo(safe(caso.getDigitoVerificacion(), "0"), "digito de verificación"));
        inner.put("correoCliente", campo(caso.getCorreoCliente(), "Correo de cliente"));
        inner.put("numeroCelular", campo(caso.getNumeroCelular(), "Número celular"));
        inner.put("numeroAltamiraCliente", campo(caso.getNumeroAltamiraCliente(), "Numero Altamira cliente"));
        inner.put("fechaVigenciaCampana", campo(caso.getFechaVigenciaCampana(), "Fecha vigencia Campaña"));
        inner.put("fechaHoraSolicitud", campo(caso.getFechaHoraSolicitud(), "Fecha y hora de Solicitud"));
        inner.put("numeroProducto", campo(caso.getNumeroProducto(), "Numero Producto "));
        inner.put("subProducto", campo(caso.getSubProducto(), "Subproducto"));
        inner.put("codigoSubproducto", campo(caso.getCodigoSubproducto(), "código Subproducto"));
        inner.put("marcaOneClic", campo(caso.getMarcaOneClic(), "Marca one Clic"));
        inner.put("valorNuevaOperacion", campo(caso.getValorNuevaOperacion(), "Valor nueva Operación"));
        inner.put("valorCuotaNuevaOperacion", campo(caso.getValorCuotaNuevaOperacion(), "Valor cuota nueva Operación"));
        inner.put("tasaCreditoNuevo", campo(caso.getTasaCreditoNuevo(), "Tasa Crédito Nuevo"));
        inner.put("plazo", campo(caso.getPlazo(), "Plazo "));
        inner.put("cuentaAbono", campo(caso.getCuentaAbono(), "Cuenta de abono"));
        inner.put("canal", campo(caso.getCanal(), "Canal"));
        inner.put("oficinaSegmentacion", campo(caso.getOficinaSegmentacion(), "Oficina Segmentación"));
        inner.put("creRetanquearLibranza", campo(caso.getCreRetanquearLibranza(), "Crédito a retanquear (libranza)"));
        inner.put("creRetanquearConsumo", campo(caso.getCreRetanquearConsumo(), "Crédito a retanquear (consumo)"));
        inner.put("creRetanquearHipoteca", campo(caso.getCreRetanquearHipoteca(), "Crédito a retanquear (hipotecario)"));
        inner.put("creRetanquearVehiculo", campo(caso.getCreRetanquearVehiculo(), "Crédito a retanquear (vehículo)"));
        inner.put("creRetanquearLeasing", campo(caso.getCreRetanquearLeasing(), "Crédito a retanquear (Leasing Habitacional)"));
        inner.put("codigoConvenio", campo(caso.getCodigoConvenio(), "Código de convenio"));
        inner.put("nombreConvenio", campo(caso.getNombreConvenio(), "nombre del convenio"));
        inner.put("tipoConvenio", campo(safe(caso.getTipoConvenio(), "3"), "tipo Convenio"));
        inner.put("numeroConsecutivo", campo(safe(caso.getNumeroConsecutivo(), "00001"), "Número de Consecutivo"));
        inner.put("direccionIp", campo(caso.getDireccionIp(), "Dirección IP"));
        inner.put("descripcion", campo(safe(caso.getDescripcion(), "T"), "Descripción"));

        // Campos auxiliares que el script Groovy también valida
        inner.put("origenGestion", campo("Auto", "Origen Gestión"));
        inner.put("fuerzaDeVenta", campo("", "Fuerza de Ventas"));
        inner.put("destino", campo("", "Destino"));

        Map<String, Object> root = new HashMap<>();
        root.put("contrato", inner);
        return root;
    }

    private Map<String, String> campo(String valor, String descripcion) {
        Map<String, String> obj = new LinkedHashMap<>();
        obj.put("valor", valor != null ? valor : "");
        obj.put("descripcion", descripcion != null ? descripcion : "");
        return obj;
    }

    private String safe(String str, String defaultVal) {
        return (str != null && !str.trim().isEmpty()) ? str : defaultVal;
    }
}
