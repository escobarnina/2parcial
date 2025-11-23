package Arquinuevo.datos;

/**
 * Modelo de datos para Materia
 * Capa de Datos - Representa la entidad Materia
 */
public class Materia {
    private Integer id;
    private String nombre;
    private String sigla;
    private Integer nivel;

    public Materia() {
    }

    public Materia(String nombre, String sigla, Integer nivel) {
        this.nombre = nombre;
        this.sigla = sigla;
        this.nivel = nivel;
    }

    public Materia(Integer id, String nombre, String sigla, Integer nivel) {
        this.id = id;
        this.nombre = nombre;
        this.sigla = sigla;
        this.nivel = nivel;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    @Override
    public String toString() {
        return "Materia{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", sigla='" + sigla + '\'' +
                ", nivel=" + nivel +
                '}';
    }
}

