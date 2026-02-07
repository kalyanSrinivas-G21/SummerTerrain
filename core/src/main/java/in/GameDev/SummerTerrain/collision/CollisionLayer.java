package in.GameDev.SummerTerrain.collision;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Reads collision data from tile collision shapes in Tiled.
 * Supports reading from multiple layers.
 */
public class CollisionLayer {

    private Array<TiledMapTileLayer> layers;
    private int tileWidth;
    private int tileHeight;
    private Array<Rectangle> collisionRects;

    /**
     * Initialize with multiple layer names
     */
    public CollisionLayer(TiledMap map, String... layerNames) {
        this.layers = new Array<>();
        this.collisionRects = new Array<>();

        System.out.println("=== CollisionLayer Initialization ===");
        System.out.println("Looking for layers: " + String.join(", ", layerNames));

        // Load all specified layers
        for (String layerName : layerNames) {
            MapLayer mapLayer = map.getLayers().get(layerName);

            if (mapLayer == null) {
                System.err.println("⚠️ WARNING: Layer '" + layerName + "' not found!");
                continue;
            }

            if (!(mapLayer instanceof TiledMapTileLayer)) {
                System.err.println("⚠️ WARNING: Layer '" + layerName + "' is not a tile layer!");
                continue;
            }

            TiledMapTileLayer tileLayer = (TiledMapTileLayer) mapLayer;
            layers.add(tileLayer);
            System.out.println("✓ Added layer: " + layerName);

            // Set tile dimensions from first valid layer
            if (tileWidth == 0) {
                this.tileWidth = (int) tileLayer.getTileWidth();
                this.tileHeight = (int) tileLayer.getTileHeight();
                System.out.println("  Tile size: " + tileWidth + "x" + tileHeight);
            }
        }

        if (layers.size == 0) {
            System.err.println("✗ ERROR: No valid layers found!");
            System.out.println("Available layers in map:");
            for (MapLayer l : map.getLayers()) {
                System.out.println("  - " + l.getName());
            }
            throw new IllegalArgumentException("No valid collision layers found");
        }

        // Build collision rectangles from all layers
        buildCollisionRectangles();

        System.out.println("✓ CollisionLayer initialized (using tile collision shapes)");
        System.out.println("  Layers loaded: " + layers.size);
        System.out.println("  Collision rectangles found: " + collisionRects.size);
        System.out.println("=== Initialization Complete ===");
    }

    /**
     * Extract collision rectangles from all tiles in all layers
     */
    private void buildCollisionRectangles() {
        int rectCount = 0;

        for (TiledMapTileLayer layer : layers) {
            System.out.println("========================================");
            System.out.println("Scanning layer: " + layer.getName());
            System.out.println("Layer dimensions: " + layer.getWidth() + "x" + layer.getHeight() + " tiles");
            int layerRects = 0;
            int tilesWithCollision = 0;
            int totalTiles = 0;
            int nullCells = 0;
            int nullTiles = 0;

            for (int x = 0; x < layer.getWidth(); x++) {
                for (int y = 0; y < layer.getHeight(); y++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);

                    if (cell == null) {
                        nullCells++;
                        continue;
                    }

                    totalTiles++;

                    if (cell.getTile() == null) {
                        nullTiles++;
                        continue;
                    }

                    TiledMapTile tile = cell.getTile();

                    // Check if this tile has collision objects
                    if (tile.getObjects() != null && tile.getObjects().getCount() > 0) {
                        tilesWithCollision++;

                        // Log what type of objects we found (first 3 only)
                        if (tilesWithCollision <= 3) {
                            System.out.println("  Tile at (" + x + "," + y + ") has " +
                                tile.getObjects().getCount() + " collision object(s)");

                            for (MapObject obj : tile.getObjects()) {
                                System.out.println("    Object type: " + obj.getClass().getSimpleName());
                            }
                        }

                        // Convert tile-local collision shapes to world coordinates
                        for (MapObject object : tile.getObjects()) {
                            if (object instanceof RectangleMapObject) {
                                Rectangle tileRect = ((RectangleMapObject) object).getRectangle();

                                // Convert to world coordinates
                                Rectangle worldRect = new Rectangle(
                                    x * tileWidth + tileRect.x,
                                    y * tileHeight + tileRect.y,
                                    tileRect.width,
                                    tileRect.height
                                );

                                collisionRects.add(worldRect);
                                rectCount++;
                                layerRects++;

                                // Debug: print first 5 collision rects per layer
                                if (layerRects <= 5) {
                                    System.out.println("  ✓ Rect #" + layerRects +
                                        " at tile(" + x + "," + y + "): " +
                                        "world_x=" + (int)worldRect.x + ", world_y=" + (int)worldRect.y +
                                        ", w=" + (int)worldRect.width + ", h=" + (int)worldRect.height);
                                }
                            } else {
                                // Non-rectangle collision object found
                                if (layerRects <= 5) {
                                    System.out.println("  ⚠ Non-rectangle collision object at (" + x + "," + y +
                                        ") - type: " + object.getClass().getSimpleName());
                                }
                            }
                        }
                    }
                }
            }

            System.out.println("Layer '" + layer.getName() + "' summary:");
            System.out.println("  Null cells (empty): " + nullCells);
            System.out.println("  Cells with tiles: " + totalTiles);
            System.out.println("  Null tiles in cells: " + nullTiles);
            System.out.println("  Tiles with collision objects: " + tilesWithCollision);
            System.out.println("  Collision rectangles found: " + layerRects);
            System.out.println("========================================");
        }
    }

    /**
     * Check if a point collides with any collision rectangle
     */
    public boolean isBlocked(float worldX, float worldY) {
        for (Rectangle rect : collisionRects) {
            if (rect.contains(worldX, worldY)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a rectangle (entity bounds) collides with any collision rectangle
     */
    public boolean isRectangleBlocked(float x, float y, float width, float height) {
        Rectangle entityRect = new Rectangle(x, y, width, height);

        for (Rectangle rect : collisionRects) {
            if (entityRect.overlaps(rect)) {
                return true;
            }
        }
        return false;
    }

    // Getters
    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getMapWidthInTiles() {
        return layers.first().getWidth();
    }

    public int getMapHeightInTiles() {
        return layers.first().getHeight();
    }

    public Array<Rectangle> getCollisionRects() {
        return collisionRects;
    }
}
