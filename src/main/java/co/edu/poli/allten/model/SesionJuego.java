package co.edu.poli.allten.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Coordina una partida: genera sus números, administra los diez objetivos,
 * procesa intentos y registra el estado y el tiempo de la sesión.
 */
public class SesionJuego {

    // La partida siempre contiene un objetivo por cada número del 1 al 10.
    private static final int CANTIDAD_RONDAS = 10;
    // Evita errores de comparación por la representación decimal de los resultados.
    private static final double TOLERANCIA_RESULTADO = 0.0000001;

    private final Jugador jugador;
    // El conjunto se mantiene durante toda la sesión y se reutiliza en cada objetivo.
    private List<Integer> numerosDisponibles;
    private List<Ronda> rondas;
    // Tiempos expresados en milisegundos; horaInicio es la hora Unix de inicio.
    private long horaInicio;
    private long tiempoTotal;
    private int intentos;
    private LocalDateTime fecha;
    private EstadoJuego estado;
    private final ModoJuego modo;

    /**
     * Prepara una sesión sin iniciarla. La generación de números ocurre en iniciarSesion.
     */
    public SesionJuego(Jugador jugador, ModoJuego modo) {
        this.jugador = Objects.requireNonNull(jugador, "El jugador es obligatorio.");
        this.modo = Objects.requireNonNull(modo, "El modo de juego es obligatorio.");
        this.numerosDisponibles = List.of();
        this.rondas = List.of();
    }

    /**
     * Genera el conjunto de números, crea los objetivos y pone en marcha el cronómetro.
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
     * Valida una expresión y la asigna al objetivo que produce, sin exigir que el jugador
     * resuelva los objetivos en orden.
     */
    public ResultadoValidacion procesarRespuesta(String respuesta) {
        if (estado == EstadoJuego.COMPLETADO) {
            return ResultadoValidacion.OBJETIVO_YA_RESUELTO;
        }
        if (estado != EstadoJuego.EN_CURSO) {
            throw new IllegalStateException("La sesión no está en curso.");
        }

        intentos++;

        // Se usa una ronda pendiente para comprobar la validez sintáctica y el uso de números.
        // El resultado se evalúa después para identificar el objetivo que el jugador resolvió.
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
     * Detiene la sesión: queda completada si todos los objetivos están resueltos;
     * de lo contrario, queda marcada como abandonada.
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

    /** Indica si se han resuelto las diez rondas de la sesión. */
    public boolean estaCompleta() {
        return rondas.size() == CANTIDAD_RONDAS
                && rondas.stream().allMatch(Ronda::isResuelta);
    }

    public ModoJuego getModo() {
        return modo;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public List<Integer> getNumerosDisponibles() {
        return numerosDisponibles;
    }

    public List<Ronda> getRondas() {
        return List.copyOf(rondas);
    }

    public long getHoraInicio() {
        return horaInicio;
    }

    public long getTiempoTotal() {
        if (estado == EstadoJuego.EN_CURSO) {
            return System.currentTimeMillis() - horaInicio;
        }
        return tiempoTotal;
    }

    public int getIntentos() {
        return intentos;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public EstadoJuego getEstado() {
        return estado;
    }

    /** Construye una ronda para cada objetivo, del 1 al 10. */
    private void inicializarRondas() {
        List<Ronda> rondasInicializadas = new ArrayList<>(CANTIDAD_RONDAS);
        for (int objetivo = 1; objetivo <= CANTIDAD_RONDAS; objetivo++) {
            rondasInicializadas.add(new Ronda(objetivo));
        }
        this.rondas = rondasInicializadas;
    }

    /** Encuentra el objetivo entero que coincide con el resultado de la expresión. */
    private Ronda buscarRonda(double resultado) {
        for (Ronda ronda : rondas) {
            if (Math.abs(resultado - ronda.getNumeroObjetivo()) <= TOLERANCIA_RESULTADO) {
                return ronda;
            }
        }
        return null;
    }

    /** Guarda el tiempo final y cambia el estado cuando se resuelve el último objetivo. */
    private void completarSesion() {
        tiempoTotal = System.currentTimeMillis() - horaInicio;
        estado = EstadoJuego.COMPLETADO;
    }
}
