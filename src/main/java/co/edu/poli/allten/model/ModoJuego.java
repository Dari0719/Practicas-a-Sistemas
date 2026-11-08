package co.edu.poli.allten.model;

/**
 * Define las dos variantes de juego disponibles en All Ten.
 *
 * <p>El modo se elige en la pantalla de inicio y se mantiene fijo durante toda
 * la partida, porque queda guardado en la {@link SesionJuego}. Cambia tres cosas:</p>
 * <ul>
 *   <li><b>Las operaciones permitidas</b> en {@link ValidadorExpresion}.</li>
 *   <li><b>Las soluciones válidas</b> en {@link BuscadorSoluciones} y la
 *       generación de números en {@link GeneradorNumeros}: en Hardcore solo
 *       cuentan las combinaciones que se pueden resolver con las reglas
 *       avanzadas.</li>
 *   <li><b>El ranking</b>: cada modo tiene su propia tabla de posiciones,
 *       independiente de la otra.</li>
 * </ul>
 *
 * <p>El nombre de la constante se guarda como texto en la columna {@code modo}
 * de la tabla {@code partida} y se recupera con {@code ModoJuego.valueOf(...)}
 * en {@code RankingDAO}. Por eso no se debe renombrar una constante sin migrar
 * los datos que ya están en la base de datos.</p>
 *
 * @see SesionJuego
 * @see ValidadorExpresion
 */
public enum ModoJuego {

    /**
     * Modo básico. Solo se permiten suma, resta, multiplicación, división y
     * paréntesis. No se pueden usar potencias ni raíces.
     */
    NORMAL,

    /**
     * Modo avanzado. Además de las operaciones del modo {@link #NORMAL}, permite
     * potencia ({@code ^}) y raíz cuadrada ({@code √}). Cada expresión debe incluir
     * al menos una operación avanzada no trivial, con un máximo de una raíz
     * cuadrada por expresión.
     */
    HARDCORE
}
