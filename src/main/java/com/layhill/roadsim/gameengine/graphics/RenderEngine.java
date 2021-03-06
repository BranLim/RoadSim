package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.Window;
import com.layhill.roadsim.gameengine.environments.Sun;
import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.gl.ParticleRenderer;
import com.layhill.roadsim.gameengine.graphics.gl.ShadowRenderer;
import com.layhill.roadsim.gameengine.graphics.gl.WaterRenderer;
import com.layhill.roadsim.gameengine.graphics.lights.Light;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.FrameBufferSize;
import com.layhill.roadsim.gameengine.graphics.shadows.ShadowBox;
import com.layhill.roadsim.gameengine.graphics.shadows.ShadowRenderingStage;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import com.layhill.roadsim.gameengine.skybox.Skybox;
import com.layhill.roadsim.gameengine.terrain.Terrain;
import com.layhill.roadsim.gameengine.utils.Maths;
import com.layhill.roadsim.gameengine.water.WaterFrameBuffer;
import com.layhill.roadsim.gameengine.water.WaterRenderingStage;
import com.layhill.roadsim.gameengine.water.WaterTile;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
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
public class RenderEngine {

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
    private boolean toRenderShadow;
    private WaterRenderer waterRenderer = new WaterRenderer(GLResourceLoader.getInstance());
    private FrameBufferSize windowFrameBufferSize;

    private ShadowBox shadowBox;
    private ShadowRenderingStage shadowRenderingStage = ShadowRenderingStage.END;

    public RenderEngine(long window) {
        this.window = window;
        windowFrameBufferSize = Window.getInstance().getWindowFrameBufferSize();
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
        prepareRenderingData(camera);

        if (toRenderShadow) {
            renderShadow(camera);
        }

        if (toRenderWater) {
            removeRenderer(waterRenderer);
            renderWater(camera);
            addRenderer(waterRenderer);
        }

        ViewSpecification viewSpecification = new ViewSpecification(camera.getProjectionMatrix(), camera.getViewMatrix());
        invokeRenderers(viewSpecification);
        updateDisplay(window);

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

    private void invokeRenderers(ViewSpecification viewSpecification) {
        startRendering();
        for (Renderer renderer : renderers) {
            if ((shadowRenderingStage == ShadowRenderingStage.BEGIN && toRenderShadow && renderer.getClass() != ShadowRenderer.class)
                    ||(shadowRenderingStage == ShadowRenderingStage.END && renderer.getClass() == ShadowRenderer.class) ) {
                continue;
            }
            if (renderer.getClass() == ParticleRenderer.class && toRenderWater && rendererData.getWaterRenderingStage() != WaterRenderingStage.END) {
                continue;
            }
            renderer.prepare();
            renderer.render(viewSpecification, rendererData);
        }
    }

    private void renderWater(Camera camera) {
        glEnable(GL_CLIP_DISTANCE0);
        float waterHeight = 0.0f;
        List<WaterTile> waterTiles = rendererData.getWaterTiles();
        if (waterTiles != null && !waterTiles.isEmpty()) {
            waterHeight = waterTiles.get(0).getHeight();
        }

        float moveDistance = 2 * (camera.getPosition().y - waterHeight);
        Vector3f newCamPosition = new Vector3f(camera.getPosition())
                .sub(0, moveDistance, 0);
        Matrix4f reflectionViewMatrix = Maths.createXZReflectionViewMatrix(newCamPosition, camera.getOrientation());
        ViewSpecification reflectionViewSpecification = new ViewSpecification(camera.getProjectionMatrix(), reflectionViewMatrix);
        rendererData.setWaterRenderingStage(WaterRenderingStage.REFLECTION);
        waterFrameBuffer.bindReflectionFrameBuffer(GLResourceLoader.getInstance());
        rendererData.setClipPlane(new Vector4f(0, 1, 0, -waterHeight + 0.5f));

        invokeRenderers(reflectionViewSpecification);

        ViewSpecification refractionViewSpecification = new ViewSpecification(camera.getProjectionMatrix(), camera.getViewMatrix());
        rendererData.setWaterRenderingStage(WaterRenderingStage.REFRACTION);
        waterFrameBuffer.bindRefractionFrameBuffer(GLResourceLoader.getInstance());
        rendererData.setClipPlane(new Vector4f(0, -1, 0, waterHeight + 0.2f));
        invokeRenderers(refractionViewSpecification);

        waterFrameBuffer.unbindFrameBuffer(GLResourceLoader.getInstance(), windowFrameBufferSize.width()[0], windowFrameBufferSize.height()[0]);

        rendererData.setWaterRenderingStage(WaterRenderingStage.END);
        glDisable(GL_CLIP_DISTANCE0);
    }

    private void renderShadow(Camera camera) {
        if (shadowBox == null) {
            shadowBox = new ShadowBox(rendererData.getSun().getDirection(), camera.getPosition(),
                    camera.getOrientation(), 45.f, 1920f / 1080f, camera.getNearPlane(),
                    camera.getFarPlane());
        }
        shadowRenderingStage = ShadowRenderingStage.BEGIN;
        shadowBox.update(camera.getPosition(), camera.getOrientation());
        sun.getLight().getShadowMap().bind(FrameBufferMode.WRITE);
        Matrix4f lightProjection = shadowBox.calculateProjectionMatrix();
        Matrix4f lightView = shadowBox.calculateViewMatrix();
        ViewSpecification shadowViewSpecification = new ViewSpecification(lightProjection, lightView);
        rendererData.setToShadowMapSpace(createShadowMapSpaceOffset().mul(lightProjection).mul(lightView));

        invokeRenderers(shadowViewSpecification);
        sun.getLight().getShadowMap().unbind();
        shadowRenderingStage = ShadowRenderingStage.END;
    }

    private Matrix4f createShadowMapSpaceOffset() {
        return new Matrix4f()
                .translate(0.5f, 0.5f, 0.5f)
                .scale(0.5f, 0.5f, 0.5f);
    }

    private void prepareRenderingData(Camera camera) {
        rendererData.setLights(lights);
        rendererData.setEntities(entities);
        rendererData.setTerrains(terrains);
        rendererData.setEmitters(emitters);
        rendererData.setSkybox(skybox);
        rendererData.setSun(sun);
        rendererData.setFogColour(fogColour);

        rendererData.setToRenderShadow(toRenderShadow);
        rendererData.setShadowMapResolution(ShadowRenderer.SHADOW_MAP_SIZE);
        rendererData.setShadowDistance(ShadowBox.MAX_DISTANCE_FOR_SHADOW_CASTING);

        rendererData.setWaterTiles(waters);
        rendererData.setToRenderWater(toRenderWater);
        if (waterFrameBuffer != null) {
            rendererData.setWaterFrameBuffer(waterFrameBuffer);
        }

        rendererData.setCameraPosition(camera.getPosition());
        rendererData.setNearPlane(camera.getNearPlane());
        rendererData.setFarPlane(camera.getFarPlane());

    }

    public void updateDisplay(long window) {
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

    public void addWaterFrameBuffer(WaterFrameBuffer frameBuffer) {
        this.waterFrameBuffer = frameBuffer;
    }

    public void setToRenderWater(boolean toRenderWater) {
        this.toRenderWater = toRenderWater;
    }

    public void setToRenderShadow(boolean toRenderShadow) {
        this.toRenderShadow = toRenderShadow;
    }

}
