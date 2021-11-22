package com.layhill.roadsim.gameengine.graphics.gl.data;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL20.glUniform3f;

public class UniformVector3f extends Uniform {

    private Vector3f value;

    public UniformVector3f(String name) {
        super(name);
    }

    public void load(Vector3f value) {
        glUniform3f(super.getLocation(), value.x, value.y, value.z);
        this.value = value;
    }
}
