# PvpHUD v2.0 — Fabric 1.20.1
> HUD completo estilo Soup API para Box PvP · Tecla **K** abre el menú

## Features completas (estilo Soup API)

### HUD
- ❤ Barra de vida con absorción — colores dinámicos
- 🍖 Barra de comida
- 🛡 Armadura
- ✨ Efectos activos con tiempo + colores por efecto
- 📦 Contador: Stew / GAP / GGAP / Perlas / Tótems / Pociones
- ⏱ Cooldowns de perla y tótem (barras visuales)
- 🎯 Target HUD — 3 estilos (Barra / Minimal / Lateral)
- 🎒 Inventory HUD — cantidades en hotbar
- 🔢 Combo counter — se resetea a los 3s sin golpear
- 🪨 Pearl timer
- 📋 Module list (lateral derecha)
- 🏷 Watermark

### Partículas
- 💥 Hit particles — 3 estilos (Estrellas / Corazones / Crítico mágico)
- 🩸 Blood particles
- 🔵 Pearl particles (portales)
- 💫 Totem particles

### Crosshair
- 4 estilos: Cruz / Punto / Círculo / Arrow
- Tamaño y espacio configurables
- RGB en la mira

### RGB / Tema
- Modo RGB global — todo el acento cambia de color
- Velocidad del RGB ajustable
- 6 colores preset
- Transparencia de fondos

## Compilar en GitHub Codespaces

1. Sube esta carpeta a un repo en GitHub
2. Verde **Code** → **Codespaces** → **Create codespace on main**
3. En la terminal: `./gradlew build`
4. Descarga `build/libs/pvphud-2.0.0.jar`
5. Ponlo en `.minecraft/mods/`

O usa **GitHub Actions** — cada push genera el `.jar` automáticamente en la pestaña **Actions → Artifacts**.

## Requisitos
- Minecraft 1.20.1 + Fabric Loader 0.14.22+ + Fabric API
