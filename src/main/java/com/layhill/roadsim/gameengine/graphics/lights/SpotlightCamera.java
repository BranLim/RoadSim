package com.layhill.roadsim.gameengine.graphics.lights;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SpotlightCamera {

    private final Vector3f UP = new Vector3f(0, 1, 0);
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Vector3f direction = new Vector3f();


    public SpotlightCamera(float fovInDegree, int width, int height) {
        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(fovInDegree),
                (float) width / (float) height, 0.1f, 100f);
        viewMatrix = new Matrix4f();
    }

    public void update(Vector3f position, Vector3f direction) {
        this.direction.set(direction);
        viewMatrix.setLookAt(position, direction.add(position), UP);
    }

    public Matrix4f getProjectionMatrix(){
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix(){
        return viewMatrix;
    }

    public Matrix4f getProjectionViewMatrix(){
        Matrix4f tempProjectionMatrix = new Matrix4f(projectionMatrix);
        return tempProjectionMatrix.mul(viewMatrix);
    }

}
