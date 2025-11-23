package Arquinuevo.datos;

/**
 * Modelo de datos para Horario
 * Capa de Datos - Representa la entidad Horario
 */
public class Horario {
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
}

