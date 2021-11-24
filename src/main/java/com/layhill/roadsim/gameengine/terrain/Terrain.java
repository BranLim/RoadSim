package com.layhill.roadsim.gameengine.terrain;

import com.layhill.roadsim.gameengine.graphics.Renderable;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import org.joml.Vector3f;

import java.util.UUID;

public class Terrain implements Renderable {

    public static final float SIZE = 800;
    public static final int VERTEX_COUNT_PER_SIDE = 128;

    private String id;
    private float x;
    private float z;
    private TexturedModel texturedModel;

    public Terrain(int gridX, int gridZ, TexturedModel texturedModel) {
        id = UUID.randomUUID().toString();
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.texturedModel = texturedModel;
    }

    public String getId() {
        return id;
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
