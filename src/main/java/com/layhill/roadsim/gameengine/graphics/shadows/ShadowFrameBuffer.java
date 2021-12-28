package com.layhill.roadsim.gameengine.graphics.shadows;

import com.layhill.roadsim.gameengine.Window;
import com.layhill.roadsim.gameengine.graphics.FrameBufferMode;
import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;

public class ShadowFrameBuffer {

    private final int depthBuffer;
    private final int frameBuffer;
    private final int width;
    private final int height;

    private ShadowFrameBuffer(int frameBuffer, int depthBuffer, int width, int height) {
        this.frameBuffer = frameBuffer;
        this.depthBuffer = depthBuffer;
        this.width = width;
        this.height = height;
    }

    public int getDepthBuffer() {
        return depthBuffer;
    }

    public static ShadowFrameBuffer createFrameBuffer(GLResourceLoader loader, int width, int height) {
        int shadowFrameBuffer = loader.createFrameBuffer();
        loader.drawToEmptyAttachment();
        loader.readFromNoBuffer();
        int shadowDepthTexture = loader.createShadowDepthTextureAttachment(width, height);
        var windowFrameBufferSize = Window.getInstance().getWindowFrameBufferSize();
        loader.unbindFrameBuffer(windowFrameBufferSize.width()[0], windowFrameBufferSize.height()[0]);

        return new ShadowFrameBuffer(shadowFrameBuffer, shadowDepthTexture, width, height);
    }

    public void bind(GLResourceLoader loader){
        loader.bindFrameBuffer(frameBuffer, width, height, FrameBufferMode.WRITE);
    }

    public void unbind(GLResourceLoader loader, int width, int height){
        loader.unbindFrameBuffer(width,height);
    }
}
