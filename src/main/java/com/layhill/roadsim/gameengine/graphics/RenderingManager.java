package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.gl.GlRenderer;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.graphics.models.Mesh;
import com.layhill.roadsim.gameengine.io.MeshLoader;
import com.layhill.roadsim.gameengine.io.TextureLoader;
import com.layhill.roadsim.gameengine.skybox.Skybox;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RenderingManager {

    private final List<Light> lights = new ArrayList<>();
    private final Map<TexturedModel, List<Renderable>> entities = new HashMap<>();

    private Skybox skybox;
    private Renderer renderer;
    private long window;

    public RenderingManager(long window) {
        this.window = window;
        renderer = new GlRenderer();
    }

    public void loadSkybox(String[] skyBoxTextures) {
        Mesh mesh = MeshLoader.loadObjAsMesh("assets/models/skybox.obj").get();
        RawTexture rSkybox = TextureLoader.loadAsTextureFromFile("assets/textures/Daylight_Box_Right.jpg").orElse(null);
        RawTexture lSkybox = TextureLoader.loadAsTextureFromFile("assets/textures/Daylight_Box_Left.jpg").orElse(null);
        RawTexture topSkybox = TextureLoader.loadAsTextureFromFile("assets/textures/Daylight_Box_Top.jpg").orElse(null);
        RawTexture bottomSkybox = TextureLoader.loadAsTextureFromFile("assets/textures/Daylight_Box_Bottom.jpg").orElse(null);
        RawTexture backSkybox = TextureLoader.loadAsTextureFromFile("assets/textures/Daylight_Box_Back.jpg").orElse(null);
        RawTexture frontSkybox = TextureLoader.loadAsTextureFromFile("assets/textures/Daylight_Box_Front.jpg").orElse(null);

        RawTexture[] skyboxTextures = new RawTexture[6];
        List.of(rSkybox, lSkybox, topSkybox, bottomSkybox,frontSkybox ,backSkybox).toArray(skyboxTextures);
        skybox = GLResourceLoader.getInstance().loadCubeMapAsSkybox(mesh, skyboxTextures);
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
        renderer.processEntities(window, camera, lights, entities);
        renderer.renderSkybox(skybox, camera);
        renderer.show(window);
        lights.clear();
        entities.clear();
    }

    public void dispose() {
        renderer.dispose(skybox, entities);
        lights.clear();
        entities.clear();
    }

}
