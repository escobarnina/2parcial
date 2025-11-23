package Arquinuevo.datos;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper para la gestión de la base de datos MySQL
 * Capa de Datos - Gestión de Base de Datos
 * Base de datos MySQL para sistema de asistencia académica
 */
public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "asistencia_db";
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    private static DatabaseHelper instance;
    private Connection connection;
    private String dbUrl;

    private DatabaseHelper() {
        // Base de datos MySQL - se conecta al servidor MySQL
        this.dbUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME + 
                     "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        initializeDatabase();
    }

    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    /**
     * Inicializa la base de datos y crea las tablas necesarias
     */
    private void initializeDatabase() {
        try {
            // Primero conectar sin especificar base de datos para crearla si no existe
            String urlSinDB = "jdbc:mysql://" + HOST + ":" + PORT + 
                             "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            Connection tempConn = DriverManager.getConnection(urlSinDB, USERNAME, PASSWORD);
            
            // Crear base de datos si no existe
            try (Statement stmt = tempConn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
                logger.info("Base de datos " + DATABASE_NAME + " verificada/creada");
            }
            tempConn.close();

            // Conectar a la base de datos específica
            connection = DriverManager.getConnection(dbUrl, USERNAME, PASSWORD);
            
            // Crear tablas y datos iniciales
            DatabaseMigrations.createTables(connection);
            DatabaseSeeder.seed(connection);
            
            Logger.getLogger(TAG).info("Base de datos MySQL inicializada correctamente");
        } catch (SQLException e) {
            Logger.getLogger(TAG).log(Level.SEVERE, "Error al inicializar la base de datos", e);
            throw new RuntimeException("Error al inicializar la base de datos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la conexión a la base de datos
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbUrl, USERNAME, PASSWORD);
        }
        return connection;
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Logger.getLogger(TAG).info("Conexion cerrada correctamente");
            }
        } catch (SQLException e) {
            Logger.getLogger(TAG).log(Level.SEVERE, "Error al cerrar la conexion", e);
        }
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    private static final Logger logger = Logger.getLogger(DatabaseHelper.class.getName());
}

