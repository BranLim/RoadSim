package com.layhill.roadsim.gameengine.graphics.lights;

import org.joml.Vector3f;

public class Light {

    public final static int DEFAULT_SHADOW_RESOLUTION = 1024;
    private Vector3f position;
    private Vector3f colour;
    protected ShadowMap shadowMap;
    protected int shadowResolution = DEFAULT_SHADOW_RESOLUTION;

    public Light(Vector3f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setPosition(Vector3f position){
        this.position = position;
    }

    public int getShadowResolution() {
        return shadowResolution;
    }

    public void setShadowResolution(int shadowResolution) {
        this.shadowResolution = shadowResolution;
        shadowMap.resize(shadowResolution);
    }

    public ShadowMap getShadowMap() {
        return shadowMap;
    }

    public void dispose() {
        if (shadowMap != null) {
            shadowMap.dispose();
        }
    }
}
