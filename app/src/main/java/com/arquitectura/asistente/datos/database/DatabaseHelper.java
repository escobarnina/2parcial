package com.arquitectura.asistente.datos.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.arquitectura.asistente.datos.database.DatabaseMigrations;
import com.arquitectura.asistente.datos.database.DatabaseSeeder;

/**
 * Helper para la gestión de la base de datos SQLite en Android
 * Capa de Datos - Gestión de Base de Datos
 * Adaptado de MySQL a SQLite para Android
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "asistencia_db.db";
    
    private static DatabaseHelper instance;
    private static Context appContext;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            appContext = context.getApplicationContext();
            instance = new DatabaseHelper(appContext);
        }
        return instance;
    }

    /**
     * Método estático para obtener instancia sin contexto (solo si ya fue inicializada)
     */
    public static DatabaseHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseHelper debe ser inicializado primero con getInstance(Context)");
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creando base de datos...");
        DatabaseMigrations.createTables(db);
        DatabaseSeeder.seed(db);
        Log.d(TAG, "Base de datos SQLite inicializada correctamente");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Actualizando base de datos de versión " + oldVersion + " a " + newVersion);
        DatabaseMigrations.migrate(db, oldVersion, newVersion);
    }

    /**
     * Obtiene una base de datos escribible
     */
    public SQLiteDatabase getWritableDatabaseInstance() {
        return getWritableDatabase();
    }

    /**
     * Obtiene una base de datos de solo lectura
     */
    public SQLiteDatabase getReadableDatabaseInstance() {
        return getReadableDatabase();
    }
}

