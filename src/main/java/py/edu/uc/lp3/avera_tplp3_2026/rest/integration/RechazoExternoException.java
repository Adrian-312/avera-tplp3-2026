package py.edu.uc.lp3.avera_tplp3_2026.rest.integration;

public class RechazoExternoException extends IntegracionExternaException {

    private final int statusExterno;
    private final String codigoExterno;

    public RechazoExternoException(String companero, int statusExterno, String codigoExterno, String motivo) {
        super(companero, "Rechazo de " + companero + ": " + motivo);
        this.statusExterno = statusExterno;
        this.codigoExterno = codigoExterno;
    }

    public int getStatusExterno() {
        return statusExterno;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }
}
