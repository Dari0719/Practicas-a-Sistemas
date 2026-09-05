# Configuracion inicial de MySQL

## Objetivo

Configurar la conexion inicial del proyecto All Ten con una base de datos relacional MySQL para preparar la persistencia del juego.

## Versiones y datos de conexion

- Motor: MySQL Community Server 8.0.46.
- Base de datos: `allten`.
- Servidor: `localhost`.
- Puerto: `3306`.
- Usuario local: `root`.
- Driver JDBC: MySQL Connector/J `9.4.0`.

La base de datos se creo con:

```sql
CREATE DATABASE allten;
USE allten;
SELECT DATABASE();
```

El ultimo comando debe devolver `allten`.

## Dependencia del proyecto

La dependencia del controlador JDBC se encuentra en `pom.xml`:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.4.0</version>
</dependency>
```

## Credenciales

Las credenciales no se escriben directamente en el codigo. En PowerShell, desde la carpeta `allten-game`, se configuran como variables de entorno:

```powershell
$env:ALLTEN_DB_USER="root"
$env:ALLTEN_DB_PASSWORD="TU_CONTRASENA"
```

Estas variables permanecen disponibles solamente en la ventana actual de PowerShell. No se deben subir contrasenas al repositorio.

## Clase de conexion

La clase `DatabaseConnection` centraliza la conexion usando la URL:

```text
jdbc:mysql://localhost:3306/allten
```

El metodo `getConnection()` lee `ALLTEN_DB_USER` y `ALLTEN_DB_PASSWORD` y devuelve una instancia de `java.sql.Connection`. Si falta alguna variable, se informa que debe configurarse antes de conectar.

## Datos que se van a persistir

Para la primera version del juego se propone guardar el historial de partidas y los datos necesarios para consultar el rendimiento del jugador.

### Tabla `players`

- `id`: identificador unico del jugador.
- `name`: nombre del jugador.

### Tabla `games`

- `id`: identificador unico de la partida.
- `player_id`: jugador que realizo la partida.
- `started_at`: fecha y hora de inicio.
- `ended_at`: fecha y hora de finalizacion.
- `duration_ms`: tiempo total empleado, expresado en milisegundos.
- `attempts`: cantidad de intentos realizados.
- `status`: estado de la partida, por ejemplo `IN_PROGRESS`, `COMPLETED` o `ABANDONED`.

La partida se completa cuando el jugador resuelve correctamente los diez objetivos. Al finalizar, la aplicacion mostrara el tiempo total y guardara el resultado en `games`. `duration_ms` permite comparar los tiempos con precision, mientras que `started_at` y `ended_at` conservan el momento exacto de la partida.

El mejor tiempo de cada jugador se puede consultar con:

```sql
SELECT
    p.name,
    MIN(g.duration_ms) AS best_time_ms
FROM players p
JOIN games g ON g.player_id = p.id
WHERE g.status = 'COMPLETED'
GROUP BY p.id, p.name
ORDER BY best_time_ms ASC;
```

La tabla de clasificacion se obtiene a partir del historial de partidas; no se guardara una copia separada del mejor tiempo. Asi se conservan los resultados anteriores y se evita duplicar informacion.

Por ahora no se almacenaran contrasenas ni datos personales innecesarios. Tampoco se guardaran todas las operaciones realizadas; ese detalle se podra agregar despues si las reglas definitivas del juego lo requieren.

## Validacion

Para compilar el proyecto y ejecutar las pruebas:

```powershell
mvn test
```

Para comprobar la conexion real desde Java, se genera el classpath de Maven:

```powershell
mvn dependency:build-classpath "-Dmdep.outputFile=target\classpath.txt"
$cp = "target\classes;" + (Get-Content target\classpath.txt)
jshell --class-path $cp
```

Dentro de JShell se ejecuta:

```java
import co.edu.poli.allten.database.DatabaseConnection;
var connection = DatabaseConnection.getConnection();
connection.isValid(2);
connection.close();
```

El resultado esperado de `connection.isValid(2)` es `true`.

Para salir de JShell:

```text
/exit
```

## Solucion de problemas

- `No default schema selected`: ejecutar `USE allten;` en MySQL Shell.
- Error por variables de entorno: volver a configurar `ALLTEN_DB_USER` y `ALLTEN_DB_PASSWORD` en la misma ventana de PowerShell.
- Error de conexion en el puerto: comprobar que el servicio `MySQL80` este iniciado y que MySQL use el puerto `3306`.
- Error de autenticacion: verificar que la contrasena coincida con la configurada para el usuario `root`.
