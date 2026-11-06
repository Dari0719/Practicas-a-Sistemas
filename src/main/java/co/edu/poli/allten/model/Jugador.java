package co.edu.poli.allten.model;

/**
 * Representa a una persona que juega All Ten, identificada por su apodo.
 *
 * <p>Es una clase del modelo sin lógica de juego: solo guarda el identificador
 * de la base de datos y el apodo. Se relaciona con la tabla {@code jugador} de
 * MySQL, donde el apodo es único.</p>
 *
 * <p>El ciclo de vida típico es el siguiente:</p>
 * <ol>
 *   <li>La pantalla de inicio crea el jugador con {@link #Jugador(String)}.
 *       Todavía no existe en la base de datos, así que su {@code id} vale 0.</li>
 *   <li>Al terminar la partida, {@code JugadorDAO.guardar} lo inserta (o
 *       recupera el registro si el apodo ya existía) y le asigna el
 *       identificador real con {@link #setId(int)}.</li>
 *   <li>{@code SesionJuegoDAO} exige un {@code id} mayor que 0 para poder
 *       guardar la partida asociada a este jugador.</li>
 * </ol>
 *
 * <p>El apodo es inmutable: se valida y limpia una sola vez al construir el
 * objeto y después no puede cambiar.</p>
 *
 * @see SesionJuego
 */
public class Jugador {

    /**
     * Identificador del jugador en la base de datos. El valor 0 significa que
     * todavía no ha sido guardado. No es {@code final} porque lo asigna la capa
     * de datos después de la inserción.
     */
    private int id;

    /**
     * Apodo del jugador, sin espacios al inicio ni al final. Es {@code final}
     * porque no cambia durante la vida del objeto.
     */
    private final String apodo;

    /**
     * Crea un jugador nuevo que todavía no existe en la base de datos, con
     * {@code id} igual a 0. Es el constructor que usa la pantalla de inicio.
     *
     * @param apodo nombre con el que se mostrará al jugador; no puede ser nulo
     *              ni estar en blanco
     * @throws IllegalArgumentException si el apodo es nulo o está en blanco
     */
    public Jugador(String apodo) {
        this(0, apodo);
    }

    /**
     * Crea un jugador con un identificador conocido. Se usa al reconstruir un
     * jugador leído de la base de datos, por ejemplo en
     * {@code JugadorDAO.buscarPorApodo}, y al pasar el jugador de una pantalla a
     * otra conservando su {@code id}.
     *
     * @param id    identificador en la base de datos (0 si aún no se ha guardado)
     * @param apodo nombre del jugador; no puede ser nulo ni estar en blanco.
     *              Se le quitan los espacios del inicio y del final.
     * @throws IllegalArgumentException si el apodo es nulo o está en blanco
     */
    public Jugador(int id, String apodo) {
        if (apodo == null || apodo.isBlank()) {
            throw new IllegalArgumentException("El apodo no puede estar vacío.");
        }
        this.id = id;
        this.apodo = apodo.trim();
    }

    /**
     * Devuelve el identificador del jugador en la base de datos.
     *
     * @return el {@code id} asignado, o 0 si el jugador aún no se ha guardado
     */
    public int getId() {
        return id;
    }

    /**
     * Asigna el identificador del jugador. Lo llama {@code JugadorDAO.guardar}
     * después de insertarlo o de recuperar su registro existente en MySQL.
     *
     * @param id identificador generado o recuperado de la base de datos
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Devuelve el apodo del jugador ya limpio de espacios laterales. Es el texto
     * que se muestra en la pantalla de resumen y en el ranking.
     *
     * @return el apodo del jugador
     */
    public String getApodo() {
        return apodo;
    }
}
