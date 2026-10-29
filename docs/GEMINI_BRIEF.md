# Encargo para Gemini — Dirección gráfica de los avatares flotantes K2001N

## Objetivo
Diseñar los recursos gráficos originales y una guía de animación para una aplicación Android de avatares flotantes destinada a una radio de automóvil Android basada en K2001N (captura de referencia: pantalla 1024×600). Los dos personajes de referencia proporcionados por el usuario son: (1) el segador EXACTAMENTE basado en la imagen de referencia adjunta: cuerpo pequeño envuelto en túnica negra, capucha triangular y profunda, calavera gris/blanca con grandes cuencas oscuras tipo gafas, sosteniendo una taza gris con un corazón negro y vapor; una guadaña grande curvada detrás y un pequeño libro negro flotante a su lado; estética monocromática, brillante y caricaturesca. No sustituirlo por un segador genérico ni omitir la taza, la guadaña o el libro. (2) un fantasma blanco 3D con gafas negras y sonrisa. Mantener la identidad visual reconocible en todos los fotogramas. Conservar la idea y los rasgos generales, pero producir ilustraciones originales, consistentes y utilizables en una app; no copiar marcas ni añadir texto incrustado en los dibujos.

## Entregables obligatorios
1. **Sprites PNG con transparencia**, sRGB, sin fondo, sin sombra exterior cortada, personaje completo y margen transparente suficiente:
   - `reaper_idle.png`, `ghost_idle.png`: poses neutrales, cada una 512×512.
   - Hojas de sprites separadas por personaje para expresiones y movimiento. Preferido: PNG de 4 columnas × 4 filas, cada celda 512×512, con fondo transparente y separación uniforme. Si la hoja no mantiene celdas exactas, entregar cada fotograma por separado.
   - Expresiones mínimas por personaje: neutral, feliz, sorprendido, asustado, triste, dormido, enfadado, travieso.
   - Acciones mínimas por personaje: saludo, flotar/caminar, correr, esconderse, reaparecer, magia/desvanecerse, comer/tomar algo, conducir coche, conducir moto y navegar en lancha. Las acciones con vehículo pueden incluir el vehículo como parte del sprite, sin texto.
2. **Efectos PNG transparentes**: destello mágico, humo suave, estrellas, corazones, gotas de sudor, signos de sorpresa y polvo de carrera. Entregar archivos individuales.
3. **Hoja de especificaciones** con nombres exactos de archivos, orden de fotogramas, duración recomendada en milisegundos, punto de anclaje (x/y normalizados 0–1), escala sugerida y descripción de cada estado.
4. **Vista previa de contacto** (JPG/PNG) que muestre todos los estados y acciones con etiquetas fuera de los sprites.
5. No entregar PSD como único formato. PNG transparente es obligatorio.

## Dirección artística
- Segador: respetar la imagen de referencia adjunta como modelo principal, no como inspiración vaga. Conservar la capucha negra triangular, la calavera gris/blanca con cuencas oscuras grandes, la túnica negra, la taza gris con corazón negro y vapor, la guadaña curva alta detrás del personaje y el pequeño libro negro flotante con símbolo de calavera. Mantener la composición y los detalles identificadores al crear expresiones/acciones; los accesorios pueden animarse por separado, pero no desaparecer del diseño base. Lectura clara a 96–220 px de alto y contornos limpios.
- Fantasma: blanco/perla, volumen suave 3D, gafas negras gruesas, sonrisa amistosa; separar visualmente del fondo oscuro con borde/contorno sutil.
- Personalidad: cómica, simpática, traviesa; miedo y sorpresa deben ser exagerados, nunca terror gráfico.
- Los dos personajes deben parecer de la misma colección, aunque uno sea ilustración monocroma y el otro 3D.
- Fondo 100 % transparente. Nada de fondos negros, blancos o de color, ni marcas de agua, ni firmas, ni letras integradas.
- No incluir detalles pequeños que desaparezcan al reducir a 96 px.

## Restricciones de implementación
- App Android nativa; pantalla de referencia 1024×600, pero soportar distintas densidades y orientación.
- Animaciones por fotogramas, con bucles cortos y sin saltos al volver a la pose inicial.
- Idioma obligatorio de toda la app: ESPAÑOL. Las frases aleatorias, saludos, reacciones, burbujas, instrucciones y mensajes de interfaz deben estar en español natural. No usar francés. No incluir texto en los sprites: las burbujas nativas de Android mostrarán las frases en español.
- Evitar animaciones estroboscópicas, parpadeos rápidos o destellos intensos.
- Los personajes no deben tapar controles críticos de la radio; sus tamaños y posiciones se podrán ajustar.
- Entregar un ZIP `gemini_avatar_assets.zip` con estructura:
  ```
  assets/
    reaper/
      idle.png
      expressions/...
      actions/...
    ghost/
      idle.png
      expressions/...
      actions/...
    effects/...
    animation_manifest.json
    contact_sheet.png
  ```
- Entregar los nombres de archivo y el manifest en inglés técnico, pero toda frase visible al usuario debe estar en español.
- El JSON debe indicar para cada animación: `name`, `file`, `frameWidth`, `frameHeight`, `frames`, `frameDurationMs`, `loop`, `anchorX`, `anchorY`.

## Criterios de aceptación
- PNG con canal alfa real, no cuadrícula de transparencia pintada.
- Fotogramas alineados y del mismo tamaño.
- Ningún fotograma cortado.
- Identidad visual estable entre fotogramas.
- ZIP abre correctamente y contiene el JSON y la hoja de contacto.
- Antes de finalizar, comprobar visualmente cada archivo y corregir bordes, anatomía, gafas, capucha y calavera.

## Prompt de trabajo para Gemini
Actúa como director de arte y animador 2D/3D para una app Android. Sigue todo este brief y prepara los recursos gráficos listos para producción. Prioridad máxima: el segador debe conservar los rasgos exactos de la imagen de referencia adjunta (capucha negra triangular, calavera gris/blanca con cuencas oscuras, taza con corazón y vapor, guadaña curva y libro negro flotante). Todo texto visible de la app debe estar en español; queda prohibido generar frases en francés. No afirmes haber creado un ZIP ni archivos descargables si tu interfaz no permite generarlos. Si no puedes producir todos los fotogramas en una sola respuesta, entrega primero las dos poses maestras y una hoja de expresiones, después las acciones por lotes, conservando exactamente el mismo diseño de personaje. Devuelve al final el inventario real de archivos producidos y cualquier limitación.
