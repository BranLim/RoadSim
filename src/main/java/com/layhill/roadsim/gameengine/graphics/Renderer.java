package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;

import java.util.List;
import java.util.Map;

public interface Renderer {

    void prepare();

    void processEntities(long window,  Map<TexturedModel, List<GameObject>> gameObjects);

    void show(long window);

    void dispose(Map<TexturedModel, List<GameObject>> entities);
}
