package com.layhill.roadsim.gameengine.graphics.gl.shaders;

import com.layhill.roadsim.gameengine.graphics.gl.data.UniformBoolean;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformInteger;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformMatrix4f;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformVector3f;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TerrainShaderProgram extends ShaderProgram {

    private UniformMatrix4f projection = new UniformMatrix4f("uProjection");
    private UniformMatrix4f view = new UniformMatrix4f("uView");
    private UniformMatrix4f transformation = new UniformMatrix4f("uTransformation");

    private UniformVector3f globalLightDirection = new UniformVector3f("uSunDirection");
    private UniformVector3f globalLightColour = new UniformVector3f("uSunColour");
    private UniformVector3f fogColour = new UniformVector3f("uFogColour");

    private UniformInteger texture = new UniformInteger("uTexture");
    private UniformBoolean enableFog = new UniformBoolean("uEnableFog");


    public TerrainShaderProgram() {
        super.addUniform(projection, view, transformation, globalLightDirection, globalLightColour,
                fogColour, texture, enableFog);
    }

    public void loadCamera(Camera camera) {
        if (camera == null) {
            return;
        }
        projection.load(camera.getProjectionMatrix());
        view.load(camera.getViewMatrix());
    }

    public void loadSun(Vector3f direction, Vector3f lightColour) {
        globalLightDirection.load(direction);
        globalLightColour.load(lightColour);
    }

    public void loadModelTransformation(Matrix4f transformationMatrix) {
        transformation.load(transformationMatrix);
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
