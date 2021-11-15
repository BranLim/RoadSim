package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.KeyListener;
import com.layhill.roadsim.gameengine.MouseListener;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private Vector3f position;
    private Vector3f upDirection;
    private Vector3f lookAt;
    private Matrix4f projection = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();

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
        Vector3f direction = new Vector3f(lookAt).sub(position).normalize();
        int pitchSign = (int)Math.signum(direction.y);
        int yawSign = (int)Math.signum(direction.x);

        float pitchAmount = MouseListener.getDeltaY() * 0.1f * deltaTime;
        float yawAmount = MouseListener.getDeltaX() * 0.1f * deltaTime;

        lookAt.y -= pitchSign * pitchAmount;
        lookAt.x -= yawSign * yawAmount;
        if (lookAt.y < -90.0f || lookAt.y > 90.0f) {
            lookAt.y = 90.0f;
        }
        calculateViewMatrix();
    }

    public void move(float deltaTime) {

        Vector3f direction = new Vector3f(lookAt).sub(position).normalize();
        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            position.z +=  (int)Math.signum(direction.z) * 4.8f * deltaTime ;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            position.x -= 2.0f * deltaTime;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            position.x += 2.0f * deltaTime;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            position.z -= (int)Math.signum(direction.z) * 3.8f * deltaTime;
        }
        calculateViewMatrix();
    }
}
