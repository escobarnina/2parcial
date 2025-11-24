package com.arquitectura.asistente.presentacion;

import android.content.Context;
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
import com.arquitectura.asistente.datos.Asistencia;
import com.arquitectura.asistente.datos.Grupo;
import com.arquitectura.asistente.datos.database.DatabaseBaseDAO;
import com.arquitectura.asistente.negocio.AsistenciaCU;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * EstudianteActivity - Capa de Presentación
 * Permite a los estudiantes marcar su asistencia en las materias inscritas
 */
public class EstudianteActivity extends AppCompatActivity {
    private Spinner spinnerMaterias;
    private MaterialButton btnMarcarAsistencia;
    private AsistenciaCU asistenciaCU;
    
    // ID del estudiante (en una app real, esto vendría del login)
    // Estudiante 1 (Ana García) tiene 9 materias inscritas - el que más tiene
    private static final Integer ESTUDIANTE_ID = 1; // Ana García - 9 materias inscritas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estudiante);
        
        // Inicializar casos de uso
        this.asistenciaCU = new AsistenciaCU(this);
        Grupo.inicializar(this);
        
        inicializarVistas();
        cargarMateriasInscritas();
        configurarEventos();
    }

    private void inicializarVistas() {
        spinnerMaterias = findViewById(R.id.spinnerMaterias);
        btnMarcarAsistencia = findViewById(R.id.btnMarcarAsistencia);
    }

    private void cargarMateriasInscritas() {
        List<Grupo> grupos = obtenerGruposPorEstudiante(ESTUDIANTE_ID);
        
        if (grupos.isEmpty()) {
            Toast.makeText(this, getString(R.string.estudiante_sin_materias), Toast.LENGTH_LONG).show();
            btnMarcarAsistencia.setEnabled(false);
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
        btnMarcarAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marcarAsistencia();
            }
        });
    }

    private void marcarAsistencia() {
        try {
            int selectedPosition = spinnerMaterias.getSelectedItemPosition();
            if (selectedPosition == -1) {
                Toast.makeText(this, "Por favor selecciona una materia", Toast.LENGTH_SHORT).show();
                return;
            }
            
            List<Grupo> grupos = obtenerGruposPorEstudiante(ESTUDIANTE_ID);
            Grupo grupoSeleccionado = grupos.get(selectedPosition);
            
            String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            
            // Marcar asistencia usando el caso de uso
            boolean exito = asistenciaCU.marcarAsistencia(
                ESTUDIANTE_ID,
                grupoSeleccionado.getId(),
                fecha,
                hora
            );
            
            if (exito) {
                Toast.makeText(this, getString(R.string.asistencia_marcada_exito), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.asistencia_error), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Obtiene los grupos en los que está inscrito un estudiante
     * Consulta personalizada que une boletas con grupos
     */
    private List<Grupo> obtenerGruposPorEstudiante(Integer estudianteId) {
        List<Grupo> grupos = new ArrayList<>();
        DatabaseBaseDAO baseDAO = DatabaseBaseDAO.getInstance(this);
        SQLiteDatabase db = baseDAO.getReadableDatabase();
        
        String sql = "SELECT g.id, g.grupo, g.materia_id, m.nombre as materia_nombre, " +
                     "g.docente_id, u.nombres || ' ' || u.apellidos as docente_nombre, " +
                     "g.semestre, g.gestion, g.capacidad, g.tolerancia_minutos, g.tipo_estrategia " +
                     "FROM grupos g " +
                     "INNER JOIN boletas b ON g.id = b.grupo_id " +
                     "INNER JOIN materias m ON g.materia_id = m.id " +
                     "INNER JOIN usuarios u ON g.docente_id = u.id " +
                     "WHERE b.alumno_id = ? " +
                     "ORDER BY m.nombre, g.grupo";
        
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(estudianteId)})) {
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

