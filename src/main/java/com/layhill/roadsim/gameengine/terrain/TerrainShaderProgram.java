package com.layhill.roadsim.gameengine.terrain;

import com.layhill.roadsim.gameengine.graphics.ViewSpecification;
import com.layhill.roadsim.gameengine.graphics.gl.data.*;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.graphics.models.Spotlight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class TerrainShaderProgram extends ShaderProgram {

    private static final int MAX_LIGHTS = 5;

    private UniformMatrix4f projection = new UniformMatrix4f("uProjection");
    private UniformMatrix4f view = new UniformMatrix4f("uView");
    private UniformMatrix4f transformation = new UniformMatrix4f("uTransformation");

    private UniformVector3f sunDirection = new UniformVector3f("uSunDirection");
    private UniformVector3f sunColour = new UniformVector3f("uSunColour");
    private UniformVector3f fogColour = new UniformVector3f("uFogColour");

    private UniformVector3f[] lightPositions = new UniformVector3f[MAX_LIGHTS];
    private UniformVector3f[] lightsColour = new UniformVector3f[MAX_LIGHTS];

    private UniformInteger texture = new UniformInteger("uTexture");
    private UniformBoolean enableFog = new UniformBoolean("uEnableFog");

    private UniformBoolean enableSpotlight = new UniformBoolean("enableSpotlight");
    private UniformSpotlight spotlight = new UniformSpotlight("spotlight");

    private UniformVector4f clipPlane = new UniformVector4f("uClipPlane");

    private UniformMatrix4f toShadowMapSpace = new UniformMatrix4f("uToShadowMapSpace");
    private UniformInteger shadowMap = new UniformInteger("uShadowMap");
    private UniformFloat shadowDistance = new UniformFloat("uShadowDistance");

    public TerrainShaderProgram() {
        super.addUniform(projection, view, transformation, sunDirection, sunColour,
                fogColour, texture, enableFog, enableSpotlight, spotlight, spotlight.getPosition(),
                spotlight.getDirection(), spotlight.getColour(), spotlight.getCutOff(),
                spotlight.getOuterCutOff(), clipPlane, toShadowMapSpace, shadowMap, shadowDistance);

    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "aPos");
        super.bindAttribute(1, "aTexCoord");
        super.bindAttribute(2, "aSurfaceNormal");
    }

    public void loadCamera(ViewSpecification camera) {
        if (camera == null) {
            return;
        }
        projection.load(camera.getProjectionMatrix());
        view.load(camera.getViewMatrix());
    }

    public void loadSun(Vector3f direction, Vector3f lightColour) {
        sunDirection.load(direction);
        sunColour.load(lightColour);
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
            } else {
                lightPositions[i].load(new Vector3f(0, 0, 0));
                lightsColour[i].load(new Vector3f(0, 0, 0));
            }

        }
    }

    public void enableSpotlight() {
        enableSpotlight.load(true);
    }

    public void disableSpotlight() {
        enableSpotlight.load(false);
    }

    public void loadSpotlight(Spotlight spotlight) {
        this.spotlight.loadSpotlight(spotlight);
    }

    public void loadClipPlane(Vector4f plane) {
        clipPlane.load(plane);
    }

    public void loadShadowDistance(float shadowDistance){
        this.shadowDistance.load(shadowDistance);
    }

    public void loadShadowMapSpace(Matrix4f shadowMapSpace) {
        this.toShadowMapSpace.load(shadowMapSpace);
    }

    public void loadShadowMap(int textureUnit) {
        shadowMap.load(textureUnit);
    }
}
