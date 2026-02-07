package in.GameDev.SummerTerrain.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import in.GameDev.SummerTerrain.entities.Player;
import in.GameDev.SummerTerrain.collision.CollisionLayer;
import in.GameDev.SummerTerrain.collision.CollisionHandler;

public class GameScreen implements Screen {

    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private SpriteBatch batch;
    private Player player;

    // Collision system
    private CollisionLayer collisionLayer;
    private CollisionHandler collisionHandler;

    // Map boundaries (in pixels)
    private float mapWidth;
    private float mapHeight;

    @Override
    public void show() {
        System.out.println("=== GameScreen.show() START ===");

        // 1️⃣ Load the TMX map
        map = new TmxMapLoader().load("maps/Main_map.tmx");
        System.out.println("✓ Map loaded");

        // Calculate map dimensions
        int mapWidthInTiles = map.getProperties().get("width", Integer.class);
        int mapHeightInTiles = map.getProperties().get("height", Integer.class);
        int tilePixelWidth = map.getProperties().get("tilewidth", Integer.class);
        int tilePixelHeight = map.getProperties().get("tileheight", Integer.class);

        mapWidth = mapWidthInTiles * tilePixelWidth;
        mapHeight = mapHeightInTiles * tilePixelHeight;
        System.out.println("✓ Map size: " + mapWidth + "x" + mapHeight + " pixels");

        // 2️⃣ Create renderer
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
        System.out.println("✓ Map renderer created");

        // 3️⃣ Setup camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        camera.update();
        System.out.println("✓ Camera created: " + camera.viewportWidth + "x" + camera.viewportHeight);

        // 4️⃣ CREATE SPRITEBATCH
        batch = new SpriteBatch();
        System.out.println("✓ SpriteBatch created");

        // 5️⃣ INITIALIZE COLLISION SYSTEM
        // Reading collision from BOTH Base and Assets layers
        try {
            collisionLayer = new CollisionLayer(map, "Base", "Assets");
            collisionHandler = new CollisionHandler(collisionLayer);
        } catch (Exception e) {
            System.err.println("✗ Failed to initialize collision system!");
            e.printStackTrace();
        }

        // 6️⃣ CREATE PLAYER at spawn position
        player = new Player(mapWidth / 2, mapHeight / 2);
        player.setCollisionHandler(collisionHandler);

        System.out.println("=== GameScreen.show() END ===");
    }

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        // UPDATE PLAYER
        player.update(delta);

        // UPDATE CAMERA to follow player
        updateCamera();

        // Render map FIRST (background)
        mapRenderer.setView(camera);
        mapRenderer.render();

        // RENDER PLAYER (on top of map)
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();
    }

    /**
     * Update camera to follow the player smoothly
     */
    private void updateCamera() {
        // Get player center position
        float targetX = player.getCenterX();
        float targetY = player.getCenterY();

        // Clamp camera to map boundaries
        float halfViewportWidth = camera.viewportWidth / 2;
        float halfViewportHeight = camera.viewportHeight / 2;

        // Prevent camera from showing area outside the map
        targetX = MathUtils.clamp(targetX, halfViewportWidth, mapWidth - halfViewportWidth);
        targetY = MathUtils.clamp(targetY, halfViewportHeight, mapHeight - halfViewportHeight);

        // Smooth camera movement (lerp for smooth following)
        float lerpFactor = 0.1f;
        camera.position.x += (targetX - camera.position.x) * lerpFactor;
        camera.position.y += (targetY - camera.position.y) * lerpFactor;

        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("GameScreen resized to: " + width + "x" + height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        System.out.println("=== GameScreen.dispose() START ===");
        map.dispose();
        mapRenderer.dispose();

        if (player != null) {
            player.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
        System.out.println("=== GameScreen.dispose() END ===");
    }
}
