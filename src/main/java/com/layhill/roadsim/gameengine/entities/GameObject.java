package com.layhill.roadsim.gameengine.entities;

import com.layhill.roadsim.gameengine.graphics.Camera;
import com.layhill.roadsim.gameengine.graphics.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

@Slf4j
public class GameObject {
    private Vector3f position;
    private float rotateX;
    private float rotateY;
    private float rotateZ;
    private float scale;
    private TexturedModel texturedModel;
    private ShaderProgram shaderProgram;

    public GameObject(Vector3f position, float rotateX, float rotateY, float rotateZ, float scale,
                      TexturedModel meshModel, ShaderProgram shaderProgram) {
        this.position = position;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
        this.scale = scale;
        this.texturedModel = meshModel;
        this.shaderProgram = shaderProgram;
    }

    public void render() {
        texturedModel.render();
    }

    public void cleanUp() {
        if (texturedModel != null) {
            texturedModel.dispose();
        }
    }
}
