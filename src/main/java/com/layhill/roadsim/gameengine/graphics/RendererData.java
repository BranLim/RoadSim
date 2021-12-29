package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.graphics.lights.Light;
import com.layhill.roadsim.gameengine.environments.Sun;
import com.layhill.roadsim.gameengine.water.WaterRenderingStage;
import com.layhill.roadsim.gameengine.graphics.shadows.ShadowFrameBuffer;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import com.layhill.roadsim.gameengine.skybox.Skybox;
import com.layhill.roadsim.gameengine.terrain.Terrain;
import com.layhill.roadsim.gameengine.water.WaterFrameBuffer;
import com.layhill.roadsim.gameengine.water.WaterTile;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;

public class RendererData {

    private Sun sun;
    private Vector3f cameraPosition;
    private List<Light> lights;
    private Map<TexturedModel, List<Renderable>> entities;
    private Map<TexturedModel, List<Renderable>> terrains;
    private Skybox skybox;
    private Terrain terrain;
    private List<ParticleEmitter> emitters;
    private Vector3f fogColour;
    private boolean toRenderWater;
    private List<WaterTile> waterTiles;
    private WaterFrameBuffer waterFrameBuffer;
    private Vector4f clipPlane;
    private WaterRenderingStage waterRenderingStage;
    private float farPlane;
    private float nearPlane;
    private ShadowFrameBuffer shadowFrameBuffer;
    private Matrix4f toShadowMapSpace;
    private boolean isToRenderShadow;
    private float shadowDistance;
    private float shadowMapResolution;


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

    public Map<TexturedModel, List<Renderable>>  getTerrains() {
        return terrains;
    }

    public void setTerrains(Map<TexturedModel, List<Renderable>>  terrains) {
        this.terrains = terrains;
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

    public boolean isToRenderWater() {
        return toRenderWater;
    }

    public void setToRenderWater(boolean toRenderWater) {
        this.toRenderWater = toRenderWater;
    }

    public Vector4f getClipPlane() {
        return clipPlane;
    }

    public void setClipPlane(Vector4f clipPlane){
        this.clipPlane = clipPlane;
    }

    public WaterRenderingStage getWaterRenderingStage() {
        return waterRenderingStage;
    }

    public void setWaterRenderingStage(WaterRenderingStage waterRenderingStage) {
        this.waterRenderingStage = waterRenderingStage;
    }

    public Vector3f getCameraPosition() {
        return cameraPosition;
    }

    public void setCameraPosition(Vector3f cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public void setFarPlane(float farPlane) {
        this.farPlane  = farPlane;
    }

    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }

    public float getFarPlane() {
        return farPlane;
    }

    public float getNearPlane() {
        return nearPlane;
    }

    public void setShadowFrameBuffer(ShadowFrameBuffer shadowFrameBuffer) {
        this.shadowFrameBuffer = shadowFrameBuffer;
    }

    public ShadowFrameBuffer getShadowFrameBuffer() {
        return shadowFrameBuffer;
    }

    public Matrix4f getToShadowMapSpace() {
        return toShadowMapSpace;
    }

    public void setToShadowMapSpace(Matrix4f toShadowMapSpace) {
        this.toShadowMapSpace = toShadowMapSpace;
    }

    public void setToRenderShadow(boolean toRenderShadow) {
        isToRenderShadow = toRenderShadow;
    }

    public boolean isToRenderShadow() {
        return isToRenderShadow;
    }

    public float getShadowDistance() {
        return shadowDistance;
    }

    public void setShadowDistance(float shadowDistance) {
        this.shadowDistance = shadowDistance;
    }

    public void setShadowMapResolution(float shadowMapResolution) {
        this.shadowMapResolution = shadowMapResolution;
    }

    public float getShadowMapResolution() {
        return shadowMapResolution;
    }
}
