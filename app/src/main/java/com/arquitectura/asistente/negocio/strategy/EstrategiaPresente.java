package com.arquitectura.asistente.negocio.strategy;

import java.util.logging.Logger;

/**
 * Estrategia concreta que determina el estado como PRESENTE (Política Flexible)
 * Capa de Negocio - Strategy Pattern - ConcreteStrategy 1
 * No importa la hora que ingrese, siempre PRESENTE, excepto si está fuera del horario
 */
public class EstrategiaPresente implements IEstrategiaAsistencia {
    private static final Logger logger = Logger.getLogger(EstrategiaPresente.class.getName());

    @Override
    public String calcularEstado(String horaMarcado, String horaInicio, String horaFin) {
        logger.info("Evaluando asistencia (Muy Flexible) - Marcado: " + horaMarcado + ", Inicio: " + horaInicio + ", Fin: " + horaFin);
        
        try {
            // Verificar si está fuera del rango de horario [horaInicio, horaFin]
            if (estaFueraDelHorario(horaMarcado, horaInicio, horaFin)) {
                logger.info("Fuera del horario de clase → null (no se puede marcar asistencia)");
                return null;
            }
            
            // Estrategia Muy Flexible: siempre PRESENTE si está dentro del horario
            logger.info("Estado determinado: PRESENTE (Muy Flexible)");
            return "PRESENTE";
        } catch (Exception e) {
            logger.severe("Error al calcular estado: " + e.getMessage());
            return "PRESENTE"; // Por defecto retorna PRESENTE
        }
    }

    /**
     * Verifica si la hora marcada está fuera del rango de horario [horaInicio, horaFin]
     * @param horaMarcado Hora en que se marcó la asistencia
     * @param horaInicio Hora de inicio de la clase
     * @param horaFin Hora de fin de la clase
     * @return true si está fuera del horario, false si está dentro
     */
    private boolean estaFueraDelHorario(String horaMarcado, String horaInicio, String horaFin) {
        int minutosMarcado = convertirHoraAMinutos(horaMarcado);
        int minutosInicio = convertirHoraAMinutos(horaInicio);
        int minutosFin = convertirHoraAMinutos(horaFin);
        
        // Está fuera si es antes de horaInicio o después de horaFin
        return minutosMarcado < minutosInicio || minutosMarcado > minutosFin;
    }

    private int convertirHoraAMinutos(String hora) {
        String[] partes = hora.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);
        return horas * 60 + minutos;
    }
}

