package co.edu.poli.allten.model;

/**
 * Representa el estado del ciclo de vida de una partida ({@link SesionJuego}).
 *
 * <p>Una partida siempre nace en {@link #EN_CURSO} y termina en uno de los dos
 * estados finales: {@link #COMPLETADO} o {@link #ABANDONADO}. Una vez que sale
 * de {@code EN_CURSO} ya no puede volver a ese estado.</p>
 *
 * <p>El nombre de cada constante (por medio de {@code name()}) es el valor que se
 * guarda en la columna {@code estado} de la tabla de partidas en MySQL, y el
 * ranking filtra por el valor {@code 'COMPLETADO'}. Por eso, si se renombra
 * alguna constante, también hay que actualizar la base de datos y sus consultas.</p>
 *
 * @see SesionJuego
 */
public enum EstadoJuego {

    /**
     * La partida está activa: el cronómetro corre y el jugador todavía puede
     * enviar expresiones. Es el estado inicial de toda sesión nueva.
     */
    EN_CURSO,

    /**
     * El jugador resolvió los diez objetivos (del 1 al 10). Solo las partidas
     * en este estado se tienen en cuenta para el ranking.
     */
    COMPLETADO,

    /**
     * El jugador terminó o salió de la partida sin resolver los diez objetivos.
     * Se registra, pero no entra en el ranking.
     */
    ABANDONADO
}
