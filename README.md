# Sistema de Gestión de Asistencias - Arquinuevo

Sistema de escritorio desarrollado en Java para la gestión de asistencias académicas, implementando arquitectura de 3 capas y patrones de diseño.

## 📋 Descripción

Aplicación Java Desktop desarrollada con Swing que permite gestionar asistencias de estudiantes, materias, grupos, horarios e inscripciones. El sistema utiliza MySQL como base de datos y permite exportar reportes en formatos Excel y PDF.

## 🏗️ Arquitectura

El proyecto sigue una **arquitectura de 3 capas**:

```
Arquinuevo/
├── datos/          # Capa de Datos
│   ├── models/     # Modelos de datos (Usuario, Materia, Grupo, etc.)
│   ├── repository/ # Repositorios para acceso a datos
│   ├── adapter/    # Adaptadores para exportación (Excel, PDF)
│   └── database/   # Gestión de base de datos (MySQL)
│
├── negocio/        # Capa de Negocio
│   ├── UseCases/   # Casos de uso (lógica de negocio)
│   └── strategy/   # Patrones Strategy para cálculo de asistencias
│
└── presentacion/   # Capa de Presentación
    └── Forms/      # Interfaces gráficas (Swing)
```

## 🎯 Características Principales

### Gestión de Entidades
- ✅ **Usuarios**: Gestión de estudiantes y docentes
- ✅ **Materias**: Administración de materias académicas
- ✅ **Grupos**: Creación y gestión de grupos de materias
- ✅ **Horarios**: Configuración de horarios de clases
- ✅ **Inscripciones**: Gestión de inscripciones de estudiantes
- ✅ **Asistencias**: Registro y consulta de asistencias

### Funcionalidades Avanzadas
- 📊 **Exportación de Reportes**: Exportar asistencias a Excel (.xlsx) y PDF
- 🎯 **Patrón Strategy**: Cálculo flexible de estados de asistencia (PRESENTE, RETRASO, FALTA)
- 🔌 **Patrón Adapter**: Exportación a múltiples formatos sin modificar código cliente
- 📅 **Validación de Horarios**: Verificación de días y horas para marcar asistencia

## 🛠️ Tecnologías Utilizadas

- **Java 17**: Lenguaje de programación
- **Swing**: Interfaz gráfica de usuario
- **MySQL**: Base de datos relacional
- **Gradle**: Sistema de construcción
- **Apache POI**: Generación de archivos Excel
- **iText 7**: Generación de archivos PDF
- **JUnit 5**: Framework de pruebas

## 📦 Dependencias

Las dependencias principales se encuentran en `app/build.gradle`:

```gradle
dependencies {
    // MySQL JDBC Driver
    implementation 'com.mysql:mysql-connector-j:8.2.0'
    
    // Apache POI para Excel
    implementation 'org.apache.poi:poi:5.2.3'
    implementation 'org.apache.poi:poi-ooxml:5.2.3'
    
    // iText para PDF
    implementation 'com.itextpdf:itext7-core:7.2.5'
    implementation 'com.itextpdf:kernel:7.2.5'
    implementation 'com.itextpdf:layout:7.2.5'
    
    // Guava
    implementation libs.guava
    
    // JUnit para pruebas
    testImplementation libs.junit.jupiter
}
```

## 🚀 Requisitos Previos

- **Java 17** o superior
- **MySQL 8.0** o superior
- **Gradle 8.6** o superior (incluido en el proyecto)

## 📥 Instalación

1. **Clonar el repositorio**
   ```bash
   git clone <url-del-repositorio>
   cd AsistenciaArqui
   ```

2. **Configurar la base de datos MySQL**
   - Crear una base de datos MySQL
   - Actualizar las credenciales en `DatabaseHelper.java` o usar un archivo de configuración

3. **Compilar el proyecto**
   ```bash
   ./gradlew build
   ```

4. **Ejecutar la aplicación**
   ```bash
   ./gradlew run
   ```
   
   O en Windows:
   ```bash
   gradlew.bat run
   ```

## 🗄️ Configuración de Base de Datos

El sistema requiere una base de datos MySQL. Las tablas se crean automáticamente al ejecutar la aplicación por primera vez mediante `DatabaseMigrations`.

### Estructura de Tablas

- `usuarios`: Estudiantes y docentes
- `materias`: Materias académicas
- `grupos`: Grupos de materias asignados a docentes
- `horarios`: Horarios de clases por grupo
- `boletas`: Inscripciones de estudiantes en grupos
- `asistencias`: Registros de asistencia

## 🎨 Patrones de Diseño Implementados

### 1. Arquitectura de 3 Capas
- **Capa de Datos**: Modelos, repositorios y acceso a BD
- **Capa de Negocio**: Casos de uso y lógica de negocio
- **Capa de Presentación**: Interfaces gráficas

### 2. Patrón Strategy
Implementado en `negocio/strategy/` para calcular el estado de asistencia:
- `EstrategiaPresente`: Política flexible
- `EstrategiaRetraso`: Política estándar (por defecto)
- `EstrategiaFalta`: Política estricta

### 3. Patrón Adapter
Implementado en `datos/adapter/` para exportación:
- `AsistenciaExcelAdapter`: Adapta Apache POI para Excel
- `AsistenciaPDFAdapter`: Adapta iText para PDF
- `DataExportAdapter`: Interface común

## 📁 Estructura del Proyecto

```
AsistenciaArqui/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── Arquinuevo/
│   │   │   │       ├── datos/          # Capa de datos
│   │   │   │       ├── negocio/        # Capa de negocio
│   │   │   │       └── presentacion/   # Capa de presentación
│   │   │   └── resources/
│   │   └── test/
│   └── build.gradle
├── gradle/
│   └── wrapper/
├── .gitignore
├── README.md
├── settings.gradle
├── gradlew
└── gradlew.bat
```

## 🧪 Ejecutar Pruebas

```bash
./gradlew test
```

## 📝 Uso

1. **Iniciar la aplicación**: Ejecutar `./gradlew run`
2. **Gestionar asistencias**: Usar la interfaz gráfica para registrar asistencias
3. **Exportar reportes**: Seleccionar grupo y formato (Excel/PDF) para exportar

## 👥 Roles del Sistema

- **Estudiante**: Puede ver sus asistencias e inscribirse en grupos
- **Docente**: Puede gestionar grupos, horarios y marcar asistencias
- **Administrador**: Acceso completo al sistema

## 🔒 Seguridad

- Las contraseñas deben almacenarse de forma segura (hash)
- Las conexiones a MySQL deben usar SSL en producción
- No versionar archivos de credenciales (ver `.gitignore`)

## 🐛 Solución de Problemas

### Error de conexión a MySQL
- Verificar que MySQL esté ejecutándose
- Comprobar credenciales en `DatabaseHelper`
- Verificar que la base de datos exista

### Error al exportar
- Verificar que las dependencias estén instaladas
- Comprobar permisos de escritura en el directorio de salida

## 📄 Licencia

Este proyecto es de uso académico.

## 👨‍💻 Autor

Desarrollado como proyecto académico de Arquitectura de Software.

## 📚 Recursos Adicionales

- [Documentación de Gradle](https://docs.gradle.org/)
- [Documentación de Apache POI](https://poi.apache.org/)
- [Documentación de iText](https://itextpdf.com/)
- [Documentación de MySQL](https://dev.mysql.com/doc/)

---

**Versión**: 1.0.0  
**Última actualización**: 2025

