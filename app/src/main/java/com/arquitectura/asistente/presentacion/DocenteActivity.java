package com.arquitectura.asistente.presentacion;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.arquitectura.asistente.R;
import com.arquitectura.asistente.datos.Grupo;
import com.arquitectura.asistente.datos.Horario;
import com.arquitectura.asistente.datos.adapter.AsistenciaExcelAdapter;
import com.arquitectura.asistente.datos.adapter.AsistenciaPDFAdapter;
import com.arquitectura.asistente.datos.adapter.DataExportAdapter;
import com.arquitectura.asistente.datos.database.DatabaseBaseDAO;
import com.arquitectura.asistente.datos.adapter.ExportResult;
import com.arquitectura.asistente.negocio.ExportarAsistenciaCU;
import com.arquitectura.asistente.presentacion.widget.GrupoAdapter;

import java.util.ArrayList;
import java.util.List;

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
        List<Grupo> grupos = obtenerGruposPorDocente(DOCENTE_ID);
        
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
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exportar);
        dialog.setCancelable(true);
        
        // Configurar ventana del diálogo con fondo blanco sólido
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
        TextView tvMateriaInfo = dialog.findViewById(R.id.tvMateriaInfo);
        MaterialButton btnExportarExcel = dialog.findViewById(R.id.btnExportarExcel);
        MaterialButton btnExportarPDF = dialog.findViewById(R.id.btnExportarPDF);
        MaterialButton btnCancelar = dialog.findViewById(R.id.btnCancelar);
        
        // Configurar información
        tvMateriaInfo.setText(grupo.getMateriaNombre() + " - Grupo " + grupo.getGrupo());
        
        // Botón Excel
        btnExportarExcel.setOnClickListener(v -> {
            dialog.dismiss();
            exportarAsistencias(grupo, new AsistenciaExcelAdapter());
        });
        
        // Botón PDF
        btnExportarPDF.setOnClickListener(v -> {
            dialog.dismiss();
            exportarAsistencias(grupo, new AsistenciaPDFAdapter());
        });
        
        // Botón cancelar
        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        
        // Mostrar diálogo
        dialog.show();
    }

    private void exportarAsistencias(Grupo grupo, DataExportAdapter exportAdapter) {
        try {
            // Exportar usando Adapter Pattern
            ExportResult resultado = exportarCU.exportar(grupo.getId(), exportAdapter);
            
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

