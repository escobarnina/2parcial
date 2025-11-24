package com.arquitectura.asistente.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arquitectura.asistente.datos.database.DatabaseBaseDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de datos para Asistencia
 * Capa de Datos - Representa la entidad Asistencia
 * El estado se calcula usando Strategy Pattern
 * 
 * Contiene métodos estáticos para acceso a datos que muestran
 * cómo desde la clase de datos se conecta directamente con la base de datos
 */
public class Asistencia {
    private static final String TAG = "Asistencia";
    private static final String TABLE_NAME = "asistencias";
    private static DatabaseBaseDAO baseDAO;
    private static Context context;

    private Integer id;
    private Integer alumnoId;
    private Integer grupoId;
    private String fecha; // YYYY-MM-DD
    private String horaMarcada; // HH:mm
    private String estado; // "PRESENTE", "RETRASO", "FALTA" (calculado por Strategy)

    public Asistencia() {
    }

    public Asistencia(Integer alumnoId, Integer grupoId, String fecha, String horaMarcada, String estado) {
        this.alumnoId = alumnoId;
        this.grupoId = grupoId;
        this.fecha = fecha;
        this.horaMarcada = horaMarcada;
        this.estado = estado;
    }

    public Asistencia(Integer id, Integer alumnoId, Integer grupoId, String fecha, String horaMarcada, String estado) {
        this.id = id;
        this.alumnoId = alumnoId;
        this.grupoId = grupoId;
        this.fecha = fecha;
        this.horaMarcada = horaMarcada;
        this.estado = estado;
    }

    /**
     * Inicializa el acceso a la base de datos
     * Debe ser llamado antes de usar los métodos de acceso a datos
     */
    public static void inicializar(Context ctx) {
        context = ctx.getApplicationContext();
        baseDAO = DatabaseBaseDAO.getInstance(context);
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Integer alumnoId) {
        this.alumnoId = alumnoId;
    }

    public Integer getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Integer grupoId) {
        this.grupoId = grupoId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraMarcada() {
        return horaMarcada;
    }

    public void setHoraMarcada(String horaMarcada) {
        this.horaMarcada = horaMarcada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Asistencia{" +
                "id=" + id +
                ", alumnoId=" + alumnoId +
                ", grupoId=" + grupoId +
                ", fecha='" + fecha + '\'' +
                ", horaMarcada='" + horaMarcada + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }

    /**
     * Guarda una nueva asistencia
     * Usa el método genérico insert de DatabaseBaseDAO
     * Muestra cómo desde la clase de datos se conecta directamente con la base de datos
     */
    public static Asistencia guardar(Asistencia asistencia) {
        verificarInicializacion();
        
        ContentValues values = new ContentValues();
        values.put("alumno_id", asistencia.getAlumnoId());
        values.put("grupo_id", asistencia.getGrupoId());
        values.put("fecha", asistencia.getFecha());
        values.put("hora_marcada", asistencia.getHoraMarcada());
        values.put("estado", asistencia.getEstado());
        
        long id = baseDAO.insert(TABLE_NAME, values);
        if (id == -1) {
            throw new RuntimeException("Error al guardar asistencia, ninguna fila afectada");
        }
        
        asistencia.setId((int) id);
        Log.d(TAG, "Asistencia guardada con ID: " + asistencia.getId());
        return asistencia;
    }

    /**
     * Obtiene todas las asistencias de un grupo (consulta personalizada)
     */
    public static List<Asistencia> obtenerPorGrupo(Integer grupoId) {
        verificarInicializacion();
        
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE grupo_id = ? ORDER BY fecha DESC";
        SQLiteDatabase db = baseDAO.getReadableDatabase();
        
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(grupoId)})) {
            while (cursor.moveToNext()) {
                asistencias.add(mapCursorToAsistencia(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener asistencias por grupo: " + grupoId, e);
        } finally {
            db.close();
        }
        
        return asistencias;
    }

    /**
     * Verifica si un alumno está inscrito en un grupo (consulta personalizada)
     * Valida que exista una inscripción activa en la tabla boletas
     */
    public static boolean estaInscrito(Integer alumnoId, Integer grupoId) {
        verificarInicializacion();
        
        String sql = "SELECT COUNT(*) FROM boletas WHERE alumno_id = ? AND grupo_id = ?";
        SQLiteDatabase db = baseDAO.getReadableDatabase();
        
        try (Cursor cursor = db.rawQuery(sql, new String[]{
                String.valueOf(alumnoId),
                String.valueOf(grupoId)
        })) {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d(TAG, "Verificación de inscripción - Alumno: " + alumnoId + ", Grupo: " + grupoId + ", Count: " + count);
                return count > 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar inscripcion - Alumno: " + alumnoId + ", Grupo: " + grupoId, e);
        } finally {
            db.close();
        }
        
        Log.w(TAG, "No se encontró inscripción - Alumno: " + alumnoId + ", Grupo: " + grupoId);
        return false;
    }

    /**
     * Obtiene todas las asistencias de un alumno (consulta personalizada)
     */
    public static List<Asistencia> obtenerPorAlumno(Integer alumnoId) {
        verificarInicializacion();
        
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE alumno_id = ? ORDER BY fecha DESC";
        SQLiteDatabase db = baseDAO.getReadableDatabase();
        
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(alumnoId)})) {
            while (cursor.moveToNext()) {
                asistencias.add(mapCursorToAsistencia(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener asistencias por alumno: " + alumnoId, e);
        } finally {
            db.close();
        }
        
        return asistencias;
    }

    /**
     * Verifica si ya existe una asistencia marcada para un alumno, grupo y fecha específicos
     * @param alumnoId ID del alumno
     * @param grupoId ID del grupo
     * @param fecha Fecha en formato YYYY-MM-DD
     * @return La asistencia existente o null si no existe
     */
    public static Asistencia obtenerAsistenciaExistente(Integer alumnoId, Integer grupoId, String fecha) {
        verificarInicializacion();
        
        String sql = "SELECT * FROM asistencias WHERE alumno_id = ? AND grupo_id = ? AND fecha = ? LIMIT 1";
        SQLiteDatabase db = baseDAO.getReadableDatabase();
        
        try (Cursor cursor = db.rawQuery(sql, new String[]{
                String.valueOf(alumnoId),
                String.valueOf(grupoId),
                fecha
        })) {
            if (cursor.moveToFirst()) {
                Asistencia asistencia = mapCursorToAsistencia(cursor);
                Log.d(TAG, "Asistencia ya existe - Alumno: " + alumnoId + ", Grupo: " + grupoId + ", Fecha: " + fecha);
                return asistencia;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar asistencia existente - Alumno: " + alumnoId + ", Grupo: " + grupoId + ", Fecha: " + fecha, e);
        } finally {
            db.close();
        }
        
        return null;
    }

    /**
     * Mapea un Cursor a un objeto Asistencia
     */
    private static Asistencia mapCursorToAsistencia(Cursor cursor) {
        Asistencia asistencia = new Asistencia();
        asistencia.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        asistencia.setAlumnoId(cursor.getInt(cursor.getColumnIndexOrThrow("alumno_id")));
        asistencia.setGrupoId(cursor.getInt(cursor.getColumnIndexOrThrow("grupo_id")));
        asistencia.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
        asistencia.setHoraMarcada(cursor.getString(cursor.getColumnIndexOrThrow("hora_marcada")));
        asistencia.setEstado(cursor.getString(cursor.getColumnIndexOrThrow("estado")));
        return asistencia;
    }

    /**
     * Verifica que la clase haya sido inicializada antes de usar los métodos de acceso a datos
     */
    private static void verificarInicializacion() {
        if (baseDAO == null || context == null) {
            throw new IllegalStateException("Asistencia debe ser inicializada primero con Asistencia.inicializar(Context)");
        }
    }
}
