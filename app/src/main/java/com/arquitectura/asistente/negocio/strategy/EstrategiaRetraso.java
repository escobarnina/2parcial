package com.arquitectura.asistente.negocio.strategy;

import java.util.logging.Logger;

/**
 * Estrategia concreta que determina el estado con política estándar (PRESENTE/RETRASO/FALTA)
 * Capa de Negocio - Strategy Pattern - ConcreteStrategy 2
 * - Antes de horaInicio: PRESENTE
 * - Después de horaInicio hasta 30 min: RETRASO
 * - Después de 30 min pero antes de horaFin: RETRASO (no FALTA)
 * - Después de horaFin: FALTA
 */
public class EstrategiaRetraso implements IEstrategiaAsistencia {
    private static final Logger logger = Logger.getLogger(EstrategiaRetraso.class.getName());
    private static final int MARGEN_RETRASO = 30;  // 30 minutos = RETRASO

    @Override
    public String calcularEstado(String horaMarcado, String horaInicio, String horaFin) {
        logger.info("Evaluando asistencia (Estándar) - Marcado: " + horaMarcado + ", Inicio: " + horaInicio + ", Fin: " + horaFin);
        
        try {
            // Verificar si está fuera del rango de horario [horaInicio, horaFin]
            if (estaFueraDelHorario(horaMarcado, horaInicio, horaFin)) {
                logger.info("Fuera del horario de clase → null (no se puede marcar asistencia)");
                return null;
            }
            
            int diferencia = calcularDiferenciaMinutos(horaMarcado, horaInicio);
            logger.info("Diferencia: " + diferencia + " minutos desde inicio");
            
            String estado;
            if (diferencia < 0) {
                // Llegó antes de la hora de inicio (pero dentro del horario de clase)
                // Esto no debería pasar si estaFueraDelHorario funciona correctamente,
                // pero por seguridad mantenemos esta lógica
                logger.info("Llegó antes de la hora de inicio (dentro del horario) → PRESENTE");
                estado = "PRESENTE";
            } else if (diferencia <= MARGEN_RETRASO) {
                // Llegó dentro de los primeros 30 minutos después del inicio
                logger.info("Llegó con retraso (0-30 min) → RETRASO");
                estado = "RETRASO";
            } else {
                // Llegó después de 30 minutos pero antes de horaFin
                logger.info("Llegó muy tarde (>30 min pero dentro del horario) → RETRASO");
                estado = "RETRASO";
            }
            
            logger.info("Estado determinado: " + estado);
            return estado;
            
        } catch (Exception e) {
            logger.severe("Error al calcular estado: " + e.getMessage());
            return "RETRASO"; // Por defecto retorna RETRASO
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

    private int calcularDiferenciaMinutos(String hora1, String hora2) {
        int minutos1 = convertirHoraAMinutos(hora1);
        int minutos2 = convertirHoraAMinutos(hora2);
        return minutos1 - minutos2;
    }

    private int convertirHoraAMinutos(String hora) {
        String[] partes = hora.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);
        return horas * 60 + minutos;
    }
}

