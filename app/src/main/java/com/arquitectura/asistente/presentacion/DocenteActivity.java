package com.arquitectura.asistente.presentacion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.arquitectura.asistente.R;
import com.arquitectura.asistente.datos.Grupo;
import com.arquitectura.asistente.datos.adapter.AsistenciaExcelAdapter;
import com.arquitectura.asistente.datos.adapter.AsistenciaPDFAdapter;
import com.arquitectura.asistente.datos.adapter.DataExportAdapter;
import com.arquitectura.asistente.datos.database.DatabaseBaseDAO;
import com.arquitectura.asistente.negocio.ExportResult;
import com.arquitectura.asistente.negocio.ExportarAsistenciaCU;

import java.util.ArrayList;
import java.util.List;

/**
 * DocenteActivity - Capa de Presentación
 * Permite a los docentes exportar reportes de asistencia por materia
 */
public class DocenteActivity extends AppCompatActivity {
    private Spinner spinnerMaterias;
    private MaterialButton btnExportarExcel;
    private MaterialButton btnExportarPDF;
    private ExportarAsistenciaCU exportarCU;
    
    // ID del docente (en una app real, esto vendría del login)
    private static final Integer DOCENTE_ID = 4; // Por defecto docente1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docente);
        
        // Inicializar casos de uso
        this.exportarCU = new ExportarAsistenciaCU(this);
        Grupo.inicializar(this);
        
        inicializarVistas();
        cargarMateriasAsignadas();
        configurarEventos();
    }

    private void inicializarVistas() {
        spinnerMaterias = findViewById(R.id.spinnerMaterias);
        btnExportarExcel = findViewById(R.id.btnExportarExcel);
        btnExportarPDF = findViewById(R.id.btnExportarPDF);
    }

    private void cargarMateriasAsignadas() {
        List<Grupo> grupos = obtenerGruposPorDocente(DOCENTE_ID);
        
        if (grupos.isEmpty()) {
            Toast.makeText(this, getString(R.string.docente_sin_materias), Toast.LENGTH_LONG).show();
            btnExportarExcel.setEnabled(false);
            btnExportarPDF.setEnabled(false);
            return;
        }
        
        // Crear lista de strings para el spinner
        List<String> materiasList = new ArrayList<>();
        for (Grupo grupo : grupos) {
            materiasList.add(grupo.getMateriaNombre() + " - Grupo " + grupo.getGrupo());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            materiasList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterias.setAdapter(adapter);
    }

    private void configurarEventos() {
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

    private void exportarAsistencias(DataExportAdapter exportAdapter) {
        try {
            int selectedPosition = spinnerMaterias.getSelectedItemPosition();
            if (selectedPosition == -1) {
                Toast.makeText(this, "Por favor selecciona una materia", Toast.LENGTH_SHORT).show();
                return;
            }
            
            List<Grupo> grupos = obtenerGruposPorDocente(DOCENTE_ID);
            Grupo grupoSeleccionado = grupos.get(selectedPosition);
            
            // Exportar usando Adapter Pattern
            ExportResult resultado = exportarCU.exportar(grupoSeleccionado.getId(), exportAdapter);
            
            if (resultado.isSuccess()) {
                String mensaje = getString(R.string.exportacion_exito) + "\n" +
                               "Formato: " + resultado.getFormato() + "\n" +
                               "Registros: " + resultado.getCantidadRegistros();
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, 
                    getString(R.string.exportacion_error) + ": " + resultado.getMensajeError(),
                    Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Obtiene los grupos asignados a un docente
     * Consulta personalizada que filtra grupos por docente_id
     */
    private List<Grupo> obtenerGruposPorDocente(Integer docenteId) {
        List<Grupo> grupos = new ArrayList<>();
        DatabaseBaseDAO baseDAO = DatabaseBaseDAO.getInstance(this);
        SQLiteDatabase db = baseDAO.getReadableDatabase();
        
        String sql = "SELECT g.id, g.grupo, g.materia_id, m.nombre as materia_nombre, " +
                     "g.docente_id, u.nombres || ' ' || u.apellidos as docente_nombre, " +
                     "g.semestre, g.gestion, g.capacidad, g.tolerancia_minutos, g.tipo_estrategia " +
                     "FROM grupos g " +
                     "INNER JOIN materias m ON g.materia_id = m.id " +
                     "INNER JOIN usuarios u ON g.docente_id = u.id " +
                     "WHERE g.docente_id = ? " +
                     "ORDER BY m.nombre, g.grupo";
        
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(docenteId)})) {
            while (cursor.moveToNext()) {
                Grupo grupo = new Grupo();
                grupo.setId(cursor.getInt(0));
                grupo.setGrupo(cursor.getString(1));
                grupo.setMateriaId(cursor.getInt(2));
                grupo.setMateriaNombre(cursor.getString(3));
                grupo.setDocenteId(cursor.getInt(4));
                grupo.setDocenteNombre(cursor.getString(5));
                grupo.setSemestre(cursor.getInt(6));
                grupo.setGestion(cursor.getInt(7));
                grupo.setCapacidad(cursor.getInt(8));
                grupo.setToleranciaMinutos(cursor.getInt(9));
                grupo.setTipoEstrategia(cursor.getString(10));
                grupos.add(grupo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        
        return grupos;
    }
}

