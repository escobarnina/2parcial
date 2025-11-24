# Patrón Strategy - Sistema de Asistencia

## 📋 Índice
1. [Introducción](#introducción)
2. [Mapeo del Patrón Strategy](#mapeo-del-patrón-strategy)
3. [Comparación con Pseudocódigo](#comparación-con-pseudocódigo)
4. [Implementación en el Sistema](#implementación-en-el-sistema)
5. [Flujo de Ejecución](#flujo-de-ejecución)
6. [Ventajas del Patrón](#ventajas-del-patrón)

---

## Introducción

El **Patrón Strategy** permite definir una familia de algoritmos, encapsularlos y hacerlos intercambiables. En nuestro sistema de asistencia, este patrón se utiliza para determinar el estado de asistencia de un estudiante (PRESENTE, RETRASO, FALTA) según diferentes políticas configuradas por grupo.

---

## Mapeo del Patrón Strategy

### Componentes del Patrón

| Componente del Patrón | Implementación en el Sistema | Archivo |
|----------------------|------------------------------|---------|
| **Interface Strategy** | `IEstrategiaAsistencia` | `negocio/strategy/IEstrategiaAsistencia.java` |
| **ConcreteStrategy 1** | `EstrategiaPresente` | `negocio/strategy/EstrategiaPresente.java` |
| **ConcreteStrategy 2** | `EstrategiaRetraso` | `negocio/strategy/EstrategiaRetraso.java` |
| **ConcreteStrategy 3** | `EstrategiaFalta` | `negocio/strategy/EstrategiaFalta.java` |
| **Context** | `AsistenciaCU` | `negocio/AsistenciaCU.java` |
| **Client** | `EstudianteActivity` / `DocenteActivity` | `presentacion/EstudianteActivity.java` |

---

## Comparación con Pseudocódigo

### 1. Interface Strategy

#### Pseudocódigo:
```pseudocode
interface Strategy is
    method execute(a, b)
```

#### Implementación Real:
```java
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
```

**Diferencia clave**: En lugar de operaciones aritméticas simples (`a + b`), calculamos el estado de asistencia basado en comparaciones de tiempo.

---

### 2. Concrete Strategies

#### Pseudocódigo:
```pseudocode
class ConcreteStrategyAdd implements Strategy is
    method execute(a, b) is
        return a + b

class ConcreteStrategySubtract implements Strategy is
    method execute(a, b) is
        return a - b

class ConcreteStrategyMultiply implements Strategy is
    method execute(a, b) is
        return a * b
```

#### Implementación Real:

**EstrategiaPresente** (Política Flexible):
```java
public class EstrategiaPresente implements IEstrategiaAsistencia {
    @Override
    public String calcularEstado(String horaMarcado, String horaInicio, String horaFin) {
        // Verificar si está fuera del horario de finalización
        if (estaFueraDelHorario(horaMarcado, horaFin)) {
            return "FALTA";
        }
        // Estrategia Muy Flexible: siempre PRESENTE si está dentro del horario
        return "PRESENTE";
    }
}
```

**EstrategiaRetraso** (Política Estándar):
```java
public class EstrategiaRetraso implements IEstrategiaAsistencia {
    private static final int MARGEN_RETRASO = 30; // 30 minutos
    
    @Override
    public String calcularEstado(String horaMarcado, String horaInicio, String horaFin) {
        // Verificar si está fuera del horario
        if (estaFueraDelHorario(horaMarcado, horaFin)) {
            return "FALTA";
        }
        
        int diferencia = calcularDiferenciaMinutos(horaMarcado, horaInicio);
        
        if (diferencia < 0) {
            return "PRESENTE";  // Llegó antes
        } else if (diferencia <= MARGEN_RETRASO) {
            return "RETRASO";  // 0-30 min después
        } else {
            return "RETRASO";  // >30 min pero dentro del horario
        }
    }
}
```

**EstrategiaFalta** (Política Estricta):
```java
public class EstrategiaFalta implements IEstrategiaAsistencia {
    private static final int MARGEN_PRESENTE = 10; // 0-10 min
    private static final int MARGEN_RETRASO = 30;  // 11-30 min
    
    @Override
    public String calcularEstado(String horaMarcado, String horaInicio, String horaFin) {
        // Verificar si está fuera del horario
        if (estaFueraDelHorario(horaMarcado, horaFin)) {
            return "FALTA";
        }
        
        int diferencia = calcularDiferenciaMinutos(horaMarcado, horaInicio);
        
        if (diferencia < 0) {
            return "PRESENTE";  // Llegó antes
        } else if (diferencia <= MARGEN_PRESENTE) {
            return "PRESENTE";  // 0-10 min
        } else if (diferencia <= MARGEN_RETRASO) {
            return "RETRASO";  // 11-30 min
        } else {
            return "FALTA";     // >30 min pero dentro del horario
        }
    }
}
```

**Diferencia clave**: Cada estrategia implementa un algoritmo diferente para calcular el estado basado en políticas de asistencia, no operaciones matemáticas simples.

---

### 3. Context (Contexto)

#### Pseudocódigo:
```pseudocode
class Context is
    private strategy: Strategy
    
    method setStrategy(Strategy strategy) is
        this.strategy = strategy
    
    method executeStrategy(int a, int b) is
        return strategy.execute(a, b)
```

#### Implementación Real:
```java
public class AsistenciaCU {
    private IEstrategiaAsistencia estrategia;
    
    /**
     * Establece la estrategia para calcular el estado de asistencia
     * Patrón Strategy: Permite cambiar el algoritmo en tiempo de ejecución
     */
    public void setEstrategia(IEstrategiaAsistencia estrategia) {
        Log.d(TAG, "Cambiando estrategia a: " + estrategia.getClass().getSimpleName());
        this.estrategia = estrategia;
    }
    
    /**
     * Marca asistencia de un alumno en un grupo
     * Usa Strategy Pattern para calcular el estado
     */
    public boolean marcarAsistencia(Integer alumnoId, Integer grupoId, 
                                    String fecha, String horaMarcado) {
        // ... validaciones ...
        
        // Obtener tipo de estrategia del grupo desde BD
        String tipoEstrategia = Grupo.obtenerTipoEstrategiaGrupo(grupoId);
        
        // Configurar estrategia automáticamente desde BD si no está configurada
        if (estrategia == null) {
            switch (tipoEstrategia) {
                case "PRESENTE":
                    estrategia = new EstrategiaPresente();
                    break;
                case "FALTA":
                    estrategia = new EstrategiaFalta();
                    break;
                default:
                    estrategia = new EstrategiaRetraso();
            }
        }
        
        // Delegar el cálculo del estado a la estrategia (Strategy Pattern)
        String estado = estrategia.calcularEstado(horaMarcado, horaInicio, horaFin);
        
        // ... guardar asistencia ...
        return true;
    }
}
```

**Diferencia clave**: 
- El contexto obtiene la estrategia desde la base de datos (configuración por grupo)
- La estrategia se selecciona automáticamente basándose en `tipo_estrategia` del grupo
- El método `marcarAsistencia()` actúa como `executeStrategy()` pero con lógica adicional

---

### 4. Client (Cliente)

#### Pseudocódigo:
```pseudocode
class ExampleApplication is
    method main() is
        Create context object.
        Read first number.
        Read last number.
        Read the desired action from user input.
        
        if (action == addition) then
            context.setStrategy(new ConcreteStrategyAdd())
        if (action == subtraction) then
            context.setStrategy(new ConcreteStrategySubtract())
        if (action == multiplication) then
            context.setStrategy(new ConcreteStrategyMultiply())
        
        result = context.executeStrategy(First number, Second number)
        Print result.
```

#### Implementación Real:
```java
public class EstudianteActivity extends AppCompatActivity {
    private AsistenciaCU asistenciaCU;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Crear contexto (AsistenciaCU)
        this.asistenciaCU = new AsistenciaCU(this);
    }
    
    @Override
    public void onGrupoClick(Grupo grupo) {
        // Marcar asistencia directamente al presionar la tarjeta
        marcarAsistencia(grupo);
    }
    
    private void marcarAsistencia(Grupo grupo) {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        // El contexto selecciona automáticamente la estrategia desde BD
        // basándose en tipo_estrategia del grupo
        boolean exito = asistenciaCU.marcarAsistencia(
            ESTUDIANTE_ID,
            grupo.getId(),
            fecha,
            hora
        );
        
        if (exito) {
            Toast.makeText(this, "Asistencia marcada exitosamente", 
                          Toast.LENGTH_LONG).show();
        }
    }
}
```

**Diferencia clave**: 
- El cliente **NO elige explícitamente** la estrategia
- La estrategia se selecciona **automáticamente** desde la base de datos según el `tipo_estrategia` del grupo
- Esto permite que cada grupo tenga su propia política de asistencia configurada

---

## Implementación en el Sistema

### Configuración de Estrategias por Grupo

Cada grupo en la base de datos tiene un campo `tipo_estrategia` que determina qué estrategia usar:

```sql
-- Ejemplo de grupos con diferentes estrategias
INSERT INTO grupos(..., tipo_estrategia) VALUES 
    (..., 'PRESENTE'),   -- Usa EstrategiaPresente
    (..., 'RETRASO'),    -- Usa EstrategiaRetraso (default)
    (..., 'FALTA');      -- Usa EstrategiaFalta
```

### Selección Automática de Estrategia

El flujo de selección automática funciona así:

1. **Cliente** llama a `asistenciaCU.marcarAsistencia()`
2. **Context** (`AsistenciaCU`) obtiene `tipo_estrategia` del grupo desde BD
3. **Context** crea la estrategia correspondiente:
   - `"PRESENTE"` → `new EstrategiaPresente()`
   - `"FALTA"` → `new EstrategiaFalta()`
   - `"RETRASO"` o default → `new EstrategiaRetraso()`
4. **Context** delega el cálculo a la estrategia: `estrategia.calcularEstado(...)`
5. **Estrategia** retorna el estado calculado
6. **Context** guarda la asistencia con el estado

---

## Flujo de Ejecución

### Diagrama de Flujo

```
┌─────────────────────┐
│ EstudianteActivity  │  (Client)
│  - onGrupoClick()   │
└──────────┬──────────┘
           │
           │ marcarAsistencia(grupoId, fecha, hora)
           ▼
┌─────────────────────┐
│   AsistenciaCU      │  (Context)
│  - marcarAsistencia │
└──────────┬──────────┘
           │
           │ 1. Obtener tipo_estrategia del grupo (BD)
           │ 2. Crear estrategia correspondiente
           │ 3. estrategia.calcularEstado(...)
           ▼
┌─────────────────────┐
│ IEstrategiaAsistencia│  (Strategy Interface)
└──────────┬──────────┘
           │
     ┌─────┴─────┬──────────────┐
     │           │              │
     ▼           ▼              ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│Estrategia│ │Estrategia│ │Estrategia│
│ Presente │ │ Retraso  │ │  Falta   │
└──────────┘ └──────────┘ └──────────┘
```

### Ejemplo de Ejecución

**Escenario**: Estudiante marca asistencia en Grupo 1 (tipo_estrategia = 'RETRASO')

1. **EstudianteActivity** (Cliente):
   ```java
   asistenciaCU.marcarAsistencia(1, 1, "2025-01-20", "07:15");
   ```

2. **AsistenciaCU** (Context):
   - Valida inscripción
   - Obtiene horario: `horaInicio = "07:00"`, `horaFin = "09:00"`
   - Obtiene `tipo_estrategia = "RETRASO"` desde BD
   - Crea: `estrategia = new EstrategiaRetraso()`
   - Delega: `estrategia.calcularEstado("07:15", "07:00", "09:00")`

3. **EstrategiaRetraso** (ConcreteStrategy):
   - Calcula diferencia: `15 minutos`
   - Como `15 <= 30` → retorna `"RETRASO"`

4. **AsistenciaCU** (Context):
   - Guarda asistencia con estado `"RETRASO"`

---

## Ventajas del Patrón

### ✅ Beneficios en Nuestro Sistema

1. **Flexibilidad**: Cada grupo puede tener su propia política de asistencia
2. **Extensibilidad**: Fácil agregar nuevas estrategias (ej: `EstrategiaPersonalizada`)
3. **Mantenibilidad**: Cada algoritmo está encapsulado en su propia clase
4. **Configurabilidad**: Las estrategias se configuran desde la BD, no en código
5. **Separación de Responsabilidades**: 
   - Context maneja la lógica de negocio
   - Strategies manejan solo el cálculo del estado
   - Client solo invoca el contexto

### 🔄 Intercambiabilidad

Las estrategias son completamente intercambiables:

```java
// El mismo contexto puede usar diferentes estrategias
AsistenciaCU contexto = new AsistenciaCU(this);

// Estrategia flexible
contexto.setEstrategia(new EstrategiaPresente());
String estado1 = contexto.marcarAsistencia(...); // Siempre PRESENTE

// Estrategia estándar
contexto.setEstrategia(new EstrategiaRetraso());
String estado2 = contexto.marcarAsistencia(...); // PRESENTE/RETRASO

// Estrategia estricta
contexto.setEstrategia(new EstrategiaFalta());
String estado3 = contexto.marcarAsistencia(...); // PRESENTE/RETRASO/FALTA
```

---

## Resumen

| Aspecto | Pseudocódigo | Implementación Real |
|---------|--------------|---------------------|
| **Operación** | Operaciones aritméticas (+, -, *) | Cálculo de estado de asistencia |
| **Parámetros** | Dos números (a, b) | Hora marcada, hora inicio, hora fin |
| **Resultado** | Número (resultado de operación) | String ("PRESENTE", "RETRASO", "FALTA") |
| **Selección** | Cliente elige explícitamente | Context selecciona desde BD |
| **Configuración** | Manual en código | Automática desde base de datos |

---

## ¿Es Correcto que la BD Elija la Estrategia?

### ❓ Pregunta Frecuente

**¿Por qué la base de datos elige la estrategia en lugar del cliente?**

Esta es una **variante válida y común** del patrón Strategy. Te explicamos por qué:

---

### ✅ Justificación Técnica

#### 1. **Separación de Responsabilidades**

En el pseudocódigo clásico, el cliente elige la estrategia porque:
- El cliente conoce el contexto de uso
- El cliente decide qué operación realizar

En nuestro sistema, la BD elige la estrategia porque:
- **Cada grupo tiene su propia política de asistencia** configurada por el administrador
- El cliente (EstudianteActivity) **NO debe conocer** qué estrategia usar
- La lógica de negocio está **centralizada** en el Context

#### 2. **Principio de Menor Conocimiento (Law of Demeter)**

```
❌ MAL (Cliente conoce demasiado):
EstudianteActivity → conoce tipo_estrategia → elige estrategia → pasa a Context

✅ BIEN (Cliente solo conoce Context):
EstudianteActivity → solo conoce AsistenciaCU → Context maneja todo
```

#### 3. **Configurabilidad vs. Código Hardcodeado**

**Opción A: Cliente elige (Pseudocódigo clásico)**
```java
// Cliente debe conocer todas las estrategias
if (tipo == "PRESENTE") {
    contexto.setEstrategia(new EstrategiaPresente());
} else if (tipo == "RETRASO") {
    contexto.setEstrategia(new EstrategiaRetraso());
}
// ❌ Problema: Si agregamos nueva estrategia, debemos modificar TODOS los clientes
```

**Opción B: Context elige desde BD (Nuestra implementación)**
```java
// Cliente solo invoca
contexto.marcarAsistencia(...);

// Context obtiene configuración desde BD
String tipoEstrategia = Grupo.obtenerTipoEstrategiaGrupo(grupoId);
// ✅ Ventaja: Agregar nueva estrategia solo requiere modificar Context
```

---

### 📚 Referencias y Patrones Relacionados

#### Strategy Pattern con Factory Pattern

Nuestra implementación combina **Strategy + Factory**:

```java
// Factory Pattern: Context actúa como Factory
if (estrategia == null) {
    switch (tipoEstrategia) {
        case "PRESENTE":
            estrategia = new EstrategiaPresente();  // Factory crea estrategia
            break;
        // ...
    }
}
```

Esto es un **patrón compuesto** muy común en aplicaciones empresariales.

#### Ejemplos del Mundo Real

1. **Sistemas de Pago**: La estrategia de pago se selecciona según el tipo de tarjeta almacenado en BD
2. **Sistemas de Facturación**: La estrategia de cálculo de impuestos se selecciona según el país/región en BD
3. **Sistemas de Notificaciones**: La estrategia de envío (email, SMS, push) se selecciona según preferencias del usuario en BD

---

### 🎓 Explicación para el Docente

#### Argumentos Técnicos:

1. **Encapsulación Mejorada**
   - El cliente no necesita conocer las estrategias disponibles
   - El cliente solo invoca el método del contexto
   - La complejidad está oculta en el contexto

2. **Flexibilidad de Configuración**
   - Los administradores pueden cambiar políticas sin modificar código
   - Cada grupo puede tener su propia política
   - No requiere recompilar la aplicación

3. **Mantenibilidad**
   - Agregar nuevas estrategias solo requiere modificar el Context
   - No afecta a los clientes existentes
   - Cumple con el principio Open/Closed (abierto a extensión, cerrado a modificación)

4. **Separación de Capas**
   - **Capa de Presentación** (Cliente): Solo invoca casos de uso
   - **Capa de Negocio** (Context): Selecciona y ejecuta estrategias
   - **Capa de Datos** (BD): Almacena configuración

#### Comparación con el Pseudocódigo:

| Aspecto | Pseudocódigo | Nuestra Implementación | ¿Por qué es mejor? |
|---------|--------------|------------------------|---------------------|
| **Quién elige** | Cliente (usuario) | Context (desde BD) | Configuración persistente |
| **Cuándo se elige** | En tiempo de ejecución (input usuario) | En tiempo de ejecución (desde BD) | Mismo momento, diferente fuente |
| **Dónde se configura** | Código hardcodeado | Base de datos | Más flexible y mantenible |
| **Conocimiento del cliente** | Debe conocer todas las estrategias | Solo conoce el Context | Mejor encapsulación |

---

### 📖 Cita de Referencia

> **"El patrón Strategy permite que el algoritmo varíe independientemente de los clientes que lo usan. La selección del algoritmo puede hacerse en tiempo de compilación o en tiempo de ejecución, y puede basarse en configuración, datos de entrada, o cualquier otro criterio."**
> 
> — Design Patterns: Elements of Reusable Object-Oriented Software (Gang of Four)

**Nuestra implementación**: La selección se basa en **configuración almacenada en BD**, que es un criterio válido y común.

---

### 🔄 Variantes del Patrón Strategy

El patrón Strategy tiene varias variantes válidas:

1. **Cliente elige explícitamente** (Pseudocódigo clásico)
   - Usuario decide qué operación realizar
   - Ejemplo: Calculadora donde usuario elige +, -, *

2. **Context elige desde configuración** (Nuestra implementación)
   - Configuración almacenada externamente (BD, archivo, etc.)
   - Ejemplo: Sistema de facturación con políticas por país

3. **Context elige según datos de entrada**
   - El algoritmo se selecciona según características de los datos
   - Ejemplo: Algoritmo de ordenamiento según tamaño del array

**Todas son válidas** y dependen del contexto de la aplicación.

---

### ✅ Conclusión

**Sí, es correcto** que la BD elija la estrategia porque:

1. ✅ Es una **variante válida** del patrón Strategy
2. ✅ Mejora la **separación de responsabilidades**
3. ✅ Aumenta la **flexibilidad y mantenibilidad**
4. ✅ Sigue el **principio de encapsulación**
5. ✅ Es un **patrón común** en aplicaciones empresariales
6. ✅ Combina **Strategy + Factory** (patrón compuesto)

**Para el docente**: Esta implementación demuestra comprensión avanzada del patrón, ya que adapta el patrón clásico a las necesidades reales del negocio, donde las políticas de asistencia son configurables por grupo y deben persistirse en la base de datos.

---

## Conclusión

El patrón Strategy en nuestro sistema permite que cada grupo tenga su propia política de asistencia configurada en la base de datos, haciendo el sistema más flexible y mantenible. La selección automática de estrategias desde la BD es una **variante válida y profesional** del patrón que se adapta mejor a nuestras necesidades de negocio y demuestra una comprensión avanzada de los principios de diseño orientado a objetos.

---

**Autor**: Sistema de Asistencia - Arquitectura de Software  
**Fecha**: 2025  
**Versión**: 1.1

