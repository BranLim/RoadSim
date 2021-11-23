package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.graphics.models.Material;
import com.layhill.roadsim.gameengine.graphics.*;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.resources.ResourceManager;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import java.util.*;

@Slf4j
public class GameScene extends Scene {
    private Light light;
    private List<GameObject> gameObjects = new ArrayList<>();
    private ResourceManager resourceManager = new ResourceManager();
    private RenderingManager renderingManager;

    public GameScene(RenderingManager renderingManager) {
        this.renderingManager = renderingManager;
    }

    @Override
    public void init() {

        camera = new Camera(new Vector3f(0.0f, 10.0f, 50.f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f));
        light = new Light(new Vector3f(50.f, 50.f, 10.f), new Vector3f(1.0f, 1.0f, 1.0f));

        TexturedModel terrainModel = resourceManager.loadTexturedModel("assets/models/terrain.obj", "assets/textures/grass_texture.jpg", "Terrain");
        if (terrainModel != null) {
            GameObject gameObject = new GameObject(new Vector3f(0.f, 0.f, 0.f), 0.f, 0.f, 0.f, 10.0f, terrainModel);
            gameObjects.add(gameObject);
        }
        TexturedModel stoneModel = resourceManager.loadTexturedModel("assets/models/stone.obj", "assets/textures/stone_texture.jpg", "Rock");
        if (stoneModel!=null) {
            Material material = stoneModel.getMaterial();
            material.setReflectivity(0.8f);
            material.setShineDampener(2.0f);

            GameObject gameObject = new GameObject(new Vector3f(0.f, 10.f, 0.f), 0.f, 0.f, 0.0f, 2.0f, stoneModel);
            gameObjects.add(gameObject);
        }
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

        resourceManager.dispose();
        renderingManager.dispose();
    }

}
