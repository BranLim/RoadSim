package com.layhill.roadsim.gameengine.graphics.gl;

public abstract class Uniform {

    private String name;

    protected Uniform(String name) {
        this.name = name;
    }

    //public void


    public void uploadToUniformLocation(int programId) {

    }

    public void dispose() {
    }
}
