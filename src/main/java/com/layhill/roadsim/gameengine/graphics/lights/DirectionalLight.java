package com.layhill.roadsim.gameengine.graphics.lights;

import org.joml.Vector3f;

public class DirectionalLight extends Light {

    private int shadowResolution = 1024;
    private DirectionalLightShadowMap shadowMap;

    public DirectionalLight(Vector3f direction, Vector3f colour) {
        super(direction, colour);
        shadowMap = new DirectionalLightShadowMap(shadowResolution);
    }

    public int getShadowResolution() {
        return shadowResolution;
    }

    public void setShadowResolution(int shadowResolution) {
        this.shadowResolution = shadowResolution;
        shadowMap.resize(shadowResolution);
    }

    public DirectionalLightShadowMap getShadowMap() {
        return shadowMap;
    }

    public void dispose() {
        if (shadowMap != null) {
            shadowMap.dispose();
        }
    }
}
