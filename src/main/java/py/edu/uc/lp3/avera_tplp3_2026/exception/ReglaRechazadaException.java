package py.edu.uc.lp3.avera_tplp3_2026.exception;

public class ReglaRechazadaException extends MinecraftException {

    public ReglaRechazadaException(String mensaje) {
        super("REGLA_RECHAZADA", mensaje);
    }
}
