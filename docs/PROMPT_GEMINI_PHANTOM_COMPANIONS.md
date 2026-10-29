# Prompt maestro para Gemini — Phantom Companions
## Ingeniería Android, avatares flotantes, animaciones, sensores y compilación

Actúa como ingeniero Android sénior y arquitecto de software especializado en Java/Kotlin, servicios flotantes, animación 2D, sensores, ubicación, accesibilidad, rendimiento y pruebas automatizadas. No te limites a proponer soluciones: inspecciona el repositorio, implementa los cambios reales, prueba lo que puedas y entrega un ZIP y un informe Markdown descargables.

## Identidad del proyecto
El nombre de producto es **Phantom Companions**. Ya no es una aplicación de Pokémon. Elimina de la interfaz activa, nombres de recursos, notificaciones, textos y documentación de uso todas las referencias a Pokémon, Gengar y Blastoise. No cambies el applicationId/namespace sin una razón técnica documentada. El repositorio actual de referencia es https://github.com/cloudemax18/android-k2001n y la rama de trabajo histórica es `gengar-floating-companion`.

El producto tiene dos compañeros basados en las imágenes adjuntas:
1. **Fantasma** (`ghost`): criatura blanca/marfil, cuerpo ondulado, gafas negras grandes y sonrisa confiada. Personalidad moderna, elegante, simpática y bromista. Movimientos suaves; puede bostezar, acomodarse las gafas, tomar café imaginario y flotar.
2. **Esqueleto** (`reaper`): calavera marfil con capucha oscura y estética gótica, misteriosa pero simpática. Personalidad tranquila, irónica y observadora. Puede leer, sostener una taza de café, invocar páginas y partículas discretas y hacer gestos dramáticos.

Las imágenes son referencias visuales, no sprites animados. No afirmes que son animaciones completas. Integra recursos reales cuando estén disponibles y documenta claramente cualquier recurso provisional o pendiente.

## Fase 1 — inspección
- Comprueba que el repositorio, rama y archivos existen.
- Inspecciona el estado de Git, README, Gradle, Manifest, Activity, servicio flotante, permisos, recursos y workflows.
- Identifica versiones de Android, Java/Kotlin y dependencias.
- Anota el commit inicial y conserva cambios útiles.
- No sobrescribas archivos sin revisar su contenido actual.

## Fase 2 — identidad y menú
- Cambia la etiqueta visible a Phantom Companions y actualiza README, textos, notificaciones, títulos y workflow.
- Centraliza el nombre de producto y los nombres visibles de los personajes.
- Diseña un menú oscuro, elegante y accesible con vista previa y botones para activar Fantasma y Esqueleto, estado del servicio, ajustes y opción para detenerlo.
- No declares terminado el cambio visual si los recursos nuevos aún no están integrados.
- El identificador del paquete debe permanecer estable salvo justificación documentada.

## Fase 3 — arquitectura
Separa responsabilidades para el menú, servicio flotante, motor de animación, controlador de movimiento, interacciones, sensores, ubicación, rutinas horarias, diálogos, texto a voz, carga de recursos y preferencias. Reutiliza el mismo motor para ambos personajes; diferencia perfiles, assets y personalidad, sin duplicar innecesariamente la lógica.

## Fase 4 — movimiento e interacción
El avatar debe desplazarse de forma aleatoria, fluida y natural por las zonas seguras de la pantalla, con pausas, velocidades variables, aceleración/desaceleración, flotación y cambios de dirección. Evita saltos bruscos, bucles de alta frecuencia y salidas fuera de pantalla. Recalcula límites ante rotación o cambios de ventana.

El toque corto debe producir una reacción, pose o diálogo. Los toques repetidos deben variar sin saturar al usuario. Durante el arrastre, suspende el movimiento automático y sigue el dedo; al soltar, conserva una posición válida. La pulsación larga debe tener una acción clara sin disparar también el toque corto. Respeta los toques dirigidos a la aplicación que está debajo; no interceptes eventos globales ni solicites permisos invasivos.

## Fase 5 — máquina de estados
Implementa estados equivalentes a `IDLE`, `FLOATING`, `GREETING`, `HAPPY`, `CURIOUS`, `BORED`, `HUNGRY`, `SLEEPY`, `SLEEPING`, `WAKING_UP`, `DRINKING_COFFEE`, `SURPRISED`, `TOUCHED`, `DRAGGED`, `MOVING`, `ACCELERATING`, `BRAKING`, `TURNING`, `VEHICLE_MOUNT`, `VEHICLE_DRIVE`, `VEHICLE_DRIFT`, `VEHICLE_STOP` y `CELEBRATION`. Cada estado define animación, prioridad, duración, evento de entrada, salida e interrupciones. Evita que dos animaciones controlen a la vez la posición o la expresión.

## Fase 6 — rutinas horarias y diálogos
Utiliza la hora local del sistema:
- Mañana: despertar, estirarse, saludar.
- 12:00: mostrar hambre, mirar un reloj imaginario, animar comida y decir «¡Es hora de almorzar!».
- Tarde: relajarse, leer, tomar café o bromear.
- Noche: bostezar, reducir movimientos y simular dormir.
- Madrugada: sueño profundo y respuestas discretas.

Evita repetir una rutina cada vez que se actualiza un temporizador. Gestiona cambios de fecha y zona horaria, preferencias de silencio y cancelación al interactuar.

Implementa burbujas legibles con límites de pantalla, tamaños adaptables, entrada/salida y duración configurable. Incluye al menos 60 frases originales en español distribuidas entre ambos personajes y categorías como saludos, café, hambre, sueño, humor, toque, movimiento y despedida. Usa prioridades, control de frecuencia y prevención de frases repetidas. El texto a voz de Android es opcional y debe manejar ausencia de motor o idioma español.

## Fase 7 — GPS y sensores
La ubicación y los sensores son opcionales. Filtra ruido y datos imprecisos, reduce el consumo de batería y libera los recursos cuando no se necesiten. Si el usuario deniega permisos o el GPS no está disponible, la aplicación debe seguir funcionando con animaciones normales.

No confundas el movimiento del teléfono con aceleración real del automóvil. Para inferir desplazamiento combina señales fiables y sostenidas, con umbrales e histéresis. Documenta las limitaciones. La animación de vehículo es decorativa y no debe fomentar el uso del teléfono mientras se conduce.

Cuando exista una señal fiable de movimiento, la secuencia opcional puede mostrar al personaje subiéndose a un vehículo fantástico, arrancando, desplazándose con líneas de velocidad y derrapando de forma cómica, para después detenerse y volver al estado normal. Debe ser cancelable y tener tiempo máximo.

## Fase 8 — assets y rendimiento
Admite formatos apropiados como PNG transparente, sprite sheets, GIF, WebP animado y efectos separados. Valida manifiestos y rutas de cualquier paquete ZIP de animaciones que se adjunte. Nunca inventes archivos ni marques recursos provisionales como definitivos. Usa carga/caché apropiada, libera memoria, evita bloquear el hilo principal y pausa trabajo innecesario cuando el avatar esté oculto.

## Fase 9 — permisos y pruebas
Revisa las restricciones de cada versión Android para superposición, ubicación y servicios en primer plano. Solicita solo los permisos necesarios, con explicación y control por el usuario. Prueba transiciones de estados, límites de movimiento, arrastre, rutinas horarias, ausencia de permisos, GPS impreciso, recursos faltantes, parada del servicio y liberación de recursos.

Distingue honestamente entre código actualizado, compilación correcta, pruebas automatizadas, pruebas en emulador y pruebas físicas. No afirmes que el dispositivo K2001N se probó si no fue posible.

## Fase 10 — compilación y entregables
Ejecuta la compilación debug adecuada, por ejemplo `./gradlew assembleDebug`, corrige errores reproducibles y verifica que el APK final exista. Si hay GitHub Actions, comprueba el resultado final y que el artefacto corresponde al último commit.

Entrega:
- `Phantom_Companions_Android.zip`: código y cambios completos, recursos disponibles, pruebas y archivos necesarios para reproducir el build.
- `Phantom_Companions_Technical_Report.md`: arquitectura, cambios por archivo, estados, movimiento, rutinas, sensores, permisos, dependencias, pruebas, resultado de compilación, ruta del APK, limitaciones e instrucciones.
- `CHANGELOG.md` y manifiesto de assets cuando corresponda.

Antes de entregar, verifica que el ZIP se abre, que el Markdown existe, que las rutas son válidas y que no se incluyeron secretos. Si no puedes renombrar el repositorio en la configuración de GitHub, dilo explícitamente y proporciona el nombre sugerido `phantom-companions-android`; no afirmes que el nombre remoto fue cambiado si no lo hiciste.

**Resultado esperado:** proyecto real migrado a Phantom Companions, con Fantasma y Esqueleto como únicos personajes activos, implementación probada hasta donde permita el entorno, ZIP y Markdown reales y un informe honesto sobre la compilación del APK.
