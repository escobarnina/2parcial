package com.arquitectura.asistente.negocio;

import com.arquitectura.asistente.datos.Asistencia;
import com.arquitectura.asistente.datos.Grupo;
import com.arquitectura.asistente.datos.Horario;
import com.arquitectura.asistente.negocio.strategy.IEstrategiaAsistencia;
import com.arquitectura.asistente.negocio.strategy.EstrategiaPresente;
import com.arquitectura.asistente.negocio.strategy.EstrategiaRetraso;
import com.arquitectura.asistente.negocio.strategy.EstrategiaFalta;
import android.content.Context;
import android.util.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Caso de uso para gestionar asistencias
 * Capa de Negocio - Strategy Pattern - Context (Contexto)
 * Este caso de uso actúa como contexto que usa diferentes estrategias
 * para calcular el estado de asistencia (PRESENTE, RETRASO, FALTA)
 * 
 * RELACIONES EXPLÍCITAS (Capa de Negocio):
 * - AsistenciaCU -> Asistencia (instancia, capa de datos)
 * - AsistenciaCU -> Grupo (instancia, capa de datos)
 * - AsistenciaCU -> Horario (instancia, capa de datos)
 * 
 * NOTA: Las relaciones con DatabaseBaseDAO y DatabaseHelper son responsabilidad
 * de la capa de datos (Asistencia, Grupo, Horario), no de la capa de negocio.
 * La capa de negocio solo trabaja con las clases de datos, que internamente
 * gestionan su acceso a la base de datos.
 */
public class AsistenciaCU {
    private static final String TAG = "AsistenciaCU";
    private IEstrategiaAsistencia estrategia;
    private String ultimoEstadoCalculado; // Para poder obtener el estado después de marcar
    
    // Instancias explícitas de las clases de datos para hacer visibles las relaciones
    private Asistencia asistenciaData;
    private Grupo grupoData;
    private Horario horarioData;

    public AsistenciaCU(Context context) {
        // Crear instancias explícitas de las clases de datos
        // Estas instancias hacen visible la relación en diagramas de clases
        // y documentan claramente la dependencia de AsistenciaCU con estas clases
        this.asistenciaData = new Asistencia(context);
        this.grupoData = new Grupo(context);
        this.horarioData = new Horario(context);
        
        Log.d(TAG, "AsistenciaCU inicializado con instancias explícitas de clases de datos");
    }
    
    /**
     * Obtiene la instancia de Asistencia (para hacer explícita la relación)
     */
    public Asistencia getAsistenciaData() {
        return asistenciaData;
    }
    
    /**
     * Obtiene la instancia de Grupo (para hacer explícita la relación)
     */
    public Grupo getGrupoData() {
        return grupoData;
    }
    
    /**
     * Obtiene la instancia de Horario (para hacer explícita la relación)
     */
    public Horario getHorarioData() {
        return horarioData;
    }

    /**
     * Establece la estrategia para calcular el estado de asistencia
     * Patrón Strategy: Permite cambiar el algoritmo en tiempo de ejecución
     */
    public void setEstrategia(IEstrategiaAsistencia estrategia) {
        Log.d(TAG, "Cambiando estrategia a: " + estrategia.getClass().getSimpleName());
        this.estrategia = estrategia;
    }

    /**
     * Marca asistencia de un alumno en un grupo
     * Usa Strategy Pattern para calcular el estado
     */
    public boolean marcarAsistencia(Integer alumnoId, Integer grupoId, String fecha, String horaMarcado) {
        try {
            // Limpiar el estado anterior para evitar confusiones
            this.ultimoEstadoCalculado = null;
            
            // Validar que el alumno esté inscrito en el grupo
            boolean inscrito = asistenciaData.estaInscrito(alumnoId, grupoId);
            Log.d(TAG, "Validando inscripción - Alumno: " + alumnoId + ", Grupo: " + grupoId + ", Inscrito: " + inscrito);
            
            if (!inscrito) {
                Log.w(TAG, "El alumno " + alumnoId + " NO está inscrito en el grupo " + grupoId);
                return false;
            }
            
            Log.d(TAG, "Validación de inscripción exitosa - Alumno " + alumnoId + " está inscrito en grupo " + grupoId);

            // Obtener horarios del grupo para validar
            List<Horario> horarios = horarioData.obtenerHorariosGrupo(grupoId);
            if (horarios.isEmpty()) {
                Log.w(TAG, "El grupo " + grupoId + " no tiene horarios configurados");
                return false;
            }

            // Obtener el día de la semana de la fecha que se está marcando
            String diaSemana = obtenerDiaSemana(fecha);
            Log.d(TAG, "Fecha marcada: " + fecha + " - Día de la semana: " + diaSemana);

            // Buscar el horario que corresponde al día de la semana
            Horario horario = buscarHorarioPorDia(horarios, diaSemana);
            if (horario == null) {
                Log.w(TAG, "No hay horario configurado para el día " + diaSemana + " en el grupo " + grupoId);
                // No permitir marcar asistencia si no hay horario para ese día
                return false;
            }
            
            Log.d(TAG, "Horario encontrado para " + diaSemana + ": " + horario.getHoraInicio() + "-" + horario.getHoraFin());

            String horaInicio = horario.getHoraInicio();
            String horaFin = horario.getHoraFin();

            // Obtener tipo de estrategia del grupo
            String tipoEstrategia = grupoData.obtenerTipoEstrategiaGrupo(grupoId);
            Log.d(TAG, "Tipo de estrategia del grupo " + grupoId + ": " + tipoEstrategia);

            // Configurar estrategia automáticamente desde BD según el grupo actual
            // IMPORTANTE: Cada grupo puede tener una estrategia diferente, por lo que
            // debemos actualizar la estrategia cada vez que se marca asistencia
            switch (tipoEstrategia) {
                case "PRESENTE":
                    estrategia = new EstrategiaPresente();
                    break;
                case "FALTA":
                    estrategia = new EstrategiaFalta();
                    break;
                default:
                    estrategia = new EstrategiaRetraso();
            }
            Log.d(TAG, "Estrategia configurada para grupo " + grupoId + ": " + estrategia.getClass().getSimpleName());

            // Delegar el cálculo del estado a la estrategia (Strategy Pattern)
            String estado = estrategia.calcularEstado(horaMarcado, horaInicio, horaFin);
            Log.d(TAG, "Estado calculado por la estrategia: " + estado + " (Inicio: " + horaInicio + ", Fin: " + horaFin + ")");

            // Si el estado es null, significa que está fuera del horario y no se puede marcar asistencia
            if (estado == null) {
                Log.w(TAG, "No se puede marcar asistencia: fuera del horario de clase");
                this.ultimoEstadoCalculado = null;
                return false;
            }

            // Crear y guardar la asistencia
            Asistencia asistencia = new Asistencia();
            asistencia.setAlumnoId(alumnoId);
            asistencia.setGrupoId(grupoId);
            asistencia.setFecha(fecha);
            asistencia.setHoraMarcada(horaMarcado);
            asistencia.setEstado(estado);

            asistenciaData.guardar(asistencia);
            Log.d(TAG, "Asistencia marcada exitosamente");
            
            // Guardar el estado en la instancia para poder retornarlo
            this.ultimoEstadoCalculado = estado;
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error al marcar asistencia: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Obtiene todas las asistencias de un grupo
     */
    public List<Asistencia> obtenerAsistenciasPorGrupo(Integer grupoId) {
        return asistenciaData.obtenerPorGrupo(grupoId);
    }

    /**
     * Obtiene todas las asistencias de un alumno
     */
    public List<Asistencia> obtenerAsistenciasPorAlumno(Integer alumnoId) {
        return asistenciaData.obtenerPorAlumno(alumnoId);
    }

    /**
     * Obtiene los grupos en los que está inscrito un estudiante
     */
    public List<Grupo> obtenerGruposPorEstudiante(Integer estudianteId) {
        return grupoData.obtenerPorEstudiante(estudianteId);
    }

    /**
     * Obtiene los horarios configurados para un grupo
     */
    public List<Horario> obtenerHorariosDeGrupo(Integer grupoId) {
        return horarioData.obtenerHorariosGrupo(grupoId);
    }

    /**
     * Obtiene el último estado calculado después de marcar asistencia
     * @return Estado calculado: "PRESENTE", "RETRASO" o "FALTA"
     */
    public String getUltimoEstadoCalculado() {
        return ultimoEstadoCalculado;
    }

    /**
     * Obtiene el horario del grupo para un día específico
     * @param grupoId ID del grupo
     * @param fecha Fecha en formato YYYY-MM-DD
     * @return Horario del día o null si no existe
     */
    public Horario obtenerHorarioPorFecha(Integer grupoId, String fecha) {
        List<Horario> horarios = horarioData.obtenerHorariosGrupo(grupoId);
        if (horarios.isEmpty()) {
            return null;
        }
        
        String diaSemana = obtenerDiaSemana(fecha);
        return buscarHorarioPorDia(horarios, diaSemana);
    }

    /**
     * Verifica si ya existe una asistencia marcada para un alumno, grupo y fecha
     * @param alumnoId ID del alumno
     * @param grupoId ID del grupo
     * @param fecha Fecha en formato YYYY-MM-DD
     * @return La asistencia existente o null si no existe
     */
    public Asistencia obtenerAsistenciaExistente(Integer alumnoId, Integer grupoId, String fecha) {
        return asistenciaData.obtenerAsistenciaExistente(alumnoId, grupoId, fecha);
    }

    /**
     * Obtiene el día de la semana en español desde una fecha en formato YYYY-MM-DD
     * @param fecha Fecha en formato YYYY-MM-DD
     * @return Día de la semana en español: "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
     */
    private String obtenerDiaSemana(String fecha) {
        try {
            LocalDate localDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            
            // Convertir DayOfWeek a español
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
                    return "Lunes"; // Fallback
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener día de la semana de la fecha: " + fecha, e);
            return "Lunes"; // Fallback
        }
    }

    /**
     * Busca un horario en la lista que corresponda al día de la semana especificado
     * @param horarios Lista de horarios del grupo
     * @param diaSemana Día de la semana en español: "Lunes", "Martes", etc.
     * @return Horario que corresponde al día, o null si no se encuentra
     */
    private Horario buscarHorarioPorDia(List<Horario> horarios, String diaSemana) {
        for (Horario horario : horarios) {
            if (horario.getDia().equalsIgnoreCase(diaSemana)) {
                return horario;
            }
        }
        return null;
    }
}

