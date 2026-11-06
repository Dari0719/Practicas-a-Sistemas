package co.edu.poli.allten.model;

/**
 * Posibles veredictos al validar la expresión matemática que escribe el jugador.
 *
 * <p>Lo produce {@link ValidadorExpresion#validarExpresion}, y luego lo
 * reenvían {@link Ronda#resolver} y {@link SesionJuego#procesarRespuesta}.
 * Finalmente lo consume {@code PantallaJuegoController}, que muestra una alerta
 * de éxito si el valor es {@link #VALIDO} o un mensaje de error distinto para
 * cada uno de los demás casos.</p>
 *
 * <p>Se usa un enum en lugar de lanzar excepciones porque una expresión
 * incorrecta es algo normal durante el juego (los intentos son ilimitados) y no
 * una situación excepcional del programa.</p>
 *
 * @see ValidadorExpresion
 * @see SesionJuego#procesarRespuesta(String)
 */
public enum ResultadoValidacion {

    /**
     * La expresión es correcta: usa los cuatro números exactamente una vez, solo
     * emplea operaciones permitidas en el modo actual y su resultado corresponde
     * a un objetivo todavía pendiente. Al recibir este valor, el objetivo se
     * marca como resuelto.
     */
    VALIDO,

    /**
     * Los números de la expresión no coinciden con los cuatro generados para la
     * partida: falta alguno, sobra alguno, se repite uno que no estaba repetido,
     * o hay un número demasiado grande para convertirse a entero. También se
     * devuelve si la lista de números disponibles es nula, vacía o contiene nulos.
     */
    NUMEROS_INVALIDOS,

    /**
     * La expresión no se puede evaluar o no cumple las reglas del modo. Cubre
     * los siguientes casos:
     * <ul>
     *   <li>Texto vacío o nulo.</li>
     *   <li>Errores de sintaxis, como un paréntesis sin cerrar o un operador
     *       sin operando.</li>
     *   <li>División por cero o raíz cuadrada de un número negativo.</li>
     *   <li>Un resultado que no es finito.</li>
     *   <li>Usar {@code ^} o {@code √} en modo {@link ModoJuego#NORMAL}.</li>
     *   <li>En modo {@link ModoJuego#HARDCORE}, usar más de una raíz cuadrada o
     *       no incluir ninguna operación avanzada no trivial.</li>
     * </ul>
     */
    OPERACION_INVALIDA,

    /**
     * La expresión es sintácticamente válida y usa bien los números, pero su
     * resultado no corresponde a ninguno de los objetivos pendientes. Si el valor
     * no es un objetivo del 1 al 10, {@code SesionJuego} devuelve este veredicto
     * aunque la expresión esté bien escrita. El intento igual cuenta en el
     * total de intentos de la partida.
     */
    RESULTADO_INCORRECTO,

    /**
     * El resultado de la expresión corresponde a un objetivo que el jugador ya
     * resolvió antes, o la partida ya terminó y no admite más respuestas. En
     * ambos casos el jugador no gana nada nuevo.
     */
    OBJETIVO_YA_RESUELTO
}
