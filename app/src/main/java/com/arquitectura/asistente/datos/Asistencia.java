package com.arquitectura.asistente.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arquitectura.asistente.datos.adapter.AsistenciaExportDTO;
import com.arquitectura.asistente.datos.database.DatabaseBaseDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de datos para Asistencia
 * Capa de Datos - Representa la entidad Asistencia
 * El estado se calcula usando Strategy Pattern
 * 
 * Contiene métodos de instancia para acceso a datos que muestran
 * cómo desde la clase de datos se conecta directamente con la base de datos
 * 
 * RELACIONES:
 * - Asistencia -> DatabaseBaseDAO (instancia estática compartida)
 * - DatabaseBaseDAO -> DatabaseHelper (instancia estática compartida)
 */
public class Asistencia {
    private static final String TAG = "Asistencia";
    private static final String TABLE_NAME = "asistencias";
    
    // Relación explícita con la capa de acceso a datos sin usar métodos estáticos
    private DatabaseBaseDAO baseDAO;
    private Context appContext;

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
     * Constructor que habilita el acceso a datos para esta instancia
     * Permite que los métodos de acceso a BD sean de instancia (no estáticos)
     */
    public Asistencia(Context context) {
        this();
        configurarAccesoDatos(context);
    }

    /**
     * Permite habilitar el acceso a datos en cualquier momento sin recurrir a métodos estáticos.
     * Si ya fue configurado, simplemente reutiliza la instancia existente.
     */
    public void configurarAccesoDatos(Context context) {
        this.appContext = context != null ? context.getApplicationContext() : null;
        if (this.appContext == null) {
            throw new IllegalArgumentException("Se requiere un Context válido para configurar el acceso a datos de Asistencia");
        }
        this.baseDAO = DatabaseBaseDAO.getInstance(this.appContext);
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
    public Asistencia guardar(Asistencia asistencia) {
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
    public List<Asistencia> obtenerPorGrupo(Integer grupoId) {
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
    public boolean estaInscrito(Integer alumnoId, Integer grupoId) {
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
    public List<Asistencia> obtenerPorAlumno(Integer alumnoId) {
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
    public Asistencia obtenerAsistenciaExistente(Integer alumnoId, Integer grupoId, String fecha) {
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
     * Obtiene todas las asistencias de un grupo con información completa para exportación
     * Hace JOINs con las tablas relacionadas para obtener nombres, materias, grupos, etc.
     * Retorna un DTO específico para exportación sin modificar la entidad Asistencia
     * 
     * IMPORTANTE: Este método CREA las instancias de AsistenciaExportDTO.
     * Cada registro del Cursor se mapea a un nuevo DTO usando mapCursorToAsistenciaExportDTO().
     * Los adapters (Excel, PDF) reciben estas instancias ya creadas y solo las utilizan.
     * 
     * @param grupoId ID del grupo
     * @return Lista de AsistenciaExportDTO con información completa (instancias creadas aquí)
     */
    public List<AsistenciaExportDTO> obtenerPorGrupoParaExportacion(Integer grupoId) {
        verificarInicializacion();
        
        List<AsistenciaExportDTO> asistenciasDTO = new ArrayList<>();
        SQLiteDatabase db = baseDAO.getReadableDatabase();
        
        // Consulta con JOINs para obtener información completa
        String sql = "SELECT " +
                    "a.id, " +
                    "a.alumno_id, " +
                    "a.grupo_id, " +
                    "a.fecha, " +
                    "a.hora_marcada, " +
                    "a.estado, " +
                    "u.nombres || ' ' || u.apellidos as alumno_nombre, " +
                    "u.registro as alumno_registro, " +
                    "m.nombre as materia_nombre, " +
                    "m.sigla as materia_sigla, " +
                    "g.grupo as grupo_paralelo, " +
                    "ud.nombres || ' ' || ud.apellidos as docente_nombre " +
                    "FROM asistencias a " +
                    "INNER JOIN usuarios u ON a.alumno_id = u.id " +
                    "INNER JOIN grupos g ON a.grupo_id = g.id " +
                    "INNER JOIN materias m ON g.materia_id = m.id " +
                    "INNER JOIN usuarios ud ON g.docente_id = ud.id " +
                    "WHERE a.grupo_id = ? " +
                    "ORDER BY u.apellidos, u.nombres, a.fecha DESC";
        
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(grupoId)})) {
            while (cursor.moveToNext()) {
                asistenciasDTO.add(mapCursorToAsistenciaExportDTO(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener asistencias para exportación - Grupo: " + grupoId, e);
        } finally {
            db.close();
        }
        
        return asistenciasDTO;
    }

    /**
     * Mapea un Cursor a un objeto AsistenciaExportDTO con información completa
     * 
     * IMPORTANTE: Este método CREA una nueva instancia de AsistenciaExportDTO
     * y la llena con los datos del Cursor (que proviene de JOINs SQL).
     * Esta es la única ubicación donde se crean instancias de AsistenciaExportDTO.
     * 
     * @param cursor Cursor con los datos de la consulta SQL (incluye JOINs)
     * @return Nueva instancia de AsistenciaExportDTO con todos los campos poblados
     */
    private AsistenciaExportDTO mapCursorToAsistenciaExportDTO(Cursor cursor) {
        AsistenciaExportDTO dto = new AsistenciaExportDTO(); // CREACIÓN DE INSTANCIA
        
        // Campos básicos
        dto.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        dto.setAlumnoId(cursor.getInt(cursor.getColumnIndexOrThrow("alumno_id")));
        dto.setGrupoId(cursor.getInt(cursor.getColumnIndexOrThrow("grupo_id")));
        dto.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
        
        int horaIndex = cursor.getColumnIndex("hora_marcada");
        if (horaIndex >= 0 && !cursor.isNull(horaIndex)) {
            dto.setHoraMarcada(cursor.getString(horaIndex));
        }
        
        int estadoIndex = cursor.getColumnIndex("estado");
        if (estadoIndex >= 0 && !cursor.isNull(estadoIndex)) {
            dto.setEstado(cursor.getString(estadoIndex));
        }
        
        // Campos adicionales de JOINs
        int alumnoNombreIndex = cursor.getColumnIndex("alumno_nombre");
        if (alumnoNombreIndex >= 0 && !cursor.isNull(alumnoNombreIndex)) {
            dto.setAlumnoNombre(cursor.getString(alumnoNombreIndex));
        }
        
        int alumnoRegistroIndex = cursor.getColumnIndex("alumno_registro");
        if (alumnoRegistroIndex >= 0 && !cursor.isNull(alumnoRegistroIndex)) {
            dto.setAlumnoRegistro(cursor.getString(alumnoRegistroIndex));
        }
        
        int materiaNombreIndex = cursor.getColumnIndex("materia_nombre");
        if (materiaNombreIndex >= 0 && !cursor.isNull(materiaNombreIndex)) {
            dto.setMateriaNombre(cursor.getString(materiaNombreIndex));
        }
        
        int materiaSiglaIndex = cursor.getColumnIndex("materia_sigla");
        if (materiaSiglaIndex >= 0 && !cursor.isNull(materiaSiglaIndex)) {
            dto.setMateriaSigla(cursor.getString(materiaSiglaIndex));
        }
        
        int grupoParaleloIndex = cursor.getColumnIndex("grupo_paralelo");
        if (grupoParaleloIndex >= 0 && !cursor.isNull(grupoParaleloIndex)) {
            dto.setGrupoParalelo(cursor.getString(grupoParaleloIndex));
        }
        
        int docenteNombreIndex = cursor.getColumnIndex("docente_nombre");
        if (docenteNombreIndex >= 0 && !cursor.isNull(docenteNombreIndex)) {
            dto.setDocenteNombre(cursor.getString(docenteNombreIndex));
        }
        
        return dto;
    }

    /**
     * Mapea un Cursor a un objeto Asistencia
     */
    private Asistencia mapCursorToAsistencia(Cursor cursor) {
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
    private void verificarInicializacion() {
        if (baseDAO == null) {
            throw new IllegalStateException("Asistencia requiere que se configure el acceso a datos mediante configurarAccesoDatos(Context)");
        }
    }
}
