package in.GameDev.SummerTerrain.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import in.GameDev.SummerTerrain.collision.CollisionHandler;

public class Player {
    public enum State { IDLE, WALK, ATTACK, PICK_UP }
    public enum Direction { DOWN, UP, LEFT, RIGHT }

    private State currentState = State.IDLE;
    private Direction currentDirection = Direction.DOWN;
    private float stateTimer = 0f;

    private Animation<TextureRegion> downIdle, upIdle, rightIdle, leftIdle;
    private Animation<TextureRegion> downWalk, upWalk, rightWalk, leftWalk;
    private Animation<TextureRegion> downAttack, upAttack, rightAttack, leftAttack;
    private Animation<TextureRegion> pickUp;

    private Array<Texture> textureList = new Array<>();

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
            downIdle = loadAnimation("characters/player/_down idle.png", 0.15f, true);
            upIdle = loadAnimation("characters/player/_up idle.png", 0.15f, true);
            leftIdle = loadAnimation("characters/player/_side idle.png", 0.15f, true);
            rightIdle = createFlippedAnimation(leftIdle);
            
            downWalk = loadAnimation("characters/player/_down walk.png", 0.1f, true);
            upWalk = loadAnimation("characters/player/_up walk.png", 0.1f, true);
            leftWalk = loadAnimation("characters/player/_side walk.png", 0.1f, true);
            rightWalk = createFlippedAnimation(leftWalk);
            
            downAttack = loadAnimation("characters/player/_down attack.png", 0.1f, false);
            upAttack = loadAnimation("characters/player/_up attack.png", 0.1f, false);
            leftAttack = loadAnimation("characters/player/_side attack.png", 0.1f, false);
            rightAttack = createFlippedAnimation(leftAttack);

            pickUp = loadAnimation("characters/player/_pick up.png", 0.15f, false);
            
            System.out.println("✓ All animations loaded successfully!");
        } catch (Exception e) {
            System.err.println("✗ FAILED to load animations!");
            e.printStackTrace();
            return;
        }

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
     * Update logic - handle movement and animation state
     */
    public void update(float delta) {
        handleInput(delta);
        updateCollisionBox();
        stateTimer += delta;
    }

    private boolean isAnimationFinished() {
        Animation<TextureRegion> currentAnim = getAnimation();
        if (currentAnim == null) return true;
        return currentAnim.isAnimationFinished(stateTimer);
    }

    /**
     * Handle keyboard input for movement with collision
     */
    private void handleInput(float delta) {
        float moveX = 0;
        float moveY = 0;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            currentState = State.ATTACK;
            stateTimer = 0f;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            currentState = State.PICK_UP;
            stateTimer = 0f;
        }

        boolean canMove = (currentState != State.ATTACK && currentState != State.PICK_UP) 
                          || isAnimationFinished();

        if (canMove) {
            if (currentState == State.ATTACK || currentState == State.PICK_UP) {
                currentState = State.IDLE;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                moveY += 1;
                currentDirection = Direction.UP;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                moveY -= 1;
                currentDirection = Direction.DOWN;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                moveX -= 1;
                currentDirection = Direction.LEFT;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                moveX += 1;
                currentDirection = Direction.RIGHT;
            }

            if (moveX != 0 || moveY != 0) {
                if (currentState != State.WALK) stateTimer = 0f;
                currentState = State.WALK;
            } else {
                if (currentState != State.IDLE) stateTimer = 0f;
                currentState = State.IDLE;
            }
        }

        // If no movement, return early (state might still have changed)
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

    private Animation<TextureRegion> getAnimation() {
        switch (currentState) {
            case WALK:
                if (currentDirection == Direction.UP) return upWalk;
                if (currentDirection == Direction.DOWN) return downWalk;
                if (currentDirection == Direction.LEFT) return leftWalk;
                return rightWalk;
            case ATTACK:
                if (currentDirection == Direction.UP) return upAttack;
                if (currentDirection == Direction.DOWN) return downAttack;
                if (currentDirection == Direction.LEFT) return leftAttack;
                return rightAttack;
            case PICK_UP:
                return pickUp;
            case IDLE:
            default:
                if (currentDirection == Direction.UP) return upIdle;
                if (currentDirection == Direction.DOWN) return downIdle;
                if (currentDirection == Direction.LEFT) return leftIdle;
                return rightIdle;
        }
    }

    /**
     * Render the player
     */
    public void render(SpriteBatch batch) {
        Animation<TextureRegion> anim = getAnimation();
        if (anim != null) {
            TextureRegion frame = anim.getKeyFrame(stateTimer);
            batch.draw(frame, position.x, position.y, width, height);
        }
    }

    private Animation<TextureRegion> loadAnimation(String path, float frameDuration, boolean loop) {
        Texture sheet = new Texture(path);
        textureList.add(sheet);

        int cols = sheet.getWidth() / FRAME_WIDTH;
        int rows = sheet.getHeight() / FRAME_HEIGHT;

        TextureRegion[][] tmp = TextureRegion.split(sheet, FRAME_WIDTH, FRAME_HEIGHT);
        Array<TextureRegion> frames = new Array<>(cols * rows);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                frames.add(tmp[i][j]);
            }
        }

        Animation<TextureRegion> anim = new Animation<>(frameDuration, frames, 
            loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
        return anim;
    }

    private Animation<TextureRegion> createFlippedAnimation(Animation<TextureRegion> original) {
        Object[] originalFrames = original.getKeyFrames();
        Array<TextureRegion> flippedFrames = new Array<>(originalFrames.length);
        for (int i = 0; i < originalFrames.length; i++) {
            TextureRegion frame = new TextureRegion((TextureRegion) originalFrames[i]);
            frame.flip(true, false);
            flippedFrames.add(frame);
        }
        return new Animation<>(original.getFrameDuration(), flippedFrames, original.getPlayMode());
    }

    /**
     * Dispose resources
     */
    public void dispose() {
        for (Texture texture : textureList) {
            texture.dispose();
        }
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
