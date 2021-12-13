package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.RawTexture;
import com.layhill.roadsim.gameengine.graphics.Renderable;
import com.layhill.roadsim.gameengine.graphics.RenderingManager;
import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;
import com.layhill.roadsim.gameengine.graphics.models.*;
import com.layhill.roadsim.gameengine.input.KeyListener;
import com.layhill.roadsim.gameengine.input.MouseListener;
import com.layhill.roadsim.gameengine.io.TextureLoader;
import com.layhill.roadsim.gameengine.particles.ParticleEmitterConfiguration;
import com.layhill.roadsim.gameengine.particles.ParticleSystem;
import com.layhill.roadsim.gameengine.resources.ResourceManager;
import com.layhill.roadsim.gameengine.skybox.Skybox;
import com.layhill.roadsim.gameengine.terrain.Terrain;
import com.layhill.roadsim.gameengine.terrain.TerrainGenerator;
import com.layhill.roadsim.gameengine.water.WaterFrameBuffer;
import com.layhill.roadsim.gameengine.water.WaterTile;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

@Slf4j
public class GameScene extends Scene {
    private List<Terrain> terrains = new ArrayList<>();
    private List<Renderable> gameObjects = new ArrayList<>();
    private List<Light> lights = new ArrayList<>();
    private ResourceManager resourceManager = new ResourceManager();
    private RenderingManager renderingManager;
    private Spotlight spotlight;
    private boolean turnOnFlashlight = false;
    private GLTexture fireParticleTexture;
    private GLTexture rainParticleTexture;
    private ParticleSystem particleSystem;
    private final Vector3f fogColour = new Vector3f(0.3f, 0.3f, 0.3f);
    private List<WaterTile> waterTiles = new ArrayList<>();

    private static final String skyboxMesh = "assets/models/skybox.obj";
    private final static String[] skyboxTextures = {
            "assets/textures/Daylight_Box_Right.jpg",
            "assets/textures/Daylight_Box_Left.jpg",
            "assets/textures/Daylight_Box_Top.jpg",
            "assets/textures/Daylight_Box_Bottom.jpg",
            "assets/textures/Daylight_Box_Front.jpg",
            "assets/textures/Daylight_Box_Back.jpg"
    };
    private Skybox skybox;

    private Sun sun;

    public GameScene(RenderingManager renderingManager) {
        this.renderingManager = renderingManager;
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    @Override
    public void init() {

        sun = new Sun(new Vector3f(-40000.f, 10000.f, -30000.f), new Vector3f(0.0f, 0.0f, 0.0f));
        camera = new Camera(new Vector3f(0.0f, 10.0f, 50.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f));
        camera.setGameScene(this);

        spotlight = new Spotlight(
                new Vector3f(camera.getPosition()),
                new Vector3f(camera.getDirection()),
                new Vector3f(1.f, 1.f, 1.f),
                (float) Math.cos(Math.toRadians(10.5f)),
                (float) Math.cos(Math.toRadians(15.0f)));

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

        particleSystem = new ParticleSystem();

        GLResourceLoader glResourceLoader = GLResourceLoader.getInstance();

        Optional<RawTexture> fireTexture = TextureLoader.loadAsTextureFromFile("assets/textures/fire.png");
        fireTexture.ifPresent(rawTexture -> fireParticleTexture = glResourceLoader.load2DTexture(rawTexture, GL_TEXTURE_2D, true));

        Optional<RawTexture> rainTexture = TextureLoader.loadAsTextureFromFile("assets/textures/raindrop.png");
        rainTexture.ifPresent(rawTexture -> rainParticleTexture = glResourceLoader.load2DTexture(rawTexture, GL_TEXTURE_2D, true));


        skybox = glResourceLoader.loadSkybox(skyboxMesh, skyboxTextures);

        waterTiles.add(new WaterTile(-0.5f, -.5f, -0.8f));

        renderingManager.addFrameBuffer(WaterFrameBuffer.createWaterFrameBuffer(glResourceLoader, WaterFrameBuffer.REFLECTION_WIDTH, WaterFrameBuffer.REFLECTION_HEIGHT, WaterFrameBuffer.REFRACTION_WIDTH, WaterFrameBuffer.REFRACTION_HEIGHT));
        renderingManager.setToRenderWater(true);
    }

    @Override
    public void update(float deltaTime) {


        if (MouseListener.isActiveInWindow()) {
            camera.rotate(deltaTime);
            MouseListener.endFrame();
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_F) && !KeyListener.isKeyHeld(GLFW_KEY_F)) {
            log.info("Toggle flashlight on {}", turnOnFlashlight);
            turnOnFlashlight = !turnOnFlashlight;
        }
        renderingManager.setSun(sun);
        renderingManager.setFogColour(fogColour);
        if (KeyListener.isKeyPressed(GLFW_KEY_X) && !KeyListener.isKeyHeld(GLFW_KEY_X)) {
            ParticleEmitterConfiguration configuration = ParticleEmitterConfiguration.builder()
                    .affectedByGravity(true)
                    .gravityEffect(.8f)
                    .position(new Vector3f(0.f, 1.f, 0.f))
                    .defaultSpeed(2.f)
                    .initialParticleRotation(0)
                    .initialParticleSize(1)
                    .timeToLive(4)
                    .particleTimeToLive(2)
                    .particlePerSeconds(50)
                    .build();
            particleSystem.createFireParticleEmitter(configuration, fireParticleTexture);
        }
        ParticleEmitterConfiguration rainParticleConfiguration = ParticleEmitterConfiguration.builder()
                .affectedByGravity(true)
                .gravityEffect(1.f)
                .position(new Vector3f(0.f, 100.f, 0.f))
                .defaultSpeed(9.f)
                .initialParticleRotation(0)
                .initialParticleSize(1)
                .timeToLive(20)
                .particleTimeToLive(10)
                .particlePerSeconds(5)
                .build();
        particleSystem.createRainParticleEmitter(rainParticleConfiguration, rainParticleTexture);

        Light[] lightsToRender = new Light[5];
        lights.toArray(lightsToRender);
        renderingManager.addToQueue(lightsToRender);
        if (turnOnFlashlight) {
            renderingManager.addToQueue(spotlight);
        }

        camera.move(deltaTime);
        spotlight.setPosition(camera.getPosition());
        spotlight.setDirection(camera.getDirection());
        particleSystem.update();

        for (Renderable gameObject : gameObjects) {
            renderingManager.addToQueue(gameObject);
        }
        for (Terrain terrain: terrains){
            renderingManager.addTerrainsToQueue(terrain);
        }
        particleSystem.getEmitters()
                .forEach(emitter -> renderingManager.addParticleEmitter(emitter));

        renderingManager.addSkybox(skybox);
        renderingManager.addWater(waterTiles);
        renderingManager.run(camera);

    }

    @Override
    public void cleanUp() {
        resourceManager.dispose();
        renderingManager.dispose();
        particleSystem.dispose();
    }

}
