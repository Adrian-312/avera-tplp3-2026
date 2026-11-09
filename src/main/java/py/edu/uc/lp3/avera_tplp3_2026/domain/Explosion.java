package py.edu.uc.lp3.avera_tplp3_2026.domain;

import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadInvalidaException;

public record Explosion(Long origenId, String origen, int potencia) {

    public Explosion {
        if (origen == null || origen.isBlank()) {
            throw new EntidadInvalidaException("El origen de la explosión no puede estar vacío");
        }
        if (potencia <= 0) {
            throw new EntidadInvalidaException("La potencia de la explosión debe ser mayor que cero");
        }
    }
}
