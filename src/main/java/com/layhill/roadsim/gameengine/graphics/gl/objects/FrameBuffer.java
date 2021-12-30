package com.layhill.roadsim.gameengine.graphics.gl.objects;

public class FrameBuffer {
    private int frameBufferId;
    private int width;
    private int height;

    FrameBuffer(int frameBufferId, int width, int height) {
        this.frameBufferId = frameBufferId;
        this.width = width;
        this.height = height;
    }


    public int getFrameBufferId() {
        return frameBufferId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
