package com.example.rendertoggle.test;

import com.example.rendertoggle.client.RenderToggleClientMod;
import com.example.rendertoggle.client.RenderToggleConfigScreen;
import com.example.rendertoggle.compat.ShaderControl;
import com.example.rendertoggle.config.ModConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

import java.util.Arrays;

public class RenderToggleClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        // The test framework resets the game options after the mod applied its distance on startup.
        // Like a manual change in Video Settings, the mod keeps that until the shader state changes.
        context.runOnClient(client -> RenderToggleClientMod.requestReapply());
        context.waitTick();

        if (ShaderControl.IRIS_LOADED) {
            runIrisTest(context);
        } else {
            runSimulatedTest(context);
        }
    }

    /** Needs Iris with a shader pack selected but disabled (./gradlew runClientGameTest -PwithIris). */
    private void runIrisTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);
            context.waitTicks(5);
            assertEquals(false, ShaderControl.isShaderActive(), "shader state on join");
            assertRenderDistance(context, 32, "initial (shaders off)");

            // Both mods use K: Iris toggles, this mod must not toggle a second time
            context.getInput().pressKey(RenderToggleClientGameTest::toggleKey);
            context.waitFor(client -> ShaderControl.isShaderActive(), 100);
            context.waitTicks(2);
            assertRenderDistance(context, 24, "after K with shared key (shaders on)");
            context.takeScreenshot("render-toggle-iris-on");

            context.getInput().pressKey(RenderToggleClientGameTest::toggleKey);
            context.waitFor(client -> !ShaderControl.isShaderActive(), 100);
            context.waitTicks(2);
            assertRenderDistance(context, 32, "after K with shared key (shaders off)");

            // On its own key, this mod toggles Iris itself
            context.runOnClient(client -> {
                toggleKey(client.options).setKey(InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_J));
                KeyMapping.resetMapping();
            });
            context.getInput().pressKey(RenderToggleClientGameTest::toggleKey);
            context.waitFor(client -> ShaderControl.isShaderActive(), 100);
            context.waitTicks(2);
            assertRenderDistance(context, 24, "after own key (shaders on)");
        }
    }

    private void runSimulatedTest(ClientGameTestContext context) {
        // The config screen opens, and closing it saves and re-applies the settings
        context.setScreen(() -> new RenderToggleConfigScreen(null));
        context.waitForScreen(RenderToggleConfigScreen.class);
        context.takeScreenshot("render-toggle-config-screen");
        context.runOnClient(client -> {
            ModConfig.getInstance().shaderOnDistance = 6;
            ModConfig.getInstance().shaderOffDistance = 192;
        });
        context.setScreen(() -> null);
        // Closing the screen writes the slider values back, overriding the values set above
        context.runOnClient(client -> {
            assertEquals(24, ModConfig.getInstance().shaderOnDistance, "shaderOnDistance after closing screen");
            assertEquals(32, ModConfig.getInstance().shaderOffDistance, "shaderOffDistance after closing screen");
        });

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);
            context.waitTicks(5);

            // Shaders start off, so the "off" distance is applied
            assertRenderDistance(context, 32, "initial (shaders off)");

            context.getInput().pressKey(RenderToggleClientGameTest::toggleKey);
            context.waitTicks(2);
            assertEquals(true, ShaderControl.isShaderActive(), "shader state after first press");
            assertRenderDistance(context, 24, "after first press (shaders on)");
            context.takeScreenshot("render-toggle-shaders-on");

            context.getInput().pressKey(RenderToggleClientGameTest::toggleKey);
            context.waitTicks(2);
            assertEquals(false, ShaderControl.isShaderActive(), "shader state after second press");
            assertRenderDistance(context, 32, "after second press (shaders off)");

            // An out-of-range config value is clamped instead of being rejected by the game
            context.runOnClient(client -> {
                ModConfig.getInstance().shaderOffDistance = 192;
                RenderToggleClientMod.requestReapply();
            });
            context.waitTicks(2);
            assertRenderDistance(context, 32, "clamped off distance");
        }
    }

    private static KeyMapping toggleKey(Options options) {
        return Arrays.stream(options.keyMappings)
                .filter(mapping -> mapping.getName().equals("key.render-toggle.toggle"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Render Toggle key mapping not registered"));
    }

    private static void assertRenderDistance(ClientGameTestContext context, int expected, String what) {
        int actual = context.computeOnClient((Minecraft client) -> client.options.renderDistance().get());
        assertEquals(expected, actual, "render distance " + what);
    }

    private static void assertEquals(Object expected, Object actual, String what) {
        if (!expected.equals(actual)) {
            throw new AssertionError(what + ": expected " + expected + " but was " + actual);
        }
    }
}
