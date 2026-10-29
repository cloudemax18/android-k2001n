# Avatares flotantes K2001N

Aplicación Android nativa experimental con dos personajes flotantes para radios de automóvil Android K2001N: **el Segador** y **el Fantasma**. El proyecto lo desarrollamos directamente en el repositorio; no depende de Gemini ni de otro diseñador externo.

## Estado real del proyecto

- App Android inicial y flujo de compilación de APK de depuración.
- El Segador vectorial se está ajustando para conservar los rasgos de la referencia: capucha negra, calavera clara, taza con corazón y vapor, guadaña y libro flotante.
- El Fantasma provisional tiene cuerpo blanco y gafas oscuras.
- Las frases y reacciones implementadas están en español.
- Los dibujos actuales son vectores provisionales de Android, no todavía sprites PNG finales ni animaciones de producción.
- GPS, velocidad real, sensores del vehículo, desplazamiento por toda la pantalla y compatibilidad con el firmware de la radio requieren implementación y pruebas adicionales.

## Compilar el APK

1. Abre **Actions** en GitHub.
2. Selecciona **Build floating avatars APK**.
3. Pulsa **Run workflow** si no hay una ejecución reciente.
4. Cuando termine correctamente, descarga el artefacto **floating-avatars-debug-apk**.

La compilación de depuración no es un lanzamiento firmado para Play Store. No se debe dar por compilada hasta que una ejecución de GitHub Actions termine correctamente.

## Documentación

- [Plan de recursos gráficos e integración](docs/ASSET_IMPLEMENTATION.md)
- [Solicitud de cambios en curso](https://github.com/cloudemax18/android-k2001n/pull/3)

## Objetivo de diseño

Los personajes deben flotar sin bloquear los controles de la radio, reaccionar a los toques, mostrar frases cortas en español y usar animaciones suaves. GPS y sensores solo se incorporarán cuando puedan leerse con permisos disponibles y comprobarse en el dispositivo; no se deben simular como datos reales.
