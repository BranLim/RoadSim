package com.layhill.roadsim.gameengine.graphics.gl.data;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class UniformInteger extends Uniform{

    private int value;

    public UniformInteger(String varName) {
        super(varName);
    }

    public void load(int value){
        glUniform1i(super.getLocation(), value);
        this.value = value;
    }
}
