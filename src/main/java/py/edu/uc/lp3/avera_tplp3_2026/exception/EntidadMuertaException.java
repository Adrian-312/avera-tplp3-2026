package py.edu.uc.lp3.avera_tplp3_2026.exception;

// Se lanza cuando se intenta modificar una entidad que ya murió: la muerte es un estado final.
public class EntidadMuertaException extends MinecraftException {

    public EntidadMuertaException(String mensaje) {
        super("ENTIDAD_MUERTA", mensaje);
    }
}
