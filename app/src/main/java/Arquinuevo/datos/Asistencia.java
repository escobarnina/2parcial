package Arquinuevo.datos;

/**
 * Modelo de datos para Asistencia
 * Capa de Datos - Representa la entidad Asistencia
 * El estado se calcula usando Strategy Pattern
 */
public class Asistencia {
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
}

