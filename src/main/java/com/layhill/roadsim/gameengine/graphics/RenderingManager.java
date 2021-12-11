package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.graphics.gl.RendererData;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

@Slf4j
public class RenderingManager {

    private final List<Light> lights = new ArrayList<>();
    private final Map<TexturedModel, List<Renderable>> entities = new HashMap<>();
    private final List<ParticleEmitter> emitters = new ArrayList<>();
    private final List<Renderer> renderers = new ArrayList<>();

    private long window;
    private RendererData rendererData = new RendererData();

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

    public void run(Camera camera) {

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

        long endTime = System.currentTimeMillis();
        System.out.println("Total loop time: "+ (endTime - startTime) +" ms");
    }

    private void prepareRenderingData() {
        rendererData.setLights(lights);
        rendererData.setEntities(entities);
        rendererData.setEmitters(emitters);
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
}
