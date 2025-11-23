package Arquinuevo.datos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repositorio para gestionar el acceso a datos de Contacto
 * Capa de Datos - Utiliza DatabaseHelper para operaciones de base de datos
 * Sigue el patrón del diagrama UML proporcionado
 */
public class ContactoRepository {
    private static final Logger logger = Logger.getLogger(ContactoRepository.class.getName());
    private static ContactoRepository instance;
    private DatabaseHelper dbHelper;

    private ContactoRepository() {
        this.dbHelper = DatabaseHelper.getInstance();
    }

    public static ContactoRepository getInstance() {
        if (instance == null) {
            instance = new ContactoRepository();
        }
        return instance;
    }

    /**
     * Guarda un nuevo contacto en la base de datos
     */
    public Contacto guardar(Contacto contacto) {
        String sql = "INSERT INTO contactos (nombre, email, telefono) VALUES (?, ?, ?)";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, contacto.getNombre());
            pstmt.setString(2, contacto.getEmail());
            pstmt.setString(3, contacto.getTelefono());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al guardar contacto, ninguna fila afectada");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    contacto.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Error al obtener el ID generado");
                }
            }
            
            logger.info("Contacto guardado con ID: " + contacto.getId());
            return contacto;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al guardar contacto", e);
            throw new RuntimeException("Error al guardar contacto", e);
        }
    }

    /**
     * Busca un contacto por ID
     */
    public Contacto buscarPorId(Long id) {
        String sql = "SELECT * FROM contactos WHERE id = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToContacto(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar contacto por ID: " + id, e);
        }
        
        return null;
    }

    /**
     * Obtiene todos los contactos
     */
    public List<Contacto> obtenerTodos() {
        List<Contacto> contactos = new ArrayList<>();
        String sql = "SELECT * FROM contactos ORDER BY nombre";
        
        try (Connection conn = dbHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                contactos.add(mapResultSetToContacto(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener todos los contactos", e);
        }
        
        return contactos;
    }

    /**
     * Busca contactos por nombre
     */
    public List<Contacto> buscarPorNombre(String criterio) {
        List<Contacto> contactos = new ArrayList<>();
        String sql = "SELECT * FROM contactos WHERE nombre LIKE ? ORDER BY nombre";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + criterio + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    contactos.add(mapResultSetToContacto(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar contactos por nombre: " + criterio, e);
        }
        
        return contactos;
    }

    /**
     * Elimina un contacto por ID
     */
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM contactos WHERE id = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Contacto eliminado con ID: " + id);
                return true;
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar contacto con ID: " + id, e);
        }
        
        return false;
    }

    /**
     * Actualiza un contacto existente
     */
    public boolean actualizar(Contacto contacto) {
        String sql = "UPDATE contactos SET nombre = ?, email = ?, telefono = ? WHERE id = ?";
        
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, contacto.getNombre());
            pstmt.setString(2, contacto.getEmail());
            pstmt.setString(3, contacto.getTelefono());
            pstmt.setLong(4, contacto.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Contacto actualizado con ID: " + contacto.getId());
                return true;
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar contacto con ID: " + contacto.getId(), e);
        }
        
        return false;
    }

    /**
     * Mapea un ResultSet a un objeto Contacto
     */
    private Contacto mapResultSetToContacto(ResultSet rs) throws SQLException {
        Contacto contacto = new Contacto();
        contacto.setId(rs.getLong("id"));
        contacto.setNombre(rs.getString("nombre"));
        contacto.setEmail(rs.getString("email"));
        contacto.setTelefono(rs.getString("telefono"));
        return contacto;
    }
}

