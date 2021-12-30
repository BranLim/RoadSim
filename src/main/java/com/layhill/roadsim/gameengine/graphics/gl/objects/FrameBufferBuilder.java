package com.layhill.roadsim.gameengine.graphics.gl.objects;

import static org.lwjgl.opengl.GL30.*;

public class FrameBufferBuilder {

    private int frameBufferId;
    private int width;
    private int height;
    private boolean isBuilding;

    public FrameBufferBuilder(){

    }

    public FrameBufferBuilder generateFrameBuffer(){
        if (isBuilding){
            throw new IllegalStateException("Still building a framebuffer.");
        }
        isBuilding = true;
        frameBufferId = glGenFramebuffers();
        return this;
    }

    public FrameBufferBuilder bindFrameBuffer(){
        checkIsBuildingState();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
        return this;
    }

    public FrameBufferBuilder sizeFrameBuffer(int width, int height){
        checkIsBuildingState();
        this.width = width;
        this.height = height;
        return this;
    }

    public FrameBuffer build(){

        isBuilding = false;
        return new FrameBuffer(frameBufferId, width, height );
    }

    private void checkIsBuildingState(){
        if (!isBuilding){
            throw new IllegalStateException("Framebuffer is still under construction");
        }
    }
}
