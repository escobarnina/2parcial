package com.arquitectura.asistente.presentacion;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
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
    private static final Integer DOCENTE_ID = 63; // María Fernández - 4 materias (Programación I, BD I, BD II, Ing. Software)

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
                Uri uriArchivo = guardarArchivoEnDownloads(
                    resultado.getDatos(),
                    resultado.getNombreArchivo(),
                    resultado.getExtension(),
                    resultado.getTipoMime()
                );
                
                if (uriArchivo != null) {
                    // Extraer solo el nombre del archivo del URI
                    String nombreArchivo = obtenerNombreArchivoDeUri(uriArchivo);
                    
                    // Mostrar diálogo con información completa del archivo exportado
                    mostrarDialogoExportacionExitosa(resultado, nombreArchivo, uriArchivo);
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
     * @return URI del archivo guardado, o null si hubo error
     */
    private Uri guardarArchivoEnDownloads(byte[] datos, String nombreBase, String extension, String tipoMime) {
        try {
            ContentResolver resolver = getContentResolver();
            
            // Generar nombre único con fecha y hora legible
            // Formato: nombre_base_2025-01-27_14-30-45.extensión
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
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
                            
                            Log.d("DocenteActivity", "Archivo guardado: " + nombreArchivo + " (URI: " + uri.toString() + ")");
                            return uri;
                        }
                    }
                }
            } else {
                // Para versiones anteriores a Android 10 (no debería ejecutarse con minSdk 33)
                // Pero por compatibilidad:
                Toast.makeText(this, 
                    "Esta versión de Android no es compatible",
                    Toast.LENGTH_SHORT).show();
                return null;
            }
            
        } catch (Exception e) {
            Log.e("DocenteActivity", "Error al guardar archivo: " + e.getMessage(), e);
            return null;
        }
        
        return null;
    }
    
    /**
     * Obtiene el nombre del archivo desde su URI usando MediaStore
     * 
     * @param uri URI del archivo
     * @return Nombre del archivo o "archivo" si no se puede obtener
     */
    private String obtenerNombreArchivoDeUri(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            String[] projection = {MediaStore.Downloads.DISPLAY_NAME};
            
            android.database.Cursor cursor = resolver.query(uri, projection, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME);
                String nombreArchivo = cursor.getString(nameIndex);
                cursor.close();
                return nombreArchivo;
            }
            
            if (cursor != null) {
                cursor.close();
            }
            
        } catch (Exception e) {
            Log.e("DocenteActivity", "Error al obtener nombre del archivo: " + e.getMessage(), e);
        }
        
        return "archivo";
    }
    
    /**
     * Muestra un diálogo con la información completa del archivo exportado
     */
    private void mostrarDialogoExportacionExitosa(ExportResult resultado, String nombreArchivo, Uri uriArchivo) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exportacion_exitosa);
        dialog.setCancelable(true);
        
        // Configurar ventana del diálogo
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setDimAmount(0.6f);
            window.setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
        
        // Obtener vistas
        TextView tvFormato = dialog.findViewById(R.id.tvFormato);
        TextView tvRegistros = dialog.findViewById(R.id.tvRegistros);
        TextView tvNombreArchivo = dialog.findViewById(R.id.tvNombreArchivo);
        MaterialButton btnAbrir = dialog.findViewById(R.id.btnAbrir);
        
        // Configurar información
        tvFormato.setText("Formato: " + resultado.getFormato());
        tvRegistros.setText("Registros exportados: " + resultado.getCantidadRegistros());
        tvNombreArchivo.setText(nombreArchivo);
        
        // Botón abrir explorador de archivos
        btnAbrir.setOnClickListener(v -> {
            dialog.dismiss();
            abrirExploradorDeArchivos();
        });
        
        // Mostrar diálogo
        dialog.show();
    }
    
    /**
     * Abre el explorador de archivos nativo de Android en la carpeta de Descargas
     * donde se guardó el archivo exportado
     */
    private void abrirExploradorDeArchivos() {
        try {
            // Intentar abrir el explorador de archivos en la carpeta de Descargas
            // Usar el URI de DocumentsContract para la carpeta de Descargas
            Intent intent = new Intent(Intent.ACTION_VIEW);
            
            // Construir URI para la carpeta de Descargas
            // Formato: content://com.android.externalstorage.documents/document/primary:Download
            Uri downloadsUri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3ADownload");
            
            intent.setDataAndType(downloadsUri, "resource/folder");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            // Verificar si hay alguna aplicación que pueda manejar este intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                return;
            }
            
            // Método alternativo: Intent con ACTION_GET_CONTENT para explorador genérico
            Intent alternateIntent = new Intent(Intent.ACTION_GET_CONTENT);
            alternateIntent.setType("*/*");
            alternateIntent.addCategory(Intent.CATEGORY_OPENABLE);
            
            if (alternateIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(alternateIntent);
                return;
            }
            
            // Si no se puede abrir el explorador, informar al usuario
            Toast.makeText(this, 
                "No se encontró un explorador de archivos disponible. El archivo se guardó en la carpeta Descargas",
                Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Log.e("DocenteActivity", "Error al abrir explorador de archivos: " + e.getMessage(), e);
            Toast.makeText(this, 
                "No se pudo abrir el explorador. El archivo se guardó en la carpeta Descargas",
                Toast.LENGTH_LONG).show();
        }
    }
}

