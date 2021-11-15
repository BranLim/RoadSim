package com.layhill.roadsim.gameengine.entities;

import com.layhill.roadsim.gameengine.graphics.*;
import com.layhill.roadsim.gameengine.graphics.gl.MeshModel;
import com.layhill.roadsim.gameengine.io.MeshLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;

@Slf4j
public class GameObject {

    private int vaoId;
    private MeshModel meshModel;
    private ShaderProgram shaderProgram;

    private boolean initialised = false;

    public GameObject() {

    }

    public void init() {
        if (initialised) {
            return;
        }
        createVao();
        setModel();
        setShader();
        unbind();
        initialised = true;
    }

    private void unbind() {
        glBindVertexArray(0);
    }

    private void createVao() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
    }

    private void setModel() {
        try {
            Texture texture = TextureFactory.loadAsTextureFromFile("assets/textures/stone_texture.jpg", GL_TEXTURE_2D).orElse(null);
            meshModel = new MeshModel(vaoId, MeshLoader.loadObjAsMesh("assets/models/stone.obj").get(), texture);

            meshModel.uploadToGpu();
        } catch (IOException e) {
            log.error("Error loading model file.");
        }
    }

    private void setShader() {
        try {
            Shader vertexShader = ShaderFactory.loadShaderFromFile("assets/shaders/simplevertex.glsl").orElse(null);
            Shader fragmentShader = ShaderFactory.loadShaderFromFile("assets/shaders/simplefragment.glsl").orElse(null);

            shaderProgram = new ShaderProgram();
            shaderProgram.addShader(vertexShader);
            shaderProgram.addShader(fragmentShader);
            shaderProgram.init();
        } catch (IOException e) {
            log.error("Error loading shader from file", e);
        }
    }

    public void render(Camera camera) {
        if (!initialised) {
            return;
        }
        shaderProgram.start();
        shaderProgram.uploadMat4f("uProjection", camera.getProjectionMatrix());
        shaderProgram.uploadMat4f("uView", camera.getViewMatrix());
        shaderProgram.uploadTexture("fTexture", 0);

        meshModel.render();

        glBindVertexArray(0);
        shaderProgram.stop();
    }

    public void cleanUp() {
        glDeleteVertexArrays(vaoId);
        if (meshModel != null) {
            meshModel.dispose();
        }
        if (shaderProgram != null) {
            shaderProgram.dispose();
        }


    }

}
