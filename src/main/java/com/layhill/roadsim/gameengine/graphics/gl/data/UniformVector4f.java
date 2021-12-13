package com.layhill.roadsim.gameengine.graphics.gl.data;

import org.joml.Vector4f;

import static org.lwjgl.opengl.GL20.glUniform4f;

public class UniformVector4f extends Uniform {
    private Vector4f value;

    public UniformVector4f(String varName) {
        super(varName);
    }

    public void load(Vector4f value) {
        glUniform4f(super.getLocation(), value.x, value.y, value.z, value.w);
        this.value = value;
    }
}
