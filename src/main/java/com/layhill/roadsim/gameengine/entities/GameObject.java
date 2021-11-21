package com.layhill.roadsim.gameengine.entities;

import com.layhill.roadsim.gameengine.graphics.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.Texture;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

    public Matrix4f getTransformationMatrix() {
        Matrix4f matrix = new Matrix4f();
        matrix.identity()
                .translate(position, matrix)
                .rotate((float) Math.toRadians(rotateX), new Vector3f(1.f, 0.f, 0.f), matrix)
                .rotate((float) Math.toRadians(rotateY), new Vector3f(0.f, 1.f, 0.f), matrix)
                .rotate((float) Math.toRadians(rotateZ), new Vector3f(0.f, 0.f, 1.f), matrix)
                .scale(new Vector3f(scale, scale, scale), matrix);

        return matrix;
    }

    public void render() {
        texturedModel.render();
    }

    public TexturedModel getTexturedModel(){
        return texturedModel;
    }

    public Texture getTexture(){
        return texturedModel.getTexture();
    }

    public void cleanUp() {
        if (texturedModel != null) {
            texturedModel.dispose();
        }
    }
}
