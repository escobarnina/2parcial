package com.arquitectura.asistente.datos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DAO Genérico Base para operaciones CRUD básicas
 * Capa de Datos - Database - Proporciona métodos genéricos para acceso a datos
 * Los repositorios de la capa datos usan estos métodos genéricos
 */
public class DatabaseBaseDAO {
    private static final String TAG = "DatabaseBaseDAO";
    private static DatabaseBaseDAO instance;
    private DatabaseHelper dbHelper;
    private Context context;

    private DatabaseBaseDAO(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = DatabaseHelper.getInstance(this.context);
    }

    public static synchronized DatabaseBaseDAO getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseBaseDAO(context);
        }
        return instance;
    }

    /**
     * Obtiene instancia sin contexto (solo si ya fue inicializada)
     */
    public static DatabaseBaseDAO getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseBaseDAO debe ser inicializado primero con getInstance(Context)");
        }
        return instance;
    }

    /**
     * Inserta un registro en la tabla especificada
     */
    public long insert(String tableName, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabaseInstance();
        try {
            long id = db.insert(tableName, null, values);
            if (id == -1) {
                Log.e(TAG, "Error al insertar en tabla: " + tableName);
            } else {
                Log.d(TAG, "Registro insertado en " + tableName + " con ID: " + id);
            }
            return id;
        } catch (Exception e) {
            Log.e(TAG, "Error al insertar en " + tableName, e);
            throw new RuntimeException("Error al insertar en " + tableName, e);
        } finally {
            db.close();
        }
    }

    /**
     * Actualiza un registro por ID en la tabla especificada
     */
    public int update(String tableName, long id, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabaseInstance();
        try {
            int rowsAffected = db.update(tableName, values, "id = ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "Registros actualizados en " + tableName + ": " + rowsAffected);
            return rowsAffected;
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar en " + tableName, e);
            throw new RuntimeException("Error al actualizar en " + tableName, e);
        } finally {
            db.close();
        }
    }

    /**
     * Elimina un registro por ID en la tabla especificada
     */
    public int delete(String tableName, long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabaseInstance();
        try {
            int rowsAffected = db.delete(tableName, "id = ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "Registros eliminados en " + tableName + ": " + rowsAffected);
            return rowsAffected;
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar en " + tableName, e);
            throw new RuntimeException("Error al eliminar en " + tableName, e);
        } finally {
            db.close();
        }
    }

    /**
     * Obtiene un registro por ID de la tabla especificada
     */
    public Cursor findById(String tableName, long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabaseInstance();
        return db.query(tableName, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
    }

    /**
     * Obtiene todos los registros de la tabla especificada
     */
    public Cursor findAll(String tableName) {
        SQLiteDatabase db = dbHelper.getReadableDatabaseInstance();
        return db.query(tableName, null, null, null, null, null, null);
    }

    /**
     * Ejecuta una consulta SQL personalizada de lectura
     */
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabaseInstance();
        return db.rawQuery(sql, selectionArgs);
    }

    /**
     * Ejecuta una consulta SQL de escritura (INSERT, UPDATE, DELETE)
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = dbHelper.getWritableDatabaseInstance();
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            Log.e(TAG, "Error al ejecutar SQL", e);
            throw new RuntimeException("Error al ejecutar SQL", e);
        } finally {
            db.close();
        }
    }

    /**
     * Obtiene la base de datos para operaciones personalizadas de lectura
     */
    public SQLiteDatabase getReadableDatabase() {
        return dbHelper.getReadableDatabaseInstance();
    }

    /**
     * Obtiene la base de datos para operaciones de escritura
     */
    public SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabaseInstance();
    }
}

