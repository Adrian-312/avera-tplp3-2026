package py.edu.uc.lp3.avera_tplp3_2026.constants;

// Valores fijos del juego. Viven acá para no repetirlos en el dominio ni en los services.
public final class MinecraftConstants {

    private MinecraftConstants() {
    }

    // Entidades vivas
    public static final int VIDA_MAXIMA = 20;
    public static final double ALTURA_MAXIMA = 10.0;
    public static final double ALTURA_DEFECTO = 1.0;
    public static final double ALTURA_JUGADOR_DEFECTO = 1.8;
    public static final String ORIGEN_DANIO_NINGUNO = "ninguno";

    // Jugador
    public static final int HAMBRE_MAXIMA = 20;
    public static final int ESCUDO_ABSORCION = 5;
    public static final double ALTURA_SEGURA_CAIDA = 3.0;
    public static final int PODER_EXPLOSIVO_POR_TNT = 4;
    public static final String ITEM_ESCUDO = "escudo";
    public static final String ITEM_TNT = "TNT";

    // Monstruo
    public static final int POTENCIA_EXPLOSIVA_CREEPER = 10;
    public static final int POTENCIA_EXPLOSIVA_MAXIMA = 50;

    // Tipos de entidad (campo "tipo" del JSON y columna discriminadora)
    public static final String TIPO_JUGADOR = "jugador";
    public static final String TIPO_MONSTRUO = "monstruo";
    public static final String TIPO_ANIMAL = "animal";
    public static final String TIPO_PLANTA = "planta";
    public static final String TIPO_NO_JUGABLE = "no_jugable";
}
