# Android AppFunctions Demo App 🚀 (Android 16 / API 36)

Una aplicación de demostración de alto nivel diseñada específicamente para explorar y probar la
nueva e innovadora **API de Jetpack AppFunctions** de Android 16.

El propósito principal de este proyecto es ilustrar cómo los asistentes de Inteligencia Artificial
en el dispositivo (como **Google Gemini**) pueden descubrir, entender y ejecutar lógica nativa
interna de tu aplicación en segundo plano (**headless**), sin necesidad de abrir la interfaz gráfica
ni interrumpir la experiencia del usuario.

---

## 🧠 El Héroe del Proyecto: Android 16 AppFunctions

En versiones anteriores de Android, los asistentes de voz requerían abrir la aplicación mediante
Deep Links para realizar acciones (por ejemplo, abrir la pantalla de creación de notas).

Con **AppFunctions**, tu aplicación se convierte en una caja de herramientas de capacidades
autodescritas directas para el sistema operativo:

```
                  ┌──────────────────────────────┐
                  │   Interacción del Usuario    │
                  │ (Voz/Texto con Gemini / IA)  │
                  └──────────────┬───────────────┘
                                 │
                                 ▼
                  ┌──────────────────────────────┐
                  │     Android 16 System        │
                  │   (Descubrimiento Semántico) │
                  └──────────────┬───────────────┘
                                 │ Invocación Headless
                                 ▼
 ┌──────────────────────────────────────────────────────────────┐
 │             com.example.appfunctionsdemo                     │
 │                                                              │
 │  ┌───────────────────────┐        ┌───────────────────────┐  │
 │  │ AppFunctionService    │        │ MainActivity (UI)     │  │
 │  │ (Proceso de Servicio) │        │ (Proceso Principal)   │  │
 │  │                       │        │                       │  │
 │  │  Escritura en SQLite  │        │   Observa Cambios     │  │
 │  │         Room          ├───────►│    con Room Flow      │  │
 │  └───────────────────────┘        └───────────────────────┘  │
 └──────────────────────────────────────────────────────────────┘
```

1. **Definición Semántica por KDoc**: La descripción en lenguaje natural de tus funciones (
   `isDescribedByKDoc = true`) es consumida directamente por la IA para decidir cuándo e invocar tu
   función basándose en lo que pida el usuario.
2. **Ejecución Headless**: El sistema operativo levanta la aplicación en un proceso de servicio de
   fondo (`AppFunctionService`) en milisegundos, ejecuta la función de forma aislada y devuelve el
   resultado en formato estructurado (JSON).

### Ejemplo Práctico: Exponer una función a la IA

Mira qué sencillo es exponer lógica de tu app a Gemini
en [NoteFunctions.kt](file:///Users/parquesl/Proyectos/Personal/AppFunctionsDemo/app/src/main/java/com/example/appfunctionsdemo/functions/NoteFunctions.kt):

```kotlin
/**
 * Crea una nueva nota y la persiste en la base de datos local.
 *
 * @param appFunctionContext Contexto de ejecución proporcionado por el sistema.
 * @param title Título de la nota.
 * @param content Cuerpo o contenido de la nota.
 * @return La nota recién creada con su identificador único asignado.
 */
@AppFunction(isDescribedByKDoc = true)
suspend fun createNote(
    appFunctionContext: AppFunctionContext,
    title: String,
    content: String
): Note = repository.create(title, content)
```

---

## 🚀 Guía de Pruebas: Simulación del Agente de IA paso a paso

Dado que el sistema operativo Android ejecuta las AppFunctions en segundo plano, podemos simular
cómo Gemini o el asistente de IA controlan nuestra app enviando comandos estructurados a través de *
*ADB** en la terminal de tu Mac/PC.

> [!IMPORTANT]
> Requisitos: Tener encendido un emulador con **Android 16 Developer Preview (API 36.1+)**, la app
> instalada y abierta en primer plano.

### 1. Listar las funciones expuestas e indexadas por el sistema

Comprueba que Android 16 ha descubierto con éxito las capacidades semánticas de nuestra app:

```bash
adb shell cmd app_function list-app-functions | grep com.example.appfunctionsdemo
```

*Esto listará las funciones registradas de la app (`createNote`, `getNotes` y `deleteNote`) junto
con los tipos de parámetros que aceptan.*

### 2. Crear una nota en segundo plano (¡Sincronización en vivo ⚡!)

Simula que el usuario le dice a Gemini: *"Gemini, guarda una nota sobre Android 16"*. Envía la
petición estructurada con parámetros en formato JSON:

```bash
adb shell 'cmd app_function execute-app-function --package com.example.appfunctionsdemo --function "com.example.appfunctionsdemo.functions.NoteFunctions#createNote" --parameters "{\"title\":\"Nota desde Android 16\",\"content\":\"Esta nota fue registrada headlessly a traves de AppFunctions en tiempo real!\"}"'
```

**¡Observa la pantalla de tu emulador en cuanto pulses Enter!**
El servicio headless escribirá directamente en la base de datos. Como la UI está escuchando la base
de datos de forma reactiva, la nueva nota aparecerá en la pantalla instantáneamente con una
animación suave, **sin que la app haya tenido que ser abierta o recargada**.

### 3. Leer las notas de forma headless

Simula que Gemini necesita leer las notas para responderle a una duda del usuario:

```bash
adb shell 'cmd app_function execute-app-function --package com.example.appfunctionsdemo --function "com.example.appfunctionsdemo.functions.NoteFunctions#getNotes" --parameters "{\"query\":\"\"}"'
```

*Android ejecutará la consulta en segundo plano y te devolverá el listado completo de notas en JSON
estructurado en tu terminal.*

---

## ⚡ Sincronización Multi-Proceso (Room + Flow)

Dado que las llamadas del sistema a las **AppFunctions** corren en un proceso de servicio
independiente (`AppFunctionService`) y la pantalla en la `MainActivity` corre en el proceso
principal, ambos no comparten memoria física.

Para que los cambios del agente de IA aparezcan mágicamente en pantalla en tiempo real sin polling
ni APIs complejas, implementamos una **arquitectura multi-proceso reactiva**:

1. **Room como Puente IPC**: La AppFunction escribe directamente en Room mediante el repositorio.
2. **Invalidación Multi-Instancia**:
   En [AppDatabase.kt](file:///Users/parquesl/Proyectos/Personal/AppFunctionsDemo/app/src/main/java/com/example/appfunctionsdemo/data/AppDatabase.kt)
   activamos `.enableMultiInstanceInvalidation()`. Esto fuerza a Room a invalidar y notificar
   cambios en la caché a través de procesos de forma instantánea.
3. **Flujos Reactivos**: El `NoteDao` expone un `Flow<List<Note>>` reactivo que se propaga entre
   procesos al instante, actualizando la pantalla de inmediato.

---

## 🛠️ Detalles de Arquitectura e Interfaz (MVI & Jetpack Compose)

Para asegurar que el proyecto no sea solo una demo simple sino una base de código robusta y
profesional, la UI implementa estándares premium de desarrollo moderno de Android:

* **Arquitectura MVI (Model-View-Intent)**: La pantalla utiliza un flujo de datos unidireccional (
  UDF) con un único `UiState`
  inmutable ([NoteDashboardUiState.kt](file:///Users/parquesl/Proyectos/Personal/AppFunctionsDemo/app/src/main/java/com/example/appfunctionsdemo/ui/viewmodel/NoteDashboardUiState.kt))
  y eventos de una sola emisión administrados mediante un canal de `SideEffects` (para mostrar
  alertas y Snackbars).
* **Separación de Interfaz**: Dividida en contenedores Stateful (que observan el estado usando
  `collectAsStateWithLifecycle()`) y vistas Stateless (que permiten pruebas unitarias y renderizado
  de `@Preview` rápidos con estados mockeados en Android Studio).
* **Modificadores Compose Estándar**: Todos los componentes Compose exponen y respetan el parámetro
  opcional `modifier: Modifier = Modifier` para un correcto posicionamiento en el layout.
* **Inyección de Dependencias**: Gestión limpia de instancias a través de **Koin** (resolviendo
  bases de datos, repositorios y viewmodels de forma automática).

---

## 📂 Estructura de Paquetes del Proyecto

```
AppFunctionsDemo/
├── gradle/libs.versions.toml      ← Catálogo de dependencias centralizadas
└── app/src/main/
    ├── AndroidManifest.xml        ← Configuración del Manifiesto de Android
    ├── res/xml/app_metadata.xml   ← Configuración xml que indexa las AppFunctions
    └── java/com/example/appfunctionsdemo/
        ├── MainActivity.kt            ← Inicialización de UI minimalista
        ├── AppFunctionsApplication.kt  ← Inicializador de Koin DI
        ├── model/
        │   └── Note.kt                ← Entidad Room anotada con @AppFunctionSerializable
        ├── data/                      ← Capa de datos con Room, DAOs e invalidación multi-proceso
        ├── functions/
        │   └── NoteFunctions.kt       ← Métodos @AppFunction (Punto de entrada de Gemini)
        └── ui/
            ├── theme/                 ← Colores Neon y Obsidian
            ├── components/            ← Widgets de UI (NoteCard, SearchBar, Consola ADB)
            ├── viewmodel/             ← Estado reactivo (MVI)
            └── screens/
                └── NoteDashboardScreen.kt  ← Pantalla Compose reactiva y modular
```
