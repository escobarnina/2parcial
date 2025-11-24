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

import java.util.List;

/**
 * Caso de uso para gestionar asistencias
 * Capa de Negocio - Strategy Pattern - Context (Contexto)
 * Este caso de uso actúa como contexto que usa diferentes estrategias
 * para calcular el estado de asistencia (PRESENTE, RETRASO, FALTA)
 */
public class AsistenciaCU {
    private static final String TAG = "AsistenciaCU";
    private IEstrategiaAsistencia estrategia;

    public AsistenciaCU(Context context) {
        // Inicializar acceso a datos de todas las entidades necesarias
        Asistencia.inicializar(context);
        Grupo.inicializar(context);
        Horario.inicializar(context);
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
            // Validar que el alumno esté inscrito
            if (!Asistencia.estaInscrito(alumnoId, grupoId)) {
                Log.w(TAG, "El alumno " + alumnoId + " no está inscrito en el grupo " + grupoId);
                return false;
            }

            // Obtener horarios del grupo para validar
            List<Horario> horarios = Horario.obtenerHorariosGrupo(grupoId);
            if (horarios.isEmpty()) {
                Log.w(TAG, "El grupo " + grupoId + " no tiene horarios configurados");
                return false;
            }

            // Obtener la hora de inicio y fin del primer horario (simplificado)
            Horario horario = horarios.get(0);
            String horaInicio = horario.getHoraInicio();
            String horaFin = horario.getHoraFin();

            // Obtener tipo de estrategia del grupo
            String tipoEstrategia = Grupo.obtenerTipoEstrategiaGrupo(grupoId);

            // Configurar estrategia automáticamente desde BD si no está configurada
            if (estrategia == null) {
                Log.d(TAG, "Configurando estrategia desde BD: " + tipoEstrategia);
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
            }

            // Delegar el cálculo del estado a la estrategia (Strategy Pattern)
            String estado = estrategia.calcularEstado(horaMarcado, horaInicio, horaFin);
            Log.d(TAG, "Estado calculado por la estrategia: " + estado + " (Inicio: " + horaInicio + ", Fin: " + horaFin + ")");

            // Crear y guardar la asistencia
            Asistencia asistencia = new Asistencia();
            asistencia.setAlumnoId(alumnoId);
            asistencia.setGrupoId(grupoId);
            asistencia.setFecha(fecha);
            asistencia.setHoraMarcada(horaMarcado);
            asistencia.setEstado(estado);

            Asistencia.guardar(asistencia);
            Log.d(TAG, "Asistencia marcada exitosamente");
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
        return Asistencia.obtenerPorGrupo(grupoId);
    }

    /**
     * Obtiene todas las asistencias de un alumno
     */
    public List<Asistencia> obtenerAsistenciasPorAlumno(Integer alumnoId) {
        return Asistencia.obtenerPorAlumno(alumnoId);
    }
}

