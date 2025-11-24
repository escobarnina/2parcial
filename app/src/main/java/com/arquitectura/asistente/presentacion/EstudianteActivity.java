package com.arquitectura.asistente.presentacion;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.arquitectura.asistente.R;
import com.arquitectura.asistente.datos.Grupo;
import com.arquitectura.asistente.datos.Horario;
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
        Horario.inicializar(this);
        
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
            
            // Verificar si hay horario para el día actual
            Horario horario = asistenciaCU.obtenerHorarioPorFecha(grupo.getId(), fecha);
            if (horario == null) {
                // No hay horario para este día, mostrar diálogo de advertencia
                mostrarDialogoSinHorario(grupo, fecha);
                return;
            }
            
            // Obtener horario del grupo para mostrar en el diálogo
            String horarioClase = horario.getHoraInicio() + " - " + horario.getHoraFin();
            
            // Marcar asistencia usando el caso de uso
            boolean exito = asistenciaCU.marcarAsistencia(
                ESTUDIANTE_ID,
                grupo.getId(),
                fecha,
                hora
            );
            
            if (exito) {
                // Obtener el estado calculado por la estrategia
                String estado = asistenciaCU.getUltimoEstadoCalculado();
                if (estado == null) {
                    estado = "PRESENTE"; // Fallback
                }
                
                mostrarDialogoAsistencia(grupo, hora, horarioClase, estado);
            } else {
                Toast.makeText(this, getString(R.string.asistencia_error), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Muestra un diálogo Material Design con el estado de la asistencia
     */
    private void mostrarDialogoAsistencia(Grupo grupo, String horaMarcada, String horarioClase, String estado) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_asistencia);
        dialog.setCancelable(true);
        
        // Configurar ventana del diálogo con fondo blanco sólido
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            // Agregar fondo semitransparente oscuro detrás del diálogo
            window.setDimAmount(0.6f);
            // Asegurar que el diálogo tenga el ancho correcto
            window.setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
        
        // Obtener vistas
        MaterialCardView cardIcono = dialog.findViewById(R.id.cardIconoEstado);
        ImageView ivIcono = dialog.findViewById(R.id.ivIconoEstado);
        TextView tvTitulo = dialog.findViewById(R.id.tvTituloEstado);
        TextView tvMateria = dialog.findViewById(R.id.tvMateriaInfo);
        TextView tvHoraMarcada = dialog.findViewById(R.id.tvHoraMarcada);
        TextView tvHorarioClase = dialog.findViewById(R.id.tvHorarioClase);
        MaterialButton btnAceptar = dialog.findViewById(R.id.btnAceptar);
        
        // Configurar según el estado
        switch (estado) {
            case "PRESENTE":
                cardIcono.setCardBackgroundColor(Color.parseColor("#E8F5E9")); // Verde claro
                ivIcono.setImageResource(R.drawable.ic_check_circle_24);
                ivIcono.setColorFilter(Color.parseColor("#4CAF50")); // Verde
                tvTitulo.setText("Asistencia Marcada");
                tvTitulo.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "RETRASO":
                cardIcono.setCardBackgroundColor(Color.parseColor("#FFF3E0")); // Naranja claro
                ivIcono.setImageResource(R.drawable.ic_access_time_24);
                ivIcono.setColorFilter(Color.parseColor("#FF9800")); // Naranja
                tvTitulo.setText("Asistencia con Retraso");
                tvTitulo.setTextColor(Color.parseColor("#FF9800"));
                break;
            case "FALTA":
                cardIcono.setCardBackgroundColor(Color.parseColor("#FFEBEE")); // Rojo claro
                ivIcono.setImageResource(R.drawable.ic_cancel_24);
                ivIcono.setColorFilter(Color.parseColor("#F44336")); // Rojo
                tvTitulo.setText("Asistencia Marcada como Falta");
                tvTitulo.setTextColor(Color.parseColor("#F44336"));
                break;
        }
        
        // Configurar información
        tvMateria.setText(grupo.getMateriaNombre() + " - Grupo " + grupo.getGrupo());
        tvHoraMarcada.setText(horaMarcada);
        tvHorarioClase.setText(horarioClase);
        
        // Botón aceptar
        btnAceptar.setOnClickListener(v -> dialog.dismiss());
        
        // Mostrar diálogo
        dialog.show();
    }

    /**
     * Muestra un diálogo cuando no hay horario disponible para el día actual
     */
    private void mostrarDialogoSinHorario(Grupo grupo, String fecha) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sin_horario);
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
        TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
        TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
        TextView tvMateria = dialog.findViewById(R.id.tvMateriaInfo);
        TextView tvDia = dialog.findViewById(R.id.tvDiaInfo);
        MaterialButton btnAceptar = dialog.findViewById(R.id.btnAceptar);
        
        // Obtener día de la semana en español
        String diaSemana = obtenerDiaSemanaEspañol(fecha);
        
        // Configurar información
        tvTitulo.setText(getString(R.string.dialog_sin_horario_titulo));
        tvMensaje.setText(getString(R.string.dialog_sin_horario_mensaje));
        tvMateria.setText(grupo.getMateriaNombre() + " - Grupo " + grupo.getGrupo());
        tvDia.setText("Día: " + diaSemana + " (" + fecha + ")");
        
        // Botón aceptar
        btnAceptar.setOnClickListener(v -> dialog.dismiss());
        
        // Mostrar diálogo
        dialog.show();
    }

    /**
     * Obtiene el día de la semana en español desde una fecha
     */
    private String obtenerDiaSemanaEspañol(String fecha) {
        try {
            LocalDate localDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            java.time.DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            
            switch (dayOfWeek) {
                case MONDAY:
                    return "Lunes";
                case TUESDAY:
                    return "Martes";
                case WEDNESDAY:
                    return "Miércoles";
                case THURSDAY:
                    return "Jueves";
                case FRIDAY:
                    return "Viernes";
                case SATURDAY:
                    return "Sábado";
                case SUNDAY:
                    return "Domingo";
                default:
                    return "Desconocido";
            }
        } catch (Exception e) {
            return "Desconocido";
        }
    }
}

