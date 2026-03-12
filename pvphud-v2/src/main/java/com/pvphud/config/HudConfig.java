package com.pvphud.config;

public class HudConfig {

    // ── TOGGLES GENERALES ──────────────────────────────────────────────────
    public static boolean showHealth      = true;
    public static boolean showFood        = true;
    public static boolean showArmor       = true;
    public static boolean showEffects     = true;
    public static boolean showCounter     = true;
    public static boolean showCrosshair   = true;
    public static boolean showFps         = true;
    public static boolean showCoords      = true;
    public static boolean showTargetHUD   = true;
    public static boolean showModuleList  = true;
    public static boolean showWatermark   = true;
    public static boolean showInventoryHUD= true;
    public static boolean showHotbarHUD   = true;
    public static boolean showCooldowns   = true;
    public static boolean showCombo       = true;
    public static boolean showArmorHUD    = true;
    public static boolean showPearlTimer  = true;

    // ── PARTICLES ──────────────────────────────────────────────────────────
    public static boolean hitParticles    = true;
    public static boolean pearlParticles  = true;
    public static boolean hitBlood        = false;
    public static boolean totemParticles  = true;
    public static int     hitParticleStyle = 0; // 0=stars, 1=hearts, 2=crit

    // ── TARGET HUD ─────────────────────────────────────────────────────────
    public static int targetHudStyle      = 0; // 0=bar, 1=circular, 2=minimal
    public static boolean targetShowArmor = true;
    public static boolean targetShowEffects = true;

    // ── CROSSHAIR ──────────────────────────────────────────────────────────
    public static int  crosshairStyle     = 0; // 0=cross, 1=dot, 2=circle, 3=arrow
    public static int  crosshairSize      = 4;
    public static int  crosshairGap       = 2;
    public static boolean crosshairDot    = true;
    public static boolean crosshairRGB    = false;

    // ── BARRAS ─────────────────────────────────────────────────────────────
    public static int  barWidth           = 100;
    public static int  barHeight          = 8;
    public static int  barStyle           = 0; // 0=flat, 1=rounded, 2=segmented

    // ── RGB / TEMA ─────────────────────────────────────────────────────────
    public static boolean rgbMode         = false;
    public static float   rgbSpeed        = 1.0f;
    public static int     accentColor     = 0xFFFF5555; // rojo por defecto
    public static int     bgAlpha         = 0xAA;       // transparencia fondo

    // ── MANO (Hand Tweaks) ─────────────────────────────────────────────────
    public static float handScale         = 1.0f;
    public static float handX             = 0f;
    public static float handY             = 0f;

    // ── POSICIONES HUD ─────────────────────────────────────────────────────
    public static int healthX = 10, healthY = -48;   // relativo al fondo pantalla
    public static int foodX   = 10, foodY   = -30;
    public static int counterOffsetX = 85;           // desde la derecha

    // ── UTIL: RGB animado ──────────────────────────────────────────────────
    private static float rgbHue = 0f;

    public static int getRGBColor() {
        rgbHue = (rgbHue + rgbSpeed * 0.005f) % 1f;
        float[] rgb = hsvToRgb(rgbHue, 1f, 1f);
        return 0xFF000000
                | ((int)(rgb[0] * 255) << 16)
                | ((int)(rgb[1] * 255) << 8)
                |  (int)(rgb[2] * 255);
    }

    public static int getAccent() {
        return rgbMode ? getRGBColor() : accentColor;
    }

    private static float[] hsvToRgb(float h, float s, float v) {
        int i = (int)(h * 6);
        float f = h * 6 - i;
        float p = v * (1 - s), q = v * (1 - f * s), t = v * (1 - (1 - f) * s);
        return switch (i % 6) {
            case 0 -> new float[]{v, t, p};
            case 1 -> new float[]{q, v, p};
            case 2 -> new float[]{p, v, t};
            case 3 -> new float[]{p, q, v};
            case 4 -> new float[]{t, p, v};
            default -> new float[]{v, p, q};
        };
    }
}
