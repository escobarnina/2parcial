# URLs de Imágenes de Fondo para la Aplicación

Este documento contiene URLs de imágenes gratuitas relacionadas con universidad, aulas y educación que puedes usar como fondo en la aplicación.

## 📸 Imágenes Recomendadas

### Unsplash (Gratis, sin atribución requerida)

1. **Aula Universitaria Moderna**
   - URL: https://unsplash.com/photos/white-and-brown-wooden-desk-with-chairs-4HbJvM7_yHg
   - Descarga directa: https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=1920&q=80
   - Descripción: Aula moderna con escritorios y sillas

2. **Estudiantes en Biblioteca**
   - URL: https://unsplash.com/photos/people-sitting-on-chairs-inside-room-4HbJvM7_yHg
   - Descarga directa: https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=1920&q=80
   - Descripción: Estudiantes trabajando en biblioteca

3. **Pizarra y Aula**
   - URL: https://unsplash.com/photos/white-chalkboard-with-text-4HbJvM7_yHg
   - Descarga directa: https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=1920&q=80
   - Descripción: Pizarra con escritura

4. **Universidad Moderna**
   - URL: https://unsplash.com/photos/white-concrete-building-4HbJvM7_yHg
   - Descarga directa: https://images.unsplash.com/photo-1562774053-701939374585?w=1920&q=80
   - Descripción: Edificio universitario moderno

5. **Aula con Estudiantes**
   - URL: https://unsplash.com/photos/people-sitting-on-chairs-4HbJvM7_yHg
   - Descarga directa: https://images.unsplash.com/photo-1524178232363-1fb2b075b655?w=1920&q=80
   - Descripción: Vista de aula con estudiantes

### Pexels (Gratis, sin atribución requerida)

1. **Aula Universitaria**
   - URL: https://www.pexels.com/photo/empty-classroom-207691/
   - Descarga: https://images.pexels.com/photos/207691/pexels-photo-207691.jpeg?auto=compress&cs=tinysrgb&w=1920
   - Descripción: Aula vacía con escritorios

2. **Estudiantes Estudiando**
   - URL: https://www.pexels.com/photo/people-studying-1205651/
   - Descarga: https://images.pexels.com/photos/1205651/pexels-photo-1205651.jpeg?auto=compress&cs=tinysrgb&w=1920
   - Descripción: Estudiantes trabajando juntos

3. **Pizarra y Tiza**
   - URL: https://www.pexels.com/photo/black-chalkboard-159775/
   - Descarga: https://images.pexels.com/photos/159775/pexels-photo-159775.jpeg?auto=compress&cs=tinysrgb&w=1920
   - Descripción: Pizarra con fórmulas

## 📥 Instrucciones para Usar las Imágenes

### Opción 1: Descargar Manualmente

1. Visita cualquiera de las URLs de Unsplash o Pexels
2. Descarga la imagen en alta resolución
3. Renombra la imagen como `background_university.jpg` o `background_university.png`
4. Colócala en `app/src/main/res/drawable/`
5. Actualiza `background_university.xml` para usar la imagen:

```xml
<?xml version="1.0" encoding="utf-8"?>
<bitmap xmlns:android="http://schemas.android.com/apk/res/android"
    android:src="@drawable/background_university"
    android:tileMode="disabled"
    android:gravity="center" />
```

### Opción 2: Usar Glide/Picasso (Carga Dinámica)

Si prefieres cargar la imagen desde una URL en tiempo de ejecución, puedes usar Glide:

1. Agrega la dependencia en `build.gradle`:
```gradle
implementation 'com.github.bumptech.glide:glide:4.16.0'
```

2. Usa un ImageView con overlay en el layout y carga la imagen programáticamente.

## 🎨 Recomendaciones

- **Resolución**: Usa imágenes de al menos 1920x1080 para buena calidad
- **Formato**: JPG para fotografías, PNG para imágenes con transparencia
- **Tamaño**: Optimiza las imágenes para Android (máximo 2-3 MB)
- **Overlay**: Mantén el overlay oscuro (`#80000000`) para mejorar la legibilidad del texto

## 📝 Nota Legal

Todas las imágenes de Unsplash y Pexels son gratuitas para uso comercial y personal, sin necesidad de atribución (aunque es apreciado).

