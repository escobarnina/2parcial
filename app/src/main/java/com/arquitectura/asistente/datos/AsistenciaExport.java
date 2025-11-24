package com.arquitectura.asistente.datos;

/**
 * DTO (Data Transfer Object) para exportación de asistencias
 * Capa de Datos - Adapter Pattern - DTO para exportación
 * 
 * Contiene toda la información necesaria para exportar asistencias,
 * incluyendo datos relacionados de estudiantes, materias, grupos y docentes.
 * 
 * Este DTO es utilizado por los adaptadores de exportación (Excel, PDF)
 * para obtener información completa sin modificar la entidad Asistencia.
 */
public class AsistenciaExport {
    // Campos básicos de asistencia
    private Integer id;
    private Integer alumnoId;
    private Integer grupoId;
    private String fecha; // YYYY-MM-DD
    private String horaMarcada; // HH:mm
    private String estado; // "PRESENTE", "RETRASO", "FALTA"
    
    // Información del estudiante
    private String alumnoNombre; // Nombre completo del estudiante
    private String alumnoRegistro; // Registro del estudiante
    
    // Información de la materia
    private String materiaNombre; // Nombre de la materia
    private String materiaSigla; // Sigla de la materia
    
    // Información del grupo
    private String grupoParalelo; // Grupo/Paralelo (A, B, etc.)
    
    // Información del docente
    private String docenteNombre; // Nombre completo del docente

    // Constructores
    public AsistenciaExport() {
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

    public String getAlumnoNombre() {
        return alumnoNombre;
    }

    public void setAlumnoNombre(String alumnoNombre) {
        this.alumnoNombre = alumnoNombre;
    }

    public String getAlumnoRegistro() {
        return alumnoRegistro;
    }

    public void setAlumnoRegistro(String alumnoRegistro) {
        this.alumnoRegistro = alumnoRegistro;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }

    public String getMateriaSigla() {
        return materiaSigla;
    }

    public void setMateriaSigla(String materiaSigla) {
        this.materiaSigla = materiaSigla;
    }

    public String getGrupoParalelo() {
        return grupoParalelo;
    }

    public void setGrupoParalelo(String grupoParalelo) {
        this.grupoParalelo = grupoParalelo;
    }

    public String getDocenteNombre() {
        return docenteNombre;
    }

    public void setDocenteNombre(String docenteNombre) {
        this.docenteNombre = docenteNombre;
    }
}

