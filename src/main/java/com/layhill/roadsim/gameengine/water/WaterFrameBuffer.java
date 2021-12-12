package com.layhill.roadsim.gameengine.water;

import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;

public class WaterFrameBuffer {

    protected static final int REFLECTION_WIDTH = 320;
    private static final int REFLECTION_HEIGHT = 180;

    protected static final int REFRACTION_WIDTH = 1280;
    private static final int REFRACTION_HEIGHT = 720;

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
                             int refractionFrameBuffer, int refractionDepthTexture, int refractionTexture,
                             int reflectionWidth, int reflectionHeight, int refractionWidth, int refractionHeight) {
        this.refractionFrameBuffer = refractionFrameBuffer;
        this.refractionDepthTexture = refractionDepthTexture;
        this.refractionTexture = refractionTexture;
        this.reflectionFrameBuffer = reflectionFrameBuffer;
        this.reflectionTexture = reflectionTexture;
        this.reflectionDepthBuffer = reflectionDepthBuffer;
        this.reflectionWidth = reflectionWidth;
        this.reflectionHeight = reflectionHeight;
        this.refractionWidth = refractionWidth;
        this.refractionHeight = refractionHeight;
    }

    public static WaterFrameBuffer createWaterFrameBuffer(GLResourceLoader loader, int reflectionWidth, int reflectionHeight, int refractionWidth, int refractionHeight) {
        int reflectionFrameBuffer = loader.createFrameBuffer();
        int reflectionTextureId = loader.createTextureAttachemnt(reflectionWidth, reflectionHeight);
        int reflectionDepthBuffer = loader.createDepthBufferAttachment(reflectionWidth, reflectionHeight);
        loader.unbindFrameBuffer();

        int refractionFrameBuffer = loader.createFrameBuffer();
        int refractionTextureId = loader.createTextureAttachemnt(refractionWidth, refractionHeight);
        int refractionDepthTexture = loader.createDepthTextureAttachment(refractionWidth, refractionHeight);
        loader.unbindFrameBuffer();
        return new WaterFrameBuffer(reflectionFrameBuffer,reflectionTextureId,reflectionDepthBuffer, refractionFrameBuffer, refractionTextureId,refractionDepthTexture, reflectionWidth, reflectionHeight, refractionWidth, refractionHeight);
    }


    public void bindReflectionFrameBuffer(GLResourceLoader loader){
        loader.bindFrameBuffer(reflectionFrameBuffer, reflectionWidth, reflectionHeight);
    }

    public void bindRefractionFrameBuffer(GLResourceLoader loader){
        loader.bindFrameBuffer(refractionFrameBuffer, refractionWidth, refractionHeight);
    }

    public void unbindFrameBuffer(GLResourceLoader loader, int width, int height){
        loader.unbindFrameBuffer(width, height);
    }
    public int getReflectionTexture() {//get the resulting texture
        return reflectionTexture;
    }

    public int getRefractionTexture() {//get the resulting texture
        return refractionTexture;
    }

    public int getRefractionDepthTexture(){//get the resulting depth texture
        return refractionDepthTexture;
    }
}
