package Arquinuevo.negocio;

import Arquinuevo.datos.Contacto;
import Arquinuevo.datos.ContactoRepository;
import java.util.List;
import java.util.regex.Pattern;

/**
 * UseCase para la administración de contactos
 * Capa de Lógica de Negocio - Encapsula la lógica de negocio relacionada con contactos
 * Sigue el patrón del diagrama UML proporcionado (similar a AdministrarCategoriaUseCase)
 */
public class AdministrarContactoUseCase {
    private ContactoRepository repository;
    private static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public AdministrarContactoUseCase() {
        this.repository = ContactoRepository.getInstance();
    }

    /**
     * Valida los datos del contacto
     */
    private void validarContacto(Contacto contacto) throws Exception {
        if (contacto.getNombre() == null || contacto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }

        if (contacto.getEmail() == null || contacto.getEmail().trim().isEmpty()) {
            throw new Exception("El email es obligatorio");
        }

        if (!pattern.matcher(contacto.getEmail()).matches()) {
            throw new Exception("El formato del email no es valido");
        }

        if (contacto.getTelefono() == null || contacto.getTelefono().trim().isEmpty()) {
            throw new Exception("El telefono es obligatorio");
        }

        if (!contacto.getTelefono().matches("\\d{8,10}")) {
            throw new Exception("El telefono debe contener entre 8 y 10 digitos");
        }
    }

    /**
     * Crea un nuevo contacto con nombre, email y telefono
     * Similar al método crearCategoria del diagrama
     */
    public boolean crearContacto(String nombre, String email, String telefono) {
        try {
            Contacto contacto = new Contacto(nombre, email, telefono);
            validarContacto(contacto);
            repository.guardar(contacto);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear contacto: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los contactos
     * Similar al método obtenerCategorias del diagrama
     */
    public List<Contacto> obtenerContactos() {
        return repository.obtenerTodos();
    }

    /**
     * Obtiene un contacto por su ID
     * Similar al método obtenerCategoria del diagrama
     */
    public Contacto obtenerContacto(Long id) {
        return repository.buscarPorId(id);
    }

    /**
     * Actualiza un contacto existente
     * Similar al método actualizarCategoria del diagrama
     */
    public boolean actualizarContacto(Long id, String nombre, String email, String telefono) {
        try {
            Contacto contacto = new Contacto(id, nombre, email, telefono);
            validarContacto(contacto);
            return repository.actualizar(contacto);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar contacto: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un contacto por su ID
     * Similar al método eliminarCategoria del diagrama
     */
    public boolean eliminarContacto(Long id) {
        return repository.eliminar(id);
    }

    /**
     * Busca contactos por un criterio
     * Similar al método buscarCategorias del diagrama
     */
    public List<Contacto> buscarContactos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return obtenerContactos();
        }
        return repository.buscarPorNombre(criterio);
    }
}

