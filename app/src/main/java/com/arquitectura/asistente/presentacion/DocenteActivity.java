package com.arquitectura.asistente.presentacion;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.arquitectura.asistente.R;
import com.arquitectura.asistente.datos.Grupo;
import com.arquitectura.asistente.datos.Horario;
import com.arquitectura.asistente.datos.adapter.AsistenciaExcelAdapter;
import com.arquitectura.asistente.datos.adapter.AsistenciaPDFAdapter;
import com.arquitectura.asistente.datos.adapter.DataExportAdapter;
import com.arquitectura.asistente.datos.adapter.ExportResult;
import com.arquitectura.asistente.negocio.ExportarAsistenciaCU;
import com.arquitectura.asistente.presentacion.widget.GrupoAdapter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DocenteActivity - Capa de Presentación
 * Permite a los docentes ver sus grupos asignados y exportar reportes de asistencia por materia
 * Similar a EstudianteActivity pero con funcionalidad de exportación
 */
public class DocenteActivity extends AppCompatActivity implements GrupoAdapter.OnGrupoClickListener {
    private RecyclerView recyclerViewGrupos;
    private GrupoAdapter grupoAdapter;
    private ExportarAsistenciaCU exportarCU;
    
    // ID del docente (en una app real, esto vendría del login)
    // Docente 2 (María Fernández, id=62) tiene 4 materias - el que más tiene
    private static final Integer DOCENTE_ID = 62; // María Fernández - 4 materias (Programación I, BD I, BD II, Ing. Software)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docente);
        
        // Inicializar casos de uso y acceso a datos
        this.exportarCU = new ExportarAsistenciaCU(this);
        Grupo.inicializar(this);
        Horario.inicializar(this);
        
        inicializarVistas();
        cargarGruposAsignados();
    }

    private void inicializarVistas() {
        recyclerViewGrupos = findViewById(R.id.recyclerViewGrupos);
        
        // Configurar RecyclerView
        recyclerViewGrupos.setLayoutManager(new LinearLayoutManager(this));
        grupoAdapter = new GrupoAdapter(this);
        grupoAdapter.setContext(this);
        recyclerViewGrupos.setAdapter(grupoAdapter);
    }

    private void cargarGruposAsignados() {
        // Usar el método estático de Grupo siguiendo el patrón de EstudianteActivity
        List<Grupo> grupos = Grupo.obtenerPorDocente(DOCENTE_ID);
        
        if (grupos.isEmpty()) {
            Toast.makeText(this, getString(R.string.docente_sin_materias), Toast.LENGTH_LONG).show();
            return;
        }
        
        grupoAdapter.actualizarGrupos(grupos);
    }

    @Override
    public void onGrupoClick(Grupo grupo) {
        // Mostrar diálogo de exportación al presionar la tarjeta
        mostrarDialogoExportar(grupo);
    }

    private void mostrarDialogoExportar(Grupo grupo) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_exportar, null);
        bottomSheetDialog.setContentView(view);
        
        // Obtener vistas
        TextView tvMateriaInfo = view.findViewById(R.id.tvMateriaInfo);
        View optionExcel = view.findViewById(R.id.optionExcel);
        View optionPDF = view.findViewById(R.id.optionPDF);
        
        // Configurar información
        tvMateriaInfo.setText(grupo.getMateriaNombre() + " - Grupo " + grupo.getGrupo());
        
        // Opción Excel
        optionExcel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            exportarAsistencias(grupo, new AsistenciaExcelAdapter());
        });
        
        // Opción PDF
        optionPDF.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            exportarAsistencias(grupo, new AsistenciaPDFAdapter());
        });
        
        // Mostrar bottom sheet
        bottomSheetDialog.show();
    }

    private void exportarAsistencias(Grupo grupo, DataExportAdapter exportAdapter) {
        try {
            // Exportar usando Adapter Pattern
            ExportResult resultado = exportarCU.exportar(grupo.getId(), exportAdapter);
            
            if (resultado.isSuccess()) {
                // Guardar archivo en Downloads usando MediaStore
                boolean guardado = guardarArchivoEnDownloads(
                    resultado.getDatos(),
                    resultado.getNombreArchivo(),
                    resultado.getExtension(),
                    resultado.getTipoMime()
                );
                
                if (guardado) {
                    String mensaje = getString(R.string.exportacion_exito) + "\n" +
                                   "Formato: " + resultado.getFormato() + "\n" +
                                   "Registros: " + resultado.getCantidadRegistros() + "\n" +
                                   "Archivo guardado en Descargas";
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, 
                        "Error al guardar el archivo",
                        Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, 
                    getString(R.string.exportacion_error) + ": " + resultado.getMensajeError(),
                    Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("DocenteActivity", "Error al exportar: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Guarda un archivo en la carpeta Downloads usando MediaStore (compatible con Android 10+)
     * No requiere permisos especiales para Android 10+ (API 29+)
     * 
     * @param datos Contenido del archivo en bytes
     * @param nombreBase Nombre base del archivo (sin extensión)
     * @param extension Extensión del archivo (ej: "xlsx", "pdf")
     * @param tipoMime Tipo MIME del archivo (ej: "application/vnd.ms-excel", "application/pdf")
     * @return true si se guardó correctamente, false en caso contrario
     */
    private boolean guardarArchivoEnDownloads(byte[] datos, String nombreBase, String extension, String tipoMime) {
        try {
            ContentResolver resolver = getContentResolver();
            
            // Generar nombre único con timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String timestamp = sdf.format(new Date());
            String nombreArchivo = nombreBase + "_" + timestamp + "." + extension;
            
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, nombreArchivo);
            contentValues.put(MediaStore.Downloads.MIME_TYPE, tipoMime);
            
            // Para Android 10+ (API 29+), usar MediaStore directamente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                
                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                
                if (uri != null) {
                    try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                        if (outputStream != null) {
                            outputStream.write(datos);
                            outputStream.flush();
                            Log.d("DocenteActivity", "Archivo guardado: " + nombreArchivo);
                            return true;
                        }
                    }
                }
            } else {
                // Para versiones anteriores a Android 10 (no debería ejecutarse con minSdk 33)
                // Pero por compatibilidad:
                Toast.makeText(this, 
                    "Esta versión de Android no es compatible",
                    Toast.LENGTH_SHORT).show();
                return false;
            }
            
        } catch (Exception e) {
            Log.e("DocenteActivity", "Error al guardar archivo: " + e.getMessage(), e);
            return false;
        }
        
        return false;
    }
}

