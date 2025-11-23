package com.arquitectura.asistente.negocio.strategy;

import java.util.logging.Logger;

/**
 * Estrategia concreta que determina el estado como PRESENTE (Política Flexible)
 * Capa de Negocio - Strategy Pattern - ConcreteStrategy 1
 */
public class EstrategiaPresente implements IEstrategiaAsistencia {
    private static final Logger logger = Logger.getLogger(EstrategiaPresente.class.getName());

    @Override
    public String calcularEstado(String horaMarcado, String horaInicio, Integer toleranciaMinutos) {
        logger.info("Evaluando asistencia (Muy Flexible) - Marcado: " + horaMarcado + ", Inicio: " + horaInicio);
        
        try {
            // Estrategia Muy Flexible: siempre PRESENTE
            logger.info("Estado determinado: PRESENTE (Muy Flexible)");
            return "PRESENTE";
        } catch (Exception e) {
            logger.severe("Error al calcular estado: " + e.getMessage());
            return "PRESENTE"; // Por defecto retorna PRESENTE
        }
    }
}

