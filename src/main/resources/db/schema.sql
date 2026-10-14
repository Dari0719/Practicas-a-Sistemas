-- ADVERTENCIA: este script elimina permanentemente la base allten si ya existe.
DROP DATABASE IF EXISTS allten;

CREATE DATABASE allten
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE allten;

CREATE TABLE jugador (
    id INT NOT NULL AUTO_INCREMENT,
    apodo VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_jugador_apodo UNIQUE (apodo)
);

CREATE TABLE partida (
    id INT NOT NULL AUTO_INCREMENT,
    jugador_id INT NOT NULL,
    tiempo_total BIGINT NOT NULL,
    intentos INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    modo VARCHAR(20) NOT NULL,
    fecha DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_partida_jugador
        FOREIGN KEY (jugador_id) REFERENCES jugador (id),
    INDEX idx_ranking (estado, modo, tiempo_total, intentos)
);
