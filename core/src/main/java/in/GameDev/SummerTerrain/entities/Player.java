package in.GameDev.SummerTerrain.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import in.GameDev.SummerTerrain.collision.CollisionHandler;

public class Player {
    private Texture spriteSheet;
    private TextureRegion currentFrame;
    private Vector2 position;

    // Size of the player (in world units / pixels)
    private float width;
    private float height;

    // Frame dimensions - 6 columns x 2 rows, each frame is 64x64
    private static final int FRAME_WIDTH = 64;
    private static final int FRAME_HEIGHT = 64;

    // Movement speed (pixels per second)
    private static final float MOVE_SPEED = 200f;

    // Collision handler (passed from GameScreen)
    private CollisionHandler collisionHandler;

    // Collision box (smaller than sprite for better feel)
    private Rectangle collisionBox;
    private static final float COLLISION_WIDTH = 40;  // Adjust as needed
    private static final float COLLISION_HEIGHT = 40; // Adjust as needed

    public Player(float startX, float startY) {
        System.out.println("=== PLAYER CONSTRUCTOR START ===");

        try {
            // Load player sprite sheet
            spriteSheet = new Texture("characters/player/idle.png");
            System.out.println("✓ Texture loaded successfully!");
            System.out.println("  Sprite sheet size: " + spriteSheet.getWidth() + "x" + spriteSheet.getHeight());
        } catch (Exception e) {
            System.err.println("✗ FAILED to load texture!");
            e.printStackTrace();
            return;
        }

        // Extract the first frame (top-left corner at position 0,0)
        currentFrame = new TextureRegion(spriteSheet, 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        System.out.println("✓ TextureRegion created: " + FRAME_WIDTH + "x" + FRAME_HEIGHT);

        // World position
        position = new Vector2(startX, startY);

        // Set size (scale for visibility)
        width = FRAME_WIDTH * 2;   // 128 pixels (2x scale)
        height = FRAME_HEIGHT * 2;  // 128 pixels (2x scale)

        // Create collision box (centered on player, smaller than sprite)
        collisionBox = new Rectangle(
            position.x + (width - COLLISION_WIDTH) / 2,
            position.y + (height - COLLISION_HEIGHT) / 2,
            COLLISION_WIDTH,
            COLLISION_HEIGHT
        );

        System.out.println("✓ Player position: " + startX + ", " + startY);
        System.out.println("✓ Display size: " + width + "x" + height);
        System.out.println("✓ Collision box: " + COLLISION_WIDTH + "x" + COLLISION_HEIGHT);
        System.out.println("=== PLAYER CONSTRUCTOR END ===");
    }

    /**
     * Set the collision handler (called by GameScreen)
     */
    public void setCollisionHandler(CollisionHandler handler) {
        this.collisionHandler = handler;
        System.out.println("✓ Player collision handler set");
    }

    /**
     * Update logic - handle movement
     */
    public void update(float delta) {
        handleInput(delta);
        updateCollisionBox();
    }

    /**
     * Handle keyboard input for movement with collision
     */
    private void handleInput(float delta) {
        float moveX = 0;
        float moveY = 0;

        // Check WASD keys
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY += 1; // Move up
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY -= 1; // Move down
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX -= 1; // Move left
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX += 1; // Move right
        }

        // If no movement, return early
        if (moveX == 0 && moveY == 0) {
            return;
        }

        // Normalize diagonal movement
        if (moveX != 0 && moveY != 0) {
            float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            moveX /= length;
            moveY /= length;
        }

        // Calculate desired new position
        float desiredX = position.x + moveX * MOVE_SPEED * delta;
        float desiredY = position.y + moveY * MOVE_SPEED * delta;

        // If collision handler exists, check collision
        if (collisionHandler != null) {
            // Calculate collision box position for desired movement
            float desiredCollisionX = desiredX + (width - COLLISION_WIDTH) / 2;
            float desiredCollisionY = desiredY + (height - COLLISION_HEIGHT) / 2;

            // Get allowed movement from collision handler
            float[] allowed = collisionHandler.getAllowedMovement(
                collisionBox.x, collisionBox.y,
                desiredCollisionX, desiredCollisionY,
                COLLISION_WIDTH, COLLISION_HEIGHT
            );

            // Convert collision box position back to sprite position
            position.x = allowed[0] - (width - COLLISION_WIDTH) / 2;
            position.y = allowed[1] - (height - COLLISION_HEIGHT) / 2;
        } else {
            // No collision checking - move freely
            position.x = desiredX;
            position.y = desiredY;
        }
    }

    /**
     * Update collision box to match player position
     */
    private void updateCollisionBox() {
        collisionBox.x = position.x + (width - COLLISION_WIDTH) / 2;
        collisionBox.y = position.y + (height - COLLISION_HEIGHT) / 2;
    }

    /**
     * Render the player
     */
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, position.x, position.y, width, height);
    }

    /**
     * Dispose resources
     */
    public void dispose() {
        spriteSheet.dispose();
        System.out.println("Player disposed");
    }

    // --------------------
    // Getters
    // --------------------
    public Vector2 getPosition() {
        return position;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    // Getter for center position (useful for camera following)
    public float getCenterX() {
        return position.x + width / 2;
    }

    public float getCenterY() {
        return position.y + height / 2;
    }
}
