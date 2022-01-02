package com.layhill.roadsim.gameengine.graphics.lights;

import org.joml.Vector3f;

public class PointLight extends Light{
    public static final int POINT_LIGHT_SHADOW_RESOLUTION = 512;
    private float radius;

    public PointLight(Vector3f position, Vector3f colour, float radius) {
        super(position, colour);
        this.radius = radius;
        shadowMap = new PointLightShadowMap(POINT_LIGHT_SHADOW_RESOLUTION);
    }
}
