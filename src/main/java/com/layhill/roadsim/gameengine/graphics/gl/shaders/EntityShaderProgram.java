package com.layhill.roadsim.gameengine.graphics.gl.shaders;

import com.layhill.roadsim.gameengine.graphics.gl.data.UniformInteger;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformMatrix4f;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformVector3f;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class EntityShaderProgram extends ShaderProgram {

    private static final int MAX_LIGHTS = 5;

    private UniformMatrix4f projection = new UniformMatrix4f("uProjection");
    private UniformMatrix4f view = new UniformMatrix4f("uView");
    private UniformMatrix4f modelTransformation = new UniformMatrix4f("uTransformation");

    private UniformVector3f globalLightDirection = new UniformVector3f("uGlobalLightDirection");
    private UniformVector3f globalLightColour = new UniformVector3f("uGlobalLightColour");

    private UniformVector3f[] lightPositions = new UniformVector3f[MAX_LIGHTS];
    private UniformVector3f[] lightsColour = new UniformVector3f[MAX_LIGHTS];

    private UniformInteger texture = new UniformInteger("uTexture");

    public EntityShaderProgram() {
        super.addUniform(projection, view, modelTransformation, globalLightDirection, globalLightColour);
    }

    public void loadCamera(Camera camera) {
        if (camera == null) {
            return;
        }
        projection.load(camera.getProjectionMatrix());
        view.load(camera.getViewMatrix());
    }

    public void loadModelTransformation(Matrix4f transformationMatrix) {
        modelTransformation.load(transformationMatrix);
    }

    public void loadSun(Vector3f direction, Vector3f lightColour) {
        globalLightDirection.load(direction);
        globalLightColour.load(lightColour);
    }

    public void loadLights(Light[] lights) {

        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (lightPositions[i] == null) {
                lightPositions[i] = new UniformVector3f("uLightPosition[" + i + "]");
                super.addUniform(lightPositions[i]);
            }
            if (lightsColour[i] == null) {
                lightsColour[i] = new UniformVector3f("uLightColour[" + i + "]");
                super.addUniform(lightsColour[i]);
            }
            super.setupUniformData();
            if (i < lights.length) {
                lightPositions[i].load(lights[i].getPosition());
                lightsColour[i].load(lights[i].getColour());
            }else{
                lightPositions[i].load(new Vector3f(0,0,0));
                lightsColour[i].load(new Vector3f(0,0,0));
            }
        }
    }

    public void loadTexture(int textureUnit) {
        texture.load(textureUnit);
    }
}
