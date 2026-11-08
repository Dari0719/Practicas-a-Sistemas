package co.edu.poli.allten.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa una partida completa de All Ten: un jugador intentando obtener
 * los números del 1 al 10 a partir de cuatro números generados al azar.
 *
 * <p>Es la clase central del modelo. Coordina a las demás: usa
 * {@link GeneradorNumeros} para crear los números de la partida, mantiene diez
 * {@link Ronda} (una por objetivo), usa {@link ValidadorExpresion} para revisar
 * lo que escribe el jugador y lleva el control del tiempo, los intentos y el
 * {@link EstadoJuego}.</p>
 *
 * <p>El ciclo de vida típico es el siguiente:</p>
 * <ol>
 *   <li>Se crea con {@link #SesionJuego(Jugador, ModoJuego)}. Todavía no hay
 *       números, rondas ni estado.</li>
 *   <li>{@link #iniciarSesion()} genera los números, crea las rondas y pone la
 *       partida {@link EstadoJuego#EN_CURSO}.</li>
 *   <li>{@link #procesarRespuesta(String)} se llama cada vez que el jugador
 *       envía una expresión. Si resuelve el décimo objetivo, la sesión pasa
 *       sola a {@link EstadoJuego#COMPLETADO}.</li>
 *   <li>{@link #finalizarSesion()} cierra la partida cuando el jugador termina
 *       o sale, como {@link EstadoJuego#COMPLETADO} o
 *       {@link EstadoJuego#ABANDONADO}.</li>
 * </ol>
 *
 * <p>El jugador y el modo son inmutables: se fijan al construir la sesión y no
 * cambian durante la partida. Los diez objetivos se pueden resolver en
 * cualquier orden.</p>
 *
 * @see Jugador
 * @see ModoJuego
 * @see Ronda
 * @see EstadoJuego
 */
public class SesionJuego {

    /** Cantidad de objetivos de la partida: los números del 1 al 10. */
    private static final int CANTIDAD_RONDAS = 10;

    /**
     * Diferencia máxima aceptada entre el resultado de una expresión y un objetivo
     * para considerarlos iguales. Existe porque las divisiones y raíces se calculan
     * con números decimales ({@code double}), que pueden traer errores mínimos de
     * redondeo.
     */
    private static final double TOLERANCIA_RESULTADO = 0.0000001;

    /** Jugador al que pertenece esta partida. No cambia durante la sesión. */
    private final Jugador jugador;

    /**
     * Los cuatro números con los que el jugador debe construir sus expresiones.
     * Está vacía hasta que se llama a {@link #iniciarSesion()}; después es una lista
     * que no se puede modificar.
     */
    private List<Integer> numerosDisponibles;

    /**
     * Las diez rondas de la partida, una por cada objetivo del 1 al 10. Está vacía
     * hasta que se llama a {@link #iniciarSesion()}.
     */
    private List<Ronda> rondas;

    /**
     * Momento en que arrancó la partida, en milisegundos
     * ({@code System.currentTimeMillis()}). El valor 0 significa que la sesión
     * todavía no ha sido iniciada. Es la base para calcular el tiempo transcurrido.
     */
    private long horaInicio;

    /**
     * Duración final de la partida en milisegundos. Solo se fija cuando la partida
     * termina; mientras está en curso, {@link #getTiempoTotal()} calcula el tiempo
     * al momento de la consulta.
     */
    private long tiempoTotal;

    /**
     * Cantidad de expresiones que el jugador ha enviado, sin importar si fueron
     * correctas o no. Se cuenta en cada llamada a {@link #procesarRespuesta(String)}
     * mientras la sesión está en curso.
     */
    private int intentos;

    /**
     * Fecha y hora en que se inició la partida. Es el dato que se guarda en la base
     * de datos para registrar cuándo se jugó. Vale {@code null} hasta que se llama a
     * {@link #iniciarSesion()}.
     */
    private LocalDateTime fecha;

    /**
     * Estado actual de la partida. Vale {@code null} hasta que se llama a
     * {@link #iniciarSesion()}, y desde ese momento solo avanza de
     * {@link EstadoJuego#EN_CURSO} a uno de los dos estados finales.
     */
    private EstadoJuego estado;

    /**
     * Modo de juego elegido para esta partida. Define las operaciones permitidas al
     * validar las expresiones y los números que se generan.
     */
    private final ModoJuego modo;

    /**
     * Crea una sesión nueva que todavía no ha empezado. No genera números ni
     * rondas: eso ocurre al llamar a {@link #iniciarSesion()}.
     *
     * @param jugador jugador que va a jugar la partida; no puede ser nulo
     * @param modo    modo de juego de la partida; no puede ser nulo
     * @throws NullPointerException si el jugador o el modo son nulos
     */
    public SesionJuego(Jugador jugador, ModoJuego modo) {
        this.jugador = Objects.requireNonNull(jugador, "El jugador es obligatorio.");
        this.modo = Objects.requireNonNull(modo, "El modo de juego es obligatorio.");
        this.numerosDisponibles = List.of();
        this.rondas = List.of();
    }

    /**
     * Arranca la partida: genera los cuatro números según el modo, crea las diez
     * rondas, reinicia el contador de intentos y el tiempo, registra la fecha y deja
     * la sesión {@link EstadoJuego#EN_CURSO}. Desde este momento corre el cronómetro.
     *
     * @throws IllegalStateException si la sesión ya había sido iniciada
     */
    public void iniciarSesion() {
        if (horaInicio != 0) {
            throw new IllegalStateException("La sesión ya fue iniciada.");
        }

        List<Integer> numerosGenerados = new GeneradorNumeros().generar(modo);
        this.numerosDisponibles = List.copyOf(numerosGenerados);
        inicializarRondas();
        this.intentos = 0;
        this.tiempoTotal = 0;
        this.fecha = LocalDateTime.now();
        this.horaInicio = System.currentTimeMillis();
        this.estado = EstadoJuego.EN_CURSO;
    }

    /**
     * Evalúa una expresión escrita por el jugador y, si es correcta, marca como
     * resuelto el objetivo al que corresponde. Es el método que se llama cada vez
     * que el jugador envía una respuesta.
     *
     * <p>El proceso es el siguiente:</p>
     * <ol>
     *   <li>Si la partida ya está {@link EstadoJuego#COMPLETADO}, devuelve
     *       {@link ResultadoValidacion#OBJETIVO_YA_RESUELTO} sin contar el
     *       intento.</li>
     *   <li>Suma un intento y valida la expresión con {@link ValidadorExpresion}
     *       (uso correcto de los números y de las operaciones del modo).</li>
     *   <li>Si el validador detecta un error distinto de resultado incorrecto, lo
     *       devuelve de inmediato. Si no, calcula el resultado de la expresión.</li>
     *   <li>Busca la ronda cuyo objetivo coincide con ese resultado (con la
     *       tolerancia definida). Si ninguna coincide, el resultado es incorrecto;
     *       si coincide con una ronda ya resuelta, el objetivo ya estaba
     *       resuelto.</li>
     *   <li>Si la ronda estaba pendiente, la resuelve. Cuando con esto las diez
     *       rondas quedan resueltas, la sesión se completa y se detiene el
     *       cronómetro.</li>
     * </ol>
     *
     * @param respuesta expresión matemática escrita por el jugador
     * @return el veredicto de la validación; {@link ResultadoValidacion#VALIDO}
     *         indica que se resolvió un objetivo nuevo
     * @throws IllegalStateException si la sesión no está en curso (no se ha
     *         iniciado o fue abandonada)
     */
    public ResultadoValidacion procesarRespuesta(String respuesta) {
        if (estado == EstadoJuego.COMPLETADO) {
            return ResultadoValidacion.OBJETIVO_YA_RESUELTO;
        }
        if (estado != EstadoJuego.EN_CURSO) {
            throw new IllegalStateException("La sesión no está en curso.");
        }

        intentos++;

        Ronda primeraPendiente = rondas.stream()
                .filter(ronda -> !ronda.isResuelta())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "La sesión está en curso, pero no tiene rondas pendientes."));

        ValidadorExpresion validador = new ValidadorExpresion();
        ResultadoValidacion validacion = validador.validarExpresion(
                respuesta,
                primeraPendiente.getNumeroObjetivo(),
                numerosDisponibles,
                modo);

        if (validacion != ResultadoValidacion.VALIDO
                && validacion != ResultadoValidacion.RESULTADO_INCORRECTO) {
            return validacion;
        }

        double resultado;
        try {
            resultado = validador.calcularResultado(respuesta);
        } catch (IllegalArgumentException | ArithmeticException e) {
            return ResultadoValidacion.OPERACION_INVALIDA;
        }

        Ronda rondaObjetivo = buscarRonda(resultado);
        if (rondaObjetivo == null) {
            return ResultadoValidacion.RESULTADO_INCORRECTO;
        }
        if (rondaObjetivo.isResuelta()) {
            return ResultadoValidacion.OBJETIVO_YA_RESUELTO;
        }

        ResultadoValidacion resultadoRonda = rondaObjetivo.resolver(
                respuesta,
                numerosDisponibles,
                modo);

        if (resultadoRonda == ResultadoValidacion.VALIDO && estaCompleta()) {
            completarSesion();
        }

        return resultadoRonda;
    }

    /**
     * Cierra la partida y fija el tiempo total. El estado final es
     * {@link EstadoJuego#COMPLETADO} si las diez rondas están resueltas, o
     * {@link EstadoJuego#ABANDONADO} en caso contrario. Si la partida ya había
     * terminado, no hace nada, por lo que se puede llamar más de una vez sin
     * alterar el resultado.
     *
     * @throws IllegalStateException si la sesión nunca fue iniciada
     */
    public void finalizarSesion() {
        if (horaInicio == 0) {
            throw new IllegalStateException("No se puede finalizar una sesión que no ha iniciado.");
        }
        if (estado == EstadoJuego.EN_CURSO) {
            tiempoTotal = System.currentTimeMillis() - horaInicio;
            estado = estaCompleta() ? EstadoJuego.COMPLETADO : EstadoJuego.ABANDONADO;
        }
    }

    /**
     * Indica si el jugador resolvió los diez objetivos.
     *
     * @return {@code true} si existen las diez rondas y todas están resueltas
     */
    public boolean estaCompleta() {
        return rondas.size() == CANTIDAD_RONDAS
                && rondas.stream().allMatch(Ronda::isResuelta);
    }

    /**
     * Devuelve el modo de juego de esta partida.
     *
     * @return el modo de juego
     */
    public ModoJuego getModo() {
        return modo;
    }

    /**
     * Devuelve el jugador dueño de esta partida.
     *
     * @return el jugador
     */
    public Jugador getJugador() {
        return jugador;
    }

    /**
     * Devuelve los cuatro números de la partida.
     *
     * @return los números disponibles; la lista no se puede modificar y está
     *         vacía si la sesión no ha sido iniciada
     */
    public List<Integer> getNumerosDisponibles() {
        return numerosDisponibles;
    }

    /**
     * Devuelve las diez rondas de la partida.
     *
     * @return una copia no modificable de las rondas, de modo que quien la reciba
     *         pueda consultarlas pero no alterar la lista de la sesión
     */
    public List<Ronda> getRondas() {
        return List.copyOf(rondas);
    }

    /**
     * Devuelve el momento en que arrancó la partida.
     *
     * @return el momento de inicio en milisegundos, o 0 si no ha iniciado
     */
    public long getHoraInicio() {
        return horaInicio;
    }

    /**
     * Devuelve la duración de la partida en milisegundos. Mientras está en curso
     * calcula el tiempo transcurrido hasta este instante; cuando ya terminó,
     * devuelve el tiempo que quedó fijado al finalizar.
     *
     * @return duración de la partida en milisegundos
     */
    public long getTiempoTotal() {
        if (estado == EstadoJuego.EN_CURSO) {
            return System.currentTimeMillis() - horaInicio;
        }
        return tiempoTotal;
    }

    /**
     * Devuelve la cantidad de expresiones enviadas por el jugador.
     *
     * @return el número de intentos, correctos o no
     */
    public int getIntentos() {
        return intentos;
    }

    /**
     * Devuelve la fecha y hora en que se inició la partida.
     *
     * @return la fecha de inicio, o {@code null} si no ha iniciado
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Devuelve el estado actual de la partida.
     *
     * @return el estado, o {@code null} si no ha iniciado
     */
    public EstadoJuego getEstado() {
        return estado;
    }

    /**
     * Crea las diez rondas de la partida, una por cada objetivo del 1 al 10, todas
     * sin resolver, y reemplaza a las anteriores.
     */
    private void inicializarRondas() {
        List<Ronda> rondasInicializadas = new ArrayList<>(CANTIDAD_RONDAS);
        for (int objetivo = 1; objetivo <= CANTIDAD_RONDAS; objetivo++) {
            rondasInicializadas.add(new Ronda(objetivo));
        }
        this.rondas = rondasInicializadas;
    }

    /**
     * Busca la ronda cuyo objetivo es igual al resultado dado, aceptando una
     * diferencia mínima de {@code TOLERANCIA_RESULTADO} por los redondeos de los
     * decimales.
     *
     * @param resultado valor calculado a partir de la expresión del jugador
     * @return la ronda que coincide, o {@code null} si el resultado no corresponde
     *         a ningún objetivo
     */
    private Ronda buscarRonda(double resultado) {
        for (Ronda ronda : rondas) {
            if (Math.abs(resultado - ronda.getNumeroObjetivo()) <= TOLERANCIA_RESULTADO) {
                return ronda;
            }
        }
        return null;
    }

    /**
     * Cierra la partida como ganada: fija el tiempo total y pasa el estado a
     * {@link EstadoJuego#COMPLETADO}. Se llama automáticamente cuando se resuelve
     * el último objetivo.
     */
    private void completarSesion() {
        tiempoTotal = System.currentTimeMillis() - horaInicio;
        estado = EstadoJuego.COMPLETADO;
    }
}
