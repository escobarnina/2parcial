package com.arquitectura.asistente.negocio.strategy;

import java.util.logging.Logger;

/**
 * Estrategia concreta que determina el estado con política estándar (PRESENTE/RETRASO/FALTA)
 * Capa de Negocio - Strategy Pattern - ConcreteStrategy 2
 */
public class EstrategiaRetraso implements IEstrategiaAsistencia {
    private static final Logger logger = Logger.getLogger(EstrategiaRetraso.class.getName());
    private static final int MARGEN_PRESENTE = 10; // 0-10 min = PRESENTE
    private static final int MARGEN_RETRASO = 30;  // 11-30 min = RETRASO

    @Override
    public String calcularEstado(String horaMarcado, String horaInicio, Integer toleranciaMinutos) {
        logger.info("Evaluando asistencia (Estándar) - Marcado: " + horaMarcado + ", Inicio: " + horaInicio);
        
        try {
            int diferencia = calcularDiferenciaMinutos(horaMarcado, horaInicio);
            logger.info("Diferencia: " + diferencia + " minutos | Márgenes: 0-10 PRESENTE, 11-30 RETRASO, >30 FALTA");
            
            String estado;
            if (diferencia <= MARGEN_PRESENTE) {
                logger.info("Llegó a tiempo (0-10 min) → PRESENTE");
                estado = "PRESENTE";
            } else if (diferencia <= MARGEN_RETRASO) {
                logger.info("Llegó con retraso (11-30 min) → RETRASO");
                estado = "RETRASO";
            } else {
                logger.info("Llegó muy tarde (>30 min) → FALTA");
                estado = "FALTA";
            }
            
            logger.info("Estado determinado: " + estado);
            return estado;
            
        } catch (Exception e) {
            logger.severe("Error al calcular estado: " + e.getMessage());
            return "RETRASO"; // Por defecto retorna RETRASO
        }
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

