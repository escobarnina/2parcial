package com.arquitectura.asistente.negocio.strategy;

import java.util.logging.Logger;

/**
 * Estrategia concreta que determina el estado con política estricta
 * Capa de Negocio - Strategy Pattern - ConcreteStrategy 3
 * - Primeros 10 minutos después de horaInicio: PRESENTE
 * - De 11 a 30 minutos después de horaInicio: RETRASO
 * - Después de 30 minutos pero antes de horaFin: FALTA
 * - Después de horaFin: FALTA
 */
public class EstrategiaFalta implements IEstrategiaAsistencia {
    private static final Logger logger = Logger.getLogger(EstrategiaFalta.class.getName());
    private static final int MARGEN_PRESENTE = 10; // 0-10 min = PRESENTE
    private static final int MARGEN_RETRASO = 30;  // 11-30 min = RETRASO

    @Override
    public String calcularEstado(String horaMarcado, String horaInicio, String horaFin) {
        logger.info("Evaluando asistencia (Estricto) - Marcado: " + horaMarcado + ", Inicio: " + horaInicio + ", Fin: " + horaFin);
        
        try {
            // Verificar si está fuera del horario de finalización
            if (estaFueraDelHorario(horaMarcado, horaFin)) {
                logger.info("Fuera del horario de finalización → FALTA");
                return "FALTA";
            }
            
            int diferencia = calcularDiferenciaMinutos(horaMarcado, horaInicio);
            logger.info("Diferencia: " + diferencia + " minutos desde inicio | Márgenes: 0-10 PRESENTE, 11-30 RETRASO, >30 FALTA");
            
            String estado;
            if (diferencia < 0) {
                // Llegó antes de la hora de inicio
                logger.info("Llegó antes de la hora de inicio → PRESENTE");
                estado = "PRESENTE";
            } else if (diferencia <= MARGEN_PRESENTE) {
                // Primeros 10 minutos después del inicio
                logger.info("Llegó a tiempo (0-10 min) → PRESENTE");
                estado = "PRESENTE";
            } else if (diferencia <= MARGEN_RETRASO) {
                // De 11 a 30 minutos después del inicio
                logger.info("Llegó con retraso (11-30 min) → RETRASO");
                estado = "RETRASO";
            } else {
                // Después de 30 minutos pero antes de horaFin
                logger.info("Llegó muy tarde (>30 min pero dentro del horario) → FALTA");
                estado = "FALTA";
            }
            
            logger.info("Estado determinado: " + estado);
            return estado;
            
        } catch (Exception e) {
            logger.severe("Error al calcular estado: " + e.getMessage());
            return "FALTA"; // Por defecto retorna FALTA
        }
    }

    private boolean estaFueraDelHorario(String horaMarcado, String horaFin) {
        int minutosMarcado = convertirHoraAMinutos(horaMarcado);
        int minutosFin = convertirHoraAMinutos(horaFin);
        return minutosMarcado > minutosFin;
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

