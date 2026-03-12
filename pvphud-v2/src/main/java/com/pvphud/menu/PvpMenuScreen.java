package com.pvphud.menu;

import com.pvphud.config.HudConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PvpMenuScreen extends Screen {

    // Tabs
    private static final String[] TABS = {"HUD", "Partículas", "Mira", "Target", "RGB", "Barras"};
    private int activeTab = 0;

    private static final int PANEL_W = 360;
    private static final int PANEL_H = 280;

    public PvpMenuScreen() {
        super(Text.literal("PvpHUD Menu"));
    }

    @Override
    protected void init() {
        rebuildWidgets();
    }

    private void rebuildWidgets() {
        this.clearChildren();
        int px = (this.width  - PANEL_W) / 2;
        int py = (this.height - PANEL_H) / 2;

        // Tabs
        int tabW = PANEL_W / TABS.length;
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(TABS[i]),
                    btn -> { activeTab = idx; rebuildWidgets(); })
                    .dimensions(px + i * tabW, py + 20, tabW, 16)
                    .build());
        }

        // Contenido por tab
        switch (activeTab) {
            case 0 -> buildHudTab(px, py);
            case 1 -> buildParticlesTab(px, py);
            case 2 -> buildCrosshairTab(px, py);
            case 3 -> buildTargetTab(px, py);
            case 4 -> buildRGBTab(px, py);
            case 5 -> buildBarsTab(px, py);
        }

        // Botón cerrar
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Cerrar [K]"),
                btn -> this.close())
                .dimensions(px + PANEL_W / 2 - 45, py + PANEL_H - 20, 90, 16)
                .build());
    }

    // ── TAB HUD ───────────────────────────────────────────────────────────────
    private void buildHudTab(int px, int py) {
        int col1 = px + 10, col2 = px + PANEL_W / 2 + 5;
        int y = py + 44, rh = 20;

        addToggle("❤ Vida",        col1, y,       () -> HudConfig.showHealth,     v -> HudConfig.showHealth     = v);
        addToggle("🍖 Comida",     col1, y+rh,    () -> HudConfig.showFood,       v -> HudConfig.showFood       = v);
        addToggle("🛡 Armadura",   col1, y+rh*2,  () -> HudConfig.showArmorHUD,   v -> HudConfig.showArmorHUD   = v);
        addToggle("✨ Efectos",    col1, y+rh*3,  () -> HudConfig.showEffects,    v -> HudConfig.showEffects    = v);
        addToggle("📦 ItemCount",  col1, y+rh*4,  () -> HudConfig.showCounter,    v -> HudConfig.showCounter    = v);
        addToggle("⏱ Cooldowns",  col1, y+rh*5,  () -> HudConfig.showCooldowns,  v -> HudConfig.showCooldowns  = v);
        addToggle("🏷 Watermark",  col2, y,       () -> HudConfig.showWatermark,  v -> HudConfig.showWatermark  = v);
        addToggle("📋 ModuleList", col2, y+rh,    () -> HudConfig.showModuleList, v -> HudConfig.showModuleList = v);
        addToggle("🎯 TargetHUD",  col2, y+rh*2,  () -> HudConfig.showTargetHUD,  v -> HudConfig.showTargetHUD  = v);
        addToggle("🎒 InvHUD",     col2, y+rh*3,  () -> HudConfig.showInventoryHUD,v -> HudConfig.showInventoryHUD=v);
        addToggle("🔢 Combo",      col2, y+rh*4,  () -> HudConfig.showCombo,      v -> HudConfig.showCombo      = v);
        addToggle("🪨 PearlTimer", col2, y+rh*5,  () -> HudConfig.showPearlTimer, v -> HudConfig.showPearlTimer = v);
    }

    // ── TAB PARTÍCULAS ────────────────────────────────────────────────────────
    private void buildParticlesTab(int px, int py) {
        int x = px + 10, y = py + 44, rh = 22;

        addToggle("💥 Hit Particles",   x, y,      () -> HudConfig.hitParticles,   v -> HudConfig.hitParticles   = v);
        addToggle("🔵 Pearl Particles", x, y+rh,   () -> HudConfig.pearlParticles, v -> HudConfig.pearlParticles = v);
        addToggle("🩸 Blood Particles", x, y+rh*2, () -> HudConfig.hitBlood,       v -> HudConfig.hitBlood       = v);
        addToggle("💫 Totem Particles", x, y+rh*3, () -> HudConfig.totemParticles, v -> HudConfig.totemParticles = v);

        // Estilo partículas
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Estilo: " + getParticleStyleName()),
                btn -> {
                    HudConfig.hitParticleStyle = (HudConfig.hitParticleStyle + 1) % 3;
                    btn.setMessage(Text.literal("Estilo: " + getParticleStyleName()));
                })
                .dimensions(x, y + rh * 4, 160, 16)
                .build());
    }

    // ── TAB CROSSHAIR ─────────────────────────────────────────────────────────
    private void buildCrosshairTab(int px, int py) {
        int x = px + 10, y = py + 44, rh = 22;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Estilo: " + getCrosshairStyleName()),
                btn -> {
                    HudConfig.crosshairStyle = (HudConfig.crosshairStyle + 1) % 4;
                    btn.setMessage(Text.literal("Estilo: " + getCrosshairStyleName()));
                })
                .dimensions(x, y, 160, 16)
                .build());

        addToggle("● Punto central", x, y + rh, () -> HudConfig.crosshairDot, v -> HudConfig.crosshairDot = v);
        addToggle("🌈 RGB Mira",     x, y+rh*2, () -> HudConfig.crosshairRGB, v -> HudConfig.crosshairRGB = v);

        this.addDrawableChild(new CrosshairSizeSlider(x, y + rh * 3, PANEL_W - 20, 16,
                Text.literal("Tamaño: " + HudConfig.crosshairSize)));
        this.addDrawableChild(new CrosshairGapSlider(x, y + rh * 4, PANEL_W - 20, 16,
                Text.literal("Espacio: " + HudConfig.crosshairGap)));
    }

    // ── TAB TARGET ────────────────────────────────────────────────────────────
    private void buildTargetTab(int px, int py) {
        int x = px + 10, y = py + 44, rh = 22;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Estilo: " + getTargetStyleName()),
                btn -> {
                    HudConfig.targetHudStyle = (HudConfig.targetHudStyle + 1) % 3;
                    btn.setMessage(Text.literal("Estilo: " + getTargetStyleName()));
                })
                .dimensions(x, y, 160, 16)
                .build());

        addToggle("🛡 Mostrar armadura", x, y + rh,   () -> HudConfig.targetShowArmor,   v -> HudConfig.targetShowArmor   = v);
        addToggle("✨ Mostrar efectos",  x, y + rh*2, () -> HudConfig.targetShowEffects, v -> HudConfig.targetShowEffects = v);
    }

    // ── TAB RGB ───────────────────────────────────────────────────────────────
    private void buildRGBTab(int px, int py) {
        int x = px + 10, y = py + 44, rh = 24;

        addToggle("🌈 Modo RGB Global", x, y, () -> HudConfig.rgbMode, v -> HudConfig.rgbMode = v);

        this.addDrawableChild(new RGBSpeedSlider(x, y + rh, PANEL_W - 20, 16,
                Text.literal("Velocidad RGB: " + String.format("%.1f", HudConfig.rgbSpeed))));

        // Colores preset
        int[] presets = {0xFFFF5555, 0xFF55FF55, 0xFF5555FF, 0xFFFFFF55, 0xFFFF55FF, 0xFF55FFFF};
        String[] names = {"§cRojo", "§aVerde", "§9Azul", "§eAmarillo", "§dMagenta", "§bCyan"};

        for (int i = 0; i < presets.length; i++) {
            final int color = presets[i];
            final String name = names[i];
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(name),
                    btn -> HudConfig.accentColor = color)
                    .dimensions(x + (i % 3) * 60, y + rh * 2 + (i / 3) * 22, 55, 16)
                    .build());
        }
    }

    // ── TAB BARRAS ────────────────────────────────────────────────────────────
    private void buildBarsTab(int px, int py) {
        int x = px + 10, y = py + 44, rh = 26;

        this.addDrawableChild(new BarWidthSlider(x, y, PANEL_W - 20, 16,
                Text.literal("Ancho barra: " + HudConfig.barWidth)));
        this.addDrawableChild(new BarHeightSlider(x, y + rh, PANEL_W - 20, 16,
                Text.literal("Alto barra: " + HudConfig.barHeight)));
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Estilo barra: " + getBarStyleName()),
                btn -> {
                    HudConfig.barStyle = (HudConfig.barStyle + 1) % 3;
                    btn.setMessage(Text.literal("Estilo barra: " + getBarStyleName()));
                })
                .dimensions(x, y + rh * 2, 160, 16)
                .build());
    }

    // ─── RENDER ───────────────────────────────────────────────────────────────
    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        this.renderBackground(ctx);

        int px = (this.width  - PANEL_W) / 2;
        int py = (this.height - PANEL_H) / 2;
        int accent = HudConfig.getAccent();

        // Fondo
        ctx.fill(px, py, px + PANEL_W, py + PANEL_H, 0xDD0A0A0A);
        // Borde top accent
        ctx.fill(px, py, px + PANEL_W, py + 2, accent);
        // Borde
        drawOutline(ctx, px, py, px + PANEL_W, py + PANEL_H, 0xFF222222);
        // Tab activo highlight
        int tabW = PANEL_W / TABS.length;
        ctx.fill(px + activeTab * tabW, py + 20, px + activeTab * tabW + tabW, py + 36, 0x44FFFFFF);

        // Título
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                "§lPvpHUD §r§7· §fMod Menu", this.width / 2, py + 6, accent);

        // Separador
        ctx.fill(px + 4, py + 38, px + PANEL_W - 4, py + 39, 0xFF333333);

        super.render(ctx, mx, my, delta);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (key == 75) { this.close(); return true; }
        return super.keyPressed(key, scan, mods);
    }

    @Override public boolean shouldPause() { return false; }

    // ─── HELPERS ──────────────────────────────────────────────────────────────
    private void addToggle(String label, int x, int y,
                           java.util.function.BooleanSupplier getter,
                           java.util.function.Consumer<Boolean> setter) {
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(label + ": " + (getter.getAsBoolean() ? "§aON" : "§cOFF")),
                btn -> {
                    boolean v = !getter.getAsBoolean();
                    setter.accept(v);
                    btn.setMessage(Text.literal(label + ": " + (v ? "§aON" : "§cOFF")));
                })
                .dimensions(x, y, 165, 16)
                .build());
    }

    private void drawOutline(DrawContext ctx, int x1, int y1, int x2, int y2, int c) {
        ctx.fill(x1, y1, x2, y1+1, c); ctx.fill(x1, y2-1, x2, y2, c);
        ctx.fill(x1, y1, x1+1, y2, c); ctx.fill(x2-1, y1, x2, y2, c);
    }

    private String getCrosshairStyleName() {
        return switch (HudConfig.crosshairStyle) { case 0->"Cruz"; case 1->"Punto"; case 2->"Circulo"; default->"Arrow"; };
    }
    private String getTargetStyleName() {
        return switch (HudConfig.targetHudStyle) { case 0->"Barra"; case 1->"Minimal"; default->"Lateral"; };
    }
    private String getParticleStyleName() {
        return switch (HudConfig.hitParticleStyle) { case 0->"Estrellas"; case 1->"Corazones"; default->"Critico"; };
    }
    private String getBarStyleName() {
        return switch (HudConfig.barStyle) { case 0->"Plano"; case 1->"Redondeado"; default->"Segmentado"; };
    }

    // ─── SLIDERS ──────────────────────────────────────────────────────────────
    static class BarWidthSlider extends SliderWidget {
        BarWidthSlider(int x, int y, int w, int h, Text t) { super(x,y,w,h,t,(HudConfig.barWidth-40)/160.0); }
        protected void updateMessage() { setMessage(Text.literal("Ancho barra: "+HudConfig.barWidth)); }
        protected void applyValue()    { HudConfig.barWidth = (int)(40+value*160); }
    }
    static class BarHeightSlider extends SliderWidget {
        BarHeightSlider(int x, int y, int w, int h, Text t) { super(x,y,w,h,t,(HudConfig.barHeight-4)/12.0); }
        protected void updateMessage() { setMessage(Text.literal("Alto barra: "+HudConfig.barHeight)); }
        protected void applyValue()    { HudConfig.barHeight = (int)(4+value*12); }
    }
    static class CrosshairSizeSlider extends SliderWidget {
        CrosshairSizeSlider(int x, int y, int w, int h, Text t) { super(x,y,w,h,t,(HudConfig.crosshairSize-2)/12.0); }
        protected void updateMessage() { setMessage(Text.literal("Tamaño: "+HudConfig.crosshairSize)); }
        protected void applyValue()    { HudConfig.crosshairSize = (int)(2+value*12); }
    }
    static class CrosshairGapSlider extends SliderWidget {
        CrosshairGapSlider(int x, int y, int w, int h, Text t) { super(x,y,w,h,t,HudConfig.crosshairGap/8.0); }
        protected void updateMessage() { setMessage(Text.literal("Espacio: "+HudConfig.crosshairGap)); }
        protected void applyValue()    { HudConfig.crosshairGap = (int)(value*8); }
    }
    static class RGBSpeedSlider extends SliderWidget {
        RGBSpeedSlider(int x, int y, int w, int h, Text t) { super(x,y,w,h,t,(HudConfig.rgbSpeed-0.1)/4.9); }
        protected void updateMessage() { setMessage(Text.literal("Velocidad RGB: "+String.format("%.1f",HudConfig.rgbSpeed))); }
        protected void applyValue()    { HudConfig.rgbSpeed = (float)(0.1+value*4.9); }
    }
}
