package com.arquitectura.asistente.datos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arquitectura.asistente.datos.database.DatabaseBaseDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de datos para Horario
 * Capa de Datos - Representa la entidad Horario
 * 
 * Contiene métodos de instancia para acceso a datos que muestran
 * cómo desde la clase de datos se conecta directamente con la base de datos
 * 
 * RELACIONES:
 * - Horario -> DatabaseBaseDAO (instancia estática compartida)
 * - DatabaseBaseDAO -> DatabaseHelper (instancia estática compartida)
 */
public class Horario {
    private static final String TAG = "Horario";
    private static final String TABLE_NAME = "horarios";
    
    // Relación explícita con la capa de acceso a datos sin usar métodos estáticos
    private DatabaseBaseDAO baseDAO;
    private Context appContext;
    private Integer id;
    private Integer grupoId;
    private String dia; // "Lunes", "Martes", etc.
    private String horaInicio; // HH:mm
    private String horaFin; // HH:mm

    public Horario() {
    }

    public Horario(Integer grupoId, String dia, String horaInicio, String horaFin) {
        this.grupoId = grupoId;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Horario(Integer id, Integer grupoId, String dia, String horaInicio, String horaFin) {
        this.id = id;
        this.grupoId = grupoId;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Integer grupoId) {
        this.grupoId = grupoId;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    @Override
    public String toString() {
        return "Horario{" +
                "id=" + id +
                ", grupoId=" + grupoId +
                ", dia='" + dia + '\'' +
                ", horaInicio='" + horaInicio + '\'' +
                ", horaFin='" + horaFin + '\'' +
                '}';
    }

    /**
     * Constructor que habilita el acceso a datos para esta instancia
     */
    public Horario(Context context) {
        this();
        configurarAccesoDatos(context);
    }

    /**
     * Permite configurar o reconfigurar el acceso a datos
     */
    public void configurarAccesoDatos(Context context) {
        this.appContext = context != null ? context.getApplicationContext() : null;
        if (this.appContext == null) {
            throw new IllegalArgumentException("Se requiere un Context válido para configurar el acceso a datos de Horario");
        }
        this.baseDAO = DatabaseBaseDAO.getInstance(this.appContext);
    }

    /**
     * Obtiene los horarios de un grupo (consulta personalizada)
     */
    public List<Horario> obtenerHorariosGrupo(Integer grupoId) {
        verificarInicializacion();
        
        List<Horario> horarios = new ArrayList<>();
        String sql = "SELECT * FROM horarios WHERE grupo_id = ?";
        SQLiteDatabase db = baseDAO.getReadableDatabase();
        
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(grupoId)})) {
            while (cursor.moveToNext()) {
                Horario horario = new Horario();
                horario.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                horario.setGrupoId(cursor.getInt(cursor.getColumnIndexOrThrow("grupo_id")));
                horario.setDia(cursor.getString(cursor.getColumnIndexOrThrow("dia")));
                horario.setHoraInicio(cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio")));
                horario.setHoraFin(cursor.getString(cursor.getColumnIndexOrThrow("hora_fin")));
                horarios.add(horario);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener horarios del grupo: " + grupoId, e);
        } finally {
            db.close();
        }
        
        return horarios;
    }

    /**
     * Verifica que la clase haya sido inicializada antes de usar los métodos de acceso a datos
     */
    private void verificarInicializacion() {
        if (baseDAO == null) {
            throw new IllegalStateException("Horario requiere configurarAccesoDatos(Context) antes de usarse");
        }
    }
}

