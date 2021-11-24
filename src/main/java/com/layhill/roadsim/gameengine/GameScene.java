package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.models.*;
import com.layhill.roadsim.gameengine.graphics.*;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.resources.ResourceManager;
import com.layhill.roadsim.gameengine.terrain.Terrain;
import com.layhill.roadsim.gameengine.terrain.TerrainGenerator;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import java.util.*;

@Slf4j
public class GameScene extends Scene {
    private List<Renderable> gameObjects = new ArrayList<>();
    private ResourceManager resourceManager = new ResourceManager();
    private RenderingManager renderingManager;

    public GameScene(RenderingManager renderingManager) {
        this.renderingManager = renderingManager;
    }

    @Override
    public void init() {

        camera = new Camera(new Vector3f(0.0f, 10.0f, 50.f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f));

        List<Terrain> terrains = TerrainGenerator.generateTerrains(resourceManager);
        gameObjects.addAll(terrains);

        TexturedModel stoneModel = resourceManager.loadTexturedModel("assets/models/stone.obj", "assets/textures/stone_texture.jpg", "Rock");
        if (stoneModel != null) {
            Material material = stoneModel.getMaterial();
            material.setReflectivity(0.8f);
            material.setShineDampener(2.0f);
            material.attachShaderProgram(ShaderFactory.createDefaultShaderProgram());

            Random random = new Random();
            for (int i = 0; i < 5; i++) {
                float xPos = random.nextFloat(-50.0f, 50.0f);
                float zPos = random.nextFloat(-50.0f, 50.0f);
                GameObject stoneObject = new GameObject(new Vector3f(xPos, 2.4f, zPos), 0.f, 0.f, 0.0f, 2.0f, stoneModel);
                gameObjects.add(stoneObject);
                renderingManager.addToQueue(new Light(new Vector3f(xPos + 5.f, 20.f, zPos - 5.f), new Vector3f(1.0f, 1.0f, 1.0f)));
            }
        }
    }

    @Override
    public void update(float deltaTime) {

        if (MouseListener.isActiveInWindow()) {
            camera.rotate(deltaTime);
            MouseListener.endFrame();
        }

        camera.move(deltaTime);

        for (Renderable gameObject : gameObjects) {
            renderingManager.addToQueue(gameObject);
        }
        renderingManager.run(camera);
    }

    @Override
    public void cleanUp() {

        resourceManager.dispose();
        renderingManager.dispose();
    }

}
