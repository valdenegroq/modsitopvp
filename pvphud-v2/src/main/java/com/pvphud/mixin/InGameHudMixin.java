package com.pvphud.mixin;

import com.pvphud.hud.PvpHudRenderer;
import com.pvphud.particles.PvpParticleManager;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void hideHealth(DrawContext ctx, CallbackInfo ci) { ci.cancel(); }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void hideFood(DrawContext ctx, CallbackInfo ci) { ci.cancel(); }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private void hideArmor(DrawContext ctx, CallbackInfo ci) { ci.cancel(); }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void hideEffects(DrawContext ctx, CallbackInfo ci) { ci.cancel(); }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void hideCrosshair(DrawContext ctx, CallbackInfo ci) { ci.cancel(); }
}
