package com.layhill.roadsim.gameengine.water;

import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;

public class WaterFrameBuffer {

    public static final int REFLECTION_WIDTH = 640;
    public static final int REFLECTION_HEIGHT = 360;

    public static final int REFRACTION_WIDTH = 1280;
    public static final int REFRACTION_HEIGHT = 720;

    private int refractionFrameBuffer;
    private int refractionDepthTexture;
    private int refractionTexture;

    private int reflectionFrameBuffer;
    private int reflectionTexture;
    private int reflectionDepthBuffer;

    private int reflectionWidth;
    private int reflectionHeight;
    private int refractionWidth;
    private int refractionHeight;

    private WaterFrameBuffer(int reflectionFrameBuffer, int reflectionTexture, int reflectionDepthBuffer,
                             int refractionFrameBuffer,  int refractionTexture, int refractionDepthTexture,
                             int reflectionWidth, int reflectionHeight, int refractionWidth, int refractionHeight) {

        this.reflectionFrameBuffer = reflectionFrameBuffer;
        this.reflectionTexture = reflectionTexture;
        this.reflectionDepthBuffer = reflectionDepthBuffer;

        this.refractionFrameBuffer = refractionFrameBuffer;
        this.refractionTexture = refractionTexture;
        this.refractionDepthTexture = refractionDepthTexture;

        this.reflectionWidth = reflectionWidth;
        this.reflectionHeight = reflectionHeight;
        this.refractionWidth = refractionWidth;
        this.refractionHeight = refractionHeight;
    }

    public static WaterFrameBuffer createWaterFrameBuffer(GLResourceLoader loader, int reflectionWidth, int reflectionHeight, int refractionWidth, int refractionHeight) {
        int reflectionFrameBuffer = loader.createFrameBuffer();
        loader.drawToColourAttachment();
        int reflectionTextureId = loader.createTextureAttachment(reflectionWidth, reflectionHeight);
        int reflectionDepthBuffer = loader.createDepthBufferAttachment(reflectionWidth, reflectionHeight);
        loader.unbindFrameBuffer();

        int refractionFrameBuffer = loader.createFrameBuffer();
        loader.drawToColourAttachment();
        int refractionTextureId = loader.createTextureAttachment(refractionWidth, refractionHeight);
        int refractionDepthTexture = loader.createDepthTextureAttachment(refractionWidth, refractionHeight);
        loader.unbindFrameBuffer();
        return new WaterFrameBuffer(reflectionFrameBuffer, reflectionTextureId, reflectionDepthBuffer, refractionFrameBuffer, refractionTextureId, refractionDepthTexture, reflectionWidth, reflectionHeight, refractionWidth, refractionHeight);
    }


    public void bindReflectionFrameBuffer(GLResourceLoader loader) {
        loader.bindFrameBuffer(reflectionFrameBuffer, reflectionWidth, reflectionHeight);
    }

    public void bindRefractionFrameBuffer(GLResourceLoader loader) {
        loader.bindFrameBuffer(refractionFrameBuffer, refractionWidth, refractionHeight);
    }

    public void unbindFrameBuffer(GLResourceLoader loader, int width, int height) {
        loader.unbindFrameBuffer(width, height);
    }

    public void unbindFrameBuffer(GLResourceLoader loader) {
        loader.unbindFrameBuffer();
    }


    public int getReflectionTexture() {//get the resulting texture
        return reflectionTexture;
    }

    public int getRefractionTexture() {//get the resulting texture
        return refractionTexture;
    }

    public int getRefractionDepthTexture() {//get the resulting depth texture
        return refractionDepthTexture;
    }
}
