# Android AppFunctions Demo App 🚀

Una aplicación de demostración de alto nivel que implementa la nueva API **Android AppFunctions Jetpack library** (introducida en Android 16 / API 36). El proyecto ilustra cómo los asistentes de Inteligencia Artificial en el dispositivo (como Google Gemini) pueden descubrir, entender y ejecutar la funcionalidad interna de tu aplicación de forma programática y en segundo plano (**headless**).

Esta app presenta un **Dashboard Premium en Tema Oscuro (Obsidian & Neon)** con una consola de desarrollador integrada y un sistema de **sincronización multi-proceso reactiva en tiempo real** mediante Broadcasts privados.

---

## 🧠 ¿Qué es AppFunctions y cómo funciona?

Tradicionalmente, los asistentes de voz usaban Deep Links para interactuar con las apps: le decías *"Gemini, crea una nota"* y la app se abría en pantalla interrumpiendo al usuario.

Con **AppFunctions**, la app expone "herramientas" semánticas autodescritas al sistema operativo (mediante anotaciones `@AppFunction` y KDocs). Cuando el usuario interactúa con la IA mediante Lenguaje Natural:
1. **Gemini** analiza el comando de voz y busca qué aplicaciones tienen AppFunctions que puedan resolverlo.
2. El sistema operativo **Android** invoca a nuestra app en segundo plano (proceso de servicio *headless*).
3. Nuestra app ejecuta la lógica Kotlin nativa y devuelve el resultado serializado (JSON) directamente al agente de IA, todo esto en milisegundos y **sin abrir la interfaz gráfica**.

---

## 📂 Arquitectura Modular del Proyecto

El código está organizado bajo una arquitectura limpia y modular de paquetes listos para producción:

```
AppFunctionsDemo/
├── gradle/wrapper/                <-- Gradle 8.9 Wrapper precargado
├── gradle/libs.versions.toml      <-- Catálogo de versiones centralizado (Kotlin 2.0, Compose, AppFunctions)
├── gradle.properties              <-- Soporte para AndroidX habilitado
└── app/src/main/
    ├── AndroidManifest.xml        <-- MainActivity y enlaces semánticos
    ├── res/xml/app_metadata.xml   <-- Descripción de capacidades del agente
    └── java/com/example/appfunctionsdemo/
        ├── MainActivity.kt        <-- Clase de entrada minimalista (¡solo 16 líneas!)
        ├── model/Note.kt          <-- Estructura de datos anotada con @AppFunctionSerializable
        ├── data/NoteRepository.kt <-- Repositorio seguro e inmune a concurrencia (CopyOnWriteArrayList)
        ├── functions/NoteFunctions.kt <-- Métodos asíncronos (suspend) anotados con @AppFunction
        └── ui/
            ├── theme/             <-- Paleta de colores Obsidian y Compose AppTheme
            ├── components/        <-- Widgets reutilizables (Tarjetas, Buscador, Consola ADB)
            └── screens/           <-- Pantalla principal NoteDashboardScreen con BroadcastReceiver
```

---

## 💻 Requisitos de Entorno

* **Java Development Kit (JDK):** JDK 17 o superior.
* **Android Studio:** Ladybug o superior (para soporte completo de Kotlin 2.0 y KSP).
* **Emulador/Dispositivo:** Android 16 Developer Preview (**API 36.1** o superior) para contar con el soporte del servicio operativo `app_function`.

---

## 🚀 Guía de Pruebas: Comandos ADB Paso a Paso

Dado que las llamadas en segundo plano ocurren desde el proceso del sistema operativo, simulamos el comportamiento de los agentes de IA (como Gemini) utilizando comandos `adb` desde la terminal de tu Mac/PC.

> [!IMPORTANT]
> Asegúrate de tener el emulador de **Android 16 (API 36.1+)** encendido, la aplicación instalada y abierta en primer plano.

### 1. Listar las AppFunctions registradas por la app
Pregúntale a la base de datos semántica de Android qué capacidades ha indexado sobre nuestra aplicación:
```bash
adb shell cmd app_function list-app-functions | grep com.example.appfunctionsdemo
```
*Deberías ver listadas en la respuesta las tres funciones: `createNote`, `getNotes` y `deleteNote` junto con sus firmas semánticas.*

### 2. Crear una nota de forma headless (Sincronización en vivo ⚡)
Envía este comando estructurado para ejecutar la función `createNote` pasando los parámetros en formato JSON.

> [!TIP]
> **Nota para macOS (zsh):** Si ejecutas comandos con doble comilla exterior `adb shell "..."` y añades signos de exclamación `!` (como `real!`), `zsh` intentará realizar una expansión de historial y fallará con el error `zsh: event not found: \`.
> Para evitar esto, **el comando de abajo utiliza comillas simples `'...'` en el exterior**, lo que desactiva por completo la expansión de historial local en tu terminal y permite usar exclamaciones de forma segura:

```bash
adb shell 'cmd app_function execute-app-function --package com.example.appfunctionsdemo --function "com.example.appfunctionsdemo.functions.NoteFunctions#createNote" --parameters "{\"title\":\"Nota desde Android 16\",\"content\":\"Esta nota fue registrada headlessly a traves de AppFunctions en tiempo real!\"}"'
```

**¡Mira la pantalla del emulador!** En cuanto presiones *Enter*, el proceso en segundo plano del sistema operativo llamará a tu código Kotlin, el cual enviará un Broadcast privado que la interfaz de Compose capturará, agregando la nueva tarjeta con una animación suave al instante.

### 3. Consultar las notas desde la Terminal
También puedes simular que el asistente lee los datos del repositorio de la aplicación en segundo plano para responderle al usuario sin abrir la interfaz:
```bash
adb shell 'cmd app_function execute-app-function --package com.example.appfunctionsdemo --function "com.example.appfunctionsdemo.functions.NoteFunctions#getNotes" --parameters "{\"query\":\"\"}"'
```
*Esto te devolverá el listado JSON completo de las notas almacenadas en la persistencia del servicio.*

---

## 🛡️ Sincronización Multi-Proceso Segura

Dado que las AppFunctions son invocadas de forma aislada por el sistema en un proceso de servicio independiente (`AppFunctionService`) y la pantalla corre en el proceso de la `MainActivity`, implementamos un receptor de difusión privada (`DisposableEffect` con `RECEIVER_NOT_EXPORTED` en Compose) para enviar y recibir actualizaciones de forma privada e instantánea, garantizando la seguridad del sandboxing de Android.
