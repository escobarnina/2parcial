package Arquinuevo.datos;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Responsabilidad: Gestionar las migraciones de la base de datos MySQL
 * 
 * Este archivo contiene todas las definiciones de esquema (CREATE TABLE)
 * y las migraciones entre versiones de la base de datos.
 * 
 * Separado de DatabaseHelper para mantener clara la separación de responsabilidades:
 * - DatabaseMigrations: Estructura de la base de datos (esquema)
 * - DatabaseSeeder: Datos iniciales de prueba
 * - DatabaseHelper: Acceso a datos (conexión)
 */
public class DatabaseMigrations {
    private static final Logger logger = Logger.getLogger(DatabaseMigrations.class.getName());

    /**
     * Crea todas las tablas de la base de datos.
     * Se ejecuta cuando se crea la base de datos por primera vez.
     * 
     * @param conn Conexión a MySQL donde se crearán las tablas
     */
    public static void createTables(Connection conn) throws SQLException {
        createUsuariosTable(conn);
        createMateriasTable(conn);
        createGruposTable(conn);
        createHorariosTable(conn);
        createBoletasTable(conn);
        createAsistenciasTable(conn);
        logger.info("Todas las tablas creadas correctamente");
    }

    /**
     * Migra la base de datos desde una versión anterior a una nueva.
     * 
     * @param conn Conexión a MySQL
     * @param oldVersion Versión anterior de la base de datos
     * @param newVersion Nueva versión de la base de datos
     */
    public static void migrate(Connection conn, int oldVersion, int newVersion) throws SQLException {
        // Por ahora, recreamos todas las tablas
        // En producción, esto debería ser migraciones incrementales
        dropAllTables(conn);
        createTables(conn);
        logger.info("Migración completada de versión " + oldVersion + " a " + newVersion);
    }

    /**
     * Crea la tabla de usuarios.
     * Solo Estudiante y Docente (sin Admin)
     */
    private static void createUsuariosTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nombres VARCHAR(100) NOT NULL, " +
                "apellidos VARCHAR(100) NOT NULL, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "contrasena VARCHAR(255) NOT NULL, " +
                "registro VARCHAR(20) NOT NULL, " +
                "rol ENUM('Estudiante', 'Docente') NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Tabla usuarios creada");
        }
    }

    /**
     * Crea la tabla de materias.
     */
    private static void createMateriasTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS materias (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "sigla VARCHAR(20) NOT NULL UNIQUE, " +
                "nivel INT NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Tabla materias creada");
        }
    }

    /**
     * Crea la tabla de grupos.
     * 
     * PATRON STRATEGY - Campo tolerancia_minutos:
     * Define el tiempo máximo (en minutos) permitido para considerar un retraso
     * antes de marcar FALTA. Este campo permite personalizar la política de 
     * asistencia por grupo, haciendo flexible el cálculo del estado.
     */
    private static void createGruposTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS grupos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "materia_id INT NOT NULL, " +
                "materia_nombre VARCHAR(100) NOT NULL, " +
                "docente_id INT NOT NULL, " +
                "docente_nombre VARCHAR(200) NOT NULL, " +
                "grupo VARCHAR(10) NOT NULL, " +
                "semestre INT NOT NULL CHECK(semestre IN (1, 2)), " +
                "gestion INT NOT NULL, " +
                "capacidad INT NOT NULL, " +
                "nro_inscritos INT DEFAULT 0, " +
                "tolerancia_minutos INT DEFAULT 10 NOT NULL CHECK(tolerancia_minutos >= 0 AND tolerancia_minutos <= 60), " +
                "tipo_estrategia ENUM('PRESENTE', 'RETRASO', 'FALTA') DEFAULT 'RETRASO' NOT NULL, " +
                "FOREIGN KEY(materia_id) REFERENCES materias(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(docente_id) REFERENCES usuarios(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Tabla grupos creada");
        }
    }

    /**
     * Crea la tabla de horarios.
     */
    private static void createHorariosTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS horarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "grupo_id INT NOT NULL, " +
                "dia VARCHAR(20) NOT NULL, " +
                "hora_inicio VARCHAR(5) NOT NULL, " +
                "hora_fin VARCHAR(5) NOT NULL, " +
                "FOREIGN KEY(grupo_id) REFERENCES grupos(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Tabla horarios creada");
        }
    }

    /**
     * Crea la tabla de boletas (inscripciones).
     */
    private static void createBoletasTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS boletas (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "alumno_id INT NOT NULL, " +
                "grupo_id INT NOT NULL, " +
                "fecha DATE NOT NULL, " +
                "semestre INT NOT NULL, " +
                "gestion INT NOT NULL, " +
                "FOREIGN KEY(alumno_id) REFERENCES usuarios(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(grupo_id) REFERENCES grupos(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Tabla boletas creada");
        }
    }

    /**
     * Crea la tabla de asistencias.
     * 
     * Campos agregados:
     * - hora_marcada: Hora en que el alumno marcó asistencia (formato HH:mm)
     * - estado: Estado de la asistencia (PRESENTE, RETRASO, FALTA)
     */
    private static void createAsistenciasTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS asistencias (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "alumno_id INT NOT NULL, " +
                "grupo_id INT NOT NULL, " +
                "fecha DATE NOT NULL, " +
                "hora_marcada VARCHAR(5), " +
                "estado ENUM('PRESENTE', 'RETRASO', 'FALTA'), " +
                "FOREIGN KEY(alumno_id) REFERENCES usuarios(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(grupo_id) REFERENCES grupos(id) ON DELETE CASCADE, " +
                "UNIQUE KEY unique_asistencia (alumno_id, grupo_id, fecha)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Tabla asistencias creada");
        }
    }

    /**
     * Elimina todas las tablas (usado en migraciones).
     * CUIDADO: Esto elimina todos los datos.
     */
    private static void dropAllTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Desactivar verificación de claves foráneas temporalmente
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            stmt.execute("DROP TABLE IF EXISTS asistencias");
            stmt.execute("DROP TABLE IF EXISTS boletas");
            stmt.execute("DROP TABLE IF EXISTS horarios");
            stmt.execute("DROP TABLE IF EXISTS grupos");
            stmt.execute("DROP TABLE IF EXISTS materias");
            stmt.execute("DROP TABLE IF EXISTS usuarios");
            
            // Reactivar verificación de claves foráneas
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            logger.info("Todas las tablas eliminadas");
        }
    }
}

