package Arquinuevo.negocio;

import Arquinuevo.datos.Asistencia;
import Arquinuevo.datos.AsistenciaRepository;
import Arquinuevo.datos.ExportResult;
import Arquinuevo.datos.adapter.DataExportAdapter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Caso de uso para exportar asistencias en diferentes formatos
 * Capa de Negocio - Adapter Pattern - Client
 * Este UseCase es el cliente en el patrón Adapter:
 * - NO conoce los detalles de implementación específicos (Excel, PDF)
 * - Solo conoce la interface Target (DataExportAdapter)
 * - Delega la exportación al adapter sin saber cuál es
 */
public class ExportarAsistenciaCU {
    private static final Logger logger = Logger.getLogger(ExportarAsistenciaCU.class.getName());
    private AsistenciaRepository repository;

    public ExportarAsistenciaCU() {
        this.repository = AsistenciaRepository.getInstance();
    }

    /**
     * Exporta las asistencias de un grupo usando el adapter especificado
     * Patrón Adapter: El UseCase solo conoce la interface, no la implementación
     * 
     * @param grupoId ID del grupo del cual exportar las asistencias
     * @param adapter Implementación del adapter (puede ser Excel, PDF, etc.)
     * @return ExportResult con el resultado de la exportación
     */
    public ExportResult exportar(Integer grupoId, DataExportAdapter adapter) {
        logger.info("Iniciando exportacion de asistencias para grupo ID: " + grupoId);
        
        try {
            // Validar ID del grupo
            if (grupoId == null || grupoId <= 0) {
                return ExportResult.error("ID de grupo inválido");
            }

            // Obtener las asistencias del repository
            List<Asistencia> asistencias = repository.obtenerPorGrupo(grupoId);
            
            // Validar que haya datos para exportar
            if (asistencias.isEmpty()) {
                logger.warning("No hay asistencias para exportar del grupo " + grupoId);
                return ExportResult.error("No hay asistencias para exportar");
            }

            // Generar nombre del archivo
            String nombreArchivo = "asistencias_grupo_" + grupoId;

            // Delegar la exportación al adapter (Adapter Pattern)
            // El UseCase NO sabe si es Excel, PDF u otro formato
            byte[] datos = adapter.exportar(asistencias, nombreArchivo);

            logger.info("Exportacion completada exitosamente: " + asistencias.size() + " registros");

            // Retornar resultado exitoso
            return ExportResult.success(
                datos,
                nombreArchivo,
                adapter.obtenerExtension(),
                adapter.obtenerTipoMime(),
                adapter.obtenerNombreFormato(),
                asistencias.size()
            );

        } catch (Exception e) {
            logger.severe("Error al exportar asistencias: " + e.getMessage());
            return ExportResult.error("Error al exportar: " + e.getMessage());
        }
    }

    /**
     * Verifica si hay asistencias para exportar
     */
    public boolean tieneAsistenciasParaExportar(Integer grupoId) {
        List<Asistencia> asistencias = repository.obtenerPorGrupo(grupoId);
        return !asistencias.isEmpty();
    }
}

