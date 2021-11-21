package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.KeyListener;
import com.layhill.roadsim.gameengine.MouseListener;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private static final float SPEED = 10.f;
    private static final float TURNSPEED = .01f;
    private Vector3f position;
    private Vector3f upDirection;
    private Vector3f front;
    private Matrix4f projection = new Matrix4f();
    private Quaternionf orientation;
    private float currentSpeed = 0.f;
    private float mouseSensitivity = TURNSPEED;

    public Camera(Vector3f position, Vector3f upDirection, Vector3f front) {
        this.position = position;
        this.upDirection = upDirection;
        this.front = front;
        orientation = com.layhill.roadsim.gameengine.utils.Math.lookAt(this.position, this.front, new Vector3f(0.f, 0.f, -1.f), new Vector3f(0.f, 1.f, 0.f));
        projection.setPerspective((float) Math.toRadians(60.0), 1920f / 1080f, 1.0f, 1000.0f);
    }

    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity()
                .rotate(orientation)
                .translate(new Vector3f(position).negate());
        return viewMatrix;
    }

    public void rotate(float deltaTime) {
        float pitchAmount = MouseListener.getDeltaY() * mouseSensitivity * deltaTime;
        float yawAmount = MouseListener.getDeltaX() * mouseSensitivity * deltaTime;

        orientation.rotateLocalX(-pitchAmount).rotateLocalY(-yawAmount);
    }

    public void move(float deltaTime) {

        currentSpeed = SPEED;
        float distance = currentSpeed * deltaTime;
        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            position.sub(orientation.positiveZ(new Vector3f()).mul(distance));
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            position.add(orientation.positiveZ(new Vector3f()).mul(distance));
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            position.sub(orientation.positiveX(new Vector3f()).mul(distance));
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_D)){
            position.add(orientation.positiveX(new Vector3f()).mul(distance));
        }


    }
}
