package com.layhill.roadsim.gameengine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private Vector3f position;
    private Matrix4f projection;
    private Matrix4f viewMatrix = new Matrix4f();
    private Vector3f upDirection;
    private float pitch;
    private float yaw;
    private float roll;

    public Camera(Vector3f position, Vector3f upDirection, Vector3f lookAt) {
        this.position = position;
        this.upDirection = upDirection;
        createProjectionMatrix();
        calculateViewMatrixBasedOnLookAt(lookAt);
    }

    public Camera(Vector3f position, Vector3f upDirection, float pitch, float yaw, float roll) {
        this.position = position;
        this.upDirection = upDirection;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        createProjectionMatrix();
    }

    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    private void createProjectionMatrix() {
        projection = new Matrix4f();
        projection.identity();
        projection.perspective((float) Math.toRadians(38.0), 1.0f, 1.0f, 500.0f);
    }

    private void calculateViewMatrixBasedOnLookAt(Vector3f lookAt) {
        viewMatrix.identity();
        viewMatrix.lookAt(position,lookAt ,upDirection);
        Vector3f direction = (new Vector3f(lookAt).sub(position)).normalize();
        pitch = (float) Math.toDegrees(Math.asin(direction.y));
        yaw = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));
    }


    public void calculateViewMatrix() {
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
        viewMatrix.rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1));
        Vector3f negativeCameraPosition = new Vector3f(-position.x, -position.y, -position.z);
        viewMatrix.translate(negativeCameraPosition);
    }


    public void lookLeftOrRight(double turnAmount) {
        yaw -= turnAmount;
    }

    public void lookUpOrDown(float amount) {
        pitch -= amount;
        if (pitch < -90.0f || pitch > 90.0f){
            pitch = 90.0f;
        }

    }
}
