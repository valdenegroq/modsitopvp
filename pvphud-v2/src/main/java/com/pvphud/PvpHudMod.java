package com.pvphud;

import com.pvphud.hud.PvpHudRenderer;
import com.pvphud.menu.PvpMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PvpHudMod implements ClientModInitializer {

    public static final String MOD_ID = "pvphud";
    private static KeyBinding menuKey;

    @Override
    public void onInitializeClient() {
        // Tecla K → abrir menú
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pvphud.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "PvpHUD"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                if (client.currentScreen == null)
                    client.setScreen(new PvpMenuScreen());
            }
        });

        HudRenderCallback.EVENT.register(PvpHudRenderer::render);
        System.out.println("[PvpHUD] Listo — K para abrir menú");
    }
}
