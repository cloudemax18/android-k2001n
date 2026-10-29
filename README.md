# Phantom Companions

Aplicación Android de compañeros fantasmales flotantes para el dispositivo K2001N.

## Personajes previstos
- **Gengar**: compañero fantasmal con animación y controles de superposición.
- **Fantasma**: criatura blanca con gafas negras, sonrisa confiada y movimientos suaves.
- **Esqueleto**: calavera con capucha oscura, estética gótica y humor misterioso.

## Compilar APK
El flujo de GitHub Actions **Build Phantom Companions APK** compila la variante debug cuando se actualiza esta rama o se inicia manualmente desde la pestaña Actions.

1. Abre la pestaña [Actions](https://github.com/cloudemax18/android-k2001n/actions).
2. Selecciona **Build Phantom Companions APK**.
3. Abre la ejecución más reciente y espera a que termine el trabajo `build`.
4. Descarga el artefacto `Phantom-Companions`; contiene `Phantom-Companions.apk`.

## Animaciones de Gengar
La superposición usa fotogramas de la hoja de sprites de **Gastly, Haunter & Gengar** de *Pokémon Ranger 2: Shadows of Almia* para variar las poses en reposo, movimiento, aceleración, frenado y giro. La hoja se descarga durante el flujo de compilación en `app/src/main/res/raw/gengar_ranger2.png`; no se guarda como binario en el repositorio.

Fuente del recurso: [The Spriters Resource — Gastly, Haunter & Gengar](https://www.spriters-resource.com/ds_dsi/pokemonranger2shadowsofalmia/asset/20008/). Recurso de terceros; consulta la página de origen para sus créditos y condiciones de uso.

## Estado
La compilación debe considerarse válida únicamente cuando el trabajo de GitHub Actions finalice correctamente y el artefacto esté disponible.