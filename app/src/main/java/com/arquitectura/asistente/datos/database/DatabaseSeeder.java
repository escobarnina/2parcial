package com.arquitectura.asistente.datos.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsabilidad: Gestionar los datos iniciales (seeders) de la base de datos
 * SQLite
 * 
 * Este archivo contiene todos los datos de prueba que se insertan
 * cuando se crea la base de datos por primera vez.
 * 
 * Separado de DatabaseHelper para mantener clara la separación de
 * responsabilidades:
 * - DatabaseMigrations: Estructura de la base de datos (esquema)
 * - DatabaseSeeder: Datos iniciales de prueba
 * - DatabaseHelper: Acceso a datos (conexión)
 * 
 * Adaptado de MySQL a SQLite para Android
 */
public class DatabaseSeeder {
    private static final String TAG = "DatabaseSeeder";

    /**
     * Inserta todos los datos de prueba en la base de datos.
     * Se ejecuta después de crear las tablas en onCreate.
     * 
     * @param db Base de datos SQLite donde se insertarán los datos
     */
    public static void seed(SQLiteDatabase db) {
        // Verificar si ya hay datos
        if (tieneDatos(db)) {
            Log.d(TAG, "Datos iniciales ya existen, omitiendo inserción");
            return;
        }

        seedUsuarios(db);
        seedMaterias(db);
        seedGrupos(db);
        seedHorarios(db);
        seedBoletas(db);
        seedAsistencias(db);

        Log.d(TAG, "Todos los datos de prueba insertados correctamente");
    }

    /**
     * Verifica si ya existen datos en la base de datos
     */
    private static boolean tieneDatos(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) as count FROM usuarios";
        try (Cursor cursor = db.rawQuery(sql, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        }
        return false;
    }

    /**
     * Inserta usuarios de prueba en la base de datos.
     * 60 estudiantes y 10 docentes
     */
    private static void seedUsuarios(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder(
                "INSERT INTO usuarios(nombres, apellidos, username, contrasena, registro, rol) VALUES ");

        // 60 estudiantes
        String[] nombresEstudiantes = {
                "Ana", "Juan", "Carlos", "María", "Pedro", "Laura", "Diego", "Sofía", "Luis", "Carmen",
                "Miguel", "Elena", "Javier", "Isabel", "Roberto", "Patricia", "Fernando", "Lucía", "Antonio", "Marta",
                "José", "Cristina", "Manuel", "Raquel", "Francisco", "Natalia", "Ángel", "Pilar", "David", "Rosa",
                "Daniel", "Silvia", "Alejandro", "Teresa", "Rafael", "Mercedes", "Sergio", "Dolores", "Álvaro",
                "Concepción",
                "Pablo", "Dolores", "Rubén", "Amparo", "Víctor", "Esperanza", "Adrián", "Rosario", "Óscar",
                "Encarnación",
                "Iván", "Remedios", "Mario", "Purificación", "Gonzalo", "Dolores", "Héctor", "María", "Nicolás",
                "Carmen"
        };

        String[] apellidosEstudiantes = {
                "García", "Pérez", "López", "González", "Martínez", "Sánchez", "Rodríguez", "Fernández", "Gómez",
                "Díaz",
                "Ruiz", "Hernández", "Jiménez", "Moreno", "Muñoz", "Álvarez", "Romero", "Alonso", "Gutiérrez",
                "Navarro",
                "Torres", "Domínguez", "Vázquez", "Ramos", "Gil", "Ramírez", "Serrano", "Blanco", "Suárez", "Molina",
                "Morales", "Ortega", "Delgado", "Castro", "Ortiz", "Rubio", "Marín", "Sanz", "Iglesias", "Medina",
                "Garrido", "Cortés", "Castillo", "Lozano", "Guerrero", "Cano", "Prieto", "Méndez", "Cruz", "Calvo",
                "Vidal", "León", "Márquez", "Herrera", "Peña", "Flores", "Campos", "Vega", "Fuentes", "Carrasco"
        };

        for (int i = 0; i < 60; i++) {
            int registro = 210000 + i;
            sql.append(String.format("('%s', '%s', 'estudiante%d', '1234', '%d', 'Estudiante')",
                    nombresEstudiantes[i], apellidosEstudiantes[i], i + 1, registro));
            if (i < 59)
                sql.append(", ");
        }

        sql.append(", ");

        // 10 docentes
        String[] nombresDocentes = { "Marcos", "María", "Julia", "Roberto", "Laura", "Carlos", "Ana", "José", "Elena",
                "Miguel" };
        String[] apellidosDocentes = { "Rodríguez", "Fernández", "Martínez", "Sánchez", "González", "López", "García",
                "Pérez", "Ruiz", "Hernández" };

        for (int i = 0; i < 10; i++) {
            int registro = 340000 + i;
            sql.append(String.format("('%s', '%s', 'docente%d', '1234', '%d', 'Docente')",
                    nombresDocentes[i], apellidosDocentes[i], i + 1, registro));
            if (i < 9)
                sql.append(", ");
        }

        try {
            db.execSQL(sql.toString());
            Log.d(TAG, "70 usuarios de prueba insertados (60 estudiantes, 10 docentes)");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar usuarios: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar usuarios", e);
        }
    }

    /**
     * Inserta materias de prueba en la base de datos.
     * 20 materias de diferentes niveles académicos.
     */
    private static void seedMaterias(SQLiteDatabase db) {
        String sql = "INSERT INTO materias(nombre, sigla, nivel) VALUES " +
                "('Programación I', 'PROG1', 1), " +
                "('Programación II', 'PROG2', 2), " +
                "('Programación III', 'PROG3', 3), " +
                "('Base de Datos I', 'BD1', 2), " +
                "('Base de Datos II', 'BD2', 3), " +
                "('Estructura de Datos', 'ED', 2), " +
                "('Algoritmos y Complejidad', 'ALG', 3), " +
                "('Sistemas Operativos', 'SO', 3), " +
                "('Redes de Computadoras', 'REDES', 4), " +
                "('Ingeniería de Software', 'IS', 4), " +
                "('Arquitectura de Computadoras', 'ARQ', 2), " +
                "('Matemática Discreta', 'MD', 1), " +
                "('Cálculo I', 'CAL1', 1), " +
                "('Cálculo II', 'CAL2', 2), " +
                "('Algebra Lineal', 'ALG_LIN', 2), " +
                "('Física I', 'FIS1', 1), " +
                "('Física II', 'FIS2', 2), " +
                "('Química General', 'QUIM', 1), " +
                "('Ética Profesional', 'ETICA', 3), " +
                "('Gestión de Proyectos', 'GP', 4)";

        try {
            db.execSQL(sql);
            Log.d(TAG, "20 materias de prueba insertadas");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar materias: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar materias", e);
        }
    }

    /**
     * Inserta grupos de prueba en la base de datos.
     * PATRON STRATEGY CON DATOS DE BD:
     * - Cada docente da clases de al menos 2 materias
     * - Grupos con diferentes tolerancias y estrategias
     */
    private static void seedGrupos(SQLiteDatabase db) {
        // Docente 1 (id=61): Programación I, Programación II, Algoritmos
        // Docente 2 (id=62): Programación I, Base de Datos I, Base de Datos II,
        // Ingeniería de Software
        // Docente 3 (id=63): Programación II, Estructura de Datos, Arquitectura,
        // Gestión de Proyectos
        // Docente 4 (id=64): Base de Datos I, Sistemas Operativos, Matemática Discreta
        // Docente 5 (id=65): Sistemas Operativos, Redes, Cálculo I
        // Docente 6 (id=66): Redes, Cálculo II, Algebra Lineal
        // Docente 7 (id=67): Física I, Física II, Química
        // Docente 8 (id=68): Programación III, Ética Profesional
        // Docente 9 (id=69): Estructura de Datos, Ingeniería de Software
        // Docente 10 (id=70): Matemática Discreta, Cálculo I, Gestión de Proyectos

        String sql = "INSERT INTO grupos(materia_id, docente_id, semestre, gestion, capacidad, grupo, tolerancia_minutos, tipo_estrategia) VALUES "
                +
                // Docente 1 (Marcos Rodríguez - id=61)
                "(1, 61, 1, 2025, 35, 'A', 10, 'FALTA'), " + // Programación I
                "(2, 61, 1, 2025, 30, 'A', 10, 'FALTA'), " + // Programación II
                "(7, 61, 1, 2025, 25, 'A', 5, 'RETRASO'), " + // Algoritmos
                // Docente 2 (María Fernández - id=62)
                "(1, 62, 1, 2025, 30, 'B', 15, 'FALTA'), " + // Programación I
                "(4, 62, 1, 2025, 35, 'A', 10, 'PRESENTE'), " + // Base de Datos I
                "(5, 62, 1, 2025, 28, 'A', 10, 'FALTA'), " + // Base de Datos II
                "(10, 62, 1, 2025, 30, 'A', 15, 'RETRASO'), " + // Ingeniería de Software
                // Docente 3 (Julia Martínez - id=63)
                "(2, 63, 1, 2025, 32, 'B', 20, 'FALTA'), " + // Programación II
                "(6, 63, 1, 2025, 30, 'A', 10, 'FALTA'), " + // Estructura de Datos
                "(11, 63, 1, 2025, 35, 'A', 10, 'PRESENTE'), " + // Arquitectura
                "(20, 63, 1, 2025, 40, 'A', 10, 'RETRASO'), " + // Gestión de Proyectos
                // Docente 4 (Roberto Sánchez - id=64)
                "(4, 64, 1, 2025, 30, 'B', 15, 'RETRASO'), " + // Base de Datos I
                "(8, 64, 1, 2025, 28, 'A', 10, 'PRESENTE'), " + // Sistemas Operativos
                "(12, 64, 1, 2025, 40, 'A', 10, 'FALTA'), " + // Matemática Discreta
                // Docente 5 (Laura González - id=65)
                "(8, 65, 1, 2025, 30, 'B', 15, 'PRESENTE'), " + // Sistemas Operativos
                "(9, 65, 1, 2025, 32, 'A', 10, 'RETRASO'), " + // Redes
                "(13, 65, 1, 2025, 45, 'A', 15, 'RETRASO'), " + // Cálculo I
                // Docente 6 (Carlos López - id=66)
                "(9, 66, 1, 2025, 30, 'B', 10, 'RETRASO'), " + // Redes
                "(14, 66, 1, 2025, 35, 'A', 10, 'FALTA'), " + // Cálculo II
                "(15, 66, 1, 2025, 32, 'A', 10, 'PRESENTE'), " + // Algebra Lineal
                // Docente 7 (Ana García - id=67)
                "(16, 67, 1, 2025, 40, 'A', 10, 'RETRASO'), " + // Física I
                "(17, 67, 1, 2025, 38, 'A', 10, 'FALTA'), " + // Física II
                "(18, 67, 1, 2025, 42, 'A', 15, 'RETRASO'), " + // Química
                // Docente 8 (José Pérez - id=68)
                "(3, 68, 1, 2025, 25, 'A', 5, 'PRESENTE'), " + // Programación III
                "(19, 68, 1, 2025, 50, 'A', 20, 'PRESENTE'), " + // Ética Profesional
                // Docente 9 (Elena Ruiz - id=69)
                "(6, 69, 1, 2025, 30, 'B', 20, 'RETRASO'), " + // Estructura de Datos
                "(10, 69, 1, 2025, 28, 'B', 15, 'PRESENTE'), " + // Ingeniería de Software
                // Docente 10 (Miguel Hernández - id=70)
                "(12, 70, 1, 2025, 40, 'B', 10, 'FALTA'), " + // Matemática Discreta
                "(13, 70, 1, 2025, 45, 'B', 15, 'FALTA'), " + // Cálculo I
                "(20, 70, 1, 2025, 35, 'B', 10, 'PRESENTE')"; // Gestión de Proyectos

        try {
            db.execSQL(sql);
            Log.d(TAG, "30 grupos de prueba insertados con tolerancias configurables");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar grupos: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar grupos", e);
        }
    }

    /**
     * Inserta horarios de prueba en la base de datos.
     * Horarios desde las 7am, divididos en:
     * - Algunos grupos: Lunes, Miércoles, Viernes
     * - Otros grupos: Martes, Jueves
     */
    private static void seedHorarios(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder("INSERT INTO horarios(grupo_id, dia, hora_inicio, hora_fin) VALUES ");

        // Grupos con horario Lun-Mie-Vie (grupos impares: 1, 3, 5, 7, 9, 11, 13, 15,
        // 17, 19, 21, 23, 25, 27, 29)
        int[] gruposLunMieVie = { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29 };
        String[] horasLunMieVie = { "07:00", "07:20", "07:40", "08:00", "08:20" };

        int horaIndex = 0;
        for (int grupoId : gruposLunMieVie) {
            String horaInicio = horasLunMieVie[horaIndex % horasLunMieVie.length];
            String horaFin = calcularHoraFin(horaInicio);
            sql.append(String.format("(%d, 'Lunes', '%s', '%s'), ", grupoId, horaInicio, horaFin));
            sql.append(String.format("(%d, 'Miércoles', '%s', '%s'), ", grupoId, horaInicio, horaFin));
            sql.append(String.format("(%d, 'Viernes', '%s', '%s')", grupoId, horaInicio, horaFin));
            horaIndex++;
            if (grupoId < 29 || horaIndex < gruposLunMieVie.length)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupos con horario Mar-Jue (grupos pares: 2, 4, 6, 8, 10, 12, 14, 16, 18, 20,
        // 22, 24, 26, 28, 30)
        int[] gruposMarJue = { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30 };

        horaIndex = 0;
        for (int i = 0; i < gruposMarJue.length; i++) {
            int grupoId = gruposMarJue[i];
            String horaInicio = horasLunMieVie[horaIndex % horasLunMieVie.length];
            String horaFin = calcularHoraFin(horaInicio);
            sql.append(String.format("(%d, 'Martes', '%s', '%s'), ", grupoId, horaInicio, horaFin));
            sql.append(String.format("(%d, 'Jueves', '%s', '%s')", grupoId, horaInicio, horaFin));
            horaIndex++;
            if (i < gruposMarJue.length - 1)
                sql.append(", ");
        }

        try {
            db.execSQL(sql.toString());
            Log.d(TAG, "Horarios de prueba insertados (Lun-Mie-Vie y Mar-Jue desde las 7am)");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar horarios: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar horarios", e);
        }
    }

    /**
     * Calcula la hora de fin sumando 2 horas a la hora de inicio
     */
    private static String calcularHoraFin(String horaInicio) {
        String[] partes = horaInicio.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);
        horas += 2;
        if (horas >= 24)
            horas -= 24;
        return String.format("%02d:%02d", horas, minutos);
    }

    /**
     * Inserta boletas (inscripciones) de prueba en la base de datos.
     * Al menos 15 estudiantes inscritos en cada materia/grupo popular
     */
    private static void seedBoletas(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder(
                "INSERT INTO boletas(alumno_id, grupo_id, fecha, semestre, gestion) VALUES ");

        // Grupo 1 (Programación I - muy popular): 20 estudiantes (1-20)
        for (int i = 1; i <= 20; i++) {
            sql.append(String.format("(%d, 1, '2025-01-15', 1, 2025)", i));
            if (i < 20)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 2 (Programación I - grupo B): 18 estudiantes (21-38)
        for (int i = 21; i <= 38; i++) {
            sql.append(String.format("(%d, 2, '2025-01-15', 1, 2025)", i));
            if (i < 38)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 3 (Programación II): 16 estudiantes (1-16)
        for (int i = 1; i <= 16; i++) {
            sql.append(String.format("(%d, 3, '2025-01-16', 1, 2025)", i));
            if (i < 16)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 4 (Base de Datos I): 17 estudiantes (17-33)
        for (int i = 17; i <= 33; i++) {
            sql.append(String.format("(%d, 4, '2025-01-16', 1, 2025)", i));
            if (i < 33)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 5 (Base de Datos I - grupo B): 15 estudiantes (34-48)
        for (int i = 34; i <= 48; i++) {
            sql.append(String.format("(%d, 5, '2025-01-16', 1, 2025)", i));
            if (i < 48)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 6 (Base de Datos II): 15 estudiantes (1-15)
        for (int i = 1; i <= 15; i++) {
            sql.append(String.format("(%d, 6, '2025-01-17', 1, 2025)", i));
            if (i < 15)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 7 (Estructura de Datos): 16 estudiantes (16-31)
        for (int i = 16; i <= 31; i++) {
            sql.append(String.format("(%d, 7, '2025-01-17', 1, 2025)", i));
            if (i < 31)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 8 (Estructura de Datos - grupo B): 15 estudiantes (32-46)
        for (int i = 32; i <= 46; i++) {
            sql.append(String.format("(%d, 8, '2025-01-17', 1, 2025)", i));
            if (i < 46)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 9 (Algoritmos): 15 estudiantes (47-60, 1-1)
        for (int i = 47; i <= 60; i++) {
            sql.append(String.format("(%d, 9, '2025-01-18', 1, 2025)", i));
            if (i < 60)
                sql.append(", ");
        }
        sql.append(", (1, 9, '2025-01-18', 1, 2025)");

        sql.append(", ");

        // Grupo 10 (Sistemas Operativos): 15 estudiantes (2-16)
        for (int i = 2; i <= 16; i++) {
            sql.append(String.format("(%d, 10, '2025-01-18', 1, 2025)", i));
            if (i < 16)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 11 (Sistemas Operativos - grupo B): 15 estudiantes (17-31)
        for (int i = 17; i <= 31; i++) {
            sql.append(String.format("(%d, 11, '2025-01-18', 1, 2025)", i));
            if (i < 31)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 12 (Redes): 15 estudiantes (32-46)
        for (int i = 32; i <= 46; i++) {
            sql.append(String.format("(%d, 12, '2025-01-19', 1, 2025)", i));
            if (i < 46)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 13 (Redes - grupo B): 15 estudiantes (47-60, 1)
        for (int i = 47; i <= 60; i++) {
            sql.append(String.format("(%d, 13, '2025-01-19', 1, 2025)", i));
            if (i < 60)
                sql.append(", ");
        }
        sql.append(", (1, 13, '2025-01-19', 1, 2025)");

        sql.append(", ");

        // Grupo 14 (Ingeniería de Software): 15 estudiantes (2-16)
        for (int i = 2; i <= 16; i++) {
            sql.append(String.format("(%d, 14, '2025-01-19', 1, 2025)", i));
            if (i < 16)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 15 (Ingeniería de Software - grupo B): 15 estudiantes (17-31)
        for (int i = 17; i <= 31; i++) {
            sql.append(String.format("(%d, 15, '2025-01-20', 1, 2025)", i));
            if (i < 31)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 16 (Arquitectura): 15 estudiantes (32-46)
        for (int i = 32; i <= 46; i++) {
            sql.append(String.format("(%d, 16, '2025-01-20', 1, 2025)", i));
            if (i < 46)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 17 (Matemática Discreta): 15 estudiantes (47-60, 1)
        for (int i = 47; i <= 60; i++) {
            sql.append(String.format("(%d, 17, '2025-01-20', 1, 2025)", i));
            if (i < 60)
                sql.append(", ");
        }
        sql.append(", (1, 17, '2025-01-20', 1, 2025)");

        sql.append(", ");

        // Grupo 18 (Matemática Discreta - grupo B): 15 estudiantes (2-16)
        for (int i = 2; i <= 16; i++) {
            sql.append(String.format("(%d, 18, '2025-01-21', 1, 2025)", i));
            if (i < 16)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 19 (Cálculo I): 15 estudiantes (17-31)
        for (int i = 17; i <= 31; i++) {
            sql.append(String.format("(%d, 19, '2025-01-21', 1, 2025)", i));
            if (i < 31)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 20 (Cálculo I - grupo B): 15 estudiantes (32-46)
        for (int i = 32; i <= 46; i++) {
            sql.append(String.format("(%d, 20, '2025-01-21', 1, 2025)", i));
            if (i < 46)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 21 (Cálculo II): 15 estudiantes (47-60, 1)
        for (int i = 47; i <= 60; i++) {
            sql.append(String.format("(%d, 21, '2025-01-22', 1, 2025)", i));
            if (i < 60)
                sql.append(", ");
        }
        sql.append(", (1, 21, '2025-01-22', 1, 2025)");

        sql.append(", ");

        // Grupo 22 (Algebra Lineal): 15 estudiantes (2-16)
        for (int i = 2; i <= 16; i++) {
            sql.append(String.format("(%d, 22, '2025-01-22', 1, 2025)", i));
            if (i < 16)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 23 (Física I): 15 estudiantes (17-31)
        for (int i = 17; i <= 31; i++) {
            sql.append(String.format("(%d, 23, '2025-01-22', 1, 2025)", i));
            if (i < 31)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 24 (Física II): 15 estudiantes (32-46)
        for (int i = 32; i <= 46; i++) {
            sql.append(String.format("(%d, 24, '2025-01-23', 1, 2025)", i));
            if (i < 46)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 25 (Química): 15 estudiantes (47-60, 1)
        for (int i = 47; i <= 60; i++) {
            sql.append(String.format("(%d, 25, '2025-01-23', 1, 2025)", i));
            if (i < 60)
                sql.append(", ");
        }
        sql.append(", (1, 25, '2025-01-23', 1, 2025)");

        sql.append(", ");

        // Grupo 26 (Programación III): 15 estudiantes (2-16)
        for (int i = 2; i <= 16; i++) {
            sql.append(String.format("(%d, 26, '2025-01-23', 1, 2025)", i));
            if (i < 16)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 27 (Ética Profesional): 15 estudiantes (17-31)
        for (int i = 17; i <= 31; i++) {
            sql.append(String.format("(%d, 27, '2025-01-24', 1, 2025)", i));
            if (i < 31)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 28 (Gestión de Proyectos): 15 estudiantes (32-46)
        for (int i = 32; i <= 46; i++) {
            sql.append(String.format("(%d, 28, '2025-01-24', 1, 2025)", i));
            if (i < 46)
                sql.append(", ");
        }

        sql.append(", ");

        // Grupo 29 (Gestión de Proyectos - grupo B): 15 estudiantes (47-60, 1)
        for (int i = 47; i <= 60; i++) {
            sql.append(String.format("(%d, 29, '2025-01-24', 1, 2025)", i));
            if (i < 60)
                sql.append(", ");
        }
        sql.append(", (1, 29, '2025-01-24', 1, 2025)");

        sql.append(", ");

        // Grupo 30 (Ética Profesional - grupo alternativo): 15 estudiantes (2-16)
        for (int i = 2; i <= 16; i++) {
            sql.append(String.format("(%d, 30, '2025-01-25', 1, 2025)", i));
            if (i < 16)
                sql.append(", ");
        }

        try {
            db.execSQL(sql.toString());
            Log.d(TAG, "Boletas (inscripciones) de prueba insertadas - al menos 15 estudiantes por grupo");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar boletas: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar boletas", e);
        }
    }

    /**
     * Inserta asistencias de prueba en la base de datos.
     * Las asistencias respetan los horarios de los grupos y los días de clase.
     * Semana pasada: 20-24 de enero 2025
     * Al menos 5 asistencias por grupo para permitir exportación
     */
    private static void seedAsistencias(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder(
                "INSERT INTO asistencias(alumno_id, grupo_id, fecha, hora_marcada, estado) VALUES ");

        // Fechas de la semana pasada (17-21 noviembre 2025)
        String fechaLunes = "2025-11-17";
        String fechaMartes = "2025-11-18";
        String fechaMiercoles = "2025-11-19";
        String fechaJueves = "2025-11-20";
        String fechaViernes = "2025-11-21";

        // Horarios según grupo (índice del grupo - 1 para el array)
        String[] horasInicio = {
                "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", // Grupos 1-6
                "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", // Grupos 7-12
                "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", // Grupos 13-18
                "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", // Grupos 19-24
                "07:00", "08:00", "09:00", "10:00", "11:00", "12:00" // Grupos 25-30
        };

        // Estrategias por grupo (sincronizadas exactamente con seedGrupos, línea
        // 173-211)
        // Mapeo manual grupo por grupo desde la definición de INSERT
        String[] estrategias = {
                "RETRASO", "FALTA", "RETRASO", "FALTA", "PRESENTE", "FALTA", // Grupos 1-6
                "FALTA", "FALTA", "PRESENTE", "RETRASO", "RETRASO", "FALTA", // Grupos 7-12
                "PRESENTE", "RETRASO", "PRESENTE", "RETRASO", "RETRASO", "RETRASO", // Grupos 13-18
                "FALTA", "RETRASO", "PRESENTE", "PRESENTE", "RETRASO", "PRESENTE", // Grupos 19-24
                "PRESENTE", "PRESENTE", "RETRASO", "PRESENTE", "FALTA", "PRESENTE" // Grupos 25-30
        };

        // Tolerancias por grupo (sincronizadas exactamente con seedGrupos, línea
        // 173-211)
        int[] tolerancias = {
                10, 10, 5, 15, 10, 10, // Grupos 1-6: RETRASO10, FALTA10, RETRASO5, FALTA15, PRESENTE10, FALTA10
                20, 10, 10, 15, 10, 10, // Grupos 7-12: FALTA20, FALTA10, PRESENTE10, RETRASO15, RETRASO10, FALTA10
                10, 15, 10, 10, 10, 15, // Grupos 13-18: PRESENTE10, RETRASO15, PRESENTE10, RETRASO10, RETRASO10,
                                        // RETRASO15
                10, 10, 5, 20, 15, 10, // Grupos 19-24: FALTA10, RETRASO10, PRESENTE5, PRESENTE20, RETRASO15,
                                       // PRESENTE10
                10, 15, 20, 15, 10, 10 // Grupos 25-30: PRESENTE10, PRESENTE15, RETRASO20, PRESENTE15, FALTA10,
                                       // PRESENTE10
        };

        boolean firstEntry = true;

        // Generar asistencias para todos los 30 grupos
        for (int grupoId = 1; grupoId <= 30; grupoId++) {
            String horaInicio = horasInicio[grupoId - 1];
            String estrategia = estrategias[grupoId - 1];
            int tolerancia = tolerancias[grupoId - 1];

            // Determinar días según paridad del grupo
            String[] fechas;
            if (grupoId % 2 == 1) {
                // Grupos impares: Lun-Mie-Vie
                fechas = new String[] { fechaLunes, fechaMiercoles, fechaViernes };
            } else {
                // Grupos pares: Mar-Jue
                fechas = new String[] { fechaMartes, fechaJueves };
            }

            // Obtener estudiantes inscritos en este grupo desde boletas
            List<Integer> estudiantes = obtenerEstudiantesInscritosEnGrupo(db, grupoId);
            if (estudiantes.isEmpty()) {
                Log.w(TAG, "Grupo " + grupoId + " no tiene estudiantes inscritos, omitiendo asistencias");
                continue;
            }

            // Generar al menos 5 asistencias por grupo (distribuidas en los días de clase)
            int asistenciasGeneradas = 0;
            int minimoAsistencias = Math.min(5, estudiantes.size()); // Al menos 5 o todos los estudiantes

            for (String fecha : fechas) {
                if (asistenciasGeneradas >= minimoAsistencias)
                    break;

                // Seleccionar algunos estudiantes para este día (mínimo 2-3 por día)
                int estudiantesPorDia = Math.min(3, estudiantes.size() - asistenciasGeneradas);
                if (estudiantesPorDia < 1)
                    estudiantesPorDia = 1;

                for (int i = 0; i < estudiantesPorDia && asistenciasGeneradas < minimoAsistencias; i++) {
                    int estudianteIndex = asistenciasGeneradas % estudiantes.size();
                    int estudianteId = estudiantes.get(estudianteIndex);

                    // Generar hora de marcado realista dentro del horario [horaInicio,
                    // horaInicio+2h]
                    String horaMarcada = generarHoraMarcada(horaInicio, estrategia, tolerancia);
                    String estado = calcularEstadoSegunEstrategia(horaInicio, horaMarcada, estrategia, tolerancia);

                    if (!firstEntry)
                        sql.append(", ");
                    sql.append(String.format("(%d, %d, '%s', '%s', '%s')",
                            estudianteId, grupoId, fecha, horaMarcada, estado));
                    firstEntry = false;
                    asistenciasGeneradas++;
                }
            }

            // Si aún no tenemos 5 asistencias, agregar más en el primer día
            if (asistenciasGeneradas < 5 && fechas.length > 0) {
                for (int i = asistenciasGeneradas; i < Math.min(5, estudiantes.size()); i++) {
                    int estudianteIndex = i % estudiantes.size();
                    int estudianteId = estudiantes.get(estudianteIndex);
                    String horaMarcada = generarHoraMarcada(horaInicio, estrategia, tolerancia);
                    String estado = calcularEstadoSegunEstrategia(horaInicio, horaMarcada, estrategia, tolerancia);

                    if (!firstEntry)
                        sql.append(", ");
                    sql.append(String.format("(%d, %d, '%s', '%s', '%s')",
                            estudianteId, grupoId, fechas[0], horaMarcada, estado));
                    firstEntry = false;
                }
            }
        }

        try {
            db.execSQL(sql.toString());
            Log.d(TAG,
                    "Asistencias de prueba insertadas - al menos 5 por grupo de la semana pasada (20-24 enero 2025)");
        } catch (Exception e) {
            Log.w(TAG, "Error al insertar asistencias: " + e.getMessage(), e);
            throw new RuntimeException("Error al insertar asistencias", e);
        }
    }

    /**
     * Obtiene los IDs de estudiantes inscritos en un grupo
     */
    private static List<Integer> obtenerEstudiantesInscritosEnGrupo(SQLiteDatabase db, int grupoId) {
        List<Integer> estudiantes = new ArrayList<>();
        String sql = "SELECT DISTINCT alumno_id FROM boletas WHERE grupo_id = ?";

        try (Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(grupoId) })) {
            while (cursor.moveToNext()) {
                estudiantes.add(cursor.getInt(0));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener estudiantes del grupo " + grupoId, e);
        }

        return estudiantes;
    }

    /**
     * Genera una hora de marcado realista basada en la hora de inicio y la
     * estrategia
     */
    private static String generarHoraMarcada(String horaInicio, String estrategia, int tolerancia) {
        String[] partes = horaInicio.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);

        // Agregar minutos aleatorios según estrategia para generar diferentes estados
        int minutosAgregados = 0;
        switch (estrategia) {
            case "PRESENTE":
                // Mayoría de llegadas puntuales (0-10 min)
                minutosAgregados = (int) (Math.random() * 10);
                break;
            case "RETRASO":
                // Mix de puntuales y retrasos (0-30 min)
                minutosAgregados = (int) (Math.random() * 30);
                break;
            case "FALTA":
                // Mix incluyendo faltas (0-45 min, algunos dentro de tolerancia, otros no)
                minutosAgregados = (int) (Math.random() * 45);
                break;
        }

        minutos += minutosAgregados;
        if (minutos >= 60) {
            horas += minutos / 60;
            minutos = minutos % 60;
        }
        if (horas >= 24)
            horas = 23; // No exceder 23:59

        return String.format("%02d:%02d", horas, minutos);
    }

    /**
     * Calcula el estado de asistencia según la estrategia configurada
     */
    private static String calcularEstadoSegunEstrategia(String horaInicio, String horaMarcada, String estrategia,
            int tolerancia) {
        String[] partesInicio = horaInicio.split(":");
        String[] partesMarcada = horaMarcada.split(":");

        int minutosInicio = Integer.parseInt(partesInicio[0]) * 60 + Integer.parseInt(partesInicio[1]);
        int minutosMarcada = Integer.parseInt(partesMarcada[0]) * 60 + Integer.parseInt(partesMarcada[1]);

        // Hora fin es 2 horas después de inicio
        int minutosFin = minutosInicio + 120;

        // Verificar que esté dentro del horario de clase
        if (minutosMarcada < minutosInicio || minutosMarcada > minutosFin) {
            // No debería pasar si generamos horas correctamente, pero por seguridad
            return "FALTA";
        }

        int diferencia = minutosMarcada - minutosInicio;

        switch (estrategia) {
            case "PRESENTE":
                // Estrategia flexible: siempre PRESENTE si está dentro del horario
                return "PRESENTE";

            case "RETRASO":
                // Estrategia estándar
                if (diferencia < 0) {
                    return "PRESENTE"; // Llegó antes
                } else if (diferencia <= tolerancia) {
                    return "RETRASO"; // 0-tolerancia min
                } else {
                    return "RETRASO"; // >tolerancia pero dentro del horario
                }

            case "FALTA":
                // Estrategia estricta
                if (diferencia < 0) {
                    return "PRESENTE"; // Llegó antes
                } else if (diferencia <= 10) {
                    return "PRESENTE"; // 0-10 min
                } else if (diferencia <= tolerancia) {
                    return "RETRASO"; // 11-tolerancia min
                } else {
                    return "FALTA"; // >tolerancia
                }

            default:
                return "PRESENTE";
        }
    }
}
