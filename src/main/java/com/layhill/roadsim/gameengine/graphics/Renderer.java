package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.graphics.gl.RendererData;
import com.layhill.roadsim.gameengine.graphics.models.Camera;

public interface Renderer {

    void prepare();

    void render(long window, Camera camera, RendererData rendererData);

    void dispose(RendererData rendererData);
}
