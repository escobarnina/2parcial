# Sistema de Gestión de Asistencias - Arquinuevo

Aplicación Android nativa desarrollada en Java para la gestión de asistencias académicas, implementando arquitectura de 3 capas y patrones de diseño.

## 📋 Descripción

Aplicación Android desarrollada con Java que permite gestionar asistencias de estudiantes, materias, grupos, horarios e inscripciones. El sistema utiliza base de datos local (SQLite) y permite exportar reportes en formatos Excel y PDF. La interfaz utiliza Android Views tradicionales y Jetpack Compose para componentes modernos.

## 🏗️ Arquitectura

El proyecto sigue una **arquitectura de 3 capas** adaptada para Android:

```
com.arquitectura.asistente/
├── datos/              # Capa de Datos
│   ├── adapter/        # Adaptadores para exportación (Excel, PDF)
│   ├── database/       # Gestión de base de datos (SQLite/MySQL)
│   └── [Modelos]       # Modelos de datos (Usuario, Materia, Grupo, etc.)
│
├── negocio/            # Capa de Negocio
│   ├── strategy/       # Patrones Strategy para cálculo de asistencias
│   └── [Casos de Uso]  # Lógica de negocio (AsistenciaCU, ExportarAsistenciaCU)
│
└── presentacion/       # Capa de Presentación
    ├── widget/         # Adaptadores para RecyclerView
    └── [Activities]    # Activities y componentes de UI (Android Views + Compose)
```

### Separación de Responsabilidades

- **Capa de Datos**: Modelos de dominio, acceso a base de datos (SQLite/MySQL), adaptadores para exportación
- **Capa de Negocio**: Casos de uso que implementan la lógica de negocio, estrategias para cálculo de asistencias
- **Capa de Presentación**: Activities, adaptadores de UI, componentes visuales (Android Views y Jetpack Compose)

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
  - Los archivos se guardan en la carpeta de Descargas con nombre único (incluye fecha y hora)
  - Diálogo de confirmación con información del archivo exportado
  - Botón para abrir el explorador de archivos en la carpeta de Descargas
- 🎯 **Patrón Strategy**: Cálculo flexible de estados de asistencia (PRESENTE, RETRASO, FALTA)
- 🔌 **Patrón Adapter**: Exportación a múltiples formatos sin modificar código cliente
- 📅 **Validación de Horarios**: Verificación estricta de días y horas para marcar asistencia
  - Las estrategias retornan `null` cuando se intenta marcar fuera del horario de clase
  - No se registra asistencia fuera del horario establecido
  - Diálogo informativo cuando se intenta marcar fuera de horario
- 📱 **Interfaz Moderna**: Uso de Material Design y Jetpack Compose
  - Tarjetas de grupos con información de horarios y días visibles
  - Diseño con Material Design y esquema de colores destacado
  - BottomSheetDialog moderno para opciones de exportación

## 🛠️ Tecnologías Utilizadas

### Lenguajes y Frameworks
- **Java 11**: Lenguaje principal de programación
- **Kotlin**: Lenguaje secundario (para Compose y algunas utilidades)
- **Android SDK**: Framework de desarrollo móvil
- **Jetpack Compose**: Framework moderno de UI declarativa
- **Android Views**: Componentes tradicionales de UI (Activities, RecyclerView, etc.)

### Bibliotecas y Herramientas
- **Gradle**: Sistema de construcción
- **SQLite**: Base de datos local (o MySQL remoto)
- **Apache POI**: Generación de archivos Excel
- **iText 7**: Generación de archivos PDF
- **AndroidX Libraries**: AppCompat, Material Design, RecyclerView, CardView
- **JUnit**: Framework de pruebas

## 📦 Dependencias

Las dependencias principales se encuentran en `app/build.gradle`:

```gradle
dependencies {
    // AndroidX Core
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
    
    // Jetpack Compose
    implementation platform(libs.androidx.compose.bom)
    implementation libs.androidx.compose.ui
    implementation libs.androidx.compose.ui.graphics
    implementation libs.androidx.compose.ui.tooling.preview
    implementation libs.androidx.compose.material3
    implementation libs.androidx.activity.compose
    implementation libs.androidx.lifecycle.runtime.ktx
    
    // Exportación de datos
    implementation 'com.itextpdf:itext7-core:7.2.5'
    implementation 'org.apache.poi:poi-ooxml:5.2.4'
    
    // Testing
    testImplementation libs.junit
    androidTestImplementation libs.androidx.junit
    androidTestImplementation libs.androidx.espresso.core
    androidTestImplementation platform(libs.androidx.compose.bom)
    androidTestImplementation libs.androidx.compose.ui.test.junit4
}
```

## 🚀 Requisitos Previos

- **Android Studio** (Hedgehog | 2023.1.1 o superior)
- **JDK 11** o superior
- **Android SDK** (API 33 mínimo, API 36 target)
- **Gradle 8.6** o superior (incluido en el proyecto)
- **Dispositivo Android** o **Emulador** con Android 13+ (API 33+)

## 📥 Instalación

1. **Clonar el repositorio**
   ```bash
   git clone <url-del-repositorio>
   cd AsistenciaArqui
   ```

2. **Abrir el proyecto en Android Studio**
   - Abrir Android Studio
   - Seleccionar "Open an Existing Project"
   - Navegar a la carpeta del proyecto y seleccionarla
   - Esperar a que Gradle sincronice las dependencias

3. **Configurar la base de datos**
   - Si usa SQLite: La base de datos se crea automáticamente en el dispositivo
   - Si usa MySQL: Actualizar las credenciales en `DatabaseHelper.java` o usar un archivo de configuración

4. **Compilar el proyecto**
   ```bash
   ./gradlew build
   ```
   
   O desde Android Studio: `Build > Make Project`

5. **Ejecutar la aplicación**
   - Conectar un dispositivo Android o iniciar un emulador
   - Desde Android Studio: Click en el botón "Run" (▶️)
   - Desde terminal:
     ```bash
     ./gradlew installDebug
     ```

## 🗄️ Configuración de Base de Datos

El sistema puede usar SQLite (base de datos local) o MySQL (base de datos remota). Las tablas se crean automáticamente al ejecutar la aplicación por primera vez mediante `DatabaseMigrations`.

### Estructura de Tablas

- `usuarios`: Estudiantes y docentes
- `materias`: Materias académicas
- `grupos`: Grupos de materias asignados a docentes
- `horarios`: Horarios de clases por grupo
- `boletas`: Inscripciones de estudiantes en grupos
- `asistencias`: Registros de asistencia

## 🎨 Patrones de Diseño Implementados

### 1. Arquitectura de 3 Capas
- **Capa de Datos** (`datos/`): Modelos, acceso a BD, adaptadores de exportación
- **Capa de Negocio** (`negocio/`): Casos de uso y lógica de negocio
- **Capa de Presentación** (`presentacion/`): Activities, adaptadores de UI, componentes visuales

### 2. Patrón Strategy
Implementado en `negocio/strategy/` para calcular el estado de asistencia:
- `EstrategiaPresente`: Política flexible
- `EstrategiaRetraso`: Política estándar (por defecto)
- `EstrategiaFalta`: Política estricta
- `IEstrategiaAsistencia`: Interface común

**Validación de Horario**: Todas las estrategias validan que la asistencia se marque dentro del horario de clase (`[horaInicio, horaFin]`). Si se intenta marcar fuera del horario, las estrategias retornan `null` y no se registra la asistencia, mostrando un diálogo informativo al usuario.

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
│   │   │   ├── java/com/arquitectura/asistente/
│   │   │   │   ├── datos/              # Capa de datos
│   │   │   │   │   ├── adapter/        # Adaptadores de exportación
│   │   │   │   │   └── database/       # Gestión de BD
│   │   │   │   ├── negocio/            # Capa de negocio
│   │   │   │   │   └── strategy/       # Estrategias de cálculo
│   │   │   │   ├── presentacion/       # Capa de presentación
│   │   │   │   │   └── widget/         # Adaptadores de UI
│   │   │   │   └── MainActivity.java   # Activity principal
│   │   │   ├── res/                    # Recursos Android
│   │   │   │   ├── layout/             # Layouts XML
│   │   │   │   ├── values/             # Strings, colors, etc.
│   │   │   │   └── drawable/          # Imágenes y drawables
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/               # Pruebas instrumentadas
│   │   └── test/                      # Pruebas unitarias
│   ├── build.gradle                   # Configuración del módulo app
│   └── proguard-rules.pro
├── gradle/
│   ├── wrapper/
│   └── libs.versions.toml
├── .gitignore
├── README.md
├── build.gradle                       # Configuración del proyecto
├── settings.gradle
├── gradle.properties
├── gradlew
└── gradlew.bat
```

## 🧪 Ejecutar Pruebas

### Pruebas Unitarias
```bash
./gradlew test
```

### Pruebas Instrumentadas (Android)
```bash
./gradlew connectedAndroidTest
```

### Desde Android Studio
- Pruebas unitarias: Click derecho en la clase de prueba > "Run"
- Pruebas instrumentadas: Click derecho en la clase de prueba > "Run"

## 📝 Uso

1. **Iniciar la aplicación**: Ejecutar desde Android Studio o instalar el APK
2. **Gestionar asistencias**: Usar la interfaz de la aplicación para registrar asistencias
3. **Exportar reportes**: Seleccionar grupo y formato (Excel/PDF) para exportar desde la aplicación

## 👥 Roles del Sistema

- **Estudiante**: Puede ver sus asistencias e inscribirse en grupos
- **Docente**: Puede gestionar grupos, horarios y marcar asistencias
- **Administrador**: Acceso completo al sistema

## 🔒 Seguridad

- Las contraseñas deben almacenarse de forma segura (hash)
- Las conexiones a MySQL deben usar SSL en producción
- No versionar archivos de credenciales (ver `.gitignore`)
- Usar ProGuard/R8 para ofuscar el código en producción

## 🐛 Solución de Problemas

### Error de conexión a base de datos
- Verificar que la base de datos esté configurada correctamente
- Comprobar credenciales en `DatabaseHelper`
- Verificar permisos de red si usa MySQL remoto

### Error al exportar
- Verificar permisos de almacenamiento en AndroidManifest.xml
- Comprobar que las dependencias estén instaladas
- Verificar permisos de escritura en el dispositivo

### Problemas de compilación
- Limpiar el proyecto: `./gradlew clean`
- Invalidar cachés en Android Studio: `File > Invalidate Caches / Restart`
- Sincronizar Gradle: `File > Sync Project with Gradle Files`

## 📱 Compatibilidad

- **Versión mínima de Android**: API 33 (Android 13)
- **Versión objetivo**: API 36 (Android 15)
- **Arquitecturas soportadas**: armeabi-v7a, arm64-v8a, x86, x86_64

## 📄 Licencia

Este proyecto es de uso académico.

## 👨‍💻 Autor

Desarrollado como proyecto académico de Arquitectura de Software.

## 📚 Recursos Adicionales

- [Documentación de Android](https://developer.android.com/)
- [Documentación de Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Documentación de Gradle](https://docs.gradle.org/)
- [Documentación de Apache POI](https://poi.apache.org/)
- [Documentación de iText](https://itextpdf.com/)

---

**Versión**: 2.1.0  
**Última actualización**: 2025  
**Plataforma**: Android (Nativo)

### Cambios Recientes (v2.1.0)
- ✅ Validación mejorada de horarios: Las estrategias retornan `null` cuando se intenta marcar asistencia fuera del horario de clase
- ✅ Diálogo informativo cuando se intenta marcar asistencia fuera del horario
- ✅ Mejora en la UI: Las tarjetas de grupos ahora muestran horarios y días de clase
- ✅ Diseño mejorado con Material Design y esquema de colores destacado
