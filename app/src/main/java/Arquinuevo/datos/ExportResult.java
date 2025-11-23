package Arquinuevo.datos;

/**
 * Clase que representa el resultado de una operación de exportación
 * Capa de Datos - Resultado de exportación
 */
public class ExportResult {
    private boolean success;
    private byte[] datos;
    private String nombreArchivo;
    private String extension;
    private String tipoMime;
    private String formato;
    private int cantidadRegistros;
    private String mensajeError;

    private ExportResult(boolean success) {
        this.success = success;
    }

    public static ExportResult success(byte[] datos, String nombreArchivo, String extension, 
                                      String tipoMime, String formato, int cantidadRegistros) {
        ExportResult result = new ExportResult(true);
        result.datos = datos;
        result.nombreArchivo = nombreArchivo;
        result.extension = extension;
        result.tipoMime = tipoMime;
        result.formato = formato;
        result.cantidadRegistros = cantidadRegistros;
        return result;
    }

    public static ExportResult error(String mensajeError) {
        ExportResult result = new ExportResult(false);
        result.mensajeError = mensajeError;
        return result;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public byte[] getDatos() {
        return datos;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getExtension() {
        return extension;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public String getFormato() {
        return formato;
    }

    public int getCantidadRegistros() {
        return cantidadRegistros;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public String getNombreCompleto() {
        return nombreArchivo + "." + extension;
    }
}

