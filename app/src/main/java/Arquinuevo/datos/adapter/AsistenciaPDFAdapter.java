package Arquinuevo.datos.adapter;

import Arquinuevo.datos.Asistencia;
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
    public byte[] exportar(List<Asistencia> data, String nombreArchivo) throws Exception {
        logger.info("Iniciando exportacion PDF - Cantidad: " + data.size());
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            document.add(new Paragraph("Reporte de Asistencias").setBold().setFontSize(16));
            document.add(new Paragraph(" "));
            
            // Crear tabla
            Table table = new Table(6);
            
            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("ID Alumno")));
            table.addHeaderCell(new Cell().add(new Paragraph("ID Grupo")));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha")));
            table.addHeaderCell(new Cell().add(new Paragraph("Hora")));
            table.addHeaderCell(new Cell().add(new Paragraph("Estado")));
            
            // Datos
            for (Asistencia asistencia : data) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(asistencia.getId()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(asistencia.getAlumnoId()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(asistencia.getGrupoId()))));
                table.addCell(new Cell().add(new Paragraph(asistencia.getFecha())));
                table.addCell(new Cell().add(new Paragraph(asistencia.getHoraMarcada() != null ? asistencia.getHoraMarcada() : "")));
                table.addCell(new Cell().add(new Paragraph(asistencia.getEstado() != null ? asistencia.getEstado() : "")));
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

