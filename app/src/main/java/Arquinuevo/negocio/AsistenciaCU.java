package Arquinuevo.negocio;

import Arquinuevo.datos.Asistencia;
import Arquinuevo.datos.AsistenciaRepository;
import Arquinuevo.datos.Horario;
import Arquinuevo.negocio.strategy.IEstrategiaAsistencia;
import Arquinuevo.negocio.strategy.EstrategiaPresente;
import Arquinuevo.negocio.strategy.EstrategiaRetraso;
import Arquinuevo.negocio.strategy.EstrategiaFalta;
import java.util.List;
import java.util.logging.Logger;

/**
 * Caso de uso para gestionar asistencias
 * Capa de Negocio - Strategy Pattern - Context (Contexto)
 * Este caso de uso actúa como contexto que usa diferentes estrategias
 * para calcular el estado de asistencia (PRESENTE, RETRASO, FALTA)
 */
public class AsistenciaCU {
    private static final Logger logger = Logger.getLogger(AsistenciaCU.class.getName());
    private AsistenciaRepository repository;
    private IEstrategiaAsistencia estrategia;

    public AsistenciaCU() {
        this.repository = AsistenciaRepository.getInstance();
    }

    /**
     * Establece la estrategia para calcular el estado de asistencia
     * Patrón Strategy: Permite cambiar el algoritmo en tiempo de ejecución
     */
    public void setEstrategia(IEstrategiaAsistencia estrategia) {
        logger.info("Cambiando estrategia a: " + estrategia.getClass().getSimpleName());
        this.estrategia = estrategia;
    }

    /**
     * Marca asistencia de un alumno en un grupo
     * Usa Strategy Pattern para calcular el estado
     */
    public boolean marcarAsistencia(Integer alumnoId, Integer grupoId, String fecha, String horaMarcado) {
        try {
            // Validar que el alumno esté inscrito
            if (!repository.estaInscrito(alumnoId, grupoId)) {
                logger.warning("El alumno " + alumnoId + " no está inscrito en el grupo " + grupoId);
                return false;
            }

            // Obtener horarios del grupo para validar
            List<Horario> horarios = repository.obtenerHorariosGrupo(grupoId);
            if (horarios.isEmpty()) {
                logger.warning("El grupo " + grupoId + " no tiene horarios configurados");
                return false;
            }

            // Obtener la hora de inicio del primer horario (simplificado)
            String horaInicio = horarios.get(0).getHoraInicio();

            // Obtener tolerancia y tipo de estrategia del grupo
            Integer toleranciaMinutos = repository.obtenerToleranciaGrupo(grupoId);
            String tipoEstrategia = repository.obtenerTipoEstrategiaGrupo(grupoId);

            // Configurar estrategia automáticamente desde BD si no está configurada
            if (estrategia == null) {
                logger.info("Configurando estrategia desde BD: " + tipoEstrategia);
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
            String estado = estrategia.calcularEstado(horaMarcado, horaInicio, toleranciaMinutos);
            logger.info("Estado calculado por la estrategia: " + estado + " (tolerancia: " + toleranciaMinutos + " min)");

            // Crear y guardar la asistencia
            Asistencia asistencia = new Asistencia();
            asistencia.setAlumnoId(alumnoId);
            asistencia.setGrupoId(grupoId);
            asistencia.setFecha(fecha);
            asistencia.setHoraMarcada(horaMarcado);
            asistencia.setEstado(estado);

            repository.guardar(asistencia);
            logger.info("Asistencia marcada exitosamente");
            return true;

        } catch (Exception e) {
            logger.severe("Error al marcar asistencia: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todas las asistencias de un grupo
     */
    public List<Asistencia> obtenerAsistenciasPorGrupo(Integer grupoId) {
        return repository.obtenerPorGrupo(grupoId);
    }

    /**
     * Obtiene todas las asistencias de un alumno
     */
    public List<Asistencia> obtenerAsistenciasPorAlumno(Integer alumnoId) {
        return repository.obtenerPorAlumno(alumnoId);
    }
}

