package in.GameDev.SummerTerrain.collision;

import com.badlogic.gdx.math.Rectangle;

/**
 * Handles collision checking for entities.
 * Separates X and Y movement for smooth sliding along walls.
 */
public class CollisionHandler {

    private CollisionLayer collisionLayer;

    public CollisionHandler(CollisionLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
        System.out.println("✓ CollisionHandler initialized");
    }

    /**
     * Check if movement is valid and return the allowed position.
     * Uses X/Y separation for smooth wall sliding.
     *
     * @param currentX Current X position
     * @param currentY Current Y position
     * @param newX Desired new X position
     * @param newY Desired new Y position
     * @param width Entity width
     * @param height Entity height
     * @return Array [allowedX, allowedY]
     */
    public float[] getAllowedMovement(float currentX, float currentY,
                                      float newX, float newY,
                                      float width, float height) {

        float allowedX = currentX;
        float allowedY = currentY;

        // Try X movement first
        if (!collisionLayer.isRectangleBlocked(newX, currentY, width, height)) {
            allowedX = newX;
        }

        // Try Y movement
        if (!collisionLayer.isRectangleBlocked(allowedX, newY, width, height)) {
            allowedY = newY;
        }

        return new float[]{allowedX, allowedY};
    }

    /**
     * Simple collision check without movement calculation
     */
    public boolean isBlocked(float x, float y, float width, float height) {
        return collisionLayer.isRectangleBlocked(x, y, width, height);
    }
}
