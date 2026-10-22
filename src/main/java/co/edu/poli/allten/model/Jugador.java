package co.edu.poli.allten.model;

public class Jugador {

    private int id;
    private final String apodo;

    public Jugador(String apodo) {
        this(0, apodo);
    }

    public Jugador(int id, String apodo) {
        if (apodo == null || apodo.isBlank()) {
            throw new IllegalArgumentException("El apodo no puede estar vacío.");
        }
        this.id = id;
        this.apodo = apodo.trim();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApodo() {
        return apodo;
    }
}
