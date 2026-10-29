# Avatares flotantes K2001N

Aplicación Android nativa experimental con dos personajes flotantes para radios Android K2001N: **el Segador** y **el Fantasma**. El desarrollo se realiza directamente en este repositorio, sin depender de Gemini.

## Recursos gráficos

- Los recursos principales WebP están versionados como texto Base64 en `assets/sprites_base64/` para poder almacenarlos mediante la API de contenido de GitHub.
- El flujo de GitHub Actions los reconstruye como `reaper_main.webp` y `ghost_main.webp` dentro de `app/src/main/res/drawable-nodpi/` antes de compilar.
- `AvatarView.kt` carga esos recursos y conserva dibujos vectoriales de respaldo.
- Los sprites de expresiones, efectos y vehículos están parcialmente preparados como paquete de trabajo, pero el atlas de animación todavía no está integrado en la compilación.

## Compilar y descargar la APK

1. Abre **Actions** en GitHub.
2. Selecciona **Build floating avatars APK**.
3. Espera a que la ejecución termine correctamente.
4. Descarga el artefacto `floating-avatars-debug-apk`.

La APK es de depuración, no una versión de lanzamiento firmada. Una compilación correcta no sustituye la prueba en la radio real.

## Estado y limitaciones

- Todos los textos visibles de la app deben estar en español.
- Los recursos principales están integrados en el flujo de compilación; el parecido final debe comprobarse visualmente en la radio.
- El atlas completo, animaciones de vehículos, movimiento por toda la pantalla, GPS y sensores todavía necesitan integración y pruebas.
- La compatibilidad con el firmware de la radio K2001N no se considera verificada hasta instalar y probar la APK en el dispositivo.

## Documentación

- [Plan de recursos gráficos e integración](docs/ASSET_IMPLEMENTATION.md)
- [Guía de sprites](docs/SPRITE_INTEGRATION.md)
- [Solicitud de cambios](https://github.com/cloudemax18/android-k2001n/pull/3)
