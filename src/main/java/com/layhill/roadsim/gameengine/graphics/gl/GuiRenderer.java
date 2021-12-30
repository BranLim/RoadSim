package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.Renderer;
import com.layhill.roadsim.gameengine.graphics.RendererData;
import com.layhill.roadsim.gameengine.graphics.ViewSpecification;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

public class GuiRenderer implements Renderer {

    public GuiRenderer() {

    }

    @Override
    public void prepare() {
        glDisable(GL_DEPTH_TEST);
    }

    @Override
    public void render(ViewSpecification viewSpecification, RendererData rendererData) {

    }

    @Override
    public void dispose(RendererData rendererData) {

    }
}
