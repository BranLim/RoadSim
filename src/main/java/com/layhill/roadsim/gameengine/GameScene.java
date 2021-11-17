package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.*;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.io.MeshLoader;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;

@Slf4j
public class GameScene extends Scene {
    private GameObject gameObject;
    private ShaderProgram shaderProgram;
    private int vaoId;

    public GameScene() {
    }

    @Override
    public void init() {
        createAndBindVao();
        shaderProgram = loadShaders();
        camera = new Camera(new Vector3f(0.0f, 0.0f, 5.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -20.0f));
        Optional<Texture> texture = TextureFactory.loadAsTextureFromFile("assets/textures/stone_texture.jpg", GL_TEXTURE_2D);
        Optional<Mesh> mesh = MeshLoader.loadObjAsMesh("assets/models/stone.obj");

        if (mesh.isPresent() && texture.isPresent()) {
            TexturedModel model = new TexturedModel(vaoId, mesh.get(), texture.get());
            model.uploadToGpu();

            gameObject = new GameObject(new Vector3f(0.f, 0.f, 0.f), 0.f, 0.f, 0.f, 1.0f, model, shaderProgram);
        }
        unbind();
    }

    @Override
    public void update(float deltaTime) {
        bindVao();
        if (MouseListener.isActiveInWindow()) {
            camera.turn(deltaTime);
            MouseListener.endFrame();
        }
        shaderProgram.start();
        camera.move(deltaTime);
        shaderProgram.uploadMat4f("uProjection", camera.getProjectionMatrix());
        shaderProgram.uploadMat4f("uView", camera.getViewMatrix());
        shaderProgram.uploadTexture("fTexture", 0);
        gameObject.render();

        unbind();
        shaderProgram.stop();
    }

    @Override
    public void cleanUp() {
        if (shaderProgram != null) {
            shaderProgram.dispose();
        }
        if (gameObject != null) {
            gameObject.cleanUp();
        }

        glDeleteVertexArrays(vaoId);
    }

    private void createAndBindVao() {
        vaoId = glGenVertexArrays();
        bindVao();
    }

    private void bindVao() {
        glBindVertexArray(vaoId);
    }

    private void unbind() {
        glBindVertexArray(0);
    }

    private ShaderProgram loadShaders() {
        ShaderProgram shaderProgram = new ShaderProgram();
        try {
            Shader vertexShader = ShaderFactory.loadShaderFromFile("assets/shaders/simplevertex.glsl").orElse(null);
            Shader fragmentShader = ShaderFactory.loadShaderFromFile("assets/shaders/simplefragment.glsl").orElse(null);

            shaderProgram.addShader(vertexShader);
            shaderProgram.addShader(fragmentShader);
            shaderProgram.init();
        } catch (IOException e) {
            log.error("Error loading shader from file", e);
        }
        return shaderProgram;
    }
}
