package py.edu.uc.lp3.avera_tplp3_2026.domain.exception;

public class EntidadInvalidaException extends MinecraftException {

    public EntidadInvalidaException(String mensaje) {
        super("ENTIDAD_INVALIDA", mensaje);
    }
}
