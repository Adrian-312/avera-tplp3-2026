package py.edu.uc.lp3.avera_tplp3_2026.domain.exception;

public class MinecraftException extends RuntimeException {

    private final String codigo;

    protected MinecraftException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    protected MinecraftException(String codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
