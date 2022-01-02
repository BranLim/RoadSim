package com.layhill.roadsim.gameengine.graphics.gl.objects;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

public class FrameBufferBuilder {

    private int frameBufferId;
    private int width;
    private int height;
    private boolean isBuilding;

    public FrameBufferBuilder() {

    }

    public FrameBufferBuilder generateFrameBuffer() {
        if (isBuilding) {
            throw new IllegalStateException("Still building a framebuffer.");
        }
        isBuilding = true;
        frameBufferId = glGenFramebuffers();
        return this;
    }

    public FrameBufferBuilder bindFrameBuffer() {
        checkIsBuildingState();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
        return this;
    }

    public FrameBufferBuilder sizeFrameBuffer(int width, int height) {
        checkIsBuildingState();
        this.width = width;
        this.height = height;
        return this;
    }

    public FrameBufferBuilder withTexture(int attachmentType, int textureId, int mipmapLevel) {
        checkIsBuildingState();
        glFramebufferTexture(GL_FRAMEBUFFER, attachmentType, textureId, mipmapLevel);
        return this;
    }

    public FrameBufferBuilder with2DTexture(int attachmentType, int textureType, int textureId, int mipmapLevel) {
        checkIsBuildingState();
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType, textureType, textureId, mipmapLevel);
        return this;
    }

    public FrameBufferBuilder drawBuffers(int... buffers) {
        checkIsBuildingState();
        glDrawBuffers(buffers);
        return this;
    }

    public FrameBufferBuilder readColourBuffers(int buffer) {
        checkIsBuildingState();
        glReadBuffer(buffer);
        return this;
    }

    public FrameBuffer build() {
        checkIsBuildingState();

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new FrameBufferException("Incomplete framebuffer");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        isBuilding = false;
        return new FrameBuffer(frameBufferId, width, height);
    }

    private void checkIsBuildingState() {
        if (!isBuilding) {
            throw new IllegalStateException("Framebuffer is not under construction");
        }
    }
}
