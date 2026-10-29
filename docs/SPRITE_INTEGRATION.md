# Integración de recursos gráficos

## Recursos PNG principales

El paquete `cheviexplorer_recursos_avatares.zip` contiene los PNG transparentes recortados del atlas generado:
- `reaper_main.png`: Segador con capucha negra, calavera gris, guadaña curva, libro con calavera y taza con corazón.
- `ghost_main.png`: Fantasma blanco perlado con gafas negras y sonrisa.

Copia ambos archivos a `app/src/main/res/drawable-nodpi/`. `AvatarView.kt` busca los recursos por nombre y los dibuja a gran tamaño; si faltan, conserva el dibujo vectorial de respaldo.

## Alcance y precisión

Los PNG principales son recursos estáticos y tienen canal alfa. El atlas completo incluido en el paquete sirve como referencia visual para expresiones, vehículos, efectos y burbujas; no debe confundirse con un conjunto de fotogramas independientes ya segmentados y validados. Las animaciones de cada acción deben implementarse y probarse por separado.

Todo el texto visible debe estar en español. No se permiten frases en francés ni otros idiomas en diálogos, etiquetas o mensajes.

## Pruebas necesarias

1. Confirmar que ambos PNG están en la carpeta indicada.
2. Ejecutar la compilación de depuración.
3. Instalar la APK en la radio K2001N con pantalla 1024×600.
4. Comprobar recorte, escala, transparencia y que el overlay no bloquee los controles.
5. Verificar animaciones, permisos y sensores en el dispositivo real.
