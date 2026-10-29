# Plan propio de recursos gráficos e integración

Este proyecto se desarrollará directamente en el repositorio, sin depender de Gemini. Los recursos se crean, ajustan e integran por etapas, verificando cada paso.

## Personajes

### El Segador
- Capucha triangular negra y túnica oscura.
- Calavera gris/blanca con cuencas negras grandes.
- Taza gris con corazón negro y vapor.
- Guadaña curva de hoja plateada.
- Libro negro flotante con símbolo de calavera.
- Actitud cómica, curiosa y algo dramática, sin terror gráfico.

### El Fantasma
- Silueta blanca/perla, volumen redondeado.
- Gafas negras gruesas y sonrisa amistosa.
- Gestos juguetones, claros a tamaño pequeño.

## Estado de los recursos

La implementación actual dibuja los personajes con Canvas de Android. Es útil como prototipo y evita depender de archivos de arte que todavía no se han producido, pero no equivale a una hoja de sprites terminada. La prioridad inmediata es mantener los rasgos identificadores y asegurar que la app pueda compilarse antes de ampliar las animaciones.

## Próximas etapas

1. Confirmar que el proyecto compila con GitHub Actions.
2. Revisar visualmente los personajes en la pantalla 1024×600 de la radio.
3. Separar el movimiento del Segador y del Fantasma y limitar las zonas para no tapar controles.
4. Implementar reacciones táctiles, saludos de inicio y frases aleatorias en español.
5. Añadir animaciones por estados: neutral, alegría, sorpresa, miedo, sueño, correr, magia, desaparecer/reaparecer, comer/beber e interacción.
6. Implementar lectura de hora local y, más adelante, ubicación/velocidad/sensores solo con permisos y datos disponibles.
7. Añadir sprites PNG RGBA independientes cuando puedan generarse y validarse; conservar el dibujo vectorial como alternativa.
8. Probar la APK en la radio real y documentar fallos con capturas y registros.

## Requisitos para los recursos PNG futuros

- PNG RGBA con canal alfa real y fondo transparente.
- Personaje completo, sin accesorios cortados.
- Tamaños consistentes por secuencia y anclajes estables.
- Sin texto incrustado; todos los diálogos se muestran como texto nativo de Android.
- Todo texto visible en español.
- Manifiesto JSON que describa archivo, fotogramas, duración, bucle y punto de anclaje.
- Comprobar la transparencia y las rutas antes de incluir los archivos en la app.

## Limitaciones conocidas

Las ilustraciones actuales son formas vectoriales dibujadas por código y no replican con exactitud todos los matices de la imagen de referencia. El movimiento actual es limitado al área del overlay. La compatibilidad de permisos de superposición y servicios en la radio K2001N no se considera confirmada hasta probar el APK en el dispositivo.
