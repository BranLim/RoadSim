package com.layhill.roadsim.gameengine.water;

public class WaterTile {

    private float x;
    private float z;
    private float height;
    private float rotateX;
    private float rotateY;
    private float rotateZ;
    private float scale;

    public WaterTile(float x, float z, float height, float rotateX, float rotateY, float rotateZ, float scale) {
        this.x = x;
        this.z = z;
        this.height = height;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
        this.scale = scale;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public float getHeight() {
        return height;
    }

    public float getRotateX() {
        return rotateX;
    }

    public float getRotateY() {
        return rotateY;
    }

    public float getRotateZ() {
        return rotateZ;
    }

    public float getScale() {
        return scale;
    }
}
