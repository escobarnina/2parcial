package com.arquitectura.asistente.presentacion;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arquitectura.asistente.R;
import com.arquitectura.asistente.datos.Grupo;
import com.arquitectura.asistente.negocio.AsistenciaCU;
import com.arquitectura.asistente.presentacion.widget.GrupoAdapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * EstudianteActivity - Capa de Presentación
 * Permite a los estudiantes ver sus grupos inscritos y marcar su asistencia
 * Sigue el mismo enfoque de acceso a datos que Asistencia.java
 * Al presionar una materia se marca la asistencia directamente
 */
public class EstudianteActivity extends AppCompatActivity implements GrupoAdapter.OnGrupoClickListener {
    private RecyclerView recyclerViewGrupos;
    private TextView tvHoraSistema;
    private AsistenciaCU asistenciaCU;
    private GrupoAdapter grupoAdapter;
    
    private Handler handler;
    private Runnable updateTimeRunnable;
    
    // ID del estudiante (en una app real, esto vendría del login)
    // Estudiante 1 (Ana García) tiene 9 materias inscritas - el que más tiene
    private static final Integer ESTUDIANTE_ID = 1; // Ana García - 9 materias inscritas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estudiante);
        
        // Inicializar casos de uso y acceso a datos
        this.asistenciaCU = new AsistenciaCU(this);
        Grupo.inicializar(this);
        
        inicializarVistas();
        cargarGruposInscritos();
        iniciarActualizacionHora();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerActualizacionHora();
    }

    private void inicializarVistas() {
        recyclerViewGrupos = findViewById(R.id.recyclerViewGrupos);
        tvHoraSistema = findViewById(R.id.tvHoraSistema);
        
        // Configurar RecyclerView
        recyclerViewGrupos.setLayoutManager(new LinearLayoutManager(this));
        grupoAdapter = new GrupoAdapter(this);
        recyclerViewGrupos.setAdapter(grupoAdapter);
        
        // Inicializar handler para actualizar hora
        handler = new Handler(Looper.getMainLooper());
    }

    private void cargarGruposInscritos() {
        // Usar el método estático de Grupo siguiendo el patrón de Asistencia.java
        List<Grupo> grupos = Grupo.obtenerPorEstudiante(ESTUDIANTE_ID);
        
        if (grupos.isEmpty()) {
            Toast.makeText(this, getString(R.string.estudiante_sin_materias), Toast.LENGTH_LONG).show();
            return;
        }
        
        grupoAdapter.actualizarGrupos(grupos);
    }

    private void iniciarActualizacionHora() {
        actualizarHora();
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                actualizarHora();
                handler.postDelayed(this, 1000); // Actualizar cada segundo
            }
        };
        handler.postDelayed(updateTimeRunnable, 1000);
    }

    private void detenerActualizacionHora() {
        if (handler != null && updateTimeRunnable != null) {
            handler.removeCallbacks(updateTimeRunnable);
        }
    }

    private void actualizarHora() {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        tvHoraSistema.setText("Hora: " + hora + " | Fecha: " + fecha);
    }

    @Override
    public void onGrupoClick(Grupo grupo) {
        // Marcar asistencia directamente al presionar la tarjeta
        marcarAsistencia(grupo);
    }

    private void marcarAsistencia(Grupo grupo) {
        try {
            String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            
            // Marcar asistencia usando el caso de uso
            boolean exito = asistenciaCU.marcarAsistencia(
                ESTUDIANTE_ID,
                grupo.getId(),
                fecha,
                hora
            );
            
            if (exito) {
                Toast.makeText(this, 
                    "Asistencia marcada: " + grupo.getMateriaNombre() + " - Grupo " + grupo.getGrupo() + 
                    "\nHora: " + hora, 
                    Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.asistencia_error), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

