# Brief maestro para Gemini — Avatares flotantes de la radio Android K2001N

**Prioridad:** alta. Este documento es el encargo oficial de arte y animación para CheviExplorer/K2001N Floating Avatars. Lee todo el brief antes de generar imágenes. No cambies el diseño de los personajes entre imágenes.

## 1. Tu papel y el objetivo

Actúa como **director de arte, diseñador de personajes y animador de sprites para Android**. Debes crear recursos gráficos utilizables por una app nativa, no solo una ilustración bonita o un póster. La app se ejecutará en una radio Android de automóvil con pantalla de referencia de **1024 × 600 px**, aunque también debe tolerar otras resoluciones y densidades.

Habrá dos personajes flotantes simultáneos: **el Segador** y **el Fantasma**. Se desplazarán por zonas de la pantalla, harán animaciones aleatorias y reaccionarán a toques, hora del día y eventos del vehículo. La aplicación mostrará sus diálogos en **español** mediante burbujas nativas dibujadas por Android.

### Reglas que no se pueden negociar

1. Usa la imagen de referencia del usuario como modelo visual obligatorio del Segador. No lo sustituyas por un segador genérico.
2. Todo texto visible al usuario —frases, saludos, reacciones, etiquetas de vista previa e instrucciones— debe estar en **español natural**. No uses francés.
3. Los sprites no deben contener texto, globos de diálogo, marcas de agua, firmas, marcos ni fondos.
4. La transparencia debe ser real, con canal alfa; no simules transparencia pintando un tablero de ajedrez.
5. No declares que has creado, comprobado o adjuntado archivos si no existen realmente en tu respuesta o entorno.
6. No entregues una única imagen compuesta como sustituto de todos los sprites independientes.
7. Si tu interfaz no puede generar un ZIP, entrega los archivos realmente generados por lotes y explica cómo organizarlos. No inventes enlaces de descarga.
8. La seguridad y la legibilidad son prioritarias: animaciones amistosas, sin gore, sin flashes estroboscópicos y sin elementos que tapen controles importantes de la radio.

## 2. Referencia visual obligatoria: el Segador

El usuario ha proporcionado una imagen concreta. Respétala como hoja de modelo y conserva estos rasgos en cada pose:

- Cuerpo pequeño, flotante, envuelto en una túnica negra.
- Capucha negra grande, puntiaguda/triangular, con pliegues y bordes definidos.
- Calavera gris y blanca, con sombreado metálico suave y cuencas oculares negras muy grandes; nariz oscura y boca de calavera estilizada.
- Una taza gris sostenida delante del torso, con un corazón negro claramente visible y vapor que sube desde ella.
- Guadaña grande con mango oscuro y hoja curva plateada, elevada detrás del personaje.
- Pequeño libro negro flotante a un lado de la cabeza, ligeramente inclinado, con un símbolo de calavera en la cubierta.
- Paleta principalmente negra, gris, plata y blanco; alto contraste y acabado de caricatura pulida.
- Personalidad: sarcástico, curioso, dramático, simpático y travieso; nunca un monstruo realista o sangriento.

**Continuidad visual:** la capucha, la forma de la calavera, la taza con corazón, la guadaña y el libro son elementos de identidad. En animaciones de correr, saludar, asustarse o conducir, pueden cambiar de posición o separarse como piezas animadas, pero deben seguir siendo reconocibles. Si una acción requiere sujetar un volante, conserva los rasgos clave del Segador y representa la taza/libro como accesorios secundarios visibles cuando sea razonable. No elimines permanentemente sus accesorios.

## 3. Referencia visual obligatoria: el Fantasma

- Fantasma blanco/perla, simpático, con volumen suave de personaje 3D.
- Silueta compacta y redondeada, parte inferior ondulada, sensación de estar flotando.
- Gafas de sol negras gruesas, grandes y legibles incluso a tamaño reducido.
- Sonrisa amable y expresiva.
- Brillos suaves y sombreado discreto; un contorno o sombra interior sutil debe separarlo de fondos claros u oscuros.
- Personalidad juguetona, energética, cómica y amistosa.
- No lo conviertas en un fantasma terrorífico ni cambies las gafas por otro accesorio.

## 4. Coherencia de colección

Aunque el Segador tenga un acabado monocromático ilustrado y el Fantasma un volumen 3D, deben parecer personajes de una misma colección: escala visual comparable, calidad de acabado consistente, sombras coherentes y expresiones legibles. Mantén una hoja de modelo de cada personaje y úsala como referencia en cada generación posterior. No rediseñes caras, proporciones, accesorios o paleta entre fotogramas.

## 5. Especificación técnica de los archivos

- Formato principal: PNG RGBA con transparencia real, perfil sRGB.
- Tamaño base de cada fotograma: **512 × 512 px**.
- Personaje completo dentro del lienzo, con margen transparente del 8–12 % y sin cortar guadaña, libro, vapor, gafas ni extremidades.
- Centrar el personaje de forma consistente para evitar saltos al reproducir una animación.
- Los fotogramas de una misma secuencia deben usar el mismo tamaño de lienzo, escala, punto de apoyo y orientación, salvo que el manifiesto indique expresamente lo contrario.
- Sin fondo, sin texto, sin interfaz, sin vehículos de fondo, sin sombras que queden cortadas por el borde.
- En las hojas de sprites, usa una cuadrícula regular de **4 columnas × 4 filas**, con celdas exactamente iguales de 512 × 512 px. Si una secuencia requiere más de 16 fotogramas, crea otra hoja o archivos individuales. No mezcles tamaños ni dejes ambiguo el orden de lectura.
- Además de las hojas, entrega los sprites individuales esenciales para poder probarlos de inmediato.
- No uses JPEG para los personajes porque elimina la transparencia.

## 6. Lista de poses, expresiones y acciones

Crea las secuencias para **ambos personajes**, salvo donde una acción no tenga sentido; en ese caso, propón una variante equivalente y anótala en el manifiesto.

### A. Poses base obligatorias
- `idle.png`: pose neutral reconocible.
- `idle_blink`: pequeño cambio de expresión/parpadeo, si la anatomía lo permite.
- `float_loop`: flotar suavemente, con ciclo continuo.
- `greet`: saludo claro al iniciar la aplicación.
- `tap_reaction`: reacción inmediata al tocar al personaje.

### B. Ocho expresiones
1. Neutral.
2. Feliz/riendo.
3. Sorprendido.
4. Asustado.
5. Triste/decepcionado.
6. Dormido/bostezando.
7. Enfadado/frustrado de forma cómica.
8. Travieso/orgulloso.

Las expresiones deben entenderse sin texto y seguir siendo legibles a una altura de 96–220 px en pantalla.

### C. Acciones cómicas
- Saludar con la mano o con el accesorio apropiado.
- Flotar, balancearse y dar una vuelta.
- Caminar o correr con polvo suave.
- Tropezar y recuperarse sin violencia.
- Esconderse detrás de un borde o salir parcialmente de la pantalla, sin quedar perdido permanentemente.
- Desaparecer con humo/destello suave y reaparecer.
- Hacer magia con estrellas, humo y partículas.
- Comer un tentempié y beber; para el Segador, aprovechar la taza de corazón como accesorio característico.
- Celebrar, reír, bostezar y quedarse dormido.
- Mirar alrededor con curiosidad, asustarse y volver a la pose neutral.
- Interactuar entre ambos: saludarse, sorprenderse mutuamente, discutir en broma, celebrar juntos y perseguirse de manera cómica.

### D. Vehículos
Crea secuencias cortas, limpias y fácilmente reconocibles:
- Conducir un coche pequeño.
- Conducir una motocicleta.
- Navegar en una lancha.
- Para cada vehículo, prepara una pose de entrada, un bucle de conducción/navegación y una pose de salida.
- El vehículo debe formar parte del sprite o de una capa independiente con instrucciones claras. No dibujes texto ni marcas de fabricante.
- Si el tamaño lo permite, entrega el vehículo como capa separada para facilitar el movimiento. Indica el punto de anclaje y cómo combinarlo con el personaje.
- Estas animaciones son fantasía visual. No deben simular que controlan realmente el vehículo.

### E. Reacciones contextuales
Prepara poses que permitan a la app elegir una animación según el contexto:
- Mañana: despertar, bostezar, saludar.
- Día: energía, curiosidad, celebración.
- Tarde: relajarse, tomar café o merendar.
- Noche: sueño, bostezo, luz mágica tenue.
- Aceleración/movimiento del vehículo: expresión de emoción o sorpresa, inclinación y pelo/túnica/accesorios movidos de forma sutil.
- Frenada o cambio brusco detectado por sensores: sorpresa breve y recuperación.
- Sin señal GPS o sin datos del sensor: no diseñes una pantalla de error; basta con que existan estados neutrales.
No afirmes que los gráficos detectan la hora, GPS, velocidad o frenadas: eso corresponde al código Android. Tú debes entregar las poses que permitan representar esos estados.

## 7. Efectos gráficos separados

Entrega PNG RGBA independientes para:
- Humo suave de desaparición/reaparición.
- Destello mágico suave.
- Estrellas pequeñas.
- Corazones.
- Gotas de sudor.
- Signos gráficos de sorpresa sin letras.
- Polvo de carrera.
- Aura o brillo tenue nocturno.
- Partículas de celebración.

Cada efecto debe tener varios fotogramas si se anima, fondo transparente, intensidad moderada y un punto de anclaje claro. Evita flashes blancos a pantalla completa, parpadeo rápido y efectos que reduzcan la visibilidad de la conducción.

## 8. Animación y metadatos

Entrega `assets/animation_manifest.json` válido, sin comentarios y que pueda analizarse como JSON. Para cada animación incluye, como mínimo:

- `name`: identificador estable en inglés, en minúsculas y con guiones bajos.
- `character`: `reaper`, `ghost` o `shared`.
- `file`: ruta relativa exacta al PNG o sprite sheet.
- `frameWidth`, `frameHeight`: dimensiones en píxeles de cada fotograma.
- `frames`: cantidad de fotogramas.
- `columns`, `rows`: cuadrícula cuando se usa una hoja.
- `frameDurationMs`: duración sugerida por fotograma.
- `loop`: `true` para ciclos, `false` para acciones puntuales.
- `anchorX`, `anchorY`: punto de apoyo normalizado entre 0 y 1.
- `scaleRecommended`: escala inicial sugerida.
- `description_es`: explicación en español.
- `triggerSuggestions_es`: sugerencia en español de cuándo usarla.

Usa duraciones razonables: aproximadamente 80–160 ms por fotograma en movimientos rápidos, 160–350 ms en gestos expresivos y 350–800 ms en pausas o estados de sueño. Son referencias, no valores rígidos. Los ciclos deben cerrar sin saltos visibles. Si un sprite no es una hoja de animación, indícalo claramente.

## 9. Frases y personalidad: solo español

No incrustes frases en las imágenes. La aplicación mostrará burbujas nativas de Android. Prepara un archivo adicional `assets/dialogues_es.json` con frases cortas y naturales en español, organizadas por personaje, estado y contexto. Usa UTF-8 y JSON válido.

Incluye al menos 30 frases variadas por personaje, sin insultos fuertes ni referencias peligrosas. Ejemplos de tono, no para repetir en exceso:
- «¡Hola!»
- «¡Qué susto!»
- «¡Mira mi magia!»
- «¿Un cafecito?»
- «¡Vamos de paseo!»
- «¡Buenos días!»
- «¡Buenas noches!»
- «¡Me has pillado!»
- «¡Eso ha sido genial!»
- «¡Necesito una siesta!»

Las frases deben poder mostrarse solas en una burbuja pequeña y ser fáciles de leer en una radio. No uses francés, inglés ni texto aleatorio sin sentido. Evita mostrar una frase larga mientras se conduce; prioriza frases breves y poco frecuentes.

## 10. Organización de entrega

Entrega un ZIP real llamado `gemini_avatar_assets.zip` con esta estructura:

```
assets/
  reaper/
    idle.png
    expressions/
      happy.png
      surprised.png
      scared.png
      sad.png
      sleepy.png
      angry.png
      mischievous.png
    actions/
      greet.png
      float_loop.png
      run.png
      hide.png
      reappear.png
      magic.png
      eat_drink.png
      drive_car.png
      ride_motorcycle.png
      drive_boat.png
      interact.png
  ghost/
    idle.png
    expressions/
      happy.png
      surprised.png
      scared.png
      sad.png
      sleepy.png
      angry.png
      mischievous.png
    actions/
      greet.png
      float_loop.png
      run.png
      hide.png
      reappear.png
      magic.png
      eat_drink.png
      drive_car.png
      ride_motorcycle.png
      drive_boat.png
      interact.png
  effects/
    smoke.png
    magic_sparkle.png
    stars.png
    hearts.png
    sweat.png
    surprise_marks.png
    running_dust.png
    night_glow.png
    celebration.png
  animation_manifest.json
  dialogues_es.json
  contact_sheet.png
  README_ES.txt
```

Puedes añadir archivos, pero no cambies los nombres exigidos sin reflejarlo exactamente en el manifiesto. Si generas hojas de sprites en lugar de archivos individuales, mantén las rutas explícitas y explica la cuadrícula.

## 11. Hoja de contacto y control de calidad

La hoja `contact_sheet.png` debe mostrar las poses, expresiones, acciones y efectos con etiquetas en español fuera de los dibujos. Usa un fondo de comprobación solo en la hoja de contacto; **los PNG individuales deben mantener transparencia real**. Añade vistas sobre fondo claro y oscuro para revisar el contorno.

Antes de entregar:
- Comprueba visualmente que no se haya cortado ningún accesorio.
- Comprueba el canal alfa real.
- Comprueba que los fotogramas de cada ciclo mantengan el mismo personaje y escala.
- Comprueba que el Segador conserve capucha, calavera, taza con corazón, guadaña y libro.
- Comprueba que el Fantasma conserve las gafas negras y la sonrisa.
- Comprueba que todos los diálogos estén en español.
- Comprueba que el JSON sea válido y que todas las rutas del manifiesto existan.
- Abre el ZIP y confirma que contiene los archivos indicados.
- Enumera los archivos realmente creados; separa los completos de los pendientes.

## 12. Orden de trabajo recomendado

Para evitar que cambie el aspecto de los personajes, trabaja en este orden:

**Lote 1 — Aprobación del diseño:** crea una hoja de modelo limpia de cada personaje y dos PNG individuales `idle.png` con transparencia. Muestra frente, tres cuartos y perfil si tu herramienta puede producirlo. No avances a decenas de fotogramas hasta que la identidad visual sea consistente.

**Lote 2 — Expresiones:** genera las siete expresiones adicionales de ambos personajes, manteniendo el modelo aprobado. Entrega PNG individuales y una hoja de contacto.

**Lote 3 — Movimientos básicos:** saludo, flotar, correr, esconderse, reaparecer, magia y reacción al toque.

**Lote 4 — Acciones especiales:** comer/beber, coche, motocicleta, lancha e interacción entre personajes.

**Lote 5 — Efectos y datos:** efectos transparentes, `animation_manifest.json`, `dialogues_es.json`, `contact_sheet.png` y `README_ES.txt`.

**Lote 6 — Validación y paquete:** verifica archivos y crea el ZIP. Si no puedes completar un lote, dilo explícitamente y entrega el inventario real sin simular resultados.

## 13. Prompt maestro para copiar y pegar en Gemini

Eres el director de arte y animador responsable de crear los recursos gráficos de una app Android de avatares flotantes para una radio de coche K2001N. Lee y sigue íntegramente el documento `docs/GEMINI_BRIEF.md` y usa las imágenes de referencia adjuntas en esta conversación.

Tu primera prioridad es reproducir fielmente el Segador de la referencia: túnica negra, capucha triangular grande, calavera gris/blanca con cuencas oscuras grandes, taza gris con corazón negro y vapor, guadaña curva plateada detrás y pequeño libro negro flotante con símbolo de calavera. No lo conviertas en un segador genérico. El segundo personaje es un fantasma blanco 3D con gafas negras gruesas y sonrisa amistosa. Conserva la identidad y proporciones de ambos en todos los fotogramas.

Genera recursos de producción, no una ilustración decorativa: PNG RGBA con transparencia real, fotogramas de 512 × 512 px, personajes completos, escala coherente, hojas de sprites con cuadrícula exacta, efectos separados, manifiesto JSON válido y frases en español en un archivo aparte. La app debe poder mostrar saludos al iniciar, reacciones al toque, flotación, desplazamiento, miedo, alegría, sueño, magia, desaparición/reaparición, comida/bebida, coche, moto, lancha e interacciones entre los dos personajes. Los gráficos solo proporcionan las poses y efectos; no afirmes que implementan GPS, sensores o lógica Android.

Todo texto visible debe estar en español natural. No uses francés. No pongas letras ni burbujas de diálogo dentro de los sprites. No incluyas fondos en los PNG de los personajes, marcas de agua ni firmas. Evita gore, luces estroboscópicas y efectos que distraigan peligrosamente al conductor.

Trabaja por lotes en el orden descrito. Empieza con las poses maestras y espera a que el usuario pueda revisar el diseño antes de multiplicar las animaciones. Si puedes generar archivos descargables, crea y verifica realmente `gemini_avatar_assets.zip`. Si no puedes, entrega los archivos que hayas creado, identifica los pendientes y no inventes enlaces ni afirmes que el ZIP existe. Al final proporciona un inventario exacto, limitaciones conocidas y pasos concretos para incorporar los recursos en `app/src/main/assets/` de la aplicación Android.
