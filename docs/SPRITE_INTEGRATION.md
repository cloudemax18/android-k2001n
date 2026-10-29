# Integración de recursos gráficos

## Recursos principales incluidos en el flujo de compilación

Los archivos WebP principales se guardan en el repositorio como Base64 para permitir su almacenamiento mediante la API de texto de GitHub:

- `assets/sprites_base64/reaper_main.webp.b64`
- `assets/sprites_base64/ghost_main.webp.b64`

El flujo `.github/workflows/build-apk.yml` decodifica estos recursos antes de ejecutar Gradle y los coloca en `app/src/main/res/drawable-nodpi/` con los nombres `reaper_main.webp` y `ghost_main.webp`. Android puede cargarlos como recursos drawable sin conversión adicional.

## Código

`AvatarView.kt` busca los recursos `reaper_main` y `ghost_main`; si no existen, mantiene un dibujo vectorial de respaldo.

## Atlas y animaciones adicionales

El atlas de expresiones, efectos y vehículos está preparado en el paquete de recursos de trabajo, pero todavía no está incluido como recurso binario en el flujo de compilación. No se debe afirmar que todas esas animaciones funcionan hasta que se incorpore el atlas y se pruebe cada estado.

## Idioma

Todo texto visible debe estar en español. No se permiten frases en francés ni otros idiomas en diálogos, etiquetas o mensajes.

## Pruebas necesarias

1. Confirmar que GitHub Actions reconstruye los dos WebP y compila la APK.
2. Instalar la APK en la radio K2001N con pantalla 1024 × 600.
3. Comprobar transparencia, escala, similitud con las referencias y que el overlay no bloquee controles.
4. Validar cada reacción y movimiento en el dispositivo real.
5. GPS, velocidad y sensores requieren permisos y pruebas específicas; no se simulan como datos reales.
