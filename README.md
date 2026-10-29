# Phantom Companions

Aplicación Android de compañeros fantasmales flotantes para el dispositivo K2001N.

## Personajes previstos
- **Gengar**: compañero fantasmal con animación y controles de superposición.
- **Fantasma**: criatura blanca con gafas negras, sonrisa confiada y movimientos suaves.
- **Esqueleto**: calavera con capucha oscura, estética gótica y humor misterioso.

## Compilar APK
El flujo de GitHub Actions **Build Phantom Companions APK** compila la variante debug cuando se actualiza la rama `gengar-floating-companion` o se inicia manualmente desde Actions.

1. Abre [GitHub Actions](https://github.com/cloudemax18/android-k2001n/actions/workflows/gengar-build.yml).
2. Selecciona **Build Phantom Companions APK**.
3. Abre la ejecución más reciente y espera a que termine el trabajo `build`.
4. Descarga el artefacto `Phantom-Companions`; contiene `Phantom-Companions.apk`.

## Estado de las animaciones extendidas de Gengar
Se preparó fuera del repositorio un paquete independiente **GengarFloat** que contiene:
- `MainActivity.kt` y `animations.json` para 29 secuencias iniciales extraídas de una hoja de sprites.
- Un conjunto adicional preparado automáticamente a partir de cuatro hojas proporcionadas posteriormente.
- Fotogramas PNG, hojas de origen y vistas previas.

**Importante:** ese paquete local todavía no está sincronizado con el código de esta aplicación ni sus recursos binarios están comprometidos en esta rama. Por tanto, las APK que se generen desde esta rama **no deben considerarse que incluyen las secuencias extendidas**. La sincronización requiere importar los archivos fuente y los PNG al módulo Android, adaptar la lógica de `GengarOverlayService.java` para consumir las secuencias, y verificar la compilación y reproducción en un dispositivo.

## Recursos de terceros
Las hojas de sprites tienen sus propios autores y condiciones de uso. Consulta las páginas originales antes de redistribuirlas:
- [The Spriters Resource — Pokémon Ranger 2: Gastly, Haunter & Gengar](https://www.spriters-resource.com/ds_dsi/pokemonranger2shadowsofalmia/asset/20008/)
- [The Spriters Resource — Pokémon Mystery Dungeon: Explorers of Time/Darkness](https://www.spriters-resource.com/ds_dsi/pokemonmysterydungeonexplorersoftimedarkness/asset/21635/)
- [The Spriters Resource — Pokémon Generation 1 Customs](https://www.spriters-resource.com/custom_edited/pokemongeneration1customs/asset/17432/)

## Criterio de verificación
Una compilación se considera válida únicamente cuando GitHub Actions finaliza correctamente y el artefacto APK está disponible. La integración de animaciones también requiere verificar que los PNG están presentes en el APK y que las secuencias se reproducen correctamente; compilar por sí solo no confirma ese comportamiento.
