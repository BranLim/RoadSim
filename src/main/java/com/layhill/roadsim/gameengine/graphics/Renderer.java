package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.entities.GameObject;

import java.util.List;

public interface Renderer {

    void prepare();

    void process(long window, List<GameObject> gameObjects);

    void show(long window);
}
