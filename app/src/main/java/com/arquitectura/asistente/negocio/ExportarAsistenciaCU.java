package com.arquitectura.asistente.negocio;

import android.content.Context;
import android.util.Log;

import com.arquitectura.asistente.datos.Asistencia;
import com.arquitectura.asistente.datos.Grupo;
import com.arquitectura.asistente.datos.adapter.AsistenciaExportDTO;
import com.arquitectura.asistente.datos.adapter.DataExportAdapter;
import com.arquitectura.asistente.datos.adapter.ExportResult;

import java.util.List;

/**
 * Caso de uso para exportar asistencias en diferentes formatos
 * Capa de Negocio - Adapter Pattern - Client
 * Este UseCase es el cliente en el patrón Adapter:
 * - NO conoce los detalles de implementación específicos (Excel, PDF)
 * - Solo conoce la interface Target (DataExportAdapter)
 * - Delega la exportación al adapter sin saber cuál es
 * 
 * RELACIONES EXPLÍCITAS (Capa de Negocio):
 * - ExportarAsistenciaCU -> Asistencia (instancia, capa de datos)
 * - ExportarAsistenciaCU -> Grupo (instancia, capa de datos)
 * 
 * NOTA: Las relaciones con DatabaseBaseDAO y DatabaseHelper son responsabilidad
 * de la capa de datos (Asistencia, Grupo), no de la capa de negocio.
 * La capa de negocio solo trabaja con las clases de datos, que internamente
 * gestionan su acceso a la base de datos.
 */
public class ExportarAsistenciaCU {
    private static final String TAG = "ExportarAsistenciaCU";
    
    // Instancias explícitas de las clases de datos para hacer visibles las relaciones
    private Asistencia asistenciaData;
    private Grupo grupoData;

    public ExportarAsistenciaCU(Context context) {
        // Inicializar acceso a datos de todas las entidades necesarias
        Asistencia.inicializar(context);
        Grupo.inicializar(context);
        
        // Crear instancias explícitas de las clases de datos
        // Estas instancias hacen visible la relación en diagramas de clases
        // Aunque las clases usen métodos estáticos internamente, tener instancias
        // documenta claramente la dependencia de ExportarAsistenciaCU con estas clases
        this.asistenciaData = new Asistencia();
        this.grupoData = new Grupo();
        
        Log.d(TAG, "ExportarAsistenciaCU inicializado con instancias explícitas de clases de datos");
    }
    
    /**
     * Obtiene la instancia de Asistencia (para hacer explícita la relación)
     */
    public Asistencia getAsistenciaData() {
        return asistenciaData;
    }
    
    /**
     * Obtiene la instancia de Grupo (para hacer explícita la relación)
     */
    public Grupo getGrupoData() {
        return grupoData;
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
        Log.d(TAG, "Iniciando exportacion de asistencias para grupo ID: " + grupoId);
        
        try {
            // Validar ID del grupo
            if (grupoId == null || grupoId <= 0) {
                return ExportResult.error("ID de grupo inválido");
            }

            // Obtener las asistencias con información completa para exportación
            // Usa JOINs para obtener nombres de estudiantes, materias, grupos, etc.
            // Acceso a datos a través de la clase de datos (aunque use métodos estáticos)
            List<AsistenciaExportDTO> asistenciasDTO = 
                Asistencia.obtenerPorGrupoParaExportacion(grupoId);
            
            // Validar que haya datos para exportar
            if (asistenciasDTO.isEmpty()) {
                Log.w(TAG, "No hay asistencias para exportar del grupo " + grupoId);
                return ExportResult.error("No hay asistencias para exportar");
            }

            // Generar nombre del archivo
            String nombreArchivo = "asistencias_grupo_" + grupoId;

            // Delegar la exportación al adapter (Adapter Pattern)
            // El UseCase NO sabe si es Excel, PDF u otro formato
            byte[] datos = adapter.exportar(asistenciasDTO, nombreArchivo);

            Log.d(TAG, "Exportacion completada exitosamente: " + asistenciasDTO.size() + " registros");

            // Retornar resultado exitoso
            return ExportResult.success(
                datos,
                nombreArchivo,
                adapter.obtenerExtension(),
                adapter.obtenerTipoMime(),
                adapter.obtenerNombreFormato(),
                asistenciasDTO.size()
            );

        } catch (Exception e) {
            Log.e(TAG, "Error al exportar asistencias: " + e.getMessage(), e);
            return ExportResult.error("Error al exportar: " + e.getMessage());
        }
    }

    /**
     * Verifica si hay asistencias para exportar
     * Acceso a datos a través de la clase de datos (aunque use métodos estáticos)
     */
    public boolean tieneAsistenciasParaExportar(Integer grupoId) {
        List<AsistenciaExportDTO> asistenciasDTO = 
            Asistencia.obtenerPorGrupoParaExportacion(grupoId);
        return !asistenciasDTO.isEmpty();
    }
}

