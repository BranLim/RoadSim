package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.graphics.models.Sun;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import com.layhill.roadsim.gameengine.skybox.Skybox;
import com.layhill.roadsim.gameengine.water.WaterTile;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

@Slf4j
public class RenderingManager {

    private long window;

    private static final float SKY_RED = 0.05f;
    private static final float SKY_GREEN = 0.05f;
    private static final float SKY_BLUE = 0.05f;

    private final List<Light> lights = new ArrayList<>();
    private final Map<TexturedModel, List<Renderable>> entities = new HashMap<>();
    private final List<ParticleEmitter> emitters = new ArrayList<>();
    private final List<Renderer> renderers = new ArrayList<>();
    private RendererData rendererData = new RendererData();

    private Skybox skybox;
    private Vector3f fogColour;
    private Sun sun;
    private List<WaterTile> waters = new ArrayList<>();

    public RenderingManager(long window) {
        this.window = window;
    }

    public void addRenderer(Renderer renderer) {
        if (renderer == null) {
            return;
        }
        renderers.add(renderer);
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
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(SKY_RED, SKY_GREEN, SKY_BLUE, 1.0f);
    }

    public void run(Camera camera) {
        startRendering();

        long startTime = System.currentTimeMillis();
        prepareRenderingData();
        for (Renderer renderer : renderers) {
            renderer.prepare();
            renderer.render(window, camera, rendererData);
        }
        show(window);

        lights.clear();
        entities.clear();
        emitters.clear();
        waters.clear();

        long endTime = System.currentTimeMillis();
        System.out.println("Total loop time: " + (endTime - startTime) + " ms");
    }

    private void prepareRenderingData() {
        rendererData.setLights(lights);
        rendererData.setEntities(entities);
        rendererData.setEmitters(emitters);
        rendererData.setSkybox(skybox);
        rendererData.setSun(sun);
        rendererData.setFogColour(fogColour);
        rendererData.setWaterTiles(waters);
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
}
