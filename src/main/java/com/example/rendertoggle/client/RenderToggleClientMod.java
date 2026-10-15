package com.example.rendertoggle.client;

import com.example.rendertoggle.RenderToggleMod;
import com.example.rendertoggle.compat.ShaderControl;
import com.example.rendertoggle.config.ModConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RenderToggleClientMod implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(RenderToggleMod.MOD_ID, "general"));
    private static final String IRIS_TOGGLE_KEY = "iris.keybind.toggleShaders";

    private static KeyMapping toggleKey;
    // Shader state the render distance was last applied for. null means it must be (re)applied.
    private static Boolean appliedShaderState = null;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.render-toggle.toggle",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_K,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                onTogglePressed(client);
            }

            // Follow the real shader state, so toggling shaders from Iris's own key
            // or its menu also switches the render distance.
            boolean shaderActive = ShaderControl.isShaderActive();
            if (appliedShaderState == null || shaderActive != appliedShaderState) {
                appliedShaderState = shaderActive;
                applyRenderDistance(client, shaderActive);
            }
        });
    }

    /** Re-applies the render distance for the current shader state, e.g. after the config changed. */
    public static void requestReapply() {
        appliedShaderState = null;
    }

    private static void onTogglePressed(Minecraft client) {
        // Iris toggles shaders on its own key. If both mods use the same key, Iris already
        // handles this press; toggling again here would immediately undo it.
        if (ShaderControl.IRIS_LOADED && isSharedWithIrisToggle(client)) {
            return;
        }
        ShaderControl.toggle();
    }

    private static boolean isSharedWithIrisToggle(Minecraft client) {
        for (KeyMapping mapping : client.options.keyMappings) {
            if (mapping != toggleKey && IRIS_TOGGLE_KEY.equals(mapping.getName()) && mapping.same(toggleKey)) {
                return true;
            }
        }
        return false;
    }

    private static void applyRenderDistance(Minecraft client, boolean shaderActive) {
        ModConfig config = ModConfig.getInstance();
        int newDistance = shaderActive ? config.shaderOnDistance : config.shaderOffDistance;

        // renderDistance is an OptionInstance<Integer>. Values outside its range are rejected
        // (and reset to the default), so clamp to what the game allows on this machine.
        OptionInstance<Integer> renderDistance = client.options.renderDistance();
        if (renderDistance.values() instanceof OptionInstance.IntRangeBase range) {
            newDistance = Mth.clamp(newDistance, range.minInclusive(), range.maxInclusive());
        }
        renderDistance.set(newDistance);

        if (config.showMessage && client.player != null) {
            String key = ShaderControl.IRIS_LOADED ? "message.render-toggle.status" : "message.render-toggle.status.simulated";
            client.player.sendOverlayMessage(Component.translatable(key,
                    Component.translatable(shaderActive ? "options.on" : "options.off"),
                    renderDistance.get()));
        }
    }
}
