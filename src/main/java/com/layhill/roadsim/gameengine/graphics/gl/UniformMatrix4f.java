package com.layhill.roadsim.gameengine.graphics.gl;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class UniformMatrix4f extends Uniform {

    private Matrix4f value;

    public UniformMatrix4f(String name) {
        super(name);
    }

    public void load(Matrix4f value) {
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        value.get(matBuffer);
        glUniformMatrix4fv(super.getLocation(), false, matBuffer);
        this.value = value;
    }


}
