package com.arquitectura.asistente.negocio.strategy;

/**
 * Interface del Patrón Strategy para determinar el estado de asistencia
 * Capa de Negocio - Strategy Pattern
 * Define el contrato que deben cumplir todas las estrategias concretas
 */
public interface IEstrategiaAsistencia {
    
    /**
     * Calcula el estado de asistencia según el algoritmo específico de la estrategia
     * 
     * @param horaMarcado Hora en que el estudiante marcó asistencia (formato HH:mm)
     * @param horaInicio Hora de inicio de la clase (formato HH:mm)
     * @param horaFin Hora de finalización de la clase (formato HH:mm)
     * @return Estado de asistencia: "PRESENTE", "RETRASO" o "FALTA"
     */
    String calcularEstado(String horaMarcado, String horaInicio, String horaFin);
}

