# Reglas del juego

## Inicio de la partida

El jugador escribe un nickname y comienza la partida con el botón **Iniciar Partida** o presionando **Enter**.

Al comenzar, el sistema genera cuatro números enteros aleatorios entre 1 y 9. Los números pueden repetirse. El mismo conjunto de cuatro números se mantiene durante toda la partida.

## Garantía de solubilidad

El sistema no acepta cualquier combinación aleatoria. Antes de iniciar la partida, aplica una búsqueda exhaustiva por fuerza bruta sobre las operaciones y agrupaciones posibles.

La combinación solo se utiliza si permite obtener todos los objetivos del 1 al 10. Si una combinación no permite resolver los diez objetivos, se descarta y se genera otra.

Por lo tanto, toda partida iniciada debe tener solución para los diez objetivos.

## Objetivos

La partida contiene diez objetivos: los números enteros del 1 al 10.

Los objetivos pueden resolverse en cualquier orden. El jugador puede comenzar por el 1, el 7, el 10 o cualquier otro objetivo pendiente. No existe una secuencia obligatoria.

Una expresión correcta se asigna automáticamente al objetivo cuyo resultado produce. No es obligatorio seleccionar primero la fila del objetivo.

## Uso de los números

Cada expresión debe utilizar los cuatro números generados para la partida, exactamente una vez cada uno.

Los números no se consumen entre objetivos. El mismo conjunto de cuatro números puede reutilizarse para resolver los diez objetivos.

Los botones de números se deshabilitan individualmente cuando el número correspondiente ya fue usado en la expresión actual. Si existen números repetidos, se pueden utilizar tantas copias como hayan sido generadas. Al borrar o reiniciar la expresión, los botones vuelven a habilitarse.

## Operaciones permitidas

Se permiten:

- suma (`+`);
- resta (`-` o `−`);
- multiplicación (`*` o `×`);
- división (`/` o `÷`);
- paréntesis.

El teclado del juego muestra los operadores con sus símbolos visuales. Si el jugador escribe `*` o `/` en el teclado, la aplicación los muestra como `×` y `÷`.

La expresión se puede confirmar presionando el botón `=` o la tecla **Enter**.

## Resultados intermedios

Se permiten resultados intermedios fraccionarios y negativos, siempre que el resultado final sea exactamente uno de los objetivos del 1 al 10.

No se permite dividir entre cero ni utilizar operadores o paréntesis con sintaxis inválida.

## Validación de respuestas

Una expresión es correcta cuando:

- utiliza únicamente los cuatro números generados;
- usa cada número exactamente una vez;
- utiliza solo operaciones permitidas;
- tiene una sintaxis matemática válida;
- no divide entre cero; y
- produce exactamente uno de los objetivos pendientes.

Si la expresión utiliza números incorrectos o repite una copia que ya no está disponible, se muestra un mensaje indicando que deben usarse los cuatro números disponibles exactamente una vez.

Si la expresión tiene una operación o estructura inválida, se muestra un mensaje de operación matemática no válida.

Si la expresión es válida, pero produce un resultado que no corresponde a una solución pendiente, se muestra el resultado obtenido. Por ejemplo: **El resultado 12 no hace parte de las soluciones.**

## Progreso y finalización

Cada objetivo resuelto se marca visualmente en verde y conserva la expresión utilizada. El orden visual de las soluciones representa el orden en que el jugador las completó.

La partida termina cuando los diez objetivos están resueltos, sin importar el orden.

Al finalizar, se muestra una pantalla de resumen dentro de la aplicación con el tiempo total empleado.

## Tiempo y ranking

El cronómetro comienza al iniciar la partida y se detiene al resolver el décimo objetivo.

El ranking se ordena por el menor tiempo total. Si dos jugadores tienen el mismo tiempo, se desempata con el menor número de intentos. Las partidas abandonadas o incompletas no se incluyen.

Desde el resumen de la partida, el jugador puede consultar el ranking o volver a la pantalla de inicio.

## Arquitectura y recursos

La aplicación sigue el patrón MVC:

- el modelo administra las reglas, la sesión, los objetivos, la validación, el tiempo y el ranking;
- el controlador conecta las acciones de la vista con el modelo;
- la vista carga FXML, aplica CSS y muestra el estado al jugador.

Las pantallas están separadas en recursos:

- `game-home.fxml` y `game-home.css` para el inicio;
- `game.fxml` y `game.css` para la partida;
- `game-summary.fxml` para el resumen final.
