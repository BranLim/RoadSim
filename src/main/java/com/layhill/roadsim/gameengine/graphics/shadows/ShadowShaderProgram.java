package com.layhill.roadsim.gameengine.graphics.shadows;

import com.layhill.roadsim.gameengine.graphics.gl.data.UniformMatrix4f;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import org.joml.Matrix4f;

public class ShadowShaderProgram extends ShaderProgram {

    private UniformMatrix4f projectionViewMatrix = new UniformMatrix4f("uMvp");

    public ShadowShaderProgram() {
        super.addUniform(projectionViewMatrix);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "aPos");
    }

    public void loadProjectionViewMatrix(Matrix4f projectionViewMatrix) {
        this.projectionViewMatrix.load(projectionViewMatrix);
    }
}
