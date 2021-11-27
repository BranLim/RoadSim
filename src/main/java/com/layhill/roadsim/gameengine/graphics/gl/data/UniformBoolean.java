package com.layhill.roadsim.gameengine.graphics.gl.data;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class UniformBoolean extends Uniform {
    private boolean value;

    public UniformBoolean(String varName) {
        super(varName);
    }

    public void load(boolean value) {
        glUniform1i(super.getLocation(), value ? 1 : 0);
        this.value = value;
    }
}
