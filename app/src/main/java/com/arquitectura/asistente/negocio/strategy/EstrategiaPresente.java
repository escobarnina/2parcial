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
            // Verificar si está fuera del horario de finalización
            if (estaFueraDelHorario(horaMarcado, horaFin)) {
                logger.info("Fuera del horario de finalización → FALTA");
                return "FALTA";
            }
            
            // Estrategia Muy Flexible: siempre PRESENTE si está dentro del horario
            logger.info("Estado determinado: PRESENTE (Muy Flexible)");
            return "PRESENTE";
        } catch (Exception e) {
            logger.severe("Error al calcular estado: " + e.getMessage());
            return "PRESENTE"; // Por defecto retorna PRESENTE
        }
    }

    private boolean estaFueraDelHorario(String horaMarcado, String horaFin) {
        int minutosMarcado = convertirHoraAMinutos(horaMarcado);
        int minutosFin = convertirHoraAMinutos(horaFin);
        return minutosMarcado > minutosFin;
    }

    private int convertirHoraAMinutos(String hora) {
        String[] partes = hora.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);
        return horas * 60 + minutos;
    }
}

