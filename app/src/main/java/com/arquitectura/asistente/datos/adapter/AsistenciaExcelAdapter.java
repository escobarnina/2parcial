package com.arquitectura.asistente.datos.adapter;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Logger;

/**
 * Adapter concreto para exportar asistencias a formato Excel (.xlsx)
 * Capa de Datos - Adapter Pattern - Adapter 1
 * Adapta Apache POI (Adaptee) a DataExportAdapter (Target)
 */
public class AsistenciaExcelAdapter implements DataExportAdapter {
    private static final Logger logger = Logger.getLogger(AsistenciaExcelAdapter.class.getName());

    @Override
    public byte[] exportar(List<AsistenciaExportDTO> data, String nombreArchivo) throws Exception {
        logger.info("Iniciando exportacion Excel - Cantidad: " + data.size());
        
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Asistencias");
            
            // Crear encabezados con información completa
            Row headerRow = sheet.createRow(0);
            String[] encabezados = {
                "ID", 
                "Registro", 
                "Estudiante", 
                "Materia", 
                "Sigla", 
                "Grupo", 
                "Docente", 
                "Fecha", 
                "Hora Marcada", 
                "Estado"
            };
            for (int i = 0; i < encabezados.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(encabezados[i]);
            }
            
            // Agregar datos con información completa
            int rowNum = 1;
            for (AsistenciaExportDTO dto : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dto.getId() != null ? dto.getId() : 0);
                row.createCell(1).setCellValue(dto.getAlumnoRegistro() != null ? dto.getAlumnoRegistro() : "");
                row.createCell(2).setCellValue(dto.getAlumnoNombre() != null ? dto.getAlumnoNombre() : "");
                row.createCell(3).setCellValue(dto.getMateriaNombre() != null ? dto.getMateriaNombre() : "");
                row.createCell(4).setCellValue(dto.getMateriaSigla() != null ? dto.getMateriaSigla() : "");
                row.createCell(5).setCellValue(dto.getGrupoParalelo() != null ? dto.getGrupoParalelo() : "");
                row.createCell(6).setCellValue(dto.getDocenteNombre() != null ? dto.getDocenteNombre() : "");
                row.createCell(7).setCellValue(dto.getFecha() != null ? dto.getFecha() : "");
                row.createCell(8).setCellValue(dto.getHoraMarcada() != null ? dto.getHoraMarcada() : "");
                row.createCell(9).setCellValue(dto.getEstado() != null ? dto.getEstado() : "");
            }
            
            workbook.write(outputStream);
            logger.info("Exportacion Excel completada exitosamente");
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.severe("Error al exportar a Excel: " + e.getMessage());
            throw new Exception("Error al exportar a Excel: " + e.getMessage(), e);
        }
    }

    @Override
    public String obtenerExtension() {
        return "xlsx";
    }

    @Override
    public String obtenerTipoMime() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String obtenerNombreFormato() {
        return "Excel";
    }
}

