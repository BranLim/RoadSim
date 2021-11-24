package com.layhill.roadsim.gameengine.entities;

import com.layhill.roadsim.gameengine.graphics.Renderable;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

@Slf4j
public class GameObject implements Renderable {
    private Vector3f position;
    private float rotateX;
    private float rotateY;
    private float rotateZ;
    private float scale;
    private TexturedModel texturedModel;

    public GameObject(Vector3f position, float rotateX, float rotateY, float rotateZ, float scale,
                      TexturedModel meshModel) {
        this.position = position;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
        this.scale = scale;
        this.texturedModel = meshModel;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotateX() {
        return rotateX;
    }

    public float getRotateY() {
        return rotateY;
    }

    public float getRotateZ() {
        return rotateZ;
    }

    public float getScale() {
        return scale;
    }

    @Override
    public TexturedModel getTexturedModel(){
        return texturedModel;
    }
}
