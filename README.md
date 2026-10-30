
## Descripción

Proyecto grupal para el desarrollo de una aplicación de escritorio de lógica y cálculo, inspirada en la dinámica del juego **All Ten**. El jugador recibe cuatro números aleatorios y debe usarlos, con operaciones matemáticas, para obtener cada uno de los resultados del 1 al 10 en el menor tiempo posible.

La aplicación está desarrollada con **Java, JavaFX y MySQL**, siguiendo el patrón arquitectónico **MVC (Model-View-Controller)**, y aplica principios de **UX (User Experience)** en el diseño y el flujo de las pantallas.

---

## Objetivo del proyecto

Desarrollar una aplicación de escritorio basada en un juego de lógica y cálculo, aplicando buenas prácticas de desarrollo de software, arquitectura MVC, gestión de bases de datos, control de versiones y principios de experiencia de usuario.

El desarrollo se realiza de manera iterativa mediante la metodología **Scrum**, organizando el trabajo en historias de usuario, tareas y Sprints.

---

## Características

- Cuatro números aleatorios del 1 al 9 por partida.
- Partidas siempre resolubles: se descartan las combinaciones que no permiten lograr los diez objetivos.
- Los diez objetivos se pueden resolver en cualquier orden.
- Modos de juego **Normal** y **Hardcore**.
- Cronómetro, intentos ilimitados y sistema de pistas.
- Validación de expresiones con mensajes claros.
- Ranking por modo, ordenado por menor tiempo y, en caso de empate, por menor número de intentos.
- Pantallas de inicio, partida, resumen final y ranking.

---

## Reglas del juego

1. El jugador escribe un nickname y pulsa **Iniciar Partida** o `Enter`.
2. El sistema genera cuatro números enteros entre 1 y 9, que pueden repetirse.
3. Cada expresión debe usar los cuatro números, exactamente una vez cada uno.
4. Una expresión correcta resuelve automáticamente el objetivo (1 a 10) que produce.
5. Los números no se consumen entre objetivos.
6. La partida termina cuando los diez objetivos están resueltos.

### Operaciones permitidas

| Modo | Operaciones |
|---|---|
| Normal | `+`, `−`, `×`, `÷` y paréntesis |
| Hardcore | Operaciones del modo Normal, potencia (`^`) y raíz cuadrada (`√`) |

En el modo Hardcore cada expresión debe incluir al menos una operación avanzada no trivial, con un máximo de una raíz cuadrada por expresión. Su ranking es independiente del modo Normal.

Se permiten resultados intermedios negativos y fraccionarios. No se permite dividir entre cero.

Reglas completas: [`docs/reglas-del-juego.md`](docs/reglas-del-juego.md)

---

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java (JDK) | 25 | Lenguaje principal |
| JavaFX | 25.0.1 | Interfaz gráfica (FXML y CSS) |
| MySQL | 8+ | Persistencia de jugadores y ranking |
| MySQL Connector/J | 9.4.0 | Conexión JDBC |
| Maven | 3.9+ | Compilación y gestión de dependencias |
| JUnit | 4.13.2 | Pruebas unitarias |
| Git y GitHub | - | Control de versiones |

---

## Arquitectura

La aplicación sigue el patrón **MVC**:

- **Model:** reglas del juego, sesión, objetivos, validación de expresiones, generación de números, búsqueda de soluciones y ranking.
- **View:** interfaz gráfica con JavaFX (FXML y CSS).
- **Controller:** conecta las acciones de la vista con el modelo.

Incluye una capa de acceso a datos (DAO) para la comunicación con MySQL.

```text
src/main/java/co/edu/poli/allten/
├── model/        # Lógica del juego
├── view/         # App.java (punto de entrada)
├── controller/   # Controladores de cada pantalla
└── database/     # Conexión JDBC y DAOs

src/main/resources/
├── co/edu/poli/allten/views/    # Pantallas FXML
├── co/edu/poli/allten/styles/   # Hojas de estilo CSS
└── db/schema.sql                # Esquema de la base de datos
```

---

## Estructura del repositorio

```text
Practicas-a-Sistemas/
├── src/
│   ├── main/
│   └── test/
├── docs/
│   ├── Diagramas/
│   ├── ux/
│   ├── configuracion-mysql.md
│   ├── pom.md
│   └── reglas-del-juego.md
├── pom.xml
└── README.md
```

---

## Instalación y ejecución

### Requisitos

- JDK 25
- Maven 3.9 o superior
- MySQL Server en ejecución (puerto 3306)
- Git

### Clonar el repositorio

```bash
git clone https://github.com/<tu-usuario>/Practicas-a-Sistemas.git
cd Practicas-a-Sistemas
```

### Crear la base de datos

Ejecutar el script `src/main/resources/db/schema.sql` en MySQL Workbench o desde la terminal:

```powershell
Get-Content -Raw .\src\main\resources\db\schema.sql | mysql -u root -p
```

### Configurar las credenciales

La aplicación lee las credenciales de MySQL desde variables de entorno.

PowerShell:

```powershell
$env:ALLTEN_DB_USER="root"
$env:ALLTEN_DB_PASSWORD="TU_CONTRASENA"
```

Linux / macOS:

```bash
export ALLTEN_DB_USER="root"
export ALLTEN_DB_PASSWORD="TU_CONTRASENA"
```

### Ejecutar la aplicación

```bash
mvn javafx:run
```

---

## Pruebas

Pruebas unitarias:

```bash
mvn test
```

Pruebas de integración con base de datos:

```powershell
mvn "-Dtest=DatabaseIntegrationTest" "-Dallten.db.integration=true" test
```

---

## Base de datos

El esquema está compuesto por dos tablas:

- **`jugador`:** `id` y `apodo` (único).
- **`partida`:** `jugador_id` (clave foránea), `tiempo_total`, `intentos`, `estado`, `modo` y `fecha`.

![Diagrama entidad-relación](docs/Diagramas/Diagrama%20Entidad%20Relacion%20.png)

---

## Experiencia de usuario (UX)

El diseño de la aplicación busca una experiencia de juego clara, sencilla e intuitiva. Se trabajó en:

- Identificación de las necesidades de los usuarios.
- Diseño de la interfaz.
- Organización y flujo de las pantallas.
- Pruebas y retroalimentación.
- Iteración y mejora del diseño.

---

## Metodología de desarrollo

El proyecto se desarrolla con la metodología **Scrum**, organizada mediante:

- Product Backlog.
- Historias de usuario.
- Tareas y subtareas.
- Sprints.
- Incrementos del producto.

---

## Control de versiones

El código se administra con **Git y GitHub**. La rama principal es `main` y cada integrante participa mediante commits asociados a las tareas e historias de usuario asignadas.

---

## Documentación

| Documento | Contenido |
|---|---|
| [`docs/reglas-del-juego.md`](docs/reglas-del-juego.md) | Reglas, modos y validación |
| [`docs/configuracion-mysql.md`](docs/configuracion-mysql.md) | Configuración de la base de datos y DAOs |
| [`docs/pom.md`](docs/pom.md) | Explicación del `pom.xml` |
| [`docs/ux/justificacion-uso-Mysql.md`](docs/ux/justificacion-uso-Mysql.md) | Justificación del uso de MySQL |
| [`docs/Diagramas/`](docs/Diagramas) | Diagrama entidad-relación y diagrama de clases |

---

## Equipo de desarrollo

| Integrante |
|---|
| José David Hurtado Espinosa |
| Deivid Alejandro Camacho Mendez |
| Darien Stiben Diaz Suarez |

---

## Estado del proyecto

**En desarrollo.**
