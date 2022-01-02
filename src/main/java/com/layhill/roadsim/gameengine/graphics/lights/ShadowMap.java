package com.layhill.roadsim.gameengine.graphics.lights;

import com.layhill.roadsim.gameengine.graphics.FrameBufferMode;
import com.layhill.roadsim.gameengine.graphics.gl.objects.FrameBuffer;
import com.layhill.roadsim.gameengine.graphics.gl.objects.FrameBufferBuilder;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;

import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;

public abstract class ShadowMap {

    protected int resolution;
    private FrameBuffer frameBuffer;
    private GLTexture texture;

    protected ShadowMap(int resolution) {
        this.resolution = resolution;
        createFrameBuffer();
    }

    public void bind() {
        frameBuffer.bind();
    }

    public void bind(FrameBufferMode mode) {
        frameBuffer.bind(mode);
    }

    public void unbind() {
        frameBuffer.unbind();
    }

    public GLTexture getTexture() {
        return texture;
    }

    protected abstract void createShadowTexture();

    private void createFrameBuffer() {
        createShadowTexture();

        FrameBuffer frameBuffer = new FrameBufferBuilder()
                .generateFrameBuffer()
                .bindFrameBuffer()
                .sizeFrameBuffer(resolution, resolution)
                .drawBuffers(GL_NONE)
                .readColourBuffers(GL_NONE)
                .withTexture(GL_DEPTH_ATTACHMENT, texture.getTextureId(), 0)
                .build();
        setFrameBuffer(frameBuffer);
    }


    public void dispose() {
        frameBuffer.dispose();
        texture.dispose();
    }

    protected void setFrameBuffer(FrameBuffer frameBuffer) {
        this.frameBuffer = frameBuffer;
    }

    protected void setTexture(GLTexture texture) {
        this.texture = texture;
    }

    public void resize(int shadowResolution) {
        this.resolution = shadowResolution;
        dispose();
        createFrameBuffer();
    }
}
