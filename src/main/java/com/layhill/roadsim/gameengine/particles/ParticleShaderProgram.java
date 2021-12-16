package com.layhill.roadsim.gameengine.particles;

import com.layhill.roadsim.gameengine.graphics.ViewSpecification;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformInteger;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformMatrix4f;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import org.joml.Matrix4f;

public class ParticleShaderProgram extends ShaderProgram {

    private UniformMatrix4f projection = new UniformMatrix4f("uProjection");
    private UniformMatrix4f modelTransformation = new UniformMatrix4f("uTransformation");
    private UniformInteger texture = new UniformInteger("uTexture");

    public ParticleShaderProgram() {
        super.addUniform(projection, modelTransformation, texture);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "aPos");
        super.bindAttribute(1, "modelView");
    }

    public void loadCamera(ViewSpecification viewSpecification) {
        projection.load(viewSpecification.getProjectionMatrix());
    }

    public void loadModelTransformation(Matrix4f transformationMatrix) {
        modelTransformation.load(transformationMatrix);
    }

    public void loadTexture(int textureUnit) {
        texture.load(textureUnit);
    }

}
