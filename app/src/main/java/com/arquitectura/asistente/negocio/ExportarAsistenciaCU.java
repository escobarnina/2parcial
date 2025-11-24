package com.arquitectura.asistente.negocio;

import android.content.Context;
import android.util.Log;

import com.arquitectura.asistente.datos.Asistencia;
import com.arquitectura.asistente.datos.AsistenciaExport;
import com.arquitectura.asistente.datos.ExportResult;
import com.arquitectura.asistente.datos.Grupo;
import com.arquitectura.asistente.datos.Horario;
import com.arquitectura.asistente.datos.adapter.DataExportAdapter;

import java.util.List;

/**
 * Caso de uso para exportar asistencias en diferentes formatos
 * Capa de Negocio - Adapter Pattern - Client
 * Este UseCase es el cliente en el patrón Adapter:
 * - NO conoce los detalles de implementación específicos (Excel, PDF)
 * - Solo conoce la interface Target (DataExportAdapter)
 * - Delega la exportación al adapter sin saber cuál es
 * 
 * FLUJO DE CREACIÓN DE AsistenciaExport:
 * 1. Este UseCase llama a su instancia de Asistencia (asistenciaData)
 *    para obtener los datos completos mediante obtenerPorGrupoParaExportacion(grupoId)
 * 2. Asistencia (capa de datos) crea las instancias de AsistenciaExport
 *    usando mapCursorToAsistenciaExport() con datos de JOINs SQL
 * 3. Retorna List<AsistenciaExport> con información completa
 * 4. Este UseCase pasa la lista al adapter (Excel o PDF)
 * 5. El adapter utiliza los DTOs para generar el archivo (NO los crea)
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
    private Horario horarioData;
    
    // Relaciones adicionales requeridas por el diagrama
    private List<AsistenciaExport> asistenciasParaExportar;
    private ExportResult ultimoResultado;
    private DataExportAdapter adapterActual;

    public ExportarAsistenciaCU(Context context) {
        // Crear instancias explícitas de las clases de datos
        // Estas instancias hacen visible la relación en diagramas de clases
        // y documentan claramente la dependencia de ExportarAsistenciaCU con estas clases
        this.asistenciaData = new Asistencia(context);
        this.grupoData = new Grupo(context);
        this.horarioData = new Horario(context);
        
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
            // Hacer visible la relación con el Adapter concreto que se está usando
            this.adapterActual = adapter;

            // Validar ID del grupo
            if (grupoId == null || grupoId <= 0) {
                ultimoResultado = ExportResult.error("ID de grupo inválido");
                return ultimoResultado;
            }

            // Obtener las asistencias con información completa para exportación
            // IMPORTANTE: Las instancias de AsistenciaExport se CREAN aquí en la capa de datos
            // mediante asistenciaData.obtenerPorGrupoParaExportacion() que usa JOINs SQL
            // y mapCursorToAsistenciaExport() para mapear los resultados
            // Acceso a datos a través de la clase de datos (aunque use métodos estáticos)
            asistenciasParaExportar = 
                asistenciaData.obtenerPorGrupoParaExportacion(grupoId);
            
            // Validar que haya datos para exportar
            if (asistenciasParaExportar.isEmpty()) {
                Log.w(TAG, "No hay asistencias para exportar del grupo " + grupoId);
                ultimoResultado = ExportResult.error("No hay asistencias para exportar");
                return ultimoResultado;
            }

            // Generar nombre del archivo
            String nombreArchivo = "asistencias_grupo_" + grupoId;

            // Delegar la exportación al adapter (Adapter Pattern)
            // El UseCase NO sabe si es Excel, PDF u otro formato
            byte[] datos = adapter.exportar(asistenciasParaExportar, nombreArchivo);

            Log.d(TAG, "Exportacion completada exitosamente: " + asistenciasParaExportar.size() + " registros");

            // Retornar resultado exitoso
            ultimoResultado = ExportResult.success(
                datos,
                nombreArchivo,
                adapter.obtenerExtension(),
                adapter.obtenerTipoMime(),
                adapter.obtenerNombreFormato(),
                asistenciasParaExportar.size()
            );
            return ultimoResultado;

        } catch (Exception e) {
            Log.e(TAG, "Error al exportar asistencias: " + e.getMessage(), e);
            ultimoResultado = ExportResult.error("Error al exportar: " + e.getMessage());
            return ultimoResultado;
        }
    }

    /**
     * Verifica si hay asistencias para exportar
     * Acceso a datos a través de la clase de datos (aunque use métodos estáticos)
     */
    public boolean tieneAsistenciasParaExportar(Integer grupoId) {
        asistenciasParaExportar = 
            asistenciaData.obtenerPorGrupoParaExportacion(grupoId);
        return !asistenciasParaExportar.isEmpty();
    }

    public List<Grupo> obtenerGruposPorDocente(Integer docenteId) {
        return grupoData.obtenerPorDocente(docenteId);
    }

    public List<Horario> obtenerHorariosDeGrupo(Integer grupoId) {
        return horarioData.obtenerHorariosGrupo(grupoId);
    }

    public List<AsistenciaExport> getAsistenciasParaExportar() {
        return asistenciasParaExportar;
    }

    public ExportResult getUltimoResultado() {
        return ultimoResultado;
    }

    public DataExportAdapter getAdapterActual() {
        return adapterActual;
    }
}

