package com.layhill.roadsim.gameengine.graphics.gl.objects;

import com.layhill.roadsim.gameengine.graphics.FrameBufferMode;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    private int frameBufferId;
    private int width;
    private int height;
    private int[] originalViewport = new int[4]; // [x,y, width, height] => Refer to official OpenGL documentation

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

    public void bind() {
        glGetIntegerv(GL_VIEWPORT, originalViewport);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
        glViewport(0, 0, width, height);
    }

    public void bind(FrameBufferMode mode) {
        glGetIntegerv(GL_VIEWPORT, originalViewport);
        switch (mode) {
            case READ_AND_WRITE -> glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
            case READ -> glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBufferId);
            case WRITE -> glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBufferId);
        }
        glViewport(0, 0, width, height);
    }


    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(originalViewport[0], originalViewport[1], originalViewport[2], originalViewport[3]);
    }

    public void dispose(){
        glDeleteFramebuffers(frameBufferId);
    }
}
