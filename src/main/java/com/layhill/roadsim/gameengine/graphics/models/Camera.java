package com.layhill.roadsim.gameengine.graphics.models;

import com.layhill.roadsim.gameengine.GameScene;
import com.layhill.roadsim.gameengine.input.KeyListener;
import com.layhill.roadsim.gameengine.input.MouseListener;
import com.layhill.roadsim.gameengine.terrain.Terrain;
import com.layhill.roadsim.gameengine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private static final int NUM_OF_FRUSTUM_PLANES = 6;
    private static final float SPEED = 20.f;
    private static final float TURNSPEED = .01f;
    private Vector3f position;


    private float nearPlane;
    private float farPlane;
    private Vector3f upDirection;
    private Vector3f front;
    private Matrix4f projection = new Matrix4f();
    private Quaternionf orientation;
    private float currentSpeed = 0.f;
    private float mouseSensitivity = TURNSPEED;
    private Vector4f[] frustumPlanes = new Vector4f[NUM_OF_FRUSTUM_PLANES];
    private GameScene scene;
    private float pitch;
    private float yaw;
    private float roll;

    public Camera(Vector3f position,  Vector3f upDirection, Vector3f front,float nearPlane, float farPlane) {
        this.position = position;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.upDirection = upDirection;
        this.front = front;
        orientation = Maths.createLookAt(this.position, this.front, new Vector3f(0.f, 0.f, -1.f), new Vector3f(0.f, 1.f, 0.f));
        projection.setPerspective((float) Math.toRadians(45.0f), 1920f / 1080f, this.nearPlane, this.farPlane);
        for (int i = 0; i < NUM_OF_FRUSTUM_PLANES; i++) {
            frustumPlanes[i] = new Vector4f();
        }
    }

    public void setGameScene(GameScene gameScene) {
        this.scene = gameScene;
    }

    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public Matrix4f getViewMatrix() {
        return Maths.createViewMatrix(position, orientation);
    }

    public void updateFrustum() {
        Matrix4f viewSpace = new Matrix4f(projection);
        viewSpace.mul(getViewMatrix());
        for (int i = 0; i < NUM_OF_FRUSTUM_PLANES; i++) {
            viewSpace.frustumPlane(i, frustumPlanes[i]);
        }
    }


    public void rotate(float deltaTime) {
        float deltaPitch = MouseListener.getDeltaY() * mouseSensitivity * deltaTime;
        float deltaYaw = MouseListener.getDeltaX() * mouseSensitivity * deltaTime;
        System.out.printf("Delta PitchAmount: %f \n", deltaPitch);
        System.out.printf("Delta YawAmount: %f \n", deltaYaw);
        orientation.rotateLocalX(-deltaPitch).rotateY(-deltaYaw);
        pitch += deltaPitch;
        yaw += deltaYaw;
        System.out.printf("Final PitchAmount: %f \n", pitch);
        System.out.printf("Final YawAmount: %f \n", yaw);
        updateFrustum();
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
        if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            position.add(orientation.positiveX(new Vector3f()).mul(distance));
        }
        updateFrustum();
        for (Terrain terrain : scene.getTerrains()) {
            if (terrain.isOnThisTerrain(position.x, position.z)) {
                float terrainHeight = terrain.getHeight(position.x, position.z);
                if (position.y < terrainHeight) {
                    position.y = terrainHeight;
                }
                break;
            }
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternionf getOrientation() {
        return orientation;
    }

    public Vector3f getUpDirection() {
        return upDirection;
    }

    public float getNearPlane() {
        return nearPlane;
    }

    public float getFarPlane() {
        return farPlane;
    }

    public Vector3f getForwardDirection() {
        Vector3f forwardDirection = new Vector3f();
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.set(getViewMatrix());
        viewMatrix.invert().transformDirection(new Vector3f(front), forwardDirection);
        return forwardDirection;
    }

    public Vector3f getFront() {
        return front;
    }
}
