package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.gl.GlRenderer;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderingManager {

    private final Map<TexturedModel, List<GameObject>> entities = new HashMap<>();
    private List<ShaderProgram> shaderPrograms = new ArrayList<>();
    private Renderer renderer;
    private long window;

    public RenderingManager(long window) {
        this.window = window;
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

    public void run(Camera camera) {
        renderer.prepare();
        for(ShaderProgram shaderProgram: shaderPrograms){
            shaderProgram.start();
            shaderProgram.loadCamera(camera);
            shaderProgram.loadGlobalLight(new Vector3f(-40.f, 100.f, -30.f),  new Vector3f(1.f, 1.f, 1.f));
        }
        renderer.processEntities(window, entities);
        renderer.show(window);
        for(ShaderProgram shaderProgram: shaderPrograms){
            shaderProgram.stop();
        }
        entities.clear();
    }

    public void shutdown() {
       for(ShaderProgram shaderProgram: shaderPrograms){
           shaderProgram.stop();
           shaderProgram.dispose();
       }
    }
}
