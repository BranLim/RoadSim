package com.layhill.roadsim.gameengine.graphics;

import org.joml.Matrix4f;

public class ViewSpecification {

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;


    public ViewSpecification(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}
