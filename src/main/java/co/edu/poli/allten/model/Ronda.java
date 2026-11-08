package co.edu.poli.allten.model;

import java.util.List;

/**
 * Representa uno de los diez objetivos de una partida (el numero 1, el 2, ...,
 * hasta el 10). Cada Ronda sabe cual es su numero objetivo, si ya fue resuelta
 * y cual fue la ultima expresion que el jugador intento para resolverla.
 *
 * SesionJuego crea diez rondas al iniciar la partida, una por cada objetivo, y
 * las va resolviendo a medida que el jugador escribe expresiones correctas. La
 * partida termina cuando las diez rondas estan resueltas.
 *
 * Las rondas no tienen un orden obligatorio: el jugador puede resolver los
 * objetivos en cualquier orden.
 */
public class Ronda {

    // Numero que el jugador debe obtener con su expresion (de 1 a 10).
    private int numeroObjetivo;

    // Indica si el objetivo ya fue resuelto con una expresion valida.
    private boolean resuelta;

    // Ultima expresion enviada a esta ronda. Empieza vacia.
    private String respuesta;

    /**
     * Crea una ronda pendiente para un objetivo. Empieza sin resolver y con la
     * respuesta vacia.
     *
     * @param numeroObjetivo numero que hay que obtener en esta ronda
     */
    public Ronda(int numeroObjetivo) {
        this.numeroObjetivo = numeroObjetivo;
        this.resuelta = false;
        this.respuesta = "";
    }

    /**
     * Intenta resolver la ronda con la expresion del jugador. Delega la
     * validacion en ValidadorExpresion y solo marca la ronda como resuelta si el
     * resultado es VALIDO.
     *
     * Si la ronda ya estaba resuelta, no vuelve a validar ni modifica la
     * respuesta guardada: devuelve OBJETIVO_YA_RESUELTO directamente.
     *
     * Importante: la respuesta se guarda ANTES de validarla, asi que getRespuesta
     * devuelve el ultimo intento aunque haya sido incorrecto. Cuando la ronda se
     * resuelve, ese intento es la expresion ganadora.
     *
     * @param respuesta         expresion matematica escrita por el jugador
     * @param numerosDisponibles los cuatro numeros generados para la partida
     * @param modo              modo de juego, que define las operaciones permitidas
     * @return el veredicto de la validacion (VALIDO, NUMEROS_INVALIDOS,
     *         OPERACION_INVALIDA, RESULTADO_INCORRECTO u OBJETIVO_YA_RESUELTO)
     */
    public ResultadoValidacion resolver(
            String respuesta,
            List<Integer> numerosDisponibles,
            ModoJuego modo) {

        // Una ronda resuelta no se puede volver a resolver ni sobrescribir.
        if (resuelta) {
            return ResultadoValidacion.OBJETIVO_YA_RESUELTO;
        }

        // Se guarda el intento antes de validarlo (ver nota en la documentacion).
        this.respuesta = respuesta;

        ValidadorExpresion validador = new ValidadorExpresion();

        // Se comprueba que la expresion use los numeros correctos, respete el
        // modo y dé como resultado el objetivo de esta ronda.
        ResultadoValidacion resultado = validador.validarExpresion(
                respuesta,
                numeroObjetivo,
                numerosDisponibles,
                modo
        );

        // Solo una expresion totalmente valida cierra la ronda.
        if (resultado == ResultadoValidacion.VALIDO) {
            this.resuelta = true;
        }

        return resultado;
    }

    /**
     * Devuelve el numero que hay que obtener en esta ronda.
     *
     * @return el objetivo de la ronda, de 1 a 10
     */
    public int getNumeroObjetivo() {
        return numeroObjetivo;
    }

    /**
     * Indica si la ronda ya fue resuelta. La interfaz lo usa para marcar los
     * objetivos completados y SesionJuego para saber si la partida terminó.
     *
     * @return true si el objetivo ya se resolvio, false si sigue pendiente
     */
    public boolean isResuelta() {
        return resuelta;
    }

    /**
     * Devuelve la ultima expresion recibida por esta ronda. Si la ronda esta
     * resuelta, es la expresion que la resolvio. Si no, puede ser el ultimo
     * intento fallido o una cadena vacia si nunca se intento.
     *
     * @return la ultima expresion enviada, o una cadena vacia
     */
    public String getRespuesta() {
        return respuesta;
    }
}
