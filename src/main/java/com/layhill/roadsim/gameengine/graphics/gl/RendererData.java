package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.Renderable;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import com.layhill.roadsim.gameengine.skybox.Skybox;
import com.layhill.roadsim.gameengine.terrain.Terrain;

import java.util.List;
import java.util.Map;

public class RendererData {

    private List<Light> lights;
    private Map<TexturedModel, List<Renderable>> entities;
    private Skybox skybox;
    private Terrain terrain;
    private List<ParticleEmitter> emitters;

    public List<Light> getLights() {
        return lights;
    }

    public void setLights(List<Light> lights) {
        this.lights = lights;
    }

    public Map<TexturedModel, List<Renderable>> getEntities() {
        return entities;
    }

    public void setEntities(Map<TexturedModel, List<Renderable>> entities) {
        this.entities = entities;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public void setSkybox(Skybox skybox) {
        this.skybox = skybox;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public List<ParticleEmitter> getEmitters() {
        return emitters;
    }

    public void setEmitters(List<ParticleEmitter> emitters) {
        this.emitters = emitters;
    }
}
