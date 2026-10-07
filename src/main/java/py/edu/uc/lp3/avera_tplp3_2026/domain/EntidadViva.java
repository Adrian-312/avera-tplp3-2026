package py.edu.uc.lp3.avera_tplp3_2026.domain;

public abstract class EntidadViva {

    public static final int VIDA_MAXIMA = 20;

    private int vida;
    private final String nombre;
    private final double altura;
    private String ultimoOrigenDanio = "ninguno";

    // Constructor simple: vida completa y altura estándar
    protected EntidadViva(String nombre) {
        this(VIDA_MAXIMA, nombre, 1.0);
    }

    // Constructor completo: aquí viven todas las reglas
    protected EntidadViva(int vida, String nombre, double altura) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (vida < 0 || vida > VIDA_MAXIMA) {
            throw new IllegalArgumentException("La vida debe estar entre 0 y " + VIDA_MAXIMA);
        }
        if (!(altura > 0) || altura > 10) {
            throw new IllegalArgumentException("La altura debe ser mayor que 0 y no superar 10");
        }
        this.vida = vida;
        this.nombre = nombre.trim();
        this.altura = altura;
    }

    // SOBREESCRITURA: cada clase hija lo implementa a su manera
    public abstract String realizarAccionEspecial();

    // SOBRECARGA: misma acción, distinta lista de argumentos
    public void recibirDanio(int puntos) {
        if (puntos < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo");
        }
        vida = Math.max(0, vida - puntos);
    }

    public void recibirDanio(int puntos, String origen) {
        if (origen == null || origen.isBlank()) {
            throw new IllegalArgumentException("El origen del daño no puede estar vacío");
        }
        recibirDanio(puntos);
        this.ultimoOrigenDanio = origen.trim();
    }

    public void curar(int puntos) {
        if (puntos < 0) {
            throw new IllegalArgumentException("La curación no puede ser negativa");
        }
        vida = Math.min(VIDA_MAXIMA, vida + puntos);
    }

    public boolean isVivo() {
        return vida > 0;
    }

    public int getVida() {
        return vida;
    }

    public String getNombre() {
        return nombre;
    }

    public double getAltura() {
        return altura;
    }

    public String getUltimoOrigenDanio() {
        return ultimoOrigenDanio;
    }

    @Override
    public String toString() {
        return "EntidadViva{vida=" + vida + ", nombre='" + nombre + "', altura=" + altura + '}';
    }
}
