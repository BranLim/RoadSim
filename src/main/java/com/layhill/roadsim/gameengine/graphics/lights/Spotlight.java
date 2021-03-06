package com.layhill.roadsim.gameengine.graphics.lights;

import org.joml.Vector3f;

public class Spotlight extends Light {

    private int shadowResolution = DEFAULT_SHADOW_RESOLUTION;
    private Vector3f direction;
    private float radius;
    private float outerRadius;

    public Spotlight(Vector3f position, Vector3f direction, Vector3f colour, float radius, float outerRadius){
        super(position, colour);
        this.direction = direction;
        this.radius = radius;
        this.outerRadius = outerRadius;
        shadowMap = new SpotlightShadowMap(shadowResolution);
    }

    public Vector3f getDirection() {
        return direction;
    }

    public float getRadius() {
        return radius;
    }

    public float getOuterRadius(){
        return outerRadius;
    }

    public void setDirection(Vector3f direction){
        this.direction = direction;
    }

    public int getShadowResolution() {
        return shadowResolution;
    }

    public void setShadowResolution(int shadowResolution) {
        this.shadowResolution = shadowResolution;
        shadowMap.resize(shadowResolution);
    }

}
