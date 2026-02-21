package in.GameDev.SummerTerrain.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    // -------------------------------------------------------
    // Reference to main game (for screen switching)
    // -------------------------------------------------------
    private final in.GameDev.SummerTerrain.SummerTerrainGame game;

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

    // -------------------------------------------------------
    // Constructor - requires game reference
    // -------------------------------------------------------
    public GameScreen(in.GameDev.SummerTerrain.SummerTerrainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        System.out.println("=== GameScreen.show() START ===");

        // 1️⃣ Load the TMX map
        map = new TmxMapLoader().load("maps/Main_map.tmx");
        System.out.println("✓ Map loaded");

        // Calculate map dimensions
        int mapWidthInTiles  = map.getProperties().get("width",     Integer.class);
        int mapHeightInTiles = map.getProperties().get("height",    Integer.class);
        int tilePixelWidth   = map.getProperties().get("tilewidth", Integer.class);
        int tilePixelHeight  = map.getProperties().get("tileheight",Integer.class);

        mapWidth  = mapWidthInTiles  * tilePixelWidth;
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

        // 4️⃣ Create SpriteBatch
        batch = new SpriteBatch();
        System.out.println("✓ SpriteBatch created");

        // 5️⃣ Initialize collision system
        try {
            collisionLayer  = new CollisionLayer(map, "Base", "Assets");
            collisionHandler = new CollisionHandler(collisionLayer);
        } catch (Exception e) {
            System.err.println("✗ Failed to initialize collision system!");
            e.printStackTrace();
        }

        // 6️⃣ Create player at spawn position
        player = new Player(mapWidth / 2, mapHeight / 2);
        player.setCollisionHandler(collisionHandler);

        System.out.println("=== GameScreen.show() END ===");
    }

    @Override
    public void render(float delta) {

        // ── ESC key → open Pause Screen ───────────────────
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("⏸ ESC pressed - opening PauseScreen");
            game.setScreen(new PauseScreen(game, this));
            return; // skip rendering this frame
        }

        // Clear screen
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        // Update player
        player.update(delta);

        // Update camera
        updateCamera();

        // Render map (background)
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Render player (on top of map)
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();
    }

    // -------------------------------------------------------
    // Camera follow
    // -------------------------------------------------------
    private void updateCamera() {
        float targetX = player.getCenterX();
        float targetY = player.getCenterY();

        float halfViewportWidth  = camera.viewportWidth  / 2;
        float halfViewportHeight = camera.viewportHeight / 2;

        targetX = MathUtils.clamp(targetX, halfViewportWidth,  mapWidth  - halfViewportWidth);
        targetY = MathUtils.clamp(targetY, halfViewportHeight, mapHeight - halfViewportHeight);

        float lerpFactor = 0.1f;
        camera.position.x += (targetX - camera.position.x) * lerpFactor;
        camera.position.y += (targetY - camera.position.y) * lerpFactor;

        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth  = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        System.out.println("=== GameScreen.dispose() START ===");
        map.dispose();
        mapRenderer.dispose();
        if (player != null) player.dispose();
        if (batch  != null) batch.dispose();
        System.out.println("=== GameScreen.dispose() END ===");
    }
}
