package com.example.rendertoggle.compat;

import net.irisshaders.iris.api.v0.IrisApi;

/**
 * All calls into Iris live here, so this class is only loaded when Iris is installed.
 * Check {@link ShaderControl#IRIS_LOADED} before using it.
 */
final class IrisCompat {
    private IrisCompat() {
    }

    /** True only when shaders are enabled and a shader pack is actually loaded. */
    static boolean isShaderPackInUse() {
        return IrisApi.getInstance().isShaderPackInUse();
    }

    static boolean areShadersEnabled() {
        return IrisApi.getInstance().getConfig().areShadersEnabled();
    }

    static void setShadersEnabled(boolean enabled) {
        IrisApi.getInstance().getConfig().setShadersEnabledAndApply(enabled);
    }
}
