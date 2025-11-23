package com.arquitectura.asistente.datos.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Responsabilidad: Gestionar los datos iniciales (seeders) de la base de datos SQLite
 * 
 * Este archivo contiene todos los datos de prueba que se insertan
 * cuando se crea la base de datos por primera vez.
 * 
 * Separado de DatabaseHelper para mantener clara la separación de responsabilidades:
 * - DatabaseMigrations: Estructura de la base de datos (esquema)
 * - DatabaseSeeder: Datos iniciales de prueba
 * - DatabaseHelper: Acceso a datos (conexión)
 * 
 * Adaptado de MySQL a SQLite para Android
 */
public class DatabaseSeeder {
    private static final String TAG = "DatabaseSeeder";

    /**
     * Inserta todos los datos de prueba en la base de datos.
     * Se ejecuta después de crear las tablas en onCreate.
     * 
     * @param db Base de datos SQLite donde se insertarán los datos
     */
    public static void seed(SQLiteDatabase db) {
        // Verificar si ya hay datos
        if (tieneDatos(db)) {
            Log.d(TAG, "Datos iniciales ya existen, omitiendo inserción");
            return;
        }

        seedUsuarios(db);
        seedMaterias(db);
        seedGrupos(db);
        seedHorarios(db);
        seedBoletas(db);
        seedAsistencias(db);

        Log.d(TAG, "Todos los datos de prueba insertados correctamente");
    }

    /**
     * Verifica si ya existen datos en la base de datos
     */
    private static boolean tieneDatos(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) as count FROM usuarios";
        try (Cursor cursor = db.rawQuery(sql, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        }
        return false;
    }

    /**
     * Inserta usuarios de prueba en la base de datos.
     * Solo Estudiante y Docente (sin Admin)
     */
    private static void seedUsuarios(SQLiteDatabase db) {
        String sql = "INSERT INTO usuarios(nombres, apellidos, username, contrasena, registro, rol) VALUES " +
                "('Ana', 'García', 'estudiante1', '1234', '211882', 'Estudiante'), " +
                "('Juan', 'Pérez', 'estudiante2', '1234', '212732', 'Estudiante'), " +
                "('Carlos', 'López', 'estudiante3', '1234', '210882', 'Estudiante'), " +
                "('Marcos', 'Rodríguez', 'docente1', '1234', '342232', 'Docente'), " +
                "('Maria', 'Fernández', 'docente2', '1234', '45532', 'Docente'), " +
                "('Julia', 'Martínez', 'docente3', '1234', '56322', 'Docente'), " +
                "('Roberto', 'Sánchez', 'docente4', '1234', '67890', 'Docente'), " +
                "('Laura', 'González', 'docente5', '1234', '78901', 'Docente')";

        try {
            db.execSQL(sql);
            Log.d(TAG, "8 usuarios de prueba insertados (3 estudiantes, 5 docentes)");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar usuarios: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar usuarios", e);
        }
    }

    /**
     * Inserta materias de prueba en la base de datos.
     * 20 materias de diferentes niveles académicos.
     */
    private static void seedMaterias(SQLiteDatabase db) {
        String sql = "INSERT INTO materias(nombre, sigla, nivel) VALUES " +
                "('Programación I', 'PROG1', 1), " +
                "('Programación II', 'PROG2', 2), " +
                "('Programación III', 'PROG3', 3), " +
                "('Base de Datos I', 'BD1', 2), " +
                "('Base de Datos II', 'BD2', 3), " +
                "('Estructura de Datos', 'ED', 2), " +
                "('Algoritmos y Complejidad', 'ALG', 3), " +
                "('Sistemas Operativos', 'SO', 3), " +
                "('Redes de Computadoras', 'REDES', 4), " +
                "('Ingeniería de Software', 'IS', 4), " +
                "('Arquitectura de Computadoras', 'ARQ', 2), " +
                "('Matemática Discreta', 'MD', 1), " +
                "('Cálculo I', 'CAL1', 1), " +
                "('Cálculo II', 'CAL2', 2), " +
                "('Algebra Lineal', 'ALG_LIN', 2), " +
                "('Física I', 'FIS1', 1), " +
                "('Física II', 'FIS2', 2), " +
                "('Química General', 'QUIM', 1), " +
                "('Ética Profesional', 'ETICA', 3), " +
                "('Gestión de Proyectos', 'GP', 4)";

        try {
            db.execSQL(sql);
            Log.d(TAG, "20 materias de prueba insertadas");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar materias: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar materias", e);
        }
    }

    /**
     * Inserta grupos de prueba en la base de datos.
     * PATRON STRATEGY CON DATOS DE BD:
     * Incluye valores de tolerancia_minutos y tipo_estrategia variados
     */
    private static void seedGrupos(SQLiteDatabase db) {
        String sql = "INSERT INTO grupos(materia_id, materia_nombre, docente_id, docente_nombre, semestre, gestion, capacidad, grupo, tolerancia_minutos, tipo_estrategia) VALUES " +
                "(1, 'Programación I', 4, 'Marcos Rodríguez', 1, 2025, 30, 'A', 10, 'RETRASO'), " +
                "(1, 'Programación I', 5, 'Maria Fernández', 1, 2025, 25, 'B', 15, 'PRESENTE'), " +
                "(2, 'Programación II', 4, 'Marcos Rodríguez', 1, 2025, 28, 'A', 10, 'RETRASO'), " +
                "(2, 'Programación II', 6, 'Julia Martínez', 1, 2025, 30, 'B', 20, 'FALTA'), " +
                "(3, 'Programación III', 4, 'Marcos Rodríguez', 1, 2025, 20, 'A', 5, 'RETRASO'), " +
                "(4, 'Base de Datos I', 5, 'Maria Fernández', 1, 2025, 30, 'A', 10, 'PRESENTE'), " +
                "(4, 'Base de Datos I', 7, 'Roberto Sánchez', 1, 2025, 25, 'B', 15, 'RETRASO'), " +
                "(5, 'Base de Datos II', 5, 'Maria Fernández', 1, 2025, 22, 'A', 10, 'RETRASO'), " +
                "(6, 'Estructura de Datos', 6, 'Julia Martínez', 1, 2025, 28, 'A', 10, 'FALTA'), " +
                "(6, 'Estructura de Datos', 7, 'Roberto Sánchez', 1, 2025, 30, 'B', 20, 'RETRASO'), " +
                "(7, 'Algoritmos y Complejidad', 4, 'Marcos Rodríguez', 1, 2025, 20, 'A', 5, 'RETRASO'), " +
                "(8, 'Sistemas Operativos', 7, 'Roberto Sánchez', 1, 2025, 25, 'A', 10, 'RETRASO'), " +
                "(8, 'Sistemas Operativos', 8, 'Laura González', 1, 2025, 28, 'B', 15, 'PRESENTE'), " +
                "(9, 'Redes de Computadoras', 8, 'Laura González', 1, 2025, 30, 'A', 10, 'RETRASO'), " +
                "(10, 'Ingeniería de Software', 5, 'Maria Fernández', 1, 2025, 25, 'A', 15, 'RETRASO'), " +
                "(11, 'Arquitectura de Computadoras', 6, 'Julia Martínez', 1, 2025, 30, 'A', 10, 'RETRASO'), " +
                "(12, 'Matemática Discreta', 7, 'Roberto Sánchez', 1, 2025, 35, 'A', 10, 'RETRASO'), " +
                "(13, 'Cálculo I', 8, 'Laura González', 1, 2025, 40, 'A', 15, 'RETRASO'), " +
                "(19, 'Ética Profesional', 5, 'Maria Fernández', 1, 2025, 50, 'A', 20, 'PRESENTE'), " +
                "(20, 'Gestión de Proyectos', 6, 'Julia Martínez', 1, 2025, 30, 'A', 10, 'RETRASO')";

        try {
            db.execSQL(sql);
            Log.d(TAG, "20 grupos de prueba insertados con tolerancias configurables");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar grupos: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar grupos", e);
        }
    }

    /**
     * Inserta horarios de prueba en la base de datos.
     */
    private static void seedHorarios(SQLiteDatabase db) {
        String sql = "INSERT INTO horarios(grupo_id, dia, hora_inicio, hora_fin) VALUES " +
                "(1, 'Lunes', '08:00', '10:00'), " +
                "(1, 'Miércoles', '08:00', '10:00'), " +
                "(1, 'Viernes', '07:00', '09:00'), " +
                "(2, 'Martes', '10:00', '12:00'), " +
                "(2, 'Jueves', '10:00', '12:00'), " +
                "(2, 'Viernes', '07:00', '09:00'), " +
                "(3, 'Lunes', '14:00', '16:00'), " +
                "(3, 'Miércoles', '14:00', '16:00'), " +
                "(3, 'Viernes', '07:00', '09:00'), " +
                "(4, 'Martes', '14:00', '16:00'), " +
                "(4, 'Jueves', '14:00', '16:00'), " +
                "(4, 'Viernes', '07:00', '09:00'), " +
                "(5, 'Lunes', '16:00', '18:00'), " +
                "(5, 'Miércoles', '16:00', '18:00'), " +
                "(5, 'Viernes', '07:00', '09:00'), " +
                "(6, 'Martes', '08:00', '10:00'), " +
                "(6, 'Viernes', '01:00', '03:00'), " +
                "(7, 'Lunes', '10:00', '12:00'), " +
                "(7, 'Miércoles', '10:00', '12:00'), " +
                "(7, 'Viernes', '01:00', '03:00'), " +
                "(8, 'Martes', '16:00', '18:00'), " +
                "(8, 'Jueves', '16:00', '18:00'), " +
                "(8, 'Viernes', '01:00', '03:00'), " +
                "(9, 'Lunes', '08:00', '10:00'), " +
                "(9, 'Viernes', '01:00', '03:00'), " +
                "(10, 'Martes', '10:00', '12:00'), " +
                "(10, 'Viernes', '01:00', '03:00'), " +
                "(11, 'Miércoles', '14:00', '16:00'), " +
                "(11, 'Viernes', '14:00', '16:00'), " +
                "(12, 'Lunes', '10:00', '12:00'), " +
                "(12, 'Miércoles', '10:00', '12:00'), " +
                "(13, 'Martes', '08:00', '10:00'), " +
                "(13, 'Jueves', '08:00', '10:00'), " +
                "(14, 'Lunes', '14:00', '16:00'), " +
                "(14, 'Miércoles', '14:00', '16:00'), " +
                "(15, 'Martes', '14:00', '16:00'), " +
                "(15, 'Jueves', '14:00', '16:00'), " +
                "(16, 'Lunes', '16:00', '18:00'), " +
                "(16, 'Miércoles', '16:00', '18:00'), " +
                "(17, 'Martes', '16:00', '18:00'), " +
                "(17, 'Jueves', '16:00', '18:00'), " +
                "(18, 'Lunes', '08:00', '10:00'), " +
                "(18, 'Miércoles', '08:00', '10:00'), " +
                "(19, 'Viernes', '10:00', '12:00'), " +
                "(20, 'Viernes', '14:00', '16:00')";

        try {
            db.execSQL(sql);
            Log.d(TAG, "45 horarios de prueba insertados");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar horarios: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar horarios", e);
        }
    }

    /**
     * Inserta boletas (inscripciones) de prueba en la base de datos.
     */
    private static void seedBoletas(SQLiteDatabase db) {
        String sql = "INSERT INTO boletas(alumno_id, grupo_id, fecha, semestre, gestion) VALUES " +
                "(1, 1, '2025-01-15', 1, 2025), " +
                "(1, 4, '2025-01-15', 1, 2025), " +
                "(1, 6, '2025-01-15', 1, 2025), " +
                "(1, 9, '2025-01-15', 1, 2025), " +
                "(1, 12, '2025-01-15', 1, 2025), " +
                "(2, 1, '2025-01-16', 1, 2025), " +
                "(2, 3, '2025-01-16', 1, 2025), " +
                "(2, 7, '2025-01-16', 1, 2025), " +
                "(2, 10, '2025-01-16', 1, 2025), " +
                "(2, 13, '2025-01-16', 1, 2025), " +
                "(3, 2, '2025-01-17', 1, 2025), " +
                "(3, 5, '2025-01-17', 1, 2025), " +
                "(3, 8, '2025-01-17', 1, 2025), " +
                "(3, 11, '2025-01-17', 1, 2025), " +
                "(3, 14, '2025-01-17', 1, 2025), " +
                "(1, 15, '2025-01-18', 1, 2025), " +
                "(2, 16, '2025-01-18', 1, 2025), " +
                "(3, 17, '2025-01-18', 1, 2025), " +
                "(1, 19, '2025-01-19', 1, 2025), " +
                "(2, 20, '2025-01-19', 1, 2025), " +
                "(3, 19, '2025-01-19', 1, 2025)";

        try {
            db.execSQL(sql);
            Log.d(TAG, "21 boletas (inscripciones) de prueba insertadas");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar boletas: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar boletas", e);
        }
    }

    /**
     * Inserta asistencias de prueba en la base de datos.
     */
    private static void seedAsistencias(SQLiteDatabase db) {
        String sql = "INSERT INTO asistencias(alumno_id, grupo_id, fecha, hora_marcada, estado) VALUES " +
                "(1, 1, '2025-01-20', '08:05', 'PRESENTE'), " +
                "(1, 1, '2025-01-22', '08:15', 'RETRASO'), " +
                "(1, 4, '2025-01-21', '14:02', 'PRESENTE'), " +
                "(1, 4, '2025-01-23', '14:25', 'RETRASO'), " +
                "(1, 6, '2025-01-21', '08:10', 'PRESENTE'), " +
                "(1, 6, '2025-01-24', '08:35', 'FALTA'), " +
                "(1, 9, '2025-01-20', '08:03', 'PRESENTE'), " +
                "(1, 9, '2025-01-24', '08:12', 'PRESENTE'), " +
                "(1, 12, '2025-01-22', '10:08', 'PRESENTE'), " +
                "(2, 1, '2025-01-20', '08:07', 'PRESENTE'), " +
                "(2, 1, '2025-01-22', '08:20', 'RETRASO'), " +
                "(2, 3, '2025-01-20', '14:05', 'PRESENTE'), " +
                "(2, 3, '2025-01-22', '14:18', 'RETRASO'), " +
                "(2, 7, '2025-01-21', '10:04', 'PRESENTE'), " +
                "(2, 7, '2025-01-23', '10:22', 'RETRASO'), " +
                "(2, 10, '2025-01-21', '10:06', 'PRESENTE'), " +
                "(2, 10, '2025-01-24', '10:40', 'FALTA'), " +
                "(2, 13, '2025-01-22', '08:09', 'PRESENTE'), " +
                "(3, 2, '2025-01-21', '10:12', 'PRESENTE'), " +
                "(3, 2, '2025-01-23', '10:28', 'RETRASO'), " +
                "(3, 5, '2025-01-20', '16:02', 'PRESENTE'), " +
                "(3, 5, '2025-01-22', '16:15', 'RETRASO'), " +
                "(3, 8, '2025-01-21', '16:05', 'PRESENTE'), " +
                "(3, 8, '2025-01-23', '16:30', 'FALTA'), " +
                "(3, 11, '2025-01-22', '14:08', 'PRESENTE'), " +
                "(3, 11, '2025-01-24', '14:25', 'RETRASO'), " +
                "(3, 14, '2025-01-20', '14:03', 'PRESENTE'), " +
                "(3, 14, '2025-01-22', '14:20', 'RETRASO'), " +
                "(1, 15, '2025-01-21', '14:06', 'PRESENTE'), " +
                "(2, 16, '2025-01-20', '16:04', 'PRESENTE'), " +
                "(3, 17, '2025-01-21', '16:10', 'PRESENTE'), " +
                "(1, 19, '2025-01-24', '10:15', 'RETRASO'), " +
                "(2, 20, '2025-01-24', '14:12', 'PRESENTE'), " +
                "(3, 19, '2025-01-24', '10:08', 'PRESENTE')";

        try {
            db.execSQL(sql);
            Log.d(TAG, "34 asistencias de prueba insertadas con hora_marcada y estado");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar asistencias: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar asistencias", e);
        }
    }
}

