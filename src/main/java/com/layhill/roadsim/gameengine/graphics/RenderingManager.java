package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.gl.GlRenderer;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RenderingManager {

    private final List<Light> lights = new ArrayList<>();
    private final Map<TexturedModel, List<GameObject>> entities = new HashMap<>();
    private List<ShaderProgram> shaderPrograms = new ArrayList<>();
    private Renderer renderer;
    private long window;

    public RenderingManager(long window) {
        this.window = window;
        shaderPrograms.add(loadShaders());
        renderer = new GlRenderer(shaderPrograms);
    }

    public void addToQueue(GameObject gameObject){
        List<GameObject> gameObjects = entities.get(gameObject.getTexturedModel());
        if (gameObjects==null){
            gameObjects = new ArrayList<>();
            gameObjects.add(gameObject);
            entities.put(gameObject.getTexturedModel(),gameObjects );
            return;
        }
        gameObjects.add(gameObject);
    }

    public void addToQueue(Light... light){
        lights.addAll(List.of(light));
    }

    public void run(Camera camera) {
        renderer.prepare();
        for(ShaderProgram shaderProgram: shaderPrograms){
            shaderProgram.start();
            shaderProgram.loadCamera(camera);
            shaderProgram.loadGlobalLight(new Vector3f(-40.f, 100.f, -30.f),  new Vector3f(1.f, 1.f, 1.f));
            for(Light light: lights){
                shaderProgram.loadPositionalLight(light.getPosition(), light.getColour());
            }

        }
        renderer.processEntities(window, entities);
        renderer.show(window);
        for(ShaderProgram shaderProgram: shaderPrograms){
            shaderProgram.stop();
        }
        lights.clear();
        entities.clear();
    }

    public void cleanUp() {
       for(ShaderProgram shaderProgram: shaderPrograms){
           shaderProgram.stop();
           shaderProgram.dispose();
       }
    }

    private ShaderProgram loadShaders() {
        ShaderProgram shaderProgram = new ShaderProgram();
        try {
            Shader vertexShader = ShaderFactory.loadShaderFromFile("assets/shaders/simplevertex.glsl").orElse(null);
            Shader fragmentShader = ShaderFactory.loadShaderFromFile("assets/shaders/simplefragment.glsl").orElse(null);

            shaderProgram.addShader(vertexShader);
            shaderProgram.addShader(fragmentShader);
            shaderProgram.init();
        } catch (IOException e) {
            log.error("Error loading shader from file", e);
        }
        return shaderProgram;
    }
}
