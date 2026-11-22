package py.edu.uc.lp3.avera_tplp3_2026.rest.integration;

public class IntegracionExternaException extends RuntimeException {

    private final String companero;

    public IntegracionExternaException(String companero, String mensaje) {
        super(mensaje);
        this.companero = companero;
    }

    public String getCompanero() {
        return companero;
    }
}
