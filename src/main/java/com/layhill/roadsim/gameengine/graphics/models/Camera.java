package com.layhill.roadsim.gameengine.graphics.models;

import com.layhill.roadsim.gameengine.GameScene;
import com.layhill.roadsim.gameengine.input.KeyListener;
import com.layhill.roadsim.gameengine.input.MouseListener;
import com.layhill.roadsim.gameengine.terrain.Terrain;
import com.layhill.roadsim.gameengine.utils.Transformation;
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
    private Vector3f upDirection;
    private Vector3f front;
    private Matrix4f projection = new Matrix4f();
    private Quaternionf reflectedtOrientation;
    private Quaternionf orientation;
    private float currentSpeed = 0.f;
    private float mouseSensitivity = TURNSPEED;
    private Vector4f[] frustumPlanes = new Vector4f[NUM_OF_FRUSTUM_PLANES];
    private GameScene scene;
    private boolean reflected;

    public Camera(Vector3f position, Vector3f upDirection, Vector3f front) {
        this.position = position;
        this.upDirection = upDirection;
        this.front = front;
        orientation = Transformation.createLookAt(this.position, this.front, new Vector3f(0.f, 0.f, -1.f), new Vector3f(0.f, 1.f, 0.f));

        Vector3f pitchedFront = new Vector3f(front);
        pitchedFront.y *= -1.f;
        float distance = 2 * (getPosition().y - (-1.2f));
        reflectedtOrientation = Transformation.createLookAt(new Vector3f(position.x, position.y - distance, position.z), pitchedFront, new Vector3f(0.f, 0.f, -1.f), new Vector3f(0.f, 1.f, 0.f));

        projection.setPerspective((float) Math.toRadians(45.0f), 1920f / 1080f, 1.0f, 500.0f);
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

    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        if (reflected) {
            float distance = 2 * (getPosition().y - (-1.2f));
            viewMatrix.identity()
                    .rotate(reflectedtOrientation)
                    .translate(new Vector3f(position.x, position.y - distance, position.z).negate());
            return viewMatrix;
        }
        viewMatrix.identity()
                .rotate(orientation)
                .translate(new Vector3f(position).negate());
        return viewMatrix;
    }

    public void updateFrustum() {
        Matrix4f viewSpace = new Matrix4f(projection);
        viewSpace.mul(getViewMatrix());
        for (int i = 0; i < NUM_OF_FRUSTUM_PLANES; i++) {
            viewSpace.frustumPlane(i, frustumPlanes[i]);
        }
    }

    public void rotate(float deltaTime) {
        float pitchAmount = MouseListener.getDeltaY() * mouseSensitivity * deltaTime;
        float yawAmount = MouseListener.getDeltaX() * mouseSensitivity * deltaTime;

        orientation.rotateLocalX(-pitchAmount).rotateY(-yawAmount);
        reflectedtOrientation.rotateLocalX(pitchAmount).rotateY(-yawAmount);
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
                // System.out.printf("X: %f, Z: %f => Terrain Height: %f , Position (Y): %f \n", position.x, position.z, terrainHeight, position.y);
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

    public Vector3f getDirection() {

        Vector3f direction = new Vector3f();

        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.set(getViewMatrix());

        viewMatrix.invert().transformDirection(new Vector3f(front), direction);
        return direction;
    }

    public void setReflected(boolean reflected) {
        this.reflected = reflected;
    }
}
