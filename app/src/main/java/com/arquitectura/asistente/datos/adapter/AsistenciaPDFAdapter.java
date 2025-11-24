package com.arquitectura.asistente.datos.adapter;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Logger;

/**
 * Adapter concreto para exportar asistencias a formato PDF
 * Capa de Datos - Adapter Pattern - Adapter 2
 * Adapta iText (Adaptee) a DataExportAdapter (Target)
 */
public class AsistenciaPDFAdapter implements DataExportAdapter {
    private static final Logger logger = Logger.getLogger(AsistenciaPDFAdapter.class.getName());

    @Override
    public byte[] exportar(List<AsistenciaExportDTO> data, String nombreArchivo) throws Exception {
        logger.info("Iniciando exportacion PDF - Cantidad: " + data.size());
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            document.add(new Paragraph("Reporte de Asistencias").setBold().setFontSize(16));
            document.add(new Paragraph(" "));
            
            // Crear tabla con información completa
            Table table = new Table(10);
            
            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("Registro")));
            table.addHeaderCell(new Cell().add(new Paragraph("Estudiante")));
            table.addHeaderCell(new Cell().add(new Paragraph("Materia")));
            table.addHeaderCell(new Cell().add(new Paragraph("Sigla")));
            table.addHeaderCell(new Cell().add(new Paragraph("Grupo")));
            table.addHeaderCell(new Cell().add(new Paragraph("Docente")));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha")));
            table.addHeaderCell(new Cell().add(new Paragraph("Hora")));
            table.addHeaderCell(new Cell().add(new Paragraph("Estado")));
            
            // Datos con información completa
            for (AsistenciaExportDTO dto : data) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getId() != null ? dto.getId() : 0))));
                table.addCell(new Cell().add(new Paragraph(dto.getAlumnoRegistro() != null ? dto.getAlumnoRegistro() : "")));
                table.addCell(new Cell().add(new Paragraph(dto.getAlumnoNombre() != null ? dto.getAlumnoNombre() : "")));
                table.addCell(new Cell().add(new Paragraph(dto.getMateriaNombre() != null ? dto.getMateriaNombre() : "")));
                table.addCell(new Cell().add(new Paragraph(dto.getMateriaSigla() != null ? dto.getMateriaSigla() : "")));
                table.addCell(new Cell().add(new Paragraph(dto.getGrupoParalelo() != null ? dto.getGrupoParalelo() : "")));
                table.addCell(new Cell().add(new Paragraph(dto.getDocenteNombre() != null ? dto.getDocenteNombre() : "")));
                table.addCell(new Cell().add(new Paragraph(dto.getFecha() != null ? dto.getFecha() : "")));
                table.addCell(new Cell().add(new Paragraph(dto.getHoraMarcada() != null ? dto.getHoraMarcada() : "")));
                table.addCell(new Cell().add(new Paragraph(dto.getEstado() != null ? dto.getEstado() : "")));
            }
            
            document.add(table);
            document.close();
            
            logger.info("Exportacion PDF completada exitosamente");
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.severe("Error al exportar a PDF: " + e.getMessage());
            throw new Exception("Error al exportar a PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public String obtenerExtension() {
        return "pdf";
    }

    @Override
    public String obtenerTipoMime() {
        return "application/pdf";
    }

    @Override
    public String obtenerNombreFormato() {
        return "PDF";
    }
}

