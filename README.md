# Summer Terrain
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![OpenJDK](https://img.shields.io/badge/OpenJDK-Temurin%2017-red.svg)](https://adoptium.net/)
[![LibGDX](https://img.shields.io/badge/LibGDX-1.11-blue.svg)](https://libgdx.com/)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-green.svg)](https://gradle.org/)

A **Java-based 2D game project** built using the [LibGDX](https://libgdx.com/) framework and powered by [Eclipse Temurin OpenJDK](https://adoptium.net/).

# About

Summer Terrain is 2D Pixel Art exploration game built using pure java. The core game engine was built using libGDX framework.

It uses [Box2D](https://box2d.org/) as a physic system for collision handling and movement.
It also uses [Ashley](https://github.com/libgdx/ashley) entity component system.And for keyboard controllers it uses [Controllers](https://github.com/libgdx/gdx-controllers).

This project is designed for learning:

- Game loop structure
- Understand LibGDX frameworks
- Core Java
- Gradle-based multi-platform setup


## Game 

Game is still in early prototype stage. Making little progress...

The controls are:
- Arrows/WASD keys to move around
- Space to attack which is also used to interact with chests
- I to open/close inventory
- Esc to pause/resume

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `html`: Web platform using GWT and WebGL. Supports only Java projects.

## Setup Guide

Follow these steps to run the project on your system.

---

### Install OpenJDK (Eclipse Temurin)

Download and install **JDK 17** from Eclipse Temurin.

After installation, verify using:

```bash
java -version
```

Expected output should contain:

```
openjdk version "17.x.x"
```

---

### Clone the Repository

```bash
git clone <your-repository-url>
cd SummerTerrain
```

---

### Open in IDE
IDE : IntelliJ IDEA (Recommended)

1. Open IntelliJ IDEA
2. Click **Open**
3. Select the project folder
4. Allow Gradle to sync

### Build the Project

On macOS/Linux:

```bash
./gradlew build
```

On Windows:

```bash
gradlew build
```

---

### Run Desktop Version

On macOS/Linux:

```bash
./gradlew desktop:run
```

On Windows:

```bash
gradlew desktop:run
```

---
## Project Structure

```
SummerTerrain/
│
├── assets/           # Maps, tilesets, textures
├── core/             # Game logic
├── lwjgl/            # Desktop launcher
├── html/             # HTML web Launcher
├── build.gradle
└── settings.gradle
```
---

## Credits
For building these game I have used few Online Resources.

- Map : 
