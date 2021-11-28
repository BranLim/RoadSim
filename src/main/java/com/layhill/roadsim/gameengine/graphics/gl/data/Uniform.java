package com.layhill.roadsim.gameengine.graphics.gl.data;


import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public abstract class Uniform {

    private String name;
    private int location = -1;

    protected Uniform(String name) {
        this.name = name;
    }

    public void getUniformLocation(int programId) {
        if (location == -1) {
            location = glGetUniformLocation(programId, name);
        }
    }

    protected int getLocation() {
        return location;
    }
}
