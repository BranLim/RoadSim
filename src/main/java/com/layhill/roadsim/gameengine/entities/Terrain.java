package com.layhill.roadsim.gameengine.entities;

import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;

import java.util.UUID;

public class Terrain {

    public static final float SIZE = 800;
    public static final int VERTEX_COUNT_PER_SIDE = 128;

    private String id;
    private float x;
    private float z;
    private TexturedModel texturedModel;

    public Terrain(int gridX, int gridZ, TexturedModel texturedModel){
        id = UUID.randomUUID().toString();
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.texturedModel = texturedModel;
    }

    public String getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public TexturedModel getTexturedModel() {
        return texturedModel;
    }
}
