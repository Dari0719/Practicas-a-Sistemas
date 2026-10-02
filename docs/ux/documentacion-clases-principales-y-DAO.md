# Documentación de Clases Principales del Proyecto

---

## 1. SesionJuego

### Descripción  
`SesionJuego` representa una sesión completa del juego. Administra al jugador, las rondas, los números disponibles, el tiempo, el estado y el modo de juego.

### Atributos  

| Atributo | Tipo | Descripción |
|---|---|---|
| `jugador` | `Jugador` | Jugador que participa en la sesión. |
| `numerosDisponibles` | `List<Integer>` | Números disponibles para el desarrollo del juego. |
| `rondas` | `List<Ronda>` | Lista de rondas asociadas a la sesión. |
| `horario` | `long` | Información relacionada con el control temporal. |
| `tiempoTotal` | `long` | Tiempo total asignado o utilizado en la sesión. |
| `intentos` | `int` | Cantidad de intentos realizados. |
| `fecha` | `LocalDateTime` | Fecha y hora asociada a la sesión. |
| `estado` | `EstadoJuego` | Estado actual de la sesión. |
| `modo` | `ModoJuego` | Modalidad de juego seleccionada. |

### Métodos principales  

| Método | Descripción |
|---|---|
| `iniciarSesion()` | Inicia una nueva sesión de juego. |
| `procesarRespuesta(respuesta)` | Procesa la respuesta del jugador y devuelve un `ResultadoValidacion`. |
| `finalizarSesion()` | Finaliza la sesión actual. |
| `establecerTiempo()` | Establece o controla el tiempo de la sesión. |
| `finalizarRonda()` | Finaliza la ronda actual. |
| `getModo()` | Obtiene el modo de juego seleccionado. |

### Responsabilidad  
Coordinar el flujo general de la partida y conectar al jugador con las rondas, la generación de números y la validación de respuestas.

---

## 2. Ronda

### Descripción  
`Ronda` representa una ronda individual dentro de una sesión de juego. Contiene un objetivo numérico y procesa la respuesta del jugador.

### Atributos  

| Atributo | Tipo | Descripción |
|---|---|---|
| `numeroObjetivo` | `int` | Número que el jugador debe alcanzar. |
| `respuesta` | `String` | Expresión introducida por el jugador. |
| `resultado` | `boolean` | Resultado lógico asociado a la respuesta. |

### Método principal  

| Método | Descripción |
|---|---|
| `resolver(respuesta, numerosDisponibles, modo)` | Procesa la respuesta y devuelve un `ResultadoValidacion`. |

### Responsabilidad  
Representar y resolver una unidad individual de juego.

---

## 3. Jugador

### Descripción  
`Jugador` representa al usuario que participa en el juego y almacena su información básica.

### Atributos  

| Atributo | Tipo | Descripción |
|---|---|---|
| `apodo` | `String` | Nombre o alias del jugador. |
| `id` | `int` | Identificador único del jugador. |

### Métodos principales  

| Método | Descripción |
|---|---|
| `Jugador(apodo)` | Constructor que crea un jugador con apodo. |
| `Jugador(id, apodo)` | Constructor que crea un jugador con identificador y apodo. |
| `getId()` | Obtiene el identificador del jugador. |
| `setId(id)` | Modifica el identificador del jugador. |
| `getApodo()` | Obtiene el apodo del jugador. |

### Responsabilidad  
Representar y administrar los datos básicos del jugador.

---

## 4. ValidadorExpresion

### Descripción  
`ValidadorExpresion` valida si una expresión ingresada por el jugador cumple las reglas y alcanza el objetivo.

### Métodos principales  

| Método | Descripción |
|---|---|
| `validar(expresion, numeroObjetivo, numerosDisponibles, modo)` | Analiza la expresión y devuelve un `ResultadoValidacion`. |
| `calcularResultado(expresion)` | Calcula el resultado matemático de una expresión. |

### Responsabilidad  
Validar y evaluar las expresiones matemáticas ingresadas por el jugador.

---

## 5. GeneradorNumeros

### Descripción  
`GeneradorNumeros` genera los números disponibles durante una partida.

### Atributos  

| Atributo | Tipo | Descripción |
|---|---|---|
| `MAX_INTENTOS` | `int` | Límite de intentos durante la generación. |
| `ALEATORIO` | `Random` | Generador de valores aleatorios. |
| `buscador` | `BuscadorSoluciones` | Componente para comprobar posibles soluciones. |

### Método principal  

| Método | Descripción |
|---|---|
| `generar(modo: ModoJuego)` | Genera una lista de números según el modo de juego. |

### Responsabilidad  
Generar números válidos y garantizar que exista al menos una solución posible.

---

## 6. BuscadorSoluciones

### Descripción  
`BuscadorSoluciones` analiza un conjunto de números y determina si es posible alcanzar un objetivo.

### Métodos principales  

| Método | Descripción |
|---|---|
| `buscarSoluciones(numeros, objetivo, modo)` | Busca expresiones que alcancen el objetivo. |
| `existeSolucion(numeros, objetivo, modo)` | Comprueba si existe al menos una solución. |
| `tieneSolucionTodosLosObjetivos(numeros, modo)` | Verifica si los números permiten resolver todos los objetivos. |

### Responsabilidad  
Buscar y verificar soluciones matemáticas posibles.

---

## 7. EstadoJuego

### Descripción  
Enumeración que representa el estado actual de una sesión de juego.

### Valores  

| Valor | Descripción |
|---|---|
| `EN_CURSO` | La sesión está activa. |
| `COMPLETADO` | El jugador terminó la sesión correctamente. |
| `ABANDONADO` | La sesión fue abandonada. |

---

## 8. ModoJuego

### Descripción  
Enumeración que define la modalidad de juego.

### Valores  

| Valor | Descripción |
|---|---|
| `NORMAL` | Modalidad estándar. |
| `HARDCORE` | Modalidad con reglas más exigentes. |

---

## 9. ResultadoValidacion

### Descripción  
Enumeración que representa el resultado de validar una respuesta.

### Valores  

| Valor | Descripción |
|---|---|
| `VALIDO` | La expresión cumple las reglas y alcanza el objetivo. |
| `NUMEROS_INVALIDOS` | Se usaron números no permitidos. |
| `OPERACION_INVALIDA` | La operación no cumple las reglas. |
| `RESULTADO_INCORRECTO` | La expresión no alcanza el objetivo. |
| `OBJETIVO_YA_RESUELTO` | El objetivo ya había sido resuelto. |

---

## 10. PartidaDAO

### Descripción  
`PartidaDAO` administra la persistencia de las sesiones de juego.

### Atributos  

| Atributo | Tipo | Descripción |
|---|---|---|
| `conexion` | `ConexionBaseDatos` | Conexión activa hacia la base de datos. |

### Métodos principales  

| Método | Descripción |
|---|---|
| `guardarSesion(sesion: SesionJuego)` | Almacena los datos de una sesión en la base de datos. |

### Responsabilidad  
Registrar las partidas jugadas.

---

## 11. JugadorDAO

### Descripción  
`JugadorDAO` gestiona la persistencia de los jugadores.

### Atributos  

| Atributo | Tipo | Descripción |
|---|---|---|
| `conexion` | `ConexionBaseDatos` | Conexión activa hacia la base de datos. |

### Métodos principales  

| Método | Descripción |
|---|---|
| `guardarJugador(jugador: Jugador)` | Registra un nuevo jugador. |
| `buscarPorApodo(apodo: String)` | Recupera un jugador por su apodo. |

### Responsabilidad  
Administrar la creación y consulta de jugadores.

---

## 12. RankingDAO

### Descripción  
`RankingDAO` obtiene la información de clasificación de los jugadores.

### Atributos  

| Atributo | Tipo | Descripción |
|---|---|---|
| `conexion` | `ConexionBaseDatos` | Conexión activa hacia la base de datos. |

### Métodos principales  

| Método | Descripción |
|---|---|
| `obtenerRanking()` | Recupera la lista de resultados de los jugadores. |

### Responsabilidad  
Proporcionar información de clasificación y desempeño.

---

## 13. ConexionBaseDatos

### Descripción  
`ConexionBaseDatos` centraliza la configuración y acceso a la base de datos.

### Atributos  

| Atributo | Tipo | Descripción |
|---|---|---|
| `URL` | `String` | Dirección de conexión. |
| `VARIABLE_USUARIO` | `String` | Usuario de acceso. |
| `VARIABLE_CONTRASENA` | `String` | Contraseña de acceso. |

### Método principal  

| Método | Descripción |
|---|---|
| `obtenerConexion()` | Devuelve una conexión activa. |

### Responsabilidad  
Proveer la conexión necesaria para la persistencia.

## 14. ResultadoRanking

### Descripción  
`ResultadoRanking` representa los datos de clasificación de un jugador en el sistema. Contiene información sobre su desempeño en las partidas.

### Atributos  

| Atributo | Tipo | Descripción |
|---|---|---|
| `apodo` | `String` | Nombre del jugador. |
| `tiempoTotal` | `long` | Tiempo total utilizado en la sesión. |
| `fecha` | `LocalDateTime` | Fecha en la que se registró el resultado. |
| `intentos` | `int` | Número de intentos realizados. |
| `modo` | `ModoJuego` | Modalidad en la que se jugó la partida. |

### Métodos principales  

| Método | Descripción |
|---|---|
| `getApodo()` | Obtiene el apodo del jugador. |
| `getTiempoTotal()` | Obtiene el tiempo total registrado. |
| `getFecha()` | Obtiene la fecha del resultado. |
| `getIntentos()` | Obtiene la cantidad de intentos realizados. |
| `getModo()` | Obtiene el modo de juego asociado. |

### Responsabilidad  
Representar los resultados de un jugador dentro del ranking.

### Relaciones  
- Es devuelto por `RankingDAO`.  
- Utiliza `ModoJuego`.

