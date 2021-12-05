package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.Renderable;
import com.layhill.roadsim.gameengine.graphics.RenderingManager;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.graphics.models.Material;
import com.layhill.roadsim.gameengine.graphics.models.Spotlight;
import com.layhill.roadsim.gameengine.resources.ResourceManager;
import com.layhill.roadsim.gameengine.terrain.Terrain;
import com.layhill.roadsim.gameengine.terrain.TerrainGenerator;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;

@Slf4j
public class GameScene extends Scene {
    private List<Terrain> terrains = new ArrayList<>();
    private List<Renderable> gameObjects = new ArrayList<>();
    private List<Light> lights = new ArrayList<>();
    private ResourceManager resourceManager = new ResourceManager();
    private RenderingManager renderingManager;
    private Spotlight spotlight;
    private boolean turnOnFlashlight = false;

    public GameScene(RenderingManager renderingManager) {
        this.renderingManager = renderingManager;
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    @Override
    public void init() {

        camera = new Camera(new Vector3f(0.0f, 10.0f, 50.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f));
        camera.setGameScene(this);

        spotlight = new Spotlight(
                new Vector3f(camera.getPosition()),
                new Vector3f(camera.getDirection()),
                new Vector3f(1.f, 1.f, 1.f),
                (float) Math.cos(Math.toRadians(10.5f)),
                (float)Math.cos(Math.toRadians(15.0f)));

        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            float xPos = random.nextFloat(-150.f, 150.f);
            float yPos = random.nextFloat(0.0f, 100.0f);
            float zPos = random.nextFloat(-150.0f, 100.0f);
            Light light = new Light(new Vector3f(xPos, yPos, zPos), new Vector3f(0.5f, 0.5f, 0.5f));
            lights.add(light);
        }

        for (int x = -2; x < 2; x++) {
            for (int z = -2; z < 2; z++) {
                Terrain terrain = TerrainGenerator.generateTerrain(resourceManager, x, z, "assets/textures/heightmap.png");
                terrains.add(terrain);
            }
        }

        gameObjects.addAll(terrains);

        TexturedModel stoneModel = resourceManager.loadTexturedModel("assets/models/stone.obj", "assets/textures/stone_texture.jpg", "Rock");
        if (stoneModel != null) {
            Material material = stoneModel.getMaterial();
            material.setReflectivity(0.3f);
            material.setShineDampener(1.4f);
            material.attachShaderProgram(ShaderFactory.createDefaultShaderProgram());

            for (int i = 0; i < 5; i++) {
                float xPos = random.nextFloat(-50.0f, 50.0f);
                float zPos = random.nextFloat(-50.0f, 50.0f);
                GameObject stoneObject = new GameObject(new Vector3f(xPos, 2.4f, zPos), 0.f, 0.f, 0.0f, 2.0f, stoneModel);
                gameObjects.add(stoneObject);
            }
        }
    }

    @Override
    public void update(float deltaTime) {

        if (MouseListener.isActiveInWindow()) {
            camera.rotate(deltaTime);
            MouseListener.endFrame();
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_F)){
            turnOnFlashlight = !turnOnFlashlight;
        }
        Light[] lightsToRender = new Light[5];
        lights.toArray(lightsToRender);
        renderingManager.addToQueue(lightsToRender);
        if (turnOnFlashlight) {
            renderingManager.addToQueue(spotlight);
        }

        camera.move(deltaTime);

        spotlight.setPosition(camera.getPosition());
        spotlight.setDirection(camera.getDirection());

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
