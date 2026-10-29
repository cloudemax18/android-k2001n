# Integración de recursos gráficos

## Paquete preparado

Se ha generado el paquete descargable `cheviexplorer_sprites_para_repo.zip`, con recursos principales WebP, atlas compacto, fotogramas PNG separados, manifiesto y frases en español.

## Recursos Android

Copia los archivos WebP de `app/src/main/res/drawable-nodpi/` del ZIP a la misma ruta del repositorio:
- `reaper_main.webp`: Segador con capucha negra, calavera gris, guadaña curva, libro y taza con corazón.
- `ghost_main.webp`: Fantasma blanco con gafas negras y sonrisa.
- `avatar_sprites.webp`: atlas compacto para reacciones.

`AvatarView.kt` busca los recursos por nombre y conserva los dibujos vectoriales como respaldo si no existen. El atlas usa celdas de 48 × 48 píxeles; se dibujan los fotogramas 0–6 para el Segador y 7–13 para el Fantasma.

## Estado de publicación

Los archivos binarios del ZIP todavía no están confirmados dentro de la rama GitHub; este documento y el código de integración sí están versionados. No se debe considerar la APK final integrada con el arte hasta copiar los recursos, ejecutar la compilación y revisar el resultado en la radio K2001N.

## Idioma

Todo texto visible debe estar en español. No se permiten frases en francés ni otros idiomas en diálogos, etiquetas o mensajes.

## Pruebas necesarias

1. Copiar los tres WebP a `app/src/main/res/drawable-nodpi/`.
2. Ejecutar la compilación de depuración.
3. Instalar la APK en la radio K2001N con pantalla 1024 × 600.
4. Comprobar transparencia, escala y que el overlay no bloquee los controles.
5. Validar cada reacción y movimiento en el dispositivo real.
6. GPS, velocidad y sensores requieren permisos y pruebas específicas; no se simulan como datos reales.
