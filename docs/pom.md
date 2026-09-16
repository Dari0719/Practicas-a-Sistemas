# Documentación del `pom.xml`

El archivo [`pom.xml`](../pom.xml) contiene la configuración principal del proyecto Maven **AllTen Game**. En él se definen la identificación del proyecto, la versión de Java, las dependencias externas y los plugins utilizados para compilar, probar y ejecutar la aplicación.

## Identificación del proyecto

| Elemento | Valor | Descripción |
|---|---|---|
| `groupId` | `co.edu.poli.allten` | Identificador de la organización o grupo del proyecto. |
| `artifactId` | `allten-game` | Nombre del artefacto generado por Maven. |
| `version` | `1.0-SNAPSHOT` | Versión actual del proyecto en desarrollo. |
| `name` | `allten-game` | Nombre descriptivo de la aplicación. |
| `url` | `http://www.example.com` | URL provisional del proyecto. |

## Propiedades principales

| Propiedad | Valor | Descripción |
|---|---|---|
| `project.build.sourceEncoding` | `UTF-8` | Codificación utilizada para los archivos fuente. |
| `maven.compiler.release` | `25` | Versión de Java utilizada para compilar el proyecto. |
| `javafx.version` | `25.0.1` | Versión de JavaFX utilizada por la aplicación. |

El proyecto requiere un JDK compatible con **Java 25** y Maven instalado para ejecutar los comandos de compilación y pruebas.

## Dependencias

### JavaFX Controls

```xml
<groupId>org.openjfx</groupId>
<artifactId>javafx-controls</artifactId>
<version>${javafx.version}</version>
```

Proporciona los controles gráficos necesarios para construir la interfaz de usuario de la aplicación, como botones, etiquetas, campos de texto, tablas y otros componentes visuales.

La versión se obtiene de la propiedad `${javafx.version}`, configurada como `25.0.1`.

### MySQL Connector/J

```xml
<groupId>com.mysql</groupId>
<artifactId>mysql-connector-j</artifactId>
<version>9.4.0</version>
```

Permite establecer la conexión entre la aplicación Java y la base de datos MySQL. Se utiliza para ejecutar consultas y gestionar la persistencia de la información del juego.

### JUnit

```xml
<groupId>junit</groupId>
<artifactId>junit</artifactId>
<version>4.13.2</version>
<scope>test</scope>
```

Se utiliza para crear y ejecutar pruebas unitarias.

El alcance `test` indica que JUnit solo se necesita durante la ejecución de las pruebas y no se incluye como dependencia de la aplicación final.

## Plugin de JavaFX

El proyecto utiliza el plugin `javafx-maven-plugin` versión `0.0.8`. Este plugin permite ejecutar la aplicación JavaFX directamente mediante Maven.

La clase principal configurada es:

```text
co.edu.poli.allten.App
```

Para ejecutar la aplicación se utiliza:

```bash
mvn javafx:run
```

## Gestión de versiones de plugins

La sección `pluginManagement` fija las versiones de los plugins utilizados por Maven. Esto permite mantener un entorno de compilación estable y evitar diferencias entre los equipos de desarrollo.

| Plugin | Versión | Función |
|---|---:|---|
| `maven-clean-plugin` | `3.1.0` | Elimina los archivos generados durante la compilación. |
| `maven-resources-plugin` | `3.0.2` | Gestiona los recursos del proyecto. |
| `maven-compiler-plugin` | `3.14.1` | Compila el código fuente de Java. |
| `maven-surefire-plugin` | `3.5.3` | Ejecuta las pruebas unitarias. |
| `maven-jar-plugin` | `3.0.2` | Genera el archivo `.jar` de la aplicación. |
| `maven-install-plugin` | `2.5.2` | Instala el artefacto en el repositorio local de Maven. |
| `maven-deploy-plugin` | `2.8.2` | Permite publicar el artefacto en un repositorio remoto. |
| `maven-site-plugin` | `3.7.1` | Genera el sitio de documentación del proyecto. |
| `maven-project-info-reports-plugin` | `3.0.0` | Genera reportes con información del proyecto. |

## Comandos principales

### Limpiar archivos generados

```bash
mvn clean
```

Elimina la carpeta `target` y los archivos generados en compilaciones anteriores.

### Compilar el proyecto

```bash
mvn compile
```

Compila el código fuente ubicado en `src/main/java`.

### Ejecutar las pruebas

```bash
mvn test
```

Compila y ejecuta las pruebas ubicadas en `src/test/java`.

### Empaquetar la aplicación

```bash
mvn package
```

Compila el proyecto, ejecuta las pruebas y genera el artefacto correspondiente en la carpeta `target`.

### Ejecutar la aplicación JavaFX

```bash
mvn javafx:run
```

Inicia la aplicación utilizando la clase principal `co.edu.poli.allten.App`.

## Flujo recomendado

Para verificar completamente el proyecto se recomienda ejecutar:

```bash
mvn clean test
```

Este comando elimina los archivos anteriores, compila el proyecto y ejecuta todas las pruebas unitarias.
