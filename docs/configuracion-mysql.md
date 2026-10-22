# Configuración de la base de datos MySQL

## OBJETIVOS

El esquema está definido en `src/main/resources/db/schema.sql` y corresponde
al diagrama entidad-relación:

- `jugador`: identificador y apodo único.
- `partida`: jugador, tiempo total en milisegundos, intentos, estado, modo y fecha.
- Cada partida pertenece a un jugador mediante `jugador_id`.

> **Importante:** `schema.sql` contiene `DROP DATABASE IF EXISTS allten`. Ejecutarlo
> elimina permanentemente las tablas y los datos existentes en `allten`. Haz una
> copia de seguridad antes si necesitas conservarlos. El script no borra otras bases.

## Crear la base desde cero con MySQL Workbench

1. Inicia MySQL Workbench y abre la conexión local al servidor.
2. Selecciona **File → Open SQL Script…** y abre
   `src/main/resources/db/schema.sql`.
3. Antes de ejecutarlo, confirma que quieres reemplazar la base `allten`: el script
   elimina esa base y todos sus datos, y luego la crea de nuevo. No afecta a otras bases.
4. Haz clic dentro del editor SQL y pulsa el botón del rayo para ejecutar el script.
   Acepta la confirmación si Workbench pregunta si debe ejecutar varias sentencias.
5. En **SCHEMAS**, pulsa actualizar y expande `allten` → **Tables**. Deben aparecer
   `jugador` y `partida`.

También se puede ejecutar desde PowerShell, con el cliente de MySQL instalado y
ubicado en la carpeta raíz del proyecto:

```powershell
Get-Content -Raw .\src\main\resources\db\schema.sql | mysql -u root -p
```

El cliente solicitará la contraseña de MySQL. El script incluye
`DROP DATABASE IF EXISTS allten`; ejecútalo solo cuando realmente quieras eliminar
y recrear esa base.

Comprueba el esquema desde una pestaña de consultas de Workbench:

```sql
USE allten;
SHOW TABLES;
DESCRIBE jugador;
DESCRIBE partida;
SHOW CREATE TABLE partida;
```

`partida.jugador_id` debe tener una clave foránea hacia `jugador.id`.

## Tablas creadas

El esquema coincide con el diagrama entidad-relación:

- `jugador`: `id` entero autoincremental (clave primaria) y `apodo` único de hasta
  50 caracteres.
- `partida`: `id` autoincremental, `jugador_id` (clave foránea), `tiempo_total`
  en milisegundos, `intentos`, `estado`, `modo` y `fecha`.
- La tabla `partida` tiene un índice para acelerar consultas del ranking por estado,
  modo, tiempo e intentos.

## Credenciales para Java

La clase `ConexionBaseDatos` lee las credenciales desde variables de entorno. En
PowerShell, antes de ejecutar la aplicación o una comprobación de conexión:

```powershell
$env:ALLTEN_DB_USER="root"
$env:ALLTEN_DB_PASSWORD="TU_CONTRASENA"
```

No guardes la contraseña en el código ni en archivos versionados. La URL apunta a
`jdbc:mysql://localhost:3306/allten`.

## Uso de los DAO

- `JugadorDAO.guardar(jugador)` inserta el jugador o recupera el identificador del
  apodo existente; actualiza el ID dentro del objeto.
- `JugadorDAO.buscarPorApodo(apodo)` devuelve el jugador o `null` si no existe.
- `SesionJuegoDAO.guardar(sesion)` guarda una sesión iniciada. El jugador debe haberse
  guardado primero para tener una clave foránea válida. Se guarda el estado actual
  de la sesión, por ejemplo `COMPLETADO` o `ABANDONADO`.
- `RankingDAO.obtenerRanking(limite, modo)` devuelve partidas completadas del modo,
  ordenadas por menor tiempo y, en empate, por menos intentos.

Los DAO cierran automáticamente sus conexiones y sentencias JDBC. Los errores de
base de datos se propagan como `SQLException` para que la capa que los invoque pueda
informar el fallo.

En la pantalla de inicio puedes abrir **Consultar ranking** directamente, sin
iniciar una partida. Primero selecciona el modo que quieres consultar; el ranking
muestra hasta 10 partidas completadas de ese modo. Desde la pantalla del ranking,
**Actualizar** vuelve a consultar la base de datos y **Volver** regresa al inicio
si abriste el ranking desde allí.

## Dependencia y pruebas

El controlador JDBC MySQL Connector/J está declarado en `pom.xml`. Para compilar y
ejecutar las pruebas unitarias:

```powershell
mvn test
```

Las pruebas actuales no requieren un servidor MySQL; para comprobar operaciones
reales hay que tener MySQL iniciado, crear el esquema y definir las variables de
entorno en la misma terminal desde la que se ejecutará la aplicación.

Si aparece `Access denied for user ...`, la conexión está siendo rechazada antes
de consultar el ranking. Configura `ALLTEN_DB_USER` y `ALLTEN_DB_PASSWORD` con
un usuario y contraseña válidos de MySQL. Si ejecutas desde VS Code, inicia VS
Code desde la terminal que ya tenga esas variables o reinícialo después de
configurarlas. No es necesario usar `root`; el usuario debe tener permisos de
lectura y escritura en `allten`.

Las pruebas de integración de `DatabaseIntegrationTest` no se ejecutan por defecto.
Para insertar una partida de demostración persistente para el jugador `ververecho`,
usa la terminal de PowerShell donde definiste las variables de entorno:

```powershell
mvn "-Dtest=DatabaseIntegrationTest#guardaPartidaPersistenteParaVerverecho" "-Dallten.db.integration=true" test
```

Esta prueba **deja guardada** la partida completada y sus datos no se eliminan al
terminar. Cada nueva ejecución agrega otra partida para `ververecho`. Para verificar
las filas en Workbench:

```sql
USE allten;

SELECT j.id, j.apodo, p.id AS partida_id, p.tiempo_total,
       p.intentos, p.estado, p.modo, p.fecha
FROM jugador j
JOIN partida p ON p.jugador_id = j.id
WHERE j.apodo = 'ververecho'
ORDER BY p.id DESC;
```

La otra prueba, `guardaJugadorPartidaYLaExponeEnRanking`, usa un jugador temporal
y borra únicamente los registros que ella crea. Puedes ejecutar toda la clase con:

```powershell
mvn "-Dtest=DatabaseIntegrationTest" "-Dallten.db.integration=true" test
```

Usa las credenciales ya configuradas en esa terminal y las mismas con las que te
conectas en Workbench. No las pegues en el comando ni las publiques.
