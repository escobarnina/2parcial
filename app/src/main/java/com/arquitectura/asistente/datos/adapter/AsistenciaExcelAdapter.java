package com.arquitectura.asistente.datos.adapter;

import com.arquitectura.asistente.datos.Asistencia;
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
    public byte[] exportar(List<Asistencia> data, String nombreArchivo) throws Exception {
        logger.info("Iniciando exportacion Excel - Cantidad: " + data.size());
        
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Asistencias");
            
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] encabezados = {"ID", "ID_Alumno", "ID_Grupo", "Fecha", "Hora_Marcada", "Estado"};
            for (int i = 0; i < encabezados.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(encabezados[i]);
            }
            
            // Agregar datos
            int rowNum = 1;
            for (Asistencia asistencia : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(asistencia.getId());
                row.createCell(1).setCellValue(asistencia.getAlumnoId());
                row.createCell(2).setCellValue(asistencia.getGrupoId());
                row.createCell(3).setCellValue(asistencia.getFecha());
                row.createCell(4).setCellValue(asistencia.getHoraMarcada() != null ? asistencia.getHoraMarcada() : "");
                row.createCell(5).setCellValue(asistencia.getEstado() != null ? asistencia.getEstado() : "");
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

