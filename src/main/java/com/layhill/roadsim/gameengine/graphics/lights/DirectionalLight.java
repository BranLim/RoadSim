package com.layhill.roadsim.gameengine.graphics.lights;

import org.joml.Vector3f;

public class DirectionalLight extends Light {

    public DirectionalLight(Vector3f direction, Vector3f colour) {
        super(direction, colour);
        shadowMap = new DirectionalLightShadowMap(shadowResolution);
    }

}
