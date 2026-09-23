# Alternativas a MySQL

## MariaSQL
- Creado por los mismos desarrolladores de MySQL cuando Oracle adquirió el proyecto original.  
- Su objetivo es mantener el software 100% libre.  
- Migración sencilla: no requiere cambiar ni una sola línea de código si proviene de MySQL.  
- Motores de almacenamiento muy optimizados, lo que lo hace más rápido que otras bases de datos en ciertos casos.  

## PostgreSQL
- Sistema de base de datos relacional de código abierto considerado uno de los más avanzados.  
- Enfocado en la estabilidad y manejo de datos complejos.  
- Soporte híbrido para consultar y guardar datos en formato JSON de forma nativa y veloz.  
- Funciones avanzadas: datos geométricos, búsqueda de texto avanzada y análisis de datos complejos.  

## SQLite
- Base de datos embebida: no requiere instalación en un servidor.  
- Toda la información se guarda en un único archivo de texto en el dispositivo.  
- No necesita usuarios, contraseñas, puertos ni mantenimiento de servidores.  
- Muy liviana y rápida en lectura, con bajo consumo de recursos.  

## Microsoft SQL Server
- Ideal para trabajar dentro del ecosistema Microsoft.  
- Integración perfecta con .NET, Azure, PowerBI y Visual Studio.  
- Herramientas visuales intuitivas para diseñar y monitorear bases de datos.  


# Comparar Alternativas a MySQL

| **Bases de Datos** | **Fortalezas principales** | **Limitaciones frente a MySQL** |
|---------------------|----------------------------|---------------------------------|
| **MySQL** | - Relacional, multiplataforma, código abierto<br>- Alto rendimiento en consultas<br>- Transacciones ACID con InnoDB<br>- Seguridad avanzada<br>- Bajo consumo de recursos<br>- Soporte nativo JSON<br>- Conectores para juegos y backend | No aplica |
| **MariaDB** | - 100% libre y compatible con MySQL<br>- Migración sin cambios de código<br>- Motores optimizados y buen rendimiento | - Menor soporte empresarial que Oracle/MySQL<br>- Comunidad activa pero menos extendida que MySQL |
| **PostgreSQL** | - Muy avanzado en manejo de datos complejos<br>- Soporte híbrido JSON + relacional<br>- Funciones avanzadas (geométricos, búsquedas, análisis) | - Mayor consumo de recursos<br>- Configuración más compleja<br>- Rendimiento inferior en consultas simples frente a MySQL |
| **SQLite** | - Embebida, sin servidor<br>- Extremadamente ligera<br>- Ideal para apps móviles o locales | - No apta para sistemas distribuidos o de gran escala<br>- Sin gestión avanzada de usuarios ni seguridad robusta |
| **SQL Server** | - Integración perfecta con ecosistema Microsoft (.NET, Azure, PowerBI)<br>- Herramientas visuales intuitivas | - Licenciamiento costoso<br>- Menos flexible fuera del entorno Microsoft<br>- Mayor consumo de recursos |


# Justificación del uso de MySQL

## Alto rendimiento en consultas
El alto rendimiento en consultas simples y ordenamientos de MySQL es extremadamente rápido para operaciones de lectura y escritura básicas, como insertar los puntajes y ordenarlos en ese ranking que busca el proyecto.  
Con una sola línea de SQL se pueden obtener los mejores puntajes de los jugadores de manera eficiente.  

## Bajo consumo de recursos
MySQL usa muy pocos recursos, lo que lo hace ideal en casos como el nuestro porque son juegos básicos.  
Permite conectar la base de datos en servidores con planes básicos y consume menos CPU y RAM que otras bases de datos como PostgreSQL o SQL Server.  
Esto mejora el rendimiento del juego en diferentes máquinas y la escalabilidad.  

## Seguridad
También permite definir privilegios por usuario, lo que garantiza que solo el backend del juego pueda modificar los puntajes.  
Esto ayuda a proteger la información almacenada.  

## Soporte multiplataforma
MySQL tiene soporte multiplataforma, lo que permite usar diferentes motores para crear el juego.  

---

## Conclusión
MySQL es la opción más balanceada para este proyecto porque combina velocidad, simplicidad, bajo consumo de recursos y facilidad de integración con diferentes motores de juegos.  
Para el sistema de ranking de puntajes, ofrece consultas rápidas y facilidad de organización de la base de datos.  
