# SummerTerrain

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## File Structure

```SummerTerrain/
│
├── assets/                          # 🔥 SHARED GAME ASSETS (used by BOTH)
│   ├── maps/
│   │   ├── Main_map.tmx
│   │   ├── interiors.tmx
│   │   └── campus.tsx
│   │
│   ├── tilesets/
│   │   ├── campus_tiles.png
│   │   ├── buildings.png
│   │   └── nature.png
│   │
│   ├── characters/
│   │   ├── player/
│   │   │   ├── idle.png
│   │   │   ├── walk_up.png
│   │   │   ├── walk_down.png
│   │   │   ├── walk_left.png
│   │   │   └── walk_right.png
│   │   │
│   │   └── npc/
│   │       ├── student.png
│   │       └── professor.png
│   │
│   ├── objects/
│   │   ├── benches.png
│   │   ├── doors.png
│   │   └── lamps.png
│   │
│   ├── ui/
│   │   ├── buttons/
│   │   ├── icons/
│   │   └── fonts/
│   │       ├── pixel.fnt
│   │       └── pixel.png
│   │
│   ├── audio/
│   │   ├── music/
│   │   │   ├── campus_theme.mp3
│   │   │   └── night_theme.mp3
│   │   └── sfx/
│   │       ├── step.wav
│   │       ├── interact.wav
│   │       └── door.wav
│   │
│   └── shaders/                     # (Optional – later)
│       └── lighting.frag
│
├── core/                            # 🔥 MAIN GAME LOGIC (PLATFORM-INDEPENDENT)
│   └── src/
│       └── in/
│           └── hauntedcampus/
│               │
│               ├── HauntedCampusGame.java   # Main Game class
│               │
│               ├── screens/
│               │   ├── SplashScreen.java
│               │   ├── MenuScreen.java
│               │   ├── GameScreen.java
│               │   ├── PauseScreen.java
│               │   └── GameOverScreen.java
│               │
│               ├── world/
│               │   ├── GameMap.java          # Abstract map
│               │   ├── TiledGameMap.java     # TMX implementation
│               │   └── MapManager.java
│               │
│               ├── entities/
│               │   ├── Entity.java           # Base entity
│               │   ├── Player.java
│               │   ├── NPC.java
│               │   └── Enemy.java
│               │
│               ├── input/
│               │   ├── GameInputProcessor.java
│               │   ├── KeyboardInput.java
│               │   └── InputMapper.java
│               │
│               ├── collision/
│               │   ├── CollisionHandler.java
│               │   ├── CollisionLayer.java
│               │   └── RectangleCollider.java
│               │
│               ├── camera/
│               │   └── GameCamera.java
│               │
│               ├── ui/
│               │   ├── Hud.java
│               │   ├── DialogueBox.java
│               │   └── InventoryUI.java
│               │
│               ├── audio/
│               │   ├── AudioManager.java
│               │   └── MusicManager.java
│               │
│               ├── utils/
│               │   ├── Constants.java
│               │   ├── AssetLoader.java
│               │   ├── SaveManager.java
│               │   └── DebugUtils.java
│               │
│               └── enums/
│                   ├── Direction.java
│                   ├── GameState.java
│                   └── EntityType.java
│
├── desktop/                         # 🖥️ DESKTOP LAUNCHER
│   └── src/
│       └── in/
│           └── hauntedcampus/
│               └── DesktopLauncher.java
│
├── html/                            # 🌐 HTML (GWT) VERSION
│   ├── src/
│   │   └── in/
│   │       └── hauntedcampus/
│   │           └── HtmlLauncher.java
│   │
│   ├── webapp/
│   │   ├── index.html
│   │   ├── styles.css
│   │   └── favicon.png
│   │
│   └── assets/                      # (Auto-linked to main assets)
│
├── gradle/
├── build.gradle
├── settings.gradle
└── gradlew
```

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `html`: Web platform using GWT and WebGL. Supports only Java projects.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `html:dist`: compiles GWT sources. The compiled application can be found at `html/build/dist`: you can use any HTTP server to deploy it.
- `html:superDev`: compiles GWT sources and runs the application in SuperDev mode. It will be available at [localhost:8080/html](http://localhost:8080/html). Use only during development.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
