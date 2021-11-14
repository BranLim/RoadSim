package com.layhill.roadsim.gameengine.entities;

import com.layhill.roadsim.gameengine.graphics.*;
import com.layhill.roadsim.gameengine.io.MeshLoader;
import com.layhill.roadsim.gameengine.graphics.gl.MeshModel;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

@Slf4j
public class GameObject {

    private int vaoId;
    private int textureBufferId;
    private MeshModel meshModel;
    private ShaderProgram shaderProgram;
    private Texture texture;
    private List<Integer> attributes = new ArrayList<>();
    private boolean initialised = false;

    public GameObject() {

    }

    public void init() {
        if (initialised) {
            return;
        }
        float[] uvMappings = {
                1.f, 1.f, 0.f, 0.f, 1.f, 0.f, 0.f, 1.f
        };
        createVao();
        setModel(0);
        setShader();
        setTexture(2, uvMappings);
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

    private void setModel(int attribute) {
        try {
            meshModel = new MeshModel(vaoId, attribute, MeshLoader.loadObjAsMesh("assets/models/stone.obj").get(), null);
            attributes.add(attribute);
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

    private void setTexture(int attribute, float[] uvMappings) {
        textureBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, textureBufferId);

        FloatBuffer uvBuffer = uvMappingToFloatBuffer(uvMappings);
        glBufferData(GL_ARRAY_BUFFER, uvBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attribute, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(attribute);

        attributes.add(attribute);

        texture = TextureFactory.loadAsTextureFromFile("assets/textures/bricks.jpg", GL_TEXTURE_2D).orElse(null);
        if (texture != null) {
            texture.generate();
            texture.bind();
            texture.prepare();
            texture.render();
        }
    }

    private FloatBuffer uvMappingToFloatBuffer(float[] uvMappings) {
        FloatBuffer uvBuffer = BufferUtils.createFloatBuffer(uvMappings.length);
        uvBuffer.put(uvMappings);
        uvBuffer.flip();
        return uvBuffer;
    }

    public void render(Camera camera) {
        if (!initialised) {
            return;
        }
        shaderProgram.start();
        shaderProgram.uploadMat4f("uProjection", camera.getProjectionMatrix());
        shaderProgram.uploadMat4f("uView", camera.getViewMatrix());

        if (texture != null) {
            shaderProgram.uploadTexture("fTexture", 0);
            glActiveTexture(GL_TEXTURE0);
            texture.bind();
        }
        glBindVertexArray(vaoId);
        for (var attribute : attributes) {
            glEnableVertexAttribArray(attribute);
        }
        meshModel.render();
        for (var attribute : attributes) {
            glDisableVertexAttribArray(attribute);
        }
        if (texture != null) {
            texture.unbind();
        }

        glBindVertexArray(0);
        shaderProgram.stop();
    }

    public void cleanUp() {
        glDeleteVertexArrays(vaoId);
        if (meshModel != null) {
            meshModel.dispose();
        }
        if (texture != null) {
            texture.dispose();
        }
        glDeleteBuffers(textureBufferId);
        if (shaderProgram != null) {
            shaderProgram.dispose();
        }


    }

}
