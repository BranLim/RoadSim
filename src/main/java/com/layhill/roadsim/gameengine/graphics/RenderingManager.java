package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.gl.GLRenderer;

import java.util.ArrayList;
import java.util.List;

public class RenderingManager {

    private List<GameObject> gameObjects = new ArrayList<>();
    private List<ShaderProgram> shaderPrograms = new ArrayList<>();
    private Renderer renderer = new GLRenderer();
    private long window;

    public void run(Camera camera) {
        renderer.prepare();
        //TODO: Implement
        renderer.process(window, gameObjects);
        renderer.show(window);
    }
}
