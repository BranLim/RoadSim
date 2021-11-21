package com.layhill.roadsim.gameengine.entities;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.graphics.Texture;

public class Terrain {

    public static final float SIZE = 800;
    public static final int VERTEX_COUNT_PER_SIDE = 128;

    private float x;
    private float z;
    private Mesh mesh;
    private Texture texture;

    public Terrain(int gridX, int gridZ, Mesh mesh, Texture texture){
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.texture = texture;
    }

}
