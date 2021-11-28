package com.layhill.roadsim.gameengine.terrain;

import com.layhill.roadsim.gameengine.graphics.Renderable;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.utils.Maths;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.UUID;

public class Terrain implements Renderable {

    public static final float SIZE = 800;
    public static final int VERTEX_COUNT_PER_SIDE = 128;

    private String id;
    private float x;
    private float z;
    private float[][] heights;
    private TexturedModel texturedModel;

    public Terrain(int gridX, int gridZ, TexturedModel texturedModel, float[][] terrainHeight) {
        id = UUID.randomUUID().toString();
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.texturedModel = texturedModel;
        this.heights = terrainHeight;
    }

    public String getId() {
        return id;
    }

    public float getHeight(float worldX, float worldZ) {
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float) heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }
        float xCoord = (terrainX % gridSquareSize);
        float zCoord = (terrainZ % gridSquareSize);

        float answer;
        if (xCoord <= (1 - zCoord)) {
            answer = Maths.barryCentric(
                    new Vector3f(0, heights[gridX][gridZ], 0),
                    new Vector3f(1, heights[gridX + 1][gridZ], 0),
                    new Vector3f(0, heights[gridX][gridZ + 1], 1),
                    new Vector2f(xCoord, zCoord));
        } else {
            answer = Maths.barryCentric(
                    new Vector3f(1, heights[gridX + 1][gridZ], 0),
                    new Vector3f(1, heights[gridX + 1][gridZ + 1], 1),
                    new Vector3f(0, heights[gridX][gridZ + 1], 1),
                    new Vector2f(xCoord, zCoord));
        }
        return answer;

    }

    public boolean isOnThisTerrain(float worldX, float worldZ) {

        if (worldX < x || worldX >= x + SIZE) {
            return false;
        }
        if (worldZ < z || worldZ >= z + SIZE) {
            return false;
        }
        return true;
    }

    @Override
    public TexturedModel getTexturedModel() {
        return texturedModel;
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f(x, 0, z);
    }

    @Override
    public float getRotateX() {
        return 0.f;
    }

    @Override
    public float getRotateY() {
        return 0.f;
    }

    @Override
    public float getRotateZ() {
        return 0.f;
    }

    @Override
    public float getScale() {
        return 1.f;
    }
}
