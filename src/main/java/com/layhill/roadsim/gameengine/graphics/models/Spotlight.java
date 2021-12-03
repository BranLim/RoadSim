package com.layhill.roadsim.gameengine.graphics.models;

import org.joml.Vector3f;

public class Spotlight extends Light{

    private Vector3f direction;
    private float radius;

    public Spotlight(Vector3f position, Vector3f direction, Vector3f colour, float radius){
        super(position, colour);
        this.direction = direction;
        this.radius = radius;
    }


    public Vector3f getDirection() {
        return direction;
    }

    public float getRadius() {
        return radius;
    }

    public void setDirection(Vector3f direction){
        this.direction = direction;
    }

}
