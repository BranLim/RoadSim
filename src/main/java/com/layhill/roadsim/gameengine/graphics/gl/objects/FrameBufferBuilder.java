package com.layhill.roadsim.gameengine.graphics.gl.objects;

import static org.lwjgl.opengl.GL30.glGenFramebuffers;

public class FrameBufferBuilder {

    private int frameBuffer;
    private float width;
    private float height;
    private boolean isBuilding;

    public FrameBufferBuilder(){

    }

    public FrameBufferBuilder generateFrameBuffer(){
        if (isBuilding){
            throw new IllegalStateException("Still building a framebuffer.");
        }
        isBuilding = true;
        frameBuffer  = glGenFramebuffers();
        return this;
    }
}
