package com.layhill.roadsim.gameengine.graphics;

public interface Renderer {

    void prepare();

    void render(ViewSpecification viewSpecification, RendererData rendererData);

    void dispose(RendererData rendererData);
}
