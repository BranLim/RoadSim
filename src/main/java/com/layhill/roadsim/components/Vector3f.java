package com.layhill.roadsim.components;

public class Vector3f {
    private float z;
    private float y;
    private float x;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3f vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Vector3f add(Vector3f vector) {
        x = x + vector.x;
        y = y + vector.y;
        z = z + vector.z;
        return this;
    }

    public Vector3f subtract(Vector3f vector) {
        x = x - vector.x;
        y = y - vector.y;
        z = z - vector.z;
        return this;
    }

    public Vector3f scale(float scalar) {
        x = x * scalar;
        y = y * scalar;
        z = z * scalar;
        return this;
    }

    public Vector3f scaleX(float scalar) {
        x = x * scalar;
        return this;
    }
}
