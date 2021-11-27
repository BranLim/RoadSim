package com.layhill.roadsim.gameengine.graphics.gl.shaders;

import com.layhill.roadsim.gameengine.graphics.gl.data.UniformInteger;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformMatrix4f;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformVector3f;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class EntityShaderProgram extends ShaderProgram {

    private UniformMatrix4f projection = new UniformMatrix4f("uProjection");
    private UniformMatrix4f view = new UniformMatrix4f("uView");
    private UniformMatrix4f modelTransformation = new UniformMatrix4f("uTransformation");

    private UniformVector3f globalLightDirection = new UniformVector3f("uGlobalLightDirection");
    private UniformVector3f globalLightColour = new UniformVector3f("uGlobalLightColour");

    private UniformVector3f lightPosition = new UniformVector3f("uLightPosition");
    private UniformVector3f lightColour = new UniformVector3f("uLightColour");

    private UniformInteger texture = new UniformInteger("uTexture");

    public EntityShaderProgram() {
        super.addUniform(projection, view, modelTransformation, globalLightDirection, globalLightColour, lightPosition, lightColour);
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
        if (lights.length > 0) {
            lightPosition.load(lights[0].getPosition());
            lightColour.load(lights[0].getColour());
        }
    }

    public void loadTexture(int textureUnit) {
        texture.load(textureUnit);
    }
}
