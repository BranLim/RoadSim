package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.skybox.Skybox;

import java.util.List;
import java.util.Map;

public interface Renderer {

    void prepare();

    void render(long window, Camera camera, List<Light> lights, Map<TexturedModel, List<Renderable>> gameObjects);

    void show(long window);

    void dispose(Map<TexturedModel, List<Renderable>> entities);
}
