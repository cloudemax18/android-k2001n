# Integración de GengarFloat — estado y lista de entrega

## Estado verificado el 29 de octubre de 2026

- Rama de trabajo: `gengar-floating-companion`.
- Aplicación Android actual: módulo `app`, con `GengarOverlayService.java` y `MainActivity.java`.
- Paquete extendido preparado fuera del repositorio: GengarFloat, con código Kotlin propio, `animations.json`, fotogramas PNG y vistas previas.
- Se añadió `GengarAnimationPlayer.java` como primer adaptador opcional para leer `assets/animations.json` y fotogramas `frame_000.png`, etc. Aún no está conectado a `GengarOverlayService` y no sustituye al reproductor actual.
- El paquete extendido y sus PNG todavía no se han importado al módulo existente. La app actual está escrita en Java y tiene un servicio de superposición; GengarFloat es una app independiente con una Activity custom. Copiar solo `MainActivity.kt` no integraría sus animaciones en el servicio flotante existente.
- No se ha compilado esta modificación en este entorno; hay que validar el compilador y la carga de assets cuando estén disponibles.
- No se debe publicar la APK actual como una versión que contenga los sprites nuevos.

## Pasos necesarios antes de publicar

1. Importar al repositorio los archivos del paquete GengarFloat, incluidos todos los fotogramas PNG y `animations.json`.
2. Decidir si se migra la app a Kotlin o si se adapta el reproductor de secuencias a Java dentro de `GengarOverlayService`.
3. Completar y probar `GengarAnimationPlayer.java`; actualmente decodifica al cambiar de secuencia, por lo que conviene mover esa carga fuera del hilo UI antes de producción. Conectarlo al `GhostView` y mantener el GIF como fallback.
4. Crear selección aleatoria con ponderaciones: reposo frecuente; acciones breves y especiales menos frecuentes. Evitar repetir la misma secuencia consecutivamente cuando haya alternativas.
5. Conservar la interacción táctil y el ciclo de vida del overlay; detener animaciones cuando el servicio no esté visible.
6. Compilar con GitHub Actions, comprobar que los assets están empaquetados, instalar la APK y verificar el comportamiento real en Android.
7. Publicar el artefacto solo después de una compilación exitosa y pruebas básicas.

## Criterios de aceptación

- [ ] Todas las secuencias referenciadas por el JSON tienen todos sus PNG.
- [ ] Ningún fotograma devuelve un recurso inexistente.
- [ ] La animación aleatoria no interrumpe la secuencia actual en mitad de una acción.
- [ ] La animación de reposo retorna después de las acciones temporales.
- [ ] La superposición no produce cierres, parpadeos ni crecimiento sostenido de memoria.
- [ ] GitHub Actions produce una APK descargable y el dispositivo reproduce varias acciones aleatorias.

## Fuentes de sprites

Las páginas de origen y los créditos deben conservarse junto con los archivos. Revisa los términos de cada recurso antes de redistribuirlo.
