# Render Toggle — Minecraft 26.3

Keep Minecraft's render distance in sync with your shaders. Use a shorter distance with shaders enabled and a longer one when they are off.

## Features

- **Follows Iris:** changing shader state through Iris's key, its menu, or this mod updates the render distance.
- **One toggle key:** **K** toggles shaders. If Iris shares the same binding, Iris handles the press so the two mods do not toggle twice.
- **Configurable distances:** defaults are **24 chunks with shaders** and **32 without**; values are clamped to the game's allowed range.
- **Settings screen:** adjust options through Mod Menu.
- **Works without Iris:** the key flips a simulated state and switches render distance.

This repository targets **Minecraft 26.3**. For the older 1.21.10 implementation, see [toggleRenderDistance](https://github.com/Julz0711/toggleRenderDistance).

## Install

1. Install Fabric Loader **0.19.5+** for Minecraft **26.3** and the matching Fabric API.
2. Put the built mod JAR in your `mods/` folder.
3. Optionally install Iris for actual shader control and Mod Menu for the settings screen. Install Iris's own dependencies as required.

Minecraft 26.3 requires **Java 25**. Change the key under **Controls → Key Binds → Render Toggle**.

## Configure

Open **Mods → Render Toggle → config**, or edit `.minecraft/config/render-toggle.json`:

```json
{
  "shaderOnDistance": 24,
  "shaderOffDistance": 32,
  "showMessage": true
}
```

`showMessage` controls the status message above the hotbar.

## Build and test

The included Gradle wrapper and Java toolchain configuration can download Java 25. A JDK 17+ can bootstrap the wrapper.

```sh
git clone https://github.com/Julz0711/render-distance-toggle.git
cd render-distance-toggle
./gradlew build
```

The playable JAR is written to `build/libs/`. On Windows, use `gradlew.bat`.

| Command | Purpose |
| --- | --- |
| `./gradlew runClient` | Launch the development client with Mod Menu. |
| `./gradlew runClient -PwithIris` | Include Iris and Sodium in the development client. |
| `./gradlew runClientGameTest` | Run automated client game tests; requires a graphical display. |

## Code map

| Path | Responsibility |
| --- | --- |
| [client/](src/main/java/com/example/rendertoggle/client/) | Key handling, shader-state tracking, distance updates, and settings UI. |
| [compat/](src/main/java/com/example/rendertoggle/compat/) | Iris, shader-control, and Mod Menu integrations. |
| [config/](src/main/java/com/example/rendertoggle/config/) | Persisted configuration. |
| [src/gametest/](src/gametest/) | Minecraft client tests. |
