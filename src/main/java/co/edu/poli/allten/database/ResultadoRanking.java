package co.edu.poli.allten.database;

import co.edu.poli.allten.model.ModoJuego;
import java.time.LocalDateTime;
import java.util.Objects;

/** Una partida presentada como fila del ranking. */
public class ResultadoRanking {

    private final String apodo;
    private final long tiempoTotal;
    private final LocalDateTime fecha;
    private final int intentos;
    private final ModoJuego modo;

    public ResultadoRanking(
            String apodo,
            long tiempoTotal,
            LocalDateTime fecha,
            int intentos,
            ModoJuego modo) {
        this.apodo = Objects.requireNonNull(apodo, "El apodo es obligatorio.");
        this.tiempoTotal = tiempoTotal;
        this.fecha = Objects.requireNonNull(fecha, "La fecha es obligatoria.");
        this.intentos = intentos;
        this.modo = Objects.requireNonNull(modo, "El modo es obligatorio.");
    }

    public String getApodo() {
        return apodo;
    }

    public long getTiempoTotal() {
        return tiempoTotal;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public int getIntentos() {
        return intentos;
    }

    public ModoJuego getModo() {
        return modo;
    }
}
