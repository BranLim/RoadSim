package com.layhill.roadsim.gameengine.graphics.gl.shaders;

import com.layhill.roadsim.gameengine.graphics.gl.data.UniformInteger;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformMatrix4f;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SkyShaderProgram extends ShaderProgram{

    private UniformMatrix4f projection = new UniformMatrix4f("uProjection");
    private UniformMatrix4f view = new UniformMatrix4f("uView");
    private UniformInteger texture = new UniformInteger("skybox");


    public void loadCamera(Camera camera) {
        projection.load(camera.getProjectionMatrix());
        view.load(new Matrix4f(new Matrix3f(camera.getViewMatrix())));
    }

    public void loadTexture(int textureUnit) {
        texture.load(textureUnit);
    }
}
