# Patrón Adapter - Sistema de Exportación

## 📋 Índice
1. [Introducción](#introducción)
2. [Mapeo del Patrón Adapter](#mapeo-del-patrón-adapter)
3. [Comparación con Pseudocódigo](#comparación-con-pseudocódigo)
4. [Implementación en el Sistema](#implementación-en-el-sistema)
5. [Flujo de Ejecución](#flujo-de-ejecución)
6. [Ventajas del Patrón](#ventajas-del-patrón)
7. [Estructura de Archivos](#estructura-de-archivos)

---

## Introducción

El **Patrón Adapter** permite que objetos con interfaces incompatibles trabajen juntos. En nuestro sistema de asistencia, este patrón se utiliza para exportar datos de asistencia a diferentes formatos (Excel, PDF) utilizando librerías externas con APIs propias e incompatibles.

---

## Mapeo del Patrón Adapter

### Componentes del Patrón

| Componente del Patrón | Implementación en el Sistema | Archivo |
|----------------------|------------------------------|---------|
| **Client** | `ExportarAsistenciaCU` | `negocio/ExportarAsistenciaCU.java` |
| **Target** | `DataExportAdapter` | `datos/adapter/DataExportAdapter.java` |
| **Adapter 1** | `AsistenciaExcelAdapter` | `datos/adapter/AsistenciaExcelAdapter.java` |
| **Adapter 2** | `AsistenciaPDFAdapter` | `datos/adapter/AsistenciaPDFAdapter.java` |
| **Adaptee 1** | Apache POI (librería externa) | Librería externa |
| **Adaptee 2** | iText (librería externa) | Librería externa |

---

## Comparación con Pseudocódigo

### 1. Target (Interfaz común)

#### Pseudocódigo:
```pseudocode
class RoundPeg is
    method getRadius() is
        // Devuelve el radio de la pieza.
```

#### Implementación Real:
```java
public interface DataExportAdapter {
    /**
     * Exporta los datos a un formato específico
     * 
     * @param data Lista de DTOs de asistencias con información completa para exportar
     * @param nombreArchivo Nombre base del archivo (sin extensión)
     * @return byte[] con el contenido del archivo generado
     * @throws Exception Si ocurre un error durante la exportación
     */
    byte[] exportar(List<AsistenciaExportDTO> data, String nombreArchivo) throws Exception;
    
    String obtenerExtension();
    String obtenerTipoMime();
    String obtenerNombreFormato();
}
```

**Diferencia clave**: En lugar de una simple operación `getRadius()`, definimos un contrato completo para exportación de datos que incluye métodos para obtener información del formato.

---

### 2. Adaptee (Librerías externas)

#### Pseudocódigo:
```pseudocode
class SquarePeg is
    method getWidth() is
        // Devuelve la anchura de la pieza cuadrada.
```

#### Implementación Real:

**Apache POI (para Excel)** - API específica:
```java
XSSFWorkbook workbook = new XSSFWorkbook();
Sheet sheet = workbook.createSheet("Asistencias");
Row headerRow = sheet.createRow(0);
Cell cell = headerRow.createCell(0);
cell.setCellValue("ID");
// ... API específica de POI
```

**iText (para PDF)** - API específica:
```java
PdfDocument pdf = new PdfDocument(writer);
Document document = new Document(pdf);
Table table = new Table(10);
table.addHeaderCell(new Cell().add(new Paragraph("ID")));
// ... API específica de iText
```

**Diferencia clave**: Son librerías externas complejas con APIs propias que no son compatibles entre sí ni con nuestra interfaz común.

---

### 3. Adapter (Adaptadores concretos)

#### Pseudocódigo:
```pseudocode
class SquarePegAdapter extends RoundPeg is
    private field peg: SquarePeg
    
    constructor SquarePegAdapter(peg: SquarePeg) is
        this.peg = peg
    
    method getRadius() is
        // El adaptador simula que es una pieza redonda
        return peg.getWidth() * Math.sqrt(2) / 2
```

#### Implementación Real:

**AsistenciaExcelAdapter:**
```java
public class AsistenciaExcelAdapter implements DataExportAdapter {
    
    @Override
    public byte[] exportar(List<AsistenciaExportDTO> data, String nombreArchivo) throws Exception {
        logger.info("Iniciando exportacion Excel - Cantidad: " + data.size());
        
        // El adaptador "envuelve" Apache POI (Adaptee)
        try (XSSFWorkbook workbook = new XSSFWorkbook();  // ← Adaptee (POI)
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Asistencias");
            
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] encabezados = {"ID", "Registro", "Estudiante", "Materia", ...};
            for (int i = 0; i < encabezados.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(encabezados[i]);
            }
            
            // Convierte los datos del DTO a formato POI
            int rowNum = 1;
            for (AsistenciaExportDTO dto : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dto.getId());
                row.createCell(1).setCellValue(dto.getAlumnoRegistro());
                // ... adapta todos los campos del DTO a POI
            }
            
            // Retorna en formato que espera el Target (byte[])
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    @Override
    public String obtenerExtension() {
        return "xlsx";  // Implementa el contrato del Target
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
```

**AsistenciaPDFAdapter:**
```java
public class AsistenciaPDFAdapter implements DataExportAdapter {
    
    @Override
    public byte[] exportar(List<AsistenciaExportDTO> data, String nombreArchivo) throws Exception {
        logger.info("Iniciando exportacion PDF - Cantidad: " + data.size());
        
        // El adaptador "envuelve" iText (Adaptee)
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);  // ← Adaptee (iText)
             Document document = new Document(pdf)) {
            
            document.add(new Paragraph("Reporte de Asistencias").setBold().setFontSize(16));
            
            // Crear tabla
            Table table = new Table(10);
            table.addHeaderCell(new Cell().add(new Paragraph("ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("Registro")));
            // ... más encabezados
            
            // Convierte los datos del DTO a formato iText
            for (AsistenciaExportDTO dto : data) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getId()))));
                table.addCell(new Cell().add(new Paragraph(dto.getAlumnoRegistro())));
                // ... adapta todos los campos del DTO a iText
            }
            
            document.add(table);
            document.close();
            
            // Retorna en formato que espera el Target (byte[])
            return outputStream.toByteArray();
        }
    }
    
    @Override
    public String obtenerExtension() {
        return "pdf";  // Implementa el contrato del Target
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
```

**Diferencia clave**: Los adaptadores convierten datos del DTO (`AsistenciaExportDTO`) al formato específico de cada librería externa y retornan el resultado en un formato común (`byte[]`).

---

### 4. Client (Cliente)

#### Pseudocódigo:
```pseudocode
class ExampleApplication is
    method main() is
        hole = new RoundHole(5)
        small_sqpeg_adapter = new SquarePegAdapter(small_sqpeg)
        hole.fits(small_sqpeg_adapter) // funciona porque el adapter implementa RoundPeg
```

#### Implementación Real:
```java
public class ExportarAsistenciaCU {
    private static final String TAG = "ExportarAsistenciaCU";
    
    /**
     * Exporta las asistencias de un grupo usando el adapter especificado
     * Patrón Adapter: El UseCase solo conoce la interface, no la implementación
     */
    public ExportResult exportar(Integer grupoId, DataExportAdapter adapter) {
        Log.d(TAG, "Iniciando exportacion de asistencias para grupo ID: " + grupoId);
        
        try {
            // Validar ID del grupo
            if (grupoId == null || grupoId <= 0) {
                return ExportResult.error("ID de grupo inválido");
            }
            
            // Obtener las asistencias con información completa para exportación
            List<AsistenciaExportDTO> asistenciasDTO = 
                Asistencia.obtenerPorGrupoParaExportacion(grupoId);
            
            // Validar que haya datos para exportar
            if (asistenciasDTO.isEmpty()) {
                return ExportResult.error("No hay asistencias para exportar");
            }
            
            // Generar nombre del archivo
            String nombreArchivo = "asistencias_grupo_" + grupoId;
            
            // Delegar la exportación al adapter (Adapter Pattern)
            // El cliente NO sabe si es Excel, PDF u otro formato
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
}
```

**Diferencia clave**: El cliente no conoce las librerías externas (POI, iText), solo conoce la interfaz `DataExportAdapter` y puede trabajar con cualquier adaptador que la implemente.

---

## Implementación en el Sistema

### DTO para Exportación

Para evitar modificar la entidad `Asistencia`, utilizamos un **DTO (Data Transfer Object)** que contiene toda la información necesaria para la exportación:

```java
public class AsistenciaExportDTO {
    // Campos básicos de asistencia
    private Integer id;
    private Integer alumnoId;
    private Integer grupoId;
    private String fecha;
    private String horaMarcada;
    private String estado;
    
    // Información relacionada (poblada mediante JOINs)
    private String alumnoNombre;      // Nombre completo del estudiante
    private String alumnoRegistro;    // Registro del estudiante
    private String materiaNombre;     // Nombre de la materia
    private String materiaSigla;      // Sigla de la materia
    private String grupoParalelo;     // Grupo/Paralelo (A, B, etc.)
    private String docenteNombre;     // Nombre completo del docente
}
```

Este DTO se obtiene mediante un método especial en `Asistencia` que hace JOINs con las tablas relacionadas:

```java
public static List<AsistenciaExportDTO> obtenerPorGrupoParaExportacion(Integer grupoId) {
    // Consulta con JOINs para obtener información completa
    String sql = "SELECT " +
                "a.id, a.alumno_id, a.grupo_id, a.fecha, a.hora_marcada, a.estado, " +
                "u.nombres || ' ' || u.apellidos as alumno_nombre, " +
                "u.registro as alumno_registro, " +
                "m.nombre as materia_nombre, m.sigla as materia_sigla, " +
                "g.grupo as grupo_paralelo, " +
                "ud.nombres || ' ' || ud.apellidos as docente_nombre " +
                "FROM asistencias a " +
                "INNER JOIN usuarios u ON a.alumno_id = u.id " +
                "INNER JOIN grupos g ON a.grupo_id = g.id " +
                "INNER JOIN materias m ON g.materia_id = m.id " +
                "INNER JOIN usuarios ud ON g.docente_id = ud.id " +
                "WHERE a.grupo_id = ? " +
                "ORDER BY u.apellidos, u.nombres, a.fecha DESC";
    // ... mapeo a DTO
}
```

### Uso del Patrón

El cliente (`ExportarAsistenciaCU`) puede usar cualquier adaptador sin conocer su implementación:

```java
// En DocenteActivity
ExportarAsistenciaCU exportarCU = new ExportarAsistenciaCU(this);

// Crear adaptadores (el cliente puede elegir cuál usar)
DataExportAdapter excelAdapter = new AsistenciaExcelAdapter();
DataExportAdapter pdfAdapter = new AsistenciaPDFAdapter();

// Exportar a Excel
ExportResult resultadoExcel = exportarCU.exportar(grupoId, excelAdapter);

// Exportar a PDF
ExportResult resultadoPDF = exportarCU.exportar(grupoId, pdfAdapter);

// El cliente NO necesita saber cómo funciona POI o iText
// Solo sabe que ambos implementan DataExportAdapter
```

---

## Flujo de Ejecución

### Diagrama de Flujo

```
┌─────────────────────┐
│ DocenteActivity     │  (Presentación)
│  - onGrupoClick()   │
└──────────┬──────────┘
           │
           │ exportarAsistencias(grupo, adapter)
           ▼
┌─────────────────────┐
│ ExportarAsistenciaCU│  (Client)
│  - exportar()       │
└──────────┬──────────┘
           │
           │ 1. Obtener datos (AsistenciaExportDTO)
           │ 2. adapter.exportar(dto, nombreArchivo)
           ▼
┌─────────────────────┐
│ DataExportAdapter   │  (Target Interface)
│  - exportar()       │
└──────────┬──────────┘
           │
     ┌─────┴─────┬──────────────┐
     │           │              │
     ▼           ▼              ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│Asistencia│ │Asistencia│ │  (Futuro)│
│   Excel  │ │   PDF    │ │   CSV    │
│ Adapter  │ │ Adapter  │ │ Adapter  │
└────┬─────┘ └────┬─────┘ └────┬─────┘
     │            │            │
     │ envuelve   │ envuelve   │
     ▼            ▼            ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│ Apache   │ │   iText  │ │   (Otra) │
│   POI    │ │          │ │ Librería │
│ (Adaptee)│ │ (Adaptee)│ │ (Adaptee)│
└──────────┘ └──────────┘ └──────────┘
```

### Ejemplo de Ejecución

**Escenario**: Docente exporta asistencias del Grupo 1 a Excel

1. **DocenteActivity** (Presentación):
   ```java
   exportarAsistencias(grupo, new AsistenciaExcelAdapter());
   ```

2. **ExportarAsistenciaCU** (Client):
   - Valida `grupoId`
   - Obtiene `List<AsistenciaExportDTO>` mediante JOINs
   - Llama: `adapter.exportar(asistenciasDTO, "asistencias_grupo_1")`

3. **AsistenciaExcelAdapter** (Adapter):
   - Crea `XSSFWorkbook` (Apache POI - Adaptee)
   - Crea hoja y encabezados
   - Itera sobre `asistenciasDTO` y crea filas usando API de POI
   - Escribe a `ByteArrayOutputStream`
   - Retorna `byte[]`

4. **ExportarAsistenciaCU** (Client):
   - Recibe `byte[]` del adaptador
   - Crea `ExportResult` con los datos
   - Retorna resultado al cliente

5. **DocenteActivity** (Presentación):
   - Muestra mensaje de éxito con cantidad de registros exportados

---

## Ventajas del Patrón

### ✅ Beneficios en Nuestro Sistema

1. **Desacoplamiento**: El cliente (`ExportarAsistenciaCU`) no conoce las librerías externas (POI, iText)
2. **Extensibilidad**: Puedes agregar nuevos formatos (CSV, JSON) sin modificar el cliente
3. **Principio Abierto/Cerrado**: Abierto a extensión (nuevos adaptadores), cerrado a modificación (cliente no cambia)
4. **Responsabilidad única**: Cada adaptador se encarga de un formato específico
5. **Mantenibilidad**: Cambios en las librerías externas solo afectan al adaptador correspondiente
6. **Testabilidad**: Puedes crear adaptadores mock para pruebas unitarias

### 🔄 Intercambiabilidad

Los adaptadores son completamente intercambiables:

```java
// El mismo cliente puede usar diferentes adaptadores
ExportarAsistenciaCU exportarCU = new ExportarAsistenciaCU(this);

// Exportar a Excel
DataExportAdapter excel = new AsistenciaExcelAdapter();
ExportResult r1 = exportarCU.exportar(grupoId, excel);

// Exportar a PDF
DataExportAdapter pdf = new AsistenciaPDFAdapter();
ExportResult r2 = exportarCU.exportar(grupoId, pdf);

// En el futuro: Exportar a CSV (sin modificar ExportarAsistenciaCU)
DataExportAdapter csv = new AsistenciaCSVAdapter();
ExportResult r3 = exportarCU.exportar(grupoId, csv);
```

---

## Estructura de Archivos

### Organización en la Carpeta `adapter`

```
datos/adapter/
├── DataExportAdapter.java          # Target (Interface)
├── AsistenciaExportDTO.java        # DTO para exportación
├── ExportResult.java               # Resultado de exportación
├── AsistenciaExcelAdapter.java     # Adapter 1 (POI)
└── AsistenciaPDFAdapter.java       # Adapter 2 (iText)
```

### Relación entre Componentes

```
┌─────────────────────────────────────────────────┐
│         Capa de Negocio                         │
│  ExportarAsistenciaCU (Client)                 │
│  - Solo conoce DataExportAdapter               │
└──────────────────┬──────────────────────────────┘
                   │ usa
                   ▼
┌─────────────────────────────────────────────────┐
│         Capa de Datos - adapter/                │
│                                                 │
│  DataExportAdapter (Target)                    │
│  ├── AsistenciaExcelAdapter (Adapter)          │
│  │   └── Apache POI (Adaptee)                 │
│  ├── AsistenciaPDFAdapter (Adapter)            │
│  │   └── iText (Adaptee)                      │
│  └── (Futuros adaptadores...)                  │
│                                                 │
│  AsistenciaExportDTO (DTO)                     │
│  ExportResult (Resultado)                      │
└─────────────────────────────────────────────────┘
```

---

## Resumen

| Aspecto | Pseudocódigo | Implementación Real |
|---------|--------------|---------------------|
| **Target** | `RoundPeg.getRadius()` | `DataExportAdapter.exportar(...)` |
| **Adaptee** | `SquarePeg.getWidth()` | Apache POI / iText (APIs propias) |
| **Adapter** | `SquarePegAdapter` convierte `getWidth()` a `getRadius()` | `AsistenciaExcelAdapter` / `AsistenciaPDFAdapter` convierten DTOs a formatos específicos |
| **Client** | `RoundHole.fits(RoundPeg)` | `ExportarAsistenciaCU.exportar(DataExportAdapter)` |
| **Resultado** | Boolean (encaja o no) | `byte[]` (archivo generado) |

---

## Conclusión

El patrón Adapter en nuestro sistema permite exportar datos de asistencia a diferentes formatos (Excel, PDF) utilizando librerías externas con APIs incompatibles, sin que el cliente conozca los detalles de implementación. Esto hace el sistema más extensible, mantenible y fácil de probar.

---

**Autor**: Sistema de Asistencia - Arquitectura de Software  
**Fecha**: 2025  
**Versión**: 1.0
