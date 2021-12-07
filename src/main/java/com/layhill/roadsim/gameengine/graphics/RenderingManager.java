package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.graphics.gl.GLRenderer;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.particles.ParticleSystem;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RenderingManager {

    private final List<Light> lights = new ArrayList<>();
    private final Map<TexturedModel, List<Renderable>> entities = new HashMap<>();

    private Renderer renderer;
    private long window;
    private ParticleSystem particleSystem;

    public RenderingManager(long window, ParticleSystem particleSystem) {
        this.window = window;
        this.particleSystem = particleSystem;
        renderer = new GLRenderer(particleSystem);
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

    public void addToQueue(Light... light) {
        lights.addAll(List.of(light));
    }

    public void run(Camera camera) {
        renderer.prepare();
        renderer.render(window, camera, lights, entities);
        renderer.show(window);
        lights.clear();
        entities.clear();
    }

    public void dispose() {
        renderer.dispose(entities);
        lights.clear();
        entities.clear();
        particleSystem.dispose();
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }
}
