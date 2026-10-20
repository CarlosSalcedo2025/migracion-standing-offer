package com.bbva.migracionstandingoffer.etapas.vobopagador;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class VoBoPagadorMapper {

    /**
     * Enriquece el contrato acumulado con los datos de VoBo Pagador
     * según lo capturado en VoBo Pagador.har.
     */
    public Map<String, Object> enriquecerContrato(Map<String, Object> contratoAcumulado, VoBoPagadorDto dto) {
        Map<String, Object> data = new LinkedHashMap<>(contratoAcumulado);

        data.put("cuotaContratoFormalizacion", dto != null && dto.getCuotaContratoFormalizacion() != null ? dto.getCuotaContratoFormalizacion() : 30);
        data.put("cuotaVoBo", dto != null && dto.getCuotaVoBo() != null ? dto.getCuotaVoBo() : "30000");
        data.put("valorMaximoTotalCredito", dto != null && dto.getValorMaximoTotalCredito() != null ? dto.getValorMaximoTotalCredito() : "61,800,000.00");
        data.put("cuotaRetanqueo", dto != null && dto.getCuotaRetanqueo() != null ? dto.getCuotaRetanqueo() : "30");
        data.put("ValorCuotaRecoger", dto != null && dto.getValorCuotaRecoger() != null ? dto.getValorCuotaRecoger() : "30");

        data.put("observaciones", dto != null && dto.getObservaciones() != null ? dto.getObservaciones() : "ok");
        data.put("proximoPaso", dto != null && dto.getProximoPaso() != null ? dto.getProximoPaso() : "Continuar");

        Map<String, Object> root = new HashMap<>();
        root.put("contrato", data);
        return root;
    }
}
