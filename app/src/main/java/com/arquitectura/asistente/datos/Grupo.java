package com.arquitectura.asistente.datos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arquitectura.asistente.datos.database.DatabaseBaseDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de datos para Grupo
 * Capa de Datos - Representa la entidad Grupo
 * Incluye campos para Strategy Pattern (tolerancia_minutos, tipo_estrategia)
 * 
 * Contiene métodos estáticos para acceso a datos que muestran
 * cómo desde la clase de datos se conecta directamente con la base de datos
 */
public class Grupo {
    private static final String TAG = "Grupo";
    private static final String TABLE_NAME = "grupos";
    private static DatabaseBaseDAO baseDAO;
    private static Context context;
    private Integer id;
    private String grupo; // Paralelo (A, B, etc.)
    private Integer materiaId;
    private String materiaNombre;
    private Integer docenteId;
    private String docenteNombre;
    private Integer semestre; // 1 o 2
    private Integer gestion; // Año
    private Integer capacidad;
    private Integer nroInscritos;
    private Integer toleranciaMinutos; // Para Strategy Pattern
    private String tipoEstrategia; // Para Strategy Pattern: "PRESENTE", "RETRASO", "FALTA"

    public Grupo() {
        this.toleranciaMinutos = 10;
        this.tipoEstrategia = "RETRASO";
    }

    public Grupo(String grupo, Integer materiaId, String materiaNombre, Integer docenteId, 
                 String docenteNombre, Integer semestre, Integer gestion, Integer capacidad) {
        this.grupo = grupo;
        this.materiaId = materiaId;
        this.materiaNombre = materiaNombre;
        this.docenteId = docenteId;
        this.docenteNombre = docenteNombre;
        this.semestre = semestre;
        this.gestion = gestion;
        this.capacidad = capacidad;
        this.nroInscritos = 0;
        this.toleranciaMinutos = 10;
        this.tipoEstrategia = "RETRASO";
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public Integer getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(Integer materiaId) {
        this.materiaId = materiaId;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }

    public Integer getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Integer docenteId) {
        this.docenteId = docenteId;
    }

    public String getDocenteNombre() {
        return docenteNombre;
    }

    public void setDocenteNombre(String docenteNombre) {
        this.docenteNombre = docenteNombre;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    public Integer getGestion() {
        return gestion;
    }

    public void setGestion(Integer gestion) {
        this.gestion = gestion;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Integer getNroInscritos() {
        return nroInscritos;
    }

    public void setNroInscritos(Integer nroInscritos) {
        this.nroInscritos = nroInscritos;
    }

    public Integer getToleranciaMinutos() {
        return toleranciaMinutos;
    }

    public void setToleranciaMinutos(Integer toleranciaMinutos) {
        this.toleranciaMinutos = toleranciaMinutos;
    }

    public String getTipoEstrategia() {
        return tipoEstrategia;
    }

    public void setTipoEstrategia(String tipoEstrategia) {
        this.tipoEstrategia = tipoEstrategia;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "id=" + id +
                ", grupo='" + grupo + '\'' +
                ", materiaNombre='" + materiaNombre + '\'' +
                ", docenteNombre='" + docenteNombre + '\'' +
                ", semestre=" + semestre +
                ", gestion=" + gestion +
                '}';
    }

    /**
     * Inicializa el acceso a la base de datos
     * Debe ser llamado antes de usar los métodos de acceso a datos
     */
    public static void inicializar(Context ctx) {
        context = ctx.getApplicationContext();
        baseDAO = DatabaseBaseDAO.getInstance(context);
    }

    /**
     * Obtiene la tolerancia de un grupo (consulta personalizada para Strategy Pattern)
     */
    public static Integer obtenerToleranciaGrupo(Integer grupoId) {
        verificarInicializacion();
        
        String sql = "SELECT tolerancia_minutos FROM grupos WHERE id = ?";
        SQLiteDatabase db = baseDAO.getReadableDatabase();
        
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(grupoId)})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener tolerancia del grupo: " + grupoId, e);
        } finally {
            db.close();
        }
        
        return 10; // Valor por defecto
    }

    /**
     * Obtiene el tipo de estrategia de un grupo (consulta personalizada para Strategy Pattern)
     */
    public static String obtenerTipoEstrategiaGrupo(Integer grupoId) {
        verificarInicializacion();
        
        String sql = "SELECT tipo_estrategia FROM grupos WHERE id = ?";
        SQLiteDatabase db = baseDAO.getReadableDatabase();
        
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(grupoId)})) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener tipo estrategia del grupo: " + grupoId, e);
        } finally {
            db.close();
        }
        
        return "RETRASO"; // Valor por defecto
    }

    /**
     * Obtiene los grupos en los que está inscrito un estudiante (consulta personalizada)
     * Sigue el mismo patrón que Asistencia.obtenerPorAlumno
     */
    public static List<Grupo> obtenerPorEstudiante(Integer estudianteId) {
        verificarInicializacion();
        
        List<Grupo> grupos = new ArrayList<>();
        String sql = "SELECT g.id, g.grupo, g.materia_id, m.nombre as materia_nombre, " +
                     "g.docente_id, u.nombres || ' ' || u.apellidos as docente_nombre, " +
                     "g.semestre, g.gestion, g.capacidad, g.tolerancia_minutos, g.tipo_estrategia " +
                     "FROM grupos g " +
                     "INNER JOIN boletas b ON g.id = b.grupo_id " +
                     "INNER JOIN materias m ON g.materia_id = m.id " +
                     "INNER JOIN usuarios u ON g.docente_id = u.id " +
                     "WHERE b.alumno_id = ? " +
                     "ORDER BY m.nombre, g.grupo";
        
        SQLiteDatabase db = baseDAO.getReadableDatabase();
        
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(estudianteId)})) {
            while (cursor.moveToNext()) {
                grupos.add(mapCursorToGrupo(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener grupos por estudiante: " + estudianteId, e);
        } finally {
            db.close();
        }
        
        return grupos;
    }

    /**
     * Mapea un Cursor a un objeto Grupo
     */
    private static Grupo mapCursorToGrupo(Cursor cursor) {
        Grupo grupo = new Grupo();
        grupo.setId(cursor.getInt(0));
        grupo.setGrupo(cursor.getString(1));
        grupo.setMateriaId(cursor.getInt(2));
        grupo.setMateriaNombre(cursor.getString(3));
        grupo.setDocenteId(cursor.getInt(4));
        grupo.setDocenteNombre(cursor.getString(5));
        grupo.setSemestre(cursor.getInt(6));
        grupo.setGestion(cursor.getInt(7));
        grupo.setCapacidad(cursor.getInt(8));
        grupo.setToleranciaMinutos(cursor.getInt(9));
        grupo.setTipoEstrategia(cursor.getString(10));
        return grupo;
    }

    /**
     * Verifica que la clase haya sido inicializada antes de usar los métodos de acceso a datos
     */
    private static void verificarInicializacion() {
        if (baseDAO == null || context == null) {
            throw new IllegalStateException("Grupo debe ser inicializada primero con Grupo.inicializar(Context)");
        }
    }
}

