package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.graphics.gl.GLParticleRenderer;
import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.gl.GLWaterRenderer;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.graphics.models.Sun;
import com.layhill.roadsim.gameengine.graphics.models.WaterRenderingStage;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import com.layhill.roadsim.gameengine.skybox.Skybox;
import com.layhill.roadsim.gameengine.terrain.Terrain;
import com.layhill.roadsim.gameengine.water.WaterFrameBuffer;
import com.layhill.roadsim.gameengine.water.WaterTile;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE0;

@Slf4j
public class RenderingManager {

    private long window;

    private static final float SKY_RED = 0.05f;
    private static final float SKY_GREEN = 0.05f;
    private static final float SKY_BLUE = 0.05f;

    private final List<Light> lights = new ArrayList<>();
    private final Map<TexturedModel, List<Renderable>> entities = new HashMap<>();
    private final Map<TexturedModel, List<Renderable>> terrains = new HashMap<>();
    private final List<ParticleEmitter> emitters = new ArrayList<>();
    private final List<Renderer> renderers = new ArrayList<>();
    private RendererData rendererData = new RendererData();

    private Skybox skybox;
    private Vector3f fogColour;
    private Sun sun;
    private List<WaterTile> waters = new ArrayList<>();
    private WaterFrameBuffer waterFrameBuffer;
    private boolean toRenderWater;
    private GLWaterRenderer waterRenderer = new GLWaterRenderer(GLResourceLoader.getInstance());

    public RenderingManager(long window) {
        this.window = window;
    }

    public void addRenderer(Renderer renderer) {
        if (renderer == null) {
            return;
        }
        if (!renderers.contains(renderer)) {
            renderers.add(renderer);
        }
    }

    public void addToQueue(Renderable renderableEntity) {
        List<Renderable> renderableEntities = entities.get(renderableEntity.getTexturedModel());
        if (renderableEntities == null) {
            renderableEntities = new ArrayList<>();
            renderableEntities.add(renderableEntity);
            entities.put(renderableEntity.getTexturedModel(), renderableEntities);
            return;
        }
        renderableEntities.add(renderableEntity);
    }

    public void addTerrainsToQueue(Terrain terrain) {
        List<Renderable> renderableTerrains = terrains.get(terrain.getTexturedModel());
        if (renderableTerrains == null) {
            renderableTerrains = new ArrayList<>();
            renderableTerrains.add(terrain);
            terrains.put(terrain.getTexturedModel(), renderableTerrains);
            return;
        }
        renderableTerrains.add(terrain);
    }

    public void addParticleEmitter(ParticleEmitter particleEmitter) {
        if (particleEmitter == null) {
            return;
        }
        emitters.add(particleEmitter);
    }

    public void addToQueue(Light... light) {
        lights.addAll(List.of(light));
    }

    private void startRendering() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(SKY_RED, SKY_GREEN, SKY_BLUE, 1.0f);
    }

    public void run(Camera camera) {

        long startTime = System.currentTimeMillis();
        prepareRenderingData();
        if (toRenderWater) {
            removeRenderer(waterRenderer);
            float waterHeight = 0.0f;
            List<WaterTile> waterTiles = rendererData.getWaterTiles();
            if (waterTiles != null && !waterTiles.isEmpty()) {
                waterHeight = waterTiles.get(0).getHeight();
            }
            glEnable(GL_CLIP_DISTANCE0);
            rendererData.setWaterRenderingStage(WaterRenderingStage.REFLECTION);
            waterFrameBuffer.bindReflectionFrameBuffer(GLResourceLoader.getInstance());
            rendererData.setClipPlane(new Vector4f(0, 1, 0, -waterHeight));
            float distance = 2 * (camera.getPosition().y - waterHeight);
            camera.getPosition().y -= distance;
            camera.invertPitch();
            invokeRenderers(camera);
            camera.getPosition().y += distance;
            camera.invertPitch();

            rendererData.setWaterRenderingStage(WaterRenderingStage.REFRACTION);
            waterFrameBuffer.bindRefractionFrameBuffer(GLResourceLoader.getInstance());
            rendererData.setClipPlane(new Vector4f(0, -1, 0, waterHeight));
            invokeRenderers(camera);
            glDisable(GL_CLIP_DISTANCE0);
            waterFrameBuffer.unbindFrameBuffer(GLResourceLoader.getInstance(), 1920, 1080);

            rendererData.setWaterRenderingStage(WaterRenderingStage.END);
            addRenderer(waterRenderer);
        }

        rendererData.setClipPlane(new Vector4f(0, 1, 0, 10000f));
        invokeRenderers(camera);
        show(window);

        lights.clear();
        entities.clear();
        terrains.clear();
        emitters.clear();
        waters.clear();

        long endTime = System.currentTimeMillis();
        System.out.println("Total loop time: " + (endTime - startTime) + " ms");
    }

    private void removeRenderer(Renderer renderer) {
        renderers.remove(renderer);
    }

    private void invokeRenderers(Camera camera) {
        startRendering();
        for (Renderer renderer : renderers) {
            if (renderer.getClass() == GLParticleRenderer.class && toRenderWater && rendererData.getWaterRenderingStage() != WaterRenderingStage.END) {
                continue;
            }
            renderer.prepare();
            renderer.render(window, camera, rendererData);
        }
    }

    private void prepareRenderingData() {
        rendererData.setLights(lights);
        rendererData.setEntities(entities);
        rendererData.setTerrains(terrains);
        rendererData.setEmitters(emitters);
        rendererData.setSkybox(skybox);
        rendererData.setSun(sun);
        rendererData.setFogColour(fogColour);
        rendererData.setWaterTiles(waters);
        rendererData.setToRenderWater(toRenderWater);
        if (waterFrameBuffer != null) {
            rendererData.setWaterFrameBuffer(waterFrameBuffer);
        }
    }

    public void show(long window) {
        glfwSwapBuffers(window);
    }


    public void dispose() {
        for (Renderer renderer : renderers) {
            renderer.dispose(rendererData);
        }

        lights.clear();
        entities.clear();
        emitters.clear();
    }

    public void addSkybox(Skybox skybox) {
        this.skybox = skybox;
    }

    public void setFogColour(Vector3f fogColour) {
        this.fogColour = fogColour;
    }

    public void setSun(Sun sun) {
        this.sun = sun;
    }

    public void addWater(List<WaterTile> waterTiles) {
        if (waterTiles == null) {
            return;
        }
        waters.addAll(waterTiles);
    }

    public void addFrameBuffer(WaterFrameBuffer frameBuffer) {
        this.waterFrameBuffer = frameBuffer;
    }

    public void setToRenderWater(boolean renderWater) {
        this.toRenderWater = renderWater;
    }
}
