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

public class PauseScreen implements Screen {

    // -------------------------------------------------------
    // Constants
    // -------------------------------------------------------
    private static final float SCREEN_WIDTH  = 800f;
    private static final float SCREEN_HEIGHT = 600f;
    private static final float BTN_WIDTH     = 192f;
    private static final float BTN_HEIGHT    = 64f;
    private static final float PANEL_WIDTH   = 400f;
    private static final float PANEL_HEIGHT  = 400f;
    private static final float BANNER_WIDTH  = 320f;
    private static final float BANNER_HEIGHT = 64f;

    // -------------------------------------------------------
    // References
    // -------------------------------------------------------
    private final in.GameDev.SummerTerrain.SummerTerrainGame game;
    private final GameScreen gameScreen;

    // -------------------------------------------------------
    // Core
    // -------------------------------------------------------
    private SpriteBatch        batch;
    private OrthographicCamera camera;
    private BitmapFont         font;
    private GlyphLayout        glyphLayout;
    private Vector3            touchPos;

    // -------------------------------------------------------
    // Textures
    // -------------------------------------------------------
    private Texture backgroundTexture; // 👈 NEW: pause background
    private Texture panelTexture;
    private Texture bannerTexture;

    private Texture btnResumeNormal, btnResumeHover, btnResumePressed;
    private Texture btnMenuNormal,   btnMenuHover,   btnMenuPressed;
    private Texture btnQuitNormal,   btnQuitHover,   btnQuitPressed;

    private Texture iconCheck;
    private Texture iconArrow;
    private Texture iconCross;

    // -------------------------------------------------------
    // Button rectangles
    // -------------------------------------------------------
    private Rectangle resumeButtonRect;
    private Rectangle menuButtonRect;
    private Rectangle quitButtonRect;

    // -------------------------------------------------------
    // Button states
    // -------------------------------------------------------
    private boolean resumeHovered, menuHovered, quitHovered;
    private boolean resumePressed, menuPressed, quitPressed;

    // -------------------------------------------------------
    // Pending action (crash fix)
    // -------------------------------------------------------
    private enum PendingAction { NONE, RESUME, MAIN_MENU, QUIT }
    private PendingAction pendingAction = PendingAction.NONE;

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------
    public PauseScreen(
        in.GameDev.SummerTerrain.SummerTerrainGame game,
        GameScreen gameScreen
    ) {
        this.game       = game;
        this.gameScreen = gameScreen;
    }

    // -------------------------------------------------------
    // Show
    // -------------------------------------------------------
    @Override
    public void show() {
        System.out.println("=== PauseScreen.show() START ===");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch       = new SpriteBatch();
        font        = new BitmapFont();
        font.getData().setScale(2f);
        glyphLayout = new GlyphLayout();
        touchPos    = new Vector3();

        // ── Load textures ──────────────────────────────────

        // 👈 NEW: Load pause background
        backgroundTexture = new Texture("objects/PauseMenu.png");

        panelTexture  = new Texture("ui/UI_Flat_Frame02a.png");
        bannerTexture = new Texture("ui/UI_Flat_Banner02a.png");

        btnResumeNormal  = new Texture("ui/UI_Flat_Button01a_1.png");
        btnResumeHover   = new Texture("ui/UI_Flat_Button01a_2.png");
        btnResumePressed = new Texture("ui/UI_Flat_Button01a_3.png");

        btnMenuNormal  = new Texture("ui/UI_Flat_Button01a_1.png");
        btnMenuHover   = new Texture("ui/UI_Flat_Button01a_2.png");
        btnMenuPressed = new Texture("ui/UI_Flat_Button01a_3.png");

        btnQuitNormal  = new Texture("ui/UI_Flat_Button02a_1.png");
        btnQuitHover   = new Texture("ui/UI_Flat_Button02a_2.png");
        btnQuitPressed = new Texture("ui/UI_Flat_Button02a_3.png");

        iconCheck = new Texture("ui/UI_Flat_ButtonCheck01a.png");
        iconArrow = new Texture("ui/UI_Flat_ButtonArrow01a.png");
        iconCross = new Texture("ui/UI_Flat_ButtonCross01a.png");

        // ── Button positions ───────────────────────────────
        float centerX    = SCREEN_WIDTH  / 2f;
        float panelY     = (SCREEN_HEIGHT - PANEL_HEIGHT) / 2f;
        float resumeBtnY = panelY + PANEL_HEIGHT / 2f - BTN_HEIGHT / 2f;
        float menuBtnY   = resumeBtnY - BTN_HEIGHT - 16f;
        float quitBtnY   = menuBtnY   - BTN_HEIGHT - 16f;

        resumeButtonRect = new Rectangle(centerX - BTN_WIDTH / 2f, resumeBtnY, BTN_WIDTH, BTN_HEIGHT);
        menuButtonRect   = new Rectangle(centerX - BTN_WIDTH / 2f, menuBtnY,   BTN_WIDTH, BTN_HEIGHT);
        quitButtonRect   = new Rectangle(centerX - BTN_WIDTH / 2f, quitBtnY,   BTN_WIDTH, BTN_HEIGHT);

        System.out.println("=== PauseScreen.show() END ===");
    }

    // -------------------------------------------------------
    // Render
    // -------------------------------------------------------
    @Override
    public void render(float delta) {

        // ✅ Process pending action at START of frame
        if (pendingAction != PendingAction.NONE) {
            processPendingAction();
            return;
        }

        // ✅ ESC key sets pending action
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pendingAction = PendingAction.RESUME;
            return;
        }

        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        handleInput();

        batch.begin();

        // 1️⃣ Draw background (fills entire screen)
        batch.draw(backgroundTexture, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // 2️⃣ Draw panel on top of background
        float panelX = (SCREEN_WIDTH  - PANEL_WIDTH)  / 2f;
        float panelY = (SCREEN_HEIGHT - PANEL_HEIGHT) / 2f;
        batch.draw(panelTexture, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        // 3️⃣ Draw "PAUSED" banner
        float bannerX = (SCREEN_WIDTH  - BANNER_WIDTH)  / 2f;
        float bannerY = panelY + PANEL_HEIGHT - BANNER_HEIGHT - 30f;
        batch.draw(bannerTexture, bannerX, bannerY, BANNER_WIDTH, BANNER_HEIGHT);

        // 4️⃣ Draw "PAUSED" text
        glyphLayout.setText(font, "PAUSED");
        font.draw(batch, glyphLayout,
            (SCREEN_WIDTH - glyphLayout.width) / 2f,
            bannerY + BANNER_HEIGHT / 2f + glyphLayout.height / 2f);

        float iconSize = 32f;

        // 5️⃣ Resume button
        Texture resumeTex = resumePressed ? btnResumePressed
            : resumeHovered ? btnResumeHover
            : btnResumeNormal;
        batch.draw(resumeTex,
            resumeButtonRect.x, resumeButtonRect.y,
            resumeButtonRect.width, resumeButtonRect.height);
        batch.draw(iconCheck,
            resumeButtonRect.x + 16f,
            resumeButtonRect.y + (BTN_HEIGHT - iconSize) / 2f,
            iconSize, iconSize);
        glyphLayout.setText(font, "Resume");
        font.draw(batch, glyphLayout,
            resumeButtonRect.x + (BTN_WIDTH  - glyphLayout.width)  / 2f + 16f,
            resumeButtonRect.y + (BTN_HEIGHT + glyphLayout.height) / 2f);

        // 6️⃣ Main Menu button
        Texture menuTex = menuPressed ? btnMenuPressed
            : menuHovered ? btnMenuHover
            : btnMenuNormal;
        batch.draw(menuTex,
            menuButtonRect.x, menuButtonRect.y,
            menuButtonRect.width, menuButtonRect.height);
        batch.draw(iconArrow,
            menuButtonRect.x + 16f,
            menuButtonRect.y + (BTN_HEIGHT - iconSize) / 2f,
            iconSize, iconSize);
        glyphLayout.setText(font, "Main Menu");
        font.draw(batch, glyphLayout,
            menuButtonRect.x + (BTN_WIDTH  - glyphLayout.width)  / 2f + 16f,
            menuButtonRect.y + (BTN_HEIGHT + glyphLayout.height) / 2f);

        // 7️⃣ Quit button
        Texture quitTex = quitPressed ? btnQuitPressed
            : quitHovered ? btnQuitHover
            : btnQuitNormal;
        batch.draw(quitTex,
            quitButtonRect.x, quitButtonRect.y,
            quitButtonRect.width, quitButtonRect.height);
        batch.draw(iconCross,
            quitButtonRect.x + 16f,
            quitButtonRect.y + (BTN_HEIGHT - iconSize) / 2f,
            iconSize, iconSize);
        glyphLayout.setText(font, "Quit");
        font.draw(batch, glyphLayout,
            quitButtonRect.x + (BTN_WIDTH  - glyphLayout.width)  / 2f + 16f,
            quitButtonRect.y + (BTN_HEIGHT + glyphLayout.height) / 2f);

        batch.end();
    }

    // -------------------------------------------------------
    // Process pending action SAFELY
    // -------------------------------------------------------
    private void processPendingAction() {
        switch (pendingAction) {
            case RESUME:
                System.out.println("▶ Resuming game...");
                dispose();
                game.setScreen(gameScreen);
                break;
            case MAIN_MENU:
                System.out.println("🏠 Going to Main Menu...");
                gameScreen.dispose();
                dispose();
                game.setScreen(new MainMenuScreen(game));
                break;
            case QUIT:
                System.out.println("✕ Quitting game...");
                dispose();
                Gdx.app.exit();
                break;
            default:
                break;
        }
        pendingAction = PendingAction.NONE;
    }

    // -------------------------------------------------------
    // Input - ONLY sets pendingAction flag
    // -------------------------------------------------------
    private void handleInput() {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        resumeHovered = resumeButtonRect.contains(touchPos.x, touchPos.y);
        menuHovered   = menuButtonRect.contains(touchPos.x,   touchPos.y);
        quitHovered   = quitButtonRect.contains(touchPos.x,   touchPos.y);

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            resumePressed = resumeHovered;
            menuPressed   = menuHovered;
            quitPressed   = quitHovered;
        } else {
            resumePressed = false;
            menuPressed   = false;
            quitPressed   = false;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (resumeHovered) pendingAction = PendingAction.RESUME;
            if (menuHovered)   pendingAction = PendingAction.MAIN_MENU;
            if (quitHovered)   pendingAction = PendingAction.QUIT;
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth  = SCREEN_WIDTH;
        camera.viewportHeight = SCREEN_HEIGHT;
        camera.update();
    }

    @Override
    public void dispose() {
        System.out.println("=== PauseScreen.dispose() ===");
        batch.dispose();
        font.dispose();
        backgroundTexture.dispose(); // 👈 dispose background
        panelTexture.dispose();
        bannerTexture.dispose();
        btnResumeNormal.dispose();
        btnResumeHover.dispose();
        btnResumePressed.dispose();
        btnMenuNormal.dispose();
        btnMenuHover.dispose();
        btnMenuPressed.dispose();
        btnQuitNormal.dispose();
        btnQuitHover.dispose();
        btnQuitPressed.dispose();
        iconCheck.dispose();
        iconArrow.dispose();
        iconCross.dispose();
    }

    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
}
