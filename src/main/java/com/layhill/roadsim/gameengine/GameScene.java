package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.*;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.io.MeshLoader;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;

@Slf4j
public class GameScene extends Scene {
    private List<Integer> vaos = new ArrayList<>();
    private List<GameObject> gameObjects = new ArrayList<>();
    private Light light;
    private RenderingManager renderingManager;

    public GameScene(RenderingManager renderingManager) {
        this.renderingManager = renderingManager;
    }

    @Override
    public void init() {

        int vao = createAndBindVao();
        vaos.add(vao);

        camera = new Camera(new Vector3f(0.0f, 10.0f, 50.f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f));
        light = new Light(new Vector3f(50.f, 50.f, 10.f), new Vector3f(1.0f, 1.0f, 1.0f));

        Optional<Texture> grassTexture = TextureFactory.loadAsTextureFromFile("assets/textures/grass_texture.jpg", GL_TEXTURE_2D);
        Optional<Mesh> terrainMesh = MeshLoader.loadObjAsMesh("assets/models/terrain.obj");

        if (terrainMesh.isPresent() && grassTexture.isPresent()) {
            TexturedModel model = new TexturedModel(vao, terrainMesh.get(), grassTexture.get());
            model.uploadToGpu();

            GameObject gameObject = new GameObject(new Vector3f(0.f, 0.f, 0.f), 0.f, 0.f, 0.f, 10.0f, model);
            gameObjects.add(gameObject);
        }

        unbind();

        vao = createAndBindVao();
        vaos.add(vao);

        Optional<Texture> stoneTexture = TextureFactory.loadAsTextureFromFile("assets/textures/stone_texture.jpg", GL_TEXTURE_2D);
        Optional<Mesh> stoneMesh = MeshLoader.loadObjAsMesh("assets/models/stone.obj");

        if (stoneMesh.isPresent() && stoneTexture.isPresent()) {
            TexturedModel model = new TexturedModel(vao, stoneMesh.get(), stoneTexture.get());
            var texture = model.getTexture();
            texture.setReflectivity(0.8f);
            texture.setShineDampener(2.0f);
            model.uploadToGpu();

            GameObject gameObject = new GameObject(new Vector3f(0.f, 10.f, 0.f), 0.f, 0.f, 0.0f, 2.0f, model);
            gameObjects.add(gameObject);
        }

        unbind();
    }

    @Override
    public void update(float deltaTime) {

        if (MouseListener.isActiveInWindow()) {
            camera.rotate(deltaTime);
            MouseListener.endFrame();
        }

        camera.move(deltaTime);

        for (GameObject gameObject : gameObjects) {
            renderingManager.addToQueue(gameObject);
        }
        renderingManager.addToQueue(light);
        renderingManager.run(camera);
    }

    @Override
    public void cleanUp() {

        renderingManager.cleanUp();
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
}
