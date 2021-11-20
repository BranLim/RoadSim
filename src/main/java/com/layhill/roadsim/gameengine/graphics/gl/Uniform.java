package com.layhill.roadsim.gameengine.graphics.gl;


import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public abstract class Uniform<T> {

    private String name;
    private int location = -1;

    protected Uniform(String name) {
        this.name = name;
    }

    public abstract void load(T value);

    public void determineAndSetUniformLocation(int programId) {
        location = glGetUniformLocation(programId, name);
    }

    public void dispose() {
    }

    protected int getLocation() {
        return location;
    }
}
