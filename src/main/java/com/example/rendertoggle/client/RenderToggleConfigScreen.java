package com.example.rendertoggle.client;

import com.example.rendertoggle.compat.ShaderControl;
import com.example.rendertoggle.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class RenderToggleConfigScreen extends OptionsSubScreen {
    private final OptionInstance<Integer> shaderOnDistance;
    private final OptionInstance<Integer> shaderOffDistance;
    private final OptionInstance<Boolean> showMessage;

    public RenderToggleConfigScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("render-toggle.config.title"));
        ModConfig config = ModConfig.getInstance();
        int maxDistance = maxRenderDistance(this.options);

        this.shaderOnDistance = distanceOption("render-toggle.config.shaderOnDistance", config.shaderOnDistance, maxDistance);
        this.shaderOffDistance = distanceOption("render-toggle.config.shaderOffDistance", config.shaderOffDistance, maxDistance);
        this.showMessage = OptionInstance.createBoolean("render-toggle.config.showMessage",
                OptionInstance.cachedConstantTooltip(Component.translatable("render-toggle.config.showMessage.tooltip")),
                config.showMessage);
    }

    @Override
    protected void addOptions() {
        if (!ShaderControl.IRIS_LOADED) {
            this.list.addHeader(Component.translatable("render-toggle.config.irisMissing"));
        }
        this.list.addBig(this.shaderOnDistance);
        this.list.addBig(this.shaderOffDistance);
        this.list.addBig(this.showMessage);
    }

    @Override
    public void removed() {
        super.removed();
        ModConfig config = ModConfig.getInstance();
        config.shaderOnDistance = this.shaderOnDistance.get();
        config.shaderOffDistance = this.shaderOffDistance.get();
        config.showMessage = this.showMessage.get();
        ModConfig.save();
        RenderToggleClientMod.requestReapply();
    }

    private static OptionInstance<Integer> distanceOption(String captionKey, int value, int maxDistance) {
        return new OptionInstance<>(
                captionKey,
                OptionInstance.cachedConstantTooltip(Component.translatable(captionKey + ".tooltip")),
                (caption, chunks) -> Options.genericValueLabel(caption, Component.translatable("options.chunks", chunks)),
                new OptionInstance.IntRange(2, maxDistance),
                Math.clamp(value, 2, maxDistance),
                chunks -> {
                });
    }

    /** The highest render distance the game allows (32, or 16 on low-memory 32-bit systems). */
    private static int maxRenderDistance(Options options) {
        if (options.renderDistance().values() instanceof OptionInstance.IntRangeBase range) {
            return range.maxInclusive();
        }
        return 32;
    }
}
