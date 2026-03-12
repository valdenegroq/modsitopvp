package com.pvphud.particles;

import com.pvphud.config.HudConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public class PvpParticleManager {

    /**
     * Spawnea partículas al golpear una entidad.
     * Llamado desde el mixin de ataque.
     */
    public static void spawnHitParticles(double x, double y, double z) {
        if (!HudConfig.hitParticles) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        switch (HudConfig.hitParticleStyle) {
            case 0 -> { // Estrellas / crit
                for (int i = 0; i < 6; i++) {
                    mc.world.addParticle(ParticleTypes.CRIT,
                            x + (Math.random() - 0.5) * 0.5,
                            y + Math.random() * 0.5,
                            z + (Math.random() - 0.5) * 0.5,
                            (Math.random() - 0.5) * 0.3,
                             Math.random() * 0.3,
                            (Math.random() - 0.5) * 0.3);
                }
            }
            case 1 -> { // Corazones
                for (int i = 0; i < 3; i++) {
                    mc.world.addParticle(ParticleTypes.HEART,
                            x + (Math.random() - 0.5) * 0.3,
                            y + 0.5 + Math.random() * 0.3,
                            z + (Math.random() - 0.5) * 0.3,
                            0, 0.1, 0);
                }
            }
            case 2 -> { // Crit mágico
                for (int i = 0; i < 8; i++) {
                    mc.world.addParticle(ParticleTypes.ENCHANTED_HIT,
                            x + (Math.random() - 0.5) * 0.5,
                            y + Math.random() * 0.6,
                            z + (Math.random() - 0.5) * 0.5,
                            (Math.random() - 0.5) * 0.2,
                             Math.random() * 0.2,
                            (Math.random() - 0.5) * 0.2);
                }
            }
        }

        // Blood extra
        if (HudConfig.hitBlood) {
            for (int i = 0; i < 4; i++) {
                mc.world.addParticle(ParticleTypes.DAMAGE_INDICATOR,
                        x, y + 1, z,
                        (Math.random() - 0.5) * 0.4,
                        0.2 + Math.random() * 0.2,
                        (Math.random() - 0.5) * 0.4);
            }
        }
    }

    /**
     * Partículas al lanzar perla.
     */
    public static void spawnPearlParticles(double x, double y, double z) {
        if (!HudConfig.pearlParticles) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        for (int i = 0; i < 10; i++) {
            mc.world.addParticle(ParticleTypes.PORTAL,
                    x + (Math.random() - 0.5),
                    y + Math.random(),
                    z + (Math.random() - 0.5),
                    (Math.random() - 0.5) * 0.5,
                    (Math.random() - 0.5) * 0.5,
                    (Math.random() - 0.5) * 0.5);
        }
    }

    /**
     * Partículas al activar tótem.
     */
    public static void spawnTotemParticles(double x, double y, double z) {
        if (!HudConfig.totemParticles) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        for (int i = 0; i < 20; i++) {
            mc.world.addParticle(ParticleTypes.TOTEM_OF_UNDYING,
                    x + (Math.random() - 0.5) * 1.5,
                    y + Math.random() * 2,
                    z + (Math.random() - 0.5) * 1.5,
                    (Math.random() - 0.5) * 0.5,
                    0.3 + Math.random() * 0.5,
                    (Math.random() - 0.5) * 0.5);
        }
    }
}
