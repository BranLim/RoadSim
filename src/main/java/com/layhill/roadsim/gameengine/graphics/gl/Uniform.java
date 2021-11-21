package com.layhill.roadsim.gameengine.graphics.gl;


import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public abstract class Uniform {

    private String name;
    private int location = -1;

    protected Uniform(String name) {
        this.name = name;
    }

    public void getUniformLocation(int programId) {
        location = glGetUniformLocation(programId, name);
    }

    public void dispose() {
    }

    protected int getLocation() {
        return location;
    }
}
