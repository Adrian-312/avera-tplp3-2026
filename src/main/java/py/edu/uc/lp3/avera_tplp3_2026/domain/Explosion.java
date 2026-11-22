package py.edu.uc.lp3.avera_tplp3_2026.domain;

// Dato de una explosión. Las validaciones las hace el SimulacionService.
public record Explosion(Long origenId, String origen, int potencia) {
}
