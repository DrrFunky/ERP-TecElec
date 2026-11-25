-- Esquema y datos de ejemplo de L2MADevs.
--
-- Forma de restauración:
--   mysql -u root -p < database/adte.sql
--
-- Cambios respecto al dump original (`tecelecerp.sql`):
--   * La base de datos se llama `adte`, igual que la URL JDBC por defecto.
--   * `fecha_fin` y `fecha_realizar_trabajo` aceptan NULL: una solicitud
--     recién creada todavía no tiene fecha de finalización.
--   * `contraseña` admite hashes PBKDF2 (255 caracteres) en vez de texto plano.
--   * Los IDs son AUTO_INCREMENT y las claves foranes se declaran al crear
--     cada tabla, en lugar de añadirlas después.

SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';
SET time_zone = '+00:00';
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `adte`
    CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `adte`;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `historial_de_solicitud`;
DROP TABLE IF EXISTS `registro_de_solicitud`;
DROP TABLE IF EXISTS `usuario`;
DROP TABLE IF EXISTS `estado`;
DROP TABLE IF EXISTS `rol`;
DROP TABLE IF EXISTS `tipo_de_prioridad`;
DROP TABLE IF EXISTS `tipo_de_solicitud`;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `rol` (
    `id_rol` INT NOT NULL AUTO_INCREMENT,
    `nombre_rol` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`id_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `estado` (
    `id_estado` INT NOT NULL AUTO_INCREMENT,
    `descripcion_estado` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`id_estado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `tipo_de_prioridad` (
    `id_tipo_prioridad` INT NOT NULL AUTO_INCREMENT,
    `descripcion_tipo_prioridad` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`id_tipo_prioridad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `tipo_de_solicitud` (
    `id_tipo_solicitud` INT NOT NULL AUTO_INCREMENT,
    `descripcion_tipo_solicitud` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`id_tipo_solicitud`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `usuario` (
    `id_usuario` INT NOT NULL AUTO_INCREMENT,
    `id_rol` INT NOT NULL,
    `nombre` VARCHAR(45) NOT NULL,
    `a_paterno` VARCHAR(45) NOT NULL,
    `a_materno` VARCHAR(45) NOT NULL,
    `correo` VARCHAR(45) NOT NULL,
    `contraseña` VARCHAR(255) DEFAULT NULL,
    `fecha_creacion_usuario` DATETIME NOT NULL,
    `direccion` VARCHAR(45) NOT NULL,
    `telefono` VARCHAR(45) NOT NULL,
    `usuario_login` VARCHAR(45) DEFAULT NULL,
    PRIMARY KEY (`id_usuario`),
    KEY `id_rol_idx` (`id_rol`),
    CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `registro_de_solicitud` (
    `id_solicitud` INT NOT NULL AUTO_INCREMENT,
    `id_usuario` INT NOT NULL,
    `id_tipo_solicitud` INT NOT NULL,
    `id_tipo_prioridad` INT NOT NULL,
    `id_estado` INT NOT NULL,
    `descripcion_solicitud` VARCHAR(100) NOT NULL,
    `ubicacion` VARCHAR(45) NOT NULL,
    `fecha_creacion` DATETIME NOT NULL,
    `fecha_fin` DATETIME DEFAULT NULL,
    `fecha_realizar_trabajo` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id_solicitud`),
    KEY `id_usuario_idx` (`id_usuario`),
    KEY `id_tipo_solicitud_idx` (`id_tipo_solicitud`),
    KEY `id_tipo_prioridad_idx` (`id_tipo_prioridad`),
    KEY `id_estado_idx` (`id_estado`),
    CONSTRAINT `fk_solicitud_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
    CONSTRAINT `fk_solicitud_tipo` FOREIGN KEY (`id_tipo_solicitud`) REFERENCES `tipo_de_solicitud` (`id_tipo_solicitud`),
    CONSTRAINT `fk_solicitud_prioridad` FOREIGN KEY (`id_tipo_prioridad`) REFERENCES `tipo_de_prioridad` (`id_tipo_prioridad`),
    CONSTRAINT `fk_solicitud_estado` FOREIGN KEY (`id_estado`) REFERENCES `estado` (`id_estado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `historial_de_solicitud` (
    `id_historial` INT NOT NULL AUTO_INCREMENT,
    `id_usuario` INT NOT NULL,
    `id_solicitud` INT NOT NULL,
    PRIMARY KEY (`id_historial`),
    KEY `id_usuario2_idx` (`id_usuario`),
    KEY `id_solicitud_idx` (`id_solicitud`),
    CONSTRAINT `fk_historial_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
    CONSTRAINT `fk_historial_solicitud` FOREIGN KEY (`id_solicitud`) REFERENCES `registro_de_solicitud` (`id_solicitud`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `rol` (`id_rol`, `nombre_rol`) VALUES
    (1, 'Gestor'),
    (2, 'Tecnico'),
    (3, 'Cliente');

INSERT INTO `estado` (`id_estado`, `descripcion_estado`) VALUES
    (1, 'Iniciar'),
    (2, 'En progreso'),
    (3, 'Finalizado');

INSERT INTO `tipo_de_prioridad` (`id_tipo_prioridad`, `descripcion_tipo_prioridad`) VALUES
    (1, 'Alta'),
    (2, 'Media'),
    (3, 'Baja');

INSERT INTO `tipo_de_solicitud` (`id_tipo_solicitud`, `descripcion_tipo_solicitud`) VALUES
    (1, 'Soporte'),
    (2, 'Proyecto');

-- Contraseñas hasheadas con PBKDF2-HMAC-SHA256 (120.000 iteraciones):
-- cgomez / pass123  y  lperez / tech456.
INSERT INTO `usuario`
    (`id_usuario`, `id_rol`, `nombre`, `a_paterno`, `a_materno`, `correo`,
     `contraseña`, `fecha_creacion_usuario`, `direccion`, `telefono`, `usuario_login`) VALUES
    (1, 1, 'Carlos', 'Gomez', 'Rojas', 'carlos.gomez@example.com',
     'PBKDF2$120000$sG4imeAPpiLgbzWiijQJeg==$5/jg/WQi8hPVWRFgMa6Ar+W4qam+IbB/dCw8O4MZKio=',
     '2025-01-05 10:35:00', 'Av. Central 123', '987654321', 'cgomez'),
    (2, 2, 'Luis', 'Perez', 'Morales', 'luis.perez@example.com',
     'PBKDF2$120000$jrKxXzeack5p+tLs+azlqg==$aSTI3NmPxUPX4MtLDrXm41ed9yknIJiw1pHshF8ukD4=',
     '2025-01-06 09:20:00', 'Calle Norte 456', '912345678', 'lperez'),
    (3, 3, 'Maria', 'Lopez', 'Diaz', 'maria.lopez@example.com',
     NULL, '2025-01-07 14:50:00', 'Av. Sur 789', '923456789', ''),
    (4, 3, 'Jose', 'Vargas', 'Torres', 'jose.vargas@example.com',
     NULL, '2025-02-02 11:25:00', 'Calle Este 321', '934567890', '');

INSERT INTO `registro_de_solicitud`
    (`id_solicitud`, `id_usuario`, `id_tipo_solicitud`, `id_tipo_prioridad`,
     `id_estado`, `descripcion_solicitud`, `ubicacion`, `fecha_creacion`,
     `fecha_fin`, `fecha_realizar_trabajo`) VALUES
    (1, 3, 1, 1, 1, 'Revisión de cableado defectuoso en oficina',
     'Oficina Central', '2025-03-11 09:00:00', NULL, '2025-03-18 09:00:00'),
    (2, 4, 2, 2, 2, 'Instalación de nuevos medidores eléctricos',
     'Sucursal Norte', '2025-03-12 10:00:00', NULL, '2025-03-20 12:15:02'),
    (3, 3, 1, 3, 3, 'Reemplazo de interruptor dañado',
     'Sucursal Sur', '2025-03-14 14:30:00', '2025-03-16 18:00:00', '2025-03-16 10:00:00');

INSERT INTO `historial_de_solicitud` (`id_historial`, `id_usuario`, `id_solicitud`) VALUES
    (1, 2, 1),
    (2, 2, 2),
    (3, 1, 2),
    (4, 2, 3);
