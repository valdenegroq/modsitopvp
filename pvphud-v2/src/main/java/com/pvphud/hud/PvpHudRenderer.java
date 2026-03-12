package com.pvphud.hud;

import com.pvphud.config.HudConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Collection;

public class PvpHudRenderer {

    // Colores base
    private static final int COL_HP_HIGH   = 0xFF55FF55;
    private static final int COL_HP_MID    = 0xFFFFAA00;
    private static final int COL_HP_LOW    = 0xFFFF5555;
    private static final int COL_WHITE     = 0xFFFFFFFF;
    private static final int COL_GRAY      = 0xFFAAAAAA;
    private static final int COL_DARK      = 0xFF111111;

    // Combo tracker
    private static int   comboCount    = 0;
    private static long  lastHitTime   = 0;
    private static float pearlTimer    = 0f;
    private static long  pearlThrowTime= 0;

    public static void render(DrawContext ctx, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        if (mc.options.hudHidden) return;
        if (mc.currentScreen != null) return;

        int W = mc.getWindow().getScaledWidth();
        int H = mc.getWindow().getScaledHeight();
        TextRenderer font = mc.textRenderer;
        int accent = HudConfig.getAccent();

        if (HudConfig.showWatermark)    renderWatermark(ctx, font, W, accent);
        if (HudConfig.showModuleList)   renderModuleList(ctx, font, W, H, accent);
        if (HudConfig.showHealth)       renderHealthBar(ctx, font, mc, W, H, accent);
        if (HudConfig.showFood)         renderFoodBar(ctx, font, mc, W, H);
        if (HudConfig.showArmorHUD)     renderArmorHUD(ctx, font, mc, W, H, accent);
        if (HudConfig.showEffects)      renderEffects(ctx, font, mc, W, H);
        if (HudConfig.showCounter)      renderItemCounter(ctx, font, mc, W, H, accent);
        if (HudConfig.showCooldowns)    renderCooldowns(ctx, font, mc, W, H, accent);
        if (HudConfig.showTargetHUD)    renderTargetHUD(ctx, font, mc, W, H, accent);
        if (HudConfig.showInventoryHUD) renderInventoryHUD(ctx, font, mc, W, H, accent);
        if (HudConfig.showCrosshair)    renderCrosshair(ctx, W, H, accent);
        if (HudConfig.showFps || HudConfig.showCoords) renderInfo(ctx, font, mc, W, H, accent);
        if (HudConfig.showCombo)        renderCombo(ctx, font, W, H, accent);
        if (HudConfig.showPearlTimer)   renderPearlTimer(ctx, font, W, H, accent);
    }

    // ─── WATERMARK ────────────────────────────────────────────────────────────
    private static void renderWatermark(DrawContext ctx, TextRenderer font, int W, int accent) {
        String text = "§lPvpHUD §r§7v1.0";
        int tw = font.getWidth(text);
        ctx.fill(W - tw - 8, 2, W - 2, 14, 0x88000000);
        drawBorderBottom(ctx, W - tw - 8, 2, W - 2, 14, accent);
        ctx.drawTextWithShadow(font, text, W - tw - 5, 4, COL_WHITE);
    }

    // ─── MODULE LIST ──────────────────────────────────────────────────────────
    private static void renderModuleList(DrawContext ctx, TextRenderer font, int W, int H, int accent) {
        String[] modules = getActiveModules();
        int y = H / 2 - (modules.length * 11) / 2;
        int maxW = 0;
        for (String m : modules) maxW = Math.max(maxW, font.getWidth(m));
        int x = W - maxW - 6;

        for (String mod : modules) {
            int tw = font.getWidth(mod);
            ctx.fill(x - 2, y - 1, W - 2, y + 10, 0x66000000);
            ctx.fill(W - 3, y - 1, W - 1, y + 10, accent); // barra lateral
            ctx.drawTextWithShadow(font, mod, W - tw - 4, y, COL_WHITE);
            y += 11;
        }
    }

    private static String[] getActiveModules() {
        java.util.List<String> list = new java.util.ArrayList<>();
        if (HudConfig.showHealth)    list.add("HealthBar");
        if (HudConfig.showTargetHUD) list.add("TargetHUD");
        if (HudConfig.showCrosshair) list.add("Crosshair");
        if (HudConfig.hitParticles)  list.add("HitParticles");
        if (HudConfig.showCounter)   list.add("ItemCounter");
        if (HudConfig.showCooldowns) list.add("Cooldowns");
        if (HudConfig.showCombo)     list.add("Combo");
        if (HudConfig.showPearlTimer)list.add("PearlTimer");
        if (HudConfig.rgbMode)       list.add("§cR§aG§9B §fMode");
        return list.toArray(new String[0]);
    }

    // ─── HEALTH BAR ───────────────────────────────────────────────────────────
    private static void renderHealthBar(DrawContext ctx, TextRenderer font,
                                        MinecraftClient mc, int W, int H, int accent) {
        float hp  = mc.player.getHealth();
        float max = mc.player.getMaxHealth();
        float abs = mc.player.getAbsorptionAmount();
        float pct = hp / max;

        int bw = HudConfig.barWidth;
        int bh = HudConfig.barHeight;
        int x  = HudConfig.healthX;
        int y  = H + HudConfig.healthY;

        int fillColor = pct > 0.6f ? COL_HP_HIGH : pct > 0.3f ? COL_HP_MID : COL_HP_LOW;

        ctx.fill(x - 1, y - 1, x + bw + 1, y + bh + 1, 0xAA000000);
        ctx.fill(x, y, x + (int)(bw * pct), y + bh, fillColor);
        // absorción en amarillo encima
        if (abs > 0) {
            float absPct = Math.min(abs / max, 1f);
            ctx.fill(x, y, x + (int)(bw * absPct), y + 3, 0xFFFFDD00);
        }
        drawOutline(ctx, x - 1, y - 1, x + bw + 1, y + bh + 1, 0xFF333333);
        ctx.drawTextWithShadow(font, "❤ " + (int)hp + (abs > 0 ? " §e+" + (int)abs : "") + " / " + (int)max,
                x, y - 11, fillColor);
    }

    // ─── FOOD BAR ─────────────────────────────────────────────────────────────
    private static void renderFoodBar(DrawContext ctx, TextRenderer font,
                                      MinecraftClient mc, int W, int H) {
        int food = mc.player.getHungerManager().getFoodLevel();
        float pct = food / 20f;
        int bw = HudConfig.barWidth;
        int bh = HudConfig.barHeight;
        int x  = HudConfig.foodX;
        int y  = H + HudConfig.foodY;
        int col = pct > 0.5f ? 0xFFFFAA00 : 0xFFFF5555;

        ctx.fill(x - 1, y - 1, x + bw + 1, y + bh + 1, 0xAA000000);
        ctx.fill(x, y, x + (int)(bw * pct), y + bh, col);
        drawOutline(ctx, x - 1, y - 1, x + bw + 1, y + bh + 1, 0xFF333333);
        ctx.drawTextWithShadow(font, "🍖 " + food + "/20", x, y - 11, col);
    }

    // ─── ARMOR HUD ────────────────────────────────────────────────────────────
    private static void renderArmorHUD(DrawContext ctx, TextRenderer font,
                                       MinecraftClient mc, int W, int H, int accent) {
        int armor = mc.player.getArmor();
        if (armor == 0) return;
        int x = HudConfig.healthX;
        int y = H - 80;
        ctx.drawTextWithShadow(font, "🛡 " + armor + "/20", x, y, 0xFF5599FF);
    }

    // ─── EFECTOS ──────────────────────────────────────────────────────────────
    private static void renderEffects(DrawContext ctx, TextRenderer font,
                                      MinecraftClient mc, int W, int H) {
        Collection<StatusEffectInstance> effects = mc.player.getStatusEffects();
        if (effects.isEmpty()) return;

        int x = W - 90;
        int y = 20;
        int h = effects.size() * 12 + 8;

        ctx.fill(x - 3, y - 3, x + 85, y + h, 0xAA000000);
        drawOutline(ctx, x - 3, y - 3, x + 85, y + h, 0xFF333333);

        for (StatusEffectInstance eff : effects) {
            int color = getEffectColor(eff.getEffectType());
            String name = getEffectName(eff.getEffectType());
            int amp = eff.getAmplifier() + 1;
            String time = formatTicks(eff.getDuration());
            ctx.drawTextWithShadow(font, name + " " + amp + " §7" + time, x, y, color);
            y += 12;
        }
    }

    // ─── ITEM COUNTER ─────────────────────────────────────────────────────────
    private static void renderItemCounter(DrawContext ctx, TextRenderer font,
                                          MinecraftClient mc, int W, int H, int accent) {
        int soups = 0, gap = 0, ggap = 0, pearls = 0, totems = 0, pots = 0;

        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isEmpty()) continue;
            if (s.isOf(Items.MUSHROOM_STEW) || s.isOf(Items.BEETROOT_SOUP) || s.isOf(Items.RABBIT_STEW))
                soups += s.getCount();
            else if (s.isOf(Items.GOLDEN_APPLE))          gap   += s.getCount();
            else if (s.isOf(Items.ENCHANTED_GOLDEN_APPLE)) ggap  += s.getCount();
            else if (s.isOf(Items.ENDER_PEARL))            pearls+= s.getCount();
            else if (s.isOf(Items.TOTEM_OF_UNDYING))       totems+= s.getCount();
            else if (s.isOf(Items.POTION) || s.isOf(Items.SPLASH_POTION)) pots += s.getCount();
        }

        int x = W - HudConfig.counterOffsetX;
        int y = H - 95;

        ctx.fill(x - 4, y - 4, x + 80, y + 80, 0xAA000000);
        // borde accent top
        ctx.fill(x - 4, y - 4, x + 80, y - 2, accent);
        drawOutline(ctx, x - 4, y - 4, x + 80, y + 80, 0xFF333333);

        ctx.drawTextWithShadow(font, "§7── Items ──", x, y, COL_GRAY);
        ctx.drawTextWithShadow(font, "§aStew  §f" + soups,  x, y + 12, COL_WHITE);
        ctx.drawTextWithShadow(font, "§6GAP   §f" + gap,    x, y + 22, COL_WHITE);
        ctx.drawTextWithShadow(font, "§cGGAP  §f" + ggap,   x, y + 32, COL_WHITE);
        ctx.drawTextWithShadow(font, "§5Pearl §f" + pearls,  x, y + 42, COL_WHITE);
        ctx.drawTextWithShadow(font, "§eTotem §f" + totems,  x, y + 52, COL_WHITE);
        ctx.drawTextWithShadow(font, "§bPot   §f" + pots,    x, y + 62, COL_WHITE);
    }

    // ─── COOLDOWNS ────────────────────────────────────────────────────────────
    private static void renderCooldowns(DrawContext ctx, TextRenderer font,
                                        MinecraftClient mc, int W, int H, int accent) {
        int x = W / 2 - 40;
        int y = H - 60;

        // Pearl cooldown
        float pearlCd = mc.player.getItemCooldownManager()
                .getCooldownProgress(Items.ENDER_PEARL, 0f);
        if (pearlCd > 0) {
            ctx.fill(x, y, x + 80, y + 6, 0xAA000000);
            ctx.fill(x, y, x + (int)(80 * (1 - pearlCd)), y + 6, 0xFF55FFFF);
            ctx.drawTextWithShadow(font, "§bPearl CD", x, y - 10, 0xFF55FFFF);
        }

        // Totem cooldown
        float totemCd = mc.player.getItemCooldownManager()
                .getCooldownProgress(Items.TOTEM_OF_UNDYING, 0f);
        if (totemCd > 0) {
            ctx.fill(x, y + 14, x + 80, y + 20, 0xAA000000);
            ctx.fill(x, y + 14, x + (int)(80 * (1 - totemCd)), y + 20, 0xFFFFAA00);
            ctx.drawTextWithShadow(font, "§6Totem CD", x, y + 4, 0xFFFFAA00);
        }
    }

    // ─── TARGET HUD ───────────────────────────────────────────────────────────
    private static void renderTargetHUD(DrawContext ctx, TextRenderer font,
                                        MinecraftClient mc, int W, int H, int accent) {
        if (!(mc.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult ehr)) return;
        if (!(ehr.getEntity() instanceof LivingEntity target)) return;

        float hp  = target.getHealth();
        float max = target.getMaxHealth();
        float pct = hp / max;

        int bw = 120;
        int bh = 8;
        int x  = W / 2 - bw / 2;
        int y  = H / 2 + 20;

        switch (HudConfig.targetHudStyle) {
            case 0 -> renderTargetBar(ctx, font, target, hp, max, pct, x, y, bw, bh, accent);
            case 1 -> renderTargetMinimal(ctx, font, target, hp, max, x, y, accent);
            case 2 -> renderTargetSide(ctx, font, target, hp, max, pct, W, H, accent);
        }
    }

    private static void renderTargetBar(DrawContext ctx, TextRenderer font,
                                        LivingEntity target, float hp, float max, float pct,
                                        int x, int y, int bw, int bh, int accent) {
        String name = target.getName().getString();
        int nameW = font.getWidth(name);
        int col = pct > 0.6f ? COL_HP_HIGH : pct > 0.3f ? COL_HP_MID : COL_HP_LOW;

        ctx.fill(x - 4, y - 14, x + bw + 4, y + bh + 4, 0xAA000000);
        ctx.fill(x - 4, y - 14, x + bw + 4, y - 12, accent);
        drawOutline(ctx, x - 4, y - 14, x + bw + 4, y + bh + 4, 0xFF333333);

        ctx.drawCenteredTextWithShadow(font, name, x + bw / 2, y - 12, COL_WHITE);
        ctx.fill(x, y, x + bw, y + bh, COL_DARK);
        ctx.fill(x, y, x + (int)(bw * pct), y + bh, col);
        ctx.drawCenteredTextWithShadow(font, (int)hp + " / " + (int)max, x + bw / 2, y + bh + 2, col);

        // Efectos del target
        if (HudConfig.targetShowEffects) {
            int ex = x + bw + 6;
            int ey = y;
            for (StatusEffectInstance eff : target.getStatusEffects()) {
                ctx.drawTextWithShadow(font, getEffectShort(eff.getEffectType()),
                        ex, ey, getEffectColor(eff.getEffectType()));
                ey += 10;
            }
        }
    }

    private static void renderTargetMinimal(DrawContext ctx, TextRenderer font,
                                            LivingEntity target, float hp, float max,
                                            int x, int y, int accent) {
        String text = target.getName().getString() + " §f" + (int)hp + "§7/§f" + (int)max;
        int tw = font.getWidth(text);
        ctx.fill(x - 2, y - 2, x + tw + 2, y + 10, 0xAA000000);
        ctx.fill(x - 2, y + 8, x + tw + 2, y + 10, accent);
        ctx.drawTextWithShadow(font, text, x, y, COL_WHITE);
    }

    private static void renderTargetSide(DrawContext ctx, TextRenderer font,
                                         LivingEntity target, float hp, float max, float pct,
                                         int W, int H, int accent) {
        int x = 10;
        int y = H / 2 - 20;
        int col = pct > 0.6f ? COL_HP_HIGH : pct > 0.3f ? COL_HP_MID : COL_HP_LOW;
        ctx.fill(x, y, x + 4, y + 40, 0xAA000000);
        ctx.fill(x, y + (int)(40 * (1 - pct)), x + 4, y + 40, col);
        ctx.drawTextWithShadow(font, target.getName().getString(), x + 6, y, COL_WHITE);
        ctx.drawTextWithShadow(font, (int)hp + "/" + (int)max, x + 6, y + 10, col);
    }

    // ─── INVENTORY HUD ────────────────────────────────────────────────────────
    private static void renderInventoryHUD(DrawContext ctx, TextRenderer font,
                                           MinecraftClient mc, int W, int H, int accent) {
        // Muestra los items del hotbar con cantidades
        int startX = W / 2 - 90;
        int y = H - 24;

        for (int i = 0; i < 9; i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isEmpty()) continue;
            if (s.getCount() > 1) {
                int x = startX + i * 20;
                ctx.drawTextWithShadow(font, "§f" + s.getCount(), x, y - 14, COL_WHITE);
            }
        }
    }

    // ─── CROSSHAIR ────────────────────────────────────────────────────────────
    private static void renderCrosshair(DrawContext ctx, int W, int H, int accent) {
        int cx = W / 2, cy = H / 2;
        int col = HudConfig.crosshairRGB ? HudConfig.getAccent() : COL_WHITE;

        switch (HudConfig.crosshairStyle) {
            case 0 -> { // Cruz clásica
                int s = HudConfig.crosshairSize, g = HudConfig.crosshairGap;
                ctx.fill(cx - s - g, cy - 1, cx - g, cy + 1, col);
                ctx.fill(cx + g, cy - 1, cx + s + g, cy + 1, col);
                ctx.fill(cx - 1, cy - s - g, cx + 1, cy - g, col);
                ctx.fill(cx - 1, cy + g, cx + 1, cy + s + g, col);
                if (HudConfig.crosshairDot)
                    ctx.fill(cx - 1, cy - 1, cx + 1, cy + 1, 0xFFFF5555);
            }
            case 1 -> { // Solo punto
                ctx.fill(cx - 2, cy - 2, cx + 2, cy + 2, col);
            }
            case 2 -> { // Círculo (aproximado con arcos de fill)
                int r = HudConfig.crosshairSize + 2;
                for (int deg = 0; deg < 360; deg += 10) {
                    double rad = Math.toRadians(deg);
                    int px = (int)(cx + r * Math.cos(rad));
                    int py = (int)(cy + r * Math.sin(rad));
                    ctx.fill(px, py, px + 1, py + 1, col);
                }
            }
            case 3 -> { // Arrow style (T invertida)
                int s = HudConfig.crosshairSize, g = HudConfig.crosshairGap;
                ctx.fill(cx - s - g, cy - 1, cx + s + g, cy + 1, col);
                ctx.fill(cx - 1, cy + g, cx + 1, cy + s + g, col);
                if (HudConfig.crosshairDot)
                    ctx.fill(cx - 1, cy - 1, cx + 1, cy + 1, 0xFFFF5555);
            }
        }
    }

    // ─── INFO (FPS / COORDS) ──────────────────────────────────────────────────
    private static void renderInfo(DrawContext ctx, TextRenderer font,
                                   MinecraftClient mc, int W, int H, int accent) {
        int y = 10;
        if (HudConfig.showFps) {
            int fps = mc.getCurrentFps();
            String fc = fps > 100 ? "§a" : fps > 60 ? "§e" : fps > 30 ? "§6" : "§c";
            ctx.drawTextWithShadow(font, fc + "FPS §f" + fps, 10, y, COL_WHITE); y += 11;
        }
        if (HudConfig.showCoords && mc.player != null) {
            int px = (int)mc.player.getX(), py = (int)mc.player.getY(), pz = (int)mc.player.getZ();
            ctx.drawTextWithShadow(font, "§7XYZ §f" + px + " " + py + " " + pz, 10, y, COL_WHITE);
        }
    }

    // ─── COMBO ────────────────────────────────────────────────────────────────
    public static void registerHit() {
        long now = System.currentTimeMillis();
        if (now - lastHitTime < 3000) comboCount++;
        else comboCount = 1;
        lastHitTime = now;
    }

    private static void renderCombo(DrawContext ctx, TextRenderer font, int W, int H, int accent) {
        if (comboCount < 2) return;
        long now = System.currentTimeMillis();
        if (now - lastHitTime > 3000) { comboCount = 0; return; }

        String text = "§lCombo §f" + comboCount + "x";
        int tw = font.getWidth(text);
        int x  = W / 2 - tw / 2;
        int y  = H / 2 - 30;
        ctx.fill(x - 3, y - 2, x + tw + 3, y + 11, 0xAA000000);
        ctx.fill(x - 3, y + 9, x + tw + 3, y + 11, accent);
        ctx.drawTextWithShadow(font, text, x, y, COL_WHITE);
    }

    // ─── PEARL TIMER ──────────────────────────────────────────────────────────
    public static void throwPearl() {
        pearlThrowTime = System.currentTimeMillis();
    }

    private static void renderPearlTimer(DrawContext ctx, TextRenderer font, int W, int H, int accent) {
        if (pearlThrowTime == 0) return;
        long elapsed = System.currentTimeMillis() - pearlThrowTime;
        long cd = 1000; // 1 segundo cooldown perla en 1.20
        if (elapsed > cd) { pearlThrowTime = 0; return; }

        float pct = (float) elapsed / cd;
        int x = W / 2 - 40;
        int y = H - 75;

        ctx.fill(x, y, x + 80, y + 5, 0xAA000000);
        ctx.fill(x, y, x + (int)(80 * pct), y + 5, 0xFF55FFFF);
        ctx.drawTextWithShadow(font, "§bPearl §f" + String.format("%.1f", (cd - elapsed) / 1000f) + "s",
                x, y - 11, 0xFF55FFFF);
    }

    // ─── UTILS ───────────────────────────────────────────────────────────────
    static void drawOutline(DrawContext ctx, int x1, int y1, int x2, int y2, int c) {
        ctx.fill(x1, y1, x2, y1 + 1, c);
        ctx.fill(x1, y2 - 1, x2, y2, c);
        ctx.fill(x1, y1, x1 + 1, y2, c);
        ctx.fill(x2 - 1, y1, x2, y2, c);
    }

    static void drawBorderBottom(DrawContext ctx, int x1, int y1, int x2, int y2, int c) {
        ctx.fill(x1, y2 - 1, x2, y2, c);
    }

    static String formatTicks(int ticks) {
        int s = ticks / 20;
        return s < 60 ? s + "s" : (s / 60) + "m" + (s % 60) + "s";
    }

    static int getEffectColor(StatusEffect e) {
        if (e == StatusEffects.SPEED)            return 0xFF55FFFF;
        if (e == StatusEffects.STRENGTH)         return 0xFFFF5555;
        if (e == StatusEffects.REGENERATION)     return 0xFFFF55FF;
        if (e == StatusEffects.FIRE_RESISTANCE)  return 0xFFFFAA00;
        if (e == StatusEffects.RESISTANCE)       return 0xFF5555FF;
        if (e == StatusEffects.JUMP_BOOST)       return 0xFF55FF55;
        if (e == StatusEffects.ABSORPTION)       return 0xFFFFDD00;
        if (e == StatusEffects.POISON)           return 0xFF55AA00;
        if (e == StatusEffects.SLOWNESS)         return 0xFF888888;
        if (e == StatusEffects.WEAKNESS)         return 0xFFAA0000;
        if (e == StatusEffects.NIGHT_VISION)     return 0xFF0000FF;
        return COL_WHITE;
    }

    static String getEffectName(StatusEffect e) {
        if (e == StatusEffects.SPEED)           return "§bSpeed";
        if (e == StatusEffects.STRENGTH)        return "§cStrength";
        if (e == StatusEffects.REGENERATION)    return "§dRegen";
        if (e == StatusEffects.FIRE_RESISTANCE) return "§6FireRes";
        if (e == StatusEffects.RESISTANCE)      return "§9Resist";
        if (e == StatusEffects.JUMP_BOOST)      return "§aJump";
        if (e == StatusEffects.ABSORPTION)      return "§eAbsorp";
        if (e == StatusEffects.POISON)          return "§2Poison";
        if (e == StatusEffects.SLOWNESS)        return "§7Slow";
        if (e == StatusEffects.WEAKNESS)        return "§4Weak";
        if (e == StatusEffects.NIGHT_VISION)    return "§1NightV";
        return "§fEffect";
    }

    static String getEffectShort(StatusEffect e) {
        if (e == StatusEffects.SPEED)           return "§bSPD";
        if (e == StatusEffects.STRENGTH)        return "§cSTR";
        if (e == StatusEffects.REGENERATION)    return "§dREG";
        if (e == StatusEffects.FIRE_RESISTANCE) return "§6FIR";
        if (e == StatusEffects.RESISTANCE)      return "§9RES";
        if (e == StatusEffects.ABSORPTION)      return "§eABS";
        if (e == StatusEffects.POISON)          return "§2PSN";
        return "§fEFF";
    }
}
