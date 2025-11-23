package Arquinuevo.datos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repositorio para gestionar el acceso a datos de Asistencia
 * Capa de Datos - Utiliza DatabaseHelper para operaciones de base de datos
 */
public class AsistenciaRepository {
    private static final Logger logger = Logger.getLogger(AsistenciaRepository.class.getName());
    private static AsistenciaRepository instance;
    private DatabaseHelper dbHelper;

    private AsistenciaRepository() {
        this.dbHelper = DatabaseHelper.getInstance();
    }

    public static AsistenciaRepository getInstance() {
        if (instance == null) {
            instance = new AsistenciaRepository();
        }
        return instance;
    }

    /**
     * Guarda una nueva asistencia
     */
    public Asistencia guardar(Asistencia asistencia) {
        String sql = "INSERT INTO asistencias (alumno_id, grupo_id, fecha, hora_marcada, estado) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, asistencia.getAlumnoId());
            pstmt.setInt(2, asistencia.getGrupoId());
            pstmt.setString(3, asistencia.getFecha());
            pstmt.setString(4, asistencia.getHoraMarcada());
            pstmt.setString(5, asistencia.getEstado());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al guardar asistencia, ninguna fila afectada");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    asistencia.setId(generatedKeys.getInt(1));
                }
            }
            
            logger.info("Asistencia guardada con ID: " + asistencia.getId());
            return asistencia;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al guardar asistencia", e);
            throw new RuntimeException("Error al guardar asistencia", e);
        }
    }

    /**
     * Obtiene todas las asistencias de un grupo
     */
    public List<Asistencia> obtenerPorGrupo(Integer grupoId) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE grupo_id = ? ORDER BY fecha DESC";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, grupoId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    asistencias.add(mapResultSetToAsistencia(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener asistencias por grupo: " + grupoId, e);
        }
        
        return asistencias;
    }

    /**
     * Verifica si un alumno está inscrito en un grupo (tabla boletas)
     */
    public boolean estaInscrito(Integer alumnoId, Integer grupoId) {
        String sql = "SELECT COUNT(*) as count FROM boletas WHERE alumno_id = ? AND grupo_id = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, alumnoId);
            pstmt.setInt(2, grupoId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar inscripcion", e);
        }
        
        return false;
    }

    /**
     * Obtiene todas las asistencias de un alumno
     */
    public List<Asistencia> obtenerPorAlumno(Integer alumnoId) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE alumno_id = ? ORDER BY fecha DESC";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, alumnoId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    asistencias.add(mapResultSetToAsistencia(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener asistencias por alumno: " + alumnoId, e);
        }
        
        return asistencias;
    }

    /**
     * Obtiene la tolerancia de un grupo (para Strategy Pattern)
     */
    public Integer obtenerToleranciaGrupo(Integer grupoId) {
        String sql = "SELECT tolerancia_minutos FROM grupos WHERE id = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, grupoId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("tolerancia_minutos");
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener tolerancia del grupo: " + grupoId, e);
        }
        
        return 10; // Valor por defecto
    }

    /**
     * Obtiene el tipo de estrategia de un grupo (para Strategy Pattern)
     */
    public String obtenerTipoEstrategiaGrupo(Integer grupoId) {
        String sql = "SELECT tipo_estrategia FROM grupos WHERE id = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, grupoId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tipo_estrategia");
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener tipo estrategia del grupo: " + grupoId, e);
        }
        
        return "RETRASO"; // Valor por defecto
    }

    /**
     * Obtiene los horarios de un grupo
     */
    public List<Horario> obtenerHorariosGrupo(Integer grupoId) {
        List<Horario> horarios = new ArrayList<>();
        String sql = "SELECT * FROM horarios WHERE grupo_id = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, grupoId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Horario horario = new Horario();
                    horario.setId(rs.getInt("id"));
                    horario.setGrupoId(rs.getInt("grupo_id"));
                    horario.setDia(rs.getString("dia"));
                    horario.setHoraInicio(rs.getString("hora_inicio"));
                    horario.setHoraFin(rs.getString("hora_fin"));
                    horarios.add(horario);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener horarios del grupo: " + grupoId, e);
        }
        
        return horarios;
    }

    private Asistencia mapResultSetToAsistencia(ResultSet rs) throws SQLException {
        Asistencia asistencia = new Asistencia();
        asistencia.setId(rs.getInt("id"));
        asistencia.setAlumnoId(rs.getInt("alumno_id"));
        asistencia.setGrupoId(rs.getInt("grupo_id"));
        // MySQL devuelve DATE como java.sql.Date, convertir a String formato yyyy-MM-dd
        java.sql.Date fecha = rs.getDate("fecha");
        asistencia.setFecha(fecha != null ? fecha.toString() : null);
        asistencia.setHoraMarcada(rs.getString("hora_marcada"));
        asistencia.setEstado(rs.getString("estado"));
        return asistencia;
    }
}

