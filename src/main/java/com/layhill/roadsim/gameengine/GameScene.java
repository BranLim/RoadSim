package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.*;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.io.MeshLoader;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;

@Slf4j
public class GameScene extends Scene {
    private List<Integer> vaos = new ArrayList<>();
    private List<GameObject> gameObjects = new ArrayList<>();
    private ShaderProgram shaderProgram;
    private Light light;

    public GameScene() {
    }

    @Override
    public void init() {

        int vao = createAndBindVao();
        vaos.add(vao);
        shaderProgram = loadShaders();

        camera = new Camera(new Vector3f(-25.0f, 50.0f, 25.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f));
        light = new Light(new Vector3f(0.f, 150.f, 50.f), new Vector3f(1.0f, 1.0f, 1.0f));

        Optional<Texture> grassTexture = TextureFactory.loadAsTextureFromFile("assets/textures/grass_texture.jpg", GL_TEXTURE_2D);
        Optional<Mesh> terrainMesh = MeshLoader.loadObjAsMesh("assets/models/terrain.obj");

        if (terrainMesh.isPresent() && grassTexture.isPresent()) {
            TexturedModel model = new TexturedModel(vao, terrainMesh.get(), grassTexture.get());
            model.uploadToGpu();

            GameObject gameObject = new GameObject(new Vector3f(0.f, 0.f, 0.f), -45.0f, 0, -38.0f, 5.0f, model, shaderProgram);
            gameObjects.add(gameObject);
        }

        unbind();
        vao = createAndBindVao();
        vaos.add(vao);
        Optional<Texture> stoneTexture = TextureFactory.loadAsTextureFromFile("assets/textures/stone_texture.jpg", GL_TEXTURE_2D);
        Optional<Mesh> stoneMesh = MeshLoader.loadObjAsMesh("assets/models/stone.obj");

        if (stoneMesh.isPresent() && stoneTexture.isPresent()) {
            TexturedModel model = new TexturedModel(vao, stoneMesh.get(), stoneTexture.get());
            model.uploadToGpu();

            GameObject gameObject = new GameObject(new Vector3f(0.f, 5.f, -10.f), -45.0f, 0.f, -38.0f, 2.0f, model, shaderProgram);
            gameObjects.add(gameObject);
        }

        unbind();
    }

    @Override
    public void update(float deltaTime) {

        if (MouseListener.isActiveInWindow()) {
            camera.turn(deltaTime);
            MouseListener.endFrame();
        }
        shaderProgram.start();
        camera.move(deltaTime);
        shaderProgram.uploadMat4f("uProjection", camera.getProjectionMatrix());
        shaderProgram.uploadMat4f("uView", camera.getViewMatrix());
        shaderProgram.uploadTexture("fTexture", 0);

        for (Integer vao : vaos) {
            bindVao(vao);
            for (GameObject gameObject : gameObjects) {
                shaderProgram.uploadVec3f("uLightPosition", light.getPosition());
                shaderProgram.uploadVec3f("fLightColour", light.getColour());
                shaderProgram.uploadMat4f("uTransformation", gameObject.getTransformationMatrix());
                gameObject.render();
            }

            unbind();
        }

        shaderProgram.stop();
    }

    @Override
    public void cleanUp() {
        if (shaderProgram != null) {
            shaderProgram.dispose();
        }
        for (GameObject gameObject : gameObjects) {
            gameObject.cleanUp();
        }
        for (Integer vao : vaos) {
            glDeleteVertexArrays(vao);
        }
    }

    private int createAndBindVao() {
        int vaoId = glGenVertexArrays();
        bindVao(vaoId);
        return vaoId;
    }

    private void bindVao(int vaoId) {
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
