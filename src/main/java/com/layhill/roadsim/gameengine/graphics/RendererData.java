package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.graphics.models.Sun;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import com.layhill.roadsim.gameengine.skybox.Skybox;
import com.layhill.roadsim.gameengine.terrain.Terrain;
import com.layhill.roadsim.gameengine.water.WaterFrameBuffer;
import com.layhill.roadsim.gameengine.water.WaterTile;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

public class RendererData {

    private Sun sun;
    private List<Light> lights;
    private Map<TexturedModel, List<Renderable>> entities;
    private Skybox skybox;
    private Terrain terrain;
    private List<ParticleEmitter> emitters;
    private Vector3f fogColour;
    private List<WaterTile> waterTiles;
    private WaterFrameBuffer waterFrameBuffer;

    public RendererData() {
    }

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

    public Vector3f getFogColour() {
        return fogColour;
    }

    public void setFogColour(Vector3f fogColour) {
        this.fogColour = fogColour;
    }

    public Sun getSun() {
        return sun;
    }

    public void setSun(Sun sun) {
        this.sun = sun;
    }

    public List<WaterTile> getWaterTiles() {
        return waterTiles;
    }

    public void setWaterTiles(List<WaterTile> waterTiles) {
        this.waterTiles = waterTiles;
    }

    public WaterFrameBuffer getWaterFrameBuffer() {
        return waterFrameBuffer;
    }

    public void setWaterFrameBuffer(WaterFrameBuffer waterFrameBuffer) {
        this.waterFrameBuffer = waterFrameBuffer;
    }
}
