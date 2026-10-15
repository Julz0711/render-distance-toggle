# Render Toggle Mod

A Minecraft Fabric mod for 26.3 that switches your render distance depending on whether shaders are on.

## Features

- **Follows Iris**: With [Iris](https://irisshaders.net/) installed, the mod reads whether a shader pack is
  actually in use. Toggling shaders any way you like (Iris's key, the Iris menu, or this mod's key) switches
  to the matching render distance.
- **Toggle Key**: `K` toggles shaders (rebindable under Controls → Key Binds → Render Toggle).
  Iris also uses `K` by default. When both share a key, Iris does the toggling and this mod only adjusts
  the render distance, so the two never cancel each other out.
- **Render Distance**:
  - Shaders on: 24 chunks
  - Shaders off: 32 chunks
  - Values are clamped to what the game allows (2 to 32 in vanilla).
- **Settings screen**: Open it from [Mod Menu](https://modrinth.com/mod/modmenu) (Mods → Render Toggle → config button).
- **Without Iris**: The key flips a simulated shader state and only the render distance changes.

## Installation

1.  Install [Fabric Loader](https://fabricmc.net/use/installer/) 0.19.5 or newer for Minecraft 26.3.
2.  Install [Fabric API](https://modrinth.com/mod/fabric-api) for 26.3.
3.  Download the mod jar and place it in your `mods` folder.
4.  (Optional) Install [Iris Shaders](https://irisshaders.net/) for real shader support and
    [Mod Menu](https://modrinth.com/mod/modmenu) for the settings screen.

## Building from Source

This project uses Gradle via the included wrapper.

### Prerequisites

- Any JDK 17+ to start the wrapper. Minecraft 26.3 requires Java 25; Gradle downloads
  it automatically (see `gradle/gradle-daemon-jvm.properties` and the `toolchain` block in `build.gradle`).

### Build Command

```bash
./gradlew build
```

The output jar will be in `build/libs/`.

### Running and Testing

```bash
./gradlew runClient                 # dev client with Mod Menu
./gradlew runClient -PwithIris      # ...plus Iris and Sodium
./gradlew runClientGameTest         # automated in-game tests (src/gametest)
```

## Configuration

Use the settings screen in Mod Menu, or edit `.minecraft/config/render-toggle.json`:

```json
{
  "shaderOnDistance": 24,
  "shaderOffDistance": 32,
  "showMessage": true
}
```

`showMessage` controls the status line shown above the hotbar when the render distance changes.
