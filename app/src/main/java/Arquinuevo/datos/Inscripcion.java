package Arquinuevo.datos;

/**
 * Modelo de datos para Inscripcion
 * Capa de Datos - Representa la entidad Inscripcion (Boleta)
 */
public class Inscripcion {
    private Integer id;
    private Integer alumnoId;
    private Integer grupoId;
    private String fecha; // YYYY-MM-DD
    private Integer semestre; // 1 o 2
    private Integer gestion; // Año

    public Inscripcion() {
    }

    public Inscripcion(Integer alumnoId, Integer grupoId, String fecha, Integer semestre, Integer gestion) {
        this.alumnoId = alumnoId;
        this.grupoId = grupoId;
        this.fecha = fecha;
        this.semestre = semestre;
        this.gestion = gestion;
    }

    public Inscripcion(Integer id, Integer alumnoId, Integer grupoId, String fecha, Integer semestre, Integer gestion) {
        this.id = id;
        this.alumnoId = alumnoId;
        this.grupoId = grupoId;
        this.fecha = fecha;
        this.semestre = semestre;
        this.gestion = gestion;
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

    @Override
    public String toString() {
        return "Inscripcion{" +
                "id=" + id +
                ", alumnoId=" + alumnoId +
                ", grupoId=" + grupoId +
                ", fecha='" + fecha + '\'' +
                ", semestre=" + semestre +
                ", gestion=" + gestion +
                '}';
    }
}

