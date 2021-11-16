package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.KeyListener;
import com.layhill.roadsim.gameengine.MouseListener;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private static final float SPEED = 10.f;
    private Vector3f position;
    private Vector3f upDirection;
    private Vector3f lookAt;
    private Matrix4f projection = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();
    private float currentSpeed = 0.f;
    private float currentTurnSpeed = 0.1f;

    public Camera(Vector3f position, Vector3f upDirection, Vector3f lookAt) {
        this.position = position;
        this.upDirection = upDirection;
        this.lookAt = lookAt;
        createProjectionMatrix();
        calculateViewMatrix();
    }

    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    private void createProjectionMatrix() {
        projection.identity()
                .perspective((float) Math.toRadians(38.0), 1.0f, 1.0f, 1000.0f);
    }

    public void calculateViewMatrix() {
        viewMatrix.identity()
                .lookAt(position, lookAt, upDirection);
    }

    public void turn(float deltaTime) {

        float pitchAmount = MouseListener.getDeltaY() * currentTurnSpeed * deltaTime;
        float yawAmount = MouseListener.getDeltaX() * currentTurnSpeed * deltaTime;

        viewMatrix.identity()
                .rotateX(pitchAmount)
                .rotateY(yawAmount)
                .rotateZ(pitchAmount);
        calculateViewMatrix();
    }

    public void move(float deltaTime) {

        AxisAngle4f rotation = viewMatrix.getRotation(new AxisAngle4f());
        float rotationAngle = rotation.angle;

        currentSpeed = 0;
        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            currentSpeed = SPEED;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            currentSpeed = -SPEED;
        }

        float distance = currentSpeed * deltaTime;
        float dx = (float) Math.sin(rotationAngle) * distance;
        float dz = (float) Math.cos(rotationAngle) * distance;

        Vector3f distanceTravelled = new Vector3f(dx, 0, dz);

        this.position = new Vector3f(this.position).sub(distanceTravelled);
        calculateViewMatrix();
    }
}
