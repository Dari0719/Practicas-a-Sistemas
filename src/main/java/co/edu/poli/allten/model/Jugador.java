package co.edu.poli.allten.model;

/**
 * Representa al jugador asociado a una sesión de juego.
 */
public class Jugador {

    // El identificador puede asignarse al guardar o recuperar el jugador de la base de datos.
    private int id;
    private final String apodo;

    /**
     * Crea un jugador nuevo que todavía no tiene identificador persistido.
     */
    public Jugador(String apodo) {
        this(0, apodo);
    }

    /**
     * Crea un jugador con identificador, por ejemplo, al cargarlo de la base de datos.
     */
    public Jugador(int id, String apodo) {
        if (apodo == null || apodo.isBlank()) {
            throw new IllegalArgumentException("El apodo no puede estar vacío.");
        }
        this.id = id;
        this.apodo = apodo.trim();
    }

    /** Devuelve el identificador del jugador. */
    public int getId() {
        return id;
    }

    /** Permite asignar el identificador generado al persistir el jugador. */
    public void setId(int id) {
        this.id = id;
    }

    /** Devuelve el apodo normalizado, sin espacios al inicio o al final. */
    public String getApodo() {
        return apodo;
    }
}
