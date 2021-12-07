package com.layhill.roadsim.gameengine.particles;

import com.layhill.roadsim.gameengine.graphics.gl.data.UniformMatrix4f;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import org.joml.Matrix4f;

public class ParticleShaderProgram extends ShaderProgram {

    private UniformMatrix4f projection = new UniformMatrix4f("uProjection");
    private UniformMatrix4f modelTransformation = new UniformMatrix4f("uTransformation");

    public ParticleShaderProgram() {
        super.addUniform(projection, modelTransformation);
    }

    public void loadCamera(Camera camera) {
        projection.load(camera.getProjectionMatrix());
    }

    public void loadModelTransformation(Matrix4f transformationMatrix) {
        modelTransformation.load(transformationMatrix);
    }
}
