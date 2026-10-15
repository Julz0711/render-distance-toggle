package com.example.rendertoggle.compat;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Reads and changes the shader state. Uses Iris when it is installed,
 * otherwise falls back to a simulated on/off flag.
 */
public final class ShaderControl {
    public static final boolean IRIS_LOADED = FabricLoader.getInstance().isModLoaded("iris");

    private static boolean simulatedEnabled = false;

    private ShaderControl() {
    }

    public static boolean isShaderActive() {
        return IRIS_LOADED ? IrisCompat.isShaderPackInUse() : simulatedEnabled;
    }

    public static void toggle() {
        if (IRIS_LOADED) {
            IrisCompat.setShadersEnabled(!IrisCompat.areShadersEnabled());
        } else {
            simulatedEnabled = !simulatedEnabled;
        }
    }
}
