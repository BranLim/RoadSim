package com.layhill.roadsim.gameengine.skybox;

import com.layhill.roadsim.gameengine.graphics.gl.data.UniformBoolean;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformInteger;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformMatrix4f;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformVector3f;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SkyShaderProgram extends ShaderProgram {

    private UniformMatrix4f projection = new UniformMatrix4f("uProjection");
    private UniformMatrix4f view = new UniformMatrix4f("uView");

    private UniformInteger texture = new UniformInteger("uSkybox");
    private UniformBoolean enableFog = new UniformBoolean("uEnableFog");
    private UniformVector3f fogColour = new UniformVector3f("uFogColour");

    public SkyShaderProgram() {
        super.addUniform(projection, view, texture, enableFog, fogColour);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "aPos");
    }

    public void loadCamera(Camera camera) {
        projection.load(camera.getProjectionMatrix());
        view.load(new Matrix4f(new Matrix3f(camera.getViewMatrix())));
    }

    public void loadTexture(int textureUnit) {
        texture.load(textureUnit);
    }

    public void loadFogColour(Vector3f fogColour) {
        this.fogColour.load(fogColour);
    }

    public void enableFog() {
        enableFog.load(true);
    }

    public void disableFog() {
        enableFog.load(false);
    }
}
