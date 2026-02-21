package in.GameDev.SummerTerrain.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen implements Screen {

    // -------------------------------------------------------
    // Constants
    // -------------------------------------------------------
    private static final float SCREEN_WIDTH  = 800f;
    private static final float SCREEN_HEIGHT = 600f;
    private static final float BTN_WIDTH     = 192f;
    private static final float BTN_HEIGHT    = 64f;
    private static final float BANNER_WIDTH  = 400f;
    private static final float BANNER_HEIGHT = 80f;

    // -------------------------------------------------------
    // Core
    // -------------------------------------------------------
    private final in.GameDev.SummerTerrain.SummerTerrainGame game;
    private SpriteBatch        batch;
    private OrthographicCamera camera;
    private BitmapFont         font;
    private GlyphLayout        glyphLayout;
    private Vector3            touchPos;

    // -------------------------------------------------------
    // Textures
    // -------------------------------------------------------
    private Texture backgroundTexture;  // 👈 NEW: background image
    private Texture bannerTexture;

    private Texture btnPlayNormal,  btnPlayHover,  btnPlayPressed;
    private Texture btnQuitNormal,  btnQuitHover,  btnQuitPressed;

    private Texture iconPlay;
    private Texture iconCross;

    // -------------------------------------------------------
    // Button rectangles
    // -------------------------------------------------------
    private Rectangle playButtonRect;
    private Rectangle quitButtonRect;

    // -------------------------------------------------------
    // Button states
    // -------------------------------------------------------
    private boolean playHovered, quitHovered;
    private boolean playPressed, quitPressed;

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------
    public MainMenuScreen(in.GameDev.SummerTerrain.SummerTerrainGame game) {
        this.game = game;
    }

    // -------------------------------------------------------
    // Show
    // -------------------------------------------------------
    @Override
    public void show() {
        System.out.println("=== MainMenuScreen.show() START ===");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch       = new SpriteBatch();
        font        = new BitmapFont();
        font.getData().setScale(2.5f);  // slightly bigger font
        glyphLayout = new GlyphLayout();
        touchPos    = new Vector3();

        // ── Load textures ──────────────────────────────────

        // Background image
        // Place the image at: assets/ui/main_menu_bg.png
        backgroundTexture = new Texture("objects/MainMenu.png");

        // Title banner
        bannerTexture = new Texture("ui/UI_Flat_Banner01a.png");

        // Play button states
        btnPlayNormal  = new Texture("ui/UI_Flat_Button01a_1.png");
        btnPlayHover   = new Texture("ui/UI_Flat_Button01a_2.png");
        btnPlayPressed = new Texture("ui/UI_Flat_Button01a_3.png");

        // Quit button states
        btnQuitNormal  = new Texture("ui/UI_Flat_Button02a_1.png");
        btnQuitHover   = new Texture("ui/UI_Flat_Button02a_2.png");
        btnQuitPressed = new Texture("ui/UI_Flat_Button02a_3.png");

        // Icons
        iconPlay  = new Texture("ui/UI_Flat_ButtonPlay01a.png");
        iconCross = new Texture("ui/UI_Flat_ButtonCross01a.png");

        // ── Button positions ───────────────────────────────
        // Buttons placed at bottom-center of screen
        // so they sit nicely over the path in the image
        float centerX  = SCREEN_WIDTH / 2f;
        float playBtnY = 180f;  // above bottom edge
        float quitBtnY = playBtnY - BTN_HEIGHT - 16f;

        playButtonRect = new Rectangle(
            centerX - BTN_WIDTH / 2f, playBtnY,
            BTN_WIDTH, BTN_HEIGHT
        );
        quitButtonRect = new Rectangle(
            centerX - BTN_WIDTH / 2f, quitBtnY,
            BTN_WIDTH, BTN_HEIGHT
        );

        System.out.println("=== MainMenuScreen.show() END ===");
    }

    // -------------------------------------------------------
    // Render
    // -------------------------------------------------------
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        handleInput();

        batch.begin();

        // 1️⃣ Draw background image (fills entire screen)
        batch.draw(backgroundTexture, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // 2️⃣ Draw semi-transparent dark overlay on banner area
        // (helps text readability over busy background)
        // We'll just use the banner sprite as the backing

        // 3️⃣ Draw title banner (top-center of screen)
        float bannerX = (SCREEN_WIDTH  - BANNER_WIDTH)  / 2f;
        float bannerY = SCREEN_HEIGHT - BANNER_HEIGHT - 40f; // near top
        batch.draw(bannerTexture, bannerX, bannerY, BANNER_WIDTH, BANNER_HEIGHT);

        // 4️⃣ Draw title text on banner
        glyphLayout.setText(font, "Summer Terrain");
        font.draw(batch, glyphLayout,
            (SCREEN_WIDTH  - glyphLayout.width)  / 2f,
            bannerY + BANNER_HEIGHT / 2f + glyphLayout.height / 2f);

        // 5️⃣ Draw Play button
        float iconSize = 32f;

        Texture playTex = playPressed ? btnPlayPressed
            : playHovered ? btnPlayHover
            : btnPlayNormal;
        batch.draw(playTex,
            playButtonRect.x,     playButtonRect.y,
            playButtonRect.width, playButtonRect.height);

        // Play icon
        batch.draw(iconPlay,
            playButtonRect.x + 16f,
            playButtonRect.y + (BTN_HEIGHT - iconSize) / 2f,
            iconSize, iconSize);

        // Play text
        glyphLayout.setText(font, "Play");
        font.draw(batch, glyphLayout,
            playButtonRect.x + (BTN_WIDTH  - glyphLayout.width)  / 2f + 16f,
            playButtonRect.y + (BTN_HEIGHT + glyphLayout.height) / 2f);

        // 6️⃣ Draw Quit button
        Texture quitTex = quitPressed ? btnQuitPressed
            : quitHovered ? btnQuitHover
            : btnQuitNormal;
        batch.draw(quitTex,
            quitButtonRect.x,     quitButtonRect.y,
            quitButtonRect.width, quitButtonRect.height);

        // Quit icon
        batch.draw(iconCross,
            quitButtonRect.x + 16f,
            quitButtonRect.y + (BTN_HEIGHT - iconSize) / 2f,
            iconSize, iconSize);

        // Quit text
        glyphLayout.setText(font, "Quit");
        font.draw(batch, glyphLayout,
            quitButtonRect.x + (BTN_WIDTH  - glyphLayout.width)  / 2f + 16f,
            quitButtonRect.y + (BTN_HEIGHT + glyphLayout.height) / 2f);

        batch.end();
    }

    // -------------------------------------------------------
    // Input
    // -------------------------------------------------------
    private void handleInput() {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        playHovered = playButtonRect.contains(touchPos.x, touchPos.y);
        quitHovered = quitButtonRect.contains(touchPos.x, touchPos.y);

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            playPressed = playHovered;
            quitPressed = quitHovered;
        } else {
            playPressed = false;
            quitPressed = false;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (playHovered) {
                System.out.println("▶ Play clicked!");
                game.setScreen(new GameScreen(game));
            }
            if (quitHovered) {
                System.out.println("✕ Quit clicked!");
                Gdx.app.exit();
            }
        }
    }

    // -------------------------------------------------------
    // Resize
    // -------------------------------------------------------
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth  = SCREEN_WIDTH;
        camera.viewportHeight = SCREEN_HEIGHT;
        camera.update();
    }

    // -------------------------------------------------------
    // Dispose
    // -------------------------------------------------------
    @Override
    public void dispose() {
        System.out.println("=== MainMenuScreen.dispose() ===");
        batch.dispose();
        font.dispose();
        backgroundTexture.dispose();  // 👈 dispose background
        bannerTexture.dispose();
        btnPlayNormal.dispose();
        btnPlayHover.dispose();
        btnPlayPressed.dispose();
        btnQuitNormal.dispose();
        btnQuitHover.dispose();
        btnQuitPressed.dispose();
        iconPlay.dispose();
        iconCross.dispose();
    }

    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
}
