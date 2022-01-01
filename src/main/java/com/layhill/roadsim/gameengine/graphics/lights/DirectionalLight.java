package com.layhill.roadsim.gameengine.graphics.lights;

import org.joml.Vector3f;

public class DirectionalLight extends Light{

    private int shadowResolution = 1024;

    public DirectionalLight(Vector3f position, Vector3f colour) {
        super(position, colour);
    }


    public int getShadowResolution() {
        return shadowResolution;
    }

    public void setShadowResolution(int shadowResolution) {
        this.shadowResolution = shadowResolution;
    }
}
