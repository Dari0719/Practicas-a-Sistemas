# All Ten Game

Aplicacion de escritorio basada en un juego de logica y calculo, desarrollada con Java y organizada bajo MVC.

## Estructura

```text
src/main/java/com/juego/
├── controller/  # Coordinacion entre vista y modelo
├── database/    # Acceso y configuracion de la base de datos
├── model/       # Entidades y reglas del juego
└── view/        # Vistas JavaFX

docs/
├── entregas/    # Documentos de las entregas
└── ux/          # Disenos y evidencias UX
```

## Flujo de trabajo

1. Crear una rama para la tarea: `feature/nombre-de-la-tarea`.
2. Realizar cambios pequenos y relacionados con esa tarea.
3. Ejecutar `mvn clean test` antes de crear el commit.
4. Crear un commit descriptivo y abrir un Pull Request hacia `main`.

Las reglas definitivas del juego y la configuracion concreta de la base de datos se definiran durante los siguientes Sprints.
