package com.layhill.roadsim.gameengine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private Vector3f lookAt;
    private Matrix4f projection;
    private Matrix4f view;

    public Camera(Vector3f position, Vector3f lookAt) {
        this.position = position;
        this.lookAt = lookAt;
        projection = new Matrix4f();
        view = new Matrix4f();
    }

    public void createProjectionMatrix() {
        projection.identity();
        projection.perspective((float) Math.toRadians(0.75f), 0.75f, 0.1f, 100.0f);
    }

    public void createViewMatrix() {
        Vector3f upDirection = new Vector3f(0.0f, 1.0f, 0.0f);
        view.identity();
        view.lookAt(position, lookAt, upDirection);
    }
}
