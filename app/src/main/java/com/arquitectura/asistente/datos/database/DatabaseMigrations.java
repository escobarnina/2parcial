package com.arquitectura.asistente.datos.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Responsabilidad: Gestionar las migraciones de la base de datos SQLite
 * 
 * Este archivo contiene todas las definiciones de esquema (CREATE TABLE)
 * y las migraciones entre versiones de la base de datos.
 * 
 * Adaptado de MySQL a SQLite para Android
 */
public class DatabaseMigrations {
    private static final String TAG = "DatabaseMigrations";

    /**
     * Crea todas las tablas de la base de datos.
     * Se ejecuta cuando se crea la base de datos por primera vez.
     * 
     * @param db Base de datos SQLite donde se crearán las tablas
     */
    public static void createTables(SQLiteDatabase db) {
        createUsuariosTable(db);
        createMateriasTable(db);
        createGruposTable(db);
        createHorariosTable(db);
        createBoletasTable(db);
        createAsistenciasTable(db);
        Log.d(TAG, "Todas las tablas creadas correctamente");
    }

    /**
     * Migra la base de datos desde una versión anterior a una nueva.
     * 
     * @param db Base de datos SQLite
     * @param oldVersion Versión anterior de la base de datos
     * @param newVersion Nueva versión de la base de datos
     */
    public static void migrate(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Por ahora, recreamos todas las tablas
        // En producción, esto debería ser migraciones incrementales
        dropAllTables(db);
        createTables(db);
        Log.d(TAG, "Migración completada de versión " + oldVersion + " a " + newVersion);
    }

    /**
     * Crea la tabla de usuarios.
     * Solo Estudiante y Docente (sin Admin)
     */
    private static void createUsuariosTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombres TEXT NOT NULL, " +
                "apellidos TEXT NOT NULL, " +
                "username TEXT UNIQUE NOT NULL, " +
                "contrasena TEXT NOT NULL, " +
                "registro TEXT NOT NULL, " +
                "rol TEXT NOT NULL CHECK(rol IN ('Estudiante', 'Docente'))" +
                ")";
        
        db.execSQL(sql);
        Log.d(TAG, "Tabla usuarios creada");
    }

    /**
     * Crea la tabla de materias.
     */
    private static void createMateriasTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS materias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "sigla TEXT NOT NULL UNIQUE, " +
                "nivel INTEGER NOT NULL" +
                ")";
        
        db.execSQL(sql);
        Log.d(TAG, "Tabla materias creada");
    }

    /**
     * Crea la tabla de grupos.
     * 
     * PATRON STRATEGY - Campo tolerancia_minutos:
     * Define el tiempo máximo (en minutos) permitido para considerar un retraso
     * antes de marcar FALTA. Este campo permite personalizar la política de 
     * asistencia por grupo, haciendo flexible el cálculo del estado.
     */
    private static void createGruposTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS grupos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "materia_id INTEGER NOT NULL, " +
                "docente_id INTEGER NOT NULL, " +
                "grupo TEXT NOT NULL, " +
                "semestre INTEGER NOT NULL CHECK(semestre IN (1, 2)), " +
                "gestion INTEGER NOT NULL, " +
                "capacidad INTEGER NOT NULL, " +
                "nro_inscritos INTEGER DEFAULT 0, " +
                "tolerancia_minutos INTEGER DEFAULT 10 NOT NULL CHECK(tolerancia_minutos >= 0 AND tolerancia_minutos <= 60), " +
                "tipo_estrategia TEXT DEFAULT 'RETRASO' NOT NULL CHECK(tipo_estrategia IN ('PRESENTE', 'RETRASO', 'FALTA')), " +
                "FOREIGN KEY(materia_id) REFERENCES materias(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(docente_id) REFERENCES usuarios(id) ON DELETE CASCADE" +
                ")";
        
        db.execSQL(sql);
        Log.d(TAG, "Tabla grupos creada");
    }

    /**
     * Crea la tabla de horarios.
     */
    private static void createHorariosTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS horarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "grupo_id INTEGER NOT NULL, " +
                "dia TEXT NOT NULL, " +
                "hora_inicio TEXT NOT NULL, " +
                "hora_fin TEXT NOT NULL, " +
                "FOREIGN KEY(grupo_id) REFERENCES grupos(id) ON DELETE CASCADE" +
                ")";
        
        db.execSQL(sql);
        Log.d(TAG, "Tabla horarios creada");
    }

    /**
     * Crea la tabla de boletas (inscripciones).
     */
    private static void createBoletasTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS boletas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "alumno_id INTEGER NOT NULL, " +
                "grupo_id INTEGER NOT NULL, " +
                "fecha TEXT NOT NULL, " +
                "semestre INTEGER NOT NULL, " +
                "gestion INTEGER NOT NULL, " +
                "FOREIGN KEY(alumno_id) REFERENCES usuarios(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(grupo_id) REFERENCES grupos(id) ON DELETE CASCADE" +
                ")";
        
        db.execSQL(sql);
        Log.d(TAG, "Tabla boletas creada");
    }

    /**
     * Crea la tabla de asistencias.
     * 
     * Campos agregados:
     * - hora_marcada: Hora en que el alumno marcó asistencia (formato HH:mm)
     * - estado: Estado de la asistencia (PRESENTE, RETRASO, FALTA)
     */
    private static void createAsistenciasTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS asistencias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "alumno_id INTEGER NOT NULL, " +
                "grupo_id INTEGER NOT NULL, " +
                "fecha TEXT NOT NULL, " +
                "hora_marcada TEXT, " +
                "estado TEXT CHECK(estado IN ('PRESENTE', 'RETRASO', 'FALTA')), " +
                "FOREIGN KEY(alumno_id) REFERENCES usuarios(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(grupo_id) REFERENCES grupos(id) ON DELETE CASCADE, " +
                "UNIQUE(alumno_id, grupo_id, fecha)" +
                ")";
        
        db.execSQL(sql);
        Log.d(TAG, "Tabla asistencias creada");
    }

    /**
     * Elimina todas las tablas (usado en migraciones).
     * CUIDADO: Esto elimina todos los datos.
     */
    private static void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS asistencias");
        db.execSQL("DROP TABLE IF EXISTS boletas");
        db.execSQL("DROP TABLE IF EXISTS horarios");
        db.execSQL("DROP TABLE IF EXISTS grupos");
        db.execSQL("DROP TABLE IF EXISTS materias");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        Log.d(TAG, "Todas las tablas eliminadas");
    }
}

