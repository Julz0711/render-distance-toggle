package com.example.rendertoggle.compat;

import com.example.rendertoggle.client.RenderToggleConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** Adds the config button in Mod Menu. Only loaded by Mod Menu itself. */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return RenderToggleConfigScreen::new;
    }
}
