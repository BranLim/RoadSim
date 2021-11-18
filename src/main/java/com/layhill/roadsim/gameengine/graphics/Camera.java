package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.KeyListener;
import com.layhill.roadsim.gameengine.MouseListener;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private static final float SPEED = 10.f;
    private static final float TURNSPEED = .025f;
    private Vector3f position;
    private Vector3f upDirection;
    private Vector3f front;
    private Matrix4f projection = new Matrix4f();
    private Quaternionf orientation = new Quaternionf();
    private float currentSpeed = 0.f;
    private float mouseSensitivity = TURNSPEED;


    public Camera(Vector3f position, Vector3f upDirection, Vector3f front) {
        this.position = position;
        this.position.negate();
        this.upDirection = upDirection;
        this.front = front;
        Vector3f direction = new Vector3f(front).sub(position).normalize();
        orientation.lookAlong(direction, upDirection);
        projection.setPerspective((float) Math.toRadians(60.0), 1.7f, 1.0f, 1000.0f);
    }

    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity().rotate(orientation).translate(new Vector3f(position).negate());
        return viewMatrix;
    }

    public void turn(float deltaTime) {
        float pitchAmount = MouseListener.getDeltaY() * mouseSensitivity * deltaTime;
        float yawAmount = MouseListener.getDeltaX() * mouseSensitivity * deltaTime;

        orientation.rotateLocalX(pitchAmount).rotateLocalY(yawAmount);
    }

    public void move(float deltaTime) {

        currentSpeed = 0.0f;
        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            currentSpeed = -SPEED;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            currentSpeed = SPEED;
        }

        float distance = currentSpeed * deltaTime;
        position.add(orientation.positiveZ(new Vector3f()).mul(distance));
    }
}
