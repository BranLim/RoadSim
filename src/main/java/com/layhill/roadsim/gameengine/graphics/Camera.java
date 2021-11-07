package com.layhill.roadsim.gameengine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private Vector3f position;
    private Vector3f lookAt;
    private Matrix4f projection;
    private Matrix4f view;
    private Vector3f upDirection;

    public Camera(Vector3f position, Vector3f upDirection, Vector3f lookAt) {
        this.position = position;
        this.upDirection = upDirection;
        this.lookAt = lookAt;
        projection = new Matrix4f();
        view = new Matrix4f();
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public Matrix4f getView() {
        return view;
    }

    public void init() {
        createProjectionMatrix();
        createViewMatrix();
    }

    private void createProjectionMatrix() {
        projection.identity();
        projection.perspective(0.65f, 1.0f, 1.0f, 500.0f);
    }

    private void createViewMatrix() {
        view.identity();
        view.lookAt(position, lookAt, upDirection);
    }
}
