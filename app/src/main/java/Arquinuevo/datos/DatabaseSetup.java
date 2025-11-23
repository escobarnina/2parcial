package Arquinuevo.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilidad para ejecutar migraciones y seeders de forma independiente
 * Capa de Datos - Herramienta de configuración
 */
public class DatabaseSetup {
    private static final Logger logger = Logger.getLogger(DatabaseSetup.class.getName());
    private static final String DATABASE_NAME = "asistencia_db";
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        logger.info("=== INICIANDO CONFIGURACION DE BASE DE DATOS ===");
        
        try {
            // Conectar sin especificar base de datos
            String urlSinDB = "jdbc:mysql://" + HOST + ":" + PORT + 
                             "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            Connection tempConn = DriverManager.getConnection(urlSinDB, USERNAME, PASSWORD);
            
            // Crear base de datos si no existe
            logger.info("Creando base de datos si no existe...");
            try (var stmt = tempConn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
                logger.info("Base de datos " + DATABASE_NAME + " verificada/creada");
            }
            tempConn.close();

            // Conectar a la base de datos específica
            String dbUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME + 
                          "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            Connection conn = DriverManager.getConnection(dbUrl, USERNAME, PASSWORD);
            
            logger.info("=== EJECUTANDO MIGRACIONES ===");
            DatabaseMigrations.createTables(conn);
            logger.info("Migraciones completadas exitosamente");
            
            logger.info("=== EJECUTANDO SEEDERS ===");
            DatabaseSeeder.seed(conn);
            logger.info("Seeders completados exitosamente");
            
            conn.close();
            
            logger.info("=== CONFIGURACION COMPLETADA ===");
            logger.info("Base de datos lista para usar!");
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al configurar la base de datos", e);
            System.err.println("ERROR: " + e.getMessage());
            System.err.println("\nVerifica que:");
            System.err.println("1. MySQL este ejecutandose");
            System.err.println("2. Las credenciales en DatabaseSetup.java sean correctas");
            System.err.println("3. El usuario tenga permisos para crear bases de datos");
            System.exit(1);
        }
    }
}

