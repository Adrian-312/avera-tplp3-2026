package py.edu.uc.lp3.avera_tplp3_2026.domain.exception;

public class EntidadNoEncontradaException extends MinecraftException {

    public EntidadNoEncontradaException(String mensaje) {
        super("ENTIDAD_NO_ENCONTRADA", mensaje);
    }
}
