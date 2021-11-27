package com.layhill.roadsim.gameengine.graphics.gl.data;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class UniformFloat extends Uniform{

    private float value;

    public UniformFloat(String name) {
        super(name);
    }

    public void load(float value){
        glUniform1f(super.getLocation(), value);
        this.value = value;
    }
}
