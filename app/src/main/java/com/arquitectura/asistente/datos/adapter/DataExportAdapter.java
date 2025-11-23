package com.arquitectura.asistente.datos.adapter;

import com.arquitectura.asistente.datos.Asistencia;
import java.util.List;

/**
 * Interface del Patrón Adapter para exportación de datos
 * Capa de Datos - Adapter Pattern - Target (Interface)
 * Define el contrato que deben cumplir todos los adaptadores de exportación
 */
public interface DataExportAdapter {
    
    /**
     * Exporta los datos a un formato específico
     * 
     * @param data Lista de asistencias a exportar
     * @param nombreArchivo Nombre base del archivo (sin extensión)
     * @return byte[] con el contenido del archivo generado
     * @throws Exception Si ocurre un error durante la exportación
     */
    byte[] exportar(List<Asistencia> data, String nombreArchivo) throws Exception;
    
    /**
     * Obtiene la extensión del archivo para este formato
     * @return Extensión sin el punto (ej: "xlsx", "pdf")
     */
    String obtenerExtension();
    
    /**
     * Obtiene el tipo MIME del formato de exportación
     * @return Tipo MIME (ej: "application/vnd.ms-excel", "application/pdf")
     */
    String obtenerTipoMime();
    
    /**
     * Obtiene el nombre descriptivo del formato
     * @return Nombre del formato (ej: "Excel", "PDF")
     */
    String obtenerNombreFormato();
}

