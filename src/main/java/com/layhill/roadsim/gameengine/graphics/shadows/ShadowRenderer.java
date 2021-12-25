package com.layhill.roadsim.gameengine.graphics.shadows;

import com.layhill.roadsim.gameengine.graphics.Renderer;
import com.layhill.roadsim.gameengine.graphics.RendererData;
import com.layhill.roadsim.gameengine.graphics.ViewSpecification;
import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;

public class ShadowRenderer implements Renderer {

    private ShadowShaderProgram shader;
    private GLResourceLoader loader;

    public ShadowRenderer(GLResourceLoader loader){
        this.loader = loader;
        shader = ShaderFactory.createShadowShaderProgram();
    }


    @Override
    public void prepare() {

    }

    @Override
    public void render(ViewSpecification viewSpecification, RendererData rendererData) {

    }

    @Override
    public void dispose(RendererData rendererData) {

    }
}
