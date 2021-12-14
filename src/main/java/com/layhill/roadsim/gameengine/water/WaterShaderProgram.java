package com.layhill.roadsim.gameengine.water;

import com.layhill.roadsim.gameengine.graphics.gl.data.UniformInteger;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformMatrix4f;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import org.joml.Matrix4f;

public class WaterShaderProgram extends ShaderProgram {

    private UniformMatrix4f projection = new UniformMatrix4f("uProjection");
    private UniformMatrix4f view = new UniformMatrix4f("uView");
    private UniformMatrix4f transformation = new UniformMatrix4f("uTransformation");

    private UniformInteger reflectionTexture = new UniformInteger("uReflectionTexture");
    private UniformInteger refractionTexture = new UniformInteger("uRefractionTexture");

    public WaterShaderProgram() {
        super.addUniform(projection, view, transformation, reflectionTexture,refractionTexture );
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "aPos");
    }

    public void loadCamera(Camera camera) {
        projection.load(camera.getProjectionMatrix());
        view.load(camera.getViewMatrix());
    }

    public void loadModelTransformation(Matrix4f modelTransformation) {
        if (modelTransformation == null) {
            return;
        }
        transformation.load(modelTransformation);
    }

    public void loadReflectionTexture(int textureUnit){
        reflectionTexture.load(textureUnit);
    }

    public void loadRefractionTexture(int textureUnit){
        refractionTexture.load(textureUnit);
    }
}
