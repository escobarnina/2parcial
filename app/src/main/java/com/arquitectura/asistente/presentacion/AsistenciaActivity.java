package com.arquitectura.asistente.presentacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.arquitectura.asistente.R;
import com.arquitectura.asistente.datos.Asistencia;
import com.arquitectura.asistente.datos.adapter.AsistenciaExcelAdapter;
import com.arquitectura.asistente.datos.adapter.AsistenciaPDFAdapter;
import com.arquitectura.asistente.datos.adapter.DataExportAdapter;
import com.arquitectura.asistente.negocio.ExportResult;
import com.arquitectura.asistente.negocio.AsistenciaCU;
import com.arquitectura.asistente.negocio.ExportarAsistenciaCU;
import com.arquitectura.asistente.presentacion.widget.AsistenciaAdapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Activity de Asistencia - Capa de Presentación
 * Demuestra el uso de Strategy Pattern y Adapter Pattern
 * Versión Android del AsistenciaForm original
 */
public class AsistenciaActivity extends AppCompatActivity {
    private TextInputEditText txtAlumnoId;
    private TextInputEditText txtGrupoId;
    private TextInputEditText txtHora;
    private MaterialButton btnMarcarAsistencia;
    private MaterialButton btnExportarExcel;
    private MaterialButton btnExportarPDF;
    private RecyclerView recyclerViewAsistencias;
    
    private AsistenciaCU asistenciaCU;
    private ExportarAsistenciaCU exportarCU;
    private AsistenciaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);
        
        // Inicializar casos de uso con contexto
        this.asistenciaCU = new AsistenciaCU(this);
        this.exportarCU = new ExportarAsistenciaCU(this);
        
        inicializarVistas();
        configurarEventos();
        
        // Establecer hora actual por defecto
        String horaActual = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        txtHora.setText(horaActual);
    }

    private void inicializarVistas() {
        txtAlumnoId = findViewById(R.id.txtAlumnoId);
        txtGrupoId = findViewById(R.id.txtGrupoId);
        txtHora = findViewById(R.id.txtHora);
        btnMarcarAsistencia = findViewById(R.id.btnMarcarAsistencia);
        btnExportarExcel = findViewById(R.id.btnExportarExcel);
        btnExportarPDF = findViewById(R.id.btnExportarPDF);
        recyclerViewAsistencias = findViewById(R.id.recyclerViewAsistencias);
        
        // Configurar RecyclerView
        recyclerViewAsistencias.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AsistenciaAdapter();
        recyclerViewAsistencias.setAdapter(adapter);
    }

    private void configurarEventos() {
        btnMarcarAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marcarAsistencia();
            }
        });

        btnExportarExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportarAsistencias(new AsistenciaExcelAdapter());
            }
        });

        btnExportarPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportarAsistencias(new AsistenciaPDFAdapter());
            }
        });
    }

    private void marcarAsistencia() {
        try {
            String alumnoIdStr = txtAlumnoId.getText().toString().trim();
            String grupoIdStr = txtGrupoId.getText().toString().trim();
            String hora = txtHora.getText().toString().trim();
            
            if (alumnoIdStr.isEmpty() || grupoIdStr.isEmpty() || hora.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Integer alumnoId = Integer.parseInt(alumnoIdStr);
            Integer grupoId = Integer.parseInt(grupoIdStr);
            String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Marcar asistencia (usa Strategy Pattern internamente)
            boolean exito = asistenciaCU.marcarAsistencia(alumnoId, grupoId, fecha, hora);

            if (exito) {
                Toast.makeText(this, "Asistencia marcada exitosamente!", Toast.LENGTH_SHORT).show();
                actualizarListaAsistencias(grupoId);
            } else {
                Toast.makeText(this, "Error al marcar asistencia. Verifique que el alumno esté inscrito.", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error: Los IDs deben ser números válidos", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void exportarAsistencias(DataExportAdapter exportAdapter) {
        try {
            String grupoIdStr = txtGrupoId.getText().toString().trim();
            
            if (grupoIdStr.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese un ID de grupo", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Integer grupoId = Integer.parseInt(grupoIdStr);

            // Exportar usando Adapter Pattern
            ExportResult resultado = exportarCU.exportar(grupoId, exportAdapter);

            if (resultado.isSuccess()) {
                // TODO: Implementar guardado de archivo en Android
                // Por ahora solo mostramos un mensaje
                Toast.makeText(this,
                    "Archivo exportado exitosamente!\n" +
                    "Formato: " + resultado.getFormato() + "\n" +
                    "Registros: " + resultado.getCantidadRegistros(),
                    Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                    "Error al exportar: " + resultado.getMensajeError(),
                    Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error: El ID de grupo debe ser un número válido", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarListaAsistencias(Integer grupoId) {
        List<Asistencia> asistencias = asistenciaCU.obtenerAsistenciasPorGrupo(grupoId);
        adapter.actualizarAsistencias(asistencias);
    }
}

