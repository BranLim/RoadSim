package com.layhill.roadsim.gameengine.water;

import com.layhill.roadsim.gameengine.graphics.ViewSpecification;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformFloat;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformInteger;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformMatrix4f;
import com.layhill.roadsim.gameengine.graphics.gl.data.UniformVector3f;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.models.Sun;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class WaterShaderProgram extends ShaderProgram {

    private UniformMatrix4f projection = new UniformMatrix4f("uProjection");
    private UniformMatrix4f view = new UniformMatrix4f("uView");
    private UniformMatrix4f transformation = new UniformMatrix4f("uTransformation");
    private UniformVector3f cameraPosition = new UniformVector3f("uCameraPosition");

    private UniformInteger reflectionTexture = new UniformInteger("uReflectionTexture");
    private UniformInteger refractionTexture = new UniformInteger("uRefractionTexture");
    private UniformInteger dudvTexture = new UniformInteger("uDuDvTexture");
    private UniformInteger normalMap = new UniformInteger("uNormalMap");
    private UniformFloat waveOffset = new UniformFloat("uWaveOffset");

    private UniformVector3f sunColour = new UniformVector3f("uSunColour");
    private UniformVector3f sunDirection = new UniformVector3f("uSunDirection");


    public WaterShaderProgram() {
        super.addUniform(projection, view, transformation,cameraPosition ,reflectionTexture, refractionTexture, dudvTexture, waveOffset,normalMap,sunColour, sunDirection);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "aPos");
    }

    public void loadCamera(ViewSpecification viewSpecification) {
        projection.load(viewSpecification.getProjectionMatrix());
        view.load(viewSpecification.getViewMatrix());
    }

    public void loadModelTransformation(Matrix4f modelTransformation) {
        if (modelTransformation == null) {
            return;
        }
        transformation.load(modelTransformation);
    }

    public void loadReflectionTexture(int textureUnit) {
        reflectionTexture.load(textureUnit);
    }

    public void loadRefractionTexture(int textureUnit) {
        refractionTexture.load(textureUnit);
    }

    public void loadDuDvTexture(int textureUnit) {
        dudvTexture.load(textureUnit);
    }


    public void loadNormalMap(int textureUnit) {
        normalMap.load(textureUnit);
    }

    public void loadWaveOffset(float offset) {
        waveOffset.load(offset);
    }

    public void loadCameraPosition(Vector3f position){
        cameraPosition.load(position);
    }

    public void loadSun(Sun sun){
        sunDirection.load(sun.getDirection());
        sunColour.load(sun.getColour());
    }
}
