package Arquinuevo.datos;

/**
 * Modelo de datos para Usuario
 * Capa de Datos - Representa la entidad Usuario
 * Solo Estudiante y Docente (sin Admin)
 */
public class Usuario {
    private Integer id;
    private String nombres;
    private String apellidos;
    private String registro;
    private String rol; // "Estudiante" o "Docente"
    private String username;
    private String password;

    public Usuario() {
    }

    public Usuario(String nombres, String apellidos, String registro, String rol, String username, String password) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.registro = registro;
        this.rol = rol;
        this.username = username;
        this.password = password;
    }

    public Usuario(Integer id, String nombres, String apellidos, String registro, String rol, String username, String password) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.registro = registro;
        this.rol = rol;
        this.username = username;
        this.password = password;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", registro='" + registro + '\'' +
                ", rol='" + rol + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

