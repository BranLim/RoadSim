package com.layhill.roadsim.gameengine.environments;

import com.layhill.roadsim.gameengine.graphics.lights.DirectionalLight;
import org.joml.Vector3f;

public class Sun {

    private DirectionalLight directionalLight;

    public Sun(Vector3f direction, Vector3f colour) {
        directionalLight = new DirectionalLight(direction, colour);
        directionalLight.setShadowResolution(4096);
    }

    public Vector3f getDirection() {
        return directionalLight.getPosition();
    }

    public Vector3f getColour() {
        return directionalLight.getColour();
    }

    public DirectionalLight getLight(){
        return directionalLight;
    }
}
