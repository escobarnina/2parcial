package Arquinuevo.datos;

/**
 * Modelo de datos para Grupo
 * Capa de Datos - Representa la entidad Grupo
 * Incluye campos para Strategy Pattern (tolerancia_minutos, tipo_estrategia)
 */
public class Grupo {
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
}

