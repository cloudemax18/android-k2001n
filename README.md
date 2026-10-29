# K2001N Floating Avatars

Aplicación Android nativa experimental para mostrar dos avatares flotantes (segador y fantasma) en radios Android basadas en K2001N.

## Estado

- Proyecto Android y flujo de compilación inicializados.
- Brief de dirección gráfica para Gemini: [docs/GEMINI_BRIEF.md](docs/GEMINI_BRIEF.md).
- La compilación de GitHub Actions generará un APK de depuración como artefacto descargable.
- Los recursos finales dibujados/animados por Gemini aún deben incorporarse a `app/src/main/assets/`; hasta entonces se usa una representación provisional.
- GPS, movimiento real del vehículo y compatibilidad específica con el firmware de la radio requieren pruebas en el dispositivo. La velocidad GPS solo es fiable con permiso concedido y señal disponible.

## Compilar

En GitHub: **Actions → Build floating avatars APK → Run workflow**. Descarga `floating-avatars-debug-apk` en la sección Artifacts del resultado.

El APK de depuración no es un lanzamiento firmado para Play Store.
