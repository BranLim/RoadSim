package com.layhill.roadsim.gameengine.graphics.models;

import org.joml.Vector3f;

public class Sun {

    private Vector3f direction;
    private Vector3f colour;


    public Sun(Vector3f direction, Vector3f colour) {
        this.direction = direction;
        this.colour = colour;
    }


    public Vector3f getDirection() {
        return direction;
    }

    public Vector3f getColour() {
        return colour;
    }
}
